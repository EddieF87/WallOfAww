package com.example.android.petpics.model;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repo {

    //Sample URL to retrieve data from to display to user
    private static final String SAMPLE_URL = "https://dl.dropboxusercontent.com/s/2iodh4vg0eortkl/facts.json";

    public MutableLiveData<List<RedditPostData>> getLiveData() {

        final MutableLiveData<List<RedditPostData>> data = new MutableLiveData<>();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://www.reddit.com/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        Call<RedditPOJO> redditCall
                = retrofit.create(RedditAPI.class).retrieveData("https://www.reddit.com/r/aww+rarepuppers+corgi/.json?limit=100");

        redditCall.enqueue(new Callback<RedditPOJO>() {
            @Override
            public void onResponse(Call<RedditPOJO> call, Response<RedditPOJO> response) {
                RedditPOJO.RedditPost[] redditPosts = response.body().getData().getChildren();
                List<RedditPostData> imageURLs = new ArrayList<>();
                for(RedditPOJO.RedditPost redditPost : redditPosts) {
                    try {
                        RedditPostData redditPostData = redditPost.getData();
//                                .getThumbnail();
//                                .getPreview().getImages().getSource().getUrl();
                        imageURLs.add(redditPostData);
                        Log.d("pusha", redditPostData.getThumbnail() + "\n " + redditPostData.getUrl());
                    } catch (Exception e) {
                        Log.d("pusha drake", e.toString());
                    }
                }
                data.postValue(imageURLs);
            }

            @Override
            public void onFailure(Call<RedditPOJO> call, Throwable t) {
                Log.d("pusha", "FAILLL" + t.toString());
            }
        });

        return data;
    }
}
