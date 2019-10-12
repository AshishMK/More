package com.example.more.data.remote.model;

import com.example.more.data.local.entity.SearchEntity;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchEntityApiResponse {

    public SearchEntityApiResponse() {
        this.results = new ArrayList<>();
    }

    @SerializedName("data")
    private List<SearchEntity> results;



    public List<SearchEntity> getResults() {
        return results;
    }

    public void setResults(List<SearchEntity> results) {
        this.results = results;
    }
}
