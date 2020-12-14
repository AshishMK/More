package com.example.more.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SuccessResponseEntity implements Parcelable {
    public SuccessResponseEntity(){

    }
    @SerializedName("success")
    @Expose
    Boolean status  = false;

    @SerializedName("message")
    public String message;


    protected SuccessResponseEntity(Parcel in) {
        status = (Boolean) in.readValue(Boolean.class.getClassLoader()) ;
        message = in.readString();
    }

    public static final Creator<SuccessResponseEntity> CREATOR = new Creator<SuccessResponseEntity>() {
        @Override
        public SuccessResponseEntity createFromParcel(Parcel in) {
            return new SuccessResponseEntity(in);
        }

        @Override
        public SuccessResponseEntity[] newArray(int size) {
            return new SuccessResponseEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(status);
        dest.writeString(message);
    }
}
