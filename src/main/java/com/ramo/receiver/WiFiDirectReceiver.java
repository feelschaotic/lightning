package com.ramo.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;

import com.ramo.bean.WifiP2p;
import com.ramo.utils.L;
import com.ramo.utils.T;

/**
 * Created by ramo on 2016/7/21.
 */
public class WiFiDirectReceiver extends BroadcastReceiver {
    private Context context;
    private Activity activity;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager.PeerListListener mPeerListListener = null;
    private WifiP2pManager.ConnectionInfoListener mInfoListener = null;

    public WiFiDirectReceiver(){
        super();
    }
    public WiFiDirectReceiver(Context context, WifiP2p wifiP2p) {
        this.context = context;
        this.mManager = wifiP2p.getmManager();
        this.mChannel = wifiP2p.getmChannel();
        this.mPeerListListener = wifiP2p.getmPeerListListener();
        this.mInfoListener = wifiP2p.getmInfoListener();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        /*check if the wifi is enable*/
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

        }
        /*get the list*/
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            mManager.requestPeers(mChannel, mPeerListListener);
        }
        /*查看当前是否处于查找状态
        * get the state of discover*/
        else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {

            int State = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);

            if (State == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED)
                T.showShort(context, "搜索开启");
            else if (State == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED)
                T.showShort(context, "搜索已关闭");

        }
        /*Respond to new connection or disconnections
        *查看是否创建连接*/
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                L.e("已连接");
                mManager.requestConnectionInfo(mChannel, mInfoListener);
            } else {
                L.e("断开连接");
                return;
            }
        }

        /*Respond to this device's wifi state changing*/
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
        }
    }
}
