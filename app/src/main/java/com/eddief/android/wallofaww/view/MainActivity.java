package com.eddief.android.wallofaww.view;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.eddief.android.wallofaww.R;
import com.eddief.android.wallofaww.model.AwwImage;
import com.eddief.android.wallofaww.model.Resource;
import com.eddief.android.wallofaww.viewmodel.MyViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity
        implements ImageRecyclerViewAdapter.ItemClickListener,
        SubsDialog.OnFragmentInteractionListener {

    private LiveData<Resource<List<AwwImage>>> myLiveData;
    private MyViewModel myViewModel;
    private Menu optionsMenu;
    private RecyclerView mRecyclerView;
    private ImageRecyclerViewAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mPath;
    private boolean canCall = true;
    public static final Collection<Integer> subReddits = Collections.unmodifiableList(Arrays.asList(
        R.id.aww,  R.id.rarepuppers, R.id.corgi,   R.id.eyebleach,
        R.id.kitty,  R.id.foxes, R.id.cute,   R.id.pomeranians,   R.id.cats,
        R.id.rabbits,  R.id.redpandas, R.id.dogpictures,   R.id.catpictures)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPath();
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        final Observer<Resource<List<AwwImage>>> myObserver = new Observer<Resource<List<AwwImage>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<AwwImage>> awwImageData) {
                endRefresh();

                if (awwImageData == null) {
                    return;
                }
                if(awwImageData.getError() != null) {
                    Toast.makeText(MainActivity.this, "ERROR: " + awwImageData.getError(), Toast.LENGTH_LONG).show();
                    return;
                }
                List<AwwImage> awwImages = awwImageData.getData();

                if (awwImages == null || awwImages.isEmpty()) {
                    if (myViewModel.isFavorites()) {
                        optionsMenu.findItem(R.id.action_fav).setIcon(R.drawable.ic_action_save);
                        myViewModel.switchFavorites();
                    }
                    return;
                }
                initRecyclerView(awwImages);
            }
        };
        myLiveData = myViewModel.getAwwImageData(mPath);
        myLiveData.observe(this, myObserver);

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (checkConnection() && !myViewModel.isFavorites()) {
                    Objects.requireNonNull(myLiveData.getValue()).getData().clear();
                    if(mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    myViewModel.getFreshData(mPath);
                }
            }
        });
    }

    private boolean checkConnection() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connManager != null) {
            networkInfo = connManager.getActiveNetworkInfo();
        }
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(this, "Please connect to a stable data connection!", Toast.LENGTH_LONG).show();
            endRefresh();
            return false;
        }
        return true;
    }

    private void initRecyclerView(final List<AwwImage> awwImageData) {
        if (mRecyclerView == null) {
            mRecyclerView = findViewById(R.id.rv);
            int numberOfColumns = 3;
            mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, numberOfColumns));
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!recyclerView.canScrollVertically(1)) {
                        if (awwImageData.isEmpty() || !canCall || myViewModel.isFavorites()) {
                            return;
                        }
                        if (checkConnection()) {
                            myViewModel.getDataFromRepository(mPath);
                            canCall = false;
                            startRefresh();
                        }
                    }
                }
            });
        }
        if (mAdapter == null || myViewModel.isFavorites()) {
            mAdapter = new ImageRecyclerViewAdapter(awwImageData);
            mAdapter.setClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mRecyclerView.getRecycledViewPool().clear();
            mAdapter.notifyDataSetChanged();
        }
        endRefresh();
    }

    private void toggleFavorites() {
        myViewModel.toggleFavorites(mPath);
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
        if (myViewModel != null && myViewModel.isFavorites()) {
            optionsMenu.findItem(R.id.action_fav).setIcon(R.drawable.ic_star);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_fav:
                if (myViewModel.isFavorites()) {
                    item.setIcon(R.drawable.ic_action_save);
                } else {
                    item.setIcon(R.drawable.ic_star);
                }
                toggleFavorites();
                break;
            case R.id.action_edit:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                ArrayList<Integer> checked = new ArrayList<>();
                ArrayList<Integer> unchecked = new ArrayList<>();

                for(int subreddit : subReddits) {
                    if (sharedPreferences.getBoolean(String.valueOf(subreddit), true)) {
                        checked.add(subreddit);
                    } else {
                        unchecked.add(subreddit);
                    }
                }

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                android.support.v4.app.DialogFragment newFragment = SubsDialog.newInstance(checked, unchecked);
                newFragment.show(fragmentTransaction, "");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setPath() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        List<String> subs = new ArrayList<>();
        if (sharedPreferences.getBoolean(String.valueOf(R.id.aww), true)) {
            subs.add("aww");
        }
        if (sharedPreferences.getBoolean(String.valueOf(R.id.corgi), true)) {
            subs.add("corgi");
        }
        if (sharedPreferences.getBoolean(String.valueOf(R.id.rarepuppers), true)) {
            subs.add("rarepuppers");
        }
        if (sharedPreferences.getBoolean(String.valueOf(R.id.eyebleach), true)) {
            subs.add("eyebleach");
        }
        if (sharedPreferences.getBoolean(String.valueOf(R.id.kitty), true)) {
            subs.add("kitty");
        }
        if (sharedPreferences.getBoolean(String.valueOf(R.id.foxes), true)) {
            subs.add("foxes");
        }
        if (sharedPreferences.getBoolean(String.valueOf(R.id.cute), true)) {
            subs.add("cute");
        }
        if (sharedPreferences.getBoolean(String.valueOf(R.id.pomeranians), true)) {
            subs.add("pomeranians");
        }
        if (sharedPreferences.getBoolean(String.valueOf(R.id.rabbits), true)) {
            subs.add("rabbits");
        }
        if (sharedPreferences.getBoolean(String.valueOf(R.id.redpandas), true)) {
            subs.add("redpandas");
        }
        if (sharedPreferences.getBoolean(String.valueOf(R.id.dogpictures), true)) {
            subs.add("dogpictures");
        }
        if (sharedPreferences.getBoolean(String.valueOf(R.id.catpictures), true)) {
            subs.add("catpictures");
        }
        if (sharedPreferences.getBoolean(String.valueOf(R.id.cats), true)) {
            subs.add("cats");
        }
        mPath = subs.toString().replace(", ", "+")
                .replace("[", "").replace("]", "");
    }

    @Override
    public void onPreferencesSaved(List<Integer> checked, List<Integer> unchecked) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int id : checked) {
            editor.putBoolean(String.valueOf(id), true);
        }
        for (int id : unchecked) {
            editor.putBoolean(String.valueOf(id), false);
        }
        editor.apply();
        setPath();

        if(myViewModel.isFavorites()){
            return;
        }

        startRefresh();

        Objects.requireNonNull(myLiveData.getValue()).getData().clear();
        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        if (checkConnection()) {
            myViewModel.getFreshData(mPath);
        }
    }

    private void endRefresh() {
        canCall = true;
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void startRefresh() {
        if (mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }
}
