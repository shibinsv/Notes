package com.example.notesroomdb.RoomDatabase;


import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.notesroomdb.models.ContactData;
import com.example.notesroomdb.models.MainData;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@androidx.room.Dao
public interface Dao {

    //Contacts DAO

    @Insert(onConflict = REPLACE)
    void insertContact(ContactData...contactData);

    @Delete
    void deleteContact(ContactData contactData);

    @Delete
    void resetAllContacts(List<ContactData> contactData,List<MainData> mainData);

    @Query("UPDATE contact_table SET name =:sName , age =:sAge , designation =:sDesignation   WHERE specificUserID =:sID")
    void updateContacts(int sID,String sName,String sAge,String sDesignation);

    @Query("SELECT *FROM contact_table")
    List<ContactData>getAllContact();



    //Lists of notes

    @Insert(onConflict = REPLACE)
    void insert(MainData mainData);

    @Delete
    void delete(MainData mainData);

    @Query("UPDATE notes_table SET title =:sTitle,description =:sDescription,date =:sDate WHERE ID =:sID")
    void update(int sID,String sTitle,String sDescription,String sDate);

    @Query("SELECT *FROM notes_table")
    List<MainData>getAll();

   @Query("SELECT *FROM notes_table WHERE userID=:sID")
    List<MainData>loadUser(int sID);
}
