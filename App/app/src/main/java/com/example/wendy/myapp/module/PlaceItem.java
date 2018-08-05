package com.example.wendy.myapp.module;

public class PlaceItem {
    private String placeId;
    private String iconUrl;
    private String name;
    private String addr;

    public PlaceItem(String _placeId, String _iconUrl, String _name, String _addr) {
        placeId = _placeId;
        iconUrl = _iconUrl;
        name = _name;
        addr = _addr;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getName() {
        return name;
    }

    public String getAddr() {
        return addr;
    }
}
