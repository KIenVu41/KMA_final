package com.kma.demo.controller;

import android.content.Context;

import com.kma.demo.constant.Constant;
import com.kma.demo.data.model.Song;
import com.kma.demo.data.network.RetrofitInstance;
import com.kma.demo.data.network.ApiService;
import com.kma.demo.utils.StorageUtil;

import java.io.File;
import java.util.ArrayList;
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

    public void fetchAllData(String name) {
        songCallbackListener.onFetchProgress(0);
        apiService.getAllSongs(name).enqueue(new Callback<List<Song>>() {
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

    public void updateCount(String id) {
        songCallbackListener.onFetchProgress(0);
        apiService.updateCount(id).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful() && response.body() != null) {
                    songCallbackListener.onUpdateComplete(response.body());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }

    public List<Song> fetchSongFromLocal(Context context) {
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
        return mListSong;
    }

    public interface SongCallbackListener
    {
        void onFetchProgress(int mode);
        void onFetchComplete(List<Song> songs);
        void onUpdateComplete(int count);
    }
}
