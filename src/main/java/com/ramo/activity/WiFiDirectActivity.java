package com.ramo.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ramo.adapter.WiFiDirectAdapter;
import com.ramo.asynctask.DataServerAsyncTask;
import com.ramo.asynctask.FileServerAsyncTask;
import com.ramo.bean.WifiP2p;
import com.ramo.file_transfer.R;
import com.ramo.receiver.WiFiDirectReceiver;
import com.ramo.service.DataTransferService;
import com.ramo.service.FileTransferService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ramo on 2016/7/21.
 */
@EActivity(R.layout.activity_wifi_direct)
public class WiFiDirectActivity extends Activity {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager.PeerListListener mPeerListListerner;
    private List peers = new ArrayList();
    private List<HashMap<String, String>> peersshow = new ArrayList();
    private WiFiDirectAdapter wiFiDirectAdapter;
    private WifiP2pInfo info;
    private WiFiDirectReceiver mReceiver;
    private FileServerAsyncTask mServerTask;
    private DataServerAsyncTask mDataTask;
    private IntentFilter mFilter;

    @ViewById(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @ViewById
    Button discover;
    @ViewById
    Button stopdiscover;
    @ViewById
    Button stopconnect;
    @ViewById
    Button sendpicture;
    @ViewById
    Button senddata;
    @ViewById
    Button begrouppwener;

    @AfterViews
    public void init() {

        initView();
        initIntentFilter();
        initReceiver();
        initEvents();
    }

    private void initEvents() {

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscoverPeers();
            }
        });
        begrouppwener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeGroupOwener();
            }
        });

        stopdiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StopDiscoverPeers();
            }
        });
        stopconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StopConnect();
            }
        });
        sendpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 20);

            }
        });

        senddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(WiFiDirectActivity.this,
                        DataTransferService.class);

                serviceIntent.setAction(DataTransferService.ACTION_SEND_FILE);

                serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                        info.groupOwnerAddress.getHostAddress());
                Log.i("address", "owenerip is " + info.groupOwnerAddress.getHostAddress());
                serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_PORT,
                        8888);
                WiFiDirectActivity.this.startService(serviceIntent);
            }
        });


        wiFiDirectAdapter.SetOnItemClickListener(new WiFiDirectAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                CreateConnect(peersshow.get(position).get("address"),
                        peersshow.get(position).get("name"));
            }

            @Override
            public void OnItemLongClick(View view, int position) {
            }
        });
    }

    private void BeGroupOwener() {
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 20) {
            super.onActivityResult(requestCode, resultCode, data);
            Uri uri = data.getData();
            Intent serviceIntent = new Intent(WiFiDirectActivity.this,
                    FileTransferService.class);

            serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
            serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH,
                    uri.toString());

            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                    info.groupOwnerAddress.getHostAddress());
            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT,
                    8988);
            WiFiDirectActivity.this.startService(serviceIntent);
        }
    }

    private void StopConnect() {
        SetButtonGone();
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    /*A demo base on API which you can connect android device by wifidirect,
    and you can send file or data by socket,what is the most important is that you can set
    which device is the client or service.*/

    private void CreateConnect(String address, final String name) {
        WifiP2pDevice device;
        WifiP2pConfig config = new WifiP2pConfig();
        Log.i("xyz", address);

        config.deviceAddress = address;
        /*mac地址*/

        config.wps.setup = WpsInfo.PBC;
        Log.i("address", "MAC IS " + address);
        if (address.equals("9a:ff:d0:23:85:97")) {
            config.groupOwnerIntent = 0;
            Log.i("address", "lingyige shisun");
        }
        if (address.equals("36:80:b3:e8:69:a6")) {
            config.groupOwnerIntent = 15;
            Log.i("address", "lingyigeshiwo");

        }

        Log.i("address", "lingyige youxianji" + String.valueOf(config.groupOwnerIntent));

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {


            }
        });
    }

    private void StopDiscoverPeers() {
        mManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {


            }
        });
    }

    private void initView() {

        sendpicture.setVisibility(View.GONE);
        senddata.setVisibility(View.GONE);

        wiFiDirectAdapter = new WiFiDirectAdapter(WiFiDirectActivity.this, peersshow);
        mRecyclerView.setAdapter(wiFiDirectAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager
                (this.getApplicationContext()));

    }

    private void initReceiver() {
        mManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        Looper srcLooper = Looper.myLooper();
        mChannel = mManager.initialize(this, srcLooper, null);

        WifiP2pManager.PeerListListener mPeerListListerner = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peersList) {
                peers.clear();
                peersshow.clear();
                Collection<WifiP2pDevice> aList = peersList.getDeviceList();
                peers.addAll(aList);

                for (int i = 0; i < aList.size(); i++) {
                    WifiP2pDevice a = (WifiP2pDevice) peers.get(i);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", a.deviceName);
                    map.put("address", a.deviceAddress);
                    peersshow.add(map);
                }
                wiFiDirectAdapter = new WiFiDirectAdapter(WiFiDirectActivity.this, peersshow);
                mRecyclerView.setAdapter(wiFiDirectAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager
                        (WiFiDirectActivity.this));
                wiFiDirectAdapter.SetOnItemClickListener(new WiFiDirectAdapter.OnItemClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        CreateConnect(peersshow.get(position).get("address"),
                                peersshow.get(position).get("name"));

                    }

                    @Override
                    public void OnItemLongClick(View view, int position) {

                    }
                });
            }
        };

        WifiP2pManager.ConnectionInfoListener mInfoListener = new WifiP2pManager.ConnectionInfoListener() {

            @Override
            public void onConnectionInfoAvailable(final WifiP2pInfo minfo) {

                Log.i("xyz", "InfoAvailable is on");
                info = minfo;
                TextView view = (TextView) findViewById(R.id.tv_main);
                if (info.groupFormed && info.isGroupOwner) {
                    Log.i("xyz", "owmer start");

                    mServerTask = new FileServerAsyncTask(WiFiDirectActivity.this);
                    mServerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    mDataTask = new DataServerAsyncTask(WiFiDirectActivity.this, view);
                    mDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                } else if (info.groupFormed) {
                    SetButtonVisible();
                }
            }
        };
        mReceiver = new WiFiDirectReceiver(this, new WifiP2p(mManager, mChannel, mPeerListListerner, mInfoListener));
    }

    private void SetButtonVisible() {
        sendpicture.setVisibility(View.VISIBLE);
        senddata.setVisibility(View.VISIBLE);
    }

    private void SetButtonGone() {
        sendpicture.setVisibility(View.GONE);
        senddata.setVisibility(View.GONE);
    }


    private void DiscoverPeers() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
            }
        });
    }

    private void initIntentFilter() {
        mFilter = new IntentFilter();
        mFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("xyz", "hehehehehe");
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StopConnect();
    }

    public void ResetReceiver() {

        unregisterReceiver(mReceiver);
        registerReceiver(mReceiver, mFilter);

    }
}
