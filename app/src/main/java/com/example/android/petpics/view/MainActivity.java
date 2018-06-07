package com.example.android.petpics.view;

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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.petpics.R;
import com.example.android.petpics.model.AwwImage;
import com.example.android.petpics.viewmodel.MyViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity
        implements ImageRecyclerViewAdapter.ItemClickListener,
        SubsDialog.OnFragmentInteractionListener {

    private LiveData<List<AwwImage>> myLiveData;
    private MyViewModel myViewModel;
    private Menu optionsMenu;
    private RecyclerView mRecyclerView;
    private ImageRecyclerViewAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mPath;
    private boolean canCall = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPath();
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        final Observer<List<AwwImage>> myObserver = new Observer<List<AwwImage>>() {
            @Override
            public void onChanged(@Nullable List<AwwImage> awwImageData) {
                canCall = true;
                endRefresh();

                if (awwImageData == null || awwImageData.isEmpty()) {
                    if (myViewModel.isFavorites()) {
                        optionsMenu.findItem(R.id.action_fav).setIcon(R.drawable.ic_action_save);
                        myViewModel.switchFavorites();
                    }
                    return;
                }

                initRecyclerView(awwImageData);
            }
        };
        myLiveData =
                myViewModel.getAwwImageData(mPath);
        myLiveData.observe(this, myObserver);

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (checkConnection()) {
                    Objects.requireNonNull(myLiveData.getValue()).clear();
                    mAdapter.notifyDataSetChanged();
                    myViewModel.getFreshData(mPath);
                } else {
                    endRefresh();
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
                            myViewModel.addImages(mPath, awwImageData.get(awwImageData.size() - 1).getName());
                        }
                        canCall = false;
                        startRefresh();
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
                if (sharedPreferences.getBoolean(String.valueOf(R.id.aww), true)) {
                    checked.add(R.id.aww);
                } else {
                    unchecked.add(R.id.aww);
                }
                if (sharedPreferences.getBoolean(String.valueOf(R.id.corgi), true)) {
                    checked.add(R.id.corgi);
                } else {
                    unchecked.add(R.id.corgi);
                }
                if (sharedPreferences.getBoolean(String.valueOf(R.id.rarepuppers), true)) {
                    checked.add(R.id.rarepuppers);
                } else {
                    unchecked.add(R.id.rarepuppers);
                }
                if (sharedPreferences.getBoolean(String.valueOf(R.id.eyebleach), true)) {
                    checked.add(R.id.eyebleach);
                } else {
                    unchecked.add(R.id.eyebleach);
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
        mPath = subs.toString().replace(", ", "+")
                .replace("[", "").replace("]", "");
    }

    @Override
    public void onPreferencesSaved(List<Integer> checked, List<Integer> unchecked) {
        startRefresh();

        Objects.requireNonNull(myLiveData.getValue()).clear();
        mAdapter.notifyDataSetChanged();

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
        if (checkConnection()) {
            myViewModel.getFreshData(mPath);
        }
    }

    private void endRefresh() {
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
