package com.kma.demo.di.module;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import android.app.Application;

import com.kma.demo.constant.Constant;
import com.kma.demo.data.network.ApiService;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public static HttpLoggingInterceptor provideLoggingInterceptor(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    @Provides
    @Singleton
    public static ConnectionPool provideConnectionPool() {
        ConnectionPool connectionPool = new ConnectionPool(5, 5, TimeUnit.MINUTES);
        return connectionPool;
    }

    @Singleton
    @Provides
    public static OkHttpClient provideOkHttpClient(HttpLoggingInterceptor interceptor, ConnectionPool connectionPool) {
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(5, MINUTES)
                .connectionPool(connectionPool)
                .readTimeout(5, MINUTES)
                .writeTimeout(5, MINUTES)
                .retryOnConnectionFailure(true)
                .protocols(Arrays.asList(Protocol.HTTP_1_1))
                .build();
    }

    @Singleton
    @Provides
    public static Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder().baseUrl(Constant.BASE_URL).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(okHttpClient).build();
    }

    @Singleton
    @Provides
    public static ApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }
}
