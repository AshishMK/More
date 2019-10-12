package com.example.more.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Status;
import com.tonyodev.fetch2rx.RxFetch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

/**
 * @see com.example.more.ui.activity.DownloadManagerActivity
 */
public class DMActivityViewModel extends ViewModel {

    private RxFetch rxFetch;

    /* We are using LiveData to update the UI with the data changes.
     */
    private MutableLiveData<List<Download>> contentLiveData = new MutableLiveData<>();


    @Inject
    public DMActivityViewModel(RxFetch rxFetch) {
        this.rxFetch = rxFetch;
    }


    /*
     * Method called by UI to fetch movies list
     * */
    public void loadDownloadList() {
        rxFetch.getDownloads()
                .asFlowable()
                .subscribe(new Consumer<List<Download>>() {
                    @Override
                    public void accept(List<Download> downloads) throws Exception {
                        //Access results

                        Collections.sort(downloads, new Comparator<Download>() {
                            @Override
                            public int compare(Download o1, Download o2) {
                                return o1.getStatus() == Status.DOWNLOADING || o1.getStatus() == Status.QUEUED || o1.getStatus() == Status.ADDED ? -1 : 0;
                            }
                        });
                        getContentLiveData().postValue(downloads);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //An error occurred
                        getContentLiveData().postValue(new ArrayList<Download>());
                    }
                });
    }


    /*
     * LiveData observed by the UI
     * */
    public MutableLiveData<List<Download>> getContentLiveData() {
        return contentLiveData;
    }
}
