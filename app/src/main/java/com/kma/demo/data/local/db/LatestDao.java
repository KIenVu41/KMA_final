package com.kma.demo.data.local.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kma.demo.data.local.entity.FeaturedEntity;
import com.kma.demo.data.local.entity.LatestEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface LatestDao {
    @Query("SELECT * FROM latest WHERE page = :page")
    Observable<List<LatestEntity>> getLatestByPage(int page);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertSongs(List<LatestEntity> songList);

    @Query("DELETE FROM latest WHERE page = :page")
    Completable deleteByPage(int page);
}
