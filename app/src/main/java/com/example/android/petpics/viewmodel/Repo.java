package com.example.android.petpics.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.android.petpics.model.AwwImage;
import com.example.android.petpics.model.AwwRoomDB;
import com.example.android.petpics.model.ImageDao;
import com.example.android.petpics.model.RedditAPI;
import com.example.android.petpics.model.RedditPOJO;
import com.example.android.petpics.model.RedditPostData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repo {

    private static final String BASE_URL = "https://www.reddit.com";
    private final ImageDao mImageDao;
    private List<AwwImage> mFavorites;
    private MutableLiveData<List<AwwImage>> mImages;

    Repo(Application application) {
        AwwRoomDB db = AwwRoomDB.getDatabase(application);
        this.mImageDao = db.imageDao();
    }

    public MutableLiveData<List<AwwImage>> addLiveData(String path, String id) {

        final List<AwwImage> awwImages;

        if(mImages == null) {
            mImages = new MutableLiveData<>();
            awwImages = new ArrayList<>();
        } else {
            awwImages = mImages.getValue();
        }
        return getmImages(awwImages, path, id);
    }

    private MutableLiveData<List<AwwImage>> getmImages(final List<AwwImage> awwImages, String path, String id) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        Call<RedditPOJO> redditCall
                = retrofit.create(RedditAPI.class).retrieveData(path, id);

        redditCall.enqueue(new Callback<RedditPOJO>() {
            @Override
            public void onResponse(@NonNull Call<RedditPOJO> call, @NonNull Response<RedditPOJO> response) {
                try {
                    @SuppressWarnings("ConstantConditions") RedditPOJO.RedditPost[] redditPosts = response.body().getData().getChildren();
                    List<AwwImage> newAwwImageList = getList(redditPosts);
                    awwImages.addAll(newAwwImageList);
                    mImages.postValue(awwImages);
                }
                catch (Exception ignored){}
            }

            @Override
            public void onFailure(@NonNull Call<RedditPOJO> call, @NonNull Throwable t) {
            }
        });
        return mImages;
    }

    private List<AwwImage> getList(RedditPOJO.RedditPost[] redditPosts) {
        List<AwwImage> awwImageList = new ArrayList<>();
        for(RedditPOJO.RedditPost redditPost : redditPosts) {
            try {
                RedditPostData redditPostData = redditPost.getData();
                RedditPostData.RedditMedia media = redditPostData.getMedia();
                RedditPostData.RedditPreview preview = redditPostData.getPreview();
                RedditPostData parent = redditPostData.getCrosspost_parent_list();

                String title = redditPostData.getTitle();
                String thumbnail = redditPostData.getThumbnail();
                if(thumbnail.equals("self")){
                    continue;
                }
                String name = redditPostData.getName();
                String link = BASE_URL + redditPostData.getPermalink();
                String primary;
                String fallback;
                boolean isVideo;

                if(media != null) {
                    RedditPostData.RedditVideo redditVideo = media.getReddit_video();
                    if(redditVideo != null) {
                        primary = redditVideo.getScrubber_media_url();
                        fallback = redditVideo.getFallback_url();
                        isVideo = true;
                    } else {
                        continue;
                    }
                }
                else if (preview != null && preview.getReddit_video_preview() != null) {
                    RedditPostData.RedditVideo redditVideo = preview.getReddit_video_preview();
                    primary = redditVideo.getScrubber_media_url();
                    fallback = redditVideo.getFallback_url();
                    isVideo = true;
                }
                else if (parent != null && parent.getMedia() != null) {
                    RedditPostData.RedditMedia parentMedia = parent.getMedia();
                    if(parentMedia != null) {
                        RedditPostData.RedditVideo redditVideo = parentMedia.getReddit_video();
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

    public void getFreshData(String path) {
        List<AwwImage> awwImages = mImages.getValue();
        if(awwImages == null) {
            awwImages = new ArrayList<>();
        } else {
            awwImages.clear();
        }
        getmImages(awwImages, path, null);
    }

    public void getFavorites() {
        new queryAsyncTask(mImageDao).execute();
    }

    private class queryAsyncTask extends AsyncTask<Void, Void, Void> {

        private final ImageDao mAsyncTaskDao;

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
