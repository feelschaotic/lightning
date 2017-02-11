package com.ramo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ramo.application.MyApplication;
import com.ramo.asynctask.GroupSendFileAsync;
import com.ramo.asynctask.SendFileAsync;
import com.ramo.asynctask.Task;
import com.ramo.utils.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramo on 2016/4/26.
 */
public class SendingFilesService extends Service {
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_FINISHED = "ACTION_FINISHED";
    public static final String ACTION_START_GROUP_SERVER = "ACTION_START_GROUP_SERVER";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String ACTION_START_SERVER = "ACTION_START_SERVER";
    public static List<Task> sendTaskList;

    @Override
    public void onCreate() {
        super.onCreate();
        sendTaskList = new ArrayList<Task>();
        L.e("Service create");
        /*Notification notification = new Notification(R.mipmap.icon, "下载中....", System.currentTimeMillis());

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.sending_files_notification_layout);
        contentView.setImageViewResource(R.id.notification_fileImg, R.drawable.sidebar_head_aoteman);
        contentView.setTextViewText(R.id.notification_state, "30%");
        contentView.setTextViewText(R.id.notification_fileName, "This is notification_fileName");
        notification.contentView = contentView;

        Intent intent = new Intent(this, HistoricalRecordActivity_.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notification.contentIntent = pendingIntent;
        startForeground(1, notification);*/

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        judgeActionType(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void judgeActionType(Intent intent) {
        if (intent == null)
            return;
        if (ACTION_START_SERVER.equals(intent.getAction())) {
            L.e("judgeActionType :ACTION_START_SERVER");
            new SendFileAsync(this).execute();
        }else if(ACTION_START_GROUP_SERVER.equals(intent.getAction())){
            new GroupSendFileAsync(this).execute();
        }
        else if (ACTION_PAUSE.equals(intent.getAction())) {
        } else if (ACTION_UPDATE.equals(intent.getAction())) {

        }
    }


    @Override
    public void onDestroy() {
        L.e("Service destroy");
        MyApplication.isSendingFiles = false;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        L.e("onBind");

        return null;
    }

}
