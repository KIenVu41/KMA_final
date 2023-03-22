package com.kma.demo.fragment;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.kma.demo.constant.Constant;
import com.kma.demo.constant.GlobalFuntion;
import com.kma.demo.controller.SongController;
import com.kma.demo.databinding.FragmentPopularSongsBinding;
import com.kma.demo.model.Song;
import com.kma.demo.service.MusicService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PopularSongsFragment extends Fragment implements SongController.SongCallbackListener {

    private FragmentPopularSongsBinding mFragmentPopularSongsBinding;
    private List<Song> mListSong;
    private SongController songController;
    private DownloadManager downloadManager;
    private long enqueue = 0;
    private BroadcastReceiver downloadReceiver = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentPopularSongsBinding = FragmentPopularSongsBinding.inflate(inflater, container, false);

        songController = new SongController(this);

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

        getListPopularSongs();
        initListener();

        return mFragmentPopularSongsBinding.getRoot();
    }

    private void getListPopularSongs() {
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
//                    if (song.getCount() > 0) {
//                        mListSong.add(song);
//                    }
//                }
//                Collections.sort(mListSong, (song1, song2) -> song2.getCount() - song1.getCount());
//                displayListPopularSongs();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
//            }
//        });
    }

    private void displayListPopularSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentPopularSongsBinding.rcvData.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(mListSong, this::goToSongDetail, this::downloadFile);
        mFragmentPopularSongsBinding.rcvData.setAdapter(songAdapter);
    }

    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
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
            if (song.getCount() > 10) {
                mListSong.add(song);
            }
        }
        Collections.sort(mListSong, (song1, song2) -> song2.getCount() - song1.getCount());
        displayListPopularSongs();
    }

    @Override
    public void onUpdateComplete(int count) {

    }
}
