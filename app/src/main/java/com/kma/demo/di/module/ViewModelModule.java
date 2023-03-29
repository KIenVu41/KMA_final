package com.kma.demo.di.module;

import androidx.lifecycle.ViewModelProvider;

import com.kma.demo.data.network.ApiService;
import com.kma.demo.data.repository.SongRepository;
import com.kma.demo.data.repository.impl.SongRepositoryImpl;
import com.kma.demo.ui.viewmodel.SongViewModel;
import com.kma.demo.ui.viewmodel.SongViewModelFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class ViewModelModule {

    @Provides
    public SongRepository provideSongRepository(ApiService apiService) {
        return new SongRepositoryImpl(apiService);
    }

    @Provides
    public ViewModelProvider.Factory provideSongViewModel(SongRepository repository) {
        return new SongViewModelFactory(repository);
    }
}
