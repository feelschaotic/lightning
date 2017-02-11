package com.ramo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ramo.application.MyApplication;
import com.ramo.asynctask.FileServerAsyncTask;
import com.ramo.asynctask.StopLanSearchAsyncTask;
import com.ramo.file_transfer.R;
import com.ramo.utils.ExtraName;
import com.ramo.utils.T;
import com.ramo.view.CircleImageView;
import com.ramo.wifi.WiFiUtils;
import com.ramo.wifi.lan.BroadcastReceiver;
import com.ramo.wifi.lan.OnSearchListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ramo on 2016/4/18.
 */
@EActivity(R.layout.connect_state_layout)
public class ConnectStateActivity extends Activity {
    @ViewById
    LinearLayout connect_anim_LL;
    @ViewById
    TextView connect_state;
    @ViewById
    TextView connect_junior_name;
    @ViewById
    TextView connect_my_name;
    @ViewById
    Button connect_disconnect_btn;
    @ViewById
    ImageView connect_red_tip;
    @ViewById
    CircleImageView connect_userhead;

    @AfterViews
    public void init() {
        connect_my_name.setText(WiFiUtils.SSID);
        connectWiFi();
        beginAnim();
        initListener();
    }

    private void initListener() {
        connect_userhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connect_red_tip.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent(ConnectStateActivity.this, ChooseGroupActivity_.class);
                    startActivity(intent);
                }

            }
        });
    }

    private void connectWiFi() {
        connect_state.setText("连接中...");
        Intent intent = getIntent();
        if (intent != null) {
            String ssid = intent.getStringExtra(ExtraName.ssid);
            if (ssid != null) {
                connect_junior_name.setText(ssid);
                new ConnectWiFiAsyncTask().execute(ssid);

            } else {
                String result = intent.getStringExtra(ExtraName.DirectResult);
                String junior_name = intent.getStringExtra(ExtraName.junior_name);
                MyApplication.junior_address = intent.getStringExtra(ExtraName.junior_address);
                WiFiUtils.CONNECT_SSID = junior_name;

                if (result != null) {
                    connect_junior_name.setText(junior_name);
                    connect_state.setText(result);
                    MyApplication.connectSuccessd = true;

                    FileServerAsyncTask mServerTask = new FileServerAsyncTask(ConnectStateActivity.this);
                    mServerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else
                    connect_state.setText("连接失败");
            }
        }
    }

    private void beginSearchLAN() {
        new SearchLANClientAsync().execute();
    }

    private void beginAnim() {
        Animation connect_anim = AnimationUtils.loadAnimation(this, R.anim.connect_anim);
        connect_anim_LL.startAnimation(connect_anim);
    }

    @Override
    protected void onDestroy() {
        new StopLanSearchAsyncTask().execute();
        super.onDestroy();
    }

    public class ConnectWiFiAsyncTask extends AsyncTask<String, Void, Boolean> {
        private WiFiUtils wiFiUtils = new WiFiUtils(ConnectStateActivity.this);

        @Override
        protected Boolean doInBackground(String... params) {
            return wiFiUtils.connectSpecifiedWiFi(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            MyApplication.connectSuccessd = result;
            if (result) {
                connect_disconnect_btn.setText("断开连接");
                connect_state.setText("连接成功");

                beginSearchLAN();
            } else {
                connect_disconnect_btn.setText("重新连接");
                connect_state.setText("连接失败");
            }
            super.onPostExecute(result);
        }
    }

    @Click(R.id.connect_send_files_btn)
    public void send_files_btnOnClick() {
        if (MyApplication.connectSuccessd) {
            startActivity(new Intent(ConnectStateActivity.this, MainActivity_.class));
            finish();
        } else {
            T.showShort(ConnectStateActivity.this, "连接失败，请重新连接");
        }
    }

    @Click(R.id.connect_disconnect_btn)
    public void disconnect_btnOnClick() {
        if (MyApplication.connectSuccessd) {
            disconnect();
        } else {
            startActivity(new Intent(ConnectStateActivity.this, RadarAndSweepActivtiy_.class));
            finish();
        }
    }

    @Click(R.id.connect_manager_pc)
    public void manager_pc_btnOnClick() {
        if (MyApplication.connectSuccessd) {
            startActivity(new Intent(ConnectStateActivity.this, PCDirActivity_.class));
            finish();
        } else {
            T.showShort(ConnectStateActivity.this, "连接失败，请重新连接");
        }
    }

    private void disconnect() {

    }

    public class SearchLANClientAsync extends AsyncTask<Void, Void, Map<String, String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            T.showShort(ConnectStateActivity.this, "搜索群组..");
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {
            BroadcastReceiver.lanchApp();
            final Map<String, String> map = new HashMap<String, String>();
            BroadcastReceiver.setOnSearchListener(new OnSearchListener() {
                @Override
                public void onSuess(String ip, String host) {
                    map.put(ip, host);
                }

                @Override
                public void onError(Exception e) {

                }
            });
            return map;
        }

        @Override
        protected void onPostExecute(Map<String, String> map) {
            if (map != null && map.size() > 0) {
                connect_red_tip.setVisibility(View.VISIBLE);
                MyApplication.groupList.clear();

                for (String s : map.keySet()) {
                    MyApplication.groupList.add(s);
                }
            } else {
                T.showShort(ConnectStateActivity.this, "没有搜索到群组");
                connect_red_tip.setVisibility(View.INVISIBLE);
            }
            super.onPostExecute(map);
        }
    }
}
