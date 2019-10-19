package com.example.more.ui.adapter;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.more.BR;
import com.example.more.R;
import com.example.more.data.Status;
import com.example.more.data.local.dao.ContentDao;
import com.example.more.data.local.entity.ContentEntity;
import com.example.more.databinding.GridItemBinding;
import com.example.more.databinding.ListItemBinding;
import com.example.more.databinding.LoadMoreListItemBinding;
import com.example.more.ui.activity.DetailActivity;
import com.example.more.ui.activity.ListActivity;
import com.example.more.ui.activity.MemePagerActivity;
import com.example.more.ui.activity.YoutubePlayer;
import com.example.more.utills.Utils;
import com.example.more.utills.animation.AnimUtil;
import com.example.more.utills.animation.TransitionHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class to host list of content
 *
 * @see com.example.more.ui.fragment.ListFragment
 */
public class ContentListAdapter extends RecyclerView.Adapter<ContentListAdapter.ViewHolder> {
    Activity activity;
    private List<ContentEntity> contents;
    Status status = Status.SUCCESS;
    private static final int FOOTER_VIEW = 1;
    /***Note: We use Linear list for evey content type except meme contents.
     * for meme we use gris list*/
    int content_type;

    /**
     * Database object to perform star a conetnt operation
     */
    ContentDao contentDao;

    /**
     * is starred content filter enabled
     */
    public boolean isStarMode = false;

    public ContentListAdapter(Activity activity, int content_type, ContentDao contentDao) {
        this.activity = activity;
        this.content_type = content_type;
        this.contentDao = contentDao;
        contents = new ArrayList<>();
    }

    public void setStatus(Status status) {
        this.status = status;
        notifyItemChanged(contents.size());
    }

    public void setItems(List<ContentEntity> movies, boolean isFromLoadMore, Status status) {
        this.status = status;
        if (!isFromLoadMore) {
            this.contents.clear();
            notifyDataSetChanged();
        } else
            notifyItemChanged(contents.size());

        int startPosition = this.contents.size();
        this.contents.addAll(movies);
        notifyItemRangeInserted(startPosition == 0 ? 0 : startPosition, contents.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (contents.size() == position)
            return FOOTER_VIEW;
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(activity), i == FOOTER_VIEW ? R.layout.load_more_list_item : content_type == ListActivity.STORY ? R.layout.grid_list_item : R.layout.list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (i == getItemCount() - 1)
            viewHolder.bindToLoadMore();
        else
            viewHolder.bindTo(contents.get(i), viewHolder);
    }

    @Override
    public int getItemCount() {
        return contents.size() == 0 ? 0 : contents.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ListItemBinding listItemBinding;
        LoadMoreListItemBinding loadMoreListItemBinding;
        GridItemBinding gridItemBinding;

        public ViewHolder(@NonNull ViewDataBinding binding) {
            super(binding.getRoot());
            if (binding instanceof ListItemBinding) {
                listItemBinding = (ListItemBinding) binding;
            } else if (binding instanceof GridItemBinding) {
                gridItemBinding = (GridItemBinding) binding;
            } else {
                loadMoreListItemBinding = (LoadMoreListItemBinding) binding;
            }
        }

        public void bindTo(ContentEntity content, ViewHolder holder) {
            if (listItemBinding == null) {
                gridItemBinding.setVariable(BR.contentViewHolder, holder);
                gridItemBinding.setVariable(BR.content, content);
                return;
            }

            listItemBinding.setVariable(BR.contentViewHolder, holder);
            listItemBinding.setVariable(BR.content, content);

        }

        public void bindToLoadMore() {
            loadMoreListItemBinding.setStatus(status);
            loadMoreListItemBinding.imageSmall.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);
                    AnimatorSet set = (AnimatorSet) loadMoreListItemBinding.imageSmall.getTag();
                    if (set != null) {
                        set.cancel();
                        loadMoreListItemBinding.imageSmall.setRotation(0);
                    }
                    if (status == Status.LOADING) {
                        AnimUtil.rotateView(loadMoreListItemBinding.imageSmall);
                    }
                }
            });


        }

        public void onStarredChanged(View view, boolean starred) {
            int pos = getAdapterPosition();
            ContentEntity content = contents.get(pos);
            content.setStarred(starred);
            contentDao.updateContentStarred(content);
            if (isStarMode && !starred) {
                contents.remove(pos);
                if (0 == contents.size() ) {
                    System.out.println("gghg "+pos);
                    notifyDataSetChanged();
                }
                else {
                    notifyItemRemoved(pos);
                }
            }

        }

        public void onItemClick(ContentEntity content) {
            if (content.getContentType() == ListActivity.STORY) {
                Intent intent = new Intent(activity, MemePagerActivity.class);

                intent.putParcelableArrayListExtra("contents", (ArrayList<? extends Parcelable>) contents);
                intent.putExtra("position", getAdapterPosition());
                transitionToMemePagerActivity(intent);
                return;
            }
            if (content.getContentType() == ListActivity.MEDIA) {
                Intent intent = new Intent(activity, YoutubePlayer.class);
                intent.putExtra("url", content.getMedia_link());
                activity.startActivity(intent);
                return;
            }
            Intent intent = new Intent(activity, DetailActivity.class);
            intent.putExtra("content", content);
            //  activity.startActivity(intent);
            transitionToActivity(intent);
        }


        /**
         * method to perform shared object transition on target activity
         *
         * @param intent
         */
        public void transitionToActivity(Intent intent) {
            final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, true,
                    new Pair<>(listItemBinding.title, activity.getString(R.string.title_transaction)),
                    new Pair<>(listItemBinding.logo, activity.getString(R.string.image_transaction)),
                    new Pair<>(listItemBinding.dated, activity.getString(R.string.description_transaction)));
            ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs);
            activity.startActivity(intent, transitionActivityOptions.toBundle());
        }

        /**
         * method to perform shared object transition on target activity
         *
         * @param intent
         */
        public void transitionToMemePagerActivity(Intent intent) {
            final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, true,
                    new Pair<>(gridItemBinding.logo, activity.getString(R.string.image_transaction)));
            ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs);
            activity.startActivity(intent, transitionActivityOptions.toBundle());
        }

    }
}
