package com.example.more.ui.interfaces;

import android.view.View;

/**
 * interface to provide click events for
 * {@link com.example.more.ui.fragment.ContentFragment}
 * to be used in Data binding
 */
public interface ContentFragmentHandler {
    public void onClickView(View v);

    public void onClickViewTools(View v);
}
