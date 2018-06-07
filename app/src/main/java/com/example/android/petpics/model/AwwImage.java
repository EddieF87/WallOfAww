package com.example.android.petpics.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "aww_table")
public class AwwImage implements Parcelable {

    @PrimaryKey
    @NonNull
    private final String primaryUrl;
    @NonNull
    private final String thumbnail;
    @NonNull
    private final String link;

    private final String fallbackUrl;
    private final String title;
    private final boolean isVideo;
    private final String name;

    public AwwImage(@NonNull String primaryUrl, String fallbackUrl, String title, @NonNull String thumbnail, @NonNull String link, boolean isVideo, String name) {
        this.primaryUrl = primaryUrl;
        this.fallbackUrl = fallbackUrl;
        this.title = title;
        this.thumbnail = thumbnail;
        this.link = link;
        this.isVideo = isVideo;
        this.name = name;
    }

    @NonNull
    public String getPrimaryUrl() {
        return primaryUrl;
    }

    public String getFallbackUrl() {
        return fallbackUrl;
    }

    public String getTitle() {
        return title;
    }

    @NonNull
    public String getThumbnail() {
        return thumbnail;
    }

    @NonNull
    public String getLink() {
        return link;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public String getName() {
        return name;
    }

    protected AwwImage(Parcel in) {
        primaryUrl = in.readString();
        fallbackUrl = in.readString();
        title = in.readString();
        thumbnail = in.readString();
        link = in.readString();
        name = in.readString();
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
        dest.writeString(name);
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