package com.example.more.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


import com.example.more.di.ViewModelKey;
import com.example.more.factory.ViewModelFactory;
import com.example.more.viewmodel.ContentListViewModel;
import com.example.more.viewmodel.DMActivityViewModel;
import com.example.more.viewmodel.ImageFirebaseViewModel;
import com.example.more.viewmodel.NotificationListViewModel;
import com.example.more.viewmodel.SearchListViewModel;
import com.example.more.viewmodel.WhatsappStatusViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/*
 *  Module to inject specified list of ViewModule
 */
@Module
public abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);


    /*
     * This method basically says
     * inject this object into a Map using the @IntoMap annotation,
     * with the  ContentListViewModel.class as key,
     * and a Provider that will build a ContentListViewModel
     * object.
     *
     * */

    @Binds
    @IntoMap
    @ViewModelKey(ContentListViewModel.class)
    protected abstract ViewModel movieListViewModel(ContentListViewModel contentListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DMActivityViewModel.class)
    protected abstract ViewModel dmActivityViewModel(DMActivityViewModel contentListViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(SearchListViewModel.class)
    protected abstract ViewModel searchListViewModel(SearchListViewModel contentListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(WhatsappStatusViewModel.class)
    protected abstract ViewModel statusViewModel(WhatsappStatusViewModel contentListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ImageFirebaseViewModel.class)
    protected abstract ViewModel imageListViewModel(ImageFirebaseViewModel contentListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(NotificationListViewModel.class)
    protected abstract ViewModel notificationListViewModel(NotificationListViewModel contentListViewModel);


}