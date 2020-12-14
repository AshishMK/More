package com.example.more.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.transition.Visibility;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.more.R;
import com.example.more.databinding.YouTubeActivityBinding;
import com.example.more.ui.fragment.FacebookFragment;
import com.example.more.ui.fragment.YoutubeFragment;
import com.example.more.utills.Screen;
import com.example.more.utills.Utils;
import com.google.android.material.appbar.AppBarLayout;

/**
 * Activity host {@link FacebookFragment} to download facebook videos
 *
 */
public class YoutubeActivity extends AppCompatActivity {
    /*
     * I am using DataBinding
     * */
    private YouTubeActivityBinding binding;
    YoutubeFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseView();
        initAdMob();
    }

    public void setFabVisibility(boolean show){
        binding.fab.setVisibility(show?View.VISIBLE:View.GONE);
    }

    /**
     * Method to initialize admob sdk to show ads
     */
    public void initAdMob() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.buildInterstitialAd(YoutubeActivity.this);
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_youtube);
        // Performing window enter Activity transition animation
        getWindow().setEnterTransition(buildEnterTransition());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.title.setText(R.string.yt_downloader);
        fragment = ((YoutubeFragment) getSupportFragmentManager().findFragmentById(R.id.youtubeFragment));
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
            fragment.backCliked = true;
            fragment.binding.webView.goBack();
            binding.fab.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    public void onDownloadClicked(View view){
    fragment.onDownloadClicked();
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

    @Override
    protected void onPause() {
        super.onPause();
    }

}
