package com.example.more.data.repository;


import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.example.more.data.NetworkBoundResource;
import com.example.more.data.Resource;
import com.example.more.data.local.dao.ContentDao;
import com.example.more.data.local.entity.ContentEntity;
import com.example.more.data.local.entity.SearchEntity;
import com.example.more.data.remote.api.ContentApiService;
import com.example.more.data.remote.model.ContentEntityApiResponse;
import com.example.more.data.remote.model.SearchEntityApiResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/*
 * One of the first things we do in the Repository class
 * is to make it a Singleton.
 * */

@Singleton
public class SearchRepository {

    private ContentDao contentDao;
    private ContentApiService contentApiService;

    public SearchRepository(ContentDao contentDao,
                            ContentApiService contentApiService) {
        this.contentDao = contentDao;
        this.contentApiService = contentApiService;
    }


    String tag;

    /*
     * We are using this method to fetch the Search tags list
     * NetworkBoundResource is part of the Android architecture
     * components. You will notice that this is a modified version of
     * that class. That class is based on LiveData but here we are
     * using Observable from RxJava.
     *
     * There are three methods called:
     * a. fetch data from server
     * b. fetch data from local
     * c. save data from api in local
     *
     * So basically we fetch data from server, store it locally
     * and then fetch data from local and update the UI with
     * this data.
     *
     * */
    public Observable<Resource<List<SearchEntity>>> searchTags( String tag) {
        this.tag = tag;
        return new NetworkBoundResource<List<SearchEntity>, SearchEntityApiResponse>() {

            @Override
            protected void saveCallResult(@NonNull SearchEntityApiResponse item) {

                contentDao.insertTags(item.getResults());
            }

            @Override
            protected ContentDao getDAO() {
                return contentDao;
            }

            @Override
            protected boolean shouldFetch() {
                return true;
            }

            @NonNull
            @Override
            protected Flowable<List<SearchEntity>> loadFromDb() {
                List<SearchEntity> movieEntities = contentDao.searchTags(tag);
                if (movieEntities == null || movieEntities.isEmpty()) {
                    return Flowable.just(new ArrayList<>());
                }
                return Flowable.just(movieEntities);
            }

            @NonNull
            @Override
            protected Observable<Resource<SearchEntityApiResponse>> createCall() {
                return contentApiService.searchTags(RequestBody.create(MediaType.parse("text/plain"),tag))
                        .flatMap(movieApiResponse -> Observable.just(movieApiResponse == null
                                ? Resource.error("", new SearchEntityApiResponse())
                                : Resource.success(movieApiResponse)));
            }
        }.getAsObservable();
    }
}
