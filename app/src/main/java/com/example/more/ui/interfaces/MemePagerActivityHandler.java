package com.example.more.ui.interfaces;

import android.view.View;

import com.tonyodev.fetch2.Download;

/**
 * interface to provide click events for
 * {@link com.example.more.ui.fragment.ListFragment}
 * to be used in Data binding
 */
public interface MemePagerActivityHandler {
    public void onClickDownload(Download download);

    public void onTipClicked(View v);

    public void onBackClick(View v);

}
