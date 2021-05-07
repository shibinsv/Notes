package com.example.notesroomdb.activities;


import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesroomdb.R;
import com.example.notesroomdb.RoomDatabase.RoomDB;
import com.example.notesroomdb.adapters.MainAdapter;
import com.example.notesroomdb.models.MainData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {


    @BindView(R.id.recyclerNotes)
    RecyclerView recyclerNotes;
    @BindView(R.id.ll_container)
    LinearLayout llContainer;
    private String selectedNoteColor;

    private Activity mActivity;
    MainAdapter adapter;
    List<MainData> list = new ArrayList<>();
    RoomDB database;
    String options[];
    int sID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getIntentFromID();
        mActivity = this;
        ButterKnife.bind(this);
        initView();
    }
    private void getIntentFromID() {
        sID = getIntent().getIntExtra("sID", sID);
    }

    private void initView() {
        database = RoomDB.getInstance(this);
        MainData data =new MainData();
        data.setUserID(sID);
        data.setColor(selectedNoteColor);
        list = database.dao().loadUser(sID);
    /*    LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this,
                LinearLayoutManager.VERTICAL, false);*/
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        recyclerNotes.setLayoutManager(layoutManager);
        adapter = new MainAdapter(mActivity, list);
        recyclerNotes.setAdapter(adapter);
    }
}