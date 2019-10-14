package com.example.more.ui.interfaces;

import android.view.View;

/**
 * interface to provide click events for
 * {@link com.example.more.ui.fragment.SettingFragment}
 * to be used in Data binding
 */
public interface SettingsFragmentHandler {
    public void onHelpClickView(View v);
    public void shareApp(View v);
    public void onCreditClickView(View v);
}
