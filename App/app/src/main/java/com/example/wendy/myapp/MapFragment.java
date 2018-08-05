package com.example.wendy.myapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.wendy.myapp.adapter.PlaceAutocompleteAdapter;
import com.example.wendy.myapp.adapter.mSpinnerAdapter;
import com.example.wendy.myapp.module.BaseDropdownEntity;
import com.example.wendy.myapp.service.mCallback;
import com.example.wendy.myapp.util.DirectionsJSONParser;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.wendy.myapp.util.Utils.showToast;
import static com.example.wendy.myapp.util.config.getCUR_BOUNDS;
import static com.example.wendy.myapp.util.constData.getModes;
import static com.example.wendy.myapp.service.GG.reqRoute;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "mapFrag";
    private static final String ARG_PLACEID = "place";
    private static final LatLngBounds BOUNDS_USA = new LatLngBounds(
            new LatLng(18.7763, 170.5957), new LatLng(71.5388, -66.885417));
    private GoogleMap mMap;
    protected GeoDataClient mGeoDataClient;
    private PlaceAutocompleteAdapter mAdapter;

    private String placeId;
    private JSONObject mPlace;
    private ArrayList<BaseDropdownEntity> modes;
    private Double lat, lng;
    private LatLng ori;
    private LatLng des;
    private List<Polyline> polylines;


    private Spinner modeSpinner;
    private AutoCompleteTextView mAutocompleteView;

    public MapFragment() {
    }

    public static MapFragment newInstance(JSONObject place) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLACEID, place.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        polylines = new ArrayList<Polyline>();
        modes = getModes();
        if (getArguments() != null) {
            String placeStr = getArguments().getString(ARG_PLACEID);
            try {
                mPlace = new JSONObject(placeStr);
                lat = mPlace.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                lng = mPlace.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            } catch (Exception e) {
                //TODO
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_map, container, false);
        modeSpinner = rootView.findViewById(R.id.mode);


        mSpinnerAdapter adapter = new mSpinnerAdapter(getContext(),
                android.R.layout.simple_spinner_item,
                modes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(adapter);

        mAutocompleteView = (AutoCompleteTextView) rootView.findViewById(R.id.from);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        mAdapter = new PlaceAutocompleteAdapter(getActivity(), mGeoDataClient, getCUR_BOUNDS(), null);
        mAutocompleteView.setAdapter(mAdapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        modeSpinner.setOnItemSelectedListener(new modeSpinnerOnClickListener());
        return rootView;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        des = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(des));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(des, 15));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
            final AutocompletePrediction item = mAdapter.getItem(position);
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
                Log.i(TAG, "Place details received: " + place.getName());
                ori = place.getLatLng();
                mMap.addMarker(new MarkerOptions().position(ori));

//                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
//                boundsBuilder.include(ori);
//                boundsBuilder.include(des);
//                int routePadding = 100;
//                LatLngBounds latLngBounds = boundsBuilder.build();

//                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
                //TODO: show route
                for(Polyline line : polylines) {
                    line.remove();
                }
                polylines.clear();
                String mode = modeSpinner.getSelectedItem().toString();
                loadRoute(getActivity(), ori, des, mode);
                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete.", e);
                return;
            }
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    private void loadRoute(Context context, LatLng ori, LatLng des, String mode) {

        Map<String, String> param = new HashMap<String, String>();

//        String sensor = "sensor=false";
        param.put("origin", ori.latitude + "," + ori.longitude);
        param.put("destination", des.latitude + "," + des.longitude);
        param.put("mode", mode);
        reqRoute(context, param, new mCallback() {
            @Override
            public void apply(JSONObject res) {
                Log.i(TAG, res.toString());
                try {
                    //TODO: status not OK
                    if (!res.getString("status").equals("OK")) {
                        showToast(getActivity(), R.string.error_req);
                        }
                    JSONArray routes = res.getJSONArray("routes");
                    if (routes.length() == 0) {
                        showToast(getActivity(), R.string.info_no_routes);
                    }

                    JSONObject bounds = routes.getJSONObject(0).getJSONObject("bounds");
                    JSONObject ne = bounds.getJSONObject("northeast");
                    JSONObject sw = bounds.getJSONObject("southwest");
                    LatLng NE = new LatLng(ne.getDouble("lat"), ne.getDouble("lng"));
                    LatLng SW = new LatLng(sw.getDouble("lat"), sw.getDouble("lng"));
                    LatLngBounds newBounds = new LatLngBounds(SW, NE);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(newBounds, 100));

                    ParserTask parserTask = new ParserTask();
                    parserTask.execute(routes);
                } catch(JSONException e){
                    //TODO
                }
            }
        });
    }

    private class ParserTask extends AsyncTask<JSONArray, Integer, List<List<LatLng>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<LatLng>> doInBackground(JSONArray... dataList) {

            JSONObject jObject;
            List<List<LatLng>> routes = null;

            try {
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(dataList[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<LatLng>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<LatLng> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    LatLng point = path.get(j);

                    points.add(point);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);
                polylines.add(mMap.addPolyline(lineOptions));
            }
        }
    }

    public class modeSpinnerOnClickListener implements AdapterView.OnItemSelectedListener {

        public modeSpinnerOnClickListener() {
            // keep references for your onClick logic
        }

        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            if (ori == null) {
                return;
            }
            for(Polyline line : polylines) {
                line.remove();
            }
            polylines.clear();
            String mode = modeSpinner.getSelectedItem().toString();
            loadRoute(getActivity(), ori, des, mode);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
            return;
        }

    }


}
