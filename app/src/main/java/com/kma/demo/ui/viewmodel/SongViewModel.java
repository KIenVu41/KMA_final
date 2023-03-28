package com.kma.demo.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kma.demo.data.model.Song;
import com.kma.demo.data.repository.SongRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SongViewModel extends ViewModel {
    private final SongRepository songRepository;
    private MutableLiveData<List<Song>> mListSongLiveData = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = null;

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

    public LiveData<List<Song>> getmListSongLiveData() {
        return mListSongLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
