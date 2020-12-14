package com.example.more.utills;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.more.Application.AppController;
import com.example.more.BR;
import com.example.more.R;
import com.example.more.data.local.pref.SharedPrefStorage;
import com.example.more.databinding.AlertDialogBinding;
import com.example.more.databinding.DialogItemBinding;
import com.example.more.databinding.DialogItemMultiBinding;

import java.util.ArrayList;

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
    public static int TYPE_MULTI_LIST = 6;
    public static int TYPE_EDIT_LINK = 3;
    public static int TYPE_NORMAL_DONT = 4;
    public static int TYPE_PROGRESS_DEFINITE = 5;
    public static int TYPE_UPDATE = 7;
    public static int TYPE_UPDATE_FORCE = 8;
    public static int TYPE_EDIT_BIG = 9;
    public static final String TITLE_DIALOG = "title_dialog";
    public static final String MESSAGE_DIALOG = "message_dialog";
    public static final String LIST_DIALOG = "list_dialog";
    public static final String LIST_DIALOG_DRAWABLES = "list_dialog_drawables";
    public static final String DIALOG_TYPE = "dialog_type";
    public static final String LIST_DIALOG_SELECTED = "list_dialog_selected";
    // public static final String PROGRESS_DIALOG_DEFINITE = "progress_dialog_definite";
    public ObservableInt progress = new ObservableInt(0);
    boolean showCancelDownload = true;
    public boolean isIndefinte = true;
    int varificationMessage = R.string.val_sel_item;

    boolean useListDivider = false;
    boolean canceledOnTouchOutside = false;
    public static AlertDialogProvider getInstance(String title, String message, int dialog_type, boolean showCancelDownload) {
        AlertDialogProvider fragment = new AlertDialogProvider();
        Bundle args = new Bundle();
        args.putInt(DIALOG_TYPE, dialog_type);
        args.putBoolean("showCancelDownload", showCancelDownload);
        args.putString(TITLE_DIALOG, title);
        args.putString(MESSAGE_DIALOG, message);
        fragment.setArguments(args);
        return fragment;
    }

    public AlertDialogProvider useListdivider(boolean use) {
        useListDivider = use;
        return this;
    }

    public static AlertDialogProvider getInstance(String title, ArrayList<String> items, ArrayList<String> selectedItems, boolean showCancelDownload) {
        AlertDialogProvider fragment = new AlertDialogProvider();
        Bundle args = new Bundle();
        args.putInt(DIALOG_TYPE, TYPE_MULTI_LIST);
        args.putString(TITLE_DIALOG, title);
        args.putStringArrayList(LIST_DIALOG, items);
        args.putStringArrayList(LIST_DIALOG_SELECTED, selectedItems);
        args.putBoolean("showCancelDownload", showCancelDownload);
        fragment.setArguments(args);
        return fragment;
    }

    public static AlertDialogProvider getInstance(String title, ArrayList<String> items, ArrayList<Integer> drawables, int dialog_type, boolean showCancelDownload) {
        AlertDialogProvider fragment = new AlertDialogProvider();
        Bundle args = new Bundle();
        args.putInt(DIALOG_TYPE, dialog_type);
        args.putString(TITLE_DIALOG, title);
        args.putIntegerArrayList(LIST_DIALOG_DRAWABLES, drawables);
        args.putBoolean("showCancelDownload", showCancelDownload);
        args.putStringArrayList(LIST_DIALOG, items);
        fragment.setArguments(args);

        return fragment;
    }

    public AlertDialogProvider setIsIndefinte(boolean isIndefinte){
        this.isIndefinte = isIndefinte;
        //getDialog().setCanceledOnTouchOutside(canceledOnTouchOutside);
        return this;
    }

    public AlertDialogProvider setCanceledOnTouchOutside(boolean canceledOnTouchOutside){
        this.canceledOnTouchOutside = canceledOnTouchOutside;
        //getDialog().setCanceledOnTouchOutside(canceledOnTouchOutside);
        return this;
    }

    public ObservableField<String> title = new ObservableField<>();
    public String message;
    public int dialog_type = 0;
    public ArrayList<String> items = new ArrayList<>();
    public ArrayList<String> selectedItems = new ArrayList<>();
    public ArrayList<Integer> item_drawables = new ArrayList<>();
    /*
     * I am using DataBinding
     * */
    private AlertDialogBinding binding;
    AlertDialogListener alertDialogListener;
    AlertDialogItemListener alertDialogItemListener;
    AlertDialogMultiItemListener alertDialogMultiItemListener;

    public AlertDialogProvider setAlertDialogListener(AlertDialogListener alertDialogListener) {
        this.alertDialogListener = alertDialogListener;
        return this;
    }

    public AlertDialogProvider setAlertDialogItemListener(AlertDialogItemListener alertDialogItemListener) {
        this.alertDialogItemListener = alertDialogItemListener;
        return this;
    }

    public AlertDialogProvider setAlertDialogMultiItemListener(AlertDialogMultiItemListener alertDialogMultiItemListener) {
        this.alertDialogMultiItemListener = alertDialogMultiItemListener;
        return this;
    }

    public interface AlertDialogListener {
        void onDialogCancel();

        void onDialogOk(String text, AlertDialogProvider dialog);

    }

    public interface AlertDialogItemListener {
        void onItemClicked(int position);
    }

    public interface AlertDialogMultiItemListener {
        void onDoneClicked(ArrayList<String> selectedItems);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title.set(getArguments().getString(TITLE_DIALOG));
            message = getArguments().getString(MESSAGE_DIALOG);
            showCancelDownload = getArguments().getBoolean("showCancelDownload");
            dialog_type = getArguments().getInt(DIALOG_TYPE);
            items = getArguments().getStringArrayList(LIST_DIALOG);
            selectedItems = getArguments().getStringArrayList(LIST_DIALOG_SELECTED);
            item_drawables = getArguments().getIntegerArrayList(LIST_DIALOG_DRAWABLES);
        }
        if (dialog_type != TYPE_NORMAL) {
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
        } else {
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyleSmall);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        initialiseView(container);
        getDialog().setCanceledOnTouchOutside(canceledOnTouchOutside);
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
        binding.setTitle(title);
        binding.setContent(this);
        binding.setFileProgress(progress);
        binding.setPreferenceStorage(new SharedPrefStorage(AppController.getInstance()));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.ok.setText((dialog_type == TYPE_EDIT_LINK || dialog_type == TYPE_EDIT_BIG || dialog_type == TYPE_EDIT) ? R.string.submit : dialog_type == TYPE_PROGRESS_DEFINITE ? R.string.ok_cancel_download : (dialog_type == TYPE_UPDATE_FORCE || dialog_type == TYPE_UPDATE ? R.string.update : R.string.ok));
        if (dialog_type != TYPE_NORMAL) {
        } else {
            if (dialog_type == TYPE_UPDATE_FORCE) {
                binding.ok.setText(R.string.update);
                binding.cancel.setVisibility(View.GONE);
            }
            if (dialog_type == TYPE_UPDATE) {
                binding.ok.setText(R.string.update);
            }
        }
        if (dialog_type == TYPE_LIST) {
            setUplist();
        } else if (dialog_type == TYPE_MULTI_LIST) {
            setUpMultiList();
        }

        else if( dialog_type == TYPE_PROGRESS_DEFINITE ){
            binding.cancelDownload.setVisibility(showCancelDownload ? View.VISIBLE: View.GONE);
            binding.dummy.setVisibility(showCancelDownload ? View.GONE: View.VISIBLE);
            binding.loader.setVisibility(View.VISIBLE);
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
                listViewHolder.bindTo(items.get(i), item_drawables.size() == 0 ? 0 : item_drawables.get(i), listViewHolder, i);
            }

            @Override
            public int getItemCount() {
                return items.size();
            }
        });
    }

    void setUpMultiList() {
        binding.list.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        binding.list.setAdapter(new RecyclerView.Adapter<ListViewMultiHolder>() {

            @NonNull
            @Override
            public ListViewMultiHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new ListViewMultiHolder(DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.dialog_list_multi_item, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull ListViewMultiHolder listViewHolder, int i) {
                listViewHolder.bindTo(items.get(i), listViewHolder, i);
            }

            @Override
            public int getItemCount() {
                return items.size();
            }
        });
    }

    public class ListViewMultiHolder extends RecyclerView.ViewHolder {
        DialogItemMultiBinding binding;
        ObservableBoolean isSelected = new ObservableBoolean(false);

        public ListViewMultiHolder(@NonNull DialogItemMultiBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            binding.setIsSelected(isSelected);
        }

        public void bindTo(String content, ListViewMultiHolder holder, int position) {
            binding.setVariable(BR.contentViewHolder, holder);
            binding.setVariable(BR.content, content);

            binding.setVariable(BR.content, content);
            binding.setVariable(BR.position, position);
            isSelected.set(selectedItems.contains(content.toLowerCase()));
        }

        public void onItemClick(int position) {
            //AlertDialogProvider.this.dismiss();


            if (selectedItems.contains(items.get(position).toLowerCase())) {
                selectedItems.remove(items.get(position).toLowerCase());
            } else {
                selectedItems.add(items.get(position).toLowerCase());
            }
            isSelected.set(selectedItems.contains(items.get(position).toLowerCase()));
        }

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
            binding.setUseDivider(!useListDivider ? false : position < items.size() - 1);
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
        if (dialog_type != TYPE_EDIT_BIG && dialog_type != TYPE_EDIT_LINK && dialog_type != TYPE_EDIT) {
            this.dismiss();
        }
        if (alertDialogListener != null) {
            alertDialogListener.onDialogOk(binding.editText.getText().toString(), this);
        }

    }

    public AlertDialogProvider setVarificationMessage(@StringRes int varifyMessage) {
        varificationMessage = varifyMessage;
        return this;
    }

    public void onDoneClickListener(View v) {
        if (selectedItems.size() == 0) {
            Toast.makeText(getActivity(), varificationMessage, Toast.LENGTH_SHORT).show();
            return;
        }
        this.dismiss();
        if (alertDialogMultiItemListener != null) {
            alertDialogMultiItemListener.onDoneClicked(selectedItems);
        }
    }
}
