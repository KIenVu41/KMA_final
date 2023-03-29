package com.kma.demo.data.repository;

import com.kma.demo.data.model.Song;
import com.kma.demo.data.network.ApiService;
import com.kma.demo.data.network.RetrofitInstance;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public interface SongRepository {

    public Observable<List<Song>> getAllSongs(String name);
}
