package com.example.more.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.more.data.Resource;
import com.example.more.data.local.dao.ContentDao;
import com.example.more.data.local.entity.SearchEntity;
import com.example.more.data.remote.api.ContentApiService;
import com.example.more.data.repository.SearchRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * {@link com.example.more.ui.activity.SearchActivity}
 */
public class SearchListViewModel extends ViewModel {

    Disposable disposable = null;

    private SearchRepository contentRepository;

    /* We are using LiveData to update the UI with the data changes.
     */
    private MutableLiveData<Resource<List<SearchEntity>>> contentLiveData = new MutableLiveData<>();


    /*
     * We are injecting the ContentDao class
     * and the ContentApiService class to the ViewModel.
     * */

    @Inject
    public SearchListViewModel(ContentDao contentDao, ContentApiService contentApiService) {
        /* You can see we are initialising the ContentRepository class here */
        contentRepository = new SearchRepository(contentDao, contentApiService);
    }


    /*
     * Method called by UI to fetch movies list
     * */
    public void searchTagList(String tag) {
        disposeDisposable();
        disposable = contentRepository.searchTags(tag)
                .subscribe(getContentLiveData()::postValue);
    }


    /*
     * LiveData observed by the UI
     * */
    public MutableLiveData<Resource<List<SearchEntity>>> getContentLiveData() {
        return contentLiveData;
    }

    public void disposeDisposable() {
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
