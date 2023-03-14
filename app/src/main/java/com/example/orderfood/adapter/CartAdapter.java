package com.example.orderfood.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.constant.IConstant;
import com.example.orderfood.databinding.ItemCartBinding;
import com.example.orderfood.model.Food;
import com.example.orderfood.utils.GlideUtils;

import java.util.List;

public class CartAdapter  extends  RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    private final List<Food> mListFoods;
    private final IClickListener iClickListener;

    public CartAdapter(List<Food> mListFoods, IClickListener iClickListener) {
        this.mListFoods = mListFoods;
        this.iClickListener = iClickListener;
    }

    public interface IClickListener {
        void clickDeleteFood(Food food, int position);

        void updateItemFood(Food food, int position);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding itemCartBinding = ItemCartBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(itemCartBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Food food = mListFoods.get(position);
        if (food == null) {
            return;
        }
        GlideUtils.loadUrl(food.getImage(), holder.mItemCartBinding.imgFoodCart);
        holder.mItemCartBinding.tvFoodNameCart.setText(food.getName());

        String strFoodPriceCart = food.getPrice()*food.getCount() + IConstant.CURRENCY;
        if(food.getSale()>0){
            strFoodPriceCart = food.getRealPrice()*food.getCount() +IConstant.CURRENCY;
        }
        holder.mItemCartBinding.tvFoodPriceCart.setText(strFoodPriceCart);
        holder.mItemCartBinding.tvCount.setText(String.valueOf(food.getCount()));


        holder.mItemCartBinding.tvSubtract.setOnClickListener(view -> {

            int count = Integer.parseInt(holder.mItemCartBinding.tvCount.getText().toString());
            if(count<=1)
                return;
            int newCount = count-1;
            holder.mItemCartBinding.tvCount.setText(String.valueOf(newCount));

            int totalPrice = food.getRealPrice()*newCount;
            holder.mItemCartBinding.tvFoodPriceCart.setText(totalPrice+ IConstant.CURRENCY);

            food.setCount(newCount);
            food.setTotalPrice(totalPrice);

            iClickListener.updateItemFood(food, holder.getAdapterPosition());
        });
        holder.mItemCartBinding.tvAdd.setOnClickListener(view -> {
            int newCount = Integer.parseInt(holder.mItemCartBinding.tvCount.getText().toString().trim())+1;
            holder.mItemCartBinding.tvCount.setText(String.valueOf(newCount));

            int totalPrice = food.getRealPrice()*newCount;
            holder.mItemCartBinding.tvFoodPriceCart.setText(totalPrice+IConstant.CURRENCY);
            food.setCount(newCount);
            food.setTotalPrice(totalPrice);

            iClickListener.updateItemFood(food,holder.getAdapterPosition());
        });
        holder.mItemCartBinding.tvDelete.setOnClickListener(view
                -> iClickListener.clickDeleteFood(food, holder.getAdapterPosition()));


    }

    @Override
    public int getItemCount() {
        return null == mListFoods ? 0 : mListFoods.size();
    }

    public  static class CartViewHolder extends RecyclerView.ViewHolder{
        private final ItemCartBinding mItemCartBinding;


        public CartViewHolder(ItemCartBinding itemCartBinding) {
            super(itemCartBinding.getRoot());
            this.mItemCartBinding = itemCartBinding;
        }
    }
}
