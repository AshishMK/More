package com.example.more.utills;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.example.more.Application.AppController;
import com.example.more.R;

import java.io.File;

/**
 * Class to provide application related all utilities
 */
public class Utils {

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
/**return bitmap from vector drawables
 * ((BitmapDrawable) AppCompatResources.getDrawable(getTarget().getContext(), R.drawable.ic_thin_arrowheads_pointing_down)).getBitmap()
 * */
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

    public static void  shareFile(String path){
        File shareFile = new File( path);
        Uri imageUri = FileProvider.getUriForFile(AppController.getInstance(), AppController.getInstance().getPackageName() + ".provider", shareFile);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);


        final String final_text = AppController.getInstance().getString(R.string.download_app);
        shareIntent.putExtra(Intent.EXTRA_TEXT, final_text);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType(Utils.getMimeType(imageUri));
        AppController.getInstance().startActivity(shareIntent);
    }


}
