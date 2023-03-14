package com.example.orderfood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.orderfood.R;
import com.example.orderfood.adapter.MoreImageAdapter;
import com.example.orderfood.constant.IConstant;
import com.example.orderfood.database.FoodDatabase;
import com.example.orderfood.databinding.ActivityFoodDetailBinding;
import com.example.orderfood.event.ReloadListCartEvent;
import com.example.orderfood.model.Food;
import com.example.orderfood.utils.GlideUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class FoodDetailActivity extends AppCompatActivity {
    private ActivityFoodDetailBinding mActivityFoodDetailBinding;
    private Food mFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityFoodDetailBinding = ActivityFoodDetailBinding.inflate(getLayoutInflater());
        setContentView(mActivityFoodDetailBinding.getRoot());

        getDataIntent();
        initToolbar();
        setDataFoodDetail();
        initListener();

    }




    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            mFood = (Food) bundle.get(IConstant.KEY_INTENT_FOOD_OBJECT);

        }
    }
    private void initToolbar() {

        mActivityFoodDetailBinding.toolbar.layoutToolbar.setVisibility(View.VISIBLE);
        mActivityFoodDetailBinding.toolbar.imgBack.setVisibility(View.VISIBLE);
        mActivityFoodDetailBinding.toolbar.imgCart.setVisibility(View.VISIBLE);
        mActivityFoodDetailBinding.toolbar.tvTitle.setText(getString(R.string.food_detail_title));
        mActivityFoodDetailBinding.toolbar.imgBack.setOnClickListener(view -> onBackPressed());

    }
    private void setDataFoodDetail() {
        if(mFood==null){
            return;
        }
        GlideUtils.loadUrlBanner(mFood.getBanner(), mActivityFoodDetailBinding.imageFood);
        if(mFood.getSale()<=0){
            mActivityFoodDetailBinding.tvSaleOff.setVisibility(View.GONE);
            mActivityFoodDetailBinding.tvPrice.setVisibility(View.GONE);

            String strPrice = mFood.getPrice() + IConstant.CURRENCY;
            mActivityFoodDetailBinding.tvPriceSale.setText(strPrice);
        }else {
            mActivityFoodDetailBinding.tvSaleOff.setVisibility(View.VISIBLE);
            mActivityFoodDetailBinding.tvPrice.setVisibility(View.VISIBLE);

            String strSale = "Giảm " + mFood.getSale() + "%";
            mActivityFoodDetailBinding.tvSaleOff.setText(strSale);

            String strPriceOld = mFood.getPrice() + IConstant.CURRENCY;
            mActivityFoodDetailBinding.tvPrice.setText(strPriceOld);
            mActivityFoodDetailBinding.tvPrice.setPaintFlags
                    (mActivityFoodDetailBinding.tvPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

            String strRealPrice = mFood.getRealPrice() + IConstant.CURRENCY;
            mActivityFoodDetailBinding.tvPriceSale.setText(strRealPrice);

        }
        mActivityFoodDetailBinding.tvFoodName.setText(mFood.getName());
        mActivityFoodDetailBinding.tvFoodDescription.setText(mFood.getDescription());
        displayListMoreImages();

        setStatusButtonAddToCart();
    }

    private void displayListMoreImages() {
        if(mFood.getImages()==null || mFood.getImages().isEmpty()){
            mActivityFoodDetailBinding.tvMoreImageLabel.setVisibility(View.GONE);

            return;
        }
        mActivityFoodDetailBinding.tvMoreImageLabel.setVisibility(View.VISIBLE);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2 );
        mActivityFoodDetailBinding.rcvImages.setLayoutManager(gridLayoutManager);
        MoreImageAdapter moreImageAdapter = new MoreImageAdapter(mFood.getImages());
        mActivityFoodDetailBinding.rcvImages.setAdapter(moreImageAdapter);
    }

    @SuppressLint("ResourceAsColor")
    private void setStatusButtonAddToCart() {
        if(isFoodInCart()){
            mActivityFoodDetailBinding.tvAddToCart.setBackgroundResource(R.drawable.bg_gray_shape_corner_6);
            mActivityFoodDetailBinding.tvAddToCart.setText(getString(R.string.added_to_cart));
            mActivityFoodDetailBinding.tvAddToCart.setTextColor(ContextCompat.getColor(this, R.color.white));

        }else {
            mActivityFoodDetailBinding.tvAddToCart.setBackgroundResource(R.drawable.bg_green_shape_corner_6);
            mActivityFoodDetailBinding.tvAddToCart.setText(R.string.add_to_cart);
            mActivityFoodDetailBinding.tvAddToCart.setTextColor(ContextCompat.getColor(this, R.color.white));

        }
    }

    private boolean isFoodInCart() {
        List<Food> list = FoodDatabase.getInstance(this).foodDAO().getFoodById(mFood.getId());
        return  list != null && !list.isEmpty();
    }

    private void initListener() {
        mActivityFoodDetailBinding.tvAddToCart.setOnClickListener(view -> onClickAddToCart());
        mActivityFoodDetailBinding.toolbar.imgCart.setOnClickListener(v -> goToCart());
    }

    private void onClickAddToCart() {
        if(isFoodInCart()){
            return;
        }
        View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_cart, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(viewDialog);
        ImageView imgFoodCart = viewDialog.findViewById(R.id.img_food_cart);
        TextView tvFoodNameCart = viewDialog.findViewById(R.id.tv_food_name_cart);
        TextView tvFoodPriceCart = viewDialog.findViewById(R.id.tv_food_price_cart);
        TextView tvSubtractCount = viewDialog.findViewById(R.id.tv_subtract);
        TextView tvCount = viewDialog.findViewById(R.id.tv_count);
        TextView tvAddCount = viewDialog.findViewById(R.id.tv_add);
        TextView tvCancel = viewDialog.findViewById(R.id.tv_cancel);
        TextView tvAddCart = viewDialog.findViewById(R.id.tv_add_cart);

        GlideUtils.loadUrl(mFood.getImage(), imgFoodCart);
        tvFoodNameCart.setText(mFood.getName());

        int totalPrice = mFood.getRealPrice();
        String strTotalPrice = totalPrice + IConstant.CURRENCY;
        tvFoodPriceCart.setText(strTotalPrice);

        mFood.setCount(1);
        mFood.setTotalPrice(totalPrice);

        tvSubtractCount.setOnClickListener(view -> {

            int count = Integer.parseInt(tvCount.getText().toString());
            if(count<=1){
                return;
            }
            int newCount = count-1;
            tvCount.setText(String.valueOf(newCount));

            int totalPrice1 = mFood.getRealPrice()*newCount;
            String strTotalPrice1 = totalPrice1 + IConstant.CURRENCY;
            tvFoodPriceCart.setText(strTotalPrice1);

            mFood.setCount(newCount);
            mFood.setTotalPrice(totalPrice1);
        });
        tvAddCount.setOnClickListener(view -> {
            int newCount   = Integer.parseInt(tvCount.getText().toString())+1;
            tvCount.setText(String.valueOf(newCount));

            int totalPrice2 = mFood.getRealPrice()*newCount;
            String strTotalPrice2 = totalPrice2 + IConstant.CURRENCY;
            tvFoodPriceCart.setText(strTotalPrice2);

            mFood.setCount(newCount);
            mFood.setTotalPrice(totalPrice2);
        });
        tvCancel.setOnClickListener(view -> bottomSheetDialog.dismiss());
        tvAddCart.setOnClickListener(view -> {
            FoodDatabase.getInstance(FoodDetailActivity.this).foodDAO().insertFood(mFood);
            bottomSheetDialog.dismiss();
            setStatusButtonAddToCart();
            EventBus.getDefault().post(new ReloadListCartEvent());
            goToHome();


        });
        bottomSheetDialog.show();
    }

    private void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void goToCart() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("cart", true);
        startActivity(intent);
    }


}