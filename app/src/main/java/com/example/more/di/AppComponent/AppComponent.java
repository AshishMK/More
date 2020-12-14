package com.example.more.di.AppComponent;

import android.app.Application;

import com.example.more.Application.AppController;
import com.example.more.di.module.ActivityModule;
import com.example.more.di.module.ApiModule;
import com.example.more.di.module.DbModule;
import com.example.more.di.module.ExoPlayerModule;
import com.example.more.di.module.FragmentModule;
import com.example.more.di.module.ServiceModule;
import com.example.more.di.module.ToolsModule;
import com.example.more.di.module.ViewModelModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;


/*
 * We mark this interface with the @Component annotation.
 * And we define all the modules that can be injected.
 * Note that we provide AndroidSupportInjectionModule.class
 * here. This class was not created by us.
 * It is an internal class in Dagger 2.10.
 * Provides our activities and fragments with given module.
 * */
@Component(modules = {
        ApiModule.class,
        DbModule.class,
        ExoPlayerModule.class,
        ViewModelModule.class,
        ActivityModule.class,
        FragmentModule.class,
        ServiceModule.class,
        AndroidSupportInjectionModule.class})
@Singleton
public interface AppComponent {


    /* We will call this builder interface from our custom Application class.
     * This will set our application object to the AppComponent.
     * So inside the AppComponent the application instance is available.
     * So this application instance can be accessed by our modules
     * such as ApiModule when needed
     * */
    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }


    /*
     * This is our custom Application class
     * */
    void inject(AppController appController);
}


