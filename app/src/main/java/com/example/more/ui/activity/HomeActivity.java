package com.example.more.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.more.Application.AppController;
import com.example.more.R;
import com.example.more.databinding.ScrollingActivityBinding;
import com.example.more.databinding.TabItemBinding;
import com.example.more.ui.adapter.PagerAdapter;
import com.example.more.ui.fragment.ContentFragment;
import com.example.more.utills.Utils;
import com.google.android.gms.ads.MobileAds;

import static com.example.more.utills.Screen.dp;

public class HomeActivity extends AppCompatActivity {
    /*
     * I am using DataBinding
     * */
    private ScrollingActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this);
        AppController.getInstance().checkFCMUpdate();
        initialiseView();
        initAdMob();

    }

    /**
     * Method to initialize admob sdk to show ads
     */
    public void initAdMob() {
        MobileAds.initialize(this, getString(R.string.ADMOB_APP_ID));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
           //     Utils.buildRewardedAd(HomeActivity.this);
            }
        }, 3000);
        Utils.buildBannerAD(binding.includedLayout.adView);
    }


    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scrolling);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupViewPager();
    }

    /**
     * Method to configure {@link androidx.viewpager.widget.ViewPager}
     * and {@link com.google.android.material.tabs.TabLayout}
     *
     * @see PagerAdapter
     * <p>
     * Basically we add two {@link ContentFragment} here
     * one for Contents and other for tools
     */
    void setupViewPager() {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        TabItemBinding tabItemBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.tab_item, (ViewGroup) binding.includedLayout.viewpager.getRootView(), false);
        tabItemBinding.tabLogo.setImageResource(R.drawable.ic_cup_of_hot_chocolate);

        TabItemBinding tabItemBinding2 = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.tab_item, (ViewGroup) binding.includedLayout.viewpager.getRootView(), false);
        int pad = dp(2);
        ((ViewGroup.MarginLayoutParams) tabItemBinding.tabLogo.getLayoutParams()).leftMargin = dp(3.5f);
        tabItemBinding2.tabLogo.setPadding(pad, dp(6), pad, 0);
        tabItemBinding2.tabLogo.setImageResource(R.drawable.ic_handyman_tools);

        adapter.addFrag(ContentFragment.getInstance(false), "");
        adapter.addFrag(ContentFragment.getInstance(true), "");
        binding.includedLayout.viewpager.setAdapter(adapter);
        binding.includedLayout.tabLayout.setupWithViewPager(binding.includedLayout.viewpager);

        binding.includedLayout.tabLayout.getTabAt(0).setCustomView(tabItemBinding.getRoot());
        binding.includedLayout.tabLayout.getTabAt(1).setCustomView(tabItemBinding2.getRoot());
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notification) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
