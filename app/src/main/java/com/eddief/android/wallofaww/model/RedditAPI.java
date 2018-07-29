package com.eddief.android.wallofaww.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RedditAPI {

    @GET("/r/{subs}/.json?limit=100")
    Call<RedditPOJO> retrieveData(
            @Path("subs") String subs,
            @Query("after") String id
    );
}
