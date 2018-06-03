package com.example.android.petpics.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.petpics.model.RedditPostData;
import com.example.android.petpics.model.Repo;

import java.util.List;

public class MyViewModel extends ViewModel {


    private Repo mRepo;
    private MutableLiveData<List<RedditPostData>> mRedditPostData;

    //Returns data if it already exists, otherwise gets new data from FactsRepository
    public LiveData<List<RedditPostData>> getRedditPostData(){
        if(this.mRedditPostData != null) {
            return mRedditPostData;
        }
        return getDataFromRepository();
    }

    //Fetch and set new LiveData from FactsRepository
    public LiveData<List<RedditPostData>> getDataFromRepository() {
        if(mRepo == null) {
            mRepo = new Repo();
        }
        this.mRedditPostData = mRepo.getLiveData();
        return this.mRedditPostData;
    }
}
