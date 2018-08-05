package com.example.wendy.myapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wendy.myapp.R;
import com.example.wendy.myapp.module.PlaceItem;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Utils {
    public static String timeFormat(long unixSeconds) {
        Date date = new java.util.Date(unixSeconds*1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-8"));
        return sdf.format(date);

    }

    public static List<PlaceItem> getAllSharePref(SharedPreferences sharedPref) {
        Map<String, ?> allEntries = sharedPref.getAll();
        List<PlaceItem> res = new ArrayList<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Gson gson = new Gson();
            String placeStr = (String)entry.getValue();
            PlaceItem place = gson.fromJson(placeStr, PlaceItem.class);
            res.add(place);
        }
        return res;
    }

    public static void showToast(Context context, int content) {
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        View view = toast.getView();

//        view.setBackgroundColor(context.getResources().getColor(R.color.Gray200));
        view.getBackground().setColorFilter(context.getResources().getColor(R.color.Gray200),
                PorterDuff.Mode.SRC_IN);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setTextColor(Color.BLACK);
        toast.show();
    }

    public static void showToast(Context context, String content) {
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.getBackground().setColorFilter(context.getResources().getColor(R.color.Gray200),
                PorterDuff.Mode.SRC_IN);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setTextColor(Color.BLACK);
        toast.show();
    }

    public static void showProgress(final boolean show,
                                    FragmentManager fm, ProgressDialogFragment progressDialogFragment) {
        if (show) {
            progressDialogFragment.show(fm, "dialog");
        } else {
            progressDialogFragment.dismiss();
        }
    }


}
