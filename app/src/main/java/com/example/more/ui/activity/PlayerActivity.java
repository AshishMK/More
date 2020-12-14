package com.example.more.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.viewpager.widget.ViewPager;

import com.example.more.Application.AppController;
import com.example.more.BuildConfig;
import com.example.more.R;
import com.example.more.data.local.pref.PreferencesStorage;
import com.example.more.data.local.pref.SharedPrefStorage;
import com.example.more.data.remote.model.VideoEntity;
import com.example.more.databinding.PlayerActivityBinding;
import com.example.more.ui.adapter.PlayerAdapter;
import com.example.more.ui.fragment.PlayerFragment;
import com.example.more.utills.AlertDialogProvider;
import com.example.more.utills.Utils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.example.more.di.module.ApiModule.base_url_download;
import static com.example.more.utills.AlertDialogProvider.TYPE_NORMAL;
import static com.example.more.utills.Utils.ADMOB_TEST_DEVICE;

enum MediaCategory {
    ANIMAL,
    HUMAN,
    IMAGE
}

public class PlayerActivity extends AppCompatActivity implements PlayerActivityHandler {
    @Inject
    public SimpleExoPlayer simpleExoPlayer;

    @Inject
    DataSource.Factory dataSourceFactory;

    @Inject
    SimpleCache simpleCache;
    /**
     * Provide {@link android.content.SharedPreferences} operations
     */
    @Inject
    PreferencesStorage preferenceStorage;
    @Inject
    CacheDataSourceFactory cacheDataSourceFactory;

    Handler handlerLoader = new Handler();

    PlayerAdapter pagerAdapter = null;
    long playerPosition = C.TIME_UNSET;

    ConcatenatingMediaSource concatenatedSource = null;
    Boolean playerBinded = false;
    int lastIndex = 0;

    PlayerActivityBinding binding;

    /**
     * Handler for visibility toggles
     */
    Handler handler = new Handler();
    /**
     * Media List
     */
    ArrayList<VideoEntity> mediaList = new ArrayList<>();

    ObservableBoolean showControls = new ObservableBoolean(false);
    ObservableInt mediaCatXml = new ObservableInt(0);
    ObservableInt themeMode = new ObservableInt(0);

    MediaCategory mediaCategory = MediaCategory.ANIMAL;

    VideoEntity entity = new VideoEntity();

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
        getSupportActionBar().hide();
        initRewardedAd();
        mediaCategory = MediaCategory.values()[getIntent().getIntExtra("category", 0)];
        mediaList.addAll(getIntent().getParcelableArrayListExtra("list"));

        pagerAdapter = new PlayerAdapter(getSupportFragmentManager());
        initialiseView();
        setEmojiParams(mediaCategory != MediaCategory.IMAGE);
        binding.pager.setAdapter(pagerAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setUpPlayer();
            }
        }, 1000);

    }

    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player);
        binding.setHandler(this);
        binding.setMediaCat(mediaCatXml);
        binding.setThemeMode(themeMode);
        binding.setShowControls(showControls);
        binding.setEntity(entity);
        binding.descriptionDetail.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void setUpPlayer() {
        getPlayList();
        simpleExoPlayer.prepare(concatenatedSource);
        simpleExoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        binding.pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setPageValues(position, true, false);

                if (mediaCategory == MediaCategory.IMAGE) {
                    //showAd()
                    return;
                }
                if (position < lastIndex && simpleExoPlayer.hasPrevious()) {
                    simpleExoPlayer.previous();
                } else if (position > lastIndex && simpleExoPlayer.hasNext()) {
                    simpleExoPlayer.next();

                    //  simpleExoPlayer.getCurrentTimeline().nex
                }
                playerBinded = false;
                PlayerFragment playerFragment = (PlayerFragment) pagerAdapter.getItem(lastIndex);
                if (playerFragment != null) playerFragment.unBindPlayer();
                playerFragment = (PlayerFragment) pagerAdapter.getItem(position);
                if (playerFragment != null) playerFragment.showThumb(mediaList.get(position).thumb);
                lastIndex = position;

                if (position % 4 == 0) {
                    // Utils.buildRewardedAd(getActivity())
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }


        });

        if (pagerAdapter != null && pagerAdapter.getCount() > 0) {
            PlayerFragment playerFragment = (PlayerFragment) pagerAdapter.getItem(0);
            playerFragment.showThumb(mediaList.get(0).thumb);
        }
        setPageValues(0, true, true);
    }

    void getPlayList() {
        simpleExoPlayer.setPlayWhenReady(true);
        //saveCache(list).subscribe()
        MediaSource[] tmp = new MediaSource[mediaList.size()];
        int i = 0;
        for (VideoEntity entity : mediaList) {
            tmp[i] = new ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                    .createMediaSource(Uri.parse(base_url_download + entity.url));//proxyServer.getProxyUrl(base_url_download + entity.getVideo())));
            PlayerFragment frg = new PlayerFragment();
            Bundle b = new Bundle();
            b.putParcelable("videoEntity", entity);
            frg.setArguments(b);
            //  if(!isLoadingMore){
            pagerAdapter.addFrag(frg, "s" + i);
            //}
            System.out.println("tikona c " + i + " " + (tmp[0]));
            i++;
        }

        //LoopingMediaSource loopingSources = new LoopingMediaSource(secondSource);
        if (concatenatedSource == null) {
            concatenatedSource = new ConcatenatingMediaSource(tmp);
        } else {
            concatenatedSource.addMediaSources(Arrays.asList(tmp));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.requestFullScreenIfLandscape(this);
        if (mediaCategory != MediaCategory.IMAGE) {
            simpleExoPlayer.addListener(listener);
        /*playerBinded = false;

        if (concatenatedSource != null)
            simpleExoPlayer.prepare(concatenatedSource);
        if (binding.pager.getCurrentItem() > 0 || playerPosition != C.TIME_UNSET)
            simpleExoPlayer.seekTo(binding.pager.getCurrentItem(), playerPosition);
        simpleExoPlayer.setPlayWhenReady(true);

        */
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            if (pagerAdapter != null) {
                pagerAdapter = null;
            }
            simpleExoPlayer.stop(true);
        }
        //if (mediaCategory != MediaCategory.IMAGE) {
        playerPosition = simpleExoPlayer.getCurrentPosition();
        simpleExoPlayer.removeListener(listener);
        simpleExoPlayer.setPlayWhenReady(false);
        //}
    }

    void setPageValues(int position, boolean toggleControl, boolean afterLoadFirstTime) {
        if (pagerAdapter == null || pagerAdapter.getCount() <= position) {
            return;
        }
        if (mediaCategory == MediaCategory.IMAGE) {
            updateViews();
        }

        VideoEntity videoEntity = mediaList.get(position);
        entity.setValues(videoEntity);
        //needed for databinding
        entity.notifyChange();

        //binding.bottomSheet.like.setImageResource( if (pagerAdapter !!.list1[position].mid == null)
        //R.drawable.ic_love else R.drawable.ic_love_fill)
        if (toggleControl) {
            //if (afterLoadFirstTime)
            //      sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            toggleVisibility(true, (afterLoadFirstTime) ? 2500 : 1500);

        }

    }


    Player.EventListener listener = new Player.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            if (playbackState == Player.STATE_BUFFERING)
                displayLoader();
            if (playbackState == Player.STATE_READY) {
                hideLoader();

                if (pagerAdapter != null) {
                    PlayerFragment playerFragment = (PlayerFragment)
                            pagerAdapter.getItem(binding.pager.getCurrentItem());
                    playerFragment.videoPlaying();

                    updateViews();
                    if (!playerBinded) {

                        pagerAdapter.notifyDataSetChanged();
                        playerBinded = playerFragment.bindPlayer();
                        System.out.println("tikona c " + playerBinded + " " + (pagerAdapter.getCount()));
                    }
                }
                if (SharedPrefStorage.showAD((SharedPrefStorage) preferenceStorage)) {
                    AlertDialogProvider.getInstance(getString(R.string.show_reward_ad_title), getString(R.string.show_reward_ad_msg), AlertDialogProvider.TYPE_NORMAL, false)
                            .setAlertDialogListener(new AlertDialogProvider.AlertDialogListener() {
                                @Override
                                public void onDialogCancel() {

                                }

                                @Override
                                public void onDialogOk(String text, AlertDialogProvider dialog) {
                                    dialog.dismiss();
                                    showRewardedAd();
                                }
                            })
                            .show(getSupportFragmentManager(), "showAd");

                    return;
                }
            }
            if (playbackState == Player.STATE_ENDED) {
                //simpleExoPlayer.seekTo(0, C.TIME_UNSET);
            }
        }
    };

    public void onTouch(boolean singleTouch) {
        if (singleTouch) {
            toggleVisibility(!showControls.get(), 4000);

        } else {
            //binding.bottomSheet.like.performClick()
        }

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            toggleVisibility(false, 4000);
        }
    };

    private void toggleVisibility(boolean showControls, int duration) {
        if (showControls) {
            handler.postDelayed(runnable, duration);

        } else {
            handler.removeCallbacksAndMessages(null);

        }
        if (mediaCategory != MediaCategory.IMAGE) {
            if (getPlayerFragment() != null)
                getPlayerFragment().showController(!showControls);
        }
        this.showControls.set(showControls);
    }

    private PlayerFragment getPlayerFragment() {
        if (pagerAdapter != null && pagerAdapter.getCount() != 0) {
            return (PlayerFragment) pagerAdapter.getItem(binding.pager.getCurrentItem());
        } else
            return null;

    }

    private void displayLoader() {
        handlerLoader.postDelayed(new Runnable() {
            @Override
            public void run() {
                toggleVisibility(false, 0);
                binding.loader.setVisibility(View.VISIBLE);
            }
        }, 500);

    }

    private void hideLoader() {
        handlerLoader.removeCallbacksAndMessages(null);
        handlerLoader.post(new Runnable() {
            @Override
            public void run() {
                binding.loader.setVisibility(View.GONE);
            }
        });

    }

    private void updateViews() {
        if (pagerAdapter != null && pagerAdapter.getCount() > 0) {
            //loginViewModel.updateViews(pagerAdapter!!.list1[binding.pager.currentItem].id.toInt())

        }
    }


    @Override
    public void onReportClicked(View view) {
        showReportVideoDialog();
    }

    @Override
    public void onRotateClicked(View view) {
        setRequestedOrientation((getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onPlayPauseClicked(View view) {
        playPause();
    }

    void playPause() {
        if (mediaCategory != MediaCategory.IMAGE) {
            if (simpleExoPlayer.isPlaying())
                handler.removeCallbacksAndMessages(null);
            else
                handler.postDelayed(runnable, 2000);
            binding.playPauseMid.setImageResource((!simpleExoPlayer.getPlayWhenReady()) ? R.drawable.ic_baseline_pause_24 : R.drawable.ic_baseline_play_arrow_24);
            simpleExoPlayer.setPlayWhenReady(!simpleExoPlayer.getPlayWhenReady());
        } else {
            toggleVisibility(!showControls.get(), 4000);
        }
    }

    float curentVol = 0F;

    @Override
    public void onVolumeClicked(View view) {
        if (pagerAdapter == null || mediaCategory == MediaCategory.IMAGE || pagerAdapter.getCount() <= binding.pager.getCurrentItem()) {
            return;
        }
        if (simpleExoPlayer.getVolume() == 0f) {
            simpleExoPlayer.setVolume(curentVol);
            curentVol = 0F;
        } else {
            curentVol = simpleExoPlayer.getVolume();
            simpleExoPlayer.setVolume(0F);
        }
        binding.mute.setImageResource((simpleExoPlayer.getVolume() == 0F) ? R.drawable.ic_baseline_volume_up_24 : R.drawable.ic_baseline_volume_off_24);

    }

    @Override
    public void onSettingsClicked(View view) {
        Utils.shareApplication();
    }

    @Override
    public void showDetail(View view) {
        if (simpleExoPlayer.getPlayWhenReady() && simpleExoPlayer.isPlaying()) {
            playPause();
        }
        binding.infoLay.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideInfoLayout(View view) {
        binding.infoLay.setVisibility(View.GONE);
    }

    @Override
    public void onThemeChanged(View view) {
        showNoCrapDownload();
    }

    @Override
    public void onVideoCrop(View view) {
        if (pagerAdapter != null && mediaCategory != MediaCategory.IMAGE) {
            PlayerFragment playerFragment = (PlayerFragment) pagerAdapter.getItem(binding.pager.getCurrentItem());
            playerFragment.cropVideo();
        }
    }

    @Override
    public void onRetry(View view) {

    }

    private void setEmojiParams(boolean showVideoControls) {
        if (!showVideoControls) {
            ((View) binding.emotion.getParent()).setBackgroundColor(Color.parseColor("#90000000"));
            binding.grup.setVisibility(View.GONE);
            /*  (binding.emotion.layoutParams as ConstraintLayout.LayoutParams).topToTop =
                  ConstraintLayout.LayoutParams.PARENT_ID
              (binding.emotion.layoutParams as ConstraintLayout.LayoutParams).bottomToBottom =
                  ConstraintLayout.LayoutParams.PARENT_ID
              (binding.trackTitle.layoutParams as ConstraintLayout.LayoutParams).bottomToTop =
                  ConstraintLayout.LayoutParams.UNSET
              (binding.trackTitle.layoutParams as ConstraintLayout.LayoutParams).bottomMargin = 0*/
            binding.guideLine.setGuidelinePercent(.58f);

        } else {
            ((View) binding.emotion.getParent()).setBackgroundColor(0);
            binding.grup.setVisibility(View.VISIBLE);
            /*(binding.emotion.layoutParams as ConstraintLayout.LayoutParams).topToTop =
                ConstraintLayout.LayoutParams.UNSET
            (binding.emotion.layoutParams as ConstraintLayout.LayoutParams).bottomToBottom =
                ConstraintLayout.LayoutParams.UNSET
            (binding.trackTitle.layoutParams as ConstraintLayout.LayoutParams).bottomToTop =
                R.id.guideLine
            (binding.trackTitle.layoutParams as ConstraintLayout.LayoutParams).bottomMargin =
                Screen.dp(12)*/
            //resources.getDimension(R.dimen.keyline_1_minus_8dp).toInt()
            binding.guideLine.setGuidelinePercent(.49f);

        }
    }

    private void showReportVideoDialog() {
        PlayerFragment playerFragment = (PlayerFragment) pagerAdapter.getItem(binding.pager.getCurrentItem());
        if (playerFragment == null) {
            Toast.makeText(PlayerActivity.this, R.string.wait_reporet, Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialogProvider.getInstance(getString(R.string.report_video), getString(R.string.report_video_msg), AlertDialogProvider.TYPE_EDIT_BIG, false)
                .setAlertDialogListener(new AlertDialogProvider.AlertDialogListener() {
                    @Override
                    public void onDialogCancel() {

                    }

                    @Override
                    public void onDialogOk(String text, AlertDialogProvider dialog) {

                     /*   if (TextUtils.isEmpty(text) || text.length < 15) {
                            Toast.makeText(
                                    this @HomeActivity,
                            R.string.valid_report_text,
                                    Toast.LENGTH_SHORT
                        ).show()
                            return
                        }
                        dialog.dismiss()
                        //simpleExoPlayer.playWhenReady = true
                        loginViewModel.reportVideo(
                                "" + pagerAdapter !!.list1[binding.pager.currentItem].id.toInt(),
                                text
                    )*/
                    }
                }).show(getSupportFragmentManager(), "report_video");
    }

    void showNoCrapDownload() {
        AlertDialogProvider.getInstance(getString(R.string.download_nocrap), getString(R.string.download_nocrap_desc), TYPE_NORMAL, false).setAlertDialogListener(new AlertDialogProvider.AlertDialogListener() {
            @Override
            public void onDialogCancel() {

            }

            @Override
            public void onDialogOk(String text, AlertDialogProvider dialog) {
                AppController.getInstance().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.x.nocrap" )));
            }
        }).show(getSupportFragmentManager(), "NoCrapPlayStore");

    }

    RewardedAd rewardedAd;

    void initRewardedAd() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                buildRewardedAd();
            }
        }, 500);
    }

    boolean loadingFailed = false;
    void showRewardedAd() {

        if (rewardedAd.isLoaded()) {
            rewardedAd.show(this, new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                    SharedPrefStorage.setShowAD((SharedPrefStorage) preferenceStorage, SharedPrefStorage.REWARD_AD_DISPLAY_COUNT_LIMIT);
                    SharedPrefStorage.setRewardedTime((SharedPrefStorage) preferenceStorage, System.currentTimeMillis() );
                    buildRewardedAd();
                }
            });

        } else {

            if(loadingFailed )
                buildRewardedAd();

        }

    }
    public void buildRewardedAd() {
        loadingFailed = false;
        rewardedAd = new RewardedAd(this, getString(R.string.ADMOB_APP_REWARDED_ID));
        if (!AppController.getInstance().enableAd) {
            return;
        }

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.

            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
                System.out.println("failed "+errorCode);
                loadingFailed = true;
                Utils.buildInterstitialAd(PlayerActivity.this);
            }
        };

        if (BuildConfig.DEBUG) {
            rewardedAd.loadAd(new AdRequest.Builder().addTestDevice(ADMOB_TEST_DEVICE).build(), adLoadCallback);
        } else {
            rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        }
    }



}