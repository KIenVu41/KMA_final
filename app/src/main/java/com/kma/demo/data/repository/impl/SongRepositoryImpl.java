package com.kma.demo.data.repository.impl;

import android.content.Context;

import com.kma.demo.constant.Constant;
import com.kma.demo.data.local.cache.Cache;
import com.kma.demo.data.model.Song;
import com.kma.demo.data.network.ApiService;
import com.kma.demo.data.repository.SongRepository;
import com.kma.demo.utils.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class SongRepositoryImpl implements SongRepository {

    private ApiService apiService;
    private final Cache<String, Object> cache;

    @Inject
    public SongRepositoryImpl(ApiService apiService, Cache<String, Object> cache) {
        this.apiService = apiService;
        this.cache = cache;
    }

    public Observable<List<Song>> getAllSongs(String name) {
        return apiService.getAllSongsRx(name);
    }

    @Override
    public Observable<List<Song>> fetchSongFromLocal(Context context) {
        List<Song> mListSong = new ArrayList<>();
        File[] files = StorageUtil.getListFiles(Constant.DOWNLOAD_DIR);
        if(files != null) {
            for(int i = 0; i < files.length; i++) {
                if(files[i].exists() && files[i].length() > 0 && StorageUtil.getFileExtension(files[i].getName()).equals("mp3")) {
                    Song song = new Song();
                    song.setDocId("");
                    song.setUrl(files[i].getAbsolutePath());
                    song.setTitle(files[i].getName());
                    song.setLatest(false);
                    song.setFeatured(false);
                    song.setCount(0);
                    song.setArtist("");
                    mListSong.add(song);
                }
            }
        }
        return Observable.fromArray(mListSong);
    }

    @Override
    public Observable<List<Song>> pagination(int page) {
        List<Song> homePaginate = (List<Song>) cache.get(Constant.HOME_CACHE + page);
        if(homePaginate != null && homePaginate.size() > 0) {
            return Observable.just(homePaginate);
        }
        return apiService.pagination(page).doOnNext(songList -> {
            cache.put(Constant.HOME_CACHE + page, songList);
        });
    }

    @Override
    public Observable<List<Song>> featuredPagination(int page) {
        List<Song> featuredPaginate = (List<Song>) cache.get(Constant.FEATURED_CACHE + page);
        if(featuredPaginate != null && featuredPaginate.size() > 0) {
            return Observable.just(featuredPaginate);
        }
        return apiService.featuredPagination(page).doOnNext(songList -> {
            cache.put(Constant.FEATURED_CACHE + page, songList);
        });
    }

    @Override
    public Observable<List<Song>> popularPagination(int page) {
        List<Song> popularPaginate = (List<Song>) cache.get(Constant.POPULAR_CACHE + page);
        if (popularPaginate != null && popularPaginate.size() > 0) {
            return Observable.just(popularPaginate);
        }
        return apiService.popularPagination(page).doOnNext(songList -> {
            cache.put(Constant.POPULAR_CACHE + page, songList);
        });
    }

    @Override
    public Observable<List<Song>> latestPagination(int page) {
        List<Song> latestPaginate = (List<Song>) cache.get(Constant.LATEST_CACHE + page);
        if(latestPaginate != null && latestPaginate.size() > 0) {
            return Observable.just(latestPaginate);
        }
        return apiService.latestPagination(page).doOnNext(songList -> {
            cache.put(Constant.LATEST_CACHE + page, songList);
        });
    }

    @Override
    public Observable<ResponseBody> download(String url, String name) {
        return apiService.download(url, name);
    }

    @Override
    public Observable<List<Song>> getHomeData() {
        List<Song> data = (List<Song>) cache.get(Constant.HOME_CACHE);
        if (data != null) {
            return Observable.just(data);
        }
        Observable<List<Song>> homeData = apiService.getHomeData();
        return homeData.doOnNext(songList -> {
            cache.put(Constant.HOME_CACHE, songList);
        });
    }
}
