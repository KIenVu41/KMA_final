package com.kma.demo.data.repository;

import android.content.Context;

import com.kma.demo.data.local.entity.SongEntity;
import com.kma.demo.data.model.Song;
import com.kma.demo.data.network.ApiService;
import com.kma.demo.data.network.RetrofitInstance;
import com.kma.demo.utils.Resource;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

public interface SongRepository {

    public Observable<List<Song>> getAllSongs(String name);

    public Observable<List<Song>> fetchSongFromLocal(Context context);

    public Observable<List<Song>> pagination(int page);

    public Observable<List<Song>> featuredPagination(int page);

    public Observable<List<Song>> popularPagination(int page);

    public Observable<List<Song>> latestPagination(int page);

    public Observable<ResponseBody> download(String url, String name);

    // home data
    public Observable<List<Song>> getHomeData();

    public Observable<List<Song>> getSongsByType(int type, int page);

    public Completable insertSongs(List<Song> songList, int page, int type);

    public Completable deleteByType(int type, int page);

    // all data
    public Observable<List<Song>> getAllSongsByPage(int page);

    public Completable insertAllSongs(List<Song> songList, int page, int type);

    public Completable deleteAllByPage(int page);

    // featured data
    public Observable<List<Song>> getFeaturedByPage(int page);

    public Completable insertFeaturedSongs(List<Song> songList, int page, int type);

    public Completable deleteFeaturedByPage(int page);

    // latest data
    public Observable<List<Song>> getLatestByPage(int page);

    public Completable insertLatestSongs(List<Song> songList, int page, int type);

    public Completable deleteLatestByPage(int page);

    // popular data
    public Observable<List<Song>> getPopularByPage(int page);

    public Completable insertPopularSongs(List<Song> songList, int page, int type);

    public Completable deletePopularByPage(int page);

    public void deleteOldRecord(long createAt);

    public void clear();
}
