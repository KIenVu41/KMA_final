package com.kma.demo.data.local.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.kma.demo.data.local.entity.AllEntity;
import com.kma.demo.data.local.entity.FeaturedEntity;
import com.kma.demo.data.local.entity.LatestEntity;
import com.kma.demo.data.local.entity.PopularEntity;
import com.kma.demo.data.local.entity.SongEntity;

@Database(entities = {SongEntity.class, AllEntity.class, FeaturedEntity.class, LatestEntity.class, PopularEntity.class}, version = 1, exportSchema = false)
public abstract class SongDatabase extends RoomDatabase {
    public abstract SongDao songDao();
    public abstract AllSongDao allSongDao();
    public abstract FeaturedDao featuredDao();
    public abstract LatestDao latestDao();
    public abstract PopularDao popularDao();
}
