package com.kma.demo.data.local.db;

import androidx.room.Database;

import com.kma.demo.data.local.entity.SongEntity;

@Database(entities = {SongEntity.class}, version = 1)
public abstract class SongDatabase {
    public abstract SongDao songDao();
}
