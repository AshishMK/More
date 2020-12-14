package com.example.more.ui.activity;

import android.view.View;

public interface PlayerActivityHandler {
    public void onReportClicked(View view);
    public void onRotateClicked(View view);
    public void onPlayPauseClicked(View view);
    public void onVolumeClicked(View view);
    public void onSettingsClicked(View view);
    public void showDetail(View view);
    public void onThemeChanged(View view);
    public void onVideoCrop(View view);
    public void hideInfoLayout(View view);
    public void onRetry(View view);
}
