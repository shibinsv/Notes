package com.example.notesroomdb;

import android.app.Application;


public class MyApplication extends Application {

    private static MyApplication mApplicationInstance;


    public static MyApplication getInstance() {
        return mApplicationInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mApplicationInstance = this;

    }
}
