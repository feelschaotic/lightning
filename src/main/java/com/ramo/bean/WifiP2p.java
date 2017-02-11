package com.ramo.bean;

import android.net.wifi.p2p.WifiP2pManager;

/**
 * Created by ramo on 2016/7/23.
 */
public class WifiP2p {
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager.PeerListListener mPeerListListener;
    private WifiP2pManager.ConnectionInfoListener mInfoListener;

    public WifiP2p(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, WifiP2pManager.PeerListListener mPeerListListerner, WifiP2pManager.ConnectionInfoListener mInfoListener) {
        this.mChannel = mChannel;
        this.mManager = mManager;
        this.mPeerListListener = mPeerListListerner;
        this.mInfoListener = mInfoListener;
    }

    public WifiP2pManager getmManager() {
        return mManager;
    }

    public void setmManager(WifiP2pManager mManager) {
        this.mManager = mManager;
    }

    public WifiP2pManager.Channel getmChannel() {
        return mChannel;
    }

    public void setmChannel(WifiP2pManager.Channel mChannel) {
        this.mChannel = mChannel;
    }

    public WifiP2pManager.PeerListListener getmPeerListListener() {
        return mPeerListListener;
    }

    public void setmPeerListListener(WifiP2pManager.PeerListListener mPeerListListener) {
        this.mPeerListListener = mPeerListListener;
    }

    public WifiP2pManager.ConnectionInfoListener getmInfoListener() {
        return mInfoListener;
    }

    public void setmInfoListener(WifiP2pManager.ConnectionInfoListener mInfoListener) {
        this.mInfoListener = mInfoListener;
    }
}
