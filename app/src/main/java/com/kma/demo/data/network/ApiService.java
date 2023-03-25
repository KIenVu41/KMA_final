package com.kma.demo.data.network;

import com.kma.demo.data.model.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("songs")
    Call<List<Song>> getAllSongs(@Query("name") String name);

    @GET("update")
    Call<Integer> updateCount(@Query("id") String id);
}
