package com.example.notesroomdb.RoomDatabase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.notesroomdb.models.MainData;
import com.example.notesroomdb.models.ContactData;

@Database(entities = {MainData.class,ContactData.class},version =44,exportSchema = false)
public abstract class RoomDB extends RoomDatabase {

    private static RoomDB database;
    private static String DATABASE_NAME ="database";
    public synchronized static RoomDB getInstance(Context context){
        if(database == null){
            database= Room.databaseBuilder(context.getApplicationContext(),
                    RoomDB.class,DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }


    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }


    @Override
    public void clearAllTables() {

    }

    public abstract Dao dao();

}
