
package com.example.more.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.more.Application.AppController;
import com.example.more.BuildConfig;
import com.example.more.R;
import com.example.more.data.local.pref.PreferencesStorage;
import com.example.more.data.local.pref.SharedPrefStorage;
import com.example.more.databinding.FacebookFragmentBinding;
import com.example.more.ui.activity.CodeScanActivity;
import com.example.more.ui.activity.DownloadManagerActivity;
import com.example.more.ui.activity.HomeActivity;
import com.example.more.ui.activity.MemePagerActivity;
import com.example.more.ui.activity.ShowImagePlayVideoActivity;
import com.example.more.utills.AlertDialogProvider;
import com.example.more.utills.FileDownloadNotificationListener;
import com.example.more.utills.Utils;
import com.example.more.utills.animation.TransitionHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.OnAdMetadataChangedListener;
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
import java.util.Date;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.functions.Consumer;

import static com.example.more.utills.AlertDialogProvider.TYPE_EDIT_LINK;
import static com.example.more.utills.Utils.ADMOB_TEST_DEVICE;

/**
 * Fragment use to display facebook page on a webview so you can download facebook videos
 */
@SuppressLint("SetJavaScriptEnabled")
public class FacebookFragment extends Fragment {

    /**
     * I am using DataBinding
     */
    public FacebookFragmentBinding binding;

    /**
     * Provide {@link android.content.SharedPreferences} operations
     */
    @Inject
    PreferencesStorage preferenceStorage;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm");

    // Library to fetch list of downloads and download the files from server
    @Inject
    RxFetch rxFetch;

    String downloadURL;


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
        MobileAds.initialize(getActivity(), getString(R.string.ADMOB_APP_ID));
        rxFetch.addListener(new FileDownloadNotificationListener());

        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.facebook_fragment, viewGroup, false);
        setDownloaderClient();
        /*binding.webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (TextUtils.isEmpty(Uri.parse(url).getQueryParameter("src"))) {
                    return false;
                }
                // request permission for external storage as we need to save video on device
                Utils.PermissionStatus status = Utils.checkPermissionsCamera(getActivity(), PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, FacebookFragment.this);
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
                alertDialogProvider.show(getChildFragmentManager(), WhatsAppStatusActivity.class.getName());


                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                binding.
                        pb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //pb.setVisibility(View.GONE);
            }

        });*/
        /*binding.webView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                System.out.println("kuku "+ (scrollY<-80)+" "+scrollY+" "+(Screen.dp(80)-scrollY));
                binding.drawerLayout.setPadding(0,Screen.dp(80)-scrollY,0,0);
            }
        });*/
        binding.webView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int newProgress) {
                // Update the progress bar with page loading progress
                binding.pb.setProgress(newProgress);
                if (newProgress == 100) {

                 /*   binding.webView.setVisibility(View.GONE);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            binding.webView.setVisibility(View.VISIBLE);
                        }
                    });*/
                    // Hide the progressbar
                    binding.pb.setVisibility(View.GONE);
                    binding.pb.setProgress(0);
                }
            }
        });
        //  binding.webView.setVerticalScrollBarEnabled(false);
        //binding.webView.setHorizontalScrollBarEnabled(false);
        binding.webView.addJavascriptInterface(this, "mJava");
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.loadUrl("https://m.facebook.com/");
        // binding.webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    void setDownloaderClient() {
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                binding.
                        pb.setVisibility(View.VISIBLE);
            }

            public void onPageFinished(WebView view, String url) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       /* if (mprogress.getProgress()==100) {
                            mprogress.setVisibility(View.INVISIBLE);
                            mWebview.setVisibility(View.VISIBLE);
                            mySwipeRefreshLayout.setRefreshing(false);
                            //  scrollview.scrollTo(0,0);
                        }*/
                        binding.webView.loadUrl("javascript:" +
                                "var e=0;\n" +
                                "window.onscroll=function()\n" +
                                "{\n" +
                                "\tvar ij=document.querySelectorAll(\"video\");\n" +
                                "\t\tfor(var f=0;f<ij.length;f++)\n" +
                                "\t\t{\n" +
                                "\t\t\tif((ij[f].parentNode.querySelectorAll(\"img\")).length==0)\n" +
                                "\t\t\t{\n" +
                                "\t\t\t\tvar nextimageWidth=ij[f].nextSibling.style.width;\n" +
                                "\t\t\t\tvar nextImageHeight=ij[f].nextSibling.style.height;\n" +
                                "\t\t\t\tvar Nxtimgwd=parseInt(nextimageWidth, 10);\n" +
                                "\t\t\t\tvar Nxtimghght=parseInt(nextImageHeight, 10); \n" +
                                "\t\t\t\tvar DOM_img = document.createElement(\"img\");\n" +
                                "\t\t\t\t\tDOM_img.height=\"68\";\n" +
                                "\t\t\t\t\tDOM_img.width=\"68\";\n" +
                                "\t\t\t\t\tDOM_img.style.top=(Nxtimghght/2-20)+\"px\";\n" +
                                "\t\t\t\t\tDOM_img.style.left=(Nxtimgwd/2-20)+\"px\";\n" +
                                "\t\t\t\t\tDOM_img.style.position=\"absolute\";\n" +
                                "\t\t\t\t\tDOM_img.src = \"https://image.ibb.co/kobwsk/one.png\"; \n" +
                                "\t\t\t\t\tij[f].parentNode.appendChild(DOM_img);\n" +
                                "\t\t\t}\t\t\n" +
                                "\t\t\tij[f].remove();\n" +
                                "\t\t} \n" +
                                "\t\t\te++;\n" +
                                "};" +
                                "var a = document.querySelectorAll(\"a[href *= 'video_redirect']\");\n" +
                                "for (var i = 0; i < a.length; i++) {\n" +
                                "    var mainUrl = a[i].getAttribute(\"href\");\n" +
                                "  a[i].removeAttribute(\"href\");\n" +
                                "\tmainUrl=mainUrl.split(\"/video_redirect/?src=\")[1];\n" +
                                "\tmainUrl=mainUrl.split(\"&source\")[0];\n" +
                                "    var threeparent = a[i].parentNode.parentNode.parentNode;\n" +
                                "    threeparent.setAttribute(\"src\", mainUrl);\n" +
                                "    threeparent.onclick = function() {\n" +
                                "        var mainUrl1 = this.getAttribute(\"src\");\n" +
                                "         mJava.getData(mainUrl1);\n" +
                                "    };\n" +
                                "}" +
                                "var k = document.querySelectorAll(\"div[data-store]\");\n" +
                                "for (var j = 0; j < k.length; j++) {\n" +
                                "    var h = k[j].getAttribute(\"data-store\");\n" +
                                "    var g = JSON.parse(h);\nvar jp=k[j].getAttribute(\"data-sigil\");\n" +
                                "    if (g.type === \"video\") {\n" +
                                "if(jp==\"inlineVideo\")" +
                                "{" +
                                "   k[j].removeAttribute(\"data-sigil\");" +
                                "}\n" +
                                "        var url = g.src;\n" +
                                "        k[j].setAttribute(\"src\", g.src);\n" +
                                "        k[j].onclick = function() {\n" +
                                "            var mainUrl = this.getAttribute(\"src\");\n" +
                                "               mJava.getData(mainUrl);\n" +
                                "        };\n" +
                                "    }\n" +
                                "\n" +
                                "}");
                    }
                }, 00);
            }

            public void onLoadResource(WebView view, String url) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.webView.loadUrl("javascript:" +
                                "var e=document.querySelectorAll(\"span\"); " +
                                "if(e[0]!=undefined)" +
                                "{" +
                                "var fbforandroid=e[0].innerText;" +
                                "if(fbforandroid.indexOf(\"Facebook\")!=-1)" +
                                "{ " +
                                "var h =e[0].parentNode.parentNode.parentNode.style.display=\"none\";" +
                                "} " +
                                "}" +
                                "var installfb=document.querySelectorAll(\"a\");\n" +
                                "for (var hardwares = 0; hardwares < installfb.length; hardwares++) \n" +
                                "{\n" +
                                "\tif(installfb[hardwares].text.indexOf(\"Install\")!=-1)\n" +
                                "\t{\n" +
                                "\t\tvar soft=installfb[hardwares].parentNode.style.display=\"none\";\n" +
                                "\n" +
                                "\t}\n" +
                                "}\n");
                        binding.webView.loadUrl("javascript:" +
                                "var e=0;\n" +
                                "window.onscroll=function()\n" +
                                "{\n" +
                                "\tvar ij=document.querySelectorAll(\"video\");\n" +
                                "\t\tfor(var f=0;f<ij.length;f++)\n" +
                                "\t\t{\n" +
                                "\t\t\tif((ij[f].parentNode.querySelectorAll(\"img\")).length==0)\n" +
                                "\t\t\t{\n" +
                                "\t\t\t\tvar nextimageWidth=ij[f].nextSibling.style.width;\n" +
                                "\t\t\t\tvar nextImageHeight=ij[f].nextSibling.style.height;\n" +
                                "\t\t\t\tvar Nxtimgwd=parseInt(nextimageWidth, 10);\n" +
                                "\t\t\t\tvar Nxtimghght=parseInt(nextImageHeight, 10); \n" +
                                "\t\t\t\tvar DOM_img = document.createElement(\"img\");\n" +
                                "\t\t\t\t\tDOM_img.height=\"68\";\n" +
                                "\t\t\t\t\tDOM_img.width=\"68\";\n" +
                                "\t\t\t\t\tDOM_img.style.top=(Nxtimghght/2-20)+\"px\";\n" +
                                "\t\t\t\t\tDOM_img.style.left=(Nxtimgwd/2-20)+\"px\";\n" +
                                "\t\t\t\t\tDOM_img.style.position=\"absolute\";\n" +
                                "\t\t\t\t\tDOM_img.src = \"https://image.ibb.co/kobwsk/one.png\"; \n" +
                                "\t\t\t\t\tij[f].parentNode.appendChild(DOM_img);\n" +
                                "\t\t\t}\t\t\n" +
                                "\t\t\tij[f].remove();\n" +
                                "\t\t} \n" +
                                "\t\t\te++;\n" +
                                "};" +
                                "var a = document.querySelectorAll(\"a[href *= 'video_redirect']\");\n" +
                                "for (var i = 0; i < a.length; i++) {\n" +
                                "    var mainUrl = a[i].getAttribute(\"href\");\n" +
                                "  a[i].removeAttribute(\"href\");\n" +
                                "\tmainUrl=mainUrl.split(\"/video_redirect/?src=\")[1];\n" +
                                "\tmainUrl=mainUrl.split(\"&source\")[0];\n" +
                                "    var threeparent = a[i].parentNode.parentNode.parentNode;\n" +
                                "    threeparent.setAttribute(\"src\", mainUrl);\n" +
                                "    threeparent.onclick = function() {\n" +
                                "        var mainUrl1 = this.getAttribute(\"src\");\n" +
                                "         mJava.getData(mainUrl1);\n" +
                                "    };\n" +
                                "}" +
                                "var k = document.querySelectorAll(\"div[data-store]\");\n" +
                                "for (var j = 0; j < k.length; j++) {\n" +
                                "    var h = k[j].getAttribute(\"data-store\");\n" +
                                "    var g = JSON.parse(h);var jp=k[j].getAttribute(\"data-sigil\");\n" +
                                "    if (g.type === \"video\") {\n" +
                                "if(jp==\"inlineVideo\")" +
                                "{" +
                                "   k[j].removeAttribute(\"data-sigil\");" +
                                "}\n" +
                                "        var url = g.src;\n" +
                                "        k[j].setAttribute(\"src\", g.src);\n" +
                                "        k[j].onclick = function() {\n" +
                                "            var mainUrl = this.getAttribute(\"src\");\n" +
                                "               mJava.getData(mainUrl);\n" +
                                "        };\n" +
                                "    }\n" +
                                "\n" +
                                "}");
                    }
                }, 3000);
            }
        });
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

    }

    @Override
    public void onAttach(Context context) {
        /**
         * Remember in our {@link com.example.more.di.module.FragmentModule}, we
         * defined {@link FacebookFragment} injection? So we need
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


    @JavascriptInterface
    public void getData(final String pathvideo) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Save Video?");
        alertDialog.setMessage("Do you Really want to Download Video ?");
        alertDialog.setNegativeButton("Play", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //binding.webView.loadUrl(pa);
                ShowImagePlayVideoActivity.openActivity(getActivity(), pathvideo, false);
            }
        });
        alertDialog.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                downloadURL = pathvideo;
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
                SharedPrefStorage.setShowAD((SharedPrefStorage) preferenceStorage, SharedPrefStorage.readShowAD(((SharedPrefStorage) preferenceStorage)) - 1);
                downloadVideo();
            }


        });
        alertDialog.show();
    }

    void downloadVideo() {


        downloadURL = downloadURL.replaceAll("%3A", ":");
        downloadURL = downloadURL.replaceAll("%2F", "/");
        downloadURL = downloadURL.replaceAll("%3F", "?");
        downloadURL = downloadURL.replaceAll("%3D", "=");
        downloadURL = downloadURL.replaceAll("%26", "&");
        //     downloadvideo(downloadURL);
        final Request request = new Request((downloadURL), new File(Utils.getParentFile(), (sdf.format(new Date().getTime())) + "_vid.mp4").getAbsolutePath());
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
                    downloadVideo();
                    SharedPrefStorage.setShowAD((SharedPrefStorage) preferenceStorage, SharedPrefStorage.REWARD_AD_DISPLAY_COUNT_LIMIT);
                    SharedPrefStorage.setRewardedTime((SharedPrefStorage) preferenceStorage, System.currentTimeMillis() );
                    buildRewardedAd();
                }
            });

        } else {
            downloadVideo();
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

}