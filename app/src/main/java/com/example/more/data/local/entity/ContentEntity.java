package com.example.more.data.local.entity;

import androidx.databinding.BindingAdapter;
import androidx.room.Entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import timber.log.Timber;

import static com.example.more.di.module.ApiModule.base_url;
import static com.example.more.di.module.ApiModule.base_url_download;

@Entity(primaryKeys = ("id"))
public class ContentEntity implements Parcelable {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("content")
    @Expose
    private String content_text;

    @SerializedName("media")
    private String media_link;

    @SerializedName("created_at")
    @Expose
    private String created_at;


    @SerializedName("tag")
    @Expose
    private String tag;

    @SerializedName("content_type")
    @Expose
    private int contentType;

    @SerializedName("credits")
    @Expose
    private String credits;

    boolean isStarred;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent_text() {
        return content_text;
    }

    public void setContent_text(String content_text) {
        this.content_text = content_text;
    }

    public String getMedia_link() {
        if (media_link != null && !media_link.startsWith("http")) {
            //posterPath = String.format("https://image.tmdb.org/t/p/w500%s", posterPath);
        }
        return media_link;
    }

    public void setMedia_link(String media_link) {
        this.media_link = media_link;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.description);
        dest.writeString(this.content_text);
        dest.writeString(this.media_link);
        dest.writeString(this.created_at);
        dest.writeString(this.tag);
        dest.writeInt(this.contentType);
        dest.writeString(this.credits);
        dest.writeValue(this.isStarred);
    }

    public ContentEntity() {
    }

    protected ContentEntity(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.description = in.readString();
        this.content_text = in.readString();
        this.media_link = in.readString();
        this.created_at = in.readString();
        this.tag = in.readString();
        this.contentType = in.readInt();
        this.credits = in.readString();
        this.isStarred = (boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<ContentEntity> CREATOR = new Creator<ContentEntity>() {
        @Override
        public ContentEntity createFromParcel(Parcel source) {
            return new ContentEntity(source);
        }

        @Override
        public ContentEntity[] newArray(int size) {
            return new ContentEntity[size];
        }
    };


    @BindingAdapter("profileImage")
    public static void loadImage(ImageView view, String imageUrl) {
        Timber.v(base_url_download+imageUrl+".png");
   /*     if (imageUrl != null && !imageUrl.contains(".")) {
            Glide.with(view.getContext())
                    .load("http://img.youtube.com/vi/" + imageUrl + "/0.jpg").apply(new RequestOptions().circleCrop())
                    .into(view);
            return;
        }*/
        Glide.with(view.getContext())
                .load(imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(view);
    }

    @BindingAdapter("profileImageThumb")
    public static void loadImageThumb(ImageView view, String imageUrl) {
        Timber.v(base_url_download+imageUrl+".png");
   /*     if (imageUrl != null && !imageUrl.contains(".")) {
            Glide.with(view.getContext())
                    .load("http://img.youtube.com/vi/" + imageUrl + "/0.jpg").apply(new RequestOptions().circleCrop())
                    .into(view);
            return;
        }*/
        Glide.with(view.getContext())
                .load(imageUrl)
                .into(view);
    }
}
