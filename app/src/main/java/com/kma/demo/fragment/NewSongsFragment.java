package com.kma.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentNewSongsBinding = FragmentNewSongsBinding.inflate(inflater, container, false);

        songController = new SongController(this);

        getListNewSongs();
        initListener();

        return mFragmentNewSongsBinding.getRoot();
    }

    private void getListNewSongs() {
        if (getActivity() == null) {
            return;
        }
        songController.fetchAllData("");
//        MyApplication.get(getActivity()).getSongsDatabaseReference().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mListSong = new ArrayList<>();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Song song = dataSnapshot.getValue(Song.class);
//                    if (song == null) {
//                        return;
//                    }
//                    if (song.isLatest()) {
//                        mListSong.add(0, song);
//                    }
//                }
//                displayListNewSongs();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
//            }
//        });
    }

    private void displayListNewSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentNewSongsBinding.rcvData.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(mListSong, this::goToSongDetail);
        mFragmentNewSongsBinding.rcvData.setAdapter(songAdapter);
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
            if (song.isLatest()) {
                mListSong.add(0, song);
            }
        }
        displayListNewSongs();
    }
}
