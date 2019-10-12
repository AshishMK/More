package com.example.more.ui.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.more.R;
import com.example.more.data.local.entity.ContentEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter class to host fragments page
 *
 * @see com.example.more.ui.activity.HomeActivity
 */
public class MemePagerAdapter extends PagerAdapter {
    private List<ContentEntity> contents = new ArrayList<>();
    Context ctx;


    public MemePagerAdapter(Context ctx, ArrayList<ContentEntity> contents) {
        this.ctx = ctx;
        this.contents.clear();
        this.contents.addAll(contents);
    }

    public String getUrl(int position) {
        return contents.get(position).getMedia_link();
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(ctx);
        ContentEntity.loadImage(imageView, contents.get(position).getMedia_link());
        container.addView(imageView, 0);


        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}

