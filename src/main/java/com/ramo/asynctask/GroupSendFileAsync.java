package com.ramo.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.ramo.application.MyApplication;
import com.ramo.bean.File;
import com.ramo.service.SendingFilesService;
import com.ramo.wifi.nio.GroupServer;

/**
 * Created by ramo on 2016/4/28.
 */
public class GroupSendFileAsync extends AsyncTask<Void, Void, Void> {
    GroupServer groupServer;
    Context context;

    public GroupSendFileAsync(Context context) {
        this.context = context;
        groupServer = new GroupServer();
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (File f : MyApplication.sendList) {
            SendingFilesService.sendTaskList.add(fileToDownload(f));
        }
        groupServer.init(MyApplication.groupList.size());
        return null;
    }

    @Override
    protected void onPreExecute() {
        MyApplication.isSendingFiles = true;
        super.onPreExecute();
    }


    private Task fileToDownload(File f) {
        Task task = new Task(context, f, 1);
        task.readThreadInfoFromDB();
        return task;
    }

}
