package com.eddief.android.infiniteaww.model

import com.google.gson.annotations.SerializedName

data class RedditPostData(
    @SerializedName("thumbnail")
    val thumbnail: String = "",
    @SerializedName("url")
    val url: String = "",
    @SerializedName("is_video")
    val is_video: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("permalink")
    val permalink: String = "",
    @SerializedName("media")
    val media: RedditMedia? = null,
    @SerializedName("preview")
    val preview: RedditPreview? = null,
    @SerializedName("crosspost_parent_list")
    val crosspost_parent_list: Array<RedditPostData> = emptyArray(),
    @SerializedName("name")
    val name: String = ""
) {
    data class RedditMedia(
        @SerializedName("reddit_video")
        val reddit_video: RedditVideo
    )

    data class RedditVideo(
        @SerializedName("scrubber_media_url")
        val scrubber_media_url: String = "",
        @SerializedName("fallback_url")
        val fallback_url: String = ""
    )

    data class RedditPreview(
        @SerializedName("reddit_video_preview")
        val reddit_video_preview: RedditVideo? = null
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RedditPostData

        if (url != other.url) return false
        if (title != other.title) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}