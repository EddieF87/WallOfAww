package com.eddief.android.infiniteaww.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.eddief.android.infiniteaww.model.AwwImage

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(awwImage: AwwImage)
    //    @Query("DELETE FROM aww_table")
    //    void deleteAll();
    @Query("DELETE FROM aww_table WHERE primaryUrl LIKE :url")
    fun remove(url: String)
    @Query("SELECT * from aww_table")
    fun getSavedImages(): List<AwwImage>
    @Query("SELECT * FROM aww_table WHERE primaryUrl LIKE :url")
    fun checkIfExists(url: String?): List<AwwImage>
}