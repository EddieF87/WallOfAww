package com.example.android.petpics.model;

import com.google.gson.annotations.SerializedName;

public class RedditMedia {
    @SerializedName("reddit_video")
    private RedditVideo reddit_video;

    public RedditVideo getReddit_video() {
        return reddit_video;
    }
}