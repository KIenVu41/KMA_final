package com.kma.demo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kma.demo.constant.Constant;
import com.kma.demo.constant.GlobalFuntion;

public class MusicReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getExtras().getInt(Constant.MUSIC_ACTION);
        GlobalFuntion.startMusicService(context, action, MusicService.mSongPosition);
    }
}