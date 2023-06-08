package com.example.tqtruongcnpmct2.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tqtruongcnpmct2.fragment.CartFragment;
import com.example.tqtruongcnpmct2.fragment.ContactFragment;
import com.example.tqtruongcnpmct2.fragment.ProfileFragment;
import com.example.tqtruongcnpmct2.fragment.HomeFragment;
import com.example.tqtruongcnpmct2.fragment.OrderFragment;

public class MainViewPagerAdapter extends FragmentStateAdapter {

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();

            case 1:
                return new CartFragment();

            case 2:
                return new ContactFragment();
            case 3:
                return new OrderFragment();
            case 4:
                return new ProfileFragment();

            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
