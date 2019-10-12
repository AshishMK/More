package com.example.more.ui.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.more.Application.AppController;
import com.example.more.R;
import com.example.more.databinding.ScrollingActivityBinding;
import com.example.more.databinding.TabItemBinding;
import com.example.more.ui.adapter.PagerAdapter;
import com.example.more.ui.fragment.ContentFragment;

import static com.example.more.utills.Screen.dp;

public class HomeActivity extends AppCompatActivity {
    /*
     * I am using DataBinding
     * */
    private ScrollingActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.getInstance().checkFCMUpdate();
        initialiseView();
        createNotificationChannel();
    }


    /* Create the NotificationChannel, but only on API 26+ because
     the NotificationChannel class is new and not in the support library
    */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel);
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            String description = getString(R.string.notification_channel_msg);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            //Silent notification channel
            String NOTIFICATION_CHANNEL_ID = "2";
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, getString(R.string.silent_notification), NotificationManager.IMPORTANCE_LOW
            );
            //Configure the notification channel, NO SOUND
            notificationChannel.setDescription(getString(R.string.silent_notification_msg));
            notificationChannel.setSound(null, null);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scrolling);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupViewPager();
    }

    /**
     * Method to configure {@link androidx.viewpager.widget.ViewPager}
     * and {@link com.google.android.material.tabs.TabLayout}
     *
     * @see  PagerAdapter
     * <p>
     * Basically we add two {@link ContentFragment} here
     * one for Contents and other for tools
     */
    void setupViewPager() {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        TabItemBinding tabItemBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.tab_item, (ViewGroup) binding.includedLayout.viewpager.getRootView(), false);
        tabItemBinding.tabLogo.setImageResource(R.drawable.ic_cup_of_hot_chocolate);

        TabItemBinding tabItemBinding2 = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.tab_item, (ViewGroup) binding.includedLayout.viewpager.getRootView(), false);
        int pad = dp(2);
        ((ViewGroup.MarginLayoutParams) tabItemBinding.tabLogo.getLayoutParams()).leftMargin = dp(3.5f);
        tabItemBinding2.tabLogo.setPadding(pad, dp(6), pad, 0);
        tabItemBinding2.tabLogo.setImageResource(R.drawable.ic_handyman_tools);

        adapter.addFrag(ContentFragment.getInstance(false), "");
        adapter.addFrag(ContentFragment.getInstance(true), "");
        binding.includedLayout.viewpager.setAdapter(adapter);
        binding.includedLayout.tabLayout.setupWithViewPager(binding.includedLayout.viewpager);

        binding.includedLayout.tabLayout.getTabAt(0).setCustomView(tabItemBinding.getRoot());
        binding.includedLayout.tabLayout.getTabAt(1).setCustomView(tabItemBinding2.getRoot());
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notification) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
