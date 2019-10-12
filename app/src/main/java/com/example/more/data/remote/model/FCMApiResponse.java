package com.example.more.data.remote.model;

import com.example.more.data.local.entity.ContentEntity;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FCMApiResponse {

    public FCMApiResponse() {
        this.result = false;
    }

    @SerializedName("success")
    private boolean result;

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

}
