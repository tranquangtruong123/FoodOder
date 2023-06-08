package com.example.tqtruongcnpmct2.fragment;

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

import com.example.tqtruongcnpmct2.ControllerApplication;
import com.example.tqtruongcnpmct2.R;
import com.example.tqtruongcnpmct2.activity.MainActivity;
import com.example.tqtruongcnpmct2.adapter.CartAdapter;
import com.example.tqtruongcnpmct2.constant.Constant;
import com.example.tqtruongcnpmct2.constant.GlobalFuntion;
import com.example.tqtruongcnpmct2.database.FoodDatabase;
import com.example.tqtruongcnpmct2.databinding.FragmentCartBinding;
import com.example.tqtruongcnpmct2.event.ReloadListCartEvent;
import com.example.tqtruongcnpmct2.model.Food;
import com.example.tqtruongcnpmct2.model.Order;
import com.example.tqtruongcnpmct2.model.User;
import com.example.tqtruongcnpmct2.utils.GlideUtils;
import com.example.tqtruongcnpmct2.utils.StringUtil;
import com.example.tqtruongcnpmct2.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends BaseFragment {

    private FragmentCartBinding mFragmentCartBinding;
    private CartAdapter mCartAdapter;
    private List<Food> mListFoodCart;
    private int mAmount;
    private MainActivity mainActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentCartBinding = FragmentCartBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        onClickOrderCart(mainActivity.getEmail());
        displayListFoodInCart();
        mFragmentCartBinding.tvOrderCart.setOnClickListener(v -> onClickOrderCart(mainActivity.getEmail()));

        return mFragmentCartBinding.getRoot();
    }



    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(false, getString(R.string.cart));
        }
    }

    private void displayListFoodInCart() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentCartBinding.rcvFoodCart.setLayoutManager(linearLayoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        mFragmentCartBinding.rcvFoodCart.addItemDecoration(itemDecoration);

        initDataFoodCart();
    }

    private void initDataFoodCart() {
        mListFoodCart = new ArrayList<>();
        mListFoodCart = FoodDatabase.getInstance(getActivity()).foodDAO().getListFoodCart();
        if (mListFoodCart == null || mListFoodCart.isEmpty()) {
            return;
        }

        mCartAdapter = new CartAdapter(mListFoodCart, new CartAdapter.IClickListener() {
            @Override
            public void clickDeteteFood(Food food, int position) {
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

    @SuppressLint("NotifyDataSetChanged")
    private void clearCart() {
        if (mListFoodCart != null) {
            mListFoodCart.clear();
        }
        mCartAdapter.notifyDataSetChanged();
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        List<Food> listFoodCart = FoodDatabase.getInstance(getActivity()).foodDAO().getListFoodCart();
        if (listFoodCart == null || listFoodCart.isEmpty()) {
            String strZero = 0 + Constant.CURRENCY;
            mFragmentCartBinding.tvTotalPrice.setText(strZero);
            mAmount = 0;
            return;
        }

        int totalPrice = 0;
        for (Food food : listFoodCart) {
            totalPrice = totalPrice + food.getTotalPrice();
        }

        String strTotalPrice = totalPrice + Constant.CURRENCY;
        mFragmentCartBinding.tvTotalPrice.setText(strTotalPrice);
        mAmount = totalPrice;
    }

    private void deleteFoodFromCart(Food food, int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.confirm_delete_food))
                .setMessage(getString(R.string.message_delete_food))
                .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                    FoodDatabase.getInstance(getActivity()).foodDAO().deleteFood(food);
                    mListFoodCart.remove(position);
                    mCartAdapter.notifyItemRemoved(position);

                    calculateTotalPrice();
                })
                .setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void onClickOrderCart(String email) {
        if (getActivity() == null) {
            return;
        }

        if (mListFoodCart == null || mListFoodCart.isEmpty()) {
            return;
        }

        View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_order, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        // init ui
        TextView tvFoodsOrder = viewDialog.findViewById(R.id.tv_foods_order);
        TextView tvPriceOrder = viewDialog.findViewById(R.id.tv_price_order);
        TextView edtNameOrder = viewDialog.findViewById(R.id.edt_name_order);
        TextView edtPhoneOrder = viewDialog.findViewById(R.id.edt_phone_order);
        TextView edtAddressOrder = viewDialog.findViewById(R.id.edt_address_order);
        TextView tvCancelOrder = viewDialog.findViewById(R.id.tv_cancel_order);
        TextView tvCreateOrder = viewDialog.findViewById(R.id.tv_create_order);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getValue(User.class).getEmail().equals(email)){
                        User user = child.getValue(User.class);
                        edtNameOrder.setText(user.getFullname()+"");
                        edtAddressOrder.setText(user.getAdress()+"");
                       edtPhoneOrder.setText(user.getPhoneNumber());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Set data
        tvFoodsOrder.setText(getStringListFoodsOrder());
        tvPriceOrder.setText(mFragmentCartBinding.tvTotalPrice.getText().toString());

        // Set listener
        tvCancelOrder.setOnClickListener(v -> bottomSheetDialog.dismiss());

        tvCreateOrder.setOnClickListener(v -> {
            String strName = edtNameOrder.getText().toString().trim();
            String strPhone = edtPhoneOrder.getText().toString().trim();
            String strAddress = edtAddressOrder.getText().toString().trim();

            if (StringUtil.isEmpty(strName) || StringUtil.isEmpty(strPhone) || StringUtil.isEmpty(strAddress)) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.message_enter_infor_order));
            } else {
                long id = System.currentTimeMillis();
                Order order = new Order(id, strName, strPhone, strAddress,"Chờ Duyệt",
                        mAmount, getStringListFoodsOrder(), Constant.TYPE_PAYMENT_CASH);
                ControllerApplication.get(getActivity()).getBookingDatabaseReference()
                        .child(Utils.getDeviceId(getActivity()))
                        .child(String.valueOf(id))
                        .setValue(order, (error1, ref1) -> {
                            GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_order_success));
                            GlobalFuntion.hideSoftKeyboard(getActivity());
                            bottomSheetDialog.dismiss();

                            FoodDatabase.getInstance(getActivity()).foodDAO().deleteAllFood();
                            clearCart();
                        });
            }
        });

        bottomSheetDialog.show();
    }

    private String getStringListFoodsOrder() {
        if (mListFoodCart == null || mListFoodCart.isEmpty()) {
            return "";
        }
        String result = "";
        for (Food food : mListFoodCart) {
            if (StringUtil.isEmpty(result)) {
                result = "- " + food.getName() + " (" + food.getRealPrice() + Constant.CURRENCY + ") "
                        + "- " + getString(R.string.quantity) + " " + food.getCount();
            } else {
                result = result + "\n" + "- " + food.getName() + " (" + food.getRealPrice() + Constant.CURRENCY + ") "
                        + "- " + getString(R.string.quantity) + " " + food.getCount();
            }
        }
        return result;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReloadListCartEvent event) {
        displayListFoodInCart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
