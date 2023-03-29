package com.kma.demo.ui.fragment;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;


import com.kma.demo.MyApplication;
import com.kma.demo.data.repository.SongRepository;
import com.kma.demo.ui.activity.MainActivity;
import com.kma.demo.ui.activity.PlayMusicActivity;
import com.kma.demo.adapter.BannerSongAdapter;
import com.kma.demo.adapter.SongAdapter;
import com.kma.demo.adapter.SongGridAdapter;
import com.kma.demo.constant.Constant;
import com.kma.demo.constant.GlobalFuntion;
import com.kma.demo.controller.SongController;
import com.kma.demo.databinding.FragmentHomeBinding;
import com.kma.demo.data.model.Song;
import com.kma.demo.data.model.SongDiffUtilCallBack;
import com.kma.demo.service.MusicService;
import com.kma.demo.ui.viewmodel.SongViewModel;
import com.kma.demo.ui.viewmodel.SongViewModelFactory;
import com.kma.demo.utils.StringUtil;
import com.kma.demo.worker.VideoPreloadWorker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private FragmentHomeBinding mFragmentHomeBinding;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private SongViewModel songViewModel;
    private List<Song> mListSong;
    private List<Song> mListNewSong;
    private List<Song> mListPopularSong;
    private List<Song> mListSongBanner;
    private SongAdapter songAdapter;
    private BannerSongAdapter bannerSongAdapter;
    private SongGridAdapter songGridAdapter;
    private String strKey = "";
    private DownloadManager downloadManager;
    private long enqueue = 0;
    private BroadcastReceiver downloadReceiver = null;
    private SongDiffUtilCallBack songDiffUtilCallBack;

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

        songDiffUtilCallBack = new SongDiffUtilCallBack();
        displayListBannerSongs();
        displayListPopularSongs();
        displayListNewSongs();
        songViewModel = new ViewModelProvider(this, viewModelFactory).get(SongViewModel.class);
        songViewModel.getmListSongLiveData().observe(getActivity(), new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
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
                getListBannerSongs();
                getListPopularSongs();
                getListNewSongs();
            }
        });

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
        songViewModel.getAllSongs("");
    }

    private void displayListBannerSongs() {
        bannerSongAdapter = new BannerSongAdapter(songDiffUtilCallBack, this::goToSongDetail);
        mFragmentHomeBinding.viewpager2.setAdapter(bannerSongAdapter);

        mFragmentHomeBinding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandlerBanner.removeCallbacks(mRunnableBanner);
                mHandlerBanner.postDelayed(mRunnableBanner, 3000);
            }
        });
    }

    private void getListBannerSongs() {
        if (mListSongBanner != null) {
            mListSongBanner.clear();
        } else {
            mListSongBanner = new ArrayList<>();
        }
        if (mListSong == null || mListSong.isEmpty()) {
            return;
        }
        for (Song song : mListSong) {
            if (song.isFeatured() && mListSongBanner.size() < Constant.MAX_COUNT_BANNER) {
                mListSongBanner.add(song);
            }
        }
        bannerSongAdapter.submitList(mListSongBanner);
        mFragmentHomeBinding.indicator3.setViewPager(mFragmentHomeBinding.viewpager2);
    }

    private void displayListPopularSongs() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mFragmentHomeBinding.rcvPopularSongs.setLayoutManager(gridLayoutManager);

        songGridAdapter = new SongGridAdapter(songDiffUtilCallBack, this::goToSongDetail);
        mFragmentHomeBinding.rcvPopularSongs.setAdapter(songGridAdapter);
    }

    private void getListPopularSongs() {
        mListPopularSong = new ArrayList<>();
        if (mListSong == null || mListSong.isEmpty()) {
            return;
        }
        List<Song> allSongs = new ArrayList<>(mListSong);
        Collections.sort(allSongs, (song1, song2) -> song2.getCount() - song1.getCount());
        for (Song song : allSongs) {
            if (mListPopularSong.size() < Constant.MAX_COUNT_POPULAR) {
                mListPopularSong.add(song);
            }
        }
        songGridAdapter.submitList(mListPopularSong);
    }

    private void displayListNewSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentHomeBinding.rcvNewSongs.setLayoutManager(linearLayoutManager);

        songAdapter = new SongAdapter(songDiffUtilCallBack, this::goToSongDetail, this::downloadFile);
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

    private void getListNewSongs() {
        mListNewSong = new ArrayList<>();
        if (mListSong == null || mListSong.isEmpty()) {
            return;
        }
        for (Song song : mListSong) {
            if (song.isLatest() && mListNewSong.size() < Constant.MAX_COUNT_LATEST) {
                mListNewSong.add(song);
            }
        }
        songAdapter.submitList(mListNewSong);
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
        schedulePreloadWork(song.getUrl());
//        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
//        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
        startActivity(new Intent(getActivity(), PlayMusicActivity.class).putExtra("AUDIO_URL", song.getUrl()));
    }

    private void schedulePreloadWork(String url) {
        WorkManager workManager = WorkManager.getInstance(MyApplication.get(getActivity()));
        Constraints constraints=new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();
        OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(VideoPreloadWorker.class)
                .setConstraints(constraints)
                .setInputData(new Data.Builder().putString("AUDIO_URL", url).build())
                .build();
        workManager.enqueueUniqueWork("MusicPreloadWorker",
                ExistingWorkPolicy.KEEP, myWorkRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFragmentHomeBinding.imgSearch.setOnClickListener(null);
        mFragmentHomeBinding.layoutViewAllPopular.setOnClickListener(null);
        mFragmentHomeBinding.layoutViewAllNewSongs.setOnClickListener(null);
        songAdapter.setCallback(null);
        bannerSongAdapter.setCallback(null);
        songGridAdapter.setCallback(null);
    }
}
