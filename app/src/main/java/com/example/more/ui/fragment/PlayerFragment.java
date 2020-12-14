package com.example.more.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;

import com.example.more.R;
import com.example.more.data.remote.model.VideoEntity;
import com.example.more.databinding.PlayerFragmentBinding;
import com.example.more.ui.activity.PlayerActivity;
import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DataSource;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static com.example.more.di.module.ApiModule.base_url_download;

public class PlayerFragment extends DaggerFragment {
    PlayerFragmentBinding binding = null;
    ObservableBoolean isImage = new ObservableBoolean(false);
    @Inject
    DataSource.Factory dataSourceFactory;
    VideoEntity videoEntity = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
         * Step 2: Remember in our ActivityModule, we
         * defined MainActivity injection? So we need
         * to call this method in order to inject the
         * ViewModelFactory into our Activity
         * */

        initialiseView(container);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }


    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView(ViewGroup viewGroup) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_player, viewGroup, false);
        binding.setThumb("");
        binding.setIsImage(isImage);
        binding.playerView.setControllerHideOnTouch(false);
        binding.playerView.setControllerShowTimeoutMs(0);
        binding.playerView.setUseController(true);
        binding.playerView.setPlayer(null);
        showController(!showbar);
        binding.playerView.setKeepContentOnPlayerReset(true);
        //binding!!.playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        binding.playerView.setKeepScreenOn(true);
        setupTouchListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        isImage.set(videoEntity.category == 2);
        binding.setContent(videoEntity);
        binding.setImg(base_url_download + videoEntity.url);
        if (videoEntity.category == 2) {
            showController(true);
        }

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoEntity = getArguments().getParcelable("videoEntity");
        }
    }

    boolean showbar = false;

    public void showController(boolean hide) {
        showbar = !hide;
        if (binding == null)
            return;
        if (!hide)
            binding.playerView.showController();
        else
            binding.playerView.hideController();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setupTouchListener() {
        if (videoEntity != null && videoEntity.category == 2) {
            binding.photoView.setOnOutsidePhotoTapListener(new OnOutsidePhotoTapListener() {
                @Override
                public void onOutsidePhotoTap(ImageView imageView) {
                    if (getActivity() instanceof PlayerActivity) {
                        ((PlayerActivity) getActivity()).onTouch(true);
                    }
                }
            });


            binding.photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(ImageView view, float x, float y) {
                    if (getActivity() instanceof PlayerActivity) {
                        ((PlayerActivity) getActivity()).onTouch(true);
                    }
                }
            });
            return;
        }

        binding.playerView.setOnTouchListener(new View.OnTouchListener() {

            private GestureDetector gestureDetector =
                    new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onDoubleTap(MotionEvent e) {
                            if (getActivity() instanceof PlayerActivity) {
                                ((PlayerActivity) getActivity()).onTouch(false);
                            }
                            return super.onDoubleTap(e);
                        }

                        @Override
                        public boolean onSingleTapConfirmed(MotionEvent e) {
                            if (getActivity() instanceof PlayerActivity) {
                                ((PlayerActivity) getActivity()).onTouch(true);
                            }
                            return super.onSingleTapConfirmed(e);
                        }
                    });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }


    public void cropVideo() {
        binding.playerView.setResizeMode(
                (binding.playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_FIT) ?
                        AspectRatioFrameLayout.RESIZE_MODE_FILL : AspectRatioFrameLayout.RESIZE_MODE_FIT);
    }

    public boolean bindPlayer() {
//        Toast.makeText(activity,"($videoEntity==null)",1).show()
        if (videoEntity == null || videoEntity.category == 2) {
            return false;
        }
        binding.playerView.setPlayer(null);
        if (binding.playerView.getPlayer() == null) {
            if (getActivity() instanceof PlayerActivity)
                binding.playerView.setPlayer(((PlayerActivity) getActivity()).simpleExoPlayer);
            return true;
        }
        return true;
    }


    public void videoPlaying() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (binding != null) {
                    binding.setThumb(null);
                    //  binding !!.playerView.videoSurfaceView ?.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }, 300);
    }

    public void showThumb(String thumb) {
        //   binding.setThumb(null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() instanceof PlayerActivity && binding != null && !((PlayerActivity) getActivity()).simpleExoPlayer.isPlaying()) {
                    //binding.playerView.getVideoSurfaceView().setBackgroundColor(Color.WHITE)
                    binding.setThumb(base_url_download + thumb + ".png");
                }
            }
        }, 500);
    }

    public void unBindPlayer() {
        binding.setThumb("");
        if (videoEntity.category == 2) {
            return;
        }
        binding.playerView.setPlayer(null);
    }
}
