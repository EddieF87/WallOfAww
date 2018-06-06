package com.example.android.petpics.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.android.petpics.model.AwwImage;

import java.util.List;

public class MyViewModel extends AndroidViewModel {


    private Repo mRepo;
    private MutableLiveData<List<AwwImage>> mAwwImageData;
    private boolean isFavorites;

    public MyViewModel(@NonNull Application application) {
        super(application);
        mRepo = new Repo(application);
        getDataFromRepository();
    }

    //Returns data if it already exists, otherwise gets new data from FactsRepository
    public LiveData<List<AwwImage>> getAwwImageData(){
        if(this.mAwwImageData != null) {
            return this.mAwwImageData;
        }
        return getDataFromRepository();
    }

    //Fetch and set new LiveData from Repo
    public LiveData<List<AwwImage>> getDataFromRepository() {
        this.mAwwImageData = mRepo.addLiveData(null);
        return this.mAwwImageData;
    }

    public boolean isFavorites() {
        return isFavorites;
    }

    public void switchFavorites() {
        isFavorites = !isFavorites;
    }

    //Fetch and set saved images to LiveData from Repo
    public LiveData<List<AwwImage>> toggleFavorites() {
        isFavorites = !isFavorites;
        if(isFavorites){
//            this.mAwwImageData.setValue(mRepo.getFavorites());
            return mRepo.getFavorites();
        } else {
//            this.mAwwImageData.setValue();
            return mRepo.addLiveData(null);
        }
    }

    public LiveData<List<AwwImage>> addImages(String id) {
        if(!isFavorites){
            return mRepo.addLiveData(id);
        }
        return this.mAwwImageData;
    }

//    public void insert(AwwImage awwImage) { mRepo.insert(awwImage);}

}
