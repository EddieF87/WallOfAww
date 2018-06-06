package com.example.android.petpics.model;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {AwwImage.class}, version = 1)
public abstract class AwwRoomDB extends RoomDatabase{

    public abstract ImageDao imageDao();
    private static AwwRoomDB INSTANCE;

    public static AwwRoomDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AwwRoomDB.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AwwRoomDB.class, "aww_database")
//                            .addCallback(sRoomDatabaseCallback)
//                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
