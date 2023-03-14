package com.example.orderfood.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.orderfood.ControllerApplication;
import com.example.orderfood.R;
import com.example.orderfood.activity.FoodDetailActivity;
import com.example.orderfood.activity.MainActivity;
import com.example.orderfood.adapter.FoodGridAdapter;
import com.example.orderfood.adapter.FoodBannerAdapter;
import com.example.orderfood.constant.GlobalFunction;
import com.example.orderfood.constant.IConstant;
import com.example.orderfood.databinding.FragmentHomeBinding;
import com.example.orderfood.model.Food;
import com.example.orderfood.utils.StringUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends  BaseFragment{
    private FragmentHomeBinding mFragmentHomeBinding;
    private List<Food> mListFood;
    private List<Food> listFoodShow;
    private FoodBannerAdapter mFoodBannerAdapter;
    private FoodGridAdapter mFoodGridAdapter;


    private final Handler mHandlerBanner = new Handler();
    private final Runnable mRunnableBanner = new Runnable() {
        @Override
        public void run() {

            List<Food> listFoodBanner = getListFoodBanner();
            if(listFoodBanner.isEmpty()){
                return;
            }
            if(mFragmentHomeBinding.viewpager2.getCurrentItem() == listFoodBanner.size()-1){
                mFragmentHomeBinding.viewpager2.setCurrentItem(0);
                return;
            }
            mFragmentHomeBinding.viewpager2.setCurrentItem(mFragmentHomeBinding.viewpager2.getCurrentItem()+1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        getListFoodFromFirebase("");
        initListener();
        listenerScroll();

        return mFragmentHomeBinding.getRoot();
    }

    private void listenerScroll() {
        mFragmentHomeBinding.scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                // Lấy độ cao của menu
//                int slideHeight = mFragmentHomeBinding.slideShow.getHeight();
//
//                // Nếu scrollY lớn hơn hoặc bằng độ cao của menu, thiết lập translationY của menu bằng 0
//                if (scrollY >= slideHeight) {
//                    mFragmentHomeBinding.menu.setTranslationY(0);
//                } else { // Ngược lại, thiết lập translationY của menu bằng scrollY âm (để menu dừng lại ở top màn hình)
//                    mFragmentHomeBinding.menu.setTranslationY(-scrollY);
//                }
            }
        });
    }

    private void getListFoodFromFirebase(String key) {
        if(getActivity()==null){
            return;
        }

        ControllerApplication.get(getActivity()).getFoodDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFragmentHomeBinding.layoutContentHome.setVisibility(View.VISIBLE);
                mListFood = new ArrayList<>();
                listFoodShow = new ArrayList<>();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Food food = dataSnapshot.getValue(Food.class);
                    if(food==null){
                        return;
                    }
                    mListFood.add(food);
                }
                if(StringUtils.isEmpty(key)){
                    for (Food f: mListFood){
                        if (f.isPopular())
                            listFoodShow.add(f);
                    }
                }else {
                    for (Food food:mListFood){
                        if(GlobalFunction.getTextSearch(food.getName()).toLowerCase().trim()
                                .contains(GlobalFunction.getTextSearch(key).toLowerCase().trim())){
                            listFoodShow.add(0,food);
                        }
                    }

                }
                displayListFoodBanner();
                displayListFoodSuggest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void displayListFoodBanner() {
        mFoodBannerAdapter = new FoodBannerAdapter(getListFoodBanner(),this::goToFoodDetail);
        mFragmentHomeBinding.viewpager2.setAdapter(mFoodBannerAdapter);
        mFragmentHomeBinding.indicator3Home.setViewPager(mFragmentHomeBinding.viewpager2);
        mFragmentHomeBinding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandlerBanner.removeCallbacks(mRunnableBanner);
                mHandlerBanner.postDelayed(mRunnableBanner,3000);
            }
        });
    }

    private void goToFoodDetail(Food food) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(IConstant.KEY_INTENT_FOOD_OBJECT, food);
        GlobalFunction.startActivity(getActivity(), FoodDetailActivity.class, bundle);
    }

    private List<Food> getListFoodBanner() {
        List<Food> list = new ArrayList<>();
        if(mListFood==null||mListFood.isEmpty()){
            return list;
        }
        for (Food food: mListFood){
            if(food.isSetBanner()){
                list.add(food);
            }
        }
        return list;
    }

    private void displayListFoodSuggest() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        mFragmentHomeBinding.rcvFoodHome.setLayoutManager(gridLayoutManager);

        mFoodGridAdapter = new FoodGridAdapter(listFoodShow, this::goToFoodDetail);
        mFragmentHomeBinding.rcvFoodHome.setAdapter(mFoodGridAdapter);
    }

    private void initListener() {
        mFragmentHomeBinding.edtSearchNameHome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String strKey = editable.toString().trim().toLowerCase();
                if(strKey.equals("") || strKey.length() == 0){
                    if(mListFood!=null) mListFood.clear();
                    getListFoodFromFirebase("");
                }
            }
        });

        mFragmentHomeBinding.imgSearchHome.setOnClickListener(view -> searchFood());
        mFragmentHomeBinding.edtSearchNameHome.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if(actionId== EditorInfo.IME_ACTION_SEARCH){
                searchFood();
                return true;
            }
            return false;
        });

        mFragmentHomeBinding.menuSuggestFood.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                mFragmentHomeBinding.edtSearchNameHome.setText("");
            }
        });

        mFragmentHomeBinding.menuFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(Objects.requireNonNull(getActivity()), mFragmentHomeBinding.menuFoods);
                popupMenu.getMenuInflater().inflate(R.menu.menu_foods, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        listFoodShow.clear();
                        switch (item.getItemId()){
                            case  R.id.menu_chicken:
                                getListFoodChicken();
                                break;
                            case  R.id.menu_beef:
                                getListFoodBeef();
                                break;
                            case  R.id.menu_seafood:
                                getListFoodSeafood();
                                break;
                            case  R.id.menu_pork:
                                getListFoodPork();
                                break;
                            case  R.id.menu_other_foods:
                                getListFoodOtherFood();
                                break;

                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


        mFragmentHomeBinding.menuDrinks.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(Objects.requireNonNull(getActivity()), mFragmentHomeBinding.menuDrinks);
            popupMenu.getMenuInflater().inflate(R.menu.menu_drinks, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    listFoodShow.clear();
                    switch (item.getItemId()){
                        case  R.id.menu_tea:
                            getListDrinkTea();
                            break;
                        case  R.id.menu_smoothie:
                            getListDrinkSmoothie();
                            break;
                        case  R.id.menu_sweet_dessert_soup:
                            getListFDrinkSweetDessertSoup();
                            break;
                        case  R.id.menu_other_drinks:
                            getListOtherDrink();
                            break;
                    }

                    return false;
                }
            });
            popupMenu.show();
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListOtherDrink() {
        for (Food food: mListFood){
            if (food.getKind().equals("otherdrik"))
                listFoodShow.add(food);
        }
        mFoodGridAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListFDrinkSweetDessertSoup() {
        for (Food food: mListFood){
            if (food.getKind().equals("sweetdessertsoup"))
                listFoodShow.add(food);
        }
        mFoodGridAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListDrinkSmoothie() {
        for (Food food: mListFood){
            if (food.getKind().equals("smoothie"))
                listFoodShow.add(food);
        }
        mFoodGridAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListDrinkTea() {
        for (Food food: mListFood){
            if (food.getKind().equals("tea"))
                listFoodShow.add(food);
        }
        mFoodGridAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListFoodOtherFood() {
        for (Food food: mListFood){
            if (food.getKind().equals("ortherfood"))
                listFoodShow.add(food);
        }
        mFoodGridAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListFoodPork() {
        for (Food food: mListFood){
            if (food.getKind().equals("pork"))
                listFoodShow.add(food);
        }
        mFoodGridAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListFoodSeafood() {
        for (Food food: mListFood){
            if (food.getKind().equals("seafood"))
                listFoodShow.add(food);
        }
        mFoodGridAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListFoodBeef() {
       for (Food food: mListFood){
           if (food.getKind().equals("beef"))
               listFoodShow.add(food);
       }
       mFoodGridAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListFoodChicken() {
        for (Food food: mListFood){
            if (food.getKind().equals("chicken"))
                listFoodShow.add(food);
        }
        mFoodGridAdapter.notifyDataSetChanged();
    }

    private void searchFood() {
        String strKey = mFragmentHomeBinding.edtSearchNameHome.getText().toString().trim();
        if(mListFood!=null) mListFood.clear();
        GlobalFunction.hideSoftKeyboard(getActivity());
        getListFoodFromFirebase(strKey);

    }





    @Override
    protected void initToolbar() {
        if(getActivity()!=null){
            ((MainActivity) getActivity()).setToolBar(true, getString(R.string.home));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandlerBanner.removeCallbacks(mRunnableBanner);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandlerBanner.postDelayed(mRunnableBanner, 3000);
    }
}
