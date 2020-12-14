package com.example.more.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.more.Application.AppController;
import com.example.more.R;
import com.example.more.databinding.ShowImageActivityBinding;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class ShowImagePlayVideoActivity extends AppCompatActivity {
    /*
     * I am using DataBinding
     * */
    ShowImageActivityBinding binding;
    @Inject
    public SimpleExoPlayer simpleExoPlayer;

    @Inject
    DataSource.Factory dataSourceFactory;

    @Inject
    SimpleCache simpleCache;

    @Inject
    CacheDataSourceFactory cacheDataSourceFactory;

    public static void openActivity(Context ctx, String url, boolean isImage) {
        Intent intent = new Intent(ctx, ShowImagePlayVideoActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("isImage", isImage);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Remember in our {@link com.example.more.di.module.ActivityModule}, we
         * defined {@link ListActivity} injection? So we need
         * to call this method in order to inject the
         * ViewModelFactory into our Activity
         * */
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_image);
        binding.setUrl(getIntent().getStringExtra("url"));
        binding.setIsImage(getIntent().getBooleanExtra("isImage", true));
        if(!binding.getIsImage())
        setUpPlayerView();
    }

    void setUpPlayerView() {
        ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource(new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(binding.getUrl())));
        simpleExoPlayer.prepare(concatenatedSource);
        //binding.playerView.setUseController(false);
        simpleExoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        binding.playerView.setPlayer(simpleExoPlayer);
        binding.playerView.setKeepScreenOn(true);
        binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!binding.getIsImage()){
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        if(!binding.getIsImage()){
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!binding.getIsImage()){
            simpleExoPlayer.setPlayWhenReady(true);
            if(!binding.getIsImage()){
                binding.playerView.setPlayer(null);
                simpleExoPlayer.stop();
            }
        }
    }
}
