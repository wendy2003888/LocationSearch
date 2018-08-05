package com.example.wendy.myapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.wendy.myapp.R;
import com.example.wendy.myapp.listener.RecyclerViewClickListener;
import com.example.wendy.myapp.module.Review;
import com.example.wendy.myapp.service.mSingleton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<Review> dataset;
    private RecyclerViewClickListener mListener;
    private Context context;
    private ImageLoader imageloader;


    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        // each data item is just a string in this case
        private NetworkImageView iconView;
        private TextView nameView;
        private RatingBar ratingBar;
        private TextView dateView;
        private TextView commentView;
        private RecyclerViewClickListener mListener;


        public ViewHolder(View v, RecyclerViewClickListener listener) {
            super(v);
            iconView = (NetworkImageView) v.findViewById(R.id.icon);
            nameView = (TextView) v.findViewById(R.id.name);
            ratingBar = (RatingBar) v.findViewById(R.id.rating);
            dateView = (TextView) v.findViewById(R.id.date);
            commentView = (TextView) v.findViewById(R.id.comment);
            iconView.setDefaultImageResId(android.R.drawable.screen_background_light_transparent);
            iconView.setErrorImageResId(R.mipmap.user_large_square);
            mListener = listener;
            v.setOnClickListener(this);
        }

        public NetworkImageView getIconView() {
            return iconView;
        }

        public TextView getNameView() {
            return nameView;
        }

        public RatingBar getratingBar() {
            return ratingBar;
        }

        public TextView getDateView() {
            return dateView;
        }

        public TextView getCommentView() {
            return commentView;
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    public ReviewAdapter(List<Review> review, RecyclerViewClickListener listener) {
        dataset = new ArrayList<>();
        if(review != null && review.size() > 0) {
            dataset.addAll(review);
        }
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_review, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v, mListener);
        context = parent.getContext();
        imageloader = mSingleton.getInstance(context).getImageLoader();
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        try {

            Review cur = dataset.get(position);
            viewHolder.getIconView().setImageUrl(cur.getProfilePhotoUrl(), imageloader);
            viewHolder.getNameView().setText(cur.getAuthorName());
            BigDecimal rating = new BigDecimal(cur.getRating().toString());
            viewHolder.getratingBar().setRating(rating.floatValue());
            LayerDrawable stars = (LayerDrawable) viewHolder.getratingBar().getProgressDrawable();
            stars.getDrawable(2)
                    .setColorFilter(context.getResources().getColor(R.color.colorAccent),
                            PorterDuff.Mode.SRC_ATOP); // for filled stars
            stars.getDrawable(1)
                    .setColorFilter(context.getResources().getColor(R.color.lightGray),
                            PorterDuff.Mode.SRC_ATOP); // for half filled stars
            stars.getDrawable(0)
                    .setColorFilter(context.getResources().getColor(R.color.lightGray),
                            PorterDuff.Mode.SRC_ATOP); // for empty stars
            viewHolder.getDateView().setText(cur.getTime());
            viewHolder.getCommentView().setText(cur.getText());
        } catch (Exception e) {
            //TODO
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void swap(List<Review> data) {
        dataset.clear();
        dataset.addAll(data);
        notifyDataSetChanged();
    }
}
