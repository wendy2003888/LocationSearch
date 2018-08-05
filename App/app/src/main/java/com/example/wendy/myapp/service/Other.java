package com.example.wendy.myapp.service;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class Other {
    public static void getCurLoc(Context context, final mCallback callBack) {
        String url ="http://ip-api.com/json";

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
