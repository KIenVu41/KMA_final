package com.kma.demo.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

public class NetworkUtil {

    public static boolean hasConnection(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cmManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cmManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        NetworkInfo mobileNetwork = cmManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }
        NetworkInfo activeNetwork = cmManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    public static int getNetworkSpeed(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return getWifiSpeed(context);
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                return getMobileSpeed(context);
            }
        }

        return -1;
    }

    private static int getWifiSpeed(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getLinkSpeed();
    }

    private static int getMobileSpeed(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return -1;
        }
        return telephonyManager.getNetworkType();
    }

    public static boolean isConnectedFast(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            int type = activeNetwork.getType();
            int subType = activeNetwork.getSubtype();
            if (type == ConnectivityManager.TYPE_WIFI) {
                return true; // Kết nối wifi luôn đủ nhanh
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                switch (subType) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        return false; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        return false; // ~ 14-64 kbps
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return false; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        return true; // ~ 400-1000 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        return true; // ~ 600-1400 kbps
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        return false; // ~ 100 kbps
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        return true; // ~ 2-14 Mbps
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        return true; // ~ 700-1700 kbps
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        return true; // ~ 1-23 Mbps
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        return true; // ~ 400-7000 kbps
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        return true; // ~ 1-2 Mbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        return true; // ~ 5 Mbps
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return true; // ~ 10-20 Mbps
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return false; // ~25 kbps
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return true; // ~ 10+ Mbps
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    default:
                        return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
