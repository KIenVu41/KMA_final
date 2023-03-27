package com.kma.demo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AppUtil {

    @SuppressLint("DefaultLocale")
    public static String getTime(int millis) {
        long second = (millis / 1000) % 60;
        long minute = millis / (1000 * 60);
        return String.format("%02d:%02d", minute, second);
    }
}
