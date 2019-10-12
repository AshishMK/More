package com.example.more.ui.interfaces;

import android.view.View;
/**
 * interface to provide click events for
 * {@link com.example.more.ui.activity.SearchActivity}
 * to be used in Data binding
 */
public interface SearchActivityHandler {
    public void onBackClicked(View v);
    public void OnSearchClicked(View v);
}
