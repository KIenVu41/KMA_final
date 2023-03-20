package com.kma.demo.controller;

import android.util.Log;

import com.kma.demo.model.Song;
import com.kma.demo.network.RetrofitInstance;
import com.kma.demo.network.ApiService;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongController {
    private ApiService apiService;
    private SongCallbackListener songCallbackListener;

    public SongController(SongCallbackListener songCallbackListener) {
        this.songCallbackListener = songCallbackListener;
        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
    }

    public void fetchAllData() {
        songCallbackListener.onFetchProgress(0);
        apiService.getAllSongs().enqueue(new Callback<List<Song>>() {
           @Override
           public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
               if(response.isSuccessful()) {
                   songCallbackListener.onFetchComplete(response.body());
               }
           }

           @Override
           public void onFailure(Call<List<Song>> call, Throwable t) {
                call.cancel();
           }
       });
    }

    public interface SongCallbackListener
    {
        void onFetchProgress(int mode);
        void onFetchComplete(List<Song> songs);
    }
}
