package com.example.orderfood.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.orderfood.ControllerApplication;
import com.example.orderfood.R;
import com.example.orderfood.activity.MainActivity;
import com.example.orderfood.adapter.CartAdapter;
import com.example.orderfood.constant.GlobalFunction;
import com.example.orderfood.constant.IConstant;
import com.example.orderfood.database.FoodDatabase;
import com.example.orderfood.databinding.FragmentCartBinding;
import com.example.orderfood.event.ReloadListCartEvent;
import com.example.orderfood.event.SetTableCode;
import com.example.orderfood.model.Food;
import com.example.orderfood.model.Order;
import com.example.orderfood.model.Table;
import com.example.orderfood.model.User;
import com.example.orderfood.utils.DatetimeUtils;
import com.example.orderfood.utils.StringUtils;
import com.example.orderfood.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CartFragment extends BaseFragment{

    private FragmentCartBinding mFragmentCartBinding;
    private CartAdapter mCartAdapter;
    private List<Food> mListFoodCart;
    private int mAmount;
    private Table table;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentCartBinding = FragmentCartBinding.inflate(inflater,container,false);
        initUi();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        displayListFoodInCart();
        mFragmentCartBinding.tvOrderCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(table==null){
                    GlobalFunction.showToastMessage(getActivity(), "lựa chọn bàn  đặt hàng");
                }else {
                    CartFragment.this.onClickOrderCart();

                }
            }
        });
        return mFragmentCartBinding.getRoot();

    }

    @SuppressLint("SetTextI18n")
    private void initUi() {
         table = ControllerApplication.get(Objects.requireNonNull(getActivity())).getTable();
        if (table!=null){
            mFragmentCartBinding.tvTable.setText("Bàn: " + table.getCode());
        }

    }


    private void displayListFoodInCart() {
        if(getActivity()==null)
            return;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentCartBinding.rcvFoodCart.setLayoutManager(linearLayoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        mFragmentCartBinding.rcvFoodCart.addItemDecoration(itemDecoration);
        initDataFoodCart();
    }

    private void initDataFoodCart() {
        mListFoodCart = new ArrayList<>();
        mListFoodCart = FoodDatabase.getInstance(getActivity()).foodDAO().getListFoodCart();
        if(mListFoodCart==null || mListFoodCart.isEmpty())
            return;
        mCartAdapter = new CartAdapter(mListFoodCart, new CartAdapter.IClickListener() {
            @Override
            public void clickDeleteFood(Food food, int position) {
                deleteFoodFromCart(food, position);
            }

            @Override
            public void updateItemFood(Food food, int position) {
                FoodDatabase.getInstance(getActivity()).foodDAO().updateFood(food);
                mCartAdapter.notifyItemChanged(position);
                calculateTotalPrice();
            }
        });
        mFragmentCartBinding.rcvFoodCart.setAdapter(mCartAdapter);

        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        List<Food> listFoodCart =  FoodDatabase.getInstance(getActivity()).foodDAO().getListFoodCart();
        if(listFoodCart==null||listFoodCart.isEmpty()){
            String strZero = 0+ IConstant.CURRENCY;
            mFragmentCartBinding.tvTotalPrice.setText(strZero);
            mAmount = 0;
            return;
        }

        int totalPrice = 0;
        for (Food food : listFoodCart){
            totalPrice =  totalPrice + food.getTotalPrice();
        }
        String strTotalPrice = totalPrice+ IConstant.CURRENCY;
        mFragmentCartBinding.tvTotalPrice.setText(strTotalPrice);
        mAmount  = totalPrice;

    }

    private void deleteFoodFromCart(Food food, int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.confirm_delete_food))
                .setMessage(getString(R.string.message_delete_food))
                .setPositiveButton(getString(R.string.delete), (dialogInterface, i) -> {
                    FoodDatabase.getInstance(getActivity()).foodDAO().deleteFood(food);
                    mListFoodCart.remove(position);
                    calculateTotalPrice();
                })
                .setNegativeButton(getString(R.string.dialog_cancel), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();

    }

    @SuppressLint("SetTextI18n")
    private void onClickOrderCart() {
        if(getActivity()== null)
            return;
        if(mListFoodCart==null || mListFoodCart.isEmpty())
            return;

        View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_order,null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        // init ui
        TextView tvFoodsOrder = viewDialog.findViewById(R.id.tv_foods_order);
        TextView tvPriceOrder = viewDialog.findViewById(R.id.tv_price_order);
        TextView tvCancelOrder = viewDialog.findViewById(R.id.tv_cancel_order);
        TextView tvCreateOrder = viewDialog.findViewById(R.id.tv_create_order);
        TextView tvTable = viewDialog.findViewById(R.id.tv_table);

        // set data
        setTable(tvTable);

        tvPriceOrder.setText(mFragmentCartBinding.tvTotalPrice.getText().toString());

        tvFoodsOrder.setText(getStringListFoodsOrder());


        // Set Listener
        tvCancelOrder.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
        });

        tvCreateOrder.setOnClickListener(view -> {
            user = ControllerApplication.get(getActivity()).getUser();

            long id  = System.currentTimeMillis();

            String dateString = DatetimeUtils.convertTimeStampToDate(id);

            Order order = new Order(id,user.getId(), user.getName(), table.getCode(), mAmount, mListFoodCart,dateString);
            ControllerApplication.get(getActivity()).getBookingDatabaseReference()
                    .child(table.getCode())
                    .child(String.valueOf(id))
                    .setValue(order,(error, ref) -> {
                        GlobalFunction.showToastMessage(getActivity(), getString(R.string.msg_order_success));
                        GlobalFunction.hideSoftKeyboard(getActivity());
                        bottomSheetDialog.dismiss();
                        FoodDatabase.getInstance(getActivity()).foodDAO().deleteAllFood();
                        clearCart();
                    } );

        });
        bottomSheetDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void setTable(TextView tvTable) {
        tvTable.setText( "Bàn: " + table.getCode());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearCart() {
        if(mListFoodCart!=null){
            mListFoodCart.clear();
        }
        mCartAdapter.notifyDataSetChanged();
        calculateTotalPrice();
    }

    private String getStringListFoodsOrder() {
        if(mListFoodCart==null || mListFoodCart.isEmpty()){
            return "";
        }
        String result = "";
        for (Food food : mListFoodCart) {
            if (StringUtils.isEmpty(result)) {
                result = "- " + food.getName() + " (" + food.getRealPrice() + IConstant.CURRENCY + ") "
                        + "- " + getString(R.string.quantity) + " " + food.getCount();
            } else {
                result = result + "\n" + "- " + food.getName() + " (" + food.getRealPrice() + IConstant.CURRENCY + ") "
                        + "- " + getString(R.string.quantity) + " " + food.getCount();
            }
        }
        return  result;
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(false,getString(R.string.cart));
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReloadListCartEvent event ){
        displayListFoodInCart();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetTableCode event ){
        View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_order,null);
        TextView tvTable = viewDialog.findViewById(R.id.tv_table);
         initUi();
       setTable(tvTable);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


}
