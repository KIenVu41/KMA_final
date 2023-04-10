package com.kma.demo.di.module;

import com.kma.demo.data.local.cache.Cache;
import com.kma.demo.data.local.cache.impl.FifteenMinuteCache;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class CacheModule {

    @Provides
    @Singleton
    public static Cache<String, Object> provideCache() {
        return new FifteenMinuteCache<>();
    }

}
