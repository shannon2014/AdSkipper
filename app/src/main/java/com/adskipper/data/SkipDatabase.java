package com.adskipper.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SkipRecord.class, AppPreference.class}, version = 1, exportSchema = false)
public abstract class SkipDatabase extends RoomDatabase {
    
    private static SkipDatabase instance;
    
    public abstract SkipRecordDao skipRecordDao();
    public abstract AppPreferenceDao appPreferenceDao();
    
    public static synchronized SkipDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                SkipDatabase.class,
                "skip_database"
            )
            .fallbackToDestructiveMigration()
            .build();
        }
        return instance;
    }
}
