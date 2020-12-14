package com.example.more.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.more.data.Resource;
import com.example.more.data.local.dao.ContentDao;
import com.example.more.data.local.entity.ContentEntity;
import com.example.more.data.remote.api.ContentApiService;
import com.example.more.data.remote.model.VideoEntity;
import com.example.more.data.remote.model.VideoListEntity;
import com.example.more.data.repository.ContentRepository;

import java.util.ArrayList;
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
    private MutableLiveData<Resource<List<VideoEntity>>> videoLiveData = new MutableLiveData<>();


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
    public void loadContentList(int contentType, int offset, String tag, boolean filter_starred) {
        contentRepository.loadMoviesByType(contentType, offset, tag, filter_starred)
                .subscribe(resource -> getContentLiveData().postValue(resource));
    }


    /*
     * LiveData observed by the UI
     * */
    public MutableLiveData<Resource<List<ContentEntity>>> getContentLiveData() {
        return contentLiveData;
    }

    /*
     * LiveData for video list by the UI
     * */
    public MutableLiveData<Resource<List<VideoEntity>>> getVideoLiveData() {
        return videoLiveData;
    }

    /*
     * Method called by UI to fetch movies list
     * */
    public void getVideos(int mediaCategory, int offset) {
        contentRepository.getVideos(mediaCategory, offset)
                .subscribe (
            resource -> getVideoLiveData().postValue(resource));

    }
}
