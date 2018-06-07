package com.example.android.petpics.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.android.petpics.model.AwwImage;

import java.util.List;

public class MyViewModel extends AndroidViewModel {


    private final Repo mRepo;
    private MutableLiveData<List<AwwImage>> mAwwImageData;
    private boolean isFavorites;

    public MyViewModel(@NonNull Application application) {
        super(application);
        mRepo = new Repo(application);
    }

    public LiveData<List<AwwImage>> getAwwImageData(String path){
        if(this.mAwwImageData != null) {
            return this.mAwwImageData;
        }
        return getDataFromRepository(path);
    }

    private LiveData<List<AwwImage>> getDataFromRepository(String path) {
        this.mAwwImageData = mRepo.addLiveData(path,null);
        return this.mAwwImageData;
    }

    public boolean isFavorites() {
        return isFavorites;
    }

    public void switchFavorites() {
        isFavorites = !isFavorites;
    }

    public void toggleFavorites(String path) {
        isFavorites = !isFavorites;
        if(isFavorites){
            mRepo.getFavorites();
        } else {
            mRepo.addLiveData(path, null);
        }
    }

    public void addImages(String path, String id) {
        mRepo.addLiveData(path, id);
    }

    public void getFreshData(String path) {
        if(!isFavorites){
            mRepo.getFreshData(path);
        }
    }
}
