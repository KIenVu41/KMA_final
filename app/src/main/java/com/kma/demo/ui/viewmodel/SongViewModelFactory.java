package com.kma.demo.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kma.demo.data.repository.SongRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SongViewModelFactory implements ViewModelProvider.Factory {
    private final SongRepository songRepository;
    private final Application application;
    @Inject
    public SongViewModelFactory(SongRepository songRepository, Application application) {
        this.songRepository = songRepository;
        this.application = application;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SongViewModel.class)) {
            return (T) new SongViewModel(application, songRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
