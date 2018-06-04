package com.example.android.petpics.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AwwImage implements Parcelable {

    private String primaryUrl;
    private String fallbackUrl;
    private String title;
    private String thumbnail;
    private String link;
    private boolean isVideo;

    public AwwImage(String primaryUrl, String fallbackUrl, String title, String thumbnail, String link, boolean isVideo) {
        this.primaryUrl = primaryUrl;
        this.fallbackUrl = fallbackUrl;
        this.title = title;
        this.thumbnail = thumbnail;
        this.link = link;
        this.isVideo = isVideo;
    }

    public String getPrimaryUrl() {
        return primaryUrl;
    }

    public String getFallbackUrl() {
        return fallbackUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getLink() {
        return link;
    }

    public boolean isVideo() {
        return isVideo;
    }

    protected AwwImage(Parcel in) {
        primaryUrl = in.readString();
        fallbackUrl = in.readString();
        title = in.readString();
        thumbnail = in.readString();
        link = in.readString();
        isVideo = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(primaryUrl);
        dest.writeString(fallbackUrl);
        dest.writeString(title);
        dest.writeString(thumbnail);
        dest.writeString(link);
        dest.writeByte((byte) (isVideo ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AwwImage> CREATOR = new Parcelable.Creator<AwwImage>() {
        @Override
        public AwwImage createFromParcel(Parcel in) {
            return new AwwImage(in);
        }

        @Override
        public AwwImage[] newArray(int size) {
            return new AwwImage[size];
        }
    };
}