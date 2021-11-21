package com.eddief.android.infiniteaww.model

import com.google.gson.annotations.SerializedName

class RedditPOJO(
    @SerializedName("data")
    val data: RedditData
) {
    inner class RedditData(
        @SerializedName("children")
        val children: Array<RedditPost>
    ) {
        inner class RedditPost(
            @SerializedName("data")
            val data: RedditPostData
        )
    }
}