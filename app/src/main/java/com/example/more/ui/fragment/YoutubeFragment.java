
package com.example.more.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.more.Application.AppController;
import com.example.more.BuildConfig;
import com.example.more.R;
import com.example.more.data.local.pref.PreferencesStorage;
import com.example.more.data.local.pref.SharedPrefStorage;
import com.example.more.databinding.YoutubeFragmentBinding;
import com.example.more.ui.activity.CodeScanActivity;
import com.example.more.ui.activity.DownloadManagerActivity;
import com.example.more.ui.activity.YoutubeActivity;
import com.example.more.utills.AlertDialogProvider;
import com.example.more.utills.FileDownloadNotificationListener;
import com.example.more.utills.Utils;
import com.example.more.utills.animation.TransitionHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2rx.RxFetch;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.inject.Inject;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import dagger.android.support.AndroidSupportInjection;
import io.reactivex.functions.Consumer;

import static com.example.more.utills.AlertDialogProvider.TYPE_EDIT_LINK;
import static com.example.more.utills.AlertDialogProvider.TYPE_LIST;
import static com.example.more.utills.AlertDialogProvider.TYPE_NORMAL;
import static com.example.more.utills.AlertDialogProvider.TYPE_PROGRESS_DEFINITE;
import static com.example.more.utills.Utils.ADMOB_TEST_DEVICE;

/**
 * Fragment use to display facebook page on a webview so you can download facebook videos
 */
@SuppressLint("SetJavaScriptEnabled")
public class YoutubeFragment extends Fragment {
    YouTubeExtractor youTubeExtractor;
    public boolean backCliked = false;
    private static final int ITAG_FOR_AUDIO = 140;
    /**
     * I am using DataBinding
     */
    public YoutubeFragmentBinding binding;
    WebResourceRequest lastWebResourceRequest = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
    boolean vidFound = false;

    /**
     * Provide {@link android.content.SharedPreferences} operations
     */
    @Inject
    PreferencesStorage preferenceStorage;
    // Library to fetch list of downloads and download the files from server
    @Inject
    RxFetch rxFetch;

    private String BASE_URL = "https://www.youtube.com";
    private String mYoutubeLink = BASE_URL + "/watch?v=";
    private ArrayList<YtFragmentedVideo> videoList = new ArrayList<>();
    private ArrayList<YtFragmentedVideo> audioList = new ArrayList<>();
    private ArrayList<String> videoTitleList = new ArrayList<>();
    private ArrayList<String> audioTitleList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initialiseView(container);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }


    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView(ViewGroup viewGroup) {

        rxFetch.addListener(new FileDownloadNotificationListener());
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.youtube_fragment, viewGroup, false);

        binding.webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("clickked url " + url);
                /*if (TextUtils.isEmpty(Uri.parse(url).getQueryParameter("src"))) {
                    return true;
                }
                // request permission for external storage as we need to save video on device
                Utils.PermissionStatus status = Utils.checkPermissionsCamera(getActivity(), PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, YoutubeFragment.this);
                if (status != Utils.PermissionStatus.SUCCESS) {

                    return true;
                }
                AlertDialogProvider alertDialogProvider = AlertDialogProvider.getInstance(getString(R.string.download_video),getString(R.string.download_video_msg), AlertDialogProvider.TYPE_NORMAL,false);
                alertDialogProvider.setAlertDialogListener(new AlertDialogProvider.AlertDialogListener() {
                    @Override
                    public void onDialogCancel() {
                        binding.webView.loadUrl(url);
                    }


                    @Override
                    public void onDialogOk(String text, AlertDialogProvider dialog) {
                        final Request request = new Request(Uri.parse(url).getQueryParameter("src"), new File(Utils.getParentFile(), (sdf.format(new Date().getTime())) + "_vid.mp4").getAbsolutePath());
                        request.setPriority(Priority.HIGH);
                        request.setNetworkType(NetworkType.ALL);

                        //Downloading selected video
                        rxFetch.enqueue(request).asFlowable().subscribe(new Consumer<Request>() {
                            @Override
                            public void accept(Request request) throws Exception {

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(getActivity(), getString(R.string.error) + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                alertDialogProvider.show(getChildFragmentManager(), YoutubeFragment.class.getName());
*/

                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                System.out.println("clickked url st" + url);
                binding.
                        pb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                System.out.println("clickked url vv " + url);
                //pb.setVisibility(View.GONE);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                //https://m.youtube.com/watch?v=_o-IzoxAZYE&pbj=1
                String vid = Utils.getYouTubeId(request.getUrl().toString());

                System.out.println("id vfu " + request.getUrl() + " " + vid);
                if (!TextUtils.isEmpty(vid) && !vid.equalsIgnoreCase("youtube.com")) {
                    getYoutubeDownloadUrl(mYoutubeLink + vid);
                    /*Handler handler = new Handler(getMainLooper());
                    handler.post(new Runnable() {
                        @Override

                        public void run() {
                            binding.webView.goBack();
                            //   binding.webView.loadUrl("https://m.youtube.com/");
                        }
                    });*/
                }
                return super.shouldInterceptRequest(view, request);
            }
        });
        binding.webView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int newProgress) {
                // Update the progress bar with page loading progress
                binding.pb.setProgress(newProgress);
                if (newProgress == 100) {
                    // Hide the progressbar
                    binding.pb.setVisibility(View.GONE);
                    binding.pb.setProgress(0);
                }
            }
        });
        binding.webView.setVerticalScrollBarEnabled(true);
        binding.webView.setHorizontalScrollBarEnabled(true);
        //binding.webView.addJavascriptInterface(this, "mJava");
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.loadUrl("https://m.youtube.com/");
        // binding.webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRewardedAd();
        showProgressDialog();
    }

    @Override
    public void onAttach(Context context) {
        /**
         * Remember in our {@link com.example.more.di.module.FragmentModule}, we
         * defined {@link YoutubeFragment} injection? So we need
         * to call this method in order to inject the
         * ViewModelFactory into our Fragment
         * */
        AndroidSupportInjection.inject(this);
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.facebok_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_dm) {
            transitionToActivity(new Intent(getActivity(), DownloadManagerActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_edit) {
            AlertDialogProvider alertDialogProvider = AlertDialogProvider.getInstance(getString(R.string.copy_fb_link), "", TYPE_EDIT_LINK, false);
            alertDialogProvider.show(getChildFragmentManager(), CodeScanActivity.class.getName());
            alertDialogProvider.setAlertDialogListener(new AlertDialogProvider.AlertDialogListener() {
                @Override
                public void onDialogCancel() {

                }

                @Override
                public void onDialogOk(String text, AlertDialogProvider dialog) {
                    dialog.dismiss();
                    if (TextUtils.isEmpty(text)) {
                        Toast.makeText(getActivity(), R.string.invalid_link, Toast.LENGTH_SHORT).show();
                        return;
                    }
                        binding.webView.loadUrl(text);
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Apply transition on given intent
     *
     * @param intent
     */
    public void transitionToActivity(Intent intent) {
        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(getActivity(), true);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pairs);
        startActivity(intent, transitionActivityOptions.toBundle());
    }

    public void onDownloadClicked() {
        if (videoList.size() > 0)
            showYoutubeFormats();

    }

    private void getYoutubeDownloadUrl(String youtubeLink) {

        progressDialog.show(getChildFragmentManager(),"progress");
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() instanceof YoutubeActivity)
                        ((YoutubeActivity) getActivity()).setFabVisibility(false);
                }
            });

        }
        if (youTubeExtractor != null) {
            youTubeExtractor.cancel(true);
        }
        youTubeExtractor = new YouTubeExtractor(getActivity()) {

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                //mainProgressBar.setVisibility(View.GONE);
                progressDialog.dismiss();
                if (ytFiles == null) {
                    System.out.println("id vfu null* "+youtubeLink);
                    return;
                }
                System.out.println("id vfu " + ytFiles.size());
                // Iterate over itags
                videoList.clear();
                audioList.clear();
                videoTitleList.clear();
                audioTitleList.clear();
                for (int i = 0, itag; i < ytFiles.size(); i++) {
                    itag = ytFiles.keyAt(i);
                    // ytFile represents one file with its url and meta data
                    YtFile ytFile = ytFiles.get(itag);


                    // Just add videos in a decent format => height -1 = audio
                    if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                        //      addButtonToMainLayout(vMeta.getTitle(), ytFile);

                        addFormatToList(ytFile, ytFiles, vMeta.getTitle().replaceAll(" ", "_").replaceAll("\\.", ""));

                  /*      if (videoList.size() > 0) {
                            YtFragmentedVideo ytFragmentedVideo = formatsToShowList.get(formatsToShowList.size() - 1);
                            if (ytFragmentedVideo.height == -1)
                                titles.add("Only Audio " + ytFragmentedVideo.audioFile.getFormat().getAudioBitrate() + " kbit/s (" + ytFragmentedVideo.audioFile.getFormat().getExt() + ")");
                            else
                                titles.add(((ytFragmentedVideo.videoFile.getFormat().getFps() == 60 ? ytFragmentedVideo.height + "p60" :
                                        ytFragmentedVideo.height + "p") + (ytFragmentedVideo.videoFile.getFormat().getAudioBitrate() == -1 ? " - No Audio" : "")) + "(" + ytFragmentedVideo.videoFile.getFormat().getExt() + ")");
                        }*/
                        //playVideo(ytFile.getUrl());
                    }
                }
                if (videoList.size() > 0) {
                    if (!backCliked) {
                        showYoutubeFormats();
                    }
                    if (getActivity() instanceof YoutubeActivity)
                        ((YoutubeActivity) getActivity()).setFabVisibility(true);
                    backCliked = false;
                    return;


                }
            }
        };
        youTubeExtractor.extract(youtubeLink, true, true);

    }

    private void addFormatToList(YtFile ytFile, SparseArray<YtFile> ytFiles, String fileName) {
        int height = ytFile.getFormat().getHeight();

        if (height != -1) {
            for (YtFragmentedVideo frVideo : videoList) {
                if (frVideo.height == height && (frVideo.videoFile == null ||
                        frVideo.videoFile.getFormat().getFps() == ytFile.getFormat().getFps())) {
                    return;
                }
            }
        } else {

            for (YtFragmentedVideo frVideo : audioList) {
                if (frVideo.height == height && (frVideo.audioFile == null ||
                        frVideo.audioFile.getFormat().getAudioBitrate() > ytFile.getFormat().getAudioBitrate())) {
                    return;
                }
            }
        }
        YtFragmentedVideo frVideo = new YtFragmentedVideo();
        frVideo.height = height;
        frVideo.fileName = fileName;
        boolean isVideoFile = true;
        if (ytFile.getFormat().isDashContainer()) {
            if (height > 0) {
                frVideo.videoFile = ytFile;
                frVideo.audioFile = ytFiles.get(ITAG_FOR_AUDIO);
            } else {
                isVideoFile = false;
                frVideo.audioFile = ytFile;
            }
        } else {
            frVideo.videoFile = ytFile;
        }
        if (isVideoFile) {
            videoList.add(frVideo);
            videoTitleList.add(((frVideo.videoFile.getFormat().getFps() >= 60 ? frVideo.height + "p HD" :
                    frVideo.height + "p") + (frVideo.videoFile.getFormat().getAudioBitrate() == -1 ? " - No Audio" : "")) + " (" + frVideo.videoFile.getFormat().getExt() + ")");
        } else {
            if (audioList.size() == 0) {
                audioTitleList.add("Only Audio " + frVideo.audioFile.getFormat().getAudioBitrate() + " kbit/s (" + frVideo.audioFile.getFormat().getExt() + ")");
                audioList.add(frVideo);
            } else {
                audioList.set(0, frVideo);
                audioTitleList.set(0, "Only Audio " + frVideo.audioFile.getFormat().getAudioBitrate() + " kbit/s (" + frVideo.audioFile.getFormat().getExt() + ")");
            }
        }

    }

    AlertDialogProvider progressDialog;
    void showProgressDialog(){
        progressDialog = AlertDialogProvider.getInstance(getString(R.string.youtube_downloading), "s", TYPE_PROGRESS_DEFINITE, true)
                .setIsIndefinte(true);

    }
    int downloadPosition = 0;
    void showYoutubeFormats() {
        AlertDialogProvider.getInstance(getString(R.string.download_formats), videoTitleList, new ArrayList<Integer>(Collections.nCopies(videoTitleList.size(), R.drawable.ic_file_download_black_24dp)), TYPE_LIST, false)
                .setAlertDialogItemListener(new AlertDialogProvider.AlertDialogItemListener() {
                    @Override
                    public void onItemClicked(int position) {
                        downloadPosition = position;
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
                                    .show(getChildFragmentManager(), "showAd");

                            return;
                        }
                       else{
                           downloadVideoClicked();
                        }
                    }
                }).setCanceledOnTouchOutside(true).show(getChildFragmentManager(), "youtube");
    }
    void downloadVideoClicked(){
        if (videoList.get(downloadPosition).videoFile.getFormat().getAudioBitrate() == -1) {
            showMergeAudioDialog(downloadPosition);

            return;
        }

        downloadVideo(downloadPosition, true);
    }

    void showMergeAudioDialog(int position) {
        AlertDialogProvider.getInstance(getString(R.string.download_video_without_audio), getString(R.string.download_video_without_audio_msg), TYPE_NORMAL, false)
                .setAlertDialogListener(new AlertDialogProvider.AlertDialogListener() {
                    @Override
                    public void onDialogCancel() {

                    }

                    @Override
                    public void onDialogOk(String text, AlertDialogProvider dialog) {
                        dialog.dismiss();
                        downloadVideo(position, false);
                        downloadAudio(0);
                    }
                }).show(getChildFragmentManager(), "youtube");
    }

    void downloadVideo(int position, boolean hasAudio) {
        //Downloading selected video
        final Request request = new Request(videoList.get(position).videoFile.getUrl(), new File(Utils.getYoutubeResource(), (hasAudio ? "" : "Utube_") + videoList.get(position).fileName + "." + videoList.get(position).videoFile.getFormat().getExt()).getAbsolutePath());
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);

        rxFetch.enqueue(request).asFlowable().subscribe(new Consumer<Request>() {
            @Override
            public void accept(Request request) throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(getActivity(), getString(R.string.error) + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void downloadAudio(int position) {
        //Downloading selected video
        final Request request = new Request(audioList.get(position).audioFile.getUrl(), new File(Utils.getYoutubeResource(), "UtubeMp3_" + audioList.get(position).fileName + "." + audioList.get(position).audioFile.getFormat().getExt()).getAbsolutePath());
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);

        rxFetch.enqueue(request).asFlowable().subscribe(new Consumer<Request>() {
            @Override
            public void accept(Request request) throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(getActivity(), getString(R.string.error) + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void showAudioFormats() {
        AlertDialogProvider.getInstance(getString(R.string.download_nocrap), audioTitleList, new ArrayList<Integer>(Collections.nCopies(audioTitleList.size(), R.drawable.ic_file_download_black_24dp)), TYPE_LIST, false)
                .setAlertDialogItemListener(new AlertDialogProvider.AlertDialogItemListener() {
                    @Override
                    public void onItemClicked(int position) {
                        //Downloading selected video
                        final Request request = new Request(audioList.get(position).audioFile.getUrl(), new File(Utils.getParentFile(), (sdf.format(new Date().getTime())) + "_aud." + audioList.get(position).audioFile.getFormat().getExt()).getAbsolutePath());
                        request.setPriority(Priority.HIGH);
                        request.setNetworkType(NetworkType.ALL);

                        rxFetch.enqueue(request).asFlowable().subscribe(new Consumer<Request>() {
                            @Override
                            public void accept(Request request) throws Exception {

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(getActivity(), getString(R.string.error) + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).show(getChildFragmentManager(), "youtube");
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
            rewardedAd.show(getActivity(), new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    downloadVideoClicked();
                    SharedPrefStorage.setShowAD((SharedPrefStorage) preferenceStorage, SharedPrefStorage.REWARD_AD_DISPLAY_COUNT_LIMIT);
                    SharedPrefStorage.setRewardedTime((SharedPrefStorage) preferenceStorage, System.currentTimeMillis() );
                    buildRewardedAd();
                }
            });

        } else {
            downloadVideoClicked();
            if(loadingFailed )
                buildRewardedAd();

        }

    }
    public void buildRewardedAd() {
        loadingFailed = false;
        rewardedAd = new RewardedAd(getActivity(), getString(R.string.ADMOB_APP_REWARDED_ID));
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
                Utils.buildInterstitialAd(getActivity());
            }
        };

        if (BuildConfig.DEBUG) {
            rewardedAd.loadAd(new AdRequest.Builder().addTestDevice(ADMOB_TEST_DEVICE).build(), adLoadCallback);
        } else {
            rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        }
    }


    private class YtFragmentedVideo {
        int height;
        YtFile audioFile;
        YtFile videoFile;
        String fileName;
    }
}