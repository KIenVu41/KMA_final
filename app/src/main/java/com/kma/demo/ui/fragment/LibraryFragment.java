package com.kma.demo.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kma.demo.MyApplication;
import com.kma.demo.ui.activity.MainActivity;
import com.kma.demo.ui.activity.PlayMusicActivity;
import com.kma.demo.adapter.SongAdapter;
import com.kma.demo.constant.Constant;
import com.kma.demo.constant.GlobalFuntion;
import com.kma.demo.controller.SongController;
import com.kma.demo.databinding.FragmentLibraryBinding;
import com.kma.demo.data.model.Song;
import com.kma.demo.data.model.SongDiffUtilCallBack;
import com.kma.demo.service.MusicService;
import com.kma.demo.ui.viewmodel.SongViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LibraryFragment extends Fragment {

    private FragmentLibraryBinding mFragmentLibraryBinding;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private SongViewModel songViewModel;
    private List<Song> mListSong;
    private SongDiffUtilCallBack songDiffUtilCallBack;
    private SongAdapter songAdapter;
    private MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentLibraryBinding = FragmentLibraryBinding.inflate(inflater, container, false);

        songDiffUtilCallBack = new SongDiffUtilCallBack();
        songViewModel = new ViewModelProvider(this, viewModelFactory).get(SongViewModel.class);
        displayListLibrarySongs();
        songViewModel.getmListLocalSongLiveData().observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                songAdapter.submitList(songs);
            }
        });
        getListLibrarySongs();
        initListener();

        return mFragmentLibraryBinding.getRoot();
    }

    private void getListLibrarySongs() {
        if (getActivity() == null) {
            return;
        }

        songViewModel.fetchSongFromLocal(MyApplication.get(requireActivity()));
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
//        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
//        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
        startActivity(new Intent(getActivity(), PlayMusicActivity.class).putExtra("IS_LIBRARY", true));
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.getActivityMainBinding().header.layoutPlayAll.setOnClickListener(null);
        songAdapter.setCallback(null);
    }
}