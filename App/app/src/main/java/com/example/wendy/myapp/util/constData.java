package com.example.wendy.myapp.util;

import com.example.wendy.myapp.module.BaseDropdownEntity;
import com.example.wendy.myapp.module.OrderType;

import java.util.ArrayList;

public class constData {
    public static final String cate_values[] = {
            "default",
            "airport",
            "amusement_park",
            "aquarium",
            "art_gallery",
            "bakery",
            "bar",
            "beauty_salon",
            "bowling_alley",
            "bus_station",
            "cafe",
            "campground",
            "car_rental",
            "casino",
            "lodging",
            "movie_theater",
            "museum",
            "night_club",
            "park",
            "parking",
            "restaurant",
            "shopping_mall",
            "stadium",
            "subway_station",
            "taxi_stand",
            "train_station",
            "transit_station",
            "travel_agency",
            "zoo"};

    public static final String info_keys[] = {
            "formatted_address",
            "international_phone_number",
            "price_level",
            "rating",
            "url",
            "website"};
//"opening_hours"

    public static final String sourceTypesValue[] = {
            "gg",
            "yelp"
    };

    public static final String sourceTypesName[] = {
            "Google Reviews",
            "Yelp Reviews"
    };

    public static final ArrayList<BaseDropdownEntity> getSourceTypes() {
        ArrayList<BaseDropdownEntity> sourceTypes = new ArrayList<BaseDropdownEntity>();
        for (int i = 0; i < sourceTypesName.length; ++i) {
            sourceTypes.add(new BaseDropdownEntity(sourceTypesName[i], sourceTypesValue[i]));
        }
        return sourceTypes;
    }


    public static final String orderTypesValue[] = {
            "default",
            "highest",
            "lowest",
            "mostR",
            "leastR"
    };

    public static final String orderTypesName[] = {
            "Default Order",
            "Highest Rating",
            "Lowest Rating",
            "Most Recent",
            "Least Recent"
    };

    public static final ArrayList<BaseDropdownEntity> getOrderTypes() {
        ArrayList<BaseDropdownEntity> orderTypes = new ArrayList<BaseDropdownEntity>();
        for (int i = 0; i < orderTypesName.length; ++i) {
            orderTypes.add(new OrderType(orderTypesName[i], orderTypesValue[i]));
        }
        return orderTypes;
    }

    public static final String ModeOptName[] = {
            "Driving",
            "Bicycling",
            "Transit",
            "Walking"
    };

    public static final ArrayList<BaseDropdownEntity> getModes() {
        ArrayList<BaseDropdownEntity> modes = new ArrayList<BaseDropdownEntity>();
        for (int i = 0; i < ModeOptName.length; ++i) {
            modes.add(new BaseDropdownEntity(ModeOptName[i], ModeOptName[i].toLowerCase()));
        }
        return modes;
    }

}
