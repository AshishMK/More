package com.example.more.ui.activity;

import android.os.Bundle;
import android.transition.Slide;
import android.transition.Visibility;
import android.view.Gravity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.more.R;
import com.example.more.databinding.FacebookActivityBinding;
import com.example.more.ui.fragment.FacebookFragment;
import com.example.more.utills.Utils;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Activity host {@link FacebookFragment} to download facebook videos
 *
 * @see also {@link DownloadManagerActivity}
 */
public class FacebookActivity extends AppCompatActivity {
    /*
     * I am using DataBinding
     * */
    private FacebookActivityBinding binding;
    FacebookFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseView();
        initAdMob(); 
    }
    /**
     * Method to initialize admob sdk to show ads
     */
    public void initAdMob() {
        MobileAds.initialize(this, getString(R.string.ADMOB_APP_ID));
        Utils.buildBannerAD(binding.adView);
        Utils.buildInterstitialAd(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_facebook);
        // Performing window enter Activity transition animation
        getWindow().setEnterTransition(buildEnterTransition());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.title.setText(R.string.facebook);
        fragment = ((FacebookFragment) getSupportFragmentManager().findFragmentById(R.id.facebookFragment));
    }

    @Override
    public void onBackPressed() {

        if (fragment.binding.webView.canGoBack()) {
            fragment.binding.webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    /**
     * @return method to create and return explode transition for activity window
     */
    private Visibility buildEnterTransition() {
        Slide enterTransition = new Slide();
        enterTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        enterTransition.setSlideEdge(Gravity.RIGHT);
        return enterTransition;
    }
}
