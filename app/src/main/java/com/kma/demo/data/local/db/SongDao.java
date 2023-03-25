package com.kma.demo.data.local.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

import com.kma.demo.data.model.Song;

import java.util.List;

@Dao
public interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUsers(List<Song> songList);

    @Update
    public void  updateUsers(List<Song> songList);

    @Delete
    public void deleteUsers(List<Song> songs);
}
