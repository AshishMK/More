package com.example.more.utills.ui;

import androidx.databinding.BindingAdapter;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.core.app.ActivityCompat;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

import timber.log.Timber;

public class DataBindingAdapters {

    @BindingAdapter("android:src")
    public static void setImageUri(ImageView view, String imageUri) {
        if (imageUri == null) {
            view.setImageURI(null);
        } else {
            view.setImageURI(Uri.parse(imageUri));
        }
    }

    @BindingAdapter("android:src")
    public static void setImageUri(ImageView view, Uri imageUri) {
        view.setImageURI(imageUri);
    }

    @BindingAdapter("android:src")
    public static void setImageDrawable(ImageView view, Drawable drawable) {
        view.setImageDrawable(drawable);
    }


    @BindingAdapter("profileImageNoQ")
    public static void loadImageQ(ImageView view, String imageUrl) {
        Timber.v(imageUrl);
        Glide.with(view.getContext())
                .load(imageUrl)
                .apply(
                        new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)
                                .override(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL)
                )
                .into(view);
    }

    @BindingAdapter("android:src")
    public static void setImageResource(ImageView imageView, int resource) {
        imageView.setImageDrawable(ActivityCompat.getDrawable(imageView.getContext(), resource));
    }
}