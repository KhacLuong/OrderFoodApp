package com.example.orderfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.databinding.ItemFoodBannerBinding;

import com.example.orderfood.listener.IOnClickFoodItemListener;
import com.example.orderfood.model.Food;
import com.example.orderfood.utils.GlideUtils;

import java.util.List;

public class FoodBannerAdapter extends RecyclerView.Adapter<FoodBannerAdapter.FoodBannerViewHolder> {

    private final List<Food> mListFoods;
    public final IOnClickFoodItemListener iOnClickFoodItemListener;

    public FoodBannerAdapter(List<Food> mListFoods, IOnClickFoodItemListener iOnClickFoodItemListener) {
        this.mListFoods = mListFoods;
        this.iOnClickFoodItemListener = iOnClickFoodItemListener;
    }

    @NonNull
    @Override
    public FoodBannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodBannerBinding itemFoodBannerBinding = ItemFoodBannerBinding.inflate(LayoutInflater.from(parent.getContext()), parent,false);

        return new FoodBannerViewHolder(itemFoodBannerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodBannerViewHolder holder, int position) {
        Food food = mListFoods.get(position);
        if(food==null){
            return;
        }
        GlideUtils.loadUrlBanner(food.getBanner(), holder.mItemFoodBannerBinding.imgPhotoFoodViewpagerHome);
        if(food.getSale()<=0){
            holder.mItemFoodBannerBinding.tvSaleOffViewpagerHome.setVisibility(View.GONE);
        }else {
            holder.mItemFoodBannerBinding.tvSaleOffViewpagerHome.setVisibility(View.VISIBLE);
            String strSale = "Giảm " + food.getSale() +"%";
            holder.mItemFoodBannerBinding.tvSaleOffViewpagerHome.setText(strSale);

        }
        holder.mItemFoodBannerBinding.layoutItem.setOnClickListener(v -> iOnClickFoodItemListener.onClickItemFood(food));
    }

    @Override
    public int getItemCount() {
        if(mListFoods!=null)
            return mListFoods.size();
        return 0;
    }

    public static class FoodBannerViewHolder extends RecyclerView.ViewHolder{
        private final ItemFoodBannerBinding mItemFoodBannerBinding;

        public FoodBannerViewHolder(@NonNull ItemFoodBannerBinding itemFoodBannerBinding) {
            super(itemFoodBannerBinding.getRoot());
            this.mItemFoodBannerBinding = itemFoodBannerBinding;
        }
    }
}
