package com.kma.demo.data.local.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.kma.demo.data.local.entity.PopularEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface PopularDao {
    @Query("SELECT * FROM popular WHERE page = :page")
    Observable<List<PopularEntity>> getPopularByPage(int page);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertSongs(List<PopularEntity> songList);

    @Transaction
    @Query("DELETE FROM popular WHERE page = :page")
    Completable deleteByPage(int page);
}
