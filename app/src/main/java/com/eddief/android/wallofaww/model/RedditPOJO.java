package com.eddief.android.wallofaww.model;

import com.google.gson.annotations.SerializedName;

public class RedditPOJO {

    @SerializedName("data")
    private RedditData data;

    public RedditData getData() {
        return data;
    }


    public class RedditData {

        @SerializedName("children")
        private RedditPost[] children;

        public RedditPost[] getChildren() {
            return children;
        }
    }

    public class RedditPost {

        @SerializedName("data")
        private RedditPostData data;


        public RedditPostData getData() {
            return data;
        }
    }
}
