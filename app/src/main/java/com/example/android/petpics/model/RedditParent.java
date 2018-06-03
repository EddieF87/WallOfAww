package com.example.android.petpics.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RedditParent implements Parcelable {

    @SerializedName("media")
    private RedditMedia media;

    public RedditMedia getMedia() {
        return media;
    }

    protected RedditParent(Parcel in) {
        media = (RedditMedia) in.readValue(RedditMedia.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(media);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RedditParent> CREATOR = new Parcelable.Creator<RedditParent>() {
        @Override
        public RedditParent createFromParcel(Parcel in) {
            return new RedditParent(in);
        }

        @Override
        public RedditParent[] newArray(int size) {
            return new RedditParent[size];
        }
    };
}