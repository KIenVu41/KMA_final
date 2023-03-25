package com.kma.demo.data.network;

import static java.util.concurrent.TimeUnit.*;
import com.kma.demo.constant.Constant;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit retrofit = null;
    private static OkHttpClient.Builder httpClientBuilder = null;

    public static synchronized Retrofit getRetrofitInstance() {
        if(retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            httpClientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(1, MINUTES)
                    .readTimeout(30, SECONDS);
//                    .writeTimeout(15, SECONDS);

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClientBuilder.build())
                    .build();
        }
        return retrofit;
    }

    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
