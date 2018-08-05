package com.example.wendy.myapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wendy.myapp.adapter.mListAdapter;
import com.example.wendy.myapp.module.PlaceItem;
import com.example.wendy.myapp.listener.RecyclerViewClickListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.example.wendy.myapp.util.Utils.getAllSharePref;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    private RecyclerView mRecyclerView;
    private mListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView noRecordView;


    private SharedPreferences sharedPref;
    private List<PlaceItem> favData;

    //Todo: real time update, init red heart, delete

    public FavFragment() {
        // Required empty public constructor
    }


    public static FavFragment newInstance( ) {
        FavFragment fragment = new FavFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = this.getActivity().getSharedPreferences(
                this.getString(R.string.fav_file_key),
                Context.MODE_PRIVATE);
        initData();

        sharedPref.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fav, container, false);
        noRecordView = rootView.findViewById(R.id.norecord);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fav_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                PlaceItem place = favData.get(position);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                Gson gson = new Gson();
                String placeStr = gson.toJson(place);
                detailIntent.putExtra("place", placeStr);
                startActivity(detailIntent);
            }
        };
        mAdapter = new mListAdapter(favData, listener);
        mRecyclerView.setAdapter(mAdapter);

        setRecordView();
        return rootView;
    }

    private void initData() {

        favData = getAllSharePref(sharedPref);
    }

    public void setRecordView() {
        if (favData == null || favData.size() == 0) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            noRecordView.setVisibility(View.VISIBLE);
        } else {
            noRecordView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mAdapter.swap(new ArrayList<PlaceItem>(favData));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key) {
        favData.clear();
        favData = getAllSharePref(sharedPref);
        mAdapter.swap(favData);
        setRecordView();
    }
}
