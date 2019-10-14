package com.example.more.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.more.BR;
import com.example.more.R;
import com.example.more.data.Status;
import com.example.more.data.local.entity.ContentEntity;
import com.example.more.databinding.ListFragmentBinding;
import com.example.more.factory.ViewModelFactory;
import com.example.more.ui.activity.ListActivity;
import com.example.more.ui.activity.SearchActivity;
import com.example.more.ui.adapter.ContentListAdapter;
import com.example.more.ui.interfaces.ListActivityHandler;
import com.example.more.utills.animation.AnimUtil;
import com.example.more.utills.animation.TransitionHelper;
import com.example.more.viewmodel.ContentListViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.DaggerFragment;

import static com.example.more.ui.activity.ListActivity.FACT;


/**
 * Fragment use to display list of content based on content_type
 * <p>
 * Note: We use Linear list for evey content type except meme contents.
 * for meme we use gris list
 *
 * @see com.example.more.ui.activity.ListActivity#FACT/
 * Adapter @link {@link ContentListAdapter}
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ListFragment extends DaggerFragment implements ListActivityHandler {
    /*
     * we need to
     * inject the ViewModelFactory. The ViewModelFactory class
     * has a list of ViewModels and will provide
     * the corresponding ViewModel in this activity
     * */
    @Inject
    ViewModelFactory viewModelFactory;

    LinearLayoutManager mLayoutManager;

    /**
     * Grid list for meme contents
     */
    GridLayoutManager mGridLayoutManager;
    public String tag = null;
    /*
     * This is our ViewModel class
     * */
    private ContentListViewModel contentListViewModel;
    /*
     * I am using DataBinding
     * */
    private ListFragmentBinding binding;
    /**
     * Pagination properties
     * Use to indicate loadmore/pagination is in process or not
     * true = loadMore is not in process, can load more items
     * false = loadMore is in process can not load more items until process get finished
     * Note: when there are no more items to load that is last received items count from load more operation is < 10
     * then this variable remain false even on successful response
     */
    private boolean loadMore = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    /**
     * Type of content for which this fragment show the data
     * example {@link com.example.more.ui.activity.ListActivity#FACT}
     */
    public int content_type = FACT;
    ContentListAdapter contentListAdapter;


    private OnFragmentInteractionListener mListener;

    public ListFragment() {
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
        /*
         * Step 2: Remember in our ActivityModule, we
         * defined MainActivity injection? So we need
         * to call this method in order to inject the
         * ViewModelFactory into our Activity
         * */

        initialiseView(container);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setuprecyclerView();
        initialiseViewModel();
    }


    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView(ViewGroup viewGroup) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_list, viewGroup, false);


    }


    /****/
    void setuprecyclerView() {
        contentListAdapter = new ContentListAdapter(getActivity(), content_type);
        binding.list.setLayoutManager(content_type == ListActivity.STORY ? (mGridLayoutManager = new GridLayoutManager(getActivity(), 2)) : (mLayoutManager = new LinearLayoutManager(getActivity())));
        if (mGridLayoutManager != null) {
            mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return position == contentListAdapter.getItemCount() - 1 ? 2:1;
                }
            });
        }
        binding.list.setAdapter(contentListAdapter);
        binding.setVariable(BR.status, Status.LOADING);

        binding.list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager == null ? mGridLayoutManager.getChildCount() : mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager == null ? mGridLayoutManager.getItemCount() : mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager == null ? mGridLayoutManager.findFirstVisibleItemPosition() : mLayoutManager.findFirstVisibleItemPosition();

                    if (loadMore) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 3) {
                            loadMore = false;
                            //Do pagination.. i.e. fetch new data
                            contentListViewModel.loadContentList(content_type, contentListAdapter.getItemCount() - 1, tag);

                        }
                    }
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            transitionToActivity(new Intent(getActivity(), SearchActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Apply transition on given intent
     *
     * @param intent
     */
    public void transitionToActivity(Intent intent) {
        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(getActivity(), true);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pairs);
        startActivityForResult(intent, SearchActivity.REQUEST_CODE, transitionActivityOptions.toBundle());
    }

    /*
     * Initialising the ViewModel class here.
     * We are adding the ViewModelFactory class here.
     * We are observing the LiveData
     * */
    private void initialiseViewModel() {
        contentListViewModel = ViewModelProviders.of(this, viewModelFactory).get(ContentListViewModel.class);
        contentListViewModel.getContentLiveData().observe(this, resource -> {
            if (resource.isLoading()) {
                //displayLoader();
                mListener.onFragmentStatus(Status.LOADING);
                if (!loadMore)
                    contentListAdapter.setStatus(Status.LOADING);
                else {
                    AnimUtil.rotateView(binding.loadingLayout.image);
                    binding.setVariable(BR.status, Status.LOADING);
                }
            } else if (resource.data != null && resource.data.size() > 0) {

                binding.setVariable(BR.status, Status.SUCCESS);
                mListener.onFragmentStatus(resource.status);
                updateContentList(resource.data, !loadMore, resource.status);
                // request was successful but received items count is < 10 i.e. no more item to load
                // so we disallow to send any more load more requests
                if (resource.status == Status.SUCCESS && resource.data.size() < 10) {
                    if (!loadMore) {
                        contentListAdapter.setStatus(Status.NOT_FOUND);
                    }
                    loadMore = false;

                } else if (resource.status == Status.SUCCESS) {
                    //loadMore = true;
                }
            } else if (resource.status == Status.ERROR) {
                mListener.onFragmentStatus(resource.status);
                if (!loadMore)
                    contentListAdapter.setStatus(Status.ERROR);
                else
                    binding.setVariable(BR.status, Status.ERROR);
            } else if (resource.status == Status.SUCCESS && (resource.data == null || resource.data.size() == 0)) {
                if (!loadMore)
                    contentListAdapter.setStatus(Status.NOT_FOUND);
                else
                    binding.setVariable(BR.status, Status.NOT_FOUND);
            }
            if (!resource.isLoading()) {
                //mListener.onDataReceived();
            }
        });
        /* Fetch content list  */
        contentListViewModel.loadContentList(content_type, -1, tag);
    }

    /**
     * Method to update list of contents
     *
     * @param contents
     */
    private void updateContentList(List<ContentEntity> contents, boolean isFromLoadMore, Status status) {
        contentListAdapter.setItems(contents, isFromLoadMore, status);
    }

    @Override
    public void onAttach(Context context) {
        /**
         * Remember in our {@link com.example.more.di.module.FragmentModule}, we
         * defined {@link ListFragment} injection? So we need
         * to call this method in order to inject the
         * ViewModelFactory into our Fragment
         * */
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        mListener = (OnFragmentInteractionListener) getActivity();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String title);

        public void onFragmentStatus(Status status);

        public void onDataReceived();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SearchActivity.REQUEST_CODE) {
                loadMore = true;
                contentListViewModel.loadContentList(content_type, -1, tag = data.getStringExtra("tag"));
                mListener.onFragmentInteraction(data.getStringExtra("tag"));
            }
        }

    }

    public void resetList() {
        tag = null;
        loadMore = true;
        refreshList(null);
    }

    @Override
    public void refreshList(View v) {
        /* Fetch content list  */
        contentListViewModel.loadContentList(content_type, loadMore ? -1 : contentListAdapter.getItemCount() - 1, tag);

    }
}

