package com.kma.demo.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.kma.demo.data.local.db.SongDatabase;
import com.kma.demo.data.repository.SongRepository;

import java.util.Calendar;

import javax.inject.Inject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class DeleteOldRecord extends Worker {

    @Inject
    public SongRepository songRepository;

    @AssistedInject
    public DeleteOldRecord(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters params
    ) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        long cutoffTime = calendar.getTimeInMillis();
        if(songRepository != null) {
            songRepository.deleteOldRecord(cutoffTime);
        }
        return Result.success();
    }
}
