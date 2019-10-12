package com.example.more.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.more.data.Resource;
import com.example.more.data.local.dao.ContentDao;
import com.example.more.data.local.entity.ContentEntity;
import com.example.more.data.remote.api.ContentApiService;
import com.example.more.data.repository.ContentRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * @see com.example.more.ui.fragment.ListFragment
 */
public class ContentListViewModel extends ViewModel {

    private ContentRepository contentRepository;

    /* We are using LiveData to update the UI with the data changes.
     */
    private MutableLiveData<Resource<List<ContentEntity>>> contentLiveData = new MutableLiveData<>();


    /*
     * We are injecting the ContentDao class
     * and the ContentApiService class to the ViewModel.
     * */

    @Inject
    public ContentListViewModel(ContentDao contentDao, ContentApiService contentApiService) {
        /* You can see we are initialising the ContentRepository class here */
        contentRepository = new ContentRepository(contentDao, contentApiService);
    }


    /*
     * Method called by UI to fetch movies list
     * */
    public void loadContentList(int contentType, int offset,String tag) {
        contentRepository.loadMoviesByType(contentType,offset,tag)
                    .subscribe(resource -> getContentLiveData().postValue(resource));
    }


    /*
     * LiveData observed by the UI
     * */
    public MutableLiveData<Resource<List<ContentEntity>>> getContentLiveData() {
        return contentLiveData;
    }
}
