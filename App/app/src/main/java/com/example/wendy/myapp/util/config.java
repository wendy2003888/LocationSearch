package com.example.wendy.myapp.util;

import com.google.android.gms.maps.model.LatLngBounds;

public class config {
     public static final String HOST = "https://wendyh2003888.appspot.com/";
     public static final String HOST2 = "http://nodeapp-666.us-east-2.elasticbeanstalk.com/";
     public static final String HOST_MAP = "https://maps.googleapis.com/maps/api/";
     public static final String getResult = "gg/nearby?";
     public static final String getPage = "gg/page?pagetoken=";
     public static final String getDetail = "gg/detail?id=";
     public static final String getPhoto = "gg/photo?reference=";
     public static final String getDirection = "https://maps.googleapis.com/maps/api/directions/json?";
     public static final String getYelpBest = "yelp/best?";
     public static final String getYelpReview = "yelp/business?id=";
     public static final String TWI_HASHTAG = "TravelAndEntertainmentSearch";
     public static final String TWITTER_FORMATTED = "https://twitter.com/intent/tweet?" +
             "text=Check out %s located at %s . Website: &url=%s&hashtags=" + TWI_HASHTAG;

     private static LatLngBounds CUR_BOUNDS;

     public static void setCurBounds(LatLngBounds latLngBounds){
          CUR_BOUNDS = latLngBounds;
     }

     public static LatLngBounds getCUR_BOUNDS() {
          return CUR_BOUNDS;
     }
}
