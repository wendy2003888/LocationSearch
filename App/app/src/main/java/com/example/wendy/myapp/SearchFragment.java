package com.example.wendy.myapp;

import android.Manifest;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wendy.myapp.adapter.PlaceAutocompleteAdapter;
import com.example.wendy.myapp.adapter.mSpinnerAdapter;
import com.example.wendy.myapp.module.BaseDropdownEntity;
import com.example.wendy.myapp.module.Category;
import com.example.wendy.myapp.service.mCallback;
import com.example.wendy.myapp.util.ProgressDialogFragment;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;
import static com.example.wendy.myapp.util.Utils.showProgress;
import static com.example.wendy.myapp.util.Utils.showToast;
import static com.example.wendy.myapp.util.config.setCurBounds;
import static com.example.wendy.myapp.util.constData.cate_values;
import static com.example.wendy.myapp.service.GG.searchNearby;

public class SearchFragment extends Fragment {

    private static final String TAG = "searchFrag";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final LatLngBounds BOUNDS_USA = new LatLngBounds(
            new LatLng(18.7763, 170.5957), new LatLng(71.5388, -66.885417));

    // UI references.
    private EditText keywordView;
    private TextView errKeywordView;
    private EditText distanceView;
    private AutoCompleteTextView locatoinView;
    private TextView errLocationView;
    private Spinner categorySpinner;
    private RadioGroup mRadioGroup;
    private Button searchButton;
    private Button clearButton;

    private View rootView;

    //data
    private GeoDataClient mGeoDataClient;
    private ArrayList<BaseDropdownEntity> categories;
    private String cur_loc;
    private LatLngBounds cur_bound;
    private Map<String, String> param;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    protected LocationManager locationManager;
    private FragmentManager fragmentManager;
    private ProgressDialogFragment progressDialog;

    public SearchFragment () {}

    public static SearchFragment newInstance(int sectionNumber) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        fragmentManager = getActivity().getSupportFragmentManager();
        progressDialog = new ProgressDialogFragment().newInstance(R.string.action_search);
        categories = new ArrayList<BaseDropdownEntity>();
        for (int i = 0; i < cate_values.length; i++) {
            String tmp = cate_values[i].replace('_', ' ');
            String name = tmp.substring(0, 1).toUpperCase() + tmp.substring(1);
            Category cate = new Category(name, cate_values[i]);
            categories.add(cate);
        }
        param = new HashMap<>();
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        keywordView = (EditText) rootView.findViewById(R.id.keyword);
        errKeywordView = (TextView) rootView.findViewById(R.id.err_keyword);
        distanceView = (EditText) rootView.findViewById(R.id.distance);

        locatoinView = (AutoCompleteTextView) rootView.findViewById(R.id.location);
        errLocationView = (TextView) rootView.findViewById(R.id.err_location);

        categorySpinner = (Spinner) rootView.findViewById(R.id.category);
        mRadioGroup = (RadioGroup) rootView.findViewById(R.id.from);

        searchButton = (Button) rootView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSearch();
            }
        });
        searchButton.setEnabled(false);

        clearButton = (Button) rootView.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });



        final mSpinnerAdapter adapter = new mSpinnerAdapter(getActivity(),
                android.R.layout.simple_spinner_item,
                categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.current_location:
                        locatoinView.getText().clear();
                        locatoinView.clearFocus();
                        locatoinView.setEnabled(false);
                        break;
                    case R.id.other:
                        locatoinView.setEnabled(true);
                        break;
                    default:
                        break;
                }
            }
        });

        keywordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptSearch();
                    return true;
                }
                return false;
            }
        });

        clear();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
            initLoc();
        }

        locatoinView.setOnItemClickListener(mAutocompleteClickListener);


        // Set up the adapter that will retrieve suggestions from the Places Geo Data Client.
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(), mGeoDataClient, cur_bound, null);
        locatoinView.setAdapter(placeAutocompleteAdapter);

        return rootView;
    }

    private void clear() {
        param.clear();
        keywordView.getText().clear();
        keywordView.clearFocus();
        errKeywordView.setVisibility(View.INVISIBLE);
        categorySpinner.setSelection(0);
        distanceView.getText().clear();
        distanceView.clearFocus();
        mRadioGroup.check(R.id.current_location);
        locatoinView.getText().clear();
        locatoinView.clearFocus();
        locatoinView.setEnabled(false);
        errLocationView.setVisibility(View.INVISIBLE);
        keywordView.clearFocus();
    }

    public void initLoc() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Location location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    cur_loc = location.getLatitude() + "," + location.getLongitude();
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                    boundsBuilder.include(new LatLng(location.getLatitude(), location.getLongitude()));
                    cur_bound = boundsBuilder.build();
                    setCurBounds(cur_bound);
                    searchButton.setEnabled(true);
                }
            }
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    cur_loc = location.getLatitude() + "," + location.getLongitude();
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                    boundsBuilder.include(new LatLng(location.getLatitude(), location.getLongitude()));
                    cur_bound = boundsBuilder.build();
                    setCurBounds(cur_bound);
                    searchButton.setEnabled(true);
                }
        }

    }

    private void attemptSearch() {

        // Reset errors.
        errKeywordView.setVisibility(View.INVISIBLE);
        errLocationView.setVisibility(View.INVISIBLE);

        // Store values
        String keyword = keywordView.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String distance;
        if (distanceView.getText().toString().equals("")) {
            distance = "10";
        } else {
            distance = distanceView.getText().toString();
        }
        int radioButtonID = mRadioGroup.getCheckedRadioButtonId();
        int idx = mRadioGroup.indexOfChild(mRadioGroup.findViewById(radioButtonID));
        RadioButton fromRadioButton = (RadioButton)  mRadioGroup.getChildAt(idx);
        String from = fromRadioButton.getText().toString();
        String location = locatoinView.getText().toString().trim();;

        boolean cancel = false;

        if (TextUtils.isEmpty(keyword)) {
            errKeywordView.setVisibility(View.VISIBLE);
            keywordView.requestFocus();
            cancel = true;
        }

        if (from.charAt(0)=='O' && TextUtils.isEmpty(location)) {
            errLocationView.setVisibility(View.VISIBLE);
            locatoinView.requestFocus();
            cancel = true;
        }

        if (cancel) {
            showToast(getActivity(), R.string.error_fill_field);
        } else {
            try {
                if (from.charAt(0) == 'C') {
                    from = "here";
                    location = cur_loc;
                } else {
                    from = "other";
                    location = URLEncoder.encode(location, "UTF-8");
                }

                param.put("keyword", URLEncoder.encode(keyword, "UTF-8"));
                param.put("category", URLEncoder.encode(category, "UTF-8"));
                param.put("distance", distance);
                if (!param.containsKey("location")) {
                    param.put("from", from);
                    param.put("location", location);
                } else {
                    param.put("from", "here");
                }
            }catch (Exception e) {

            }

            final Intent intent = new Intent(getActivity(), mListActivity.class);
            showProgress(true, fragmentManager, progressDialog);
            search(getActivity(), param, new mCallback() {
                @Override
                public void apply(JSONObject res) {
                    try {
                        if (res.getString("status").equals("ERROR")) {
                            showProgress(false, fragmentManager, progressDialog);
                            showToast(getActivity(), R.string.error_network);

                            return;
                        } else if (res.getString("status").equals("INVALID_REQUEST")) {
                            showProgress(false, fragmentManager, progressDialog);
                            showToast(getActivity(), R.string.error_req);

                            return;
                        }
                        intent.putExtra("data", res.toString());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
                        stackBuilder.addNextIntentWithParentStack(intent);

                        PendingIntent pendingIntent = stackBuilder
                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
                        builder.setContentIntent(pendingIntent);
                        startActivity(intent);
                        showProgress(false, fragmentManager, progressDialog);

                    } catch (JSONException e) {
                        //TODO
                    }
                }
            });
        }
    }

    public void search(Context context, Map<String, String> param, mCallback callback) {
        searchNearby(context, param, callback);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data Client to retrieve a Place object with
             additional details about the place.
              */
            Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeId);
            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);

            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data Client query that shows the first place result in
     * the details view on screen.
     */
    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback
            = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(Task<PlaceBufferResponse> task) {
            try {
                PlaceBufferResponse places = task.getResult();

                // Get the Place object from the buffer.
                final Place place = places.get(0);
                LatLng loc = place.getLatLng();
                param.put("location", loc.latitude + "," + loc.longitude);
                Log.i(TAG, "Place details received: " + place.getLatLng().toString());
                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete.", e);
                return;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLoc();
            } else {
//                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
            }
                // Permission was denied. Display an error message.
        }
    }

}
