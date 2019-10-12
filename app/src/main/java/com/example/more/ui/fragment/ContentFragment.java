package com.example.more.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.more.BR;
import com.example.more.R;
import com.example.more.databinding.ContentFragmentBinding;
import com.example.more.ui.activity.CodeScanActivity;
import com.example.more.ui.activity.FacebookActivity;
import com.example.more.ui.activity.ListActivity;
import com.example.more.ui.activity.OcrCaptureActivity;
import com.example.more.ui.activity.WhatsAppStatusActivity;
import com.example.more.ui.interfaces.ContentFragmentHandler;
import com.example.more.utills.AlertDialogProvider;
import com.example.more.utills.animation.TransitionHelper;

import static com.example.more.ui.activity.CodeScanActivity.TOOL_INTENT_TYPE_PICTURE;
import static com.example.more.ui.activity.CodeScanActivity.TOOL_INTENT_TYPE_SCAN;
import static com.example.more.utills.AlertDialogProvider.TYPE_EDIT;


/**
 * Fragment to display tools or content options based on the type provided.
 * parent activity {@link com.example.more.ui.activity.HomeActivity}
 */
public class ContentFragment extends Fragment implements ContentFragmentHandler {

    /**
     * boolean determines weather to show tools or contents
     */
    public static final String IS_TOOL_FRAGMENT = "isToolFragment";
    Boolean isToolFragment = false;
    //to get scan results from CodeScanActivity and OcrCaptureActivity
    public static int REQUEST_SCAN = 167;

    public ContentFragment() {
        // Required empty public constructor
    }


    public static ContentFragment getInstance(Boolean isToolFragment) {
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_TOOL_FRAGMENT, isToolFragment);
        fragment.setArguments(args);
        return fragment;
    }

    /*
     * I am using DataBinding
     * */
    private ContentFragmentBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isToolFragment = getArguments().getBoolean(IS_TOOL_FRAGMENT);
        }
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
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.content_fragment, viewGroup, false);
        binding.setVariable(BR.handler, this);
        binding.setVariable(BR.isToolFragment, isToolFragment);
    }

    @Override
    public void onClickView(View v) {
        Intent intent = new Intent(getActivity(), ListActivity.class);
        View view = binding.titleFact;
        if (v == binding.logoFact) {
            view = binding.titleFact;
            intent.putExtra("content_type", ListActivity.FACT);
        } else if (v == binding.logoQuote) {
            view = binding.titleQuote;
            intent.putExtra("content_type", ListActivity.QUOTE);
        } else if (v == binding.logoStory) {
            view = binding.titleStory;
            intent.putExtra("content_type", ListActivity.STORY);
        } else if (v == binding.logoMedia) {
            view = binding.titleMedia;
            intent.putExtra("content_type", ListActivity.MEDIA);
        }
        transitionToActivity(intent);
        //startActivity(intent);
    }

    @Override
    public void onClickViewTools(View v) {
        if (v == binding.logoFact) {
            startActivityForResult(CodeScanActivity.getIntent(getActivity(), TOOL_INTENT_TYPE_SCAN), REQUEST_SCAN);
        } else if (v == binding.logoQuote) {
            AlertDialogProvider alertDialogProvider = AlertDialogProvider.getInstance("Choose image source", getResources().getStringArray(R.array.scan_options), new int[]{R.drawable.ic_gallery, R.drawable.ic_camera}, AlertDialogProvider.TYPE_LIST);
            alertDialogProvider.show(getChildFragmentManager(), ContentFragment.class.getName());

            alertDialogProvider.setAlertDialogItemListener(position -> {
                if (position == 0) {
                    startActivityForResult(CodeScanActivity.getIntent(getActivity(), TOOL_INTENT_TYPE_PICTURE), REQUEST_SCAN);
                } else {
                    startActivityForResult(OcrCaptureActivity.getInstance(getActivity()), REQUEST_SCAN);
                }
            });

        } else if (v == binding.logoStory) {
            transitionToActivity(new Intent(getActivity(), WhatsAppStatusActivity.class));
        } else if (v == binding.logoMedia) {
            transitionToActivity(new Intent(getActivity(), FacebookActivity.class));
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_SCAN) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialogProvider alertDialogProvider = AlertDialogProvider.getInstance(getString(R.string.copy_msg), data.getStringExtra("message"), TYPE_EDIT);
                        alertDialogProvider.show(getChildFragmentManager(), CodeScanActivity.class.getName());

                    }
                });

            }
        }
    }

    /**
     * Apply transition on given intent
     * @param intent
     */
    public void transitionToActivity(Intent intent) {

        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(getActivity(), true);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pairs);
        startActivity(intent, transitionActivityOptions.toBundle());
    }
}