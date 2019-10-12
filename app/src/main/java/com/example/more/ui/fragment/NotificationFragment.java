package com.example.more.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.more.BR;
import com.example.more.R;
import com.example.more.data.Status;
import com.example.more.data.local.entity.NotificationEntity;
import com.example.more.databinding.NotificationFragmentBinding;
import com.example.more.factory.ViewModelFactory;
import com.example.more.ui.adapter.NotificationListAdapter;
import com.example.more.utills.animation.AnimUtil;
import com.example.more.viewmodel.NotificationListViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;


/**
 * Fragment yse to list of all notifications
 * Model {@link NotificationEntity}
 * Parent Activity {@link com.example.more.ui.activity.NotificationActivity}
 * */
public class NotificationFragment extends Fragment  {

    public NotificationFragment() {
        // Required empty public constructor
    }


    public static NotificationFragment getInstance() {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }


    /*
     * This is our ViewModel class
     * */
    private NotificationListViewModel notificationListViewModel;

    NotificationListAdapter contentListAdapter;
    /*
     * I am using DataBinding
     * */
    private NotificationFragmentBinding binding;
    /*
     * we need to
     * inject the ViewModelFactory. The ViewModelFactory class
     * has a list of ViewModels and will provide
     * the corresponding ViewModel in this activity
     * */
    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initialiseView(container);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }


    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView(ViewGroup viewGroup) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.notification_fragment, viewGroup, false);
        binding.setVariable(BR.handler, this);
        contentListAdapter = new NotificationListAdapter(getActivity());
        binding.list.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.list.setAdapter(contentListAdapter);
        binding.setVariable(BR.status, Status.LOADING);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialiseViewModel();
    }
    /*
     * Initialising the ViewModel class here.
     * We are adding the ViewModelFactory class here.
     * We are observing the LiveData
     * */
    private void initialiseViewModel() {
        notificationListViewModel = ViewModelProviders.of(this, viewModelFactory).get(NotificationListViewModel.class);
        notificationListViewModel.getContentLiveData().observe(this, resource -> {
            if (resource.isLoading()) {
                //displayLoader();
                AnimUtil.rotateView(binding.loadingLayout.image);
                binding.setVariable(BR.status, Status.LOADING);
            } else if (resource.data != null && resource.data.size() > 0) {
                binding.setVariable(BR.status, Status.SUCCESS);
                updateNotificationList(resource.data);
                if (resource.status == Status.ERROR) {
                    Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                }
            } else if ((resource.data == null || resource.data.size() == 0)) {
                binding.setVariable(BR.status, Status.NOT_FOUND);
            }
        });

        /* Fetch movies list  */
        notificationListViewModel.getNotificationList();
    }

    /**
     * Method to update list of Notification
     * @param notifications
     */
    private void updateNotificationList(List<NotificationEntity> notifications) {
        binding.list.setVisibility(View.VISIBLE);
        contentListAdapter.setItems(notifications);
    }

    @Override
    public void onAttach(Context context) {
        /**
         * Remember in our {@link com.example.more.di.module.FragmentModule}, we
         * defined {@link NotificationFragment} injection? So we need
         * to call this method in order to inject the
         * ViewModelFactory into our Fragment
         * */
        AndroidSupportInjection.inject(this);
        super.onAttach(context);

    }

}