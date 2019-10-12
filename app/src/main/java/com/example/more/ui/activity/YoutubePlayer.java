package com.example.more.ui.activity;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.util.List;


/**
 * Activity to play Youtube video when content type is
 * {@link ListActivity#MEDIA}
 *
 * @see {@link com.example.more.ui.adapter.ContentListAdapter} for uses
 */
public class




YoutubePlayer extends AppCompatActivity {
    public static final String DEVELOPER_KEY = "AIzaSyDg9DTVi-5ZRMenoTTMsZrwZsv-Zr2i9rU";
    private static final int REQ_START_STANDALONE_PLAYER = 1;
    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = null;
        intent = YouTubeStandalonePlayer.createVideoIntent(this, DEVELOPER_KEY, getIntent().getStringExtra("url"), 0, true, false);

        if (intent != null) {
            if (canResolveIntent(intent)) {
                startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
            } else {
                // Could not resolve the intent - must need to install or update the YouTube API service.
                YouTubeInitializationResult.SERVICE_MISSING
                        .getErrorDialog(this, REQ_RESOLVE_SERVICE_MISSING).show();
            }
        }
    }


    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_START_STANDALONE_PLAYER && resultCode != RESULT_OK) {
            YouTubeInitializationResult errorReason =
                    YouTubeStandalonePlayer.getReturnedInitializationResult(data);
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(this, 0).show();
            } else {
                String errorMessage = errorReason.toString();
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
        finish();
    }
}
