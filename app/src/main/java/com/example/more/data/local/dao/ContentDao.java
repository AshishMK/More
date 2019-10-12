package com.example.more.data.local.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.more.data.local.entity.ContentEntity;
import com.example.more.data.local.entity.NotificationEntity;
import com.example.more.data.local.entity.SearchEntity;

import java.util.List;

@Dao
public interface ContentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertContents(List<ContentEntity> movies);

    @Query("SELECT * FROM `ContentEntity` where contentType = :content_type order by id desc  LIMIT 10 OFFSET :offset")
    List<ContentEntity> getContentByContentType(int content_type, int offset);

    @Query("SELECT * FROM `ContentEntity` where contentType = :content_type AND tag like '%' ||  :tag || '%' order by id desc  LIMIT 10 OFFSET :offset")
    List<ContentEntity> getContentByTag(int content_type, String tag, int offset);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertTags(List<SearchEntity> tags);

    @Query("SELECT * FROM `SearchEntity` where tag like '%' ||  :tag || '%'")
    List<SearchEntity> searchTags(String tag);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNotification(NotificationEntity notification);

    @Query("SELECT * FROM `NotificationEntity` ")
    List<NotificationEntity> getNotifications();
}