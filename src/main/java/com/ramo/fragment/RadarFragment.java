package com.ramo.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ramo.activity.ConnectStateActivity_;
import com.ramo.activity.MainActivity;
import com.ramo.application.MyApplication;
import com.ramo.asynctask.FileServerAsyncTask;
import com.ramo.bean.JuniorBean;
import com.ramo.bean.WifiP2p;
import com.ramo.file_transfer.R;
import com.ramo.receiver.WiFiDirectReceiver;
import com.ramo.receiver.WiFiReceiver;
import com.ramo.utils.ExtraName;
import com.ramo.utils.L;
import com.ramo.utils.RoundBitmapUtils;
import com.ramo.utils.WiFiDirectUtil;
import com.ramo.view.ArcMenu;
import com.ramo.view.RadarSearchDevicesView;
import com.ramo.wifi.WiFiUtils;
import com.ramo.wifi.lan.BroadcastReceiver;
import com.ramo.wifi.lan.BroadcastSender;
import com.ramo.wifi.lan.OnSearchListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by ramo on 2016/3/10.
 */
@EFragment(R.layout.radar_layout)
public class RadarFragment extends Fragment {
    @ViewById
    RadarSearchDevicesView search_device_view;
    private Boolean isWiFiEnabled = false;
    private Boolean scanThreadShouldStop = false;

    @ViewById
    TextView radar_os_name;
    @ViewById
    TextView radar_create;
    private WiFiUtils wiFiUtils;

    private WiFiReceiver wiFiReceiver;
    private Set<String> haveBeenDrawnWiFiSet;

    @ViewById
    ArcMenu mArcMenu;
    /*wifi直连*/
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private Context context;
    private WiFiDirectReceiver mReceiver;
    private List peers = new ArrayList();
    private List<HashMap<String, String>> peersshow = new ArrayList();
    private WifiP2pInfo info;
    private IntentFilter mDirectFilter;

    WiFiDirectUtil wiFiDirectUtil = new WiFiDirectUtil();

    private boolean isServer = true;

    private Handler jumpToConnectActivityHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {

            JuniorBean bean = (JuniorBean) msg.obj;

            String connect_msg = "wfifi直连成功";
            String junior_name = bean.getJunior_name();
            String junior_address = bean.getJunior_address();

            Intent intent = new Intent(getActivity(), ConnectStateActivity_.class);
            intent.putExtra(ExtraName.junior_name, junior_name);
            intent.putExtra(ExtraName.junior_address, junior_address);
            intent.putExtra(ExtraName.DirectResult, connect_msg);
            startActivity(intent);
            getActivity().finish();
        }

    };
    /*end wifi直连*/
    private Handler changTextHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1)
                radar_create.setText("创建成功");
        }
    };
    private Handler getWiFiResultHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1)
                modifyRadarUI();
        }
    };

    @AfterViews
    public void init() {
        isServerOrClient();
        if (isServer) {
            BroadcastReceiver.lanchApp();
            BroadcastReceiver.setOnSearchListener(new OnSearchListener() {
                @Override
                public void onSuess(String ip, String host) {
                    MainActivity.ipMap.put(ip, host);
                  //  L.e(ip + ":" + host + "               ramo");
                }

                @Override
                public void onError(Exception e) {

                }
            });
        } else {
            BroadcastSender.lanchApp();
        }
        context = getContext();
        haveBeenDrawnWiFiSet = new HashSet<String>();
        wiFiUtils = new WiFiUtils(getContext());
        regReceiver();
        beginSendBroadcast();

        search_device_view.setWillNotDraw(false);
        search_device_view.setSearching(true);

        initDirectIntentFilter();
        initListener();
    }

    private void isServerOrClient() {
        Bundle data = getArguments();
        if (data != null) {
            isServer = data.getBoolean(ExtraName.SERVER);
        }
    }

    private void beginSendBroadcast() {
        final Intent intent = new Intent(ExtraName.WiFiChangeIntent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isWiFiEnabled) {
                    if (scanThreadShouldStop)
                        break;
                    try {
                        getContext().sendBroadcast(intent);
                        getWiFiResultHandler.sendEmptyMessage(1);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }

    private void initListener() {


        mArcMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                MyApplication.isDiect = false;
                if (pos == 2) {
                    isWiFiEnabled = true;
                   // L.e("开启热点");
                    wiFiUtils.setWifiApEnabled(true);
                    destroyReceiverAndAsyncTask();
                    search_device_view.setSearching(false);
                    radar_os_name.setText(WiFiUtils.SSID);
                    radar_create.setText("正在创建");
                    radar_os_name.setVisibility(View.VISIBLE);
                    radar_create.setVisibility(View.VISIBLE);

                    simulateCreateWiFi();
                } else if (pos == 3) {

                    MyApplication.isDiect = true;
                    destroyReceiverAndAsyncTask();
                    initDirectReceiver();
                    isWiFiEnabled = false;
                    wiFiUtils.setWifiApEnabled(isWiFiEnabled);


                } else {
                    wiFiDirectUtil.unregDiectReceiver(getContext(), mReceiver);
                    isWiFiEnabled = false;
                    wiFiUtils.setWifiApEnabled(isWiFiEnabled);
                 //   L.e("wifi扫描");
                    regReceiver();
                    search_device_view.setSearching(true);
                    radar_os_name.setVisibility(View.GONE);
                    radar_create.setVisibility(View.GONE);
                }


            }
        });
    }

    int flagIsHas = 0;

    private void initDirectReceiver() {
        mManager = (WifiP2pManager) context.getSystemService(context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(context, Looper.myLooper(), null);


        if (!isServer) {
            wiFiDirectUtil.beGroupOwener(mManager, mChannel);
        }
        WifiP2pManager.PeerListListener mPeerListListerner = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peersList) {
                peers.clear();
                peersshow.clear();
                Collection<WifiP2pDevice> aList = peersList.getDeviceList();
                peers.addAll(aList);

                //  for (int i = 0; i < aList.size(); i++) {
                WifiP2pDevice a = (WifiP2pDevice) peers.get(0);
                HashMap<String, String> map = new HashMap<String, String>();
                String deviceName = a.deviceName;

                map.put("name", deviceName);
                map.put("address", a.deviceAddress);
                peersshow.add(map);
                if (flagIsHas == 0) {

                    //  if (!haveBeenDrawnWiFiSet.contains(deviceName)) {
                    createHotHead(deviceName, 0);
                    flagIsHas++;

                  //  haveBeenDrawnWiFiSet.add(deviceName);
                }
                //    }

                //    }
            }
        };

        WifiP2pManager.ConnectionInfoListener mInfoListener = new WifiP2pManager.ConnectionInfoListener() {

            @Override
            public void onConnectionInfoAvailable(final WifiP2pInfo minfo) {
                info = minfo;

                if (info.groupFormed && info.isGroupOwner) {
                    L.e("info 是组长（服务器端）");
                    FileServerAsyncTask mServerTask = new FileServerAsyncTask(context);
                    mServerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                } else if (info.groupFormed) {
                    L.e("info 是组员（客户端）");

                    new Thread(new Runnable() {
                        String junior_name = "demo";
                        String junior_address;

                        @Override
                        public void run() {
                            try {
                                // junior_name = info.groupOwnerAddress.getHostName();
                                junior_address = info.groupOwnerAddress.getHostAddress();
                                Message msg = new Message();
                                JuniorBean bean = new JuniorBean(junior_name, junior_address);
                                msg.obj = bean;
                                msg.what = 1;
                                jumpToConnectActivityHandler.sendMessage(msg);
                            } catch (Exception e) {
                                junior_name = "demo";
                                junior_address = "192.168.0.0";
                                e.printStackTrace();
                            }
                            L.e("!!!junior_address" + junior_address);
                        }
                    }).start();

                }

            }
        };
        wiFiDirectUtil.unregDiectReceiver(getContext(), mReceiver);
        mReceiver = new WiFiDirectReceiver(context, new WifiP2p(mManager, mChannel, mPeerListListerner, mInfoListener));
        context.registerReceiver(mReceiver, mDirectFilter);

        wiFiDirectUtil.discoverPeers(mManager, mChannel);

    }


    private void simulateCreateWiFi() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    changTextHandler.sendEmptyMessage(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void regReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ExtraName.WiFiChangeIntent);
        if (wiFiReceiver == null)
            wiFiReceiver = new WiFiReceiver();
        getContext().registerReceiver(wiFiReceiver, intentFilter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        wiFiDirectUtil.stopConnect(mManager, mChannel);
    }


    private void initDirectIntentFilter() {
        mDirectFilter = new IntentFilter();
        mDirectFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mDirectFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mDirectFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mDirectFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        mDirectFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public void modifyRadarUI() {

        List<String> passableWiFiList = wiFiUtils.getPassableWiFiList();
        for (String ssid : passableWiFiList)
            if (!haveBeenDrawnWiFiSet.contains(ssid)) {
                createHotHead(ssid, -1);
            }

    }

    private void createHotHead(String ssid, int position) {

        Bitmap bitmap = RoundBitmapUtils.RtoRoundBitmap(getContext(), R.drawable.sidebar_head_aoteman, Color.WHITE);
        ImageView userHead = new ImageView(getContext());
        userHead.setImageBitmap(bitmap);

        TextView userSSID = new TextView(getContext());
        userSSID.setText(ssid);
        userSSID.setTextColor(Color.WHITE);

        int width = (int) (Math.random() * 20 + 40);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = (int) (Math.random() * 300 + 10);
        layoutParams.topMargin = (int) (Math.random() * 400 + 10);

        LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams ivlayoutParams = new LinearLayout.LayoutParams(width, width);

        LinearLayout linearLayout = new LinearLayout(getContext());

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(userHead, ivlayoutParams);
        linearLayout.addView(userSSID, tvLayoutParams);

        search_device_view.addView(linearLayout, layoutParams);

        haveBeenDrawnWiFiSet.add(ssid);

        beiginAnimation(userHead);
        initUserHeadListener(linearLayout, position);
    }

    private void initUserHeadListener(LinearLayout linearLayout, final int position) {
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MyApplication.isDiect) {

                    TextView ssid = (TextView) ((LinearLayout) v).getChildAt(1);
                    Intent intent = new Intent(getContext(), ConnectStateActivity_.class);
                    intent.putExtra(ExtraName.ssid, ssid.getText().toString());
                    startActivity(intent);

                } else {
                    connectDirect(position);
                }
            }
        });
    }


    private void connectDirect(int position) {

        final String name = peersshow.get(position).get("name");
        final String address = peersshow.get(position).get("address");
        wiFiDirectUtil.createConnect(name,
                address, mManager, mChannel);

    }

    private void beiginAnimation(ImageView imageView) {
        Animation scale_anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale_anim);
        scale_anim.setFillAfter(true);
        imageView.startAnimation(scale_anim);
    }


    @Override
    public void onPause() {
        super.onPause();
        destroyReceiverAndAsyncTask();
        scanThreadShouldStop = true;
        wiFiDirectUtil.unregDiectReceiver(getContext(), mReceiver);
    }


    private void destroyReceiverAndAsyncTask() {
        if (wiFiReceiver != null) {
            getContext().unregisterReceiver(wiFiReceiver);
            wiFiReceiver = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        scanThreadShouldStop = false;
        beginSendBroadcast();
    }


}

