package com.example.wendy.myapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.wendy.myapp.adapter.ReviewAdapter;
import com.example.wendy.myapp.adapter.mSpinnerAdapter;
import com.example.wendy.myapp.module.BaseDropdownEntity;
import com.example.wendy.myapp.module.Review;
import com.example.wendy.myapp.listener.RecyclerViewClickListener;
import com.example.wendy.myapp.service.mCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.wendy.myapp.util.Utils.timeFormat;
import static com.example.wendy.myapp.util.constData.getOrderTypes;
import static com.example.wendy.myapp.util.constData.getSourceTypes;
import static com.example.wendy.myapp.service.Yelp.reqYelpMatch;
import static com.example.wendy.myapp.service.Yelp.reqYelpReview;


public class ReviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_REVIEW = "review";

    private Spinner sourceSpinner;
    private Spinner orderSpinner;
    private RecyclerView mRecyclerView;
    private ReviewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView noRecordView;

    private JSONObject mPlace;
    private ArrayList<BaseDropdownEntity> sourceTypes;
    private ArrayList<BaseDropdownEntity> orderTypes;
    private List<Review> ggReview;
    private List<Review> yelpReview;
    private List<Review> mReview;
    Map<String, String> yelpParam;


    public ReviewFragment() {
        // Required empty public constructor
    }

    public static ReviewFragment newInstance(JSONObject place) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REVIEW, place.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReview = new ArrayList<>();
        if (getArguments() != null) {
            String reviewStr = getArguments().getString(ARG_REVIEW);
            try {
                //todo no reviews
                mPlace = new JSONObject(reviewStr);
                JSONArray reviews = mPlace.getJSONArray("reviews");
                ggReview = loadGGReview(reviews);

                mReview.addAll(ggReview);

                String[] formattedAddr = mPlace.getString("formatted_address").split(",");
                String[] statePostcode = formattedAddr[formattedAddr.length-2].trim().split(" ");
                yelpParam = new HashMap<>();
                yelpParam.put("name", mPlace.getString("name"));
                yelpParam.put("city", formattedAddr[formattedAddr.length-3]);
                yelpParam.put("state", statePostcode[0]);
                yelpParam.put("postal_code", statePostcode[1]);
                yelpParam.put("country", "US");
                if (formattedAddr.length > 3) {
                    yelpParam.put("address1", formattedAddr[0]);
                }

            } catch (Exception e) {
                //TODO
            }
        }
        orderTypes = getOrderTypes();
        sourceTypes = getSourceTypes();
        getYelpReview();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_review, container, false);
        sourceSpinner = rootView.findViewById(R.id.source);
        orderSpinner = rootView.findViewById(R.id.order);
        noRecordView = rootView.findViewById(R.id.norecord);

        mSpinnerAdapter adapter = new mSpinnerAdapter(getContext(),
                android.R.layout.simple_spinner_item,
                sourceTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceSpinner.setAdapter(adapter);
        sourceSpinner.setSelection(0);

        adapter = new mSpinnerAdapter(getActivity(),
                android.R.layout.simple_spinner_item,
                orderTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(adapter);
        orderSpinner.setSelection(0);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.review_list);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                String url = mReview.get(position).getAuthorUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        };

        if (mReview == null || mReview.size() == 0) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            noRecordView.setVisibility(View.VISIBLE);
        } else {
            noRecordView.setVisibility(View.INVISIBLE);
        }

        mAdapter = new ReviewAdapter(mReview, listener);
        mRecyclerView.setAdapter(mAdapter);



        sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    mReview.clear();
                    if (ggReview == null || ggReview.size() == 0) {
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        noRecordView.setVisibility(View.VISIBLE);
                    } else {
                        //Todo deepcopy
                        noRecordView.setVisibility(View.INVISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        try {
                            for (int i = 0; i < ggReview.size(); i++) {
                                mReview.add((Review) ggReview.get(i).clone());
                            }
                        } catch (Exception e) {
                        }
                        mAdapter.swap(mReview);
                    }

                } else {
                    mReview.clear();
                    if (yelpReview == null || yelpReview.size() == 0) {
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        noRecordView.setVisibility(View.VISIBLE);
                    } else {
                        noRecordView.setVisibility(View.INVISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mReview.addAll(yelpReview);
                        mAdapter.swap(yelpReview);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (mReview == null || mReview.size() == 0) {
                    return;
                }
                String source = sourceSpinner.getSelectedItem().toString();
                switch (position) {
                    case 0:
                        mReview.clear();
                        if (source.charAt(0) == 'g') {
                            mReview.addAll(ggReview);
                        } else {
                            mReview.addAll(yelpReview);
                        }
                        break;
                    case 1:
                        Collections.sort(mReview, new Comparator<Review>() {
                            @Override
                            public int compare(Review a, Review b) {
                                return a.getRating() < b.getRating() ? 1 : -1;
                            }
                        });

                        break;
                    case 2:
                        Collections.sort(mReview, new Comparator<Review>() {
                            @Override
                            public int compare(Review a, Review b) {
                                return a.getRating().compareTo(b.getRating());
                            }
                        });
                        break;
                    case 3:
                        Collections.sort(mReview, new Comparator<Review>() {
                            @Override
                            public int compare(Review a, Review b) {
                                return a.getTime().compareTo(b.getTime()) < 0 ? 1 : -1;
                            }
                        });
                        break;
                    case 4:
                        Collections.sort(mReview, new Comparator<Review>() {
                            @Override
                            public int compare(Review a, Review b) {
                                return a.getTime().compareTo(b.getTime());
                            }
                        });
                        break;
                }
                mAdapter.swap(mReview);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    private List<Review> loadGGReview(JSONArray reviews) {
        List<Review> res = new ArrayList<>();
        try {
            for (int i = 0; i < reviews.length(); ++i) {
                JSONObject cur = reviews.getJSONObject(i);
                Long time = cur.getLong("time");
                String formatted_time = timeFormat(time);
                Review tmp = new Review(
                        cur.getString("author_name"),
                        cur.getString("author_url"),
                        cur.getString("profile_photo_url"),
                        cur.getDouble("rating"),
                        formatted_time,
                        cur.getString("text"));
                res.add(tmp);
            }
        } catch (Exception e) {
            //TODO
        }
        return res;
    }

    private List<Review> loadYelpReview(JSONArray reviews) {
        List<Review> res = new ArrayList<>();
        try {
            for (int i = 0; i < reviews.length(); ++i) {
                JSONObject cur = reviews.getJSONObject(i);
                Review tmp = new Review(
                        cur.getJSONObject("user").getString("name"),
                        cur.getString("url"),
                        cur.getJSONObject("user").getString("image_url"),
                        cur.getDouble("rating"),
                        cur.getString("time_created"),
                        cur.getString("text"));
                res.add(tmp);
            }
        } catch (Exception e) {
            //TODO
        }
        return res;
    }


    private void getYelpReview() {

        reqYelpMatch(getActivity(), yelpParam, new mCallback() {
            @Override
            public void apply(JSONObject res) {
                try {
                    JSONArray matches = res.getJSONArray("businesses");
                    if (matches != null && matches.length() > 0) {
                        String id =  matches.getJSONObject(0).getString("id");
                        reqYelpReview(getActivity(), id, new mCallback() {
                            @Override
                            public void apply(JSONObject res) {
                                try {
                                    JSONArray reviews = res.getJSONArray("reviews");
                                    yelpReview = loadYelpReview(reviews);
                                } catch (Exception e) {
                                    //TODO
                                }
                            }
                        });
                    } else {
                        //TODO no records
                    }
                } catch (Exception e) {
                    //TODO
                }

            }
        });

    }

}
