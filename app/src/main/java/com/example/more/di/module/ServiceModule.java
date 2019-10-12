package com.example.more.di.module;

import com.example.more.fcm.MyFirebaseMessagingService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/*
 *  Module to inject specified list of services
 */
@Module
public abstract class ServiceModule {

    @ContributesAndroidInjector()
    abstract MyFirebaseMessagingService contributeFirebaseMessagingService ();


}