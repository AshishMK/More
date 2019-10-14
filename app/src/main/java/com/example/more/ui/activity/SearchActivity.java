package com.example.more.ui.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.transition.Visibility;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.more.BR;
import com.example.more.R;
import com.example.more.data.Status;
import com.example.more.data.local.entity.SearchEntity;
import com.example.more.databinding.SearchActivityBinding;
import com.example.more.factory.ViewModelFactory;
import com.example.more.ui.adapter.SearchListAdapter;
import com.example.more.ui.fragment.WhatsappStatusFragment;
import com.example.more.ui.interfaces.SearchActivityHandler;
import com.example.more.utills.Screen;
import com.example.more.utills.animation.AnimUtil;
import com.example.more.viewmodel.SearchListViewModel;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.AndroidInjection;


/**
 * Activity to perform search operation for getting #tags for contents so you can filter Content list
 * {@link com.example.more.ui.fragment.ListFragment}
 */
public class SearchActivity extends AppCompatActivity implements SearchListAdapter.OnSearchItemClick, SearchActivityHandler {
    public static final int REQUEST_CODE = 233;
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
    private SearchListViewModel contentListViewModel;
    /*
     * I am using DataBinding
     * */
    private SearchActivityBinding binding;


    //Previous height and target height for cardview to perform expendable animation
    int headHeight = 0, lastSize = 0;


    //SearchListAdapter to inflate search views for current search Items for list RecyclerView
    SearchListAdapter mAdapter;

    //Default list of data on which search filtration will be performed
    //   ArrayList<String> list_data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Remember in our {@link com.example.more.di.module.ActivityModule}, we
         * defined {@link SearchActivity} injection? So we need
         * to call this method in order to inject the
         * ViewModelFactory into our {@link Activity}
         * */
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        initialiseView();
        //SetUp search EditText listener for submitting the search query
        setEditListener();
        initialiseViewModel();
    }

    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        //init CardView and apply startup expandable animation on it
        binding.card.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                binding.card.removeOnLayoutChangeListener(this);
                headHeight = binding.head.getHeight();
                binding.card.getLayoutParams().height = headHeight;
                AnimUtil.animateRevealShow(binding.card);
            }
        });
        binding.setVariable(BR.handler, this);
        setRecycler();

    }


    /**
     * Method use to setup and init RecyclerView with adapter
     */
    void setRecycler() {
        mAdapter = new SearchListAdapter(this);
        binding.searchList.setLayoutManager(new LinearLayoutManager(this));
        binding.searchList.setAdapter(mAdapter);
    }

    /**
     * When user chooses or select a search tag from list
     *
     * @param s
     */
    public void onSearchPerformed(String s) {
        if (TextUtils.isEmpty(s)) {

        } else {
            setResult(Activity.RESULT_OK, new Intent().putExtra("tag", s.replaceAll("#", "").toLowerCase()));
            onBackPressed();
        }
    }

    /**
     * Method use to setup TextChangedListener on search EditText edit_search for submitting search query
     */
    void setEditListener() {
        binding.editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearchPerformed(binding.editSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });
        binding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
                //perform search operation
                // mAdapter.updateSearchData(filter(list_data, s.toString()));
                binding.setVariable(BR.isTyping, s.length() > 0);
                if (TextUtils.isEmpty(s)) {
                    contentListViewModel.disposeDisposable();
                    mAdapter.updateSearchData(new ArrayList<>());
                    expandAnimation(0);
                } else {
                    contentListViewModel.searchTagList(s.toString().replaceAll("#", "").toLowerCase());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    /**
     * Method to init and perform expand animation on CardView
     */
    void expandAnimation(int size) {
        if (lastSize == size) return;
        int rootHeight = findViewById(R.id.activity_search).getHeight() - Screen.dp(32);

        int targetHeight = (size * Screen.dp(48)) + headHeight;
        targetHeight = targetHeight > rootHeight ? rootHeight : targetHeight;
        int startHeight = (lastSize * Screen.dp(48)) + headHeight;
        startHeight = startHeight > rootHeight ? rootHeight : startHeight;
        boolean useLast = lastSize > size;
        lastSize = size;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startHeight, targetHeight);
        int finalTargetHeight = targetHeight;
        int finalStartHeight = startHeight;
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {

                binding.card.getLayoutParams().height = (int) animation.getAnimatedValue();
                binding.card.requestLayout();
                if ((int) animation.getAnimatedValue() == (useLast ? finalStartHeight : finalTargetHeight)) {
                    binding.card.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                }
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(700);
        //valueAnimator.setStartDelay(100);
        valueAnimator.start();

    }

    public void onClick(@NonNull View v) {
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();

        }

    }

    /*
     * Initialising the ViewModel class here.
     * We are adding the ViewModelFactory class here.
     * We are observing the LiveData
     * */
    private void initialiseViewModel() {
        contentListViewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchListViewModel.class);
        contentListViewModel.getContentLiveData().observe(this, resource -> {
            binding.setVariable(BR.status, resource.status);
            if (resource.isLoading()) {
                //displayLoader();

            } else if (resource.data != null && resource.data.size() > 0) {
                mAdapter.updateSearchData((ArrayList<SearchEntity>) resource.data);

                expandAnimation(resource.data.size());
                if (resource.status == Status.ERROR) {
                    Toast.makeText(SearchActivity.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }

            } else if (resource.status == Status.ERROR) {
                handleEmptyList(getString(R.string.no_connection));
            } else if (resource.status == Status.SUCCESS && (resource.data == null || resource.data.size() == 0)) {
                handleEmptyList(getString(R.string.no_more_item));
            }
        });
    }

    /**
     * When search operation returns empty result
     *
     * @param message
     */
    public void handleEmptyList(String message) {
        contentListViewModel.disposeDisposable();
        ArrayList<SearchEntity> tmp = new ArrayList<>();
        SearchEntity entity = new SearchEntity();
        entity.setId(-1L);
        entity.setTag(message);
        tmp.add(entity);
        mAdapter.updateSearchData(tmp);
        expandAnimation(tmp.size());
    }

    @Override
    public void onItemSearchClicked(String s) {
        onSearchPerformed(s);
    }

    @Override
    public void onItemEditClicked(String s) {
        binding.editSearch.setText(s);
        binding.editSearch.setSelection(s.length());
    }

    @Override
    public void onBackPressed() {
        Visibility returnTransition = buildReturnTransition();
        getWindow().setReturnTransition(returnTransition);

        finishAfterTransition();
    }

    /**
     * @return method to create and return transition for activity window exit
     */

    private Visibility buildReturnTransition() {
        Visibility enterTransition = new Slide();
        //enterTransition.setSlideEdge(Gravity.RIGHT);
        enterTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        return enterTransition;
    }

    @Override
    public void onBackClicked(View view) {
        onBackPressed();
    }

    @Override
    public void OnSearchClicked(View v) {
        if (TextUtils.isEmpty(binding.editSearch.getText().toString()))
            performVoiceRecognition();
        else
            onSearchPerformed(binding.editSearch.getText().toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing())
            contentListViewModel.disposeDisposable();
    }

    /**
     * Method to initiate Google Voice search
     */
    void performVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
        try {
            startActivityForResult(intent, 199);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry your device not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 199: {
                // Fetching Voice search results
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    binding.editSearch.setText(result.get(0).toString());
                    binding.editSearch.setSelection(result.get(0).toString().length());
                }
                break;
            }
        }
    }
}
