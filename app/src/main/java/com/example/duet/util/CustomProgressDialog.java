package com.example.duet.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;

import com.example.duet.R;

/**
 * 커스텀 ProgressDialog 클래스
 * 네트워크, DB 작업 시, 앱이 멈추지 않았다는 signal 로 표시하기 위한 로딩 창을 위한 Progress Dialog
 * - Dialog 시작 후, 사이에 처리할 작업을 Thread 로 처리
 * - Thread 의 모든 동작이 끝난 뒤, Dialog 종료
 * @author Seunggun Sin, 2022-05-06
 */
public class CustomProgressDialog{
    private Activity activity;
    private AlertDialog dialog;

    public CustomProgressDialog(Activity activity){
        this.activity = activity;
    }

    /**
     * Progress Dialog 를 시작
     */
    public void showLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_progress_dialog_layout, null));
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    /**
     * Progress Dialog 를 종료
     */
    public void dismissDialog(){
        dialog.dismiss();
    }
}
