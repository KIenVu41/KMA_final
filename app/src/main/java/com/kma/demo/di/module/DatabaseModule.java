package com.kma.demo.di.module;

import android.app.Application;
import android.content.Context;
import androidx.room.Room;

import com.kma.demo.data.local.db.SongDao;
import com.kma.demo.data.local.db.SongDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

//    @Provides
//    @Singleton
//    public static Context provideContext(Application application) {
//        return application.getApplicationContext();
//    }

    @Provides
    public static SongDatabase provideSongDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, SongDatabase.class, "songs").build();
    }

//    @Provides
//    public static SongDao provideUserDao(SongDatabase appDatabase) {
//        return appDatabase.songDao();
//    }
}
