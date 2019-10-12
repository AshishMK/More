package com.example.more.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.more.R;
import com.example.more.databinding.NotificationActivityBinding;

/**
 * Activity to host{@link com.example.more.ui.fragment.NotificationFragment} to
 * display list of notifications
 *
 */
public class NotificationActivity extends AppCompatActivity  {
    /*
     * I am using DataBinding
     * */
    private NotificationActivityBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView() {
        binding = DataBindingUtil.setContentView(this, R.layout.notification_list);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.title.setText(R.string.notification);
    }

}
