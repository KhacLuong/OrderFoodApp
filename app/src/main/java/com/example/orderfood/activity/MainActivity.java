package com.example.orderfood.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.orderfood.ControllerApplication;
import com.example.orderfood.R;
import com.example.orderfood.adapter.MainViewPagerAdapter;
import com.example.orderfood.adapter.TableAdapter;
import com.example.orderfood.constant.GlobalFunction;
import com.example.orderfood.constant.IConstant;
import com.example.orderfood.databinding.ActivityMainBinding;
import com.example.orderfood.databinding.ItemTableBinding;
import com.example.orderfood.event.ReloadListCartEvent;
import com.example.orderfood.event.SetTableCode;
import com.example.orderfood.model.SnapShot;
import com.example.orderfood.model.Table;
import com.example.orderfood.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    public ActivityMainBinding mActivityMainBinding;

    private TableAdapter mTableAdapter;
    private List<Table> mTableList;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(mActivityMainBinding.getRoot());
        mTableList = new ArrayList<>();

        mActivityMainBinding.viewpager2.setUserInputEnabled(false);
        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(this);
        mActivityMainBinding.viewpager2.setAdapter(mainViewPagerAdapter);

        getUserFromFirebase();
        getListTableFromFirebase();
        gotoCart();
        displayListTable(mTableList);
        onPageChangeViewPager();
        onClickBottomNavigation();
        onClickProfile();
        onClickSignOut();

    }



    public void onPageChangeViewPager(){
        mActivityMainBinding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.nav_home).setChecked(true);
                        break;

                    case 1:
                        mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.nav_cart).setChecked(true);
                        break;

                    case 2:
                        mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.nav_feedback).setChecked(true);
                        break;


                    case 3:
                        mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.nav_order).setChecked(true);
                        break;
                }
            }
        });
    }
    public void onClickBottomNavigation(){
        mActivityMainBinding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        mActivityMainBinding.viewpager2.setCurrentItem(0);
                        break;
                    case R.id.nav_cart:
                        mActivityMainBinding.viewpager2.setCurrentItem(1);
                        break;
                    case R.id.nav_feedback:
                        mActivityMainBinding.viewpager2.setCurrentItem(2);
                        break;
                    case R.id.nav_order:
                        mActivityMainBinding.viewpager2.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });
    }




    public void gotoCart(){
        boolean showCartFragment = getIntent().getBooleanExtra("cart", false);

        if (showCartFragment) {
            mActivityMainBinding.viewpager2.setCurrentItem(1);
            mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.nav_cart).setChecked(true);

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

//        Navigation

    private void getListTableFromFirebase() {
        if(getApplicationContext()==null)
            return;


        ControllerApplication.get(getApplicationContext()).getTableDatabaseReference().addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Table table = snapshot.getValue(Table.class);
                if(table!=null){
                    mTableList.add(table);
                }
                mTableAdapter.notifyDataSetChanged();

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Table table = snapshot.getValue(Table.class);
                if(table== null || mTableList ==null || mTableList.isEmpty()){
                    return;
                }
                for (int i = 0; i < mTableList.size(); i++) {
                    if( table.getCode().equals(mTableList.get(i).getCode())){
                        mTableList.set(i, table);
                    }
                }
                mTableAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(MainActivity.this, getString(R.string.msg_get_date_error));
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void displayListTable(List<Table> mTableList) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 4);
        mActivityMainBinding.rcvTable.setLayoutManager(gridLayoutManager);
        mTableAdapter = new TableAdapter(mTableList, new TableAdapter.IClickListener() {
            @Override
            public void onClickItemTable(Table table) {
                onClickTable(table);
                if(mActivityMainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                    mActivityMainBinding.drawerLayout.closeDrawers();
                }
                EventBus.getDefault().post(new SetTableCode());
            }

            @Override
            public void onLongClickItemTable(Table table) {
                onLongClickTable( table);
                if(mActivityMainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                    mActivityMainBinding.drawerLayout.closeDrawers();
                }
                EventBus.getDefault().post(new SetTableCode());
            }
        });
        mActivityMainBinding.rcvTable.setAdapter(mTableAdapter);
        mTableAdapter.notifyDataSetChanged();
    }

    private void onClickTable(Table table) {
        if(!table.isStatus()){
            table.setStatus(true);
            ControllerApplication.get(getApplicationContext()).getTableDatabaseReference().child(table.getCode()).updateChildren(table.toMap());
        }
        ControllerApplication.get(getApplicationContext()).setTable(table);
    }

    private void onLongClickTable(Table table) {
        if(table.isStatus()){
            table.setStatus(false);
            ControllerApplication.get(getApplicationContext()).getTableDatabaseReference().child(table.getCode()).updateChildren(table.toMap());
        }

//        if(ControllerApplication.get(getApplicationContext()).getTable().getCode().equals(table.getCode()))
            ControllerApplication.get(getApplicationContext()).setTable(null);
    }

    private void getUserFromFirebase() {
       FirebaseUser currentUser = ControllerApplication.get(getApplicationContext()).getUserAuth();
        if( currentUser !=null){
            getUserInfo(currentUser.getUid());
        }else {
            mActivityMainBinding.headerNav.tvName.setText("name");
            mActivityMainBinding.headerNav.tvEmail.setText("email");
        }
    }

    private void getUserInfo(String userId) {
        if(getApplicationContext()==null)
            return;
        ControllerApplication.get(MainActivity.this).getTUserDatabaseReference().child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

               user = snapshot.getValue(User.class);
               if(user!=null)
                   ControllerApplication.get(getApplicationContext()).setUser(user);
                    displayInfoUser(user);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(MainActivity.this, getString(R.string.msg_get_date_error));

            }
        });
    }

    private void displayInfoUser(User user) {
        mActivityMainBinding.headerNav.tvName.setText(user.getName());
        mActivityMainBinding.headerNav.tvEmail.setText(user.getEmail());
    }

    private void onClickSignOut() {
        mActivityMainBinding.btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Bạn có chắc  chắn muốn đăng xuất" )
                    .setPositiveButton("Yes", (dialog, which) -> {
                        ControllerApplication.get(getApplicationContext()).getFirebaseAuth().signOut();
                        GlobalFunction.startActivity(MainActivity.this, SignInActivity.class);
                        finish();
                    })
                .setNegativeButton("No", null)
                .show();
            }
        });
    }

    private void onClickProfile() {
        mActivityMainBinding.btnProfile.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(IConstant.KEY_INTENT_FOOD_OBJECT, user);
            GlobalFunction.startActivity(MainActivity.this, ProfileActivity.class, bundle);
        });


    }
    @Override
    public void onBackPressed() {

        showConfirmExitApp();

    }

    public void setToolBar(boolean isHome, String title){
        if (isHome){
            mActivityMainBinding.toolbar.layoutToolbar.setVisibility(View.GONE);
            return;
        }
        mActivityMainBinding.toolbar.layoutToolbar.setVisibility(View.VISIBLE);
        mActivityMainBinding.toolbar.tvTitle.setText(title);
    }




}