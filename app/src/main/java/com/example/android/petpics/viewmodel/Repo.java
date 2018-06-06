package com.example.android.petpics.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.android.petpics.R;
import com.example.android.petpics.model.AwwImage;
import com.example.android.petpics.model.AwwRoomDB;
import com.example.android.petpics.model.ImageDao;
import com.example.android.petpics.model.RedditAPI;
import com.example.android.petpics.model.RedditMedia;
import com.example.android.petpics.model.RedditPOJO;
import com.example.android.petpics.model.RedditPostData;
import com.example.android.petpics.model.RedditPreview;
import com.example.android.petpics.model.RedditVideo;
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
    private static final String BASE_URL = "https://www.reddit.com";
    private ImageDao mImageDao;
    private List<AwwImage> mFavorites;
    private MutableLiveData<List<AwwImage>> mImages;


    Repo(Application application) {
        AwwRoomDB db = AwwRoomDB.getDatabase(application);
        this.mImageDao = db.imageDao();
    }

    public MutableLiveData<List<AwwImage>> addLiveData(String id) {

        final List<AwwImage> awwImages;

        if(mImages == null) {
            mImages = new MutableLiveData<>();
            awwImages = new ArrayList<>();
        } else {
            awwImages = mImages.getValue();
        }

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        String callUrl;
        if(id == null) {
            callUrl = BASE_URL + "/r/aww+rarepuppers+corgi/.json?limit=30";
        } else {
            callUrl = BASE_URL + "/r/aww+rarepuppers+corgi/.json?limit=30&after=" + id;
        }
        Log.d("poop", "callUrl = " + callUrl);

        Call<RedditPOJO> redditCall
                = retrofit.create(RedditAPI.class).retrieveData(callUrl);

        redditCall.enqueue(new Callback<RedditPOJO>() {
            @Override
            public void onResponse(Call<RedditPOJO> call, Response<RedditPOJO> response) {
                RedditPOJO.RedditPost[] redditPosts = response.body().getData().getChildren();
                List<AwwImage> newAwwImageList = getList(redditPosts);
                awwImages.addAll(newAwwImageList);
                mImages.postValue(awwImages);
            }

            @Override
            public void onFailure(Call<RedditPOJO> call, Throwable t) {
                Log.d("pusha", "FAILLL" + t.toString());
            }
        });
        return mImages;
    }

    private List<AwwImage> getList(RedditPOJO.RedditPost[] redditPosts) {
        List<AwwImage> awwImageList = new ArrayList<>();
        for(RedditPOJO.RedditPost redditPost : redditPosts) {
            try {
                RedditPostData redditPostData = redditPost.getData();
                RedditMedia media = redditPostData.getMedia();
                RedditPreview preview = redditPostData.getPreview();
                RedditPostData parent = redditPostData.getCrosspost_parent_list();

                String title = redditPostData.getTitle();
                String thumbnail = redditPostData.getThumbnail();
                if(thumbnail==null){
                    continue;
                }
                String name = redditPostData.getName();
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
                AwwImage awwImage = new AwwImage(primary, fallback, title, thumbnail, link, isVideo, name);

                awwImageList.add(awwImage);
            } catch (Exception ignored) {
            }
        }
        return awwImageList;
    }

    public MutableLiveData<List<AwwImage>> getFavorites() {
        new queryAsyncTask(mImageDao).execute();
        return mImages;
    }

    private class queryAsyncTask extends AsyncTask<Void, Void, Void> {

        private ImageDao mAsyncTaskDao;

        queryAsyncTask(ImageDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mFavorites = mAsyncTaskDao.getSavedImages();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mImages.setValue(mFavorites);
        }
    }
}
