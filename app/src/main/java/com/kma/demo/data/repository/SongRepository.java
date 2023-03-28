package com.kma.demo.data.repository;

import com.kma.demo.data.model.Song;
import com.kma.demo.data.network.ApiService;
import com.kma.demo.data.network.RetrofitInstance;

import java.util.List;

import io.reactivex.Observable;

public class SongRepository {
    private ApiService apiService;

    public SongRepository() {
        this.apiService = RetrofitInstance.getRetrofitInstance().create(ApiService.class);
    }

    public Observable<List<Song>> getAllSongs(String name) {
        return apiService.getAllSongsRx(name);
    }
}
