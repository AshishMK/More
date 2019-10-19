package com.example.more.di.module;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


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
                .addMigrations(MIGRATION_1_2)
                .allowMainThreadQueries().build();
    }


    /**Add new column to star a content entity*/
    static final Migration MIGRATION_1_2= new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE ContentEntity "
                    + " ADD COLUMN isStarred INTEGER DEFAULT 0 NOT NULL");
        }
    };

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
