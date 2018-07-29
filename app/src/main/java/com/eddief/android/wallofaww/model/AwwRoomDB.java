package com.eddief.android.wallofaww.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {AwwImage.class}, version = 1, exportSchema = false)
public abstract class AwwRoomDB extends RoomDatabase{

    public abstract ImageDao imageDao();
    private static AwwRoomDB INSTANCE;

    public static AwwRoomDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AwwRoomDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AwwRoomDB.class, "aww_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
