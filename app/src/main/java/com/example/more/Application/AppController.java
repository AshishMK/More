package com.example.more.Application;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.media.AudioAttributes;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.multidex.MultiDexApplication;

import com.example.more.BuildConfig;
import com.example.more.R;
import com.example.more.data.local.pref.PreferencesStorage;
import com.example.more.data.remote.api.ContentApiService;
import com.example.more.di.AppComponent.DaggerAppComponent;
import com.example.more.utills.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import timber.log.Timber;


/*
 * Application class
 */
public class AppController extends MultiDexApplication implements HasSupportFragmentInjector, HasActivityInjector, HasServiceInjector {
    private static AppController mInstance;
    @Inject
    ContentApiService contentApiService;

    /**
     * Provide {@link android.content.SharedPreferences} operations
     */
    @Inject
    PreferencesStorage preferenceStorage;

    public static final String FCM_UPDATED = "fcm_updated";

    /*Inject android activities @see @link{#ActivityModule} for list of injected activities*/
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    /*Inject android fragments @see @link{#FragmentModule} for list of injected fragments*/
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingFragmentAndroidInjector;

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingFragmentAndroidInjector;
    }

    /*Inject android services @see @link{#ServiceModule} for list of injected services*/
    @Inject
    DispatchingAndroidInjector<Service> dispatchingServiceAndroidInjector;

    @Override
    public DispatchingAndroidInjector<Service> serviceInjector() {
        return dispatchingServiceAndroidInjector;
    }

    /*returns Appplication object or application context
     * */
    public static synchronized AppController getInstance() {
        return mInstance;
    }


    public boolean enableAd = true;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        mInstance = this;
        //print logs only in debug mode
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // initialization of Dagger Dependency injection library
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this);
        Utils.createNotificationChannel();

    }


    /**
     * Method check and update newest FCM token to server if it is not updated yet
     */
    public void checkFCMUpdate() {
        //check weather FCM token is not updated to server
        if (!(boolean) (preferenceStorage.readValue(FCM_UPDATED, false))) {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Timber.v("getInstanceId failed " + task.getException());
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();

                            updateToken(token);
                            Timber.v(token);
                        }
                    });
        }
    }

    /**
     * Update  given token on server
     *
     * @param fcm
     */
    public void updateToken(String fcm) {

        contentApiService.insert_fcm(RequestBody.create(MediaType.parse("text/plain"), fcm)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(fcmApiResponse -> {
            preferenceStorage.writeValue(FCM_UPDATED, fcmApiResponse.getResult());
        }, throwable -> {
            Timber.v(throwable.toString());
            preferenceStorage.writeValue(FCM_UPDATED, false);
        });
    }
}

