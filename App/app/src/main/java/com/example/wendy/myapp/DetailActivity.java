package com.example.wendy.myapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.wendy.myapp.adapter.DetailPagerAdapter;
import com.example.wendy.myapp.module.PlaceItem;
import com.example.wendy.myapp.service.mCallback;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URLEncoder;

import static com.example.wendy.myapp.service.GG.reqDetail;
import static com.example.wendy.myapp.util.Utils.showToast;
import static com.example.wendy.myapp.util.config.TWITTER_FORMATTED;

public class DetailActivity extends AppCompatActivity {

    private DetailPagerAdapter mSectionsPagerAdapter;
    private SharedPreferences sharedPref;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private PlaceItem place;
    private String placeId;
    private int[] tabIcons = {
            R.drawable.info_outline,
            R.drawable.photo,
            R.drawable.map,
            R.drawable.review
    };
    private int[] tabTitle = {
            R.string.tab_text_info,
            R.string.tab_text_photo,
            R.string.tab_text_map,
            R.string.tab_text_review,
    };
    private Boolean isFav;
    private String webpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Gson gson = new Gson();
        Intent intent = getIntent();
        String placeStr = intent.getStringExtra("place");
        place = gson.fromJson(placeStr, PlaceItem.class);
        placeId = place.getPlaceId();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(place.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedPref = getSharedPreferences(
                this.getString(R.string.fav_file_key),
                Context.MODE_PRIVATE);
        isFav = sharedPref.contains(placeId) ? true: false;

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        for (int i = 0; i < 4; i++) {
            View tabView = LayoutInflater.from(getBaseContext()).inflate(R.layout.custom_tab, null);
            ImageView tabIcon = (ImageView) tabView.findViewById(R.id.tab_icon);
            TextView tabName = (TextView) tabView.findViewById(R.id.tab_name);
            tabName.setText(tabTitle[i]);
            tabIcon.setImageDrawable(getDrawable(tabIcons[i]));
            tabLayout.getTabAt(i).setCustomView(tabView);
        }

        getDetail(this, placeId, new mCallback() {
            @Override
            public void apply(JSONObject res) {
                try {

                    JSONObject result = res.getJSONObject("result");
                    placeId = result.getString("place_id");
                    webpage = result.has("website") ? result.getString("website") : result.getString("url");

                    mSectionsPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), placeId, result);

                   mViewPager = (ViewPager) findViewById(R.id.container);
                mViewPager.setAdapter(mSectionsPagerAdapter);
                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

                } catch (Exception e) {
                    //Todo
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        if(isFav){
            menu.findItem(R.id.action_fav).setIcon(R.drawable.heart_fill_white);
        } else {
            menu.findItem(R.id.action_fav).setIcon(R.drawable.heart_outline_white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_share:
                share2Twitter();
                return true;

            case R.id.action_fav:
                if(isFav){
                    //change your view and sort it by Alphabet
                    item.setIcon(R.drawable.heart_outline_white);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.remove(placeId);
                    editor.apply();
                    String text = String.format(getString(R.string.unfav_format), place.getName());
                    showToast(this, text);
                    isFav=false;
                } else {
                    //change your view and sort it by Date of Birth
                    item.setIcon(R.drawable.heart_fill_white);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    Gson gson = new Gson();
                    String placeStr = gson.toJson(place);
                    editor.putString (placeId, placeStr);
//                    editor.commit();
                    editor.apply();
                    String text = String.format(getString(R.string.fav_format), place.getName());
                    showToast(this, text);
                    isFav=true;
                }
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void getDetail(Context context, String placeId, mCallback callback) {
        reqDetail(context, placeId, callback);
    }

    private void share2Twitter() {
        try {
            String url = String.format(TWITTER_FORMATTED,
                    URLEncoder.encode(place.getName(), "UTF-8"),
                    URLEncoder.encode(place.getAddr(), "UTF-8"),
                    webpage);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (Exception e) {

        }
        return;
    }



}
