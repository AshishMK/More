package com.example.more.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VideoListEntity extends SuccessResponseEntity implements Parcelable {
    @SerializedName("data")
    @Expose
    public ArrayList<VideoEntity> data;

    public VideoListEntity() {

    }

    public VideoListEntity(Parcel in) {
        super(in);
        data = (ArrayList<VideoEntity>) in.readValue(ArrayList.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(data);
    }

    public static final Creator<VideoListEntity> CREATOR = new Creator<VideoListEntity>() {
        @Override
        public VideoListEntity createFromParcel(Parcel in) {
            return new VideoListEntity(in);
        }

        @Override
        public VideoListEntity[] newArray(int size) {
            return new VideoListEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return super.describeContents();
    }
}
