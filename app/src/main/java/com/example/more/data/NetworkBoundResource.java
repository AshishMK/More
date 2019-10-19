package com.example.more.data;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.example.more.data.local.dao.ContentDao;
import com.example.more.data.local.entity.ContentEntity;
import com.example.more.data.remote.model.ContentEntityApiResponse;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public abstract class NetworkBoundResource<ResultType, RequestType> {

    private Observable<Resource<ResultType>> result;

    @MainThread
    protected NetworkBoundResource() {
        Observable<Resource<ResultType>> source;
        if (shouldFetch()) {
            source = createCall()
                    .subscribeOn(Schedulers.io())
                    .doOnNext(apiResponse-> NetworkBoundResource.this.saveCallResult(NetworkBoundResource.this.processResponse(apiResponse)))
                    .flatMap(apiResponse -> NetworkBoundResource.this.loadFromDb().toObservable().map(Resource::success))

                    .doOnError(t -> onFetchFailed())
                    .onErrorResumeNext(t -> {
                        return loadFromDb()
                                .toObservable()
                                .map(data -> Resource.error(t.getMessage(), data));

                    })
                    .observeOn(AndroidSchedulers.mainThread());
        } else {
            source = loadFromDb()
                    .toObservable()
                    .map(Resource::success);
        }

        result = Observable.concat(
                loadFromDb()
                        .toObservable()
                        .map(Resource::loading)
                        .take(1),
                source
        );
    }

    public Observable<Resource<ResultType>> getAsObservable() {return result;}

    protected void onFetchFailed() {}

    @WorkerThread
    protected RequestType processResponse(Resource<RequestType> response) {

        /**
         * when we call content API the new data of ContentEntity also
         * override old {@link ContentEntity} field isStarred so
         * basically we set the original/old value of a {@link ContentEntity}
         * to new loaded {@link ContentEntity} here.
         * */
        if(response.data!=null && response.data instanceof ContentEntityApiResponse  ) {
            for (ContentEntity entity : ((ContentEntityApiResponse)response.data).getResults()) {
            ContentEntity contentEntity = getDAO().getContentById(entity.getId());
            if(contentEntity!=null){
                entity.setStarred(contentEntity.isStarred());
            }
            }
        }
        return response.data;}

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    @WorkerThread
    protected abstract ContentDao getDAO();

    @MainThread
    protected abstract boolean shouldFetch();

    @NonNull
    @MainThread
    protected abstract Flowable<ResultType> loadFromDb();

    @NonNull
    @MainThread
    protected abstract Observable<Resource<RequestType>> createCall();
}