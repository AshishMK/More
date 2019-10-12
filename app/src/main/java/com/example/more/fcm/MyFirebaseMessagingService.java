package com.example.more.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.more.Application.AppController;
import com.example.more.R;
import com.example.more.data.local.entity.NotificationEntity;
import com.example.more.data.local.pref.PreferencesStorage;
import com.example.more.data.remote.api.ContentApiService;
import com.example.more.data.remote.model.FCMApiResponse;
import com.example.more.ui.activity.HomeActivity;
import com.example.more.ui.activity.ListActivity;
import com.example.more.ui.fragment.ListFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static androidx.core.app.NotificationCompat.PRIORITY_MAX;
import static com.example.more.Application.AppController.FCM_UPDATED;
import static com.example.more.di.module.ApiModule.base_url;
import static com.example.more.ui.activity.ListActivity.FACT;
import static com.example.more.ui.activity.ListActivity.QUOTE;
import static com.example.more.ui.activity.ListActivity.STORY;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * Provide {@link android.content.SharedPreferences} operations
     */
    @Inject
    PreferencesStorage preferenceStorage;

    @Override
    public void onCreate() {
        /**
         * Remember in our {@link com.example.more.di.module.ServiceModule}, we
         * defined {@link FirebaseMessagingService} injection? So we need
         * to call this method in order to inject the
         * ViewModelFactory into our service
         * */
        AndroidInjection.inject(this);

        super.onCreate();
    }

    private static final String TAG = "MyFirebaseMsgService";
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
    int clr[];

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            NotificationEntity notificationEntity = new NotificationEntity();
            notificationEntity.setTitle(remoteMessage.getData().get("title"));
            notificationEntity.setMedia(remoteMessage.getData().get("media"));
            notificationEntity.setReceivedAt(new Date().getTime());
            ////  contentDao.insertNotification(notificationEntity);
            System.out.println("jljl");
            if (showNotification(Integer.parseInt(remoteMessage.getData().get("content_type")))) {

            loadMedia(Integer.parseInt(remoteMessage.getData().get("content_type")), remoteMessage.getData().get("media"));
             }
        }
    }

    boolean showNotification(int type) {
        switch (type) {
            case FACT:
                return (boolean) preferenceStorage.readValue(getString(R.string.fact_notification), true);
            case QUOTE:
                return (boolean) preferenceStorage.readValue(getString(R.string.quote_notification), true);
            case STORY:
                return (boolean) preferenceStorage.readValue(getString(R.string.meme_notification), true);
            default:
                return (boolean) preferenceStorage.readValue(getString(R.string.movie_notification), true);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        preferenceStorage.writeValue(FCM_UPDATED, false);
        AppController.getInstance().updateToken(s);
    }

    void loadMedia(int type, String media) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Glide.with(MyFirebaseMessagingService.this).asBitmap()
                        .load((media != null && !media.contains(".png")) ? "http://img.youtube.com/vi/" + media + "/0.jpg" : base_url + "tnt_file/" + media)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                createCustomNotification(type, resource);
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                                System.out.println("jamenu e");
                            }
                        });

            }
        });
    }

    String getTitle(int type) {
        switch (type) {
            case 0:
                return this.getString(R.string.notif_fact);
            case 1:
                return this.getString(R.string.notif_quote);
            case 2:
                return this.getString(R.string.notif_meme);
            default:
                return this.getString(R.string.notif_video);
        }
    }

    private void createCustomNotification(int content_type, Bitmap image) {

        // BEGIN_INCLUDE(notificationCompat)
        String title = getTitle(content_type);
        //Create Intent to launch this Activity again if the notification is clicked.
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // stackBuilder.addParentStack(HomeActivity.class);
        //  stackBuilder.addNextIntent(intent);
        Intent listActivity = new Intent(this, ListActivity.class);
        listActivity.putExtra("content_type", content_type);
        //   stackBuilder.addNextIntent(listActivity);
        //  PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        PendingIntent pendingIntent = PendingIntent.getActivities(this, content_type, new Intent[]{intent, listActivity}, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, (boolean) preferenceStorage.readValue(getString(R.string.status_notification), true)?"1":"2")
                .setContentIntent(pendingIntent)
                .setTicker(title)
                .setSmallIcon(R.drawable.notif)
                .setAutoCancel(true)
                .setPriority(PRIORITY_MAX);
        // Build the notification
        Notification notification = builder.build();
        // END_INCLUDE(buildNotification)

        // BEGIN_INCLUDE(customLayout)
        // Inflate the notification layout as RemoteViews
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);

        // Set text on a TextView in the RemoteViews programmatically.
        final String time = sdf.format(new Date());
        notification.contentView = contentView;

        contentView.setTextViewText(R.id.title, title);
        contentView.setImageViewBitmap(R.id.imageView, image);
        contentView.setTextViewText(R.id.date_time, time);// Use the NotificationManager to show the notification
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(content_type, notification);
        // END_INCLUDE(notify)
    }


}