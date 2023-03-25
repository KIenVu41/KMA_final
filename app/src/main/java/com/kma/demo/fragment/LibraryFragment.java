package com.kma.demo.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kma.demo.MyApplication;
import com.kma.demo.activity.MainActivity;
import com.kma.demo.activity.PlayMusicActivity;
import com.kma.demo.adapter.SongAdapter;
import com.kma.demo.constant.Constant;
import com.kma.demo.constant.GlobalFuntion;
import com.kma.demo.controller.SongController;
import com.kma.demo.databinding.FragmentLibraryBinding;
import com.kma.demo.data.model.Song;
import com.kma.demo.data.model.SongDiffUtilCallBack;
import com.kma.demo.service.MusicService;

import java.util.List;

public class LibraryFragment extends Fragment {

    private FragmentLibraryBinding mFragmentLibraryBinding;
    private List<Song> mListSong;
    private SongController songController;
    private SongDiffUtilCallBack songDiffUtilCallBack;
    private SongAdapter songAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentLibraryBinding = FragmentLibraryBinding.inflate(inflater, container, false);

        songController = new SongController(null);
        songDiffUtilCallBack = new SongDiffUtilCallBack();

        displayListLibrarySongs();
        getListLibrarySongs();
        initListener();

        return mFragmentLibraryBinding.getRoot();
    }

    private void getListLibrarySongs() {
        if (getActivity() == null) {
            return;
        }

        mListSong = songController.fetchSongFromLocal(MyApplication.get(requireActivity()));
        songAdapter.submitList(mListSong);
    }

    private void displayListLibrarySongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentLibraryBinding.rcvData.setLayoutManager(linearLayoutManager);

        songAdapter = new SongAdapter(songDiffUtilCallBack, this::goToSongDetail, null);
        mFragmentLibraryBinding.rcvData.setAdapter(songAdapter);
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