package com.example.more.di.module;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.room.Room;


import com.example.more.data.local.AppDatabase;
import com.example.more.data.local.dao.ContentDao;
import com.example.more.data.local.pref.PreferencesStorage;
import com.example.more.data.local.pref.SharedPrefStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {

    /*
     * The method returns the Database object
     * */
    @Provides
    @Singleton
    AppDatabase provideDatabase(@NonNull Application application) {
        return Room.databaseBuilder(application,
                AppDatabase.class, "Entertainment.db")
                .allowMainThreadQueries().build();
    }


    /*
     * We need the ContentDao module.
     * For this, We need the AppDatabase object
     * So we will define the providers for this here in this module.
     * */

    @Provides
    @Singleton
    ContentDao provideMovieDao(@NonNull AppDatabase appDatabase) {
        return appDatabase.contentDao();
    }

    @Provides
    @Singleton
    public PreferencesStorage providePreferenceStorage(Application context){
        return new SharedPrefStorage(context);
}
}
