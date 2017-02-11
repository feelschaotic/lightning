package com.ramo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ramo.wifi.WiFiUtils;

/**
 * 监听热点的变化的广播
 * Created by ramo on 2016/3/30.
 */

public class WiFiReceiver extends BroadcastReceiver {
    private WiFiUtils wiFiUtils;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (wiFiUtils == null)
            wiFiUtils = new WiFiUtils(context);
        wiFiUtils.openWiFi();
        wiFiUtils.searchWiFi();

    }
}
