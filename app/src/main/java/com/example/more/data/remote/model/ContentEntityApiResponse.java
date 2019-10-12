package com.example.more.data.remote.model;

import com.example.more.data.local.entity.ContentEntity;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ContentEntityApiResponse {

    public ContentEntityApiResponse() {
        this.results = new ArrayList<>();
    }

    private long page;
    @SerializedName("data")
    private List<ContentEntity> results;

    public List<ContentEntity> getResults() {
        return results;
    }

    public void setResults(List<ContentEntity> results) {
        this.results = results;
    }
}
