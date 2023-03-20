package com.kma.demo.network;

import com.kma.demo.model.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;

public interface ApiService {
    @GET("songs")
    Call<List<Song>> getAllSongs();
}
