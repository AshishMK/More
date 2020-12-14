package com.example.more.ui.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.more.Application.AppController;
import com.example.more.R;
import com.example.more.ui.activity.HomeActivity;
import com.example.more.utills.Utils;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;


public class VideoAudioMergerService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "1")
                .setContentTitle("Processing your video")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_file_download_black_24dp)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(UUID.randomUUID().hashCode(), notification);
        convertVideoToMp3(intent.getStringExtra("audio"));

        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

    void downloaded(String name){
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        builder.setOngoing(false);
        builder.setContentTitle(AppController.getInstance().getString(R.string.video_downloaded));
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ic_file_download_black_24dp);
        builder.setTicker(AppController.getInstance().getString(R.string.video_downloaded));
        builder.setContentText(AppController.getInstance().getString(R.string.app_name) + "/" + name);
        builder.setProgress(0, 0, false);
        ((NotificationManager) AppController.getInstance().getSystemService(Context.NOTIFICATION_SERVICE)).notify(UUID.randomUUID().hashCode(), builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void convertVideoToMp3(String audio) {
        String output = new File(Utils.getYoutubeResource(), new File(audio).getName().replaceAll("UtubeMp3_", "UtubeMp33_") + ".mp3").getAbsolutePath();
        String outputVideo = new File(audio).getName().replaceAll("UtubeMp3_", "Utube_");
        outputVideo = new File(Utils.getYoutubeResource(), outputVideo.substring(0, outputVideo.lastIndexOf(".")) + ".mp4").getAbsolutePath();
        if(!new File(outputVideo).exists()){
            System.out.println("cd mp3 convert WEBM");
            outputVideo = outputVideo.replace(".mp4", ".webm");
        }
        System.out.println("cd mp3 convert vid " + outputVideo);
        String finalOutputVideo = outputVideo;
        EpEditor.demuxer(audio, output, EpEditor.Format.MP3, new OnEditorListener() {
            @Override
            public void onSuccess() {
                System.out.println("cd mp3 convert comp");
new Handler(VideoAudioMergerService.this.getMainLooper()).post(new Runnable() {
    @Override
    public void run() {
        deleteFile(new File(audio));
        mergeMp3(finalOutputVideo, output);
    }
});



            }

            @Override
            public void onFailure() {
                System.out.println("cd mp3 convert failed");
            }

            @Override
            public void onProgress(float progress) {
                System.out.println("cd mp3 convert pr " + progress);
            }
        });
    }

   public void mergeMp3(String videoPath, String audioPath) {
        EpEditor.music(videoPath, audioPath, new File(Utils.getYoutubeResource(), new File(videoPath).getName().replaceAll("Utube_", "") + ".mp4").getAbsolutePath(), 0, 1, new OnEditorListener() {
            @Override
            public void onSuccess() {
                System.out.println("cd mp3 pr merge success");
                deleteFile(new File(videoPath));
                deleteFile(new File(audioPath));
        downloaded(new File(videoPath).getName().replaceAll("Utube_", "") );
                stopForeground(true);
            }

            @Override
            public void onFailure() {
                System.out.println("cd mp3 merge fail ");

            }

            @Override
            public void onProgress(float progress) {
                System.out.println("cd mp3 merge pr " + progress);
            }
        });
    }

    private static final ExecutorService DELETE_SERVICE = Executors.newSingleThreadExecutor();

    public static void deleteFile(final File file) {
        if (file != null) {
            DELETE_SERVICE.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("cd mp3 deleting " + file.getName() + file.delete());
                }
            });
        }
    }
}