package com.example.more.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.more.Application.AppController;
import com.example.more.BR;
import com.example.more.R;
import com.example.more.data.Status;
import com.example.more.data.local.model.WhatsAppStatus;
import com.example.more.databinding.WhatsappFragmentBinding;
import com.example.more.factory.ViewModelFactory;
import com.example.more.ui.adapter.StaggeredListAdapter;
import com.example.more.utills.AlertDialogProvider;
import com.example.more.utills.Utils;
import com.example.more.utills.animation.AnimUtil;
import com.example.more.viewmodel.WhatsappStatusViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.DaggerFragment;
import timber.log.Timber;


/**
 * Fragment to show list of whatsapp status and provide functionality to download them
 * Parent activity {@link com.example.more.ui.activity.WhatsAppStatusActivity}
 * Adapter {@link StaggeredListAdapter}
 */
public class WhatsappStatusFragment extends DaggerFragment implements StaggeredListAdapter.onItemClick {
    final static int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 101;
    /*
     * we need to
     * inject the ViewModelFactory. The ViewModelFactory class
     * has a list of ViewModels and will provide
     * the corresponding ViewModel in this activity
     * */
    @Inject
    ViewModelFactory viewModelFactory;

    /*
     * This is our ViewModel class
     * */
    private WhatsappStatusViewModel whatsappStatusViewModel;
    /*
     * I am using DataBinding
     * */
    private WhatsappFragmentBinding binding;
    StaggeredListAdapter contentListAdapter;


    public WhatsappStatusFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        initialiseView(container);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialiseViewModel();
    }


    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView(ViewGroup viewGroup) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_whatsapp, viewGroup, false);

        contentListAdapter = new StaggeredListAdapter(getActivity(), this);
        binding.list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.list.setHasFixedSize(true);
        binding.list.setAdapter(contentListAdapter);
        binding.setVariable(BR.status, Status.LOADING);
    }

    /**
     * Method to update list of WhatsAppStatus
     *
     * @param statusList
     */
    private void updateWhatsappStatusList(List<WhatsAppStatus> statusList) {
        binding.list.setVisibility(View.VISIBLE);
        contentListAdapter.setItems(statusList);
    }

    @Override
    public void onAttach(Context context) {
        /**
         * Remember in our {@link com.example.more.di.module.FragmentModule}, we
         * defined {@link WhatsappStatusFragment} injection? So we need
         * to call this method in order to inject the
         * ViewModelFactory into our Fragment
         * */
        AndroidSupportInjection.inject(this);
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDownloadClicked(WhatsAppStatus path) {
        if (path.isDownloaded) {
            AlertDialogProvider.getInstance(getString(R.string.already_downloaded), String.format(getString(R.string.location), getString(R.string.app_name) + "/" + new File(path.path).getName()), AlertDialogProvider.TYPE_NORMAL)
                    .show(getChildFragmentManager(), WhatsappStatusFragment.class.getName());
            return;
        }
        ArrayList<WhatsAppStatus> temp = new ArrayList<>();
        temp.add(path);
        whatsappStatusViewModel.copyFileByPath(temp, AppController.getInstance());
    }

    @Override
    public void onItemShared(String path) {
        Utils.shareFile(path);

    }


    /*
     * Initialising the ViewModel class here.
     * We are adding the ViewModelFactory class here.
     * We are observing the LiveData
     * */
    private void initialiseViewModel() {
        whatsappStatusViewModel = ViewModelProviders.of(this, viewModelFactory).get(WhatsappStatusViewModel.class);
        whatsappStatusViewModel.getContentLiveData().observe(this, resource -> {
            if (resource.isLoading()) {
                //displayLoader();
                AnimUtil.rotateView(binding.loadingLayout.image);
                binding.setVariable(BR.status, Status.LOADING);

            } else if (resource.data != null && resource.data.size() > 0) {
                binding.setVariable(BR.status, Status.SUCCESS);
                updateWhatsappStatusList(resource.data);
                if (resource.status == Status.ERROR) {
                    Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            } else if (resource.status == Status.ERROR) {
                binding.setVariable(BR.status, Status.ERROR);
            } else if (resource.status == Status.SUCCESS && (resource.data == null || resource.data.size() == 0)) {
                binding.setVariable(BR.status, Status.NOT_FOUND);
            }
            //else handleErrorResponse();
        });

        whatsappStatusViewModel.getPathLiveData().observe(this, resource -> {
            Timber.v(resource.data.status.name() + " " + resource.isSuccess() + " " + resource.isLoading());
            Toast.makeText(getActivity(), "" + resource.isSuccess(), Toast.LENGTH_SHORT).show();
            if (resource.data != null) {
                contentListAdapter.setItem(resource.data);
            } //else handleErrorResponse();
        });


        Utils.PermissionStatus status = Utils.checkPermissionsCamera(getActivity(), PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, this);
        if (status == Utils.PermissionStatus.SUCCESS) whatsappStatusViewModel.loadContentList();
        else if (status == Utils.PermissionStatus.ERROR) getActivity().finish();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                whatsappStatusViewModel.loadContentList();
            } else getActivity().finish();
        }
    }
}
