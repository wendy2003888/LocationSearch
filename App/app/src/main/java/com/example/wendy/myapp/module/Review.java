package com.example.wendy.myapp.module;

public class Review implements Cloneable {
    private String authorName;
    private String authorUrl;
    private String profilePhotoUrl;
    private Double rating;
    private String time;
    private String text;

    public Review(String _authorName,
                  String _authorURL,
                  String _profilePhotoUrl,
                  Double _rating,
                  String _time,
                  String _text) {
        authorName = _authorName;
        authorUrl = _authorURL;
        profilePhotoUrl = _profilePhotoUrl;
        rating = _rating;
        time = _time;
        text = _text;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public Double getRating() {
        return rating;
    }

    public String getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
