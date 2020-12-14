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

package com.example.more.utills;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.more.Application.AppController;
import com.example.more.R;
import com.example.more.receiver.CancelReceiver;
import com.example.more.ui.activity.DownloadManagerActivity;
import com.example.more.ui.activity.HomeActivity;
import com.example.more.ui.services.VideoAudioMergerService;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2core.DownloadBlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;

/**
 * Class to provide notifications about facebook video download and
 * progress of dialog
 *
 * @see com.example.more.ui.fragment.FacebookFragment for uses.
 */
@SuppressLint("RestrictedApi")
public class FileDownloadNotificationListener implements FetchListener {
    private NotificationCompat.Builder builder;
    private NotificationManager manager;

    public FileDownloadNotificationListener() {
        initNotification();
    }


    public void initNotification() {
        manager = (NotificationManager) AppController.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(AppController.getInstance(), HomeActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent listActivity = new Intent(AppController.getInstance(), DownloadManagerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivities(AppController.getInstance(), 0, new Intent[]{intent, listActivity}, PendingIntent.FLAG_CANCEL_CURRENT);
        builder = new NotificationCompat.Builder(AppController.getInstance(), "1");
        builder.setDefaults(Notification.DEFAULT_LIGHTS)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)

                .setSmallIcon(R.drawable.ic_file_download_black_24dp);
    }

    @Override
    public void onAdded(@NotNull Download download) {

    }


    @Override
    public void onCancelled(@NotNull Download download) {
        builder.mActions.clear();
        builder.setOngoing(false);
        builder.setContentTitle(AppController.getInstance().getString(R.string.download_canceled));
        builder.setAutoCancel(true);
        builder.setTicker(AppController.getInstance().getString(R.string.download_canceled));
        builder.setContentText(new File(download.getFile()).getName());
        builder.setProgress(0, 0, false);
        manager.notify(download.getId(), builder.build());
    }

    @Override
    public void onCompleted(@NotNull Download download) {
        System.out.println("cd mp3 completed " + download.getFile());
        builder.mActions.clear();
        builder.setOngoing(false);
        builder.setContentTitle(AppController.getInstance().getString(R.string.video_downloaded));
        builder.setAutoCancel(true);
        builder.setTicker(AppController.getInstance().getString(R.string.video_downloaded));
        builder.setContentText(AppController.getInstance().getString(R.string.app_name) + "/" + new File(download.getFile()).getName());
        builder.setProgress(0, 0, false);
        manager.notify(download.getId(), builder.build());

        if (new File(download.getFile()).getName().startsWith("UtubeMp3_")) {
            manager.cancel(download.getId());
            startService(download.getFile());

            return;
        }
        if (new File(download.getFile()).getName().startsWith("Utube_")) {
            manager.cancel(download.getId());

            return;
        }
        Utils.sendFileToScan(new File(download.getFile()));
    }

    @Override
    public void onDeleted(@NotNull Download download) {
        manager.cancel(download.getId());
    }

    @Override
    public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int totalBlocks) {

    }

    @Override
    public void onError(@NotNull Download download, @NotNull Error error, @Nullable Throwable throwable) {
        builder.setOngoing(false);
        builder.mActions.clear();
        builder.setAutoCancel(true);
        builder.setContentTitle(AppController.getInstance().getString(R.string.error_downloading) + new File(download.getFile()).getName());
        builder.setTicker(AppController.getInstance().getString(R.string.error_downloading));
        builder.setContentText(error.name() + " " + throwable.getMessage());
        builder.setProgress(1, 1, false);
        manager.notify(download.getId(), builder.build());
    }

    @Override
    public void onPaused(@NotNull Download download) {

    }

    @Override
    public void onProgress(@NotNull Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {
        //builder.setContentText("downloading with speed: " + downloadedBytesPerSecond);
        builder.setContentText(AppController.getInstance().getResources().getString(R.string.percent_progress, download.getProgress()));
        System.out.println("sdsd " + download.getTotal() + " / " + download.getProgress());
        builder.setContentTitle(AppController.getInstance().getString(R.string.downloading) + new File(download.getFile()).getName());
        builder.setTicker(AppController.getInstance().getString(R.string.downloading));
        setAction(download);
        builder.setProgress(100, (int) download.getProgress(), false);
        manager.notify(download.getId(), builder.build());
    }

    @Override
    public void onQueued(@NotNull Download download, boolean waitingOnNetwork) {

        builder.setContentTitle(AppController.getInstance().getString(R.string.on_queue));
        builder.setTicker(AppController.getInstance().getString(R.string.downloading) + new File(download.getFile()).getName());
        builder.setOngoing(true);
        builder.setAutoCancel(false);
        //builder.setContentText(AppController.getInstance().getString(R.string.downloading));
        builder.setContentText(AppController.getInstance().getResources().getString(R.string.percent_progress, 0));
        builder.setProgress(0, 0, true);
        setAction(download);
        manager.notify(download.getId(), builder.build());
    }

    @Override
    public void onRemoved(@NotNull Download download) {

    }

    @Override
    public void onResumed(@NotNull Download download) {

    }

    @Override
    public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int totalBlocks) {

    }

    @Override
    public void onWaitingNetwork(@NotNull Download download) {
        builder.setTicker(AppController.getInstance().getString(R.string.downloading));
        builder.setOngoing(true);
        builder.setAutoCancel(false);
        builder.setContentTitle(AppController.getInstance().getString(R.string.downloading));
        builder.setContentText(AppController.getInstance().getString(R.string.waiting_for_network));
        builder.setProgress(0, 0, true);
        manager.notify(download.getId(), builder.build());
    }

    public void setAction(Download download) {
        builder.mActions.clear();
        final Intent intentAction = new Intent(AppController.getInstance(), CancelReceiver.class);
        intentAction.putExtra("id", download.getId());
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(AppController.getInstance(), 0, intentAction,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(new NotificationCompat.Action(0, AppController.getInstance().getString(R.string.cancel), cancelPendingIntent));

    }


    public void startService(String audio) {
        Intent serviceIntent = new Intent(AppController.getInstance(), VideoAudioMergerService.class);
        serviceIntent.putExtra("audio", audio);
        ContextCompat.startForegroundService( AppController.getInstance(), serviceIntent);
    }
}