package com.example.more.ui.adapter;

import android.os.Parcelable;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.more.data.remote.model.VideoEntity;

import java.util.ArrayList;

public class PlayerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();
    ArrayList<String> mFragmentTitleList = new ArrayList<>();


    public PlayerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList.clear();
        mFragmentTitleList.clear();
    }

    @Override
    public Fragment getItem(int position) {

        if (getCount() <=position) {
            return null;
        }
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment frag = (Fragment) super.instantiateItem(container, position);
        mFragmentList.set(position,frag);
        return frag;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        try {
            super.restoreState(state, loader);
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return androidx.viewpager.widget.PagerAdapter.POSITION_NONE;
    }
}
