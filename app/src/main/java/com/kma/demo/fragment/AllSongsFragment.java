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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.kma.demo.MyApplication;
import com.kma.demo.activity.MainActivity;
import com.kma.demo.activity.PlayMusicActivity;
import com.kma.demo.adapter.PaginationScrollListener;
import com.kma.demo.adapter.SongAdapter;
import com.kma.demo.constant.Constant;
import com.kma.demo.constant.GlobalFuntion;
import com.kma.demo.controller.SongController;
import com.kma.demo.databinding.FragmentAllSongsBinding;
import com.kma.demo.data.model.Song;
import com.kma.demo.data.model.SongDiffUtilCallBack;
import com.kma.demo.service.MusicService;
import com.kma.demo.worker.VideoPreloadWorker;

import java.util.ArrayList;
import java.util.List;

public class AllSongsFragment extends Fragment implements SongController.SongCallbackListener {

    private FragmentAllSongsBinding mFragmentAllSongsBinding;
    private List<Song> mListSong;
    private List<Song> rowsArrayList = new ArrayList<>();
    private SongController songController;
    private SongDiffUtilCallBack songDiffUtilCallBack;
    private SongAdapter songAdapter;
    private DownloadManager downloadManager;
    private long enqueue = 0;
    private BroadcastReceiver downloadReceiver = null;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int totalPage = 5;
    private int currentPage = 1;
    private int visibleItem = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentAllSongsBinding = FragmentAllSongsBinding.inflate(inflater, container, false);

        songController = new SongController(this);
        songDiffUtilCallBack = new SongDiffUtilCallBack();

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

        displayListAllSongs();
        getListAllSongs();
        initListener();

        return mFragmentAllSongsBinding.getRoot();
    }

    private void getListAllSongs() {
        if (getActivity() == null) {
            return;
        }
        songController.fetchAllData("");
    }

    private void displayListAllSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        visibleItem = linearLayoutManager.getItemCount();
        mFragmentAllSongsBinding.rcvData.setLayoutManager(linearLayoutManager);

        songAdapter = new SongAdapter(songDiffUtilCallBack, this::goToSongDetail, this::downloadFile);
        mFragmentAllSongsBinding.rcvData.setAdapter(songAdapter);
//        mFragmentAllSongsBinding.rcvData.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
//            @Override
//            protected void loadMoreItems() {
//                isLoading = true;
//                currentPage++;
//                loadNextPage();
//            }
//
//            @Override
//            public boolean isLastPage() {
//                return isLastPage;
//            }
//
//            @Override
//            public boolean isLoading() {
//                return isLoading;
//            }
//        });
    }

    private void loadNextPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                songAdapter.removeFooterLoading();
                rowsArrayList.addAll(getPageSong());
                songAdapter.submitList(rowsArrayList);
                isLoading = false;
                if(currentPage < totalPage) {
                    songAdapter.addFooterLoading();
                } else {
                    isLastPage = true;
                }
            }
        }, 2000 );
    }

    private List<Song> getPageSong() {
        List<Song> pageSong = new ArrayList<>();
        for(int i = 0; i < currentPage * 10; i++) {
            pageSong.add(mListSong.get(i));
        }
        return pageSong;
    }

    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        schedulePreloadWork(song.getUrl());
        //GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        //GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
        startActivity(new Intent(getActivity(), PlayMusicActivity.class).putExtra("AUDIO_URL", song.getUrl()));
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

    private void initListener() {
        MainActivity activity = (MainActivity) getActivity();
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
    public void onFetchProgress(int mode) {

    }

    @Override
    public void onFetchComplete(List<Song> songs) {
        mListSong = new ArrayList<>();
        for (Song song : songs) {
            if (song == null) {
                return;
            }
            mListSong.add(0, song);

        }

//        if(mListSong.size() % 10 == 0) {
//            totalPage = mListSong.size() % 10;
//        } else {
//            totalPage = (mListSong.size() / 10) + 1;
//        }

        int i = 0;
        while (i < 10) {
            rowsArrayList.add(mListSong.get(i));
            i++;
        }
        songAdapter.submitList(rowsArrayList);

//        if(currentPage < totalPage) {
//            songAdapter.addFooterLoading();
//        } else {
//            isLastPage = true;
//        }
    }

    @Override
    public void onUpdateComplete(int count) {

    }
}
