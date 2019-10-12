/*
 * Copyright (c) 2018 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.more.ui.activity;

import android.os.Bundle;
import android.transition.Explode;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.more.R;
import com.example.more.databinding.DMActivityBinding;
import com.example.more.factory.ViewModelFactory;
import com.example.more.ui.adapter.DMListAdapter;
import com.example.more.utills.AlertDialogProvider;
import com.example.more.utills.Utils;
import com.example.more.viewmodel.DMActivityViewModel;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.Status;
import com.tonyodev.fetch2core.DownloadBlock;
import com.tonyodev.fetch2rx.RxFetch;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.functions.Consumer;

/**
 * Activity to show the list of Facebook Downloads
 * {@link DMListAdapter} {@link com.example.more.ui.fragment.FacebookFragment}
 * {@link FacebookActivity}
 */
public class DownloadManagerActivity extends AppCompatActivity implements DMListAdapter.OnClickItem {

    /*
     * we need to
     * inject the ViewModelFactory. The ViewModelFactory class
     * has a list of ViewModels and will provide
     * the corresponding ViewModel in this activity
     * */
    @Inject
    ViewModelFactory viewModelFactory;

    /*
     * This is our ViewModel class
     * */
    private DMActivityViewModel dmActivityViewModel;
    /*
     * I am using DataBinding
     * */
    private DMActivityBinding binding;
    // Library to fetch list of downloads and download the files from server
    @Inject
    RxFetch rxFetch;
    /**
     * Adapter to display list of {@link Download}
     */
    DMListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        /**
         * Remember in our {@link com.example.more.di.module.ActivityModule}, we
         * defined {@link DownloadManagerActivity} injection? So we need
         * to call this method in order to inject the
         * ViewModelFactory into our Activity.
         * */
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        initialiseView();
        initialiseViewModel();
        setFetchListener();
    }

    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_download_manager);
        setSupportActionBar(binding.toolbar);
        // Performing window enter Activity transition animation
        getWindow().setEnterTransition(buildEnterTransition());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.title.setText(R.string.facebook);
        mAdapter = new DMListAdapter(this);
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        binding.list.setAdapter(mAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onClickItem(Download download, boolean canceled, int position) {
        if (canceled)
            rxFetch.cancel(download.getId()).asFlowable().subscribe(new Consumer<Download>() {
                @Override
                public void accept(Download download) throws Exception {
                    mAdapter.setItem(download, position);

                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Toast.makeText(DownloadManagerActivity.this, "error" + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }


            });
        else
            rxFetch.retry(download.getId()).asFlowable().subscribe(new Consumer<Download>() {
                @Override
                public void accept(Download download) {
                    mAdapter.setItem(download, position);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Toast.makeText(DownloadManagerActivity.this, "error" + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }


            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "info").setIcon(ActivityCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            AlertDialogProvider.getInstance(getString(R.string.download_location), String.format(getString(R.string.whats_app_head_message), getString(R.string.app_name) + " directory"), AlertDialogProvider.TYPE_NORMAL)
                    .show(getSupportFragmentManager(), WhatsAppStatusActivity.class.getName());
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    void setFetchListener() {
        rxFetch.addListener(new FetchListener() {
            @Override
            public void onAdded(@NotNull Download download) {

            }

            @Override
            public void onQueued(@NotNull Download download, boolean b) {

            }

            @Override
            public void onWaitingNetwork(@NotNull Download download) {

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
        ArrayList<Download> tmp = mAdapter.getAdapterList();
        for (int i = 0; i < tmp.size(); i++) {
            if (download.getId() == tmp.get(i).getId()) {
                mAdapter.setItem(download, i);
            }
        }
    }

    /**
     * @return method to create and return explode transition for activity window
     */
    private Transition buildEnterTransition() {
        Explode enterTransition = new Explode();
        enterTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        return enterTransition;
    }

    /*
     * Initialising the ViewModel class here.
     * We are adding the ViewModelFactory class here.
     * We are observing the LiveData
     * */
    private void initialiseViewModel() {
        dmActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(DMActivityViewModel.class);
        dmActivityViewModel.getContentLiveData().observe(this, resource -> {
            binding.setStatus(resource.size() > 0 ? com.example.more.data.Status.SUCCESS : com.example.more.data.Status.NOT_FOUND);
            mAdapter.setItems(resource);
        });
        dmActivityViewModel.loadDownloadList();
    }


}