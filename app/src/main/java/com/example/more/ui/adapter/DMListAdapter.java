package com.example.more.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.more.BR;
import com.example.more.R;
import com.example.more.databinding.DMListItemBinding;
import com.example.more.utills.Utils;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Status;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter class to host list of facebook download
 * @see com.example.more.ui.activity.DownloadManagerActivity
 */
public class DMListAdapter extends RecyclerView.Adapter<DMListAdapter.ViewHolder> {
    Activity activity;
    private List<Download> contents;
    OnClickItem onClickItem;

    public interface OnClickItem {
        public void onClickItem(Download download, boolean canceled, int position);
    }

    public DMListAdapter(Activity activity) {
        this.activity = activity;
        onClickItem = (OnClickItem) activity;
        contents = new ArrayList<>();
    }

    public ArrayList<Download> getAdapterList() {
        return (ArrayList<Download>) contents;
    }

    public void setItems(List<Download> movies) {
        int startPosition = this.contents.size();
        this.contents.clear();
        this.contents.addAll(movies);
        notifyItemRangeChanged(startPosition, movies.size());
    }

    public void setItem(Download download, int position) {
        this.contents.set(position, download);
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.dm_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bindTo(contents.get(i), viewHolder);
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        DMListItemBinding listItemBinding;

        public ViewHolder(@NonNull DMListItemBinding binding) {
            super(binding.getRoot());
            listItemBinding = binding;

        }

        public void bindTo(Download content, ViewHolder holder) {
            listItemBinding.setVariable(BR.contentViewHolder, holder);
            listItemBinding.setVariable(BR.content, content);
        }

        public void onItemClick(Download content, boolean canceled) {
            onClickItem.onClickItem(content, canceled, getAdapterPosition());
        }

        public void onItemClick(Download content) {
            if (content.getStatus() != Status.COMPLETED) {
                Toast.makeText(activity, R.string.not_downloaded, Toast.LENGTH_SHORT).show();
                return;
            }
            else if(!content.getFile().contains(".mp4")){
                Utils.shareFile(content.getFile());
                return;
            }

            Uri shareFile = Uri.parse(content.getFile());
            Intent intent = new Intent(Intent.ACTION_VIEW, (shareFile));
            intent.setDataAndType((shareFile), "video/*");
            activity.startActivity(intent);

        }
    }

    @BindingAdapter("FBImage")
    public static void loadImage(ImageView view, Download download) {
        if (download
                .getStatus() == Status.COMPLETED) {
            Glide.with(view.getContext())
                    .load(download.getFile())
                    .into(view);
        } else
            Glide.with(view.getContext())
                    .load(download.getFileUri())
                    .into(view);
    }
}

