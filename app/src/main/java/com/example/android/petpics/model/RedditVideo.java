package com.example.android.petpics.model;

import com.google.gson.annotations.SerializedName;

public class RedditVideo {

    @SerializedName("scrubber_media_url")
    private String scrubber_media_url;

    public String getScrubber_media_url() {
        return scrubber_media_url;
    }

    @SerializedName("fallback_url")
    private String fallback_url;

    public String getFallback_url() {
        return fallback_url;
    }

}