package com.kma.demo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.kma.demo.R;
import com.kma.demo.utils.NetworkUtil;

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean hasConnectioon = NetworkUtil.hasConnection(context);
        if(!hasConnectioon) {
            Toast.makeText(context, context.getString(R.string.msg_no_internet), Toast.LENGTH_LONG).show();
        }
    }
}
