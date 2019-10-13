
package com.example.more.ui.activity;

import android.os.Bundle;
import android.transition.Slide;
import android.transition.Visibility;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.example.more.R;
import com.example.more.databinding.WhatsappStatusAcivityBinding;
import com.example.more.utills.AlertDialogProvider;
import com.example.more.utills.Utils;
import com.google.android.gms.ads.MobileAds;

/**
 * Activity hosts {@link com.example.more.ui.fragment.WhatsappStatusFragment}
 * that display and download whatsapp status
 */
public class WhatsAppStatusActivity extends AppCompatActivity {

    /*
     * I am using DataBinding
     * */
    private WhatsappStatusAcivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseView();
        initAdMob();
    }

    /**
     * Method to initialize admob sdk to show ads
     */
    public void initAdMob() {
        Utils.buildBannerAD(binding.adView);
        Utils.buildInterstitialAd(this);
    }

    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_whats_app_status);
        // Performing window enter Activity transition animation
        getWindow().setEnterTransition(buildEnterTransition());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "info").setIcon(ActivityCompat.getDrawable(this, R.drawable.ic_info_outline_black_24dp)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {

            AlertDialogProvider alertDialogProvider = AlertDialogProvider.getInstance(getString(R.string.download_location), String.format(getString(R.string.whats_app_head_message), getString(R.string.app_name) + " directory"), AlertDialogProvider.TYPE_NORMAL);
            alertDialogProvider.setAlertDialogListener(new AlertDialogProvider.AlertDialogListener() {
                @Override
                public void onDialogCancel() {

                }

                @Override
                public void onDialogOk(String text) {
                    Utils.openFileManager();
                }
            });
            alertDialogProvider.show(getSupportFragmentManager(), WhatsAppStatusActivity.class.getName());
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @return method to create and return explode transition for activity window
     */

    private Visibility buildEnterTransition() {
        Slide enterTransition = new Slide();
        enterTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        enterTransition.setSlideEdge(Gravity.RIGHT);
        return enterTransition;
    }
}
