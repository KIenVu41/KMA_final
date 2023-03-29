package com.kma.demo.data.repository.impl;

import com.kma.demo.data.model.Song;
import com.kma.demo.data.network.ApiService;
import com.kma.demo.data.repository.SongRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class SongRepositoryImpl implements SongRepository {

    private ApiService apiService;

    @Inject
    public SongRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    public Observable<List<Song>> getAllSongs(String name) {
        return apiService.getAllSongsRx(name);
    }
}
