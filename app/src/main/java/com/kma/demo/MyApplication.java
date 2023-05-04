package com.kma.demo;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MyApplication extends Application implements Configuration.Provider {
    public static final String CHANNEL_ID = "channel_music_basic_id";
    private static final String CHANNEL_NAME = "channel_music_basic_name";
    public static SimpleCache cache;
    private int cacheSize = 90 * 1024 * 1024;
    private LeastRecentlyUsedCacheEvictor cacheEvictor;
    private ExoDatabaseProvider exoDatabaseProvider;

    @Inject
    HiltWorkerFactory workerFactory;

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cacheEvictor = new LeastRecentlyUsedCacheEvictor(cacheSize);
        exoDatabaseProvider = new ExoDatabaseProvider(this);
        cache = new SimpleCache(getCacheDir(), cacheEvictor, exoDatabaseProvider);
        createChannelNotification();
    }

    private void createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_MIN);
            channel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }
}
