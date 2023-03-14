package com.example.orderfood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.orderfood.ControllerApplication;
import com.example.orderfood.R;
import com.example.orderfood.constant.GlobalFunction;
import com.example.orderfood.databinding.ActivityFoodDetailBinding;
import com.example.orderfood.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding mActivitySignInBinding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySignInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(mActivitySignInBinding.getRoot());
        progressDialog = new ProgressDialog(this);
        mActivitySignInBinding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignIn();

            }
        });
    }

    private void onClickSignIn() {
        String strEmail = mActivitySignInBinding.edtEmail.getText().toString().trim();
        String strPassword =  mActivitySignInBinding.edtPassword.getText().toString().trim();
         if (strEmail.isEmpty() || strPassword.isEmpty()){
             GlobalFunction.showToastMessage(SignInActivity.this, "Authentication failed");
         }else {
             progressDialog.show();
             ControllerApplication.get(getApplicationContext()).getFirebaseAuth().signInWithEmailAndPassword(strEmail,strPassword )
                     .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             if (task.isSuccessful()) {
                                 progressDialog.dismiss();
                                ControllerApplication.get(getApplicationContext()).setUserAuth(FirebaseAuth.getInstance().getCurrentUser());
                                 GlobalFunction.startActivity(SignInActivity.this, MainActivity.class);
                                 finishAffinity();
                             } else {
                                 GlobalFunction.showToastMessage(SignInActivity.this, "Authentication failed");
                                 progressDialog.dismiss();
                             }
                         }
                     });
         }

    }
    private  void showConfirmExitApp(){
        new MaterialDialog.Builder(this)
                .title(getString(R.string.app_name))
                .content(getString(R.string.msg_exit_app))
                .positiveText(getString(R.string.action_ok))
                .onPositive((dialog, which) -> finish())
                .negativeText(getString(R.string.action_cancel))
                .cancelable(false)
                .show();
    }


    @Override
    public void onBackPressed() {

        showConfirmExitApp();

    }
}