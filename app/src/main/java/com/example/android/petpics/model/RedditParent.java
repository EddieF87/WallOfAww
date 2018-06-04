package com.example.android.petpics.model;

import com.google.gson.annotations.SerializedName;

public class RedditParent {

    @SerializedName("media")
    private RedditMedia media;

    public RedditMedia getMedia() {
        return media;
    }
}