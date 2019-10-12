package com.example.more.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.more.BR;
import com.example.more.R;
import com.example.more.data.local.entity.SearchEntity;
import com.example.more.databinding.SearchItemBinding;

import java.util.ArrayList;


/**
 * Adapter class to host list of Search tags
 * @see com.example.more.ui.activity.SearchActivity
 */

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    //ArrayList on which list view will be inflated
    @NonNull
    ArrayList<SearchEntity> data = new ArrayList<>();

    //Current search query
    Context ctx;
    OnSearchItemClick mOnSearchItemClick;

    public interface OnSearchItemClick {
        public void onItemSearchClicked(String s);

        public void onItemEditClicked(String s);
    }

    public SearchListAdapter(Context ctx) {
        this.ctx = ctx;
        mOnSearchItemClick = (OnSearchItemClick) ctx;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(ctx), R.layout.search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(data.get(position), holder);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Method to update current searchlist
     *
     * @param listm SearchList to be replace with current search ArrayList
     */
    public void updateSearchData(ArrayList<SearchEntity> listm) {
        data.clear();
        data.addAll(listm);
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SearchItemBinding binding;

        public ViewHolder(@NonNull SearchItemBinding binding) {
            super(binding.root);
            this.binding = binding;
        }

        public void bindTo(SearchEntity content, ViewHolder holder) {
            binding.setVariable(BR.contentViewHolder, holder);
            binding.setVariable(BR.content, content);
        }

        public void searchClicked(View v) {
            mOnSearchItemClick.onItemSearchClicked(data.get(getAdapterPosition()).getTag());
        }

        public void editClicked(View v) {
            mOnSearchItemClick.onItemEditClicked(data.get(getAdapterPosition()).getTag());
        }
    }
}