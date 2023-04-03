package com.kma.demo.data.network;

import java.io.IOException;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class DownloadSpeedInterceptor implements Interceptor {
    private static final int MINIMUM_DOWNLOAD_SPEED = 1024 * 10; // 10KB/s
    private static final int MAXIMUM_DOWNLOAD_SPEED = 1024 * 100; // 100KB/s

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Response originalResponse = chain.proceed(originalRequest);

        // Check if we need to limit the download speed
        if (originalResponse.isSuccessful()) {
            ResponseBody responseBody = originalResponse.body();
            if (responseBody != null) {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire response body.
                Buffer buffer = source.buffer();

                long contentLength = responseBody.contentLength();
                if (contentLength != -1) {
                    long downloadSpeed = calculateDownloadSpeed(originalResponse.receivedResponseAtMillis(), originalResponse.headers().getDate(""), contentLength);
                    if (downloadSpeed < MINIMUM_DOWNLOAD_SPEED) {
                        // Limit the download speed
                        long delayMillis = (contentLength / MINIMUM_DOWNLOAD_SPEED) * 1000 - (System.currentTimeMillis() - originalResponse.receivedResponseAtMillis());
                        if (delayMillis > 0) {
                            try {
                                Thread.sleep(delayMillis);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (downloadSpeed > MAXIMUM_DOWNLOAD_SPEED) {
                        // Increase the download speed
                        long delayMillis = (contentLength / MAXIMUM_DOWNLOAD_SPEED) * 1000 - (System.currentTimeMillis() - originalResponse.receivedResponseAtMillis());
                        if (delayMillis > 0) {
                            try {
                                Thread.sleep(delayMillis);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        return originalResponse;
    }

    private long calculateDownloadSpeed(long responseAtMillis, Date date, long contentLength) {
        long responseTime = System.currentTimeMillis() - responseAtMillis;
        long downloadTime = date.getTime() - responseAtMillis;
        return (contentLength / downloadTime) * 1000;
    }
}