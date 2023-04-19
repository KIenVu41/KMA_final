package com.kma.demo.data.local.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kma.demo.data.local.entity.AllEntity;
import com.kma.demo.data.local.entity.SongEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface AllSongDao {

    @Query("SELECT * FROM all_song WHERE page = :page")
    Observable<List<AllEntity>> getAllSongsByPage(int page);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertSongs(List<AllEntity> songList);

    @Query("DELETE FROM all_song WHERE page = :page")
    Completable deleteByPage(int page);
}
