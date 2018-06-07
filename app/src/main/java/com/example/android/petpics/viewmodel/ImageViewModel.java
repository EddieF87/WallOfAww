package com.example.android.petpics.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.android.petpics.model.AwwImage;

import java.util.List;

public class ImageViewModel  extends AndroidViewModel {

    private final Repo mRepo;
    private MutableLiveData<List<AwwImage>> mAwwImageData;

    public ImageViewModel(@NonNull Application application) {
        super(application);
        mRepo = new Repo(application);
    }
}
