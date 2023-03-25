package com.kma.demo.viewmodel;

import androidx.lifecycle.ViewModel;

import com.kma.demo.data.repository.SongRepository;

public class SongViewModel extends ViewModel {
    private final SongRepository songRepository;

    public SongViewModel(SongRepository songRepository) {
        this.songRepository = songRepository;
    }
}
