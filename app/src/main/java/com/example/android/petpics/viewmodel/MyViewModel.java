package com.example.android.petpics.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.petpics.model.AwwImage;
import com.example.android.petpics.model.RedditPostData;
import com.example.android.petpics.model.Repo;

import java.util.List;

public class MyViewModel extends ViewModel {


    private Repo mRepo;
    private MutableLiveData<List<AwwImage>> mAwwImageData;

    //Returns data if it already exists, otherwise gets new data from FactsRepository
    public LiveData<List<AwwImage>> getAwwImageData(){
        if(this.mAwwImageData != null) {
            return mAwwImageData;
        }
        return getDataFromRepository();
    }

    //Fetch and set new LiveData from FactsRepository
    public LiveData<List<AwwImage>> getDataFromRepository() {
        if(mRepo == null) {
            mRepo = new Repo();
        }
        this.mAwwImageData = mRepo.getLiveData();
        return this.mAwwImageData;
    }
}
