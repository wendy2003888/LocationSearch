package com.example.wendy.myapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wendy.myapp.adapter.mListAdapter;
import com.example.wendy.myapp.module.PlaceItem;
import com.example.wendy.myapp.listener.RecyclerViewClickListener;
import com.example.wendy.myapp.service.mCallback;
import com.example.wendy.myapp.util.ProgressDialogFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.example.wendy.myapp.service.GG.reqPage;
import static com.example.wendy.myapp.util.Utils.getAllSharePref;
import static com.example.wendy.myapp.util.Utils.showProgress;
import static com.example.wendy.myapp.util.Utils.showToast;

public class mListActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String TAG = "mListAct";


    private Context context;
    private Intent intent;
    private JSONObject data;
    private List<PlaceItem> resultData;
    private String nextPage;
    private Stack<String> pageTokens;
    private Stack<List<PlaceItem>> pageItems;

    private RecyclerView mRecyclerView;
    private mListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView noRecordView;
    private Button btnPrev;
    private Button btnNext;
    private View mProgressView;
    private View containerView;
    private PopupWindow mProgressWin;
    private ProgressDialogFragment progressDialog;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_list);
        resultData = new ArrayList<>();
        pageTokens = new Stack();
        pageItems = new Stack<List<PlaceItem>>();
        context = this;
        intent = getIntent();

        initData();

        progressDialog = new ProgressDialogFragment().newInstance(R.string.action_page);


        mRecyclerView = (RecyclerView) findViewById(R.id.my_list);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                PlaceItem place = resultData.get(position);
                Intent detailIntent = new Intent(context, DetailActivity.class);
                Gson gson = new Gson();
                String placeStr = gson.toJson(place);
                detailIntent.putExtra("place", placeStr);
                startActivity(detailIntent);
            }
        };

        mAdapter = new mListAdapter(resultData, listener);
        mRecyclerView.setAdapter(mAdapter);
        noRecordView = findViewById(R.id.norecord);
        if (resultData.size() == 0) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            noRecordView.setVisibility(View.VISIBLE);
        } else {
            noRecordView.setVisibility(View.INVISIBLE);
        }

        containerView = findViewById(R.id.container);

        btnPrev = findViewById(R.id.btn_pre);
        btnNext = findViewById(R.id.btn_next);
        toggleButtons();
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true,  getSupportFragmentManager(), progressDialog);
                resultData.clear();
                List<PlaceItem> cur = pageItems.pop();
                resultData.addAll(cur);
                nextPage = pageTokens.pop();
                mAdapter.swap(resultData);
                toggleButtons();
                showProgress(false,  getSupportFragmentManager(), progressDialog);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true,  getSupportFragmentManager(), progressDialog);

                pageItems.push(new ArrayList(resultData));
                pageTokens.push(nextPage);
                getPage(nextPage);
            }
        });


    }

    private void initData() {
        try {
            String data_string = intent.getStringExtra("data");
            JSONObject data = new JSONObject(data_string);
            load2List(data);
        } catch (Exception e) {
            //Todo:
        }
    }

    private void getPage(String pageToken) {
        reqPage(this, pageToken, new mCallback() {
            @Override
            public void apply(JSONObject res) {
                Log.i(TAG, String.valueOf(resultData.size()));
                try {
                    if (res.getString("status").equals("OK")) {
                        load2List(res);
                        mAdapter.swap(resultData);
                        toggleButtons();
                        Log.i(TAG, String.valueOf(resultData.size()));
                    } else {
                        showToast(context, R.string.error_req);
                    }
                    showProgress(false,  getSupportFragmentManager(), progressDialog);
                } catch (Exception e){

                }
            }
        });

    }

    private void load2List(JSONObject data){
        Log.i(TAG, data.toString());
        try {
            nextPage = data.has("next_page_token") ? data.getString("next_page_token") : null;
            JSONArray results = data.getJSONArray("results");
            resultData.clear();
            for (int i = 0; i < results.length(); i++) {
                JSONObject cur = results.getJSONObject(i);
                PlaceItem tmp = new PlaceItem(cur.getString("place_id"),
                        cur.getString("icon"),
                        cur.getString("name"),
                        cur.getString("vicinity"));
                resultData.add(tmp);
            }
        } catch (JSONException e) {
            //Todo:
            e.getStackTrace();
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key) {
        Log.i(TAG, key);
//        mAdapter.swap(resultData);
    }

//    @Override
//    public void onRestart() {
//        super.onRestart();
//        //When BACK BUTTON is pressed, the activity on the stack is restarted
//        //Do what you want on the refresh procedure here
//        mAdapter.swap(resultData);
//    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.swap(new ArrayList<PlaceItem>(resultData));
    }

    private void toggleButtons() {
        btnPrev.setEnabled(true);
        btnNext.setEnabled(true);
        if (pageItems.empty()) {
            btnPrev.setEnabled(false);
        }
        if (nextPage == null) {
            btnNext.setEnabled(false);
        }
    }

}
