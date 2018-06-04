package com.example.android.petpics.model;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;
import android.widget.ImageView;

import com.example.android.petpics.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repo {

    //Sample URL to retrieve data from to display to user
    private static final String BASE_URL = "https://www.reddit.com/";

    public MutableLiveData<List<AwwImage>> getLiveData() {

        final MutableLiveData<List<AwwImage>> data = new MutableLiveData<>();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        Call<RedditPOJO> redditCall
                = retrofit.create(RedditAPI.class).retrieveData("https://www.reddit.com/r/aww+rarepuppers+corgi/.json?limit=100");

        redditCall.enqueue(new Callback<RedditPOJO>() {
            @Override
            public void onResponse(Call<RedditPOJO> call, Response<RedditPOJO> response) {
                RedditPOJO.RedditPost[] redditPosts = response.body().getData().getChildren();
                List<AwwImage> awwImageList = new ArrayList<>();
                for(RedditPOJO.RedditPost redditPost : redditPosts) {
                    try {
                        RedditPostData redditPostData = redditPost.getData();
                        RedditMedia media = redditPostData.getMedia();
                        RedditPreview preview = redditPostData.getPreview();
                        RedditPostData parent = redditPostData.getCrosspost_parent_list();

                        String title = redditPostData.getTitle();
                        String thumbnail = redditPostData.getThumbnail();
                        String link = BASE_URL + redditPostData.getPermalink();
                        String primary;
                        String fallback;
                        boolean isVideo;

                        if(media != null) {
                            RedditVideo redditVideo = media.getReddit_video();
                            if(redditVideo != null) {
                                primary = redditVideo.getScrubber_media_url();
                                fallback = redditVideo.getFallback_url();
                                isVideo = true;
                            } else {
                                continue;
                            }
                        }
                        else if (preview != null && preview.getReddit_video_preview() != null) {
                            RedditVideo redditVideo = preview.getReddit_video_preview();
                            primary = redditVideo.getScrubber_media_url();
                            fallback = redditVideo.getFallback_url();
                            isVideo = true;
                        }
                        else if (parent != null && parent.getMedia() != null) {
                            Log.d("jakk", "parent != null");
                            RedditMedia parentMedia = parent.getMedia();
                            if(parentMedia != null) {
                                RedditVideo redditVideo = parentMedia.getReddit_video();
                                primary = redditVideo.getScrubber_media_url();
                                fallback = redditVideo.getFallback_url();
                                isVideo = true;
                            } else {
                                continue;
                            }
                        } else {
                            primary = redditPostData.getUrl();
                            fallback = thumbnail;
                            isVideo = false;
                        }
                        AwwImage awwImage = new AwwImage(primary, fallback, title, thumbnail, link, isVideo);

                        awwImageList.add(awwImage);
                        Log.d("pusha", redditPostData.getThumbnail() + "\n " + redditPostData.getUrl());
                    } catch (Exception e) {
                        Log.d("pusha drake", e.toString());
                    }
                }
                data.postValue(awwImageList);
            }

            @Override
            public void onFailure(Call<RedditPOJO> call, Throwable t) {
                Log.d("pusha", "FAILLL" + t.toString());
            }
        });

        return data;
    }
}
