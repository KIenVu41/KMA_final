package com.kma.demo.ui.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kma.demo.data.model.Song;
import com.kma.demo.data.repository.SongRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class SongViewModel extends ViewModel {
    private final SongRepository songRepository;
    private MutableLiveData<List<Song>> mListSongLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Song>> mListLocalSongLiveData = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = null;

    @Inject
    public SongViewModel(SongRepository songRepository) {
        this.songRepository = songRepository;
        compositeDisposable = new CompositeDisposable();
    }

    public void getAllSongs(String name) {
        compositeDisposable.add(songRepository.getAllSongs(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> mListSongLiveData.setValue(list), throwable -> {}));
    }

    public void fetchSongFromLocal(Context context) {
        compositeDisposable.add(songRepository.fetchSongFromLocal(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> mListLocalSongLiveData.setValue(list), throwable -> {}));
    }

    public LiveData<List<Song>> getmListSongLiveData() {
        return mListSongLiveData;
    }

    public LiveData<List<Song>> getmListLocalSongLiveData() {
        return mListLocalSongLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
