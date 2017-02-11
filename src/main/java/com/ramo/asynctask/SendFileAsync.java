package com.ramo.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.ramo.application.MyApplication;
import com.ramo.bean.File;
import com.ramo.service.SendingFilesService;
import com.ramo.wifi.nio.MyServer;

/**
 * Created by ramo on 2016/4/28.
 */
public class SendFileAsync extends AsyncTask<Void, Void, Void> {
    MyServer myServer;
    Context context;

    public SendFileAsync(Context context) {
        this.context = context;
        myServer = new MyServer();
    }

    @Override
    protected void onPreExecute() {
        MyApplication.isSendingFiles = true;
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (File f : MyApplication.sendList) {
            SendingFilesService.sendTaskList.add(fileToDownload(f));
        }
        myServer.init();
        return null;
    }

    private Task fileToDownload(File f) {
        Task task = new Task(context, f, 1);
        task.readThreadInfoFromDB();
        return task;
    }

}
