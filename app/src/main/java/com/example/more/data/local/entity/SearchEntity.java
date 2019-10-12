package com.example.more.data.local.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = ("id"))
public class SearchEntity implements Parcelable {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("tag")
    @Expose
    private String tag;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.tag);
    }

    public SearchEntity() {
    }

    protected SearchEntity(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.tag = in.readString();
    }

    public static final Creator<SearchEntity> CREATOR = new Creator<SearchEntity>() {
        @Override
        public SearchEntity createFromParcel(Parcel source) {
            return new SearchEntity(source);
        }

        @Override
        public SearchEntity[] newArray(int size) {
            return new SearchEntity[size];
        }
    };


}
