package com.example.wendy.myapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.wendy.myapp.R;
import com.example.wendy.myapp.listener.RecyclerViewClickListener;
import com.example.wendy.myapp.module.PlaceItem;
import com.example.wendy.myapp.service.mSingleton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.example.wendy.myapp.util.Utils.showToast;

public class mListAdapter extends RecyclerView.Adapter<mListAdapter.ViewHolder>{
    private List<PlaceItem> dataset;
    private RecyclerViewClickListener mListener;
    private Context context;
    private ImageLoader imageloader;
    private SharedPreferences sharedPref;


    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        // each data item is just a string in this case
        private NetworkImageView iconView;
        private TextView nameView;
        private TextView addrView;
        private ImageButton favButton;
        private RecyclerViewClickListener mListener;


        public ViewHolder(View v, RecyclerViewClickListener listener) {
            super(v);
            iconView = (NetworkImageView) v.findViewById(R.id.icon);
            nameView = (TextView) v.findViewById(R.id.name);
            addrView = (TextView) v.findViewById(R.id.rating);
            favButton = (ImageButton) v.findViewById(R.id.fav_button);
            iconView.setDefaultImageResId(android.R.drawable.screen_background_light_transparent);
            iconView.setErrorImageResId(R.mipmap.ic_launcher_round);
            mListener = listener;
            v.setOnClickListener(this);
        }

        public TextView getNameView() {
            return nameView;
        }

        public TextView getAddrView() {
            return addrView;
        }

        public NetworkImageView getIconView() {
            return iconView;
        }

        public ImageButton getFavButton() {
            return favButton;
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    public mListAdapter(List<PlaceItem> _dataset, RecyclerViewClickListener listener) {
        dataset = new ArrayList<>();
        dataset.addAll(_dataset);
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v, mListener);
        context = parent.getContext();
        imageloader = mSingleton.getInstance(context).getImageLoader();
        sharedPref = context.getSharedPreferences(
                context.getString(R.string.fav_file_key),
                Context.MODE_PRIVATE);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final PlaceItem cur = dataset.get(position);

        viewHolder.getIconView().setImageUrl(cur.getIconUrl(), imageloader);
        viewHolder.getNameView().setText(cur.getName());
        viewHolder.getAddrView().setText(cur.getAddr());
        if (sharedPref.contains(cur.getPlaceId())) {
            viewHolder.getFavButton().setImageResource(R.drawable.heart_fill_red);

        } else {
            viewHolder.getFavButton().setImageResource(R.drawable.heart_outline_black);
        }

        viewHolder.getFavButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageButton button = (ImageButton)view;
                if (sharedPref.contains(cur.getPlaceId())){
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.remove(cur.getPlaceId());
//                    editor.commit();
                    editor.apply();
                    button.setImageResource(R.drawable.heart_outline_black);
                    String text = String.format(context.getString(R.string.unfav_format), cur.getName());
                    showToast(context, text);
                } else {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    Gson gson = new Gson();
                    String placeStr = gson.toJson(cur);
                    editor.putString (cur.getPlaceId(), placeStr);
                    editor.apply();
//                    editor.commit();
                    button.setImageResource(R.drawable.heart_fill_red);
                    String text = String.format(context.getString(R.string.fav_format), cur.getName());
                    showToast(context, text);
                }
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void swap(List<PlaceItem> data) {
        dataset.clear();
        dataset.addAll(data);
        notifyDataSetChanged();
        return;
    }

}
