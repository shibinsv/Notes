package com.example.notesroomdb.adapters;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesroomdb.R;
import com.example.notesroomdb.RoomDatabase.RoomDB;
import com.example.notesroomdb.activities.ContactActivity;
import com.example.notesroomdb.models.ContactData;
import com.example.notesroomdb.models.MainData;
import com.example.notesroomdb.utils.AppUtils;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    ContactActivity contactActivity;
    private List<ContactData> list;
    private List<MainData> mainDataList;
    private int lastPosition=-1;
    String options[];
    private String selectedNoteColor;
    private View viewSubtitleIndicator;
    private RoomDB database;
    private MainData alreadyAvailableNote;

    private static final int REQUEST_CODE_SELECT_EDIT_IMAGE=4;


    public ContactAdapter(ContactActivity contactActivity, List<ContactData> list) {
        this.contactActivity = contactActivity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_contact_adapter, parent, false);
        return new ViewHolder(itemView);    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        database=RoomDB.getInstance(contactActivity);
        holder.setNote(list.get(position));

        holder.deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(contactActivity);
                builder.setTitle("Are you sure you want to delete this contact?");
                options = new String[]{"Yes", "Cancel"};
                builder.setItems(options, (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            ContactData c=list.get(holder.getAdapterPosition());
                            database.dao().deleteContact(c);
                            int position=holder.getAdapterPosition();
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position,list.size());
                        case 1:
                            dialogInterface.dismiss();
                            break;
                    }
                });
                androidx.appcompat.app.AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.nameTV)
        TextView nameTV;
        @BindView(R.id.ageTV)
        TextView ageTV;
        @BindView(R.id.designationTV)
        TextView designationTV;
        @BindView(R.id.editIV)
        ImageView editIV;
        @BindView(R.id.deleteIV)
        ImageView deleteIV;
        @BindView(R.id.rootContact)
        ConstraintLayout rootContact;
        @BindView(R.id.addNotes)
        ImageView addNotes;
        @BindView(R.id.addedImage)
        CircleImageView addedImage;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            nameTV=itemView.findViewById(R.id.nameTV);
            ageTV=itemView.findViewById(R.id.ageTV);
            designationTV=itemView.findViewById(R.id.designationTV);
            editIV=itemView.findViewById(R.id.editIV);
            deleteIV=itemView.findViewById(R.id.deleteIV);
            rootContact=itemView.findViewById(R.id.rootContact);
            addNotes=itemView.findViewById(R.id.addNotes);
            addedImage=itemView.findViewById(R.id.addedImage);

        }

        public void setNote(ContactData contactData) {

            nameTV.setText(contactData.getName());
            ageTV.setText(contactData.getAge());
            designationTV.setText(contactData.getDesignation());


            if (contactData.getImage() != null) {
                addedImage.setImageBitmap(BitmapFactory.decodeFile(contactData.getImage()));
            }
            editIV.setOnClickListener(v -> {
                int sID = contactData.getSpecificUserID();
                String sName = contactData.getName();
                String sAge = contactData.getAge();
                String sDes = contactData.getDesignation();
                String sImage=contactData.getImage();

                Dialog dialog = new Dialog(contactActivity);
                dialog.setContentView(R.layout.edit_contact);
                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width, height);
                dialog.show();

                EditText editName = dialog.findViewById(R.id.nameUpdate);
                EditText editAge = dialog.findViewById(R.id.ageUpdate);
                EditText editDesignation = dialog.findViewById(R.id.desUpdate);

                Button btnSaveContact = dialog.findViewById(R.id.btnSaveContact);

                editName.setText(sName);
                editAge.setText(sAge);
                editDesignation.setText(sDes);


                btnSaveContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newName =editName.getText().toString().trim();
                        String newAge =editAge.getText().toString().trim();
                        String newDesignation =editDesignation.getText().toString().trim();

                        editName.setText(newName);
                        editAge.setText(newAge);
                        editDesignation.setText(newDesignation);

                        database.dao().updateContacts(sID,newName,newAge,newDesignation);
                        list.clear();
                        list.addAll(database.dao().getAllContact());
                        notifyDataSetChanged();
                        dialog.dismiss();

                    }
                });
            });

            rootContact.setOnClickListener(v -> {
                int sID =contactData.getSpecificUserID();
                contactActivity.viewHistory(sID);
            });

            addNotes.setOnClickListener(v -> {
                Dialog dialog = new Dialog(contactActivity);
                dialog.setContentView(R.layout.add_dialog);
                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width, height);
                dialog.show();

                Button btnSave = dialog.findViewById(R.id.btnSave);
                EditText titleAdd = dialog.findViewById(R.id.titleAdd);
                EditText descAdd = dialog.findViewById(R.id.descAdd);
                TextView dateAdd = dialog.findViewById(R.id.dateAdd);
                LinearLayout addNoteLayout=dialog.findViewById(R.id.addNoteLayout);
                LinearLayout bottomTaskLayout=dialog.findViewById(R.id.layout_bottom_notes);
                viewSubtitleIndicator = dialog.findViewById(R.id.viewSubtitleIndicator);


                final ImageView imageColor1 = bottomTaskLayout.findViewById(R.id.imageColor1);
                final ImageView imageColor2 = bottomTaskLayout.findViewById(R.id.imageColor2);
                final ImageView imageColor3 = bottomTaskLayout.findViewById(R.id.imageColor3);
                final ImageView imageColor4 = bottomTaskLayout.findViewById(R.id.imageColor4);
                final ImageView imageColor5 = bottomTaskLayout.findViewById(R.id.imageColor5);

                //white
                bottomTaskLayout.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedNoteColor = "#ffffff";
                        imageColor1.setImageResource(R.drawable.ic_done);
                        imageColor2.setImageResource(0);
                        imageColor3.setImageResource(0);
                        imageColor4.setImageResource(0);
                        imageColor5.setImageResource(0);
                        setSubtitleIndicatorColor();
                    }
                });

                //yellow
                bottomTaskLayout.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedNoteColor = "#ffff33";
                        imageColor1.setImageResource(0);
                        imageColor2.setImageResource(R.drawable.ic_done);
                        imageColor3.setImageResource(0);
                        imageColor4.setImageResource(0);
                        imageColor5.setImageResource(0);
                        setSubtitleIndicatorColor();
                    }
                });

                //red
                bottomTaskLayout.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedNoteColor = "#ff9999";
                        imageColor1.setImageResource(0);
                        imageColor2.setImageResource(0);
                        imageColor3.setImageResource(R.drawable.ic_done);
                        imageColor4.setImageResource(0);
                        imageColor5.setImageResource(0);
                        setSubtitleIndicatorColor();
                    }
                });

                //blue
                bottomTaskLayout.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedNoteColor = "#80e5ff";
                        imageColor1.setImageResource(0);
                        imageColor2.setImageResource(0);
                        imageColor3.setImageResource(0);
                        imageColor4.setImageResource(R.drawable.ic_done);
                        imageColor5.setImageResource(0);
                        setSubtitleIndicatorColor();
                    }
                });

                //peach
                bottomTaskLayout.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedNoteColor = "#FFE5B4";
                        imageColor1.setImageResource(0);
                        imageColor2.setImageResource(0);
                        imageColor3.setImageResource(0);
                        imageColor4.setImageResource(0);
                        imageColor5.setImageResource(R.drawable.ic_done);
                        setSubtitleIndicatorColor();
                    }
                });


                if (alreadyAvailableNote != null &&
                        alreadyAvailableNote.getColor() != null
                        && !alreadyAvailableNote.getColor().trim().isEmpty()){
                    switch (alreadyAvailableNote.getColor()){
                        case "#FDBE3B":
                            bottomTaskLayout.findViewById(R.id.viewColor2).performClick();
                            break;
                        case "#FF4842":
                            bottomTaskLayout.findViewById(R.id.viewColor3).performClick();
                            break;
                        case "#3A52Fc":
                            bottomTaskLayout.findViewById(R.id.viewColor4).performClick();
                            break;
                        case "#000000":
                            bottomTaskLayout.findViewById(R.id.viewColor5).performClick();
                            break;
                    }
                }


                Calendar myCalendar= Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd/MMM/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                        dateAdd.setText(sdf.format(myCalendar.getTime()));
                    }};

                dateAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DatePickerDialog(contactActivity, dateSetListener, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show(); }
                });

                Dialog successDialog = new Dialog(contactActivity);
                successDialog.setContentView(R.layout.success_dialog);
                Button okDbtn=successDialog.findViewById(R.id.okDbtn);

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String sNewTitle = titleAdd.getText().toString().trim();
                        String sNewDesc = descAdd.getText().toString().trim();
                        String sNewDate = dateAdd.getText().toString().trim();
                        int sID=contactData.getSpecificUserID();

                            if (!sNewTitle.equals("") && !sNewDesc.equals("") && !sNewDate.equals("")) {
                            successDialog.show();
                            MainData data = new MainData();
                            data.setTitle(sNewTitle);
                            data.setDescription(sNewDesc);
                            data.setDate(sNewDate);
                            data.setUserID(sID);
                            data.setColor(selectedNoteColor);
                            database.dao().insert(data);
                            //clear editText
                            titleAdd.setText("");
                            descAdd.setText("");
                            dateAdd.setText("");
                            list.clear();
                            list.addAll(database.dao().getAllContact());
                            notifyDataSetChanged();
                            okDbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    successDialog.dismiss();
                                }
                            });
                            dialog.dismiss();
                        }else {
                                if (sNewTitle.isEmpty() && sNewDesc.isEmpty()) {
                                    Toast.makeText(dialog.getContext(),
                                            "Note title can't be empty!", Toast.LENGTH_SHORT).show();
                                }
                            }
                    }
                });
            });
        }

    }
    private void setSubtitleIndicatorColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));

    }


}