package com.ramo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ramo.adapter.HistoricalRecordAdapter;
import com.ramo.application.MyApplication;
import com.ramo.bean.HistoricalRecord;
import com.ramo.bean.State;
import com.ramo.database.DB;
import com.ramo.database.FileDao;
import com.ramo.database.FileDaoImpl;
import com.ramo.database.RecordDaoImpl;
import com.ramo.file_transfer.R;
import com.ramo.service.FileTransferService;
import com.ramo.service.SendingFilesService;
import com.ramo.utils.L;
import com.ramo.utils.T;
import com.ramo.view.CustomDialog;
import com.ramo.view.TopBar;
import com.ramo.wifi.WiFiUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ramo on 2016/4/1.
 */
@EActivity(R.layout.activity_record_historical)
public class HistoricalRecordActivity extends Activity {
    @ViewById
    ListView historical_record_listview;
    @ViewById
    TopBar top_bar;

    private HistoricalRecordAdapter adapter;
    private List<HistoricalRecord> recordList;

    private String[] dialogItem = {"重新发送", "删除该记录", "属性"};
    private String[] dialogItem2 = {"打开文件所在位置", "删除该记录", "属性"};
    private AlertDialog.Builder to_dialog;
    private AlertDialog.Builder from_dialog;

    private static int currentLongClickPos = 0;

    private RecordDaoImpl recordDao;
    private FileDao fileDaoImpl;

    private List<com.ramo.bean.File> sendFilesList;
    private IntentFilter filter, filter2;

    private Intent serviceIntent;
    private Intent serviceGroupIntent;

    private static int isOUT = 0;
    private int isIn = 1;
    private String updateAction = "updateReceivListReceiver";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @AfterViews
    public void init() {
        to_dialog = new AlertDialog.Builder(HistoricalRecordActivity.this).
                setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        to_dialog_onClickEvent(i);
                    }
                });
        from_dialog = new AlertDialog.Builder(HistoricalRecordActivity.this).
                setItems(dialogItem2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        from_dialog_onClickEvent(i);
                    }
                });

        recordDao = new RecordDaoImpl(this);
        fileDaoImpl = new FileDaoImpl(this);

        filter = new IntentFilter();
        filter.addAction(SendingFilesService.ACTION_UPDATE);
        filter.addAction(SendingFilesService.ACTION_FINISHED);
        filter2 = new IntentFilter();

        serviceIntent = new Intent(this, SendingFilesService.class);
        serviceIntent.setAction(SendingFilesService.ACTION_START_SERVER);
        serviceGroupIntent = new Intent(this, SendingFilesService.class);
        serviceGroupIntent.setAction(SendingFilesService.ACTION_START_GROUP_SERVER);

        initData();
        initListener();
    }

    private void initData() {
        recordList = recordDao.findAllRecord();
        adapter = new HistoricalRecordAdapter(this, recordList);
        historical_record_listview.setAdapter(adapter);
        historical_record_listview.setEmptyView(findViewById(R.id.history_record_empty_lv));

        Intent intent = getIntent();
        L.e("isDiect:" + MyApplication.isDiect + "; intent:" + intent);
        if (intent != null) {
            if (MyApplication.isDiect)
                sendDirectFile();
            else
                recordOrSendFiles(intent);
        }

    }

    private void sendDirectFile() {
        List<com.ramo.bean.File> sendList = MyApplication.sendList;
        for (com.ramo.bean.File img : sendList) {
            L.e(img.getFileUrl());
            HistoricalRecord object = FileToRecord(img, isOUT);
            recordList.add(object);
            object.setState(State.FINISH);

            serviceIntent = new Intent(HistoricalRecordActivity.this,
                    FileTransferService.class);
            serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
            serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH,
                    img.getFileUrl());
            Set<String> keyIP = MainActivity.ipMap.keySet();
            L.e("Main的map size:"+MainActivity.ipMap.size());
            Iterator<String> it = keyIP.iterator();
            if(it.hasNext()){
                String key=it.next();
                L.e("key!!!!!!!!!!"+key);
                serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                        key);

            }

/*       !!!!ip     serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                    MyApplication.junior_address);*/
            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT,
                    7000);
            startService(serviceIntent);

            recordDao.addRecord(FileToRecord(img, isOUT), img);
        }
        adapter.notifyDataSetChanged();
    }

    private void recordOrSendFiles(Intent intent) {
        sendFilesList = (ArrayList<com.ramo.bean.File>) intent.getSerializableExtra(MyApplication.INTENT_EXTRA_NAME);

        if (sendFilesList != null) {
            for (com.ramo.bean.File img : sendFilesList) {
                recordList.add(FileToRecord(img, isOUT));
            }
            adapter.notifyDataSetChanged();
        }
        sendFilesList = (ArrayList<com.ramo.bean.File>) intent.getSerializableExtra("receiveList");
        if (sendFilesList != null) {
            for (com.ramo.bean.File img : sendFilesList) {
                recordList.add(FileToRecord(img, isIn));
            }
            adapter.notifyDataSetChanged();
        }
    }

    @NonNull
    private HistoricalRecord FileToRecord(com.ramo.bean.File img, int isInOrOut) {
        HistoricalRecord record = new HistoricalRecord();
        record.setDate(new Date());
        record.setRecordId(UUID.randomUUID().toString());

        if (isInOrOut == isOUT) {
            record.setType(HistoricalRecord.Type.OUTCOME);
            record.setSender(WiFiUtils.SSID);
            record.setReceiver(WiFiUtils.CONNECT_SSID);
        } else {
            record.setType(HistoricalRecord.Type.INCOME);
            record.setSender(WiFiUtils.CONNECT_SSID);
            record.setReceiver(WiFiUtils.SSID);
        }
        record.setFile(img);
        record.setState(State.WAIT);
        return record;
    }

    private void to_dialog_onClickEvent(int i) {
        HistoricalRecord record = recordList.get(currentLongClickPos);
        switch (i) {
            case 0:
                sendAgain(record);
                break;
            case 1:
                deleteThisRecord(record);
                break;
            case 2:
                browseFileProperties(record);
                break;
        }

    }

    private void from_dialog_onClickEvent(int i) {
        HistoricalRecord record = recordList.get(currentLongClickPos);
        switch (i) {
            case 0:
                openFileDir();
                break;
            case 1:
                deleteThisRecord(record);
                break;
            case 2:
                browseFileProperties(record);
                break;
        }

    }

    private void openFileDir() {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File("/storage/emulated/0/DCIM/Camera/20160328_124555.jpg"));
            intent.setDataAndType(uri, "image/*");
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            T.showShort(this, "请安装文件管理器");
        }
    }

    private void sendAgain(HistoricalRecord re) {
        HistoricalRecord record = copyBean(re);
        recordDao.beginTran(record, DB.ADD);
        recordList.add(record);
        adapter.notifyDataSetChanged();
    }

    private HistoricalRecord copyBean(HistoricalRecord re) {
        HistoricalRecord clone = null;
        try {
            clone = (HistoricalRecord) re.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        clone.setDate(new Date());
        clone.setRecordId(UUID.randomUUID().toString());
        return clone;
    }

    private void deleteThisRecord(HistoricalRecord re) {
        recordDao.beginTran(re, DB.DELETE);
        recordList.remove(re);
        adapter.notifyDataSetChanged();
    }

    private void browseFileProperties(HistoricalRecord record) {
        StringBuffer fileMsg = new StringBuffer();
        fileMsg.append("文件名：" + record.getFile().getFileName() + "\n");
        fileMsg.append("文件大小：" + record.getFile().getFileSize() + "\n");
        fileMsg.append("文件所在目录：" + record.getFile().getFileUrl() + "\n");
        fileMsg.append("传输时间：" + record.getDate().toLocaleString() + "\n");

        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(fileMsg.toString());
        builder.setTitle("文件属性");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }


    private void initListener() {

        top_bar.setOnTopBarClickListener(new TopBar.TopBarClickListener() {
            @Override
            public void leftClick() {
                onBackPressed();
                finish();
            }

            @Override
            public void RightClick() {
                clearAllRecordConfirm();
            }

        });
        historical_record_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if ((recordList.get(i).getType().toString()).equals(HistoricalRecord.Type.OUTCOME.toString()))
                    to_dialog.show();
                else
                    from_dialog.show();

                currentLongClickPos = i;
                return false;
            }
        });

    }

    private void clearAllRecordConfirm() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("确定删除全部传输记录？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                clearAllRecord();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void clearAllRecord() {
        for (HistoricalRecord re : recordDao.findAllRecord()) {
            recordDao.beginTran(re, DB.DELETE);
        }
        recordList.clear();
        adapter.notifyDataSetChanged();
    }

    /**
     * 更新UI的广播接收器
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SendingFilesService.ACTION_UPDATE.equals(intent.getAction())) {
                int finised = intent.getIntExtra("finished", 0);
                int id = intent.getIntExtra("id", 0);
                adapter.updateProgress(id, finised);
            } else if (SendingFilesService.ACTION_FINISHED.equals(intent.getAction())) {
                int id = intent.getIntExtra("id", 0);
                L.e("id : " + id);
                int isInOrOut = intent.getIntExtra("isInOrOut", 0);
                com.ramo.bean.File file = (com.ramo.bean.File) intent.getSerializableExtra("fileInfo");
                adapter.updateProgress(id, 100);
                L.e("ACTION_FINISHED");
                recordDao.addRecord(FileToRecord(file, isInOrOut), file);
            }
        }
    };

    BroadcastReceiver updateReceivListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if ("updateReceivListReceiver".equals(intent.getAction())) {
                L.e("updateReceivListReceiver");
                com.ramo.bean.File file = (com.ramo.bean.File) intent.getSerializableExtra("fileInfo");
                recordList.add(FileToRecord(file, isIn));
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mReceiver);
            unregisterReceiver(updateReceivListReceiver);
            stopService(serviceIntent);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.e("启动广播");
        registerReceiver(mReceiver, filter);
        registerReceiver(updateReceivListReceiver, filter2);

        L.e("启动服务器的服务");
        if (MyApplication.groupList.size() <= 1)
            startService(serviceIntent);
        else
            startService(serviceGroupIntent);

    }
}
