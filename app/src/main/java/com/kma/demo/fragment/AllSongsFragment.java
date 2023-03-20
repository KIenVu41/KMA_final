package com.kma.demo.fragment;

import android.os.Bundle;
import android.util.Log;
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
import com.kma.demo.databinding.FragmentAllSongsBinding;
import com.kma.demo.model.Song;
import com.kma.demo.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class AllSongsFragment extends Fragment implements SongController.SongCallbackListener {

    private FragmentAllSongsBinding mFragmentAllSongsBinding;
    private List<Song> mListSong;
    private SongController songController;
    private SongAdapter songAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentAllSongsBinding = FragmentAllSongsBinding.inflate(inflater, container, false);

        songController = new SongController(this);

        getListAllSongs();
        initListener();

        return mFragmentAllSongsBinding.getRoot();
    }

    private void getListAllSongs() {
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
//                    mListSong.add(0, song);
//                }
//                displayListAllSongs();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
//            }
//        });
    }

    private void displayListAllSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentAllSongsBinding.rcvData.setLayoutManager(linearLayoutManager);

        songAdapter = new SongAdapter(mListSong, this::goToSongDetail);
        mFragmentAllSongsBinding.rcvData.setAdapter(songAdapter);
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
            mListSong.add(0, song);

        }
//        songAdapter.setData(mListSong);
//        songAdapter.notifyDataSetChanged();
        displayListAllSongs();
    }
}
