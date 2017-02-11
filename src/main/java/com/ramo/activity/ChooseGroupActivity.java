package com.ramo.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ramo.file_transfer.R;
import com.ramo.wifi.WiFiUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by ramo on 2016/5/19.
 */
@EActivity(R.layout.activity_group_choose)
public class ChooseGroupActivity extends Activity {
    @ViewById
    TextView group_tv_name1;
    @ViewById
    TextView group_tv_name2;
    @ViewById
    TextView group_tv_name3;
    @ViewById
    Button group_join_btn;
    @ViewById
    TextView group_my_name;
    @ViewById
    TextView group_choose_state;

    private boolean isJoin = false;

    @AfterViews
    public void init() {
        group_my_name.setText(WiFiUtils.SSID);
        initListener();
        initData();
    }

    private void initData() {

    }

    private void initListener() {
        group_join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isJoin) {
                    startActivity(new Intent(ChooseGroupActivity.this, MainActivity_.class));
                    finish();
                } else {
                    isJoin = true;
                    group_choose_state.setText("连接成功");
                    group_join_btn.setText("分享文件");
                }
            }
        });
    }
}
