package com.example.more.data.repository;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.more.data.NetworkBoundResource;
import com.example.more.data.Resource;
import com.example.more.data.local.dao.ContentDao;
import com.example.more.data.local.entity.ContentEntity;
import com.example.more.data.local.entity.NotificationEntity;
import com.example.more.data.local.model.WhatsAppStatus;
import com.example.more.data.remote.api.ContentApiService;
import com.example.more.data.remote.model.ContentEntityApiResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/*
 * One of the first things we do in the Repository class
 * is to make it a Singleton.
 * */

@Singleton
public class NotificationRepository {

    private ContentDao contentDao;

    public NotificationRepository(ContentDao contentDao) {
        this.contentDao = contentDao;
    }


    /*
     * We are using this method to fetch the notifications list
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
    public Observable<Resource<List<NotificationEntity>>> getNotificationList() {
        Observable<Resource<List<NotificationEntity>>> result;
        Observable<Resource<List<NotificationEntity>>> source = Observable.fromCallable(()->contentDao.getNotifications()).subscribeOn(Schedulers.io())
                .flatMap(apiResponse -> Observable.just(processResponse(apiResponse)).map(Resource::success))
                .onErrorResumeNext(t -> {
                    return handleError()
                            .toObservable()
                            .map(strings -> Resource.error(t.getMessage(), strings));
                })
                .observeOn(AndroidSchedulers.mainThread());

        result = Observable.concat(
                handleError()
                        .toObservable()
                        .map(Resource::loading).take(1)
                ,
                source
        );

        return result;
    }

    List<NotificationEntity> processResponse(List<NotificationEntity> response) {

        return response;
    }

    Flowable<List<NotificationEntity>> handleError() {
        return Flowable.just(new ArrayList<NotificationEntity>());
    }

}
