package com.kma.demo.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kma.demo.data.repository.SongRepository;

public class SongViewModelFactory implements ViewModelProvider.Factory {
    private final SongRepository songRepository;

    public SongViewModelFactory(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SongViewModel.class)) {
            return (T) new SongViewModel(songRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
