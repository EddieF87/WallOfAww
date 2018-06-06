package com.example.android.petpics.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.android.petpics.model.AwwImage;

import java.util.List;

public class ImageViewModel  extends AndroidViewModel {


    private Repo mRepo;
    private MutableLiveData<List<AwwImage>> mAwwImageData;

    public ImageViewModel(@NonNull Application application) {
        super(application);
        mRepo = new Repo(application);
//        getDataFromRepository();
    }

//    //Returns data if it already exists, otherwise gets new data from FactsRepository
//    public LiveData<List<AwwImage>> getAwwImageData(){
//        if(this.mAwwImageData != null) {
//            return mAwwImageData;
//        }
//        return getDataFromRepository();
//    }
//
//    //Fetch and set new LiveData from Repo
//    public LiveData<List<AwwImage>> getDataFromRepository() {
//        this.mAwwImageData = mRepo.getLiveData();
//        return this.mAwwImageData;
//    }

//    public void insert(AwwImage awwImage) { mRepo.insert(awwImage);}

}
