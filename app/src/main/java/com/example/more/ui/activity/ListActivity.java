package com.example.more.ui.activity;

import android.app.NotificationManager;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.Visibility;
import android.view.Gravity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.more.BR;
import com.example.more.R;
import com.example.more.data.Status;
import com.example.more.databinding.ListActivityBinding;
import com.example.more.ui.fragment.ListFragment;
import com.example.more.utills.animation.AnimUtil;

import dagger.android.AndroidInjection;

/**
 * Activity host {@link ListFragment} to display list of content
 * Content is define by {@link ListFragment#content_type}
 *
 * @see also {@link DownloadManagerActivity}
 */

public class ListActivity extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener {
    /*
     * I am using DataBinding
     * */
    private ListActivityBinding binding;
    /**
     * Weather it is the unfiltered content list or filtered content list by tag
     *
     * @see SearchActivity
     */
    boolean isMainList = true;

    /**
     * Types of Content
     */
    final public static int FACT = 0;
    final public static int QUOTE = 1;
    final public static int STORY = 2;
    final public static int MEDIA = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Remember in our {@link com.example.more.di.module.ActivityModule}, we
         * defined {@link ListActivity} injection? So we need
         * to call this method in order to inject the
         * ViewModelFactory into our Activity
         * */
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        initialiseView();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cancel notification from status bar as we saw them here
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(getIntent().getIntExtra("content_type", 0));
    }

    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView() {
        //supportPostponeEnterTransition();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list);
        // Performing window enter Activity transition animation
        getWindow().setEnterTransition(buildEnterTransition());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((ListFragment) getSupportFragmentManager().findFragmentById(R.id.listFragment)).content_type = getIntent().getIntExtra("content_type", 0);
        binding.title.setText(getTitle(getIntent().getIntExtra("content_type", 0)));
        binding.setVariable(BR.status, Status.LOADING);
        binding.setHandler(((ListFragment) getSupportFragmentManager().findFragmentById(R.id.listFragment)));

    }

    /**
     * Returns the title for activity toolbar based on content type
     *
     * @param content_type
     * @return
     */
    public String getTitle(int content_type) {
        switch (content_type) {
            case FACT:
                return getResources().getQuantityString(R.plurals.fact, 2);
            case QUOTE:
                return getResources().getQuantityString(R.plurals.quote, 2);
            case STORY:
                return getResources().getQuantityString(R.plurals.meme, 2);
            default:
                return getResources().getQuantityString(R.plurals.media, 2);

        }
    }

    @Override
    public void onBackPressed() {
        if (!isMainList) {
            isMainList = true;
            binding.title.setText(getTitle(getIntent().getIntExtra("content_type", 0)));
            ((ListFragment) getSupportFragmentManager().findFragmentById(R.id.listFragment)).resetList();
            return;
        }
        Visibility returnTransition = buildReturnTransition();
        getWindow().setReturnTransition(returnTransition);

        finishAfterTransition();
    }

    /**
     * @return method to create and return transition for activity window exit
     */

    private Visibility buildReturnTransition() {
        Slide enterTransition = new Slide();
        enterTransition.setSlideEdge(Gravity.RIGHT);
        enterTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        return enterTransition;
    }

    @Override
    public void onFragmentInteraction(String title) {
        isMainList = false;
        binding.title.setText(title + " " + getTitle(getIntent().getIntExtra("content_type", 0)));
    }

    @Override
    public void onFragmentStatus(Status status) {

        binding.setVariable(BR.status, status);


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDataReceived() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                supportStartPostponedEnterTransition();
            }
        }, 200);

    }


    /**
     * @return method to create and return explode transition for activity window
     */
    private Transition buildEnterTransition() {
        Explode enterTransition = new Explode();
        enterTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        return enterTransition;
    }


}
