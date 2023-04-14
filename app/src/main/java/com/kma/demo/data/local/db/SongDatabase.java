package com.kma.demo.data.local.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.kma.demo.data.local.entity.SongEntity;

@Database(entities = {SongEntity.class}, version = 1, exportSchema = false)
public abstract class SongDatabase extends RoomDatabase {
    public abstract SongDao songDao();
}
