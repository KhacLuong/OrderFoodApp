package com.example.orderfood.activity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.orderfood.ControllerApplication;
import com.example.orderfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        nextActivity();

    }
    private void nextActivity() {
        Handler handler = new Handler();
        handler.postDelayed(this::startActivity,2000);
    }

    public  void startActivity(){

        ControllerApplication.get(getApplicationContext()).setUserAuth(FirebaseAuth.getInstance().getCurrentUser());

        Intent intent;
        if(ControllerApplication.get(getApplicationContext()).getUserAuth() == null){
            intent = new Intent(SplashActivity.this, SignInActivity.class);
        }else {
            intent =  new Intent(SplashActivity.this, MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}