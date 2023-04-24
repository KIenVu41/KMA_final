package com.kma.demo.data.local.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

import com.kma.demo.data.local.entity.SongEntity;
import com.kma.demo.data.model.Song;

import java.util.List;

@Dao
public interface SongDao {

    @Query("SELECT * FROM song WHERE type = :type AND page = :page")
    Observable<List<SongEntity>> getSongsByType(int type, int page);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertSongs(List<SongEntity> songList);

    @Transaction
    @Query("DELETE FROM song")
    Completable deleteByType();
}
