package com.example.duet.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

import com.example.duet.R;

public class CustomProgressDialog{
    private Activity activity;
    public AlertDialog dialog;

    public CustomProgressDialog(Activity activity){
        this.activity = activity;
    }

    public void showLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_progress_dialog_layout, null));
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}
