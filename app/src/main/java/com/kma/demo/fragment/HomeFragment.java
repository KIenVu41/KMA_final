package com.kma.demo.fragment;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.kma.demo.MyApplication;
import com.kma.demo.R;
import com.kma.demo.activity.MainActivity;
import com.kma.demo.activity.PlayMusicActivity;
import com.kma.demo.adapter.BannerSongAdapter;
import com.kma.demo.adapter.SongAdapter;
import com.kma.demo.adapter.SongGridAdapter;
import com.kma.demo.constant.Constant;
import com.kma.demo.constant.GlobalFuntion;
import com.kma.demo.controller.SongController;
import com.kma.demo.databinding.FragmentHomeBinding;
import com.kma.demo.model.Song;
import com.kma.demo.service.MusicService;
import com.kma.demo.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements SongController.SongCallbackListener {

    private FragmentHomeBinding mFragmentHomeBinding;

    private List<Song> mListSong;
    private List<Song> mListSongBanner;
    private SongController songController;
    private String strKey = "";
    private DownloadManager downloadManager;
    private long enqueue = 0;
    private BroadcastReceiver downloadReceiver = null;

    private final Handler mHandlerBanner = new Handler();
    private final Runnable mRunnableBanner = new Runnable() {
        @Override
        public void run() {
            if (mListSongBanner == null || mListSongBanner.isEmpty()) {
                return;
            }
            if (mFragmentHomeBinding.viewpager2.getCurrentItem() == mListSongBanner.size() - 1) {
                mFragmentHomeBinding.viewpager2.setCurrentItem(0);
                return;
            }
            mFragmentHomeBinding.viewpager2.setCurrentItem(mFragmentHomeBinding.viewpager2.getCurrentItem() + 1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);

        songController = new SongController(this);

        if(downloadReceiver == null) {
            downloadReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                        if(isAdded()) {
                            Toast.makeText(requireActivity(), "Download successfully", Toast.LENGTH_LONG).show();
                        }
                    }
                    requireActivity().unregisterReceiver(this);
                }
            };

            requireActivity().registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        getListSongFromServer("");
        initListener();

        return mFragmentHomeBinding.getRoot();
    }

    private void initListener() {
        mFragmentHomeBinding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    if (mListSong != null) mListSong.clear();
                    //getListSongFromFirebase("");
                }
            }
        });

        mFragmentHomeBinding.imgSearch.setOnClickListener(view -> searchSong());

        mFragmentHomeBinding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchSong();
                return true;
            }
            return false;
        });

        mFragmentHomeBinding.layoutViewAllPopular.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openPopularSongsScreen();
            }
        });

        mFragmentHomeBinding.layoutViewAllNewSongs.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openNewSongsScreen();
            }
        });
    }

    private void getListSongFromServer(String key) {
        if (getActivity() == null) {
            return;
        }
        songController.fetchAllData(key);
    }

    private void displayListBannerSongs() {
        BannerSongAdapter bannerSongAdapter = new BannerSongAdapter(getListBannerSongs(), this::goToSongDetail);
        mFragmentHomeBinding.viewpager2.setAdapter(bannerSongAdapter);
        mFragmentHomeBinding.indicator3.setViewPager(mFragmentHomeBinding.viewpager2);

        mFragmentHomeBinding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandlerBanner.removeCallbacks(mRunnableBanner);
                mHandlerBanner.postDelayed(mRunnableBanner, 3000);
            }
        });
    }

    private List<Song> getListBannerSongs() {
        if (mListSongBanner != null) {
            mListSongBanner.clear();
        } else {
            mListSongBanner = new ArrayList<>();
        }
        if (mListSong == null || mListSong.isEmpty()) {
            return mListSongBanner;
        }
        for (Song song : mListSong) {
            if (song.isFeatured() && mListSongBanner.size() < Constant.MAX_COUNT_BANNER) {
                mListSongBanner.add(song);
            }
        }
        return mListSongBanner;
    }

    private void displayListPopularSongs() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mFragmentHomeBinding.rcvPopularSongs.setLayoutManager(gridLayoutManager);

        SongGridAdapter songGridAdapter = new SongGridAdapter(getListPopularSongs(), this::goToSongDetail);
        mFragmentHomeBinding.rcvPopularSongs.setAdapter(songGridAdapter);
    }

    private List<Song> getListPopularSongs() {
        List<Song> list = new ArrayList<>();
        if (mListSong == null || mListSong.isEmpty()) {
            return list;
        }
        List<Song> allSongs = new ArrayList<>(mListSong);
        Collections.sort(allSongs, (song1, song2) -> song2.getCount() - song1.getCount());
        for (Song song : allSongs) {
            if (list.size() < Constant.MAX_COUNT_POPULAR) {
                list.add(song);
            }
        }
        return list;
    }

    private void displayListNewSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentHomeBinding.rcvNewSongs.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(getListNewSongs(), this::goToSongDetail, this::downloadFile);
        mFragmentHomeBinding.rcvNewSongs.setAdapter(songAdapter);
    }

    private void downloadFile(@NonNull Song song) {
        downloadManager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(song.getUrl()));

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                .setTitle(song.getTitle() + ".mp3")
                .setDescription(song.getTitle() + "-" + song.getArtist())
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, song.getTitle() + ".mp3")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        enqueue = downloadManager.enqueue(request);

        Intent i = new Intent();
        i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(i);
    }

    private List<Song> getListNewSongs() {
        List<Song> list = new ArrayList<>();
        if (mListSong == null || mListSong.isEmpty()) {
            return list;
        }
        for (Song song : mListSong) {
            if (song.isLatest() && list.size() < Constant.MAX_COUNT_LATEST) {
                list.add(song);
            }
        }
        return list;
    }

    private void searchSong() {
        strKey = mFragmentHomeBinding.edtSearchName.getText().toString().trim();
        if (mListSong != null) mListSong.clear();
        getListSongFromServer(strKey);
        GlobalFuntion.hideSoftKeyboard(getActivity());
    }

    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }

    @Override
    public void onFetchProgress(int mode) {

    }

    @Override
    public void onFetchComplete(List<Song> songs) {
        mFragmentHomeBinding.layoutContent.setVisibility(View.VISIBLE);
        mListSong = new ArrayList<>();
        for (Song song : songs) {
            if (song == null) {
                return;
            }

            if (StringUtil.isEmpty(strKey)) {
                mListSong.add(0, song);
            } else {
                if (GlobalFuntion.getTextSearch(song.getTitle()).toLowerCase().trim()
                        .contains(GlobalFuntion.getTextSearch(strKey).toLowerCase().trim())) {
                    mListSong.add(0, song);
                }
            }
        }
        displayListBannerSongs();
        displayListPopularSongs();
        displayListNewSongs();
    }

    @Override
    public void onUpdateComplete(int count) {

    }
}
