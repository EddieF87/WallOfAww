package com.example.android.petpics.model;

import com.google.gson.annotations.SerializedName;

public class RedditPreview {


    public RedditVideo getReddit_video_preview() {
        return reddit_video_preview;
    }

    @SerializedName("reddit_video_preview")
    private RedditVideo reddit_video_preview;
}