package com.example.tqtruongcnpmct2.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.tqtruongcnpmct2.R;
import com.example.tqtruongcnpmct2.adapter.MainViewPagerAdapter;
import com.example.tqtruongcnpmct2.databinding.ActivityMainBinding;
import com.example.tqtruongcnpmct2.utils.Utils;
import com.example.tqtruongcnpmct2.widget.NotiFication;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class MainActivity extends BaseActivity  {

    private ActivityMainBinding mActivityMainBinding;
    private LoginActivity loginActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mActivityMainBinding.getRoot());
        mActivityMainBinding.viewpager2.setUserInputEnabled(false);
        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(this);
        mActivityMainBinding.viewpager2.setAdapter(mainViewPagerAdapter);
        loginActivity = new LoginActivity();
        loginActivity.finish();
        mActivityMainBinding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.nav_home).setChecked(true);
                        break;

                    case 1:
                        mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.nav_cart).setChecked(true);
                        break;
                    case 2:
                        mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.nav_contact).setChecked(true);
                        break;
                    case 3:
                        mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.nav_order).setChecked(true);
                        break;
                    case 4:
                        mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.nav_profile).setChecked(true);
                        break;
                }
            }
        });
        mActivityMainBinding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                mActivityMainBinding.viewpager2.setCurrentItem(0);
            } else if (id == R.id.nav_cart) {
                mActivityMainBinding.viewpager2.setCurrentItem(1);
            } else if (id == R.id.nav_contact) {
                mActivityMainBinding.viewpager2.setCurrentItem(2);
            } else if (id == R.id.nav_order) {
                mActivityMainBinding.viewpager2.setCurrentItem(3);
            } else if (id == R.id.nav_profile) {
                mActivityMainBinding.viewpager2.setCurrentItem(4);
            }
            return true;
        });
    }



    public  String getEmail(){
        return getIntent().getStringExtra("key_email");
    }

    @Override
    public void onBackPressed() {
        showConfirmExitApp();
    }

    private void showConfirmExitApp() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.app_name))
                .content(getString(R.string.msg_exit_app))
                .positiveText(getString(R.string.action_ok))
                .onPositive((dialog, which) -> finish())
                .negativeText(getString(R.string.action_cancel))
                .cancelable(false)
                .show();
    }

    public void setToolBar(boolean isHome, String title) {
        if (isHome) {
            mActivityMainBinding.toolbar.layoutToolbar.setVisibility(View.GONE);
            return;
        }
        mActivityMainBinding.toolbar.layoutToolbar.setVisibility(View.VISIBLE);
        mActivityMainBinding.toolbar.tvTitle.setText(title);
    }

}