package com.kma.demo.data.local.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.kma.demo.data.local.entity.FeaturedEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface FeaturedDao {
    @Query("SELECT * FROM featured WHERE page = :page")
    Observable<List<FeaturedEntity>> getFeaturedByPage(int page);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertSongs(List<FeaturedEntity> songList);

    @Query("DELETE FROM featured WHERE page = :page")
    Completable deleteByPage(int page);
}
