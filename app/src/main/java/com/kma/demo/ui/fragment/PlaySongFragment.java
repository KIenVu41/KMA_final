package com.kma.demo.ui.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.kma.demo.MyApplication;
import com.kma.demo.R;
import com.kma.demo.constant.Constant;
import com.kma.demo.constant.GlobalFuntion;
import com.kma.demo.databinding.FragmentPlaySongBinding;
import com.kma.demo.data.model.Song;
import com.kma.demo.service.MusicService;
import com.kma.demo.ui.activity.PlayMusicActivity;
import com.kma.demo.utils.AppUtil;
import com.kma.demo.utils.GlideUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NonConstantResourceId")
public class PlaySongFragment extends Fragment implements View.OnClickListener {

    private FragmentPlaySongBinding mFragmentPlaySongBinding;
    private Timer mTimer;
    private int mAction;
    private HttpDataSource.Factory mHttpDataSourceFactory;
    private DefaultDataSourceFactory mDefaultDataSourceFactory;
    private DataSource.Factory mCacheDataSourceFactory;
    private SimpleExoPlayer exoPlayer;
    private MediaSource mediaSource;
    private SimpleCache cache = MyApplication.cache;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAction = intent.getIntExtra(Constant.MUSIC_ACTION, 0);
            handleMusicAction();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentPlaySongBinding = FragmentPlaySongBinding.inflate(inflater, container, false);

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                    new IntentFilter(Constant.CHANGE_LISTENER));
        }
        initControl();
        showInforSong();
        mAction = MusicService.mAction;
        handleMusicAction();

        return mFragmentPlaySongBinding.getRoot();
    }

    private void initControl() {
        String videoUrl = MusicService.mListSongPlaying.get(MusicService.mSongPosition).getUrl();
        if(PlayMusicActivity.isLibrary) {
            exoPlayer = new SimpleExoPlayer.Builder(MyApplication.get(getActivity())).build();
            Uri uri = Uri.parse(videoUrl);
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(MyApplication.get(getActivity()), "exoplayer")).createMediaSource(uri);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
            mFragmentPlaySongBinding.playerView.setPlayer(exoPlayer);
        } else {
            mHttpDataSourceFactory = new DefaultHttpDataSource.Factory()
                    .setAllowCrossProtocolRedirects(true);

            this.mDefaultDataSourceFactory = new DefaultDataSourceFactory(
                    MyApplication.get(getActivity()), mHttpDataSourceFactory
            );

            mCacheDataSourceFactory = new CacheDataSource.Factory()
                    .setCache(cache)
                    .setUpstreamDataSourceFactory(mHttpDataSourceFactory)
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);


            exoPlayer = new SimpleExoPlayer.Builder(MyApplication.get(getActivity()))
                .setMediaSourceFactory(new DefaultMediaSourceFactory(mCacheDataSourceFactory)).build();

            Uri videoUri = Uri.parse(videoUrl);
            MediaItem mediaItem = MediaItem.fromUri(videoUri);
            mediaSource = new ProgressiveMediaSource.Factory(mCacheDataSourceFactory).createMediaSource(mediaItem);
            mFragmentPlaySongBinding.playerView.setPlayer(exoPlayer);
            mFragmentPlaySongBinding.playerView.setControllerHideOnTouch(false);
            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    Player.Listener.super.onIsPlayingChanged(isPlaying);
                    if(isPlaying) {
                        startAnimationPlayMusic();
                    } else {
                        stopAnimationPlayMusic();
                    }
                }
            });
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.seekTo(0, 0);
            exoPlayer.setMediaSource(mediaSource, true);
            exoPlayer.prepare();
        }

    }

    private void showInforSong() {
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
            return;
        }
        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        mFragmentPlaySongBinding.tvSongName.setText(currentSong.getTitle());
        mFragmentPlaySongBinding.tvArtist.setText(currentSong.getArtist());
        GlideUtils.loadUrl(currentSong.getImage(), mFragmentPlaySongBinding.imgSong);
    }

    private void handleMusicAction() {
        if (Constant.CANNEL_NOTIFICATION == mAction) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        }
        mFragmentPlaySongBinding.playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(exoPlayer.isPlaying()) {
                    stopAnimationPlayMusic();
                    Log.d("TAG", "exo stop");
                } else {
                    startAnimationPlayMusic();
                    Log.d("TAG", "exo stop");
                }
            }
        });
        mFragmentPlaySongBinding.playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if (visibility == View.VISIBLE) {
                    // Playback controls are visible, handle click events here
                    mFragmentPlaySongBinding.playerView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle click event here
                            if(exoPlayer.isPlaying()) {
                                stopAnimationPlayMusic();
                            } else {
                                startAnimationPlayMusic();
                            }
                        }
                    });
                } else {
                    // Playback controls are hidden, remove click listener
                    mFragmentPlaySongBinding.playerView.setOnClickListener(null);
                }
            }
        });
    }

    private void startAnimationPlayMusic() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mFragmentPlaySongBinding.imgSong.animate().rotationBy(360).withEndAction(this).setDuration(15000)
                        .setInterpolator(new LinearInterpolator()).start();
            }
        };
        mFragmentPlaySongBinding.imgSong.animate().rotationBy(360).withEndAction(runnable).setDuration(15000)
                .setInterpolator(new LinearInterpolator()).start();
    }

    private void stopAnimationPlayMusic() {
        mFragmentPlaySongBinding.imgSong.animate().cancel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
        }
        if(exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
        stopAnimationPlayMusic();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_previous:
                clickOnPrevButton();
                break;

            case R.id.img_play:
                clickOnPlayButton();
                break;

            case R.id.img_next:
                clickOnNextButton();
                break;

            default:
                break;
        }
    }

    private void clickOnPrevButton() {
        GlobalFuntion.startMusicService(getActivity(), Constant.PREVIOUS, MusicService.mSongPosition);
    }

    private void clickOnNextButton() {
        GlobalFuntion.startMusicService(getActivity(), Constant.NEXT, MusicService.mSongPosition);
    }

    private void clickOnPlayButton() {
        if (MusicService.isPlaying) {
            GlobalFuntion.startMusicService(getActivity(), Constant.PAUSE, MusicService.mSongPosition);
        } else {
            GlobalFuntion.startMusicService(getActivity(), Constant.RESUME, MusicService.mSongPosition);
        }
    }
}
