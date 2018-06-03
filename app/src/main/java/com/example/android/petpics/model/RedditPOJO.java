package com.example.android.petpics.model;

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
//
//    public class RedditPostData {
//
//        @SerializedName("thumbnail")
//        private String thumbnail;
//
//        @SerializedName("url")
//        private String url;
//
//
//        public String getThumbnail() {
//            return thumbnail;
//        }
//
//        public String getUrl() {
//            return url;
//        }
//    }
//    public class RedditPostPreview {
//
//        @SerializedName("images")
//        private RedditPostImage[] images;
//
//
//        public RedditPostImage getImages() {
//            return images[0];
//        }
//    }
//    public class RedditPostImage {
//
//        @SerializedName("source")
//        private RedditImageSource source;
//
//
//        public RedditImageSource getSource() {
//            return source;
//        }
//    }
//
//    public class RedditImageSource {
//
//        @SerializedName("url")
//        private String url;
//
//
//        public String getUrl() {
//            return url;
//        }
//    }


}
