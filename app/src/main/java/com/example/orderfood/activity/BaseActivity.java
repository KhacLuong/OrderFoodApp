package com.example.orderfood.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.orderfood.R;

public abstract class BaseActivity extends AppCompatActivity {
    protected MaterialDialog progressDialog, alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createProgressDialog();
        createAlertDialog();
    }

    public   void createAlertDialog(){
        alertDialog = new MaterialDialog.Builder(this)
                .title(R.string.app_name)
                .positiveText(R.string.action_ok)
                .cancelable(false)
                .build();
    }

    public void createProgressDialog(){
        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.waiting_message)
                .progress(true,0)
                .build();
    }

    public  void showProgressDialog (boolean value){
        if(value){
            if(progressDialog !=null && !progressDialog.isShowing()){
                progressDialog.show();
                progressDialog.setCancelable(false);
            }
        }else {
            if(progressDialog!=null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }
    public void dismissProgressDialog(){
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        if(alertDialog!=null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
        super.onDestroy();
    }

}
