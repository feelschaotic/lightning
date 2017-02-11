package com.ramo.asynctask;

import android.os.AsyncTask;

/**
 * Created by ramo on 2016/5/2.
 */
public class StopLanSearchAsyncTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        com.ramo.wifi.lan.BroadcastReceiver.stopReceiver();
        return null;
    }
}
