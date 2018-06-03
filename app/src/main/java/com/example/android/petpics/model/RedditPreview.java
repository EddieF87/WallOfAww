package com.example.android.petpics.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RedditPreview implements Parcelable {


    public RedditVideo getReddit_video_preview() {
        return reddit_video_preview;
    }

    @SerializedName("reddit_video_preview")
    private RedditVideo reddit_video_preview;


    protected RedditPreview(Parcel in) {
        reddit_video_preview = (RedditVideo) in.readValue(RedditVideo.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(reddit_video_preview);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RedditPreview> CREATOR = new Parcelable.Creator<RedditPreview>() {
        @Override
        public RedditPreview createFromParcel(Parcel in) {
            return new RedditPreview(in);
        }

        @Override
        public RedditPreview[] newArray(int size) {
            return new RedditPreview[size];
        }
    };
}