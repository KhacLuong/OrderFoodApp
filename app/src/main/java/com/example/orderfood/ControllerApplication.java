package com.example.orderfood;

import android.app.Application;
import android.content.Context;

import com.example.orderfood.constant.IConstant;
import com.example.orderfood.model.Table;
import com.example.orderfood.model.User;
import com.google.firebase.FirebaseApp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ControllerApplication extends Application {
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser userAuth;
    private User user;
    private  Table table;



    public static ControllerApplication get(Context context){
        return (ControllerApplication) context.getApplicationContext();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance(IConstant.FIREBASE_URL);
        mFirebaseAuth = FirebaseAuth.getInstance();


    }
    public DatabaseReference getFoodDatabaseReference(){
        return mFirebaseDatabase.getReference("/food");
    }
    public DatabaseReference getFeedbackDatabaseReference(){
        return mFirebaseDatabase.getReference("/feedback");
    }
    public DatabaseReference getBookingDatabaseReference(){
        return  mFirebaseDatabase.getReference("/booking");

    }
    public DatabaseReference getTableDatabaseReference(){
        return  mFirebaseDatabase.getReference("/table");

    }
    public DatabaseReference getTUserDatabaseReference(){
        return  mFirebaseDatabase.getReference("/user");

    }
    public DatabaseReference getBillDatabaseReference(){
        return  mFirebaseDatabase.getReference("/bill");

    }
    public FirebaseAuth getFirebaseAuth() {
        return mFirebaseAuth;
    }


    public  Table getTable() {
        return table;
    }

    public  void setTable(Table table) {
        this.table = table;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public FirebaseUser getUserAuth() {
        return userAuth;
    }

    public void setUserAuth(FirebaseUser userAuth) {
        this.userAuth = userAuth;
    }

}
