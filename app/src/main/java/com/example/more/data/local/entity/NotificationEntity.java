package com.example.more.data.local.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = ("id"))
public class NotificationEntity implements Parcelable {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("media")
    @Expose
    private String media;

    @SerializedName("receivedAt")
    @Expose
    private Long receivedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public Long getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Long receivedAt) {
        this.receivedAt = receivedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.title);
        dest.writeString(this.media);
        dest.writeValue(this.receivedAt);
    }

    public NotificationEntity() {
    }

    protected NotificationEntity(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.title = in.readString();
        this.media = in.readString();
        this.receivedAt = (Long) in.readValue(Long.class.getClassLoader());
    }


    public static final Creator<NotificationEntity> CREATOR = new Creator<NotificationEntity>() {
        @Override
        public NotificationEntity createFromParcel(Parcel source) {
            return new NotificationEntity(source);
        }

        @Override
        public NotificationEntity[] newArray(int size) {
            return new NotificationEntity[size];
        }
    };


}
