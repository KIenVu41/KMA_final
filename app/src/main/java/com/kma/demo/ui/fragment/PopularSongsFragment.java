package com.kma.demo.ui.fragment;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.TextView;
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
import com.kma.demo.R;
import com.kma.demo.data.repository.SongRepository;
import com.kma.demo.ui.activity.MainActivity;
import com.kma.demo.ui.activity.PlayMusicActivity;
import com.kma.demo.adapter.SongAdapter;
import com.kma.demo.constant.Constant;
import com.kma.demo.constant.GlobalFuntion;
import com.kma.demo.controller.SongController;
import com.kma.demo.databinding.FragmentPopularSongsBinding;
import com.kma.demo.data.model.Song;
import com.kma.demo.data.model.SongDiffUtilCallBack;
import com.kma.demo.service.MusicService;
import com.kma.demo.ui.viewmodel.SongViewModel;
import com.kma.demo.ui.viewmodel.SongViewModelFactory;
import com.kma.demo.utils.Resource;
import com.kma.demo.utils.StorageUtil;
import com.kma.demo.worker.VideoPreloadWorker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PopularSongsFragment extends Fragment {

    private FragmentPopularSongsBinding mFragmentPopularSongsBinding;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private MainActivity activity;
    private Dialog dialogProgress;
    private SongViewModel songViewModel;
    private List<Song> mListSong = new ArrayList<>();
    private SongAdapter songAdapter;
    private SongDiffUtilCallBack songDiffUtilCallBack;
    private boolean isError = false;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean isScrolling = false;
    private boolean isRefresh = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentPopularSongsBinding = FragmentPopularSongsBinding.inflate(inflater, container, false);

        songDiffUtilCallBack = new SongDiffUtilCallBack();
        displayListPopularSongs();
        songViewModel = new ViewModelProvider(this, viewModelFactory).get(SongViewModel.class);

        songViewModel.getPopularLiveData().observe(getViewLifecycleOwner(), new Observer<Resource>() {
            @Override
            public void onChanged(Resource resource) {
                switch (resource.status) {
                    case SUCCESS:
                        if(resource.data != null) {
                            hideProgressBar();
                            hideErrorMessage();

                            if(isRefresh) {
                                mFragmentPopularSongsBinding.swipeRefreshLayout.setRefreshing(false);
                                isRefresh = false;
                            }

                            mListSong.addAll((List<Song>) resource.data);
                            songAdapter.submitList(mListSong);
                            int totalPages = mListSong.size() / Constant.QUERY_PAGE_SIZE + 2;
                            isLastPage = songViewModel.popularPage == totalPages;
                            if (isLastPage) {
                                mFragmentPopularSongsBinding.rcvData.setPadding(0, 0, 0, 0);
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
                            //hideProgressBar();
                            dimissDialogLoadding();
                        }
                        Constant.isDownloading = false;
                        break;
                    case LOADING:
                        Constant.isDownloading = true;
                        //showProgressBar();
                        if(!activity.isFinishing()) {
                            dialogProgress.show();
                        }
                        break;
                    case ERROR:
                        Constant.isDownloading = false;
                        //hideProgressBar();
                        dimissDialogLoadding();
                        if(resource.message != null) {
                            showErrorMessage(resource.message);
                        }
                }
            }
        });

        getListPopularSongs();
        initListener();
        createDialogLoadding();

        return mFragmentPopularSongsBinding.getRoot();
    }

    private void createDialogLoadding() {
        if (dialogProgress != null) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(activity);
        View dialogViewProgress = inflater.inflate(R.layout.progress_loading, null);
        dialogProgress = new Dialog(activity, R.style.MyDialogTheme);
        dialogProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogProgress.setContentView(dialogViewProgress);
        TextView title = (TextView) dialogViewProgress
                .findViewById(R.id.title_progress);
        title.setText(this.getResources().getString(R.string.TEXT_LOADING));
        dialogProgress.setCanceledOnTouchOutside(true);
        dialogProgress.getWindow().setGravity(Gravity.CENTER);
        dialogProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        dialogProgress.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (dialogProgress != null && dialogProgress.isShowing())
                        dialogProgress.dismiss();
                }
                return true;
            }
        });
    }

    private void dimissDialogLoadding() {
        if (dialogProgress != null && dialogProgress.isShowing()) {
            dialogProgress.dismiss();
        }
    }

    private void hideProgressBar() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!activity.isFinishing()) {
                    mFragmentPopularSongsBinding.paginationProgressBar.setVisibility(View.INVISIBLE);
                    isLoading = false;
                }
            }
        }, 500);
    }

    private void showProgressBar() {
        mFragmentPopularSongsBinding.paginationProgressBar.setVisibility(View.VISIBLE);
        isLoading = true;
    }

    private void hideErrorMessage() {
        mFragmentPopularSongsBinding.itemErrorMessage.cvItemError.setVisibility(View.INVISIBLE);
        isError = false;
    }

    private void showErrorMessage(String message) {
        mFragmentPopularSongsBinding.itemErrorMessage.cvItemError.setVisibility(View.VISIBLE);
        mFragmentPopularSongsBinding.itemErrorMessage.tvErrorMessage.setText(message);
        isError = true;
    }

    private void getListPopularSongs() {
        if (getActivity() == null) {
            return;
        }
        songViewModel.popularPagination();
    }

    private void displayListPopularSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentPopularSongsBinding.rcvData.setLayoutManager(linearLayoutManager);

        songAdapter = new SongAdapter(songDiffUtilCallBack, this::goToSongDetail, this::downloadFile);
        mFragmentPopularSongsBinding.rcvData.setAdapter(songAdapter);
        mFragmentPopularSongsBinding.rcvData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) mFragmentPopularSongsBinding.rcvData.getLayoutManager();
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
                    songViewModel.popularPagination();
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

        mFragmentPopularSongsBinding.itemErrorMessage.btnRetry.setOnClickListener(view -> {
            songViewModel.popularPagination();
        });

        mFragmentPopularSongsBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                songViewModel.popularPage = 1;
                mFragmentPopularSongsBinding.rcvData.setPadding(0, 0, 0, 50);
                mListSong.clear();
                songAdapter.submitList(mListSong);
                songViewModel.popularPagination();
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
        workManager.enqueueUniqueWork("MusicPreloadWorker" + url,
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
//            GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
            GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.getActivityMainBinding().header.layoutPlayAll.setOnClickListener(null);
        songAdapter.setCallback(null);
        dimissDialogLoadding();
        dialogProgress = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
