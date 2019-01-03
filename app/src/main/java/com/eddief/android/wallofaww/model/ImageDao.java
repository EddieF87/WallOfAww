package com.eddief.android.wallofaww.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AwwImage awwImage);

//    @Query("DELETE FROM aww_table")
//    void deleteAll();

    @Query("DELETE FROM aww_table WHERE primaryUrl LIKE :url")
    void remove(String url);

    @Query("SELECT * from aww_table")
    List<AwwImage> getSavedImages();

    @Query("SELECT * FROM aww_table WHERE primaryUrl LIKE :url")
    List<AwwImage> checkIfExists(String url);
}
