package com.example.android.petpics.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RedditAPI {

    @GET
    Call<RedditPOJO> retrieveData(
            @Url String url
    );
}
