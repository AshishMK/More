package com.example.more.di.module;

import com.example.more.ui.activity.CodeScanActivity;
import com.google.zxing.integration.android.IntentIntegrator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public  class ToolsModule {

    /*
     * The method returns the Zxing Scanner object
     * */
    @Provides
     IntentIntegrator provideScanner(CodeScanActivity activity){
    return new IntentIntegrator(activity);
    }

}