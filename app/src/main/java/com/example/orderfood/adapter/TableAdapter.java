package com.example.orderfood.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.ControllerApplication;
import com.example.orderfood.R;
import com.example.orderfood.constant.GlobalFunction;
import com.example.orderfood.databinding.ItemTableBinding;
import com.example.orderfood.event.OrderEvent;
import com.example.orderfood.event.SetTableCode;
import com.example.orderfood.model.Order;
import com.example.orderfood.model.Table;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {
    private boolean isEventBusRegistered = false;
    private List<Order> listOrder;
    private final List<Table> mTableList;
    private final IClickListener iClickListener;
    private int indexSelected = -1;
    public TableAdapter(List<Table> mTableList, IClickListener iClickListener ) {
        this.mTableList = mTableList;
        this.iClickListener = iClickListener;

    }

    public interface  IClickListener{
        void onClickItemTable(Table table);
        void onLongClickItemTable(Table table);
    }
    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTableBinding itemTableBinding = ItemTableBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new TableViewHolder(itemTableBinding);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Table table = mTableList.get(position);
        if (table==null)
            return;


        holder.mItemTableBinding.tvTable.setText(String.valueOf(table.getCode()) );
        if (table.isStatus()){
            holder.mItemTableBinding.tvTable.setBackgroundResource(R.drawable.bg_dark_red_shape_corner_6);
        }else {
            holder.mItemTableBinding.tvTable.setBackgroundResource(R.drawable.bg_gray_shape_corner_6);
        }
        if (position ==indexSelected){
            holder.mItemTableBinding.tvTable.setBackgroundResource(R.drawable.bg_red_shape_corner_6);
        }

        holder.mItemTableBinding.layoutItem.setOnClickListener(view -> {
            if(!table.isStatus()){

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Xác nhận đặt  bàn: " + table.getCode() )
                    .setPositiveButton("Yes", (dialog, which) -> {
                        indexSelected = position;
                        iClickListener.onClickItemTable(table);
                    })
                    .setNegativeButton("No", null)
                    .show();
            }else {

                if (indexSelected!=position && table.isStatus()){
                    indexSelected = position;
                    iClickListener.onClickItemTable(table);
                    notifyDataSetChanged();

                }else {
                    indexSelected=-1;
                }
            }

        });

//        VIET THEM PHẦN KO CHO XÓA KHI BÀN ĐÓ ĐÃ CÓ ORDER

        holder.mItemTableBinding.layoutItem.setOnLongClickListener(view -> {
            if(table.isStatus()) {

                getListOrders(holder.itemView.getContext(), table);

                EventBus.getDefault().register(new Object() {
                    @Subscribe(threadMode = ThreadMode.MAIN)
                    public void onOrderEvent(OrderEvent event) {

                        boolean checkNull = event.getOrderList();

                        if (checkNull) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setMessage("Hủy Đặt bàn: " + table.getCode())
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    if (indexSelected == position)
                                        indexSelected = -1;

                                    iClickListener.onLongClickItemTable(table);
                                })
                                .setNegativeButton("No", null)
                                .show();
                        }

                    }

                });
                onDestroy();

            }
            return false;
        });



    }

    @Override
    public int getItemCount() {
        return null == mTableList ? 0 : mTableList.size();
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder{
        private final ItemTableBinding mItemTableBinding;

        public TableViewHolder(ItemTableBinding mItemTableBinding) {
            super(mItemTableBinding.getRoot());
            this.mItemTableBinding = mItemTableBinding;
        }


    }

    private void getListOrders(Context context, Table table) {
        listOrder = new ArrayList<>();
        if(context == null || table==null){
            return;
        }
        ControllerApplication.get(context).getBookingDatabaseReference().child(table.getCode())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listOrder.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Order order= dataSnapshot.getValue(Order.class);
                            if(order==null){
                                return;
                            }
                            try {
                                listOrder.add((Order) order.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }

                        EventBus.getDefault().post(new OrderEvent(listOrder==null||listOrder.isEmpty()));


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        GlobalFunction.showToastMessage(context.getApplicationContext(), context.getString(R.string.msg_get_date_error));
                    }
                });
         }

    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }



}
