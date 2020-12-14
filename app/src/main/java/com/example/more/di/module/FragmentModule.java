package com.example.more.di.module;

import com.example.more.ui.fragment.FacebookFragment;
import com.example.more.ui.fragment.ListFragment;
import com.example.more.ui.fragment.NotificationFragment;
import com.example.more.ui.fragment.PlayerFragment;
import com.example.more.ui.fragment.SettingFragment;
import com.example.more.ui.fragment.WhatsappStatusFragment;
import com.example.more.ui.fragment.YoutubeFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/*
 *  Module to inject specified list of fragments
 */
@Module
public abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract ListFragment contributeListFragment();

    @ContributesAndroidInjector
    abstract PlayerFragment contributePlayerFragment();

    @ContributesAndroidInjector
    abstract YoutubeFragment contributeYoutubeFragment();

    @ContributesAndroidInjector
    abstract WhatsappStatusFragment whatsappStatusFragment();

    @ContributesAndroidInjector
    abstract NotificationFragment notificationFragment();

    @ContributesAndroidInjector
    abstract SettingFragment settingFragment();

    @ContributesAndroidInjector
    abstract FacebookFragment facebookFragment();
}