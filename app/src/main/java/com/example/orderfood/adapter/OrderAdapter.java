package com.example.orderfood.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.R;
import com.example.orderfood.constant.IConstant;
import com.example.orderfood.databinding.ItemOrderBinding;
import com.example.orderfood.model.Food;
import com.example.orderfood.model.Order;
import com.example.orderfood.utils.DatetimeUtils;
import com.example.orderfood.utils.StringUtils;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{

    private List<Order> mListOrder;

    public OrderAdapter(List<Order> mListOrder) {
        this.mListOrder = mListOrder;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding itemOrderBinding = ItemOrderBinding
                .inflate(LayoutInflater.from(parent.getContext()),parent, false);
        return new OrderViewHolder(itemOrderBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = mListOrder.get(position);
        if (order == null) {
            return;
        }
        holder.mItemOrderBinding.tvWaiter.setText(order.getUserName());
        holder.mItemOrderBinding.tvMenu.setText(getStringListFoodsOrder(order.getFoodList()));
        holder.mItemOrderBinding.tvDate.setText(DatetimeUtils.convertTimeStampToDate(order.getId()));

    }

    @Override
    public int getItemCount() {
        if (mListOrder != null) {
            return mListOrder.size();
        }
        return 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{
        private final ItemOrderBinding mItemOrderBinding;

        public OrderViewHolder(ItemOrderBinding mItemOrderBinding) {
            super(mItemOrderBinding.getRoot());
            this.mItemOrderBinding = mItemOrderBinding;
        }
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
}
