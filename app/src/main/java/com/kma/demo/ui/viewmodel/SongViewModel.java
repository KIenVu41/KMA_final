package com.kma.demo.ui.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.kma.demo.constant.Constant;
import com.kma.demo.data.local.entity.SongEntity;
import com.kma.demo.data.model.Song;
import com.kma.demo.data.repository.SongRepository;
import com.kma.demo.utils.NetworkUtil;
import com.kma.demo.utils.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

@HiltViewModel
public class SongViewModel extends AndroidViewModel {

    private final SongRepository songRepository;
    private MutableLiveData<Resource> mSongLiveData = new MutableLiveData<>();
    private MutableLiveData<Resource> mFeaturedLiveData = new MutableLiveData<>();
    private MutableLiveData<Resource> mPopularLiveData = new MutableLiveData<>();
    private MutableLiveData<Resource> mLatestLiveData = new MutableLiveData<>();
    private MutableLiveData<Resource> mDownloadLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Song>> mListLocalSongLiveData = new MutableLiveData<>();
    private MutableLiveData<Resource> mListHomeLiveData = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = null;
    private Application application;
    public int songPage = 1;
    public int featuredPage = 1;
    public int popularPage = 1;
    public int latestPage = 1;

    @Inject
    public SongViewModel(@NonNull Application application, SongRepository songRepository) {
        super(application);
        this.songRepository = songRepository;
        this.application = application;
        compositeDisposable = new CompositeDisposable();
    }

    public void getHomeData() {
        mListHomeLiveData.postValue(Resource.loading(null));
        compositeDisposable.add(songRepository.getHomeData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleHomeResponse, throwable -> {
                    mListHomeLiveData.postValue(Resource.error(throwable.getMessage(), null));
                }));
    }

    public void download(String url, String name) {
        mDownloadLiveData.postValue(Resource.loading(null));
        if(hasInternetConnection()) {
            compositeDisposable.add(songRepository.download(url, name)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleDownloadResponse, throwable -> {
                        mDownloadLiveData.postValue(Resource.error(throwable.getMessage(), null));
                    }));
        } else {
            mDownloadLiveData.postValue(Resource.error("No internet connection",null));
        }
    }

    public void pagination() {
        mSongLiveData.postValue(Resource.loading(null));
        compositeDisposable.add(songRepository.pagination(songPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, throwable -> {
                        mSongLiveData.postValue(Resource.error(throwable.getMessage(), null));
                }));
    }

    public void featuredPagination() {
        mFeaturedLiveData.postValue(Resource.loading(null));
            compositeDisposable.add(songRepository.featuredPagination(featuredPage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleFeaturedResponse, throwable -> {
                        mFeaturedLiveData.postValue(Resource.error(throwable.getMessage(), null));
                    }));
    }

    public void popularPagination() {
        mPopularLiveData.postValue(Resource.loading(null));

            compositeDisposable.add(songRepository.popularPagination(popularPage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handlePopularResponse, throwable -> {
                        mPopularLiveData.postValue(Resource.error(throwable.getMessage(), null));
                    }));
    }

    public void latestPagination() {
        mLatestLiveData.postValue(Resource.loading(null));

            compositeDisposable.add(songRepository.latestPagination(latestPage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleLatestResponse, throwable -> {
                        mLatestLiveData.postValue(Resource.error(throwable.getMessage(), null));
                    }));
    }

    public void handleResponse(List<Song> songList) {
        mSongLiveData.postValue(Resource.success(songList));
        songPage++;
    }

    public void handleHomeResponse(List<Song> songList) {
        mListHomeLiveData.postValue(Resource.success(songList));
    }

    public void handleFeaturedResponse(List<Song> songList) {
        mFeaturedLiveData.postValue(Resource.success(songList));
        featuredPage++;
    }

    public void handlePopularResponse(List<Song> songList) {
        mPopularLiveData.postValue(Resource.success(songList));
        popularPage++;
    }

    public void handleLatestResponse(List<Song> songList) {
        mLatestLiveData.postValue(Resource.success(songList));
        latestPage++;
    }

    public void handleDownloadResponse(ResponseBody responseBody) {
        mDownloadLiveData.postValue(Resource.success(responseBody.byteStream()));
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

    public LiveData<Resource> getResourceLiveData() {
        return mSongLiveData;
    }

    public LiveData<Resource> getPopularLiveData() {
        return mPopularLiveData;
    }

    public LiveData<Resource> getLatestLiveData() {
        return mLatestLiveData;
    }

    public LiveData<Resource> getDownloadLiveData() {
        return mDownloadLiveData;
    }

    public LiveData<List<Song>> getmListLocalSongLiveData() {
        return mListLocalSongLiveData;
    }

    public LiveData<Resource> getmListHomeLiveData() {
        return mListHomeLiveData;
    }

    public LiveData<Resource> getmFeaturedLiveData() {
        return mFeaturedLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        songRepository.clear();
    }
}
