package com.example.more.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.more.BR;
import com.example.more.R;
import com.example.more.data.local.pref.PreferencesStorage;
import com.example.more.databinding.SettingActivityBinding;
import com.example.more.ui.fragment.SettingFragment;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Activity to host {@link com.example.more.ui.fragment.SettingFragment}
 * to display list of setting for the application
 */
public class SettingActivity extends AppCompatActivity {
    /**
     * Provide {@link android.content.SharedPreferences} operations
     */
    @Inject
    PreferencesStorage preferenceStorage;
    /*
     * I am using DataBinding
     * */
    private SettingActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Remember in our {@link com.example.more.di.module.ActivityModule}, we
         * defined {@link SettingActivity} injection? So we need
         * to call this method in order to inject the
         * ViewModelFactory into our Activity
         * */

        AndroidInjection.inject(this);
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        binding.setVariable(BR.preferenceStorage, preferenceStorage);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.title.setText(R.string.action_settings);
    }

}
