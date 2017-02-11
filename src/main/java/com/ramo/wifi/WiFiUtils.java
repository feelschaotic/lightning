package com.ramo.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.ramo.utils.L;
import com.ramo.utils.RandomUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramo on 2016/3/26.
 */
public class WiFiUtils {
    private Context context;
    private WifiManager wifiManager;
    private static List<ScanResult> scanResults;
    private boolean isConnected = false;
    public static final String SSID = Build.MODEL + "_" + RandomUtil.getRandomNumber(2);
    public static String CONNECT_SSID;

    private static List<String> passableWiFiList = new ArrayList();

    public WiFiUtils(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    // wifi热点开关
    public Boolean setWifiApEnabled(Boolean enabled) {
        if (!enabled) {
            openWiFi();
            return null;
        }
        closeWiFi();
        //热点的配置类
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfiguration.SSID = getSSID();
        wifiConfiguration.preSharedKey = getSSID();
        try {
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            return (Boolean) method.invoke(wifiManager, wifiConfiguration, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void closeWiFi() {
        //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            //  Toast.makeText(context, "热点启用中，关闭wifi..", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(false);
        }
    }


    public void searchWiFi() {
        openWiFi();
        wifiManager.startScan();
        scanWiFi();
    }

    public void openWiFi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }


    public void scanWiFi() {
        scanResults = wifiManager.getScanResults();
        if (scanResults == null || scanResults.size() == 0 || isConnected)
            return;
        verdictWiFi();
    }

    private void verdictWiFi() {
        passableWiFiList.clear();
        for (ScanResult scanResult : scanResults) {
            //  Log.e("file", "ssid:" + scanResult.SSID);
            if (scanResult.SSID.contains("_")) {
                passableWiFiList.add(scanResult.SSID);
                Log.e("file", "匹配的passableWiFiList:" + scanResult.SSID);
            }
        }
    }

    public boolean connectSpecifiedWiFi(String ssid) {
        synchronized (this) {
            return connectWiFi(ssid);
        }
    }

    public Boolean connectWiFi(String ssid) {
        if (passableWiFiList == null || passableWiFiList.size() <= 0)
            return false;

        WifiConfiguration wifiConfiguration = setWifiParams(ssid);
        int wificonfigID = wifiManager.addNetwork(wifiConfiguration);
        boolean flag = wifiManager.enableNetwork(wificonfigID, true);
        isConnected = true;
        L.e("连接wifi结果：" + flag);
        CONNECT_SSID = ssid;
        return flag;
    }

    public WifiConfiguration setWifiParams(String ssid) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";

        config.preSharedKey = "\"" + ssid + "\"";
        config.hiddenSSID = true;
        config.status = WifiConfiguration.Status.ENABLED;
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        L.e("连接wifi：ssid：" + config.SSID + ";wificonfigID:" + config.preSharedKey);
        return config;

    }

    public List getPassableWiFiList() {
        return passableWiFiList;
    }

    public String getSSID() {
        return SSID;
    }

}
