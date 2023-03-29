package com.kma.demo.data.repository.impl;

import android.content.Context;

import com.kma.demo.constant.Constant;
import com.kma.demo.data.model.Song;
import com.kma.demo.data.network.ApiService;
import com.kma.demo.data.repository.SongRepository;
import com.kma.demo.utils.StorageUtil;

import java.io.File;
import java.util.ArrayList;
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
}
