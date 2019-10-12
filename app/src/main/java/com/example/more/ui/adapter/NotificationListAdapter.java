package com.example.more.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.more.BR;
import com.example.more.R;
import com.example.more.data.local.entity.NotificationEntity;
import com.example.more.databinding.NotificationListItemBinding;
import com.example.more.ui.activity.DetailActivity;

import java.util.ArrayList;
import java.util.List;
/**
 * Adapter class to host list of Notification
 * @see com.example.more.ui.fragment.NotificationFragment
 */
public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {
    Activity activity;
    private List<NotificationEntity> contents;
    public NotificationListAdapter(Activity activity) {
        this.activity = activity;
        contents = new ArrayList<>();
    }

    public void setItems(List<NotificationEntity> movies) {
        int startPosition = this.contents.size();
        this.contents.clear();
        this.contents.addAll(movies);
        notifyItemRangeChanged(startPosition, movies.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.notification_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
             viewHolder.bindTo(contents.get(i),viewHolder);
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        NotificationListItemBinding listItemBinding;

        public ViewHolder(@NonNull NotificationListItemBinding binding) {
            super(binding.getRoot());
            listItemBinding = binding;

        }

        public void bindTo(NotificationEntity content, ViewHolder holder) {
            listItemBinding.setVariable(BR.contentViewHolder,holder);
            listItemBinding.setVariable(BR.content,content);
        }

       public void onItemClick(NotificationEntity content){

           Intent intent = new Intent(activity, DetailActivity.class);
           intent.putExtra("content",content);
           activity.startActivity(intent);

        }

    }
}
