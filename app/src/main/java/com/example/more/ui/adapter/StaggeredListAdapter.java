package com.example.more.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.more.BR;
import com.example.more.R;
import com.example.more.data.local.model.WhatsAppStatus;
import com.example.more.databinding.StaggeredItemBinding;
import com.example.more.utills.Screen;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Adapter class to host list of whatsapp status
 * @see com.example.more.ui.fragment.WhatsappStatusFragment
 */
public class StaggeredListAdapter extends RecyclerView.Adapter<StaggeredListAdapter.ViewHolder> {
    Activity activity;
    private List<WhatsAppStatus> contents;
    public onItemClick onItemClick;

    public interface onItemClick {
        void onDownloadClicked(WhatsAppStatus path);

        void onItemShared(String path);
    }

    public StaggeredListAdapter(Activity activity, onItemClick onItemClick) {
        this.activity = activity;
        this.onItemClick = onItemClick;
        contents = new ArrayList<>();
    }

    public void setItems(List<WhatsAppStatus> movies) {
        int startPosition = this.contents.size();
        this.contents.addAll(movies);
        notifyItemRangeChanged(startPosition, movies.size());
    }

    public void setItem(WhatsAppStatus data) {

        this.contents.set(data.position, data);
        notifyItemChanged(data.position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.staggered_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        viewHolder.bindTo(contents.get(position), viewHolder);
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        StaggeredItemBinding listItemBinding;

        public ViewHolder(@NonNull StaggeredItemBinding binding) {
            super(binding.getRoot());
            listItemBinding = binding;

        }

        public void bindTo(WhatsAppStatus content, ViewHolder holder) {
            if (getAdapterPosition() == 0 || getAdapterPosition() % 2 == 0)
                listItemBinding.img.getLayoutParams().height = Screen.dp(190);
            else {
                listItemBinding.img.getLayoutParams().height = Screen.dp(220);
            }
            listItemBinding.setVariable(BR.contentViewHolder, holder);
            listItemBinding.setVariable(BR.content, content);
        }


        public void onDownloadClicked(WhatsAppStatus content) {
            content.position = getAdapterPosition();
            onItemClick.onDownloadClicked(content);
        }

        public void onItemShared(String content) {
            onItemClick.onItemShared(content);
        }

        public void onItemClick(WhatsAppStatus content) {
if(!content.type.startsWith(".mp4")){
    onItemShared(content.path);
    return;
}
            Uri shareFile = Uri.parse(content.path);
            Intent intent = new Intent(Intent.ACTION_VIEW, (shareFile));
            intent.setDataAndType( (shareFile), content.type.startsWith(".mp4")?"video/*":"image/*");
            activity.startActivity(intent);
        }
    }

    @BindingAdapter("whatsImage")
    public static void loadImage(ImageView view, WhatsAppStatus imageUrl) {
        Timber.v(imageUrl.path);

        Glide.with(view.getContext())
                .load(imageUrl.path)
                .into(view);
    }
}
