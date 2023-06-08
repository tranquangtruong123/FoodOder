package com.example.tqtruongcnpmct2.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tqtruongcnpmct2.ControllerApplication;
import com.example.tqtruongcnpmct2.R;
import com.example.tqtruongcnpmct2.activity.FoodDetailActivity;
import com.example.tqtruongcnpmct2.activity.MainActivity;
import com.example.tqtruongcnpmct2.adapter.FoodGridAdapter;
import com.example.tqtruongcnpmct2.adapter.FoodPopularAdapter;
import com.example.tqtruongcnpmct2.constant.Constant;
import com.example.tqtruongcnpmct2.constant.GlobalFuntion;
import com.example.tqtruongcnpmct2.databinding.FragmentHomeBinding;
import com.example.tqtruongcnpmct2.model.Food;
import com.example.tqtruongcnpmct2.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding mFragmentHomeBinding;

    private List<Food> mListFood;
    private FoodPopularAdapter mFoodPopularAdapter;
    private FoodGridAdapter mFoodGridAdapter;

    private final Handler mHandlerBanner = new Handler();
    private final Runnable mRunnableBanner = new Runnable() {
        @Override
        public void run() {
            if (mListFood == null || mListFood.isEmpty()) {
                return;
            }
            if (mFragmentHomeBinding.viewpager2.getCurrentItem() == mListFood.size() - 1) {
                mFragmentHomeBinding.viewpager2.setCurrentItem(0);
                return;
            }
            mFragmentHomeBinding.viewpager2.setCurrentItem(mFragmentHomeBinding.viewpager2.getCurrentItem() + 1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);

        getListFoodFromFirebase("");
        initListener();
        return mFragmentHomeBinding.getRoot();
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(true, getString(R.string.home));
        }
    }

    private void initListener() {
        mFragmentHomeBinding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    if (mListFood != null) mListFood.clear();
                    getListFoodFromFirebase("");
                }
            }
        });

        mFragmentHomeBinding.imgSearch.setOnClickListener(view -> searchFood());

        mFragmentHomeBinding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchFood();
                return true;
            }
            return false;
        });
    }

    private void displayListFoodPopular() {
        mFoodPopularAdapter = new FoodPopularAdapter(getListFoodPopular(), this::goToFoodDetail);
        mFragmentHomeBinding.viewpager2.setAdapter(mFoodPopularAdapter);
        mFragmentHomeBinding.indicator3.setViewPager(mFragmentHomeBinding.viewpager2);

        mFragmentHomeBinding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandlerBanner.removeCallbacks(mRunnableBanner);
                mHandlerBanner.postDelayed(mRunnableBanner, 3000);
            }
        });
    }

    private void displayListFoodSuggest() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        mFragmentHomeBinding.rcvFood.setLayoutManager(gridLayoutManager);

        mFoodGridAdapter = new FoodGridAdapter(mListFood, this::goToFoodDetail);
        mFragmentHomeBinding.rcvFood.setAdapter(mFoodGridAdapter);
    }

    private List<Food> getListFoodPopular() {
        List<Food> list = new ArrayList<>();
        if (mListFood == null || mListFood.isEmpty()) {
            return list;
        }
        for (Food food : mListFood) {
            if (food.isPopular()) {
                list.add(food);
            }
        }
        return list;
    }

    private void getListFoodFromFirebase(String key) {
        if (getActivity() == null) {
            return;
        }
        ControllerApplication.get(getActivity()).getFoodDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFragmentHomeBinding.layoutContent.setVisibility(View.VISIBLE);
                mFragmentHomeBinding.txtview.setVisibility(View.VISIBLE);
                mFragmentHomeBinding.notitxt.setVisibility(View.GONE);
                mListFood = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    if (food == null) {
                        return;
                    }

                    if (StringUtil.isEmpty(key)) {
                        mListFood.add(0, food);
                    } else {
                        if (GlobalFuntion.getTextSearch(food.getName()).toLowerCase().trim()
                                .contains(GlobalFuntion.getTextSearch(key).toLowerCase().trim())) {
                            mListFood.add(0, food);
                        }
                        if(mListFood.isEmpty()){
                            mFragmentHomeBinding.txtview.setVisibility(View.GONE);
                            mFragmentHomeBinding.notitxt.setVisibility(View.VISIBLE);
                        }
                    }

                }
                displayListFoodPopular();
                displayListFoodSuggest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void searchFood() {
        String strKey = mFragmentHomeBinding.edtSearchName.getText().toString().trim();
        if (mListFood != null) mListFood.clear();
        getListFoodFromFirebase(strKey);
        GlobalFuntion.hideSoftKeyboard(getActivity());
    }

    private void goToFoodDetail(@NonNull Food food) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_FOOD_OBJECT, food);
        GlobalFuntion.startActivity(getActivity(), FoodDetailActivity.class, bundle);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandlerBanner.removeCallbacks(mRunnableBanner);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandlerBanner.postDelayed(mRunnableBanner, 3000);
    }
}
