package com.example.android.petpics.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RedditPostData implements Parcelable {

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

//    @SerializedName("crosspost_parent_list")
//    private RedditPostData crosspost_parent_list;

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

//    public RedditPostData getCrosspost_parent_list() {
//        if(crosspost_parent_list == null) {
//            return null;
//        }
//        return crosspost_parent_list;
//    }

    protected RedditPostData(Parcel in) {
        thumbnail = in.readString();
        url = in.readString();
        is_video = in.readString();
        title = in.readString();
        permalink = in.readString();
        media = (RedditMedia) in.readValue(RedditMedia.class.getClassLoader());
        preview = (RedditPreview) in.readValue(RedditPreview.class.getClassLoader());
//        crosspost_parent_list = (RedditPostData) in.readValue(RedditPostData.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(thumbnail);
        dest.writeString(url);
        dest.writeString(is_video);
        dest.writeString(title);
        dest.writeString(permalink);
        dest.writeValue(media);
        dest.writeValue(preview);
//        dest.writeValue(crosspost_parent_list);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RedditPostData> CREATOR = new Parcelable.Creator<RedditPostData>() {
        @Override
        public RedditPostData createFromParcel(Parcel in) {
            return new RedditPostData(in);
        }

        @Override
        public RedditPostData[] newArray(int size) {
            return new RedditPostData[size];
        }
    };
}