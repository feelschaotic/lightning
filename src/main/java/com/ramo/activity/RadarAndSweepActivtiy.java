package com.ramo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.ramo.file_transfer.R;
import com.ramo.fragment.QrSweepFragment_;
import com.ramo.fragment.RadarFragment_;
import com.ramo.fragment.SoundFragment_;
import com.ramo.utils.ExtraName;
import com.ramo.view.TopBar;
import com.ramo.wifi.lan.BroadcastReceiver;
import com.ramo.wifi.lan.BroadcastSender;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramo on 2016/3/26.
 */
@EActivity(R.layout.radar_and_sweep)
public class RadarAndSweepActivtiy extends FragmentActivity {

    @ViewById
    ViewPager radar_and_sweep_vp;
    @ViewById
    TopBar radar_and_sweep_top_bar;
    @ViewById
    TextView radar_and_sweep_tv1;
    @ViewById
    TextView radar_and_sweep_tv2;
    @ViewById
    TextView radar_and_sweep_tv3;

    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private FragmentPagerAdapter adapter;
    private boolean isIAmServer = true;


    @AfterViews
    public void init() {

        judgeIsClientOrServer();
        initFragment();
        lightFont(0);
        // radar_and_sweep_vp.setCurrentItem(2);
    }

    private void judgeIsClientOrServer() {
        String stringExtra = getIntent().getStringExtra(ExtraName.SERVER_RO_CLIENT);
        if (stringExtra==null||stringExtra.equals(ExtraName.SERVER)) {
            isIAmServer = true;
        } else if (stringExtra.equals(ExtraName.CLIENT)) {
            isIAmServer = false;
        }
    }

    private void lightFont(int position) {
        resetFont();
        if (position == 0) {
            radar_and_sweep_tv1.setTextColor(Color.WHITE);
        } else if (position == 1) {
            radar_and_sweep_tv2.setTextColor(Color.WHITE);
        } else
            radar_and_sweep_tv3.setTextColor(Color.WHITE);
    }

    private void resetFont() {
        radar_and_sweep_tv1.setTextColor(Color.GRAY);
        radar_and_sweep_tv2.setTextColor(Color.GRAY);
        radar_and_sweep_tv3.setTextColor(Color.GRAY);
    }

    private void initFragment() {
        Fragment fragment1 = new RadarFragment_();
        Bundle args = new Bundle();
        args.putBoolean(ExtraName.SERVER,isIAmServer);
        fragment1.setArguments(args);
        Fragment fragment2 = new QrSweepFragment_();
        Fragment fragment3 = new SoundFragment_();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        fragmentList.add(fragment3);

        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }
        };

        radar_and_sweep_vp.setAdapter(adapter);

        initListener();
    }

    private void initListener() {
        radar_and_sweep_vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                lightFont(position);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        radar_and_sweep_tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lightFont(0);
                radar_and_sweep_vp.setCurrentItem(0);
            }
        });
        radar_and_sweep_tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lightFont(1);
                radar_and_sweep_vp.setCurrentItem(1);
            }
        });
        radar_and_sweep_tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lightFont(2);
                radar_and_sweep_vp.setCurrentItem(2);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastSender.stopSend();
        BroadcastReceiver.stopReceiver();
    }
}
