package com.example.android.petpics.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RedditMedia implements Parcelable {
    @SerializedName("reddit_video")
    private RedditVideo reddit_video;

    public RedditVideo getReddit_video() {
        return reddit_video;
    }

    protected RedditMedia(Parcel in) {
        reddit_video = (RedditVideo) in.readValue(RedditVideo.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(reddit_video);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RedditMedia> CREATOR = new Parcelable.Creator<RedditMedia>() {
        @Override
        public RedditMedia createFromParcel(Parcel in) {
            return new RedditMedia(in);
        }

        @Override
        public RedditMedia[] newArray(int size) {
            return new RedditMedia[size];
        }
    };
}