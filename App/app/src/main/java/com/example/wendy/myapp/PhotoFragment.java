package com.example.wendy.myapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;



public class PhotoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String TAG = "photoFrag";
    private static final String ARG_PLACEID = "placeId";

    private String placeId;
    protected GeoDataClient mGeoDataClient;
    private LinearLayout linearLayout;
    private TextView noRecordView;


    public PhotoFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PhotoFragment newInstance(String  placeId) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLACEID, placeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        // Construct a PlaceDetectionClient.
        if (getArguments() != null) {
            placeId = getArguments().getString(ARG_PLACEID);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        noRecordView = rootView.findViewById(R.id.norecord);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.container);
        getPhotos(placeId);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void getPhotos(String placeId) {
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();

                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                if(photoMetadataBuffer.getCount() == 0) {
                    noRecordView.setVisibility(View.VISIBLE);
                } else {
                    noRecordView.setVisibility(View.INVISIBLE);
                }
                for (int i = 0; i < photoMetadataBuffer.getCount(); ++i) {
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(i);
                    final int imgId = i;
                    // Get the attribution text.
//                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();
                            ImageView imageView = new ImageView(getActivity());
                            imageView.setId(imgId);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 16 , 0, 16);
                            imageView.setLayoutParams(params);
                            float aspectRatio = bitmap.getWidth() /
                                    (float) bitmap.getHeight();
                            Bitmap resized = Bitmap.createScaledBitmap(bitmap,
                                    linearLayout.getWidth(),
                                    Math.round(linearLayout.getWidth() / aspectRatio),
                                    false);
                            imageView.setImageBitmap(resized);

                            linearLayout.addView(imageView);
                        }
                    });
                }
                photoMetadataBuffer.release();
            }
        });
    }

}
