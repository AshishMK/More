package com.example.more.di.module;

import android.app.Application;

import com.example.more.data.remote.api.ContentApiService;
import com.example.more.data.remote.interceptor.RequestInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2rx.RxFetch;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * Module to provide api call functionality
 */
@Module
public class ApiModule {
    public static final String base_url = "http://indianmessenger.in:3040/";

    /**
     * The method returns the {@link RxFetchobject}
     *
     * @return
     */
    @Provides
    @Singleton
    RxFetch provideRxFetch(FetchConfiguration fetchConfiguration) {
        return RxFetch.Impl.getRxInstance(fetchConfiguration);
    }

    /**
     * The method returns the {@link FetchConfiguration}
     *
     * @return
     */
    @Provides
    @Singleton
    FetchConfiguration provideFetchConfiguration(Application application){
        return new FetchConfiguration.Builder(application).build();
    }

    /*
     * The method returns the Gson object
     * */
    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }


    /*
     * The method returns the Cache object
     * */
    @Provides
    @Singleton
    Cache provideCache(Application application) {
        long cacheSize = 10 * 1024 * 1024; // 10 MB
        File httpCacheDirectory = new File(application.getCacheDir(), "http-cache");
        return new Cache(httpCacheDirectory, cacheSize);
    }


    /*
     * The method returns the Okhttp object
     * */
    @Provides
    @Singleton
    OkHttpClient provideOkhttpClient(Cache cache) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.cache(cache);
        httpClient.addInterceptor(logging);
        httpClient.addNetworkInterceptor(new RequestInterceptor());
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        return httpClient.build();
    }


    /*
     * The method returns the Retrofit object
     * */
    @Provides
    @Singleton
    Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(base_url)
                .client(okHttpClient)
                .build();
    }

    /*
     * We need the ContentApiService module.
     * For this, We need the Retrofit object, Gson, Cache and OkHttpClient .
     * So we will define the providers for these objects here in this module.
     *
     * */

    @Provides
    @Singleton
    ContentApiService provideMovieApiService(Retrofit retrofit) {
        return retrofit.create(ContentApiService.class);
    }
}
