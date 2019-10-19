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
public class ContentRepository {

    private ContentDao contentDao;
    private ContentApiService contentApiService;

    public ContentRepository(ContentDao contentDao,
                             ContentApiService contentApiService) {
        this.contentDao = contentDao;
        this.contentApiService = contentApiService;
    }


    int content_type;
    String tag;
    boolean filter_starred;

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
    public Observable<Resource<List<ContentEntity>>> loadMoviesByType(int contentType, int offset, String tag, boolean filter_starred) {
        this.content_type = contentType;
        this.tag = tag;
        this.filter_starred = filter_starred;
        return new NetworkBoundResource<List<ContentEntity>, ContentEntityApiResponse>() {

            @Override
            protected void saveCallResult(@NonNull ContentEntityApiResponse item) {
                contentDao.insertContents(item.getResults());
            }

            @Override
            protected boolean shouldFetch() {
                return true;
            }

            @NonNull
            @Override
            protected Flowable<List<ContentEntity>> loadFromDb() {
                List<ContentEntity> movieEntities = (TextUtils.isEmpty(tag) ? (filter_starred ? contentDao.getContentByContentTypeWithStarred(content_type, offset == -1 ? 0 : offset,filter_starred) :contentDao.getContentByContentType(content_type, offset == -1 ? 0 : offset)) : (filter_starred ? contentDao.getContentByTagWithStarred(content_type, tag, offset == -1 ? 0 : offset,filter_starred):contentDao.getContentByTag(content_type, tag, offset == -1 ? 0 : offset)));
                if (movieEntities == null || movieEntities.isEmpty()) {
                    return Flowable.just(new ArrayList<>());
                }
                return Flowable.just(movieEntities);
            }

            @NonNull
            @Override
            protected ContentDao getDAO() {
                return contentDao;
            }

            @NonNull
            @Override
            protected Observable<Resource<ContentEntityApiResponse>> createCall() {

                return (TextUtils.isEmpty(tag) ? contentApiService.fetchContent(contentType, offset) : contentApiService.fetchContentByTag(contentType, offset, RequestBody.create(MediaType.parse("text/plain"), tag)))
                        .flatMap(movieApiResponse -> Observable.just(movieApiResponse == null
                                ? Resource.error("", new ContentEntityApiResponse())
                                : Resource.success(movieApiResponse)));
            }
        }.getAsObservable();
    }
}
