package com.kma.demo.data.repository.impl;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.kma.demo.constant.Constant;
import com.kma.demo.data.local.cache.Cache;
import com.kma.demo.data.local.db.SongDatabase;
import com.kma.demo.data.local.entity.AllEntity;
import com.kma.demo.data.local.entity.FeaturedEntity;
import com.kma.demo.data.local.entity.LatestEntity;
import com.kma.demo.data.local.entity.PopularEntity;
import com.kma.demo.data.local.entity.SongEntity;
import com.kma.demo.data.mapper.SongMapper;
import com.kma.demo.data.model.Song;
import com.kma.demo.data.network.ApiService;
import com.kma.demo.data.repository.SongRepository;
import com.kma.demo.utils.NetworkUtil;
import com.kma.demo.utils.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class SongRepositoryImpl implements SongRepository {

    private ApiService apiService;
    private final Cache<String, Object> cache;
    private SongDatabase songDatabase;
    private Application application;

    @Inject
    public SongRepositoryImpl(ApiService apiService, Cache<String, Object> cache, SongDatabase songDatabase, Application application) {
        this.apiService = apiService;
        this.songDatabase = songDatabase;
        this.cache = cache;
        this.application = application;
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

    @Override
    public Observable<List<Song>> pagination(int page) {
        List<Song> homePaginate = (List<Song>) cache.get(Constant.ALL_CACHE + page);
        if(homePaginate != null && homePaginate.size() > 0) {
            return Observable.just(homePaginate);
        }
        if(hasInternetConnection()) {
            return apiService.pagination(page).doOnNext(songList -> {
                cache.put(Constant.ALL_CACHE + page, songList);
                handleAllDb(songList, page);
            });
        } else {
            return getAllSongsByPage(page);
        }
    }

    @Override
    public Observable<List<Song>> featuredPagination(int page) {
        List<Song> featuredPaginate = (List<Song>) cache.get(Constant.FEATURED_CACHE + page);
        if(featuredPaginate != null && featuredPaginate.size() > 0) {
            return Observable.just(featuredPaginate);
        }
        if(hasInternetConnection()) {
            return apiService.featuredPagination(page).doOnNext(songList -> {
                cache.put(Constant.FEATURED_CACHE + page, songList);
                handleFeaturedDb(songList, page);
            });
        } else {
            return getFeaturedByPage(page);
        }
    }

    private void handleFeaturedDb(List<Song> songList, int page) {
        deleteFeaturedByPage(page)
                .andThen(insertFeaturedSongs(songList, page, Constant.DB_FEATURED))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorComplete(new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        Log.d("TAG", "featured db f " + throwable.getMessage());
                        return false;
                    }
                })
                .subscribe(() -> {
                    Log.d("TAG", "featured db s");
                }, throwable -> {
                    Log.d("TAG", "featured db f " + throwable.getMessage());
                });
    }

    @Override
    public Observable<List<Song>> popularPagination(int page) {
        List<Song> popularPaginate = (List<Song>) cache.get(Constant.POPULAR_CACHE + page);
        if (popularPaginate != null && popularPaginate.size() > 0) {
            return Observable.just(popularPaginate);
        }
        if(hasInternetConnection()) {
            return apiService.popularPagination(page).doOnNext(songList -> {
                cache.put(Constant.POPULAR_CACHE + page, songList);
                handlePopularDb(songList, page);
            });
        } else {
            return getPopularByPage(page);
        }
    }

    private void handlePopularDb(List<Song> songList, int page) {
        deletePopularByPage(page)
                .andThen(insertPopularSongs(songList, page, Constant.DB_POPULAR))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorComplete(new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        Log.d("TAG", "popular db f " + throwable.getMessage());
                        return false;
                    }
                })
                .subscribe(() -> {
                    Log.d("TAG", "popular db s");
                }, throwable -> {
                    Log.d("TAG", "popular db f " + throwable.getMessage());
                });
    }

    @Override
    public Observable<List<Song>> latestPagination(int page) {
        List<Song> latestPaginate = (List<Song>) cache.get(Constant.LATEST_CACHE + page);
        if(latestPaginate != null && latestPaginate.size() > 0) {
            return Observable.just(latestPaginate);
        }
        if(hasInternetConnection()) {
            return apiService.latestPagination(page).doOnNext(songList -> {
                cache.put(Constant.LATEST_CACHE + page, songList);
                handleLatestDb(songList, page);
            });
        } else {
            return getLatestByPage(page);
        }
    }

    private void handleLatestDb(List<Song> songList, int page) {
        deleteLatestByPage(page)
                .andThen(insertLatestSongs(songList, page, Constant.DB_LATEST))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorComplete(new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        Log.d("TAG", "latest db f " + throwable.getMessage());
                        return false;
                    }
                })
                .subscribe(() -> {
                    Log.d("TAG", "latest db s");
                }, throwable -> {
                    Log.d("TAG", "latest db f " + throwable.getMessage());
                });
    }

    @Override
    public Observable<ResponseBody> download(String url, String name) {
        return apiService.download(url, name);
    }

    @Override
    public Observable<List<Song>> getHomeData() {
        List<Song> data = (List<Song>) cache.get(Constant.HOME_CACHE);
        if (data != null) {
            return Observable.just(data);
        }
        if(hasInternetConnection()) {
            Observable<List<Song>> homeData = apiService.getHomeData();
            return homeData.doOnNext(songList -> {
                cache.put(Constant.HOME_CACHE, songList);
                handleHomeDb(songList);
            });
        } else {
            return getSongsByType(Constant.DB_HOME, 0);
        }
    }

    private void handleHomeDb(List<Song> songList) {
//        Completable deleteAllCompletable =  Completable.fromAction(() -> deleteByType(Constant.DB_HOME, 0));
//        Completable insertSongCompletable =  Completable.fromAction(() -> insertSongs(songList, 0, Constant.DB_HOME));

        deleteByType(Constant.DB_HOME, 0)
                .andThen(insertSongs(songList, 0, Constant.DB_HOME))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorComplete(new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        Log.d("TAG", "home db f " + throwable.getMessage());
                        return false;
                    }
                })
                    .subscribe(() -> {
                        Log.d("TAG", "home db s");
                    }, throwable -> {
                        Log.d("TAG", "home db f " + throwable.getMessage());
                    });
    }

    private void handleAllDb(List<Song> songList, int page) {
        deleteAllByPage(page)
                .andThen(insertAllSongs(songList, page, Constant.DB_ALL))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorComplete(new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        Log.d("TAG", "all db f " + throwable.getMessage());
                        return false;
                    }
                })
                .subscribe(() -> {
                    Log.d("TAG", "all db s");
                }, throwable -> {
                    Log.d("TAG", "all db f " + throwable.getMessage());
                });
    }

    @Override
    public Observable<List<Song>> getSongsByType(int type, int page) {
        return songDatabase.songDao().getSongsByType(type, page)
                .map(entities -> {
                    List<Song> dtos = new ArrayList<>();
                    for (SongEntity entity : entities) {
                        Song dto = SongMapper.getInstance().toDTO(entity);
                        dtos.add(dto);
                    }
                    return dtos;
                });
    }

    @Override
    public Completable insertSongs(List<Song> songList, int page, int type) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            List<SongEntity> songEntities = songList.stream().map(dto -> SongMapper.getInstance().toEntity(dto, page, type)).collect(Collectors.toList());
            return songDatabase.songDao().insertSongs(songEntities);
        }
        return null;
    }

    @Override
    public Completable deleteByType(int type, int page) {
        return songDatabase.songDao().deleteByType();
    }

    @Override
    public Observable<List<Song>> getAllSongsByPage(int page) {
        return songDatabase.allSongDao().getAllSongsByPage(page)
                .map(entities -> {
                    List<Song> dtos = new ArrayList<>();
                    for (AllEntity entity : entities) {
                        Song dto = SongMapper.getInstance().toDTO(entity);
                        dtos.add(dto);
                    }
                    return dtos;
                });
    }

    @Override
    public Completable insertAllSongs(List<Song> songList, int page, int type) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            List<AllEntity> songEntities = songList.stream().map(dto -> SongMapper.getInstance().toAllEntity(dto, page, type)).collect(Collectors.toList());
            return songDatabase.allSongDao().insertSongs(songEntities);
        }
        return null;
    }

    @Override
    public Completable deleteAllByPage(int page) {
        return songDatabase.allSongDao().deleteByPage(page);
    }

    @Override
    public Observable<List<Song>> getFeaturedByPage(int page) {
        return songDatabase.featuredDao().getFeaturedByPage(page)
                .map(entities -> {
                    List<Song> dtos = new ArrayList<>();
                    for (FeaturedEntity entity : entities) {
                        Song dto = SongMapper.getInstance().toDTO(entity);
                        dtos.add(dto);
                    }
                    return dtos;
                });
    }

    @Override
    public Completable insertFeaturedSongs(List<Song> songList, int page, int type) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            List<FeaturedEntity> songEntities = songList.stream().map(dto -> SongMapper.getInstance().toFeaturedEntity(dto, page, type)).collect(Collectors.toList());
            return songDatabase.featuredDao().insertSongs(songEntities);
        }
        return null;
    }

    @Override
    public Completable deleteFeaturedByPage(int page) {
        return songDatabase.featuredDao().deleteByPage(page);
    }

    @Override
    public Observable<List<Song>> getLatestByPage(int page) {
        return songDatabase.latestDao().getLatestByPage(page)
                .map(entities -> {
                    List<Song> dtos = new ArrayList<>();
                    for (LatestEntity entity : entities) {
                        Song dto = SongMapper.getInstance().toDTO(entity);
                        dtos.add(dto);
                    }
                    return dtos;
                });
    }

    @Override
    public Completable insertLatestSongs(List<Song> songList, int page, int type) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            List<LatestEntity> songEntities = songList.stream().map(dto -> SongMapper.getInstance().toLatestEntity(dto, page, type)).collect(Collectors.toList());
            return songDatabase.latestDao().insertSongs(songEntities);
        }
        return null;
    }

    @Override
    public Completable deleteLatestByPage(int page) {
        return songDatabase.latestDao().deleteByPage(page);
    }

    @Override
    public Observable<List<Song>> getPopularByPage(int page) {
        return songDatabase.popularDao().getPopularByPage(page)
                .map(entities -> {
                    List<Song> dtos = new ArrayList<>();
                    for (PopularEntity entity : entities) {
                        Song dto = SongMapper.getInstance().toDTO(entity);
                        dtos.add(dto);
                    }
                    return dtos;
                });
    }

    @Override
    public Completable insertPopularSongs(List<Song> songList, int page, int type) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            List<PopularEntity> songEntities = songList.stream().map(dto -> SongMapper.getInstance().toPopularEntity(dto, page, type)).collect(Collectors.toList());
            return songDatabase.popularDao().insertSongs(songEntities);
        }
        return null;
    }

    @Override
    public Completable deletePopularByPage(int page) {
        return songDatabase.popularDao().deleteByPage(page);
    }

    private boolean hasInternetConnection() {
        return NetworkUtil.hasConnection(application);
    }
}
