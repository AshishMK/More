package com.example.more.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.more.BR;
import com.example.more.R;
import com.example.more.data.local.pref.PreferencesStorage;
import com.example.more.databinding.SettingFragmentBinding;
import com.example.more.ui.interfaces.SettingsFragmentHandler;
import com.example.more.utills.Utils;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.DaggerFragment;


/**
 * Fragment to provide different settings options for Application
 * parent activity {@link com.example.more.ui.activity.SettingActivity}
 */
public class SettingFragment extends DaggerFragment implements SettingsFragmentHandler {
    /**
     * Provide {@link android.content.SharedPreferences} operations
     */
    @Inject
    PreferencesStorage preferenceStorage;
    /*
     * I am using DataBinding
     * */
    private SettingFragmentBinding binding;


    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
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
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_setting, viewGroup, false);
        binding.setVariable(BR.preferenceStorage, preferenceStorage);
        binding.setVariable(BR.settingsHandler, this);

    }


    @Override
    public void onAttach(Context context) {
        /**
         * Remember in our {@link com.example.more.di.module.FragmentModule}, we
         * defined {@link SettingFragment} injection? So we need
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
    public void onHelpClickView(View v) {
        Utils.askHelp();

    }

    @Override
    public void shareApp(View v) {
        Utils.shareApplication();
    }

    @Override
    public void onCreditClickView(View v) {
        Utils.gotoAppCredits();
    }
}

