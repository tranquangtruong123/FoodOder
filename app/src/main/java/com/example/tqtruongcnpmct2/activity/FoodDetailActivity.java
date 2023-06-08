package com.example.tqtruongcnpmct2.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.tqtruongcnpmct2.R;
import com.example.tqtruongcnpmct2.adapter.MoreImageAdapter;
import com.example.tqtruongcnpmct2.constant.Constant;
import com.example.tqtruongcnpmct2.database.FoodDatabase;
import com.example.tqtruongcnpmct2.databinding.ActivityFoodDetailBinding;
import com.example.tqtruongcnpmct2.event.ReloadListCartEvent;
import com.example.tqtruongcnpmct2.model.Food;
import com.example.tqtruongcnpmct2.utils.GlideUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class FoodDetailActivity extends BaseActivity {

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
        if (bundle != null) {
            mFood = (Food) bundle.get(Constant.KEY_INTENT_FOOD_OBJECT);
        }
    }

    private void initToolbar() {
        if(mFood.getCondition().equals("Hết Đơn")){
            mActivityFoodDetailBinding.tvCodition.setVisibility(View.VISIBLE);
            mActivityFoodDetailBinding.tvCodition.setText("Hết Đơn");
        }
        else {
            mActivityFoodDetailBinding.tvCodition.setVisibility(View.GONE);
        }
        mActivityFoodDetailBinding.toolbar.imgBack.setVisibility(View.VISIBLE);
        mActivityFoodDetailBinding.toolbar.imgCart.setVisibility(View.VISIBLE);
        mActivityFoodDetailBinding.toolbar.tvTitle.setText(getString(R.string.food_detail_title));

        mActivityFoodDetailBinding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
    }

    private void setDataFoodDetail() {
        if (mFood == null) {
            return;
        }

        GlideUtils.loadUrlBanner(mFood.getBanner(), mActivityFoodDetailBinding.imageFood);
        if (mFood.getSale() <= 0) {
            mActivityFoodDetailBinding.tvSaleOff.setVisibility(View.GONE);
            mActivityFoodDetailBinding.tvPrice.setVisibility(View.GONE);

            String strPrice = mFood.getPrice() + Constant.CURRENCY;
            mActivityFoodDetailBinding.tvPriceSale.setText(strPrice);
        } else {
            mActivityFoodDetailBinding.tvSaleOff.setVisibility(View.VISIBLE);
            mActivityFoodDetailBinding.tvPrice.setVisibility(View.VISIBLE);

            String strSale = "Giảm " + mFood.getSale() + "%";
            mActivityFoodDetailBinding.tvSaleOff.setText(strSale);

            String strPriceOld = mFood.getPrice() + Constant.CURRENCY;
            mActivityFoodDetailBinding.tvPrice.setText(strPriceOld);
            mActivityFoodDetailBinding.tvPrice.setPaintFlags(mActivityFoodDetailBinding.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            String strRealPrice = mFood.getRealPrice() + Constant.CURRENCY;
            mActivityFoodDetailBinding.tvPriceSale.setText(strRealPrice);
        }
        mActivityFoodDetailBinding.tvFoodName.setText(mFood.getName());
        mActivityFoodDetailBinding.tvFoodDescription.setText(mFood.getDescription());

        displayListMoreImages();

        setStatusButtonAddToCart();
    }

    private void displayListMoreImages() {
        if (mFood.getImages() == null || mFood.getImages().isEmpty()) {
            mActivityFoodDetailBinding.tvMoreImageLabel.setVisibility(View.GONE);
            return;
        }
        mActivityFoodDetailBinding.tvMoreImageLabel.setVisibility(View.VISIBLE);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mActivityFoodDetailBinding.rcvImages.setLayoutManager(gridLayoutManager);

        MoreImageAdapter moreImageAdapter = new MoreImageAdapter(mFood.getImages());
        mActivityFoodDetailBinding.rcvImages.setAdapter(moreImageAdapter);
    }

    private void setStatusButtonAddToCart() {
        if (isFoodInCart()) {
            mActivityFoodDetailBinding.tvAddToCart.setBackgroundResource(R.drawable.bg_gray_shape_corner_6);
            mActivityFoodDetailBinding.tvAddToCart.setText(getString(R.string.added_to_cart));
            mActivityFoodDetailBinding.tvAddToCart.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary));
            mActivityFoodDetailBinding.toolbar.imgCart.setVisibility(View.GONE);
        } else {
            mActivityFoodDetailBinding.tvAddToCart.setBackgroundResource(R.drawable.bg_green_shape_corner_6);
            mActivityFoodDetailBinding.tvAddToCart.setText(getString(R.string.add_to_cart));
            mActivityFoodDetailBinding.tvAddToCart.setTextColor(ContextCompat.getColor(this, R.color.white));
            mActivityFoodDetailBinding.toolbar.imgCart.setVisibility(View.VISIBLE);
        }
    }

    private boolean isFoodInCart() {
        List<Food> list = FoodDatabase.getInstance(this).foodDAO().checkFoodInCart(mFood.getId());
        return list != null && !list.isEmpty();
    }

    private void initListener() {
            mActivityFoodDetailBinding.tvAddToCart.setOnClickListener(v -> onClickAddToCart());
            mActivityFoodDetailBinding.toolbar.imgCart.setOnClickListener(v -> onClickAddToCart());
    }

    public void onClickAddToCart() {
        if(mFood.getCondition().equals("Hết Đơn")){
            Toast.makeText(this, "Xin Lỗi! Món Ăn Đã Hết.Quay Lại Sau", Toast.LENGTH_SHORT).show();
        }
        else{
            if (isFoodInCart()) {
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
            String strTotalPrice = totalPrice + Constant.CURRENCY;
            tvFoodPriceCart.setText(strTotalPrice);

            mFood.setCount(1);
            mFood.setTotalPrice(totalPrice);

            tvSubtractCount.setOnClickListener(v -> {
                int count = Integer.parseInt(tvCount.getText().toString());
                if (count <= 1) {
                    return;
                }
                int newCount = Integer.parseInt(tvCount.getText().toString()) - 1;
                tvCount.setText(String.valueOf(newCount));
                int totalPrice1 = mFood.getRealPrice() * newCount;
                String strTotalPrice1 = totalPrice1 + Constant.CURRENCY;
                tvFoodPriceCart.setText(strTotalPrice1);
                mFood.setCount(newCount);
                mFood.setTotalPrice(totalPrice1);
            });

            tvAddCount.setOnClickListener(v -> {
                int newCount = Integer.parseInt(tvCount.getText().toString()) + 1;
                tvCount.setText(String.valueOf(newCount));

                int totalPrice2 = mFood.getRealPrice() * newCount;
                String strTotalPrice2 = totalPrice2 + Constant.CURRENCY;
                tvFoodPriceCart.setText(strTotalPrice2);

                mFood.setCount(newCount);
                mFood.setTotalPrice(totalPrice2);
            });

            tvCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

            tvAddCart.setOnClickListener(v -> {
                FoodDatabase.getInstance(FoodDetailActivity.this).foodDAO().insertFood(mFood);
                bottomSheetDialog.dismiss();
                setStatusButtonAddToCart();
                EventBus.getDefault().post(new ReloadListCartEvent());
            });

            bottomSheetDialog.show();
        }

    }
}