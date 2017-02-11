package com.ramo.service;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ramo.application.MyApplication;
import com.ramo.utils.L;
import com.ramo.wifi.lan.BroadcastReceiver;
import com.ramo.wifi.lan.BroadcastSender;
import com.ramo.wifi.lan.OnSearchListener;
import com.ramo.wifi.nio.ClipboardClient;
import com.ramo.wifi.nio.ClipboardServer;

/**
 * 后台监听剪贴板的变化
 * Created by ramo on 2016/7/27.
 */
public class ClipboardService extends Service {
    String connect_ip="192.168.0.0";
    int num=10;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ClipboardServer clipboardServer = new ClipboardServer();
        clipboardServer.init();

        BroadcastSender.lanchApp();
        BroadcastReceiver.lanchApp();
        BroadcastReceiver.setOnSearchListener(new OnSearchListener() {
            @Override
            public void onSuess(String ip, String host) {
                L.e("剪贴板搜索到ip:"+host+"/"+ip);
                connect_ip=ip;
                num--;
                if(num<=0) {
                    BroadcastReceiver.stopReceiver();
                    BroadcastSender.stopSend();
                }
            }

            @Override
            public void onError(Exception e) {
            }
        });
        L.e("监听剪贴板");
        final ClipboardManager cb = (ClipboardManager) MyApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        // cb.setPrimaryClip(ClipData.newPlainText("", ""));
        cb.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                final CharSequence text = cb.getText();
                L.e("剪贴板发生变化:" + text+" , 当前ip:"+connect_ip);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ClipboardClient clipboardClient = new ClipboardClient();
                        clipboardClient.init(text.toString(),connect_ip);
                    }
                }).start();

            }
        });
        return super.onStartCommand(intent, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
