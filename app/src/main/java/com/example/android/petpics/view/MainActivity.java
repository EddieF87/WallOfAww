package com.example.android.petpics.view;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.petpics.R;
import com.example.android.petpics.model.AwwImage;
import com.example.android.petpics.viewmodel.MyViewModel;

import java.util.List;


public class MainActivity extends AppCompatActivity
        implements ImageRecyclerViewAdapter.ItemClickListener
{

    private LiveData<List<AwwImage>> myLiveData;
    private MyViewModel myViewModel;
    private Menu optionsMenu;
    private RecyclerView mRecyclerView;
    private ImageRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("poop", "onCreate");

        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        final Observer<List<AwwImage>> myObserver = new Observer<List<AwwImage>>() {
            @Override
            public void onChanged(@Nullable List<AwwImage> awwImageData) {
                Log.d("poop", "onChanged");

                if (awwImageData == null || awwImageData.isEmpty()) {
                        if(myViewModel.isFavorites()) {
                            optionsMenu.findItem(R.id.action_fav).setIcon(R.drawable.ic_action_save);
                            myViewModel.switchFavorites();
//                            Toast.makeText(MainActivity.this, "gggetfavs", Toast.LENGTH_SHORT).show();
                        }
                    Log.d("poop", "awwImageData == null || awwImageData.isEmpty(");
                    return;
                }

                initRecyclerView(awwImageData);
            }
        };
        myLiveData = myViewModel.getAwwImageData();
        myLiveData.observe(this, myObserver);
    }
    
    private void initRecyclerView(final List<AwwImage> awwImageData){
        if(mRecyclerView == null) {
            mRecyclerView = findViewById(R.id.rv);
            int numberOfColumns = 3;
            mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, numberOfColumns));
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!recyclerView.canScrollVertically(1)) {
                        Log.d("poop", "last");
                        if(awwImageData.isEmpty()) {
                            Log.d("poop", "awwImageData.isEmpty");
                            return;
                        }
                        Log.d("poop", "addImages");
                        myLiveData = myViewModel.addImages(awwImageData.get(awwImageData.size()-1).getName());
                    }
                }
            });
        }
//        mAdapter = new ImageRecyclerViewAdapter(awwImageData);
//        mAdapter.setClickListener(this);
//        mRecyclerView.setAdapter(mAdapter);
        if(mAdapter == null || myViewModel.isFavorites()) {
            mAdapter = new ImageRecyclerViewAdapter(awwImageData);
            mAdapter.setClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void toggleFavorites() {
        myLiveData = myViewModel.toggleFavorites();
    }

    @Override
    public void onItemClick(AwwImage data) {

        Intent intent = new Intent(MainActivity.this, ImgActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        optionsMenu = menu;
        if(myViewModel != null && myViewModel.isFavorites()){
            optionsMenu.findItem(R.id.action_fav).setIcon(R.drawable.ic_star);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_fav:
                if(myViewModel.isFavorites()) {
                    item.setIcon(R.drawable.ic_action_save);
                } else {
                    item.setIcon(R.drawable.ic_star);
                }
                toggleFavorites();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
