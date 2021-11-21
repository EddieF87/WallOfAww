package com.eddief.android.infiniteaww.data

import android.app.Application
import com.eddief.android.infiniteaww.model.AwwImage
import com.eddief.android.infiniteaww.model.RedditPOJO
import com.eddief.android.infiniteaww.model.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class Repo(application: Application) {

    companion object {
        private const val BASE_URL: String = "https://www.reddit.com"

        private var INSTANCE: Repo? = null
        fun getInstance(application: Application): Repo {
            if (INSTANCE == null) {
                synchronized(Repo::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Repo(application)
                    }
                }
            }
            return INSTANCE!!
        }
    }

    private val db: AwwRoomDB = AwwRoomDB.getDatabase(application)
    private val mImageDao: ImageDao = db.imageDao()

    private var mFavorites: List<AwwImage> = emptyList()
    private var awwImages: MutableList<AwwImage> = mutableListOf()

    private var mImages: Resource<MutableList<AwwImage>>? = null

    private val redditAPI: RedditAPI = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RedditAPI::class.java)


    suspend fun addMoreImages(path: String): Resource<MutableList<AwwImage>> =
        withContext(Dispatchers.IO) {
            val after: String? = try {
                awwImages.lastOrNull()?.name
            } catch (ignored: Exception) {
                null
            }
            fetchImages(path, after)
        }

    suspend fun refreshData(path: String): Resource<MutableList<AwwImage>> =
        withContext(Dispatchers.IO) {
            awwImages.clear()
            fetchImages(path, null)
        }

    private suspend fun fetchImages(path: String, after: String?): Resource<MutableList<AwwImage>> {
        mImages = try {
            val redditPosts: Array<RedditPOJO.RedditData.RedditPost> =
                redditAPI.retrieveData(path, after).await().data.children
            val newAwwImageList = getList(redditPosts)
            awwImages.addAll(newAwwImageList)
            Resource.Success(awwImages)
        } catch (e: Exception) {
            Resource.Error(awwImages, e)
        }
        return mImages!!
    }

    private fun getList(redditPosts: Array<RedditPOJO.RedditData.RedditPost>): MutableList<AwwImage> {
        val awwImageList: MutableList<AwwImage> = ArrayList()
        redditPosts.forEach { redditPost ->
            try {
                val redditPostData = redditPost.data
                val media = redditPostData.media
                val preview = redditPostData.preview
                val parent = redditPostData.crosspost_parent_list.firstOrNull()
                val title = redditPostData.title
                val thumbnail = redditPostData.thumbnail
                if (thumbnail != "self") {
                    val name = redditPostData.name
                    val link = BASE_URL + redditPostData.permalink
                    val primary: String
                    val fallback: String
                    val isVideo: Boolean
                    when {
                        media?.reddit_video != null -> {
                            val redditVideo = media.reddit_video
                            primary = redditVideo.scrubber_media_url
                            fallback = redditVideo.fallback_url
                            isVideo = true
                        }
                        preview?.reddit_video_preview != null -> {
                            val redditVideo = preview.reddit_video_preview
                            primary = redditVideo.scrubber_media_url
                            fallback = redditVideo.fallback_url
                            isVideo = true
                        }
                        parent?.media != null -> {
                            val redditVideo = parent.media.reddit_video
                            primary = redditVideo.scrubber_media_url
                            fallback = redditVideo.fallback_url
                            isVideo = true
                        }
                        else -> {
                            primary = redditPostData.url
                            fallback = thumbnail
                            isVideo = false
                        }
                    }
                    awwImageList.add(
                        AwwImage(
                            primaryUrl = primary,
                            thumbnail = thumbnail,
                            link = link,
                            fallbackUrl = fallback,
                            title = title,
                            isVideo = isVideo,
                            name = name
                        )
                    )
                }
            } catch (ignored: Exception) {

            }
        }
        return awwImageList
    }

    suspend fun getCurrentImages(path: String) = withContext(Dispatchers.IO) {
        if (awwImages.isEmpty()) {
            fetchImages(path, null)
        } else {
            Resource.Success(awwImages)
        }
    }

    fun repoImages() = awwImages

    suspend fun getFavorites(): Resource.Success<List<AwwImage>> = withContext(Dispatchers.IO) {
        mFavorites = mImageDao.getSavedImages()
        Resource.Success(mFavorites)
    }
}