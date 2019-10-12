
package com.example.more.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.more.Application.AppController;
import com.example.more.R;
import com.example.more.databinding.FacebookFragmentBinding;
import com.example.more.ui.activity.CodeScanActivity;
import com.example.more.ui.activity.DownloadManagerActivity;
import com.example.more.ui.activity.WhatsAppStatusActivity;
import com.example.more.utills.AlertDialogProvider;
import com.example.more.utills.FileDownloadNotificationListener;
import com.example.more.utills.Utils;
import com.example.more.utills.animation.TransitionHelper;
import com.tonyodev.fetch2.FetchConfiguration;
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

import static com.example.more.ui.fragment.WhatsappStatusFragment.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE;
import static com.example.more.utills.AlertDialogProvider.TYPE_EDIT;
import static com.example.more.utills.AlertDialogProvider.TYPE_EDIT_LINK;

/**
 * Fragment use to display facebook page on a webview so you can download facebook videos
 */
@SuppressLint("SetJavaScriptEnabled")
public class FacebookFragment extends Fragment {

    /**
     * I am using DataBinding
     */
    public FacebookFragmentBinding binding;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm");

    // Library to fetch list of downloads and download the files from server
    @Inject
    RxFetch rxFetch;

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

        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.facebook_fragment, viewGroup, false);
        binding.webView.setWebViewClient(new WebViewClient() {
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
                AlertDialogProvider alertDialogProvider = AlertDialogProvider.getInstance(getString(R.string.download_video),getString(R.string.download_video_msg), AlertDialogProvider.TYPE_NORMAL);
                alertDialogProvider.setAlertDialogListener(new AlertDialogProvider.AlertDialogListener() {
                    @Override
                    public void onDialogCancel() {
                        binding.webView.loadUrl(url);
                    }

                    @Override
                    public void onDialogOk(String text) {
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
        binding.webView.setVerticalScrollBarEnabled(false);
        binding.webView.setHorizontalScrollBarEnabled(false);

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.loadUrl("https://mbasic.facebook.com/home.php");

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
            AlertDialogProvider alertDialogProvider = AlertDialogProvider.getInstance(getString(R.string.copy_fb_link), "", TYPE_EDIT_LINK);
            alertDialogProvider.show(getChildFragmentManager(), CodeScanActivity.class.getName());
            alertDialogProvider.setAlertDialogListener(new AlertDialogProvider.AlertDialogListener() {
                @Override
                public void onDialogCancel() {

                }

                @Override
                public void onDialogOk(String text) {
                    if (TextUtils.isEmpty(text)) {
                        Toast.makeText(getActivity(), R.string.invalid_link, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(Uri.parse(text).getQueryParameter("src")) && !text.contains(".mp4")) {
                        binding.webView.loadUrl(text);
                        return;
                    }
                    String downloadUrl = text.contains(".mp4") ? text : Uri.parse(text).getQueryParameter("src");
                    final Request request = new Request(downloadUrl, new File(Utils.getParentFile(), (sdf.format(new Date().getTime())) + "_vid.mp4").getAbsolutePath());
                    request.setPriority(Priority.HIGH);
                    request.setNetworkType(NetworkType.ALL);


                    rxFetch.enqueue(request).asFlowable().subscribe(new Consumer<Request>() {
                        @Override
                        public void accept(Request request) throws Exception {

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(getActivity(), "error" + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

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

}