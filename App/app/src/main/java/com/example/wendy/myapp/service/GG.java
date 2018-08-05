package com.example.wendy.myapp.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static com.example.wendy.myapp.util.config.HOST;
import static com.example.wendy.myapp.util.config.getDetail;
import static com.example.wendy.myapp.util.config.getDirection;
import static com.example.wendy.myapp.util.config.getPage;
import static com.example.wendy.myapp.util.config.getResult;

public class GG {
    private static final String TAG = "GGService";

    public static void searchNearby(Context context, Map<String, String> param, final mCallback callBack) {
        String req_data = "";

        try {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                if (req_data != null && !req_data.isEmpty()) {
                    req_data += '&';
                }
                req_data += entry.getKey() + '=' + entry.getValue();
            }
        } catch (Exception e) {

        }
        String url = HOST + getResult + req_data;
        Log.i(TAG, url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                try {
                    callBack.apply(response);
                } catch (Exception e) {
                    e .printStackTrace();
                    try {
                        JSONObject error = new JSONObject("{\"error\":" + e.getMessage() + "}");
                        callBack.apply(error);
                    } catch (JSONException jsonE) {
                        //TODO
                    }
                }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                }
            });

        mSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static void reqDetail(Context context, String placeId, final mCallback callBack) {

        String url = HOST + getDetail + placeId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                try {
                    callBack.apply(response);
                } catch (Exception e) {
                    e .printStackTrace();
                }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                }
            });

        mSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static void reqRoute(Context context, Map<String, String> param, final mCallback callBack) {

        String req_data = "";

        try {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                if (req_data != null && !req_data.isEmpty()) {
                    req_data += '&';
                }
                req_data += entry.getKey() + '=' + entry.getValue();
            }
        } catch (Exception e) {

        }


        // Building the url to the web service
        String url = getDirection + req_data;
        Log.i(TAG, url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            callBack.apply(response);
                        } catch (Exception e) {
                            e .printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });

        mSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static void reqPage(Context context, String pageToken, final mCallback callBack) {

        String url = HOST + getPage + pageToken;
        Log.i(TAG, url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            callBack.apply(response);
                        } catch (Exception e) {
                            e .printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });
        mSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

}

