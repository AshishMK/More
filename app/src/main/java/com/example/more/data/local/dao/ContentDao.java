package com.example.more.data.local.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.more.data.local.entity.ContentEntity;
import com.example.more.data.local.entity.NotificationEntity;
import com.example.more.data.local.entity.SearchEntity;
import com.example.more.data.remote.model.VideoEntity;

import java.util.List;

@Dao
public interface ContentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertContents(List<ContentEntity> movies);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertMediaList(List<VideoEntity> list);

    @Query("SELECT * FROM `VideoEntity` where category = :mediaCategory order by id desc  LIMIT 10 OFFSET :offset")
    public List<VideoEntity> getMediaList(int mediaCategory, int offset);

    @Query("SELECT * FROM `ContentEntity` where contentType = :content_type order by id desc  LIMIT 10 OFFSET :offset")
    List<ContentEntity> getContentByContentType(int content_type, int offset);

    @Query("SELECT * FROM `ContentEntity` where contentType = :content_type AND tag like '%' ||  :tag || '%' order by id desc  LIMIT 10 OFFSET :offset")
    List<ContentEntity> getContentByTag(int content_type, String tag, int offset);

    @Query("SELECT * FROM `ContentEntity` where contentType = :content_type and isStarred = :filter_starred order by id desc  LIMIT 10 OFFSET :offset")
    List<ContentEntity> getContentByContentTypeWithStarred(int content_type, int offset, boolean filter_starred);

    @Query("SELECT * FROM `ContentEntity` where contentType = :content_type and isStarred = :filter_starred  AND tag like '%' ||  :tag || '%' order by id desc  LIMIT 10 OFFSET :offset")
    List<ContentEntity> getContentByTagWithStarred(int content_type, String tag, int offset, boolean filter_starred);


    @Query("SELECT * FROM `ContentEntity` where tag = :tag")
    ContentEntity getContentByTag(String tag);

    @Query("SELECT * FROM `ContentEntity` where id = :id")
    ContentEntity getContentById(long id);

    @Update
    int updateContentStarred(ContentEntity contentEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertTags(List<SearchEntity> tags);

    @Query("SELECT * FROM `SearchEntity` where tag like '%' ||  :tag || '%'")
    List<SearchEntity> searchTags(String tag);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNotification(NotificationEntity notification);

    @Query("SELECT * FROM `NotificationEntity` ")
    List<NotificationEntity> getNotifications();


}