package com.example.more.data.remote.api;


import com.example.more.data.remote.model.ContentEntityApiResponse;
import com.example.more.data.remote.model.FCMApiResponse;
import com.example.more.data.remote.model.SearchEntityApiResponse;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ContentApiService {
    @Multipart
    @POST("getContentList")
    Observable<ContentEntityApiResponse> fetchContent(@Part("content_type") int contentType, @Part("offset") int offset);
    @Multipart
    @POST("getContentListByTag")
    Observable<ContentEntityApiResponse> fetchContentByTag(@Part("content_type") int contentType, @Part("offset") int offset, @Part("tag") RequestBody tag);
    @Multipart
    @POST("searchTags")
    Observable<SearchEntityApiResponse> searchTags(@Part("tag") RequestBody tag);
    @Multipart
    @POST("insert_fcm")
    Observable<FCMApiResponse> insert_fcm(@Part("fcm") RequestBody tag);


}
