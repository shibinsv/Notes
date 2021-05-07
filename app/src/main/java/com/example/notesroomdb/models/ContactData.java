package com.example.notesroomdb.models;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "contact_table")
public class ContactData implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "specificUserID")
    private int specificUserID;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "age")
    private String age;

    @ColumnInfo(name = "designation")
    private String designation;

    @ColumnInfo(name = "image")
    private String image;

    @ColumnInfo(name = "color")
    private String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public int getSpecificUserID() {
        return specificUserID;
    }

    public void setSpecificUserID(int specificUserID) {
        this.specificUserID = specificUserID;
    }
}
