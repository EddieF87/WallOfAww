package com.example.android.petpics.view;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.android.petpics.R;
import com.example.android.petpics.model.AwwImage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImgActivity extends AppCompatActivity {

    private AwwImage mAwwImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img);

        mAwwImage = getIntent().getParcelableExtra("data");

        if (mAwwImage.isVideo()) {
            String vidUrl = mAwwImage.getPrimaryUrl();
            if (vidUrl != null) {
                playVideo(vidUrl);
            } else {
                vidUrl = mAwwImage.getFallbackUrl();
                playVideo(vidUrl);
            }
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
        VideoView videoView = findViewById(R.id.vidView);
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);

        MediaController mc = new MediaController(this);
        mc.setAnchorView(videoView);
        mc.setMediaPlayer(videoView);
        videoView.setMediaController(mc);

        Uri uri = Uri.parse(vidUrl);
        videoView.setVideoURI(uri);
        videoView.start();
    }

    private void showImage(String primaryUrl, final String fallbackUrl) {
        final ImageView imageView = findViewById(R.id.image_full);
        Picasso.get()
                .load(primaryUrl)
                .placeholder(R.drawable.ic_action_save)
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
                                .placeholder(R.drawable.ic_action_link)
                                .error(R.drawable.ic_broken_img)
                                .into(imageView);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_img, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                sendLink(mAwwImage.getLink());
                break;
            case R.id.action_link:
                goToLink(mAwwImage.getLink());
                break;
            case R.id.action_save:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendLink(String link) {
        Intent msgIntent = new Intent(Intent.ACTION_SEND);
        msgIntent.setType("text/plain");
        msgIntent.putExtra(Intent.EXTRA_TEXT, link);
        startActivity(Intent.createChooser(msgIntent, "Share with friends!"));
    }

    private void goToLink(String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }
}
