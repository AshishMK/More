package com.example.more.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.example.more.BR;
import com.example.more.R;
import com.example.more.data.local.pref.PreferencesStorage;
import com.example.more.databinding.MemePagerActivityBinding;
import com.example.more.ui.adapter.MemePagerAdapter;
import com.example.more.ui.fragment.ListFragment;
import com.example.more.ui.interfaces.MemePagerActivityHandler;
import com.example.more.utills.Utils;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2.Status;
import com.tonyodev.fetch2core.DownloadBlock;
import com.tonyodev.fetch2rx.RxFetch;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.functions.Consumer;

import static com.example.more.di.module.ApiModule.base_url;

/**
 * Activity show detail image of meme
 * selected in {@link ListFragment}
 * also @see {@link com.example.more.ui.adapter.ContentListAdapter}
 */

public class MemePagerActivity extends AppCompatActivity implements MemePagerActivityHandler {


    /**
     * Provide {@link android.content.SharedPreferences} operations
     */
    @Inject
    PreferencesStorage preferenceStorage;


    /*
     * I am using DataBinding
     * */
    private MemePagerActivityBinding binding;

    HashMap<Integer, Integer> map = new HashMap<>();
    // Library to fetch list of downloads and download the files from server
    @Inject
    RxFetch rxFetch;
    /**
     * Position of item on which download initiated -1 = none
     */
    volatile int positionDownload = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Remember in our {@link com.example.more.di.module.ActivityModule}, we
         * defined {@link MemePagerActivity} injection? So we need
         * to call this method in order to inject the
         * ViewModelFactory into our Activity.
         * */
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        initialiseView();
        setFetchListener();
        initAdMob();
    }

    /**
     * Method to initialize admob sdk to show ads
     */
    public void initAdMob() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.buildInterstitialAd(MemePagerActivity.this);
            }
        },1500);
        Utils.buildBannerAD(binding.adView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView() {
        //supportPostponeEnterTransition();
        binding = DataBindingUtil.setContentView(this, R.layout.meme_pager_activity_list);
        binding.setHandler(this);
        binding.setVariable(BR.preferenceStorage, preferenceStorage);
        binding.pager.setAdapter(new MemePagerAdapter(this, getIntent().getParcelableArrayListExtra("contents")));
        binding.pager.setCurrentItem(getIntent().getIntExtra("position", 0), false);
        binding.pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (map.containsKey(position)) {
                    rxFetch.getDownload(map.get(position)).asFlowable().subscribe(new Consumer<Download>() {
                        @Override
                        public void accept(Download download) throws Exception {
                            binding.setContent(download);
                        }
                    });

                } else {
                    binding.setContent(null);
                    binding.setIsFileExists(new File(Utils.getParentFile(), ((MemePagerAdapter) binding.pager.getAdapter()).getUrl(position)).exists());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.setIsFileExists(new File(Utils.getParentFile(), ((MemePagerAdapter) binding.pager.getAdapter()).getUrl(getIntent().getIntExtra("position", 0))).exists());
    }


    /**
     * method to setListener on current download requests
     */
    void setFetchListener() {
        rxFetch.addListener(new FetchListener() {
            @Override
            public void onAdded(@NotNull Download download) {
                changeDownloadState(download);
            }

            @Override
            public void onQueued(@NotNull Download download, boolean b) {
                changeDownloadState(download);
            }

            @Override
            public void onWaitingNetwork(@NotNull Download download) {
                changeDownloadState(download);
            }

            @Override
            public void onCompleted(@NotNull Download download) {
                changeDownloadState(download);
                Utils.sendFileToScan(new File(download.getFile()));
            }

            @Override
            public void onError(@NotNull Download download, @NotNull Error error, @org.jetbrains.annotations.Nullable Throwable throwable) {
                changeDownloadState(download);
            }

            @Override
            public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {

            }

            @Override
            public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {

            }

            @Override
            public void onProgress(@NotNull Download download, long l, long l1) {
                changeDownloadState(download);
            }

            @Override
            public void onPaused(@NotNull Download download) {

            }

            @Override
            public void onResumed(@NotNull Download download) {

            }

            @Override
            public void onCancelled(@NotNull Download download) {
                changeDownloadState(download);
            }

            @Override
            public void onRemoved(@NotNull Download download) {

            }

            @Override
            public void onDeleted(@NotNull Download download) {
            }
        });
    }

    /**
     * @param download update list of downloads when {@link Status} of a {@link Download} changed
     */
    void changeDownloadState(Download download) {
        System.out.println("dasds " + positionDownload);
        if (positionDownload != -1 && !map.containsKey(positionDownload)) {
            map.put(positionDownload, download.getId());
            positionDownload = -1;
        }
        if (map.containsKey(binding.pager.getCurrentItem()) && map.get(binding.pager.getCurrentItem()) == download.getId()) {
            binding.setContent(download);
        }

    }


    @Override
    public void onClickDownload(Download download) {
        positionDownload = binding.pager.getCurrentItem();
        if (download != null && (download.getStatus() == Status.DOWNLOADING || download.getStatus() == Status.COMPLETED)) {

            if (download.getStatus() == Status.COMPLETED) {
                Utils.shareFile(download.getFile());
                positionDownload = -1;
                return;
            }

        }
        if (new File(Utils.getParentFile(), ((MemePagerAdapter) binding.pager.getAdapter()).getUrl(positionDownload)).exists()) {
            Utils.shareFile(new File(Utils.getParentFile(), ((MemePagerAdapter) binding.pager.getAdapter()).getUrl(positionDownload)).getAbsolutePath());
            positionDownload = -1;
            return;
        }


        String url = ((MemePagerAdapter) binding.pager.getAdapter()).getUrl(positionDownload);
        final Request request = new Request(base_url + "tnt_file/" + url, new File(Utils.getParentFile(), url).getAbsolutePath());
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
                Toast.makeText(MemePagerActivity.this, getString(R.string.error) + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTipClicked(View v) {
        int toScroll = binding.pager.getCurrentItem();
        toScroll = toScroll >= binding.pager.getAdapter().getCount() - 1 ? toScroll - 1 : toScroll + 1;
        binding.pager.setCurrentItem(toScroll, true);
        preferenceStorage.writeValue(getString(R.string.show_scroll_pref), false);
        binding.setPreferenceStorage(preferenceStorage);
    }

    @Override
    public void onBackClick(View v) {
        onBackPressed();
    }
}
