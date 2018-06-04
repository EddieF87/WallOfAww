package com.example.android.petpics.view;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.android.petpics.R;
import com.example.android.petpics.model.AwwImage;
import com.example.android.petpics.model.RedditPostData;
import com.example.android.petpics.viewmodel.MyViewModel;

import java.util.List;


public class MainActivity extends AppCompatActivity
        implements ImageRecyclerViewAdapter.ItemClickListener
{

    private LiveData<List<AwwImage>> myLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setContentView(R.layout.fragment_video);

//        VideoView videoView = findViewById(R.id.vidView);
//
//        MediaController mc = new MediaController(this);
//        mc.setAnchorView(videoView);
//        mc.setMediaPlayer(videoView);
//        videoView.setMediaController(mc);
//
//        Uri uri = Uri.parse("https://v.redd.it/ngn39tu292111/DASH_9_6_M");
//        videoView.setVideoURI(uri);
//        videoView.start();

        final MyViewModel model = ViewModelProviders.of(MainActivity.this).get(MyViewModel.class);

        final Observer<List<AwwImage>> myObserver = new Observer<List<AwwImage>>() {

            @Override
            public void onChanged(@Nullable List<AwwImage> awwImageData) {

                    if (awwImageData == null) {
                        return;
                    }
                    initRecyclerView(awwImageData);
            }
        };
        myLiveData = model.getAwwImageData();
        myLiveData.observe(this, myObserver);
    }
    
    private void initRecyclerView(List<AwwImage> awwImageData){
        RecyclerView recyclerView = findViewById(R.id.rv);
//                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, numberOfColumns));
        ImageRecyclerViewAdapter adapter = new ImageRecyclerViewAdapter(awwImageData);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AwwImage data) {

        Intent intent = new Intent(MainActivity.this, ImgActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }
}
