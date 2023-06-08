package com.example.tqtruongcnpmct2.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tqtruongcnpmct2.constant.Constant;
import com.example.tqtruongcnpmct2.databinding.ItemFoodGridBinding;
import com.example.tqtruongcnpmct2.listener.IOnClickFoodItemListener;
import com.example.tqtruongcnpmct2.model.Food;
import com.example.tqtruongcnpmct2.utils.GlideUtils;

import java.util.List;

public class FoodGridAdapter extends RecyclerView.Adapter<FoodGridAdapter.FoodGridViewHolder> {

    private final List<Food> mListFoods;
    public final IOnClickFoodItemListener iOnClickFoodItemListener;

    public FoodGridAdapter(List<Food> mListFoods, IOnClickFoodItemListener iOnClickFoodItemListener) {
        this.mListFoods = mListFoods;
        this.iOnClickFoodItemListener = iOnClickFoodItemListener;
    }

    @NonNull
    @Override
    public FoodGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodGridBinding itemFoodGridBinding = ItemFoodGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodGridViewHolder(itemFoodGridBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodGridViewHolder holder, int position) {
        Food food = mListFoods.get(position);
        if (food == null) {
            return;
        }
        GlideUtils.loadUrl(food.getImage(), holder.mItemFoodGridBinding.imgFood);
        if (food.getSale() <= 0) {
            holder.mItemFoodGridBinding.tvSaleOff.setVisibility(View.GONE);
            holder.mItemFoodGridBinding.tvPrice.setVisibility(View.GONE);

            String strPrice = food.getPrice() + Constant.CURRENCY;
            holder.mItemFoodGridBinding.tvPriceSale.setText(strPrice);
        } else {
            holder.mItemFoodGridBinding.tvSaleOff.setVisibility(View.VISIBLE);
            holder.mItemFoodGridBinding.tvPrice.setVisibility(View.VISIBLE);

            String strSale = "Giảm " + food.getSale() + "%";
            holder.mItemFoodGridBinding.tvSaleOff.setText(strSale);

            String strOldPrice = food.getPrice() + Constant.CURRENCY;
            holder.mItemFoodGridBinding.tvPrice.setText(strOldPrice);
            holder.mItemFoodGridBinding.tvPrice.setPaintFlags(holder.mItemFoodGridBinding.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            String strRealPrice = food.getRealPrice() + Constant.CURRENCY;
            holder.mItemFoodGridBinding.tvPriceSale.setText(strRealPrice);
        }
        if(food.getCondition().equals("Hết Đơn")){
            holder.mItemFoodGridBinding.tvCodition.setVisibility(View.VISIBLE);
            holder.mItemFoodGridBinding.tvCodition.setText("Hết Đơn");
        }
        else {
            holder.mItemFoodGridBinding.tvCodition.setVisibility(View.GONE);
        }
        holder.mItemFoodGridBinding.tvFoodName.setText(food.getName());

        holder.mItemFoodGridBinding.layoutItem.setOnClickListener(v -> iOnClickFoodItemListener.onClickItemFood(food));
    }

    @Override
    public int getItemCount() {
        return null == mListFoods ? 0 : mListFoods.size();
    }

    public static class FoodGridViewHolder extends RecyclerView.ViewHolder {

        private final ItemFoodGridBinding mItemFoodGridBinding;

        public FoodGridViewHolder(ItemFoodGridBinding itemFoodGridBinding) {
            super(itemFoodGridBinding.getRoot());
            this.mItemFoodGridBinding = itemFoodGridBinding;
        }
    }
}
