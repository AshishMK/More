package com.example.more.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.more.data.Resource;
import com.example.more.data.local.dao.ContentDao;
import com.example.more.data.local.entity.ContentEntity;
import com.example.more.data.local.entity.NotificationEntity;
import com.example.more.data.remote.api.ContentApiService;
import com.example.more.data.repository.ContentRepository;
import com.example.more.data.repository.NotificationRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * {@link com.example.more.ui.fragment.NotificationFragment}
 */
public class NotificationListViewModel extends ViewModel {

    private NotificationRepository contentRepository;

    /* We are using LiveData to update the UI with the data changes.
     */
    private MutableLiveData<Resource<List<NotificationEntity>>> contentLiveData = new MutableLiveData<>();


    /*
     * We are injecting the ContentDao class
     * and the ContentApiService class to the ViewModel.
     * */

    @Inject
    public NotificationListViewModel(ContentDao contentDao ) {
        /* You can see we are initialising the ContentRepository class here */
        contentRepository = new NotificationRepository(contentDao);
    }


    /*
     * Method called by UI to fetch movies list
     * */
    public void getNotificationList() {
        contentRepository.getNotificationList()
                    .subscribe(resource -> getContentLiveData().postValue(resource));
    }


    /*
     * LiveData observed by the UI
     * */
    public MutableLiveData<Resource<List<NotificationEntity>>> getContentLiveData() {
        return contentLiveData;
    }
}
