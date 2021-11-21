package com.eddief.android.infiniteaww.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.eddief.android.infiniteaww.model.AwwImage

@Database(entities = [AwwImage::class], version = 1, exportSchema = false)
abstract class AwwRoomDB : RoomDatabase() {
    abstract fun imageDao(): ImageDao

    companion object {
        private var INSTANCE: AwwRoomDB? = null
        fun getDatabase(context: Context): AwwRoomDB {
            if (INSTANCE == null) {
                synchronized(AwwRoomDB::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AwwRoomDB::class.java, "aww_database"
                        ).build()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}