package com.kma.demo.worker;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheWriter;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.kma.demo.MyApplication;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class VideoPreloadWorker extends Worker {
     private HttpDataSource.Factory mHttpDataSourceFactory;
     private DefaultDataSourceFactory mDefaultDataSourceFactory;
     private CacheDataSource mCacheDataSource;
     private SimpleCache cache = MyApplication.cache;
     private Context context;

    public VideoPreloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            String audioUrl = getInputData().getString("AUDIO_URL");
            mHttpDataSourceFactory = new DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true);

            mDefaultDataSourceFactory = new DefaultDataSourceFactory(context, mDefaultDataSourceFactory);

            mCacheDataSource = new CacheDataSource.Factory()
                    .setCache(cache)
                    .setUpstreamDataSourceFactory(mHttpDataSourceFactory)
                    .createDataSource();

            preCacheAudio(audioUrl);

            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }
    }

    private void preCacheAudio(String audioUrl) {
        Uri audioUri = Uri.parse(audioUrl);
        DataSpec dataSpec = new DataSpec(audioUri);

        CacheWriter.ProgressListener progressListener = new CacheWriter.ProgressListener() {
            @Override
            public void onProgress(long requestLength, long bytesCached, long newBytesCached) {
                Double downloadPercentage = (bytesCached * 100.0 / requestLength);
            }
        };

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                cacheAudio(dataSpec, progressListener);
                preCacheAudio(audioUrl);
            }
        });
    }

    private void cacheAudio(DataSpec dataSpec, CacheWriter.ProgressListener mProgressListener) {
        try {
            CacheWriter cacheWriter = new CacheWriter(mCacheDataSource, dataSpec, null, mProgressListener);
            cacheWriter.cache();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
