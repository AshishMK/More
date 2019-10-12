package com.example.more.di.module;

import com.example.more.ui.activity.CodeScanActivity;
import com.example.more.ui.activity.DownloadManagerActivity;
import com.example.more.ui.activity.ListActivity;
import com.example.more.ui.activity.MemePagerActivity;
import com.example.more.ui.activity.SearchActivity;
import com.example.more.ui.activity.SettingActivity;
import com.example.more.ui.activity.WhatsAppStatusActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/*
 *  Module to inject specified list of activities
 */
@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector()
    abstract ListActivity contributeMainActivity();

    @ContributesAndroidInjector(modules = {ToolsModule.class})
    abstract CodeScanActivity contributeCodeScanActivity();

    @ContributesAndroidInjector
    abstract WhatsAppStatusActivity contributeWhatsAppStatusActivity();

    @ContributesAndroidInjector
    abstract SearchActivity contributeSearchActivity();

    @ContributesAndroidInjector
    abstract DownloadManagerActivity contributeDownloadManagerActivity();

    @ContributesAndroidInjector
    abstract SettingActivity contributeSettingActivity();

    @ContributesAndroidInjector
    abstract MemePagerActivity contributeMemePagerActivity();
}