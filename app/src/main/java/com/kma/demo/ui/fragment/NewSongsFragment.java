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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import com.kma.demo.adapter.SongAdapter;
import com.kma.demo.constant.Constant;
import com.kma.demo.constant.GlobalFuntion;
import com.kma.demo.controller.SongController;
import com.kma.demo.databinding.FragmentNewSongsBinding;
import com.kma.demo.data.model.Song;
import com.kma.demo.data.model.SongDiffUtilCallBack;
import com.kma.demo.service.MusicService;
import com.kma.demo.ui.viewmodel.SongViewModel;
import com.kma.demo.ui.viewmodel.SongViewModelFactory;
import com.kma.demo.utils.Resource;
import com.kma.demo.utils.StorageUtil;
import com.kma.demo.utils.StringUtil;
import com.kma.demo.worker.VideoPreloadWorker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NewSongsFragment extends Fragment {

    private FragmentNewSongsBinding mFragmentNewSongsBinding;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private SongViewModel songViewModel;
    private List<Song> mListSong = new ArrayList<>();
    private SongAdapter songAdapter;
    private SongDiffUtilCallBack songDiffUtilCallBack;
    private MainActivity activity;
    private boolean isError = false;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean isScrolling = false;
    private boolean isRefresh = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentNewSongsBinding = FragmentNewSongsBinding.inflate(inflater, container, false);

        songDiffUtilCallBack = new SongDiffUtilCallBack();
        displayListNewSongs();
        songViewModel = new ViewModelProvider(this, viewModelFactory).get(SongViewModel.class);

        songViewModel.getLatestLiveData().observe(getViewLifecycleOwner(), new Observer<Resource>() {
            @Override
            public void onChanged(Resource resource) {
                switch (resource.status) {
                    case SUCCESS:
                        if(resource.data != null) {
                            hideProgressBar();
                            hideErrorMessage();

                            if(isRefresh) {
                                mFragmentNewSongsBinding.swipeRefreshLayout.setRefreshing(false);
                                isRefresh = false;
                            }

                            mListSong.addAll((List<Song>) resource.data);
                            songAdapter.submitList(mListSong);
                            int totalPages = mListSong.size() / Constant.QUERY_PAGE_SIZE + 2;
                            isLastPage = songViewModel.latestPage == totalPages;
                            if (isLastPage) {
                                mFragmentNewSongsBinding.rcvData.setPadding(0, 0, 0, 0);
                            }
                        }
                        break;
                    case LOADING:
                        showProgressBar();
                        break;
                    case ERROR:
                        hideProgressBar();
                        if(resource.message != null) {
                            showErrorMessage(resource.message);
                        }
                }
            }
        });
        songViewModel.getDownloadLiveData().observe(getViewLifecycleOwner(), new Observer<Resource>() {
            @Override
            public void onChanged(Resource resource) {
                switch (resource.status) {
                    case SUCCESS:
                        if(resource.data != null) {
                            try {
                                StorageUtil.convertInputStreamToMp3File((InputStream) resource.data, Constant.songDownloadName + ".mp3");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            hideErrorMessage();
                            hideProgressBar();
                        }
                        Constant.isDownloading = false;
                        break;
                    case LOADING:
                        Constant.isDownloading = true;
                        showProgressBar();
                        break;
                    case ERROR:
                        Constant.isDownloading = false;
                        hideProgressBar();
                        if(resource.message != null) {
                            showErrorMessage(resource.message);
                        }
                }
            }
        });

        getListNewSongs();
        initListener();

        return mFragmentNewSongsBinding.getRoot();
    }

    private void getListNewSongs() {
        if (getActivity() == null) {
            return;
        }
        songViewModel.latestPagination();
    }

    private void displayListNewSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentNewSongsBinding.rcvData.setLayoutManager(linearLayoutManager);

        songAdapter = new SongAdapter(songDiffUtilCallBack, this::goToSongDetail, this::downloadFile);
        mFragmentNewSongsBinding.rcvData.setAdapter(songAdapter);
        mFragmentNewSongsBinding.rcvData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) mFragmentNewSongsBinding.rcvData.getLayoutManager();
                int firstVisibleItemPosition = Objects.requireNonNull(layoutManager).findFirstVisibleItemPosition();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();

                boolean isNoErrors = !isError;
                boolean isNotLoadingAndNotLastPage = !isLoading && !isLastPage;
                boolean isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount;
                boolean isNotAtBeginning = firstVisibleItemPosition >= 0;
                boolean isTotalMoreThanVisible = totalItemCount >= Constant.QUERY_PAGE_SIZE;
                boolean shouldPaginate = isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                        isTotalMoreThanVisible && isScrolling;
                if(shouldPaginate) {
                    songViewModel.latestPagination();
                    isScrolling = false;
                }

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }
        });

        mFragmentNewSongsBinding.itemErrorMessage.btnRetry.setOnClickListener(view -> {
            songViewModel.latestPagination();
        });

        mFragmentNewSongsBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                songViewModel.latestPage = 1;
                mFragmentNewSongsBinding.rcvData.setPadding(0, 0, 0, 50);
                mListSong.clear();
                songAdapter.submitList(mListSong);
                songViewModel.latestPagination();
            }
        });
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

    private void hideProgressBar() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!activity.isFinishing()) {
                    mFragmentNewSongsBinding.paginationProgressBar.setVisibility(View.INVISIBLE);
                    isLoading = false;
                }
            }
        }, 500);
    }

    private void showProgressBar() {
        mFragmentNewSongsBinding.paginationProgressBar.setVisibility(View.VISIBLE);
        isLoading = true;
    }

    private void hideErrorMessage() {
        mFragmentNewSongsBinding.itemErrorMessage.cvItemError.setVisibility(View.INVISIBLE);
        isError = false;
    }

    private void showErrorMessage(String message) {
        mFragmentNewSongsBinding.itemErrorMessage.cvItemError.setVisibility(View.VISIBLE);
        mFragmentNewSongsBinding.itemErrorMessage.tvErrorMessage.setText(message);
        isError = true;
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

    private void downloadFile(@NonNull Song song) {
        if(Constant.isDownloading) {
            return;
        }
        Constant.songDownloadName = song.getTitle();
        songViewModel.download(song.getUrl(), song.getTitle());
    }

    private void initListener() {
        activity = (MainActivity) getActivity();
        if (activity == null || activity.getActivityMainBinding() == null) {
            return;
        }
        activity.getActivityMainBinding().header.layoutPlayAll.setOnClickListener(v -> {
            MusicService.clearListSongPlaying();
            MusicService.mListSongPlaying.addAll(mListSong);
            MusicService.isPlaying = false;
            GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
            GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.getActivityMainBinding().header.layoutPlayAll.setOnClickListener(null);
        songAdapter.setCallback(null);
    }
}
