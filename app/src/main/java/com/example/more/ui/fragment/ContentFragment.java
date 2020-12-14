package com.example.more.ui.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.example.more.ui.activity.YoutubeActivity;
import com.example.more.ui.interfaces.ContentFragmentHandler;
import com.example.more.utills.AlertDialogProvider;
import com.example.more.utills.animation.TransitionHelper;

import java.util.ArrayList;
import java.util.Arrays;

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
        binding.logoMedia.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                    showShortcutDialog(!isToolFragment);
                return true;
            }
        });
    }

    @Override
    public void onClickView(View v) {
        Intent intent = new Intent(getActivity(), ListActivity.class);
        View view = binding.titleFact;
        if (v == binding.logoFact) {
            view = binding.titleFact;
            intent.putExtra("content_type", ListActivity.ANIMAL);
        } else if (v == binding.logoQuote) {
            view = binding.titleQuote;
            intent.putExtra("content_type", ListActivity.HUMAN);
        } else if (v == binding.logoStory) {
            view = binding.titleStory;
            intent.putExtra("content_type", ListActivity.PICTURE);
        } else if (v == binding.logoMedia) {
            transitionToActivity(new Intent(getActivity(), YoutubeActivity.class));
            return;
        }
        transitionToActivity(intent);
        //startActivity(intent);
    }


    @Override
    public void onClickViewTools(View v) {
        if (v == binding.logoFact) {
            startActivityForResult(CodeScanActivity.getIntent(getActivity(), TOOL_INTENT_TYPE_SCAN), REQUEST_SCAN);
        } else if (v == binding.logoQuote) {
            AlertDialogProvider alertDialogProvider = AlertDialogProvider.getInstance("Choose image source", new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.scan_options))), new ArrayList<Integer>(Arrays.asList(new Integer[]{R.drawable.ic_gallery, R.drawable.ic_camera})), AlertDialogProvider.TYPE_LIST, false);
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
                        AlertDialogProvider alertDialogProvider = AlertDialogProvider.getInstance(getString(R.string.copy_msg), data.getStringExtra("message"), TYPE_EDIT, false);
                        alertDialogProvider.show(getChildFragmentManager(), CodeScanActivity.class.getName());

                    }
                });

            }
        }
    }

    /**
     * Apply transition on given intent
     *
     * @param intent
     */
    public void transitionToActivity(Intent intent) {

        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(getActivity(), true);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pairs);
        startActivity(intent, transitionActivityOptions.toBundle());
    }

    void showShortcutDialog(boolean isYoutube) {
        AlertDialogProvider.getInstance("Create " + (isYoutube ? "YouTube" : "Facebook") + " Shortcut", "Do you want to create shortcut to home screen?", AlertDialogProvider.TYPE_NORMAL, false).setAlertDialogListener(new AlertDialogProvider.AlertDialogListener() {
            @Override
            public void onDialogCancel() {

            }

            @Override
            public void onDialogOk(String text, AlertDialogProvider dialog) {
                dialog.dismiss();
                createShortcut(isYoutube);
            }
        }).show(getChildFragmentManager(), ContentFragment.class.getName());


    }

    private void createShortcut(boolean isYoutube) {
        ShortcutManager shortcutManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            shortcutManager = getActivity().getSystemService(ShortcutManager.class);
            if (shortcutManager != null) {
                if (shortcutManager.isRequestPinShortcutSupported()) {
                    ShortcutInfo shortcut = new ShortcutInfo.Builder(getActivity(), isYoutube ? "ytShort" : "fbShort")
                            .setShortLabel(isYoutube ? "Youtube" : "Facebook")
                            .setLongLabel(isYoutube ? "Open Youtube Download" : "Open Facebook Download")
                            .setIcon(Icon.createWithResource(getActivity(), isYoutube ? R.drawable.movie : R.drawable.facebook))
                            .setIntent(new Intent(getActivity(), isYoutube ? YoutubeActivity.class : FacebookActivity.class).setAction(Intent.ACTION_CREATE_SHORTCUT))
                            .build();

                    shortcutManager.requestPinShortcut(shortcut, null);
                } else
                    Toast.makeText(getActivity(), "Pinned shortcuts are not supported!", Toast.LENGTH_SHORT).show();
            }
        } else {
            createShortcutBeforeO(isYoutube);
        }
    }

    private void createShortcutBeforeO(boolean isYoutube) {
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutIntent.putExtra("duplicate", false);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, isYoutube ? "Youtube" : "Facebook");
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getActivity().getApplicationContext(), isYoutube ? R.drawable.movie : R.drawable.facebook);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getActivity().getApplicationContext(), isYoutube ? YoutubeActivity.class : FacebookActivity.class));
        //shortcutIntent.setComponent(new ComponentName(getActivity().getPackageName(), isYoutube ? YoutubeActivity.class : FacebookActivity.class));
        getActivity().sendBroadcast(shortcutIntent);
    }
}