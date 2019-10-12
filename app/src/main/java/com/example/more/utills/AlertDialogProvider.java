package com.example.more.utills;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.more.Application.AppController;
import com.example.more.BR;
import com.example.more.R;
import com.example.more.data.local.pref.PreferencesStorage;
import com.example.more.data.local.pref.SharedPrefStorage;
import com.example.more.databinding.AlertDialogBinding;
import com.example.more.databinding.DialogItemBinding;

/**
 * {@link DialogFragment} class to provide different types of Dialog to application wide
 */
public class AlertDialogProvider extends DialogFragment {
    /**
     * Types of Dialogs
     */
    public static int TYPE_EDIT = 0;
    public static int TYPE_NORMAL = 1;
    public static int TYPE_LIST = 2;
    public static int TYPE_EDIT_LINK = 3;
    public static int TYPE_NORMAL_DONT = 4;
    public static final String TITLE_DIALOG = "title_dialog";
    public static final String MESSAGE_DIALOG = "message_dialog";
    public static final String LIST_DIALOG = "list_dialog";
    public static final String LIST_DIALOG_DRAWABLES = "list_dialog_drawables";
    public static final String DIALOG_TYPE = "dialog_type";


    public static AlertDialogProvider getInstance(String title, String message, int dialog_type) {
        AlertDialogProvider fragment = new AlertDialogProvider();
        Bundle args = new Bundle();
        args.putInt(DIALOG_TYPE, dialog_type);
        args.putString(TITLE_DIALOG, title);
        args.putString(MESSAGE_DIALOG, message);
        fragment.setArguments(args);
        return fragment;
    }

    public static AlertDialogProvider getInstance(String title, String[] items, int[] drawables, int dialog_type) {
        AlertDialogProvider fragment = new AlertDialogProvider();
        Bundle args = new Bundle();
        args.putInt(DIALOG_TYPE, dialog_type);
        args.putString(TITLE_DIALOG, title);
        args.putStringArray(LIST_DIALOG, items);
        args.putIntArray(LIST_DIALOG_DRAWABLES, drawables);
        fragment.setArguments(args);
        return fragment;
    }

    public String title;
    public String message;
    public int dialog_type = 0;
    public String[] items;
    public int[] item_drawables;
    /*
     * I am using DataBinding
     * */
    private AlertDialogBinding binding;
    AlertDialogListener alertDialogListener;
    AlertDialogItemListener alertDialogItemListener;

    public void setAlertDialogListener(AlertDialogListener alertDialogListener) {
        this.alertDialogListener = alertDialogListener;
    }

    public void setAlertDialogItemListener(AlertDialogItemListener alertDialogItemListener) {
        this.alertDialogItemListener = alertDialogItemListener;
    }

    public interface AlertDialogListener {
        void onDialogCancel();

        void onDialogOk(String text);
    }

    public interface AlertDialogItemListener {
        void onItemClicked(int position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(TITLE_DIALOG);
            message = getArguments().getString(MESSAGE_DIALOG);
            dialog_type = getArguments().getInt(DIALOG_TYPE);
            items = getArguments().getStringArray(LIST_DIALOG);
            item_drawables = getArguments().getIntArray(LIST_DIALOG_DRAWABLES);
        }
        if (dialog_type != TYPE_NORMAL) {
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        initialiseView(container);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        //getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /*
     * Initialising the View using Data Binding
     * */
    private void initialiseView(ViewGroup viewGroup) {

        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.alert_dialog_fragment, viewGroup, false);
        binding.setVariable(BR.content, this);
        binding.setPreferenceStorage(new SharedPrefStorage(AppController.getInstance()));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (dialog_type == TYPE_LIST) {
            setUplist();
        }

    }

    void setUplist() {
        binding.list.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.list.setAdapter(new RecyclerView.Adapter<ListViewHolder>() {

            @NonNull
            @Override
            public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new ListViewHolder(DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.dialog_list_item, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull ListViewHolder listViewHolder, int i) {
                listViewHolder.bindTo(items[i], item_drawables[i], listViewHolder, i);
            }

            @Override
            public int getItemCount() {
                return items.length;
            }
        });
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        DialogItemBinding binding;

        public ListViewHolder(@NonNull DialogItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bindTo(String content, int src, ListViewHolder holder, int position) {
            binding.setVariable(BR.contentViewHolder, holder);
            binding.setVariable(BR.content, content);
            binding.setVariable(BR.position, position);
            binding.setVariable(BR.src, src);
        }

        public void onItemClick(int position) {
            AlertDialogProvider.this.dismiss();
            if (alertDialogItemListener != null) {
                alertDialogItemListener.onItemClicked(position);
            }

        }


    }

    public void onDialogCancel(View v) {
        this.dismiss();
        if (alertDialogListener != null) {
            alertDialogListener.onDialogCancel();
        }
    }

    public void onDialogOk(View v) {
        this.dismiss();
        if (alertDialogListener != null) {
            alertDialogListener.onDialogOk(binding.editText.getText().toString());
        }

    }
}
