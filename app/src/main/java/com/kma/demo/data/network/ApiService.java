package com.kma.demo.data.network;

import com.kma.demo.data.model.Song;
import com.kma.demo.utils.Resource;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface ApiService {

    @GET("songs")
    Observable<List<Song>> getAllSongsRx(@Query("name") String name);

    @GET("pagination/songs")
    Observable<List<Song>> pagination(@Query("page") int page);

    @GET("pagination/featured")
    Observable<List<Song>> featuredPagination(@Query("page") int page);

    @GET("pagination/latest")
    Observable<List<Song>> latestPagination(@Query("page") int page);

    @GET("pagination/popular")
    Observable<List<Song>> popularPagination(@Query("page") int page);

    @GET("home")
    Observable<List<Song>> getHomeData();

    @Streaming
    @GET("download")
    Observable<ResponseBody> download(@Query("url") String url);

    @GET("songs")
    Call<List<Song>> getAllSongs(@Query("name") String name);

    @GET("update")
    Call<Integer> updateCount(@Query("id") String id);
}
