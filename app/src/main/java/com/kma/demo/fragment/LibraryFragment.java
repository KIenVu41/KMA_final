package com.kma.demo.fragment;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kma.demo.MyApplication;
import com.kma.demo.R;
import com.kma.demo.activity.MainActivity;
import com.kma.demo.activity.PlayMusicActivity;
import com.kma.demo.adapter.SongAdapter;
import com.kma.demo.adapter.SongBaseAdapter;
import com.kma.demo.constant.Constant;
import com.kma.demo.constant.GlobalFuntion;
import com.kma.demo.controller.SongController;
import com.kma.demo.databinding.FragmentAllSongsBinding;
import com.kma.demo.databinding.FragmentLibraryBinding;
import com.kma.demo.model.Song;
import com.kma.demo.service.MusicService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private FragmentLibraryBinding mFragmentLibraryBinding;
    private List<Song> mListSong;
    private SongController songController;
    //private SongAdapter songAdapter;
    private SongBaseAdapter songBaseAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentLibraryBinding = FragmentLibraryBinding.inflate(inflater, container, false);

        songController = new SongController(null);

        getListLibrarySongs();
        initListener();

        return mFragmentLibraryBinding.getRoot();
    }

    private void getListLibrarySongs() {
        if (getActivity() == null) {
            return;
        }

        mListSong = songController.fetchSongFromLocal(MyApplication.get(requireActivity()));
        displayListLibrarySongs();
    }

    private void displayListLibrarySongs() {
        if (getActivity() == null) {
            return;
        }
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        mFragmentLibraryBinding.rcvData.setLayoutManager(linearLayoutManager);
//
//        songAdapter = new SongAdapter(mListSong, this::goToSongDetail, null);
        songBaseAdapter = new SongBaseAdapter(mListSong, this::goToSongDetail, null);
        mFragmentLibraryBinding.rcvData.setAdapter(songBaseAdapter);
    }

    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
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
}