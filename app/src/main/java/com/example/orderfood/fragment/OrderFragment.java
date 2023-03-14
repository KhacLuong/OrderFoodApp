package com.example.orderfood.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.orderfood.ControllerApplication;
import com.example.orderfood.R;
import com.example.orderfood.activity.MainActivity;
import com.example.orderfood.adapter.OrderAdapter;
import com.example.orderfood.constant.GlobalFunction;
import com.example.orderfood.constant.IConstant;
import com.example.orderfood.database.FoodDatabase;
import com.example.orderfood.databinding.FragmentOrderBinding;
import com.example.orderfood.event.SetTableCode;
import com.example.orderfood.model.Bill;
import com.example.orderfood.model.Food;
import com.example.orderfood.model.Order;
import com.example.orderfood.model.Table;
import com.example.orderfood.utils.StringUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class OrderFragment extends  BaseFragment{

    private FragmentOrderBinding mFragmentOrderBinding;
    private List<Order> mListOrder;
    private OrderAdapter mOrderAdapter;
    private Table table;
    private Bill bill;
    long a;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             @NonNull Bundle savedInstanceState) {
        mFragmentOrderBinding = FragmentOrderBinding.inflate(inflater,container, false);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        mListOrder= new ArrayList<>();

        setTable();
        getListOrders();

        initListener();

        return mFragmentOrderBinding.getRoot();
    }

    private void initListener() {
        mFragmentOrderBinding.btnPayment.setOnClickListener(view -> {
            if(bill==null||bill.getFoodList().isEmpty()){
                return;
            }
            new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.confirm_payment))
                .setMessage(getString(R.string.message_payment)+bill.getTableCode()
                +"\n" +getString(R.string.label_price)+bill.getAmount() + IConstant.CURRENCY)
                .setPositiveButton(getString(R.string.payment), (dialogInterface, i) -> {
                    ControllerApplication.get(Objects.requireNonNull(getActivity())).getBillDatabaseReference()
                    .child(String.valueOf(bill.getId()))
                    .setValue(bill, (error, ref) -> {
                        ControllerApplication.get(getActivity()).getBookingDatabaseReference()
                            .child(bill.getTableCode()).removeValue().addOnSuccessListener(unused -> {

                                if(table.isStatus()){
                                    table.setStatus(false);
                                    ControllerApplication.get(getActivity()).getTableDatabaseReference().child(table.getCode()).updateChildren(table.toMap());
                                    ControllerApplication.get(getActivity()).setTable(null);
                                }
                                GlobalFunction.showToastMessage(getActivity(), getString(R.string.message_payment_success));
                                })
                                .addOnFailureListener(e -> {
                                    ControllerApplication.get(Objects.requireNonNull(getActivity())).getBookingDatabaseReference()
                                            .child(String.valueOf(bill.getId())).removeValue();
                                    GlobalFunction.showToastMessage(getActivity(), getString(R.string.message_payment_failure));
                                });
                    });
                })
                .setNegativeButton(getString(R.string.dialog_cancel), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();

        });
    }

    private void setTable() {
        table = ControllerApplication.get(Objects.requireNonNull(getActivity())).getTable();
    }



    private void getListOrders() {

        if(getActivity() == null || table==null){
            return;
        }
        ControllerApplication.get(getActivity()).getBookingDatabaseReference().child(table.getCode())
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mListOrder.clear();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                        Order order= dataSnapshot.getValue(Order.class);
                        if(order==null){
                            return;
                        }
                        mListOrder.add(order);
                    }
                    disPlayListOrder();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    GlobalFunction.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
                }
            });
    }

    private void disPlayListOrder() {
            if (mListOrder==null||mListOrder.size()==0||getActivity()==null){
                mFragmentOrderBinding.layoutOrder.setVisibility(View.GONE);
                return;
            }
            mFragmentOrderBinding.layoutOrder.setVisibility(View.VISIBLE);
            mFragmentOrderBinding.tvTableCode.setText(table.getCode());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            mFragmentOrderBinding.rcvOrder.setLayoutManager(linearLayoutManager);
            mOrderAdapter = new OrderAdapter(mListOrder);
            mFragmentOrderBinding.rcvOrder.setAdapter(mOrderAdapter);

        getBillLast();
    }

    private void createBill(int key) {
        bill= new Bill();
        if (getActivity()==null){
            return;
        }

        bill.setTableCode(ControllerApplication.get(Objects.requireNonNull(getActivity())).getTable().getCode());
            for (Order order: mListOrder){
                for (Food food:order.getFoodList()){
                    boolean checkDup = false;
                    for (int i = 0; i < bill.getFoodList().size() ; i++){
                        if(bill.getFoodList().get(i).getId()==food.getId()){
                            int a  =bill.getFoodList().get(i).getCount();
                            bill.getFoodList().get(i).setCount(a+food.getCount());
                            checkDup= true;
                        }
                    }
                    if(!checkDup) {
                        try {
                            bill.getFoodList().add((Food) food.clone());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                bill.setAmount(bill.getAmount()+order.getAmount());
            }

        bill.setId(key+1);
        bill.setTimeStart("");
        bill.setTimeStart("");
        displayBill(bill);

    }

    @SuppressLint("SetTextI18n")
    private void displayBill(Bill bill) {
        String a =getStringListFoodsOrder(bill.getFoodList());
        mFragmentOrderBinding.tvTotalMenu.setText(a);
        mFragmentOrderBinding.tvTotalAmount.setText(bill.getAmount()+IConstant.CURRENCY);
        mFragmentOrderBinding.tvPayment.setText(IConstant.PAYMENT_METHOD_CASH);
    }

    private void getBillLast(){

        ControllerApplication.get(Objects.requireNonNull(getActivity())).getBillDatabaseReference().orderByKey()
                .limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DataSnapshot lastChildSnapshot = null;
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            lastChildSnapshot = childSnapshot;
                        }
                        int key=0;
                        if (lastChildSnapshot != null) {
                            try {
                                key = Integer.parseInt(Objects.requireNonNull(lastChildSnapshot.getKey()));
                            } catch (Exception ignored) {
                            }
                        }
                        createBill(key);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    private String getStringListFoodsOrder(List<Food> listFood) {
        if(listFood==null || listFood.isEmpty()){
            return "";
        }
        String result = "";
        for (Food food : listFood) {
            if (StringUtils.isEmpty(result)) {
                result = "- " + food.getName() + " (" + food.getRealPrice() + IConstant.CURRENCY + ") "
                        + "- " + "Số lượng: " + " " + food.getCount();
            } else {
                result = result + "\n" + "- " + food.getName() + " (" + food.getRealPrice() + IConstant.CURRENCY + ") "
                        + "- " + "Số lượng: " + " " + food.getCount();
            }
        }
        return  result;
    }
    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(false, getString(R.string.order));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SetTableCode event ){
        setTable();
        getListOrders();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
