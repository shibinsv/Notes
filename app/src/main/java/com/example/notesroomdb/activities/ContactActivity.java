package com.example.notesroomdb.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesroomdb.R;
import com.example.notesroomdb.RoomDatabase.RoomDB;
import com.example.notesroomdb.adapters.ContactAdapter;
import com.example.notesroomdb.models.ContactData;
import com.example.notesroomdb.models.MainData;
import com.example.notesroomdb.utils.AppUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactActivity extends BaseActivity {

    @BindView(R.id.recyclerContacts)
    RecyclerView recyclerContacts;
    @BindView(R.id.ll_container)
    LinearLayout llContainer;

    private Activity cActivity;
    private ContactAdapter contactAdapter;
    List<ContactData> contactList = new ArrayList<>();
    List<MainData> mainDataList = new ArrayList<>();
    RoomDB database;
    String options[];
    private String selectedImagePath;
    Dialog dialog;
    private int clickedPosition = -1;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        cActivity = this;
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        database = RoomDB.getInstance(this);
        contactList = database.dao().getAllContact();
        LinearLayoutManager layoutManager = new LinearLayoutManager(ContactActivity.this,
                LinearLayoutManager.VERTICAL, false);
        recyclerContacts.setLayoutManager(layoutManager);
        contactAdapter = new ContactAdapter(this, contactList);
        recyclerContacts.setAdapter(contactAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addContact:
                dialog = new Dialog(this);
                dialog.setContentView(R.layout.add_contact);
                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width, height);
                dialog.show();

                EditText nameAdd = dialog.findViewById(R.id.nameUpdate);
                EditText ageAdd = dialog.findViewById(R.id.ageUpdate);
                EditText designationAdd = dialog.findViewById(R.id.desUpdate);
                CircleImageView addImage = dialog.findViewById(R.id.addImage);
                Button ok = dialog.findViewById(R.id.btnSaveContact);

                addImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(
                                getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                    ContactActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_CODE_STORAGE_PERMISSION
                            );
                        } else {
                            selectImage();
                        }
                    }});

                Dialog successDialog = new Dialog(ContactActivity.this);
                successDialog.setContentView(R.layout.added_contact);
                Button okCbtn = successDialog.findViewById(R.id.okCbtn);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String sNewName = nameAdd.getText().toString().trim();
                        String sNewAge = ageAdd.getText().toString().trim();
                        String sNewDes = designationAdd.getText().toString().trim();
                        ContactData data = new ContactData();
                        int sID = data.getSpecificUserID();

                        if (Integer.parseInt(sNewAge) >= 110 || Integer.parseInt(sNewAge) <= 18) {
                            ageAdd.setCursorVisible(true);
                            dialog.dismiss();
                            AppUtils.getInstance().showSnackbar(cActivity, "Age limit is 18-110.", llContainer);

                        } else if (!sNewName.equals("") && !sNewAge.equals("") && !sNewDes.equals("")) {
                            successDialog.show();
                            data.setName(sNewName);
                            data.setAge(sNewAge);
                            data.setDesignation(sNewDes);
                            data.setImage(selectedImagePath);
                            database.dao().insertContact(data);
                            contactList.clear();
                            mainDataList.clear();
                            contactList.addAll(database.dao().getAllContact());

                            okCbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    successDialog.dismiss();
                                    contactAdapter = new ContactAdapter((ContactActivity) cActivity, contactList);
                                    recyclerContacts.setAdapter(contactAdapter);
                                    contactAdapter.notifyDataSetChanged();
                                }
                            });
                            dialog.dismiss();
                            AppUtils.getInstance().showSnackbar(cActivity, "Data added successfully", llContainer);
                        } else {
                            dialog.dismiss();
                            AppUtils.getInstance().showSnackbar(cActivity, "Fields are missing", llContainer);
                        }
                    }
                });
                break;
            case R.id.clearContacts:
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                builder.setTitle("Do you want to clear all contacts?");
                options = new String[]{"Yes", "Cancel"};
                builder.setItems(options, (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            database.dao().resetAllContacts(contactList, mainDataList);
                            contactList.clear();
                            mainDataList.clear();
                            contactAdapter.notifyItemChanged(clickedPosition,contactList.get(clickedPosition));
                            contactAdapter.notifyItemRemoved(clickedPosition);
                        case 1:
                            dialogInterface.dismiss();
                            break;
                    }
                });
                androidx.appcompat.app.AlertDialog clearDialog = builder.create();
                clearDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void viewHistory(Integer sID) {
        Intent intent = new Intent(ContactActivity.this, MainActivity.class);
        intent.putExtra("sID", sID);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE_SELECT_IMAGE &&resultCode == RESULT_OK)
            if (data != null){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        CircleImageView addImage;
                        addImage=dialog.findViewById(R.id.addImage);
                        addImage.setImageBitmap(bitmap);
                        addImage.setVisibility(View.VISIBLE);
                        selectedImagePath = getPathFromUri(selectedImageUri);
                    }catch (Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }
}