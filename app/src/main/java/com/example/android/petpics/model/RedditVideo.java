package com.example.android.petpics.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RedditVideo implements Parcelable {

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

    protected RedditVideo(Parcel in) {

        scrubber_media_url = in.readString();
        fallback_url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(scrubber_media_url);
        dest.writeString(fallback_url);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RedditVideo> CREATOR = new Parcelable.Creator<RedditVideo>() {
        @Override
        public RedditVideo createFromParcel(Parcel in) {
            return new RedditVideo(in);
        }

        @Override
        public RedditVideo[] newArray(int size) {
            return new RedditVideo[size];
        }
    };
}