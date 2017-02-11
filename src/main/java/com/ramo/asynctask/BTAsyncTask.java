package com.ramo.asynctask;

import android.os.AsyncTask;

import com.ramo.bt.GetPeersInfo;

/**
 * Created by ramo on 2016/5/2.
 */
public class BTAsyncTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        GetPeersInfo peersInfo=new GetPeersInfo();
        peersInfo.getPeer();
        return null;
    }
}
