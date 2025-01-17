package com.example.more.data.local;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.more.data.local.dao.ContentDao;
import com.example.more.data.local.entity.ContentEntity;
import com.example.more.data.local.entity.NotificationEntity;
import com.example.more.data.local.entity.SearchEntity;
import com.example.more.data.remote.model.VideoEntity;


@Database(entities = {ContentEntity.class, SearchEntity.class, NotificationEntity.class, VideoEntity.class}, version = 2,  exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ContentDao contentDao();
}
