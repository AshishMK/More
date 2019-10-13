package com.example.more.utills;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.example.more.Application.AppController;
import com.example.more.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.io.File;

/**
 * Class to provide application related all utilities
 */
public class Utils {

    public static final String ADMOB_TEST_DEVICE = "90DE0FF5D1BEB82ACBE8518D057B6FA5";

    /**
     * Method to provide mime type of a file provided by file uri
     *
     * @param uri
     * @return
     */
    public static String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme() != null) {
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                ContentResolver cr = AppController.getInstance().getContentResolver();
                mimeType = cr.getType(uri);
            } else {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                        .toString());
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        fileExtension.toLowerCase());
            }
        } else {
            mimeType = "*/*";
        }
        return mimeType;
    }

    /**
     * Returns weather th Android version has lollipop and above or not
     *
     * @return
     */
    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * Method to perform Permission model request response operations for the app with
     * fallback functionality {@link Utils#openSettingApp}
     *
     * @param activity
     * @param PERMISSION_REQUEST_CODE
     * @param permission
     * @param fragment
     * @return
     */
    public static PermissionStatus checkPermissionsCamera(Activity activity, int PERMISSION_REQUEST_CODE, String permission, Fragment fragment) {
        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permission)) {
                Toast.makeText(activity, "Please provide Storage permission to use this functionality", Toast.LENGTH_LONG).show();
                openSettingApp(activity);
                return PermissionStatus.ERROR;
            } else {
                if (fragment == null) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{permission}, PERMISSION_REQUEST_CODE);
                    return PermissionStatus.REQUESTED;
                } else {
                    fragment.requestPermissions(new String[]{permission}, PERMISSION_REQUEST_CODE);
                    return PermissionStatus.REQUESTED;

                }
            }

        } else {
            return PermissionStatus.SUCCESS;
        }

    }

    /**
     * method to visit the credits page for the app
     */
    public static void gotoAppCredits() {
        Uri uri = Uri.parse("https://drive.google.com/file/d/168SS0sDYYTaru9sfOAbKPim_duhYs5m9/view?usp=sharing");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
        AppController.getInstance().startActivity(browserIntent);
    }

    /**
     * Method to open App's Settings info screen to manually revoke permissions
     * its a fallback for permission model
     *
     * @param ctx
     */
    public static void openSettingApp(Context ctx) {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + AppController.getInstance().getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        ctx.startActivity(intent);
    }

    public enum PermissionStatus {
        SUCCESS,
        ERROR,
        REQUESTED
    }

    /**
     * Method to open help mail
     */
    public static void askHelp() {
        PackageManager pm = AppController.getInstance().getPackageManager();
        PackageInfo pi;
        String subject = AppController.getInstance().getString(R.string.help_subject);

        try {
            pi = pm.getPackageInfo(AppController.getInstance().getPackageName(), 0);
            subject = String.format(subject, AppController.getInstance().getString(R.string.app_name), pi.versionName);
        } catch (Exception e) {
            subject = String.format(subject, AppController.getInstance().getString(R.string.app_name), "*");
        }
        String uriText = "mailto:" + AppController.getInstance().getString(R.string.help_mail) +
                "?subject=" + Uri.encode(subject);

        Uri uri = Uri.parse(uriText);
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(uri);
        AppController.getInstance().startActivity(Intent.createChooser(sendIntent, AppController.getInstance().getString(R.string.help_email_title)));

    }

    /**
     * return bitmap from vector drawables
     * ((BitmapDrawable) AppCompatResources.getDrawable(getTarget().getContext(), R.drawable.ic_thin_arrowheads_pointing_down)).getBitmap()
     */
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    /**
     * Return the media directory of the application
     *
     * @return
     */
    public static File getParentFile() {
        return new File(Environment.getExternalStorageDirectory().toString() + "/" + AppController.getInstance().getString(R.string.app_name) + "/");
    }

    /**
     * Method to open File manage app
     */
    public static void openFileManager() {
        Uri imageUri = FileProvider.getUriForFile(AppController.getInstance(), AppController.getInstance().getPackageName() + ".provider", getParentFile());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(imageUri, "*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivityInfo(
                AppController.getInstance().getPackageManager(), 0) != null) {
            AppController.getInstance().startActivity(intent);
        } else {
        }
    }

    /**
     * Method to send media file to OS scanning
     * so it can immediately available for the rest of the system apps.
     *
     * @param destFile
     */
    public static void sendFileToScan(File destFile) {
        MediaScannerConnection.scanFile(AppController.getInstance(), new String[]{destFile.getAbsolutePath()},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            final Uri contentUri = Uri.fromFile(destFile);
            scanIntent.setData(contentUri);
            AppController.getInstance().sendBroadcast(scanIntent);
        } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(destFile.getAbsolutePath()));
            AppController.getInstance().sendBroadcast(intent);
        }

    }

    public static void shareFile(String path) {
        File shareFile = new File(path);
        Uri imageUri = FileProvider.getUriForFile(AppController.getInstance(), AppController.getInstance().getPackageName() + ".provider", shareFile);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);


        final String final_text = AppController.getInstance().getString(R.string.download_app);
        shareIntent.putExtra(Intent.EXTRA_TEXT, final_text);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType(Utils.getMimeType(imageUri));
        AppController.getInstance().startActivity(shareIntent);
    }


    /* Create the NotificationChannel, but only on API 26+ because
    the NotificationChannel class is new and not in the support library
   */
    public static void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = AppController.getInstance().getString(R.string.notification_channel);
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            String description = AppController.getInstance().getString(R.string.notification_channel_msg);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = AppController.getInstance().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            //Silent notification channel
            String NOTIFICATION_CHANNEL_ID = "2";
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, AppController.getInstance().getString(R.string.silent_notification), NotificationManager.IMPORTANCE_LOW
            );
            //Configure the notification channel, NO SOUND
            notificationChannel.setDescription(AppController.getInstance().getString(R.string.silent_notification_msg));
            notificationChannel.setSound(null, null);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public static void buildInterstitialAd(Activity activity) {
        if(!AppController.getInstance().enableAd){
            return;
        }
        InterstitialAd mInterstitialAd = new InterstitialAd(activity);
        mInterstitialAd.setAdUnitId(activity.getString(R.string.ADMOB_APP_INTERSTITIAL_ID));
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice(ADMOB_TEST_DEVICE).build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }
        });
    }

    public static void buildBannerAD(AdView adView) {
        if(!AppController.getInstance().enableAd){
            return;
        }
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(ADMOB_TEST_DEVICE).build();
        adView.loadAd(adRequest);
    }


    public static void buildRewardedAd(Activity activity) {
        if(!AppController.getInstance().enableAd){
            return;
        }
        RewardedAd rewardedAd = new RewardedAd(activity, activity.getString(R.string.ADMOB_APP_REWARDED_ID));
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                rewardedAd.show(activity, new RewardedAdCallback() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                    }
                });
            }


            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().addTestDevice(ADMOB_TEST_DEVICE).build(), adLoadCallback);

    }
}
