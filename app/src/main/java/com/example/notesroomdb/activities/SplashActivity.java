package com.example.notesroomdb.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.notesroomdb.R;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //For delay in displaying next screen
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                        startActivity(new Intent(SplashActivity.this, ContactActivity.class)),
                3000);
    }
}