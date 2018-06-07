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

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

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

    public static class RedditMedia {
        @SerializedName("reddit_video")
        private RedditVideo reddit_video;

        public RedditVideo getReddit_video() {
            return reddit_video;
        }
    }

    static class RedditParent {

        @SerializedName("media")
        private RedditMedia media;

        public RedditMedia getMedia() {
            return media;
        }
    }

    public static class RedditVideo {

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

    public static class RedditPreview {


        public RedditVideo getReddit_video_preview() {
            return reddit_video_preview;
        }

        @SerializedName("reddit_video_preview")
        private RedditVideo reddit_video_preview;
    }
}