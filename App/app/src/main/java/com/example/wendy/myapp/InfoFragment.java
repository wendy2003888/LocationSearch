package com.example.wendy.myapp;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.constraint.Barrier;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.wendy.myapp.service.mCallback;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import static com.example.wendy.myapp.util.constData.info_keys;
import static com.example.wendy.myapp.service.GG.reqDetail;


public class InfoFragment extends Fragment {
    private static final String TAG = "infoFrag";
    private static final String ARG_PLACE = "place";

    private JSONObject mPlace;
    protected GeoDataClient mGeoDataClient;
    private ConstraintLayout constraintLayout;
    private Barrier barrier;

    public InfoFragment() {
    }

    public static InfoFragment newInstance(JSONObject place) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLACE, place.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        if (getArguments() != null) {
            String placeStr = getArguments().getString(ARG_PLACE);
            try {
                mPlace = new JSONObject(placeStr);
            }catch (Exception e) {
                //TODO
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_info, container, false);
        //Todo set toolbar lable -> placeName

        constraintLayout = (ConstraintLayout)rootView.findViewById(R.id.info);
        barrier = rootView.findViewById(R.id.barrier);

        setView(mPlace);

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    void setView(JSONObject place) {
        try {
            ArrayList<Integer> labelIds = new ArrayList<>();

            TextView addrLabel = new TextView(getActivity());
            addrLabel.setText(R.string.label_addr);
            int addrLabelId = 1;
            addrLabel.setId(addrLabelId);
            addrLabel.setTypeface(addrLabel.getTypeface(), Typeface.BOLD);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
            params.startToStart = constraintLayout.getId();
            params.topToTop =  constraintLayout.getId();
            params.setMargins(16, 16 , 0 , 0);
            addrLabel.setLayoutParams(params);
            constraintLayout.addView(addrLabel);

            labelIds.add(addrLabelId);

            TextView addrView = new TextView(getActivity());
            String addr = place.getString("formatted_address");
            Log.i(TAG, addr);
            addrView.setText(addr);
            int addrId = 2;
            addrView.setId(addrId);
            ConstraintLayout.LayoutParams params_view = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
            params_view.startToEnd = barrier.getId();
            params_view.topToTop =  constraintLayout.getId();
            params_view.setMargins(16, 16 , 0 , 0);
            addrView.setLayoutParams(params_view);
            constraintLayout.addView(addrView);

            String[] labelNames = new String[]{
                    getString(R.string.label_addr),
                    getString(R.string.label_phone),
                    getString(R.string.label_price),
                    getString(R.string.label_rating),
                    getString(R.string.label_page),
                    getString(R.string.label_website)
            };
            // getString(R.string.label_hour)
            //Todo links and drawables
            for (int i = 1; i < info_keys.length; ++i) {
                String k = info_keys[i];
                int curLabelId = i * 2 + 1;
                int curId = i * 2 + 2;
                if (place.has(k) && !place.isNull(k)) {
                    int preLabelId = labelIds.get(labelIds.size() - 1);
                    int preId = preLabelId + 1;

                    TextView curLabel = new TextView(getActivity());
                    curLabel.setText(labelNames[i]);
                    curLabel.setId(curLabelId);
                    curLabel.setTypeface(curLabel.getTypeface(), Typeface.BOLD);
                    params = new ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.WRAP_CONTENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT);
                    params.startToStart = constraintLayout.getId();
                    params.topToBottom =  preLabelId;
                    params.setMargins(16, 16 , 0 , 0);
                    curLabel.setLayoutParams(params);
                    constraintLayout.addView(curLabel);

                    params_view = new ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.WRAP_CONTENT,
                            ConstraintLayout.LayoutParams.WRAP_CONTENT);
//                    params_view.endToEnd = constraintLayout.getId();
                    params_view.startToEnd = barrier.getId();
                    params_view.topToBottom =  preId;
                    params_view.setMargins(16, 16, 0, 0);

                    if (i == 3) {
//                        RatingBar ratingBar = new RatingBar(context, null, android.R.attr.ratingBarStyleSmall);

                        RatingBar curView = new RatingBar(getActivity(), null, android.R.attr.ratingBarStyleSmall);
                        BigDecimal v = new BigDecimal(place.getString(k));
                        curView.setNumStars(5);
                        curView.setRating(v.floatValue());
                        curView.setId(curId);

                        curView.setLayoutParams(params_view);
                        LayerDrawable stars = (LayerDrawable) curView.getProgressDrawable();
                        stars.getDrawable(2)
                                .setColorFilter(getResources().getColor(R.color.colorAccent),
                                        PorterDuff.Mode.SRC_ATOP); // for filled stars
                        stars.getDrawable(1)
                                .setColorFilter(getResources().getColor(R.color.lightGray),
                                        PorterDuff.Mode.SRC_ATOP); // for half filled stars
                        stars.getDrawable(0)
                                .setColorFilter(getResources().getColor(R.color.lightGray),
                                        PorterDuff.Mode.SRC_ATOP); // for empty stars
                        constraintLayout.addView(curView);
                    } else {
                        TextView curView = new TextView(getActivity());
                        String v = place.getString(k);
                        if (i == 1) {
                            curView.setAutoLinkMask(Linkify.PHONE_NUMBERS);
                            curView.setLinksClickable(true);
                            curView.setText(v);
                        } else if (i == 2) {
                            int price = Integer.parseInt(v);
                            StringBuffer outputBuffer = new StringBuffer(price);
                            for (int j = 0; j < price; j++) {
                                outputBuffer.append("$");
                            }
                            v = outputBuffer.toString();
                            curView.setText(v);
                        } else if (i == 4 || i  == 5) {
//                            curView.setAutoLinkMask(Linkify.WEB_URLS);
                            String formatted = String.format("<a href=\"%s\">%s</a>", v, v);
                            curView.setText(Html.fromHtml(formatted));
                            curView.setMovementMethod(LinkMovementMethod.getInstance());
                        } else {
                            curView.setText(v);
                        }
                        curView.setId(curId);
                        curView.setLayoutParams(params_view);
                        constraintLayout.addView(curView);
                    }
                    labelIds.add(curLabelId);
                }
            }

            int[] referIds = convertIntegers(labelIds);
            barrier.setReferencedIds(referIds);
        } catch (Exception e) {
            //Todo
        }
    }

    public static int[] convertIntegers(ArrayList<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }

}
