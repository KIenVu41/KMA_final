package com.kma.demo.data.repository;

import android.content.Context;

import com.kma.demo.data.model.Song;
import com.kma.demo.data.network.ApiService;
import com.kma.demo.data.network.RetrofitInstance;
import com.kma.demo.utils.Resource;

import java.util.List;

import javax.inject.Inject;

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

    public Observable<List<Song>> getHomeData();
}
