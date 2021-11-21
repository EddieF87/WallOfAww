package com.eddief.android.infiniteaww.data

import com.eddief.android.infiniteaww.model.RedditPOJO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RedditAPI {

    companion object {
        private const val LIMIT = 100
    }

    @GET("/r/{subs}/.json?limit=$LIMIT")
    fun retrieveData(
        @Path("subs") subs: String,
        @Query("after") id: String?
    ): Call<RedditPOJO>
}