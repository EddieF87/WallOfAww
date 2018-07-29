package com.eddief.android.wallofaww.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.eddief.android.wallofaww.model.AwwImage;
import com.eddief.android.wallofaww.model.Repo;
import com.eddief.android.wallofaww.model.Resource;

import java.util.List;

public class MyViewModel extends AndroidViewModel {


    private final Repo mRepo;
    private MutableLiveData<Resource<List<AwwImage>>> mAwwImageData;
    private boolean mIsFavorites;

    public MyViewModel(@NonNull Application application) {
        super(application);
        mRepo = new Repo(application);
    }

    public LiveData<Resource<List<AwwImage>>> getAwwImageData(String path){
        if(this.mAwwImageData != null) {
            return this.mAwwImageData;
        }
        return getDataFromRepository(path);
    }

    public LiveData<Resource<List<AwwImage>>> getDataFromRepository(String path) {
        this.mAwwImageData = mRepo.addLiveData(path);
        return this.mAwwImageData;
    }

    public void getFreshData(String path) {
        if(!mIsFavorites){
            mRepo.getFreshData(path);
        }
    }

    public boolean isFavorites() {
        return mIsFavorites;
    }

    public void switchFavorites() {
        mIsFavorites = !mIsFavorites;
    }

    public void toggleFavorites(String path) {
        switchFavorites();
        if(mIsFavorites){
            mRepo.getFavorites();
        } else {
            mRepo.addLiveData(path);
        }
    }
}
