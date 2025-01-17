package com.example.more.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.transition.Visibility;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.example.more.R;
import com.example.more.databinding.FacebookActivityBinding;
import com.example.more.ui.fragment.FacebookFragment;
import com.example.more.utills.Screen;
import com.example.more.utills.Utils;
import com.google.android.material.appbar.AppBarLayout;

/**
 * Activity host {@link FacebookFragment} to download facebook videos
 *
 * @see  {@link DownloadManagerActivity}
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.buildInterstitialAd(FacebookActivity.this);
            }
        },1000);
        //Utils.buildBannerAD(binding.adView);
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
        binding.nsv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                fragment.binding.webView.onTouchEvent(event);
                return false;
            }
        });

        binding.appBar.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                System.out.println("kuku "+ (i<-80)+" "+i+" "+(Screen.dp(80)+i));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       binding.container.setPadding(0,Screen.dp(80)+i,0,0);;
                    }
                });

            }
        });
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
