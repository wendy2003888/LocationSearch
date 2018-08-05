package com.example.wendy.myapp.util;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DirectionsJSONParser {

    public List<List<LatLng>> parse(JSONArray data){

        List<List<LatLng>> routes = new ArrayList<List<LatLng>>() ;
        try {
            for (int i = 0; i < data.length(); ++i) {
                JSONArray jLegs = data.getJSONObject(i).
                        getJSONArray("legs");
                List path = new ArrayList<LatLng>();
                // leg corresponding to waypoints (medi destination)
                for (int j = 0; j < jLegs.length(); ++j) {
                    JSONArray jSteps = jLegs.getJSONObject(0).getJSONArray("steps");

                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = jSteps.getJSONObject(k).
                                getJSONObject("polyline").getString("points");
                        List<LatLng> list = decodePoly(polyline);

                        //all points
                        for(int p = 0; p < list.size(); p++){
                            LatLng hm = new LatLng(list.get(p).latitude, list.get(p).longitude);
                            path.add(hm);
                        }
                    }
                }
                routes.add(path);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return routes;
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
