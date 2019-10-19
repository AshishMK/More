package com.example.more.data.repository;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.more.data.NetworkBoundResource;
import com.example.more.data.Resource;
import com.example.more.data.local.dao.ContentDao;
import com.example.more.data.local.entity.ContentEntity;
import com.example.more.data.remote.api.ContentApiService;
import com.example.more.data.remote.model.ContentEntityApiResponse;

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
public class ServiceRepository {

    private ContentDao contentDao;
    private ContentApiService contentApiService;

    public ServiceRepository(ContentDao contentDao,
                             ContentApiService contentApiService) {
        this.contentDao = contentDao;
        this.contentApiService = contentApiService;
    }


    int content_type;
    String tag;

    /*
     * We are using this method to fetch the content list
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
    public Observable<Resource<ContentEntity>> loadContentByTag(int contentType, int offset, String tag) {
        this.content_type = contentType;
        this.tag = tag;
        return new NetworkBoundResource<ContentEntity, ContentEntityApiResponse>() {

            @Override
            protected void saveCallResult(@NonNull ContentEntityApiResponse item) {
                contentDao.insertContents(item.getResults());
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
            protected Flowable<ContentEntity> loadFromDb() {
                ContentEntity contentEntity = contentDao.getContentByTag(tag);
                if (contentEntity == null)
                {
                    return Flowable.just(new ContentEntity());

                }
                return Flowable.just(contentEntity);
            }

            @NonNull
            @Override
            protected Observable<Resource<ContentEntityApiResponse>> createCall() {

                return (contentApiService.fetchContentByTag(contentType, offset, RequestBody.create(MediaType.parse("text/plain"), tag)))
                        .flatMap(movieApiResponse -> Observable.just(movieApiResponse == null
                                ? Resource.error("", new ContentEntityApiResponse())
                                : Resource.success(movieApiResponse)));
            }
        }.getAsObservable();
    }
}
