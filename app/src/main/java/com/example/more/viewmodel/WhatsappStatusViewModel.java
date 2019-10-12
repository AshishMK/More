package com.example.more.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.content.Context;

import com.example.more.data.Resource;
import com.example.more.data.asyncTasks.AsyncTaskWorkerWhatsAppStatus;
import com.example.more.data.local.dao.ContentDao;
import com.example.more.data.remote.api.ContentApiService;
import com.example.more.data.local.model.WhatsAppStatus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * {@link com.example.more.ui.fragment.WhatsappStatusFragment}
 */
public class WhatsappStatusViewModel extends ViewModel {

    private AsyncTaskWorkerWhatsAppStatus contentRepository;

    /* We are using LiveData to update the UI with the data changes.
     */
    private MutableLiveData<Resource<List<WhatsAppStatus>>> contentLiveData = new MutableLiveData<>();
    private MutableLiveData<Resource<WhatsAppStatus>> copyPath = new MutableLiveData<>();


    /*
     * We are injecting the ContentDao class
     * and the ContentApiService class to the ViewModel.
     * */

    @Inject
    public WhatsappStatusViewModel(ContentDao contentDao, ContentApiService contentApiServic) {
        /* You can see we are initialising the ContentRepository class here */
        contentRepository = new AsyncTaskWorkerWhatsAppStatus(contentDao,contentApiServic);
    }


    /*
     * Method called by UI to fetch movies list
     * */
    public void loadContentList() {
        contentRepository.getFilePaths()
                .subscribe(resurce -> getContentLiveData().postValue(resurce));
    }

    public void copyFileByPath(ArrayList<WhatsAppStatus> paths, Context ctx) {
        contentRepository.copyFile(paths,ctx)
                .subscribe(resurce -> getPathLiveData().postValue(resurce));
    }


    /*
     * LiveData observed by the UI
     * */
    public MutableLiveData<Resource<List<WhatsAppStatus>>> getContentLiveData() {
        return contentLiveData;
    }
    public MutableLiveData<Resource<WhatsAppStatus>> getPathLiveData() {
        return copyPath;
    }
}
