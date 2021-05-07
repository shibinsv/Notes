package com.example.notesroomdb.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.notesroomdb.R;
import com.google.android.material.snackbar.Snackbar;

public class AppUtils {

    private static AppUtils appUtils = null;
    private Toast toast;


    public static AppUtils getInstance() {
        if (appUtils == null) {
            return new AppUtils();
        } else {
            return appUtils;
        }
    }

    //show SnackBar
    public void showSnackbar(Context context, String text, View view) {
        if (view != null) {
            Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            snackbar.show();
        }
    }

    //used to show Toast
    public void showToast(Context context, String text) {
        if (toast != null)
            toast.cancel();

        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void alertDialogBox(Activity activity, String title, String message){
        AlertDialog.Builder alertBox = new AlertDialog.Builder(activity);
        alertBox.setTitle(title);
        alertBox.setCancelable(false);
        alertBox.setMessage(message);
        alertBox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        alertBox.show();
    }

}
