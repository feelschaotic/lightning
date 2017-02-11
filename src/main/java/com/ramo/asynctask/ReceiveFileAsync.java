package com.ramo.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.ramo.utils.L;
import com.ramo.wifi.nio.MyClient;
import com.ramo.wifi.nio.NIOConstant;

/**
 * Created by ramo on 2016/4/28.
 */
public class ReceiveFileAsync extends AsyncTask<String, Object, Void> {
    private Context context;

    public ReceiveFileAsync(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        MyClient client = new MyClient();
        if (params.length > 0)
            client.init(params[0], NIOConstant.PORT);
        else
            client.init(null,NIOConstant.PORT);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        L.e("ReceiveFileAsync结束");
    }
}
