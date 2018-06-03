package com.example.android.petpics.view;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.android.petpics.R;
import com.example.android.petpics.model.RedditMedia;
import com.example.android.petpics.model.RedditParent;
import com.example.android.petpics.model.RedditPostData;
import com.example.android.petpics.model.RedditPreview;
import com.example.android.petpics.model.RedditVideo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.zip.Inflater;

public class ImgActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img);

        final RedditPostData data = getIntent().getParcelableExtra("data");
        RedditMedia media = data.getMedia();
        RedditPreview preview = data.getPreview();
//        RedditPostData parent = data.getCrosspost_parent_list();

        if(media != null) {
            Log.d("jakk", "media != null");
            playVideo(media.getReddit_video());
        } else if (preview != null && preview.getReddit_video_preview() != null) {
            Log.d("jakk", "preview != null");
            playVideo(preview.getReddit_video_preview());

//        }
//        else if (parent != null && parent.getMedia() != null) {
//            Log.d("jakk", "parent != null");
//            RedditMedia parentMedia = parent.getMedia();
//            if(parentMedia != null) {
//                Log.d("jakk", "parentmedia != null");
//                playVideo(parentMedia.getReddit_video());
//            }
        } else {
            final ImageView imageView = findViewById(R.id.image_full);
            final String imageUrl = data.getUrl();
            Log.d("jakk", "image" + imageUrl);
            Picasso.get().load(imageUrl).into(imageView, new Callback() {
                @Override
                public void onSuccess() { }

                @Override
                public void onError(Exception e) {
                    String errorUrl = data.getThumbnail();
                    Picasso.get().load(errorUrl).into(imageView);
                }
            });
        }

//        Glide.with(this).load(imageUrl)
//                .into(imageView);
        TextView titleView = findViewById(R.id.title);
        String title = data.getTitle();
        titleView.setText(title);
    }

    private void playVideo(RedditVideo video) {
        if(video != null){
            String vidUrl = video.getScrubber_media_url();
            if(vidUrl != null) {
                ImageView imageView = findViewById(R.id.image_full);
                VideoView videoView = findViewById(R.id.vidView);
                imageView.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                Log.d("jakk", vidUrl);

                MediaController mc = new MediaController(this);
                mc.setAnchorView(videoView);
                mc.setMediaPlayer(videoView);
                videoView.setMediaController(mc);

                Uri uri = Uri.parse(vidUrl);
                videoView.setVideoURI(uri);
                videoView.start();
            }
        }
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
}
