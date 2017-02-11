package com.ramo.utils;

import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;

import com.ramo.receiver.WiFiDirectReceiver;

/**
 * Android4.0之后开始支持WifiDirect技术，即Wifi直连
 * 1.创建一个广播接收器和对等网络管理器
 * 2.初始化对等点的搜索
 * 3.获取对等点列表
 * 4.连接一个对等点
 * Created by ramo on 2016/7/21.
 */
public class WiFiDirectUtil {


    public void createConnect(String name, String address, WifiP2pManager mManager, WifiP2pManager.Channel mChannel) {

        WifiP2pConfig config = new WifiP2pConfig();
      //  L.d("address:" + address);

        config.deviceAddress = address;
        /*mac地址*/

        config.wps.setup = WpsInfo.PBC;
      //  L.d("address MAC IS " + address);
      /*  if (address.equals("9a:ff:d0:23:85:97")) {
            config.groupOwnerIntent = 0;
        }
        if (address.equals("36:80:b3:e8:69:a6")) {
            config.groupOwnerIntent = 15;
        }*/

      //  L.e("address " + String.valueOf(config.groupOwnerIntent));
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
            }
        });
    }

    public void discoverPeers(WifiP2pManager mManager, WifiP2pManager.Channel mChannel) {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
            }
        });
    }


    public void stopConnect(WifiP2pManager mManager, WifiP2pManager.Channel mChannel) {
        if (mManager != null)
            mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(int reason) {
                }
            });
    }


    public void unregDiectReceiver(Context context, WiFiDirectReceiver mReceiver) {
        if (mReceiver != null) {
            try {
                context.unregisterReceiver(mReceiver);
            } catch (Exception e) {
                L.e("广播未注册");
            }
        }
    }

   /* private void stopConnect() {
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }*/

    public void beGroupOwener(WifiP2pManager mManager, WifiP2pManager.Channel mChannel) {
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }
}
