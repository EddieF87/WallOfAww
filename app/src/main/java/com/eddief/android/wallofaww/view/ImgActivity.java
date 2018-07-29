package com.eddief.android.wallofaww.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.eddief.android.wallofaww.R;
import com.eddief.android.wallofaww.model.AwwImage;
import com.eddief.android.wallofaww.model.AwwRoomDB;
import com.eddief.android.wallofaww.model.ImageDao;
//import com.eddief.android.wallofaww.viewmodel.ImageViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImgActivity extends AppCompatActivity {

    private AwwImage mAwwImage;
//    private ImageViewModel mImageViewModel;
    private VideoView mVideoView;
    private int mVideoPosition;
    private Menu imageMenu;
    private ImageDao mImageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img);

        if(savedInstanceState != null) {
            mVideoPosition = savedInstanceState.getInt("pos", 0);
        }

//        mImageViewModel = ViewModelProviders.of(this).get(ImageViewModel.class);
        AwwRoomDB db = AwwRoomDB.getDatabase(getApplication());
        this.mImageDao = db.imageDao();

        mAwwImage = getIntent().getParcelableExtra("data");

        if (mAwwImage.isVideo()) {
            String vidUrl = mAwwImage.getPrimaryUrl();
            playVideo(vidUrl);
        } else {
            String primaryUrl = mAwwImage.getPrimaryUrl();
            String fallbackUrl = mAwwImage.getFallbackUrl();
            showImage(primaryUrl, fallbackUrl);
        }


        TextView titleView = findViewById(R.id.title);
        String title = mAwwImage.getTitle();
        titleView.setText(title);
    }

    private void playVideo(String vidUrl) {

        ImageView imageView = findViewById(R.id.image_full);
        mVideoView = findViewById(R.id.vidView);
        imageView.setVisibility(View.GONE);
        mVideoView.setVisibility(View.VISIBLE);

        MediaController mc = new MediaController(this);
        mc.setAnchorView(mVideoView);
        mc.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(mc);

        Uri uri = Uri.parse(vidUrl);
        mVideoView.setVideoURI(uri);
        mVideoView.seekTo(mVideoPosition);
        mVideoView.start();
    }

    private void showImage(String primaryUrl, final String fallbackUrl) {
        final ImageView imageView = findViewById(R.id.image_full);
        Picasso.get()
                .load(primaryUrl)
                .placeholder(R.drawable.ic_paw)
                .fit()
                .centerInside()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(fallbackUrl)
                                .fit()
                                .centerInside()
                                .placeholder(R.drawable.ic_paw)
                                .error(R.drawable.ic_broken_img)
                                .into(imageView);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_img, menu);
        imageMenu = menu;
        checkIfSaved();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                sendLink();
                break;
            case R.id.action_link:
                goToLink();
                break;
            case R.id.action_save:
                new checkExistenceAsyncTask(mImageDao).execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendLink() {
        String link = mAwwImage.getLink();
        Intent msgIntent = new Intent(Intent.ACTION_SEND);
        msgIntent.setType("text/plain");
        msgIntent.putExtra(Intent.EXTRA_TEXT, link);
        startActivity(Intent.createChooser(msgIntent, "Share with friends!"));
    }

    private void goToLink() {
        String link = mAwwImage.getLink();
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    private void updateSaveStatus(boolean exists) {
        if(exists) {
            removeAwwImage();
        } else {
            saveAwwImage();
        }
        updateSaveIcon(!exists);
    }

    private void saveAwwImage() {
        new insertAsyncTask(mImageDao).execute(mAwwImage);
    }

    private void removeAwwImage() {
        new deleteAsyncTask(mImageDao).execute(mAwwImage);
    }

    private void checkIfSaved(){
        new setIconAsyncTask(mImageDao).execute();
    }

    private void updateSaveIcon(boolean saved) {
        if(saved){
            imageMenu.findItem(R.id.action_save).setIcon(R.drawable.ic_star);
        } else {
            imageMenu.findItem(R.id.action_save).setIcon(R.drawable.ic_action_save);
        }
    }

    private static class insertAsyncTask extends android.os.AsyncTask<AwwImage, Void, Void> {

        private final ImageDao mAsyncTaskDao;

        insertAsyncTask(ImageDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AwwImage... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends android.os.AsyncTask<AwwImage, Void, Void> {

        private final ImageDao mAsyncTaskDao;

        deleteAsyncTask(ImageDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final AwwImage... params) {
            mAsyncTaskDao.remove(params[0].getPrimaryUrl());
            return null;
        }
    }

    private class setIconAsyncTask extends android.os.AsyncTask<Void, Void, List<AwwImage>> {

        private final ImageDao mAsyncTaskDao;

        setIconAsyncTask(ImageDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<AwwImage> doInBackground(Void... voids) {
            return mAsyncTaskDao.checkIfExists(mAwwImage.getPrimaryUrl());
        }

        @Override
        protected void onPostExecute(List<AwwImage> list) {
            if(list == null || list.isEmpty()) {
                updateSaveIcon(false);
            } else {
                updateSaveIcon(true);
            }
        }
    }

    private class checkExistenceAsyncTask extends android.os.AsyncTask<Void, Void, List<AwwImage>> {

        private final ImageDao mAsyncTaskDao;

        checkExistenceAsyncTask(ImageDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<AwwImage> doInBackground(Void... voids) {
            return mAsyncTaskDao.checkIfExists(mAwwImage.getPrimaryUrl());
        }

        @Override
        protected void onPostExecute(List<AwwImage> list) {
            if(list == null || list.isEmpty()) {
                updateSaveStatus(false);
            } else {
                updateSaveStatus(true);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mVideoView != null) {
            mVideoView.pause();
            mVideoPosition = mVideoView.getCurrentPosition();
            outState.putInt("pos", mVideoPosition);
        }
    }
}
