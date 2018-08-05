package com.example.wendy.myapp.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.example.wendy.myapp.util.config.HOST;
import static com.example.wendy.myapp.util.config.getYelpBest;
import static com.example.wendy.myapp.util.config.getYelpReview;

public class Yelp {
    public static void reqYelpMatch(Context context, Map<String, String> param, final mCallback callBack) {

        String req_data = "";

        try {
            for ( Map.Entry<String, String> entry : param.entrySet() ) {
                if (req_data != null && !req_data.isEmpty()) {
                    req_data += '&';
                }
                req_data += entry.getKey() + '=' + URLEncoder.encode(entry.getValue().trim(), "UTF-8");
            }
        } catch (Exception e) {

        }
//        try {
//            req_data = URLEncoder.encode(req_data, "UTF-8");
//        } catch (UnsupportedEncodingException ignored) {
//            // Can be safely ignored because UTF-8 is always supported
//        }



        String url = HOST + getYelpBest + req_data;


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callBack.apply(response);
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                // Now you can use any deserializer to make sense of data
                                JSONObject obj = new JSONObject(res);

                            } catch (UnsupportedEncodingException e1) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                                // returned data is not JSONObject?
                                e2.printStackTrace();
                            }
                        }
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                };
        mSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }

    public static void reqYelpReview(Context context, String id, final mCallback callBack) {

        String url = HOST + getYelpReview + id;
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
