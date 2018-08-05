package com.example.wendy.myapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.wendy.myapp.DetailActivity;
import com.example.wendy.myapp.InfoFragment;
import com.example.wendy.myapp.MapFragment;
import com.example.wendy.myapp.PhotoFragment;
import com.example.wendy.myapp.ReviewFragment;

import org.json.JSONObject;

public class DetailPagerAdapter extends FragmentPagerAdapter {
    String placeId;
    JSONObject place;

    public DetailPagerAdapter(FragmentManager fm, String _placeId, JSONObject _place) {
        super(fm);
        placeId = _placeId;
        place = _place;
    }


    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return new InfoFragment().newInstance(place);
            case 1:
                return new PhotoFragment().newInstance(placeId);
            case 2:
                return new MapFragment().newInstance(place);
            case 3:
                return new ReviewFragment().newInstance(place);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
