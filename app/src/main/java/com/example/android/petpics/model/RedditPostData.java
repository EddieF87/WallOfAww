package com.example.android.petpics.model;

import com.google.gson.annotations.SerializedName;

public class RedditPostData {

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("url")
    private String url;

    @SerializedName("is_video")
    private String is_video;

    @SerializedName("title")
    private String title;

    @SerializedName("permalink")
    private String permalink;

    @SerializedName("media")
    private RedditMedia media;

    @SerializedName("preview")
    private RedditPreview preview;

    @SerializedName("crosspost_parent_list")
    private RedditPostData[] crosspost_parent_list;

    public String getIs_video() {
        return is_video;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public String getPermalink() {
        return permalink;
    }

    public RedditMedia getMedia() {
        return media;
    }

    public RedditPreview getPreview() {
        return preview;
    }

    public RedditPostData getCrosspost_parent_list() {
        if(crosspost_parent_list == null) {
            return null;
        }
        return crosspost_parent_list[0];
    }
}