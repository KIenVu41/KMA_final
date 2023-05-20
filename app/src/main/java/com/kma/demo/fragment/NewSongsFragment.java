package com.kma.demo.fragment;

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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.kma.demo.MyApplication;
import com.kma.demo.R;
import com.kma.demo.activity.MainActivity;
import com.kma.demo.activity.PlayMusicActivity;
import com.kma.demo.adapter.SongAdapter;
import com.kma.demo.adapter.SongBaseAdapter;
import com.kma.demo.constant.Constant;
import com.kma.demo.constant.GlobalFuntion;
import com.kma.demo.controller.SongController;
import com.kma.demo.databinding.FragmentNewSongsBinding;
import com.kma.demo.model.Song;
import com.kma.demo.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class NewSongsFragment extends Fragment implements SongController.SongCallbackListener {

    private FragmentNewSongsBinding mFragmentNewSongsBinding;
    private List<Song> mListSong;
    private SongController songController;
    private MainActivity activity;
    private Dialog dialogProgress;
    private SongBaseAdapter songBaseAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentNewSongsBinding = FragmentNewSongsBinding.inflate(inflater, container, false);

        songController = new SongController(this);

        getListNewSongs();
        initListener();
        createDialogLoadding();

        return mFragmentNewSongsBinding.getRoot();
    }

    private void getListNewSongs() {
        if (getActivity() == null) {
            return;
        }
        songController.fetchAllData("");
    }

    private void createDialogLoadding() {
        if (dialogProgress != null) {
            return;
        }
        if(activity == null || !isAdded()) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(activity);
        View dialogViewProgress = inflater.inflate(R.layout.progress_loading, null);
        dialogProgress = new Dialog(activity, R.style.MyDialogTheme);
        dialogProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogProgress.setContentView(dialogViewProgress);
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

    private void displayListNewSongs() {
        if (getActivity() == null) {
            return;
        }
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        mFragmentNewSongsBinding.rcvData.setLayoutManager(linearLayoutManager);
//
//        SongAdapter songAdapter = new SongAdapter(mListSong, this::goToSongDetail, this::downloadFile);
        songBaseAdapter = new SongBaseAdapter(mListSong, this::goToSongDetail, this::downloadFile);
        mFragmentNewSongsBinding.rcvData.setAdapter(songBaseAdapter);
    }

    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }

    private void downloadFile(@NonNull Song song) {
        songController.download(song.getUrl(), song.getTitle());
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

    @Override
    public void onFetchProgress(int mode) {
        if(mode == 0) {
            if(!activity.isFinishing()) {
                dialogProgress.show();
            }
        } else {
            dimissDialogLoadding();
        }
    }

    @Override
    public void onFetchComplete(List<Song> songs) {
        mListSong = new ArrayList<>();
        for (Song song : songs) {
            if (song == null) {
                return;
            }
            if (song.isLatest()) {
                mListSong.add(0, song);
            }
        }
        displayListNewSongs();
    }

    @Override
    public void onUpdateComplete(int count) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dimissDialogLoadding();
        dialogProgress = null;
    }
}
