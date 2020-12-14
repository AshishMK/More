package com.example.more.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = ("id"))
public class VideoEntity extends BaseObservable implements Parcelable {
    @SerializedName("thumb")
    @Expose
    public String thumb;
    @Bindable
    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    @Bindable
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Bindable
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Bindable
    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Bindable
    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    @Bindable
    public Integer getMedia_type() {
        return media_type;
    }

    public void setMedia_type(Integer media_type) {
        this.media_type = media_type;
    }

    @Bindable
    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    @Bindable
    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    @Bindable
    public Integer getShares() {
        return shares;
    }

    public void setShares(Integer shares) {
        this.shares = shares;
    }

    @Bindable
    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    @Bindable
    public Integer getEmotion() {
        return emotion;
    }

    public void setEmotion(Integer emotion) {
        this.emotion = emotion;
    }

    @Bindable
    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    @SerializedName("id")
    @Expose
   public Integer id;

    @SerializedName("url")
    @Expose
    public String url;

    @SerializedName("description")
    @Expose
    public String description;

    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("user_id")
    @Expose
    public Integer user_id;

    @SerializedName("media_type")
    @Expose
    public Integer media_type;

    @SerializedName("views")
    @Expose
    public Integer views;

    @SerializedName("likes")
    @Expose
    public Integer likes;

    @SerializedName("shares")
    @Expose
    public Integer shares;

    @SerializedName("category")
    @Expose
    public Integer category;

    @SerializedName("emotion")
    @Expose
    public Integer emotion;

    @SerializedName("mid")
    @Expose
    public Integer mid;
    public VideoEntity(){

    }

    public VideoEntity(Parcel parcel) {
        id = (Integer) parcel.readValue(Integer.class.getClassLoader());
        thumb = parcel.readString();
        url = parcel.readString();
        description = parcel.readString();
        title = parcel.readString();
        user_id = (Integer) parcel.readValue(Integer.class.getClassLoader());
        media_type = (Integer) parcel.readValue(Integer.class.getClassLoader());
        views = (Integer) parcel.readValue(Integer.class.getClassLoader());
        likes = (Integer) parcel.readValue(Integer.class.getClassLoader());
        shares = (Integer) parcel.readValue(Integer.class.getClassLoader());
        category = (Integer) parcel.readValue(Integer.class.getClassLoader());
        emotion = (Integer) parcel.readValue(Integer.class.getClassLoader());
        mid = (Integer) parcel.readValue(Integer.class.getClassLoader());
    }


    public static final Creator<VideoEntity> CREATOR = new Creator<VideoEntity>() {
        @Override
        public VideoEntity createFromParcel(Parcel in) {
            return new VideoEntity(in);
        }

        @Override
        public VideoEntity[] newArray(int size) {
            return new VideoEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeValue(id);
        parcel.writeString(thumb);
        parcel.writeString(url);
        parcel.writeString(description);
        parcel.writeString(title);
        parcel.writeValue(user_id);
        parcel.writeValue(media_type);
        parcel.writeValue(views);
        parcel.writeValue(likes);
        parcel.writeValue(shares);
        parcel.writeValue(category);
        parcel.writeValue(emotion);
        parcel.writeValue(mid);
    }

    public void setValues(VideoEntity entity) {
        this.id = entity.id;
        this.thumb = entity.thumb;
        this.url = entity.url;
        this.description = entity.description;
        this.title = entity.title;
        this.user_id = entity.user_id;
        this.media_type = entity.media_type;
        this.views = entity.views;
        this.likes = entity.likes;
        this.shares = entity.shares;
        this.category = entity.category;
        this.emotion = entity.emotion;
        this.mid = entity.mid;
    }
}
