package com.kma.demo.ui.viewmodel;

import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kma.demo.data.model.Song;
import com.kma.demo.data.repository.SongRepository;
import com.kma.demo.utils.NetworkUtil;
import com.kma.demo.utils.Resource;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class SongViewModel extends AndroidViewModel {

    private final SongRepository songRepository;
    private MutableLiveData<List<Song>> mListSongLiveData = new MutableLiveData<>();
    private MutableLiveData<Resource> mSongLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Song>> mListLocalSongLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Song>> mListSearchSongLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Song>> mListHomeLiveData = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = null;
    private Application application;
    public int songPage = 1;

//    @Inject
//    public SongViewModel(SongRepository songRepository) {
//        this.songRepository = songRepository;
//        compositeDisposable = new CompositeDisposable();
//    }

    @Inject
    public SongViewModel(@NonNull Application application, SongRepository songRepository) {
        super(application);
        this.songRepository = songRepository;
        this.application = application;
        compositeDisposable = new CompositeDisposable();
    }

    public void getAllSongs(String name) {
        compositeDisposable.add(songRepository.getAllSongs(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> mListSongLiveData.postValue(list), throwable -> {}));
    }

    public void getHomeData() {
        compositeDisposable.add(songRepository.getHomeData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> mListHomeLiveData.postValue(list), throwable -> {}));
    }

    public void pagination() {
        mSongLiveData.postValue(Resource.loading(null));
        if(hasInternetConnection()) {
            compositeDisposable.add(songRepository.pagination(songPage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse, throwable -> {
                        mSongLiveData.postValue(Resource.error(throwable.getMessage(), null));
                    }));
        } else {
            mSongLiveData.postValue(Resource.error("No internet connection",null));
        }
    }

    public void handleResponse(List<Song> songList) {
        songPage++;
        mSongLiveData.postValue(Resource.success(songList));
    }

    public void fetchSongFromLocal(Context context) {
        compositeDisposable.add(songRepository.fetchSongFromLocal(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> mListLocalSongLiveData.postValue(list), throwable -> {}));
    }

    private boolean hasInternetConnection() {
        return NetworkUtil.hasConnection(application);
    }

    public LiveData<List<Song>> getmListSongLiveData() {
        return mListSongLiveData;
    }

    public LiveData<Resource> getResourceLiveData() {
        return mSongLiveData;
    }

    public LiveData<List<Song>> getmListLocalSongLiveData() {
        return mListLocalSongLiveData;
    }

    public LiveData<List<Song>> getmListHomeLiveData() {
        return mListHomeLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
