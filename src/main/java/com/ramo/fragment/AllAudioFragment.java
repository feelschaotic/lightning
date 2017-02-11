package com.ramo.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ramo.activity.MainActivity;
import com.ramo.adapter.OnClickSendImgListener;
import com.ramo.adapter.SendAllMultimediaAdapter;
import com.ramo.bean.Multimedia;
import com.ramo.file_transfer.R;
import com.ramo.utils.FileManageUtil;
import com.ramo.utils.SystemFilesGetter;
import com.ramo.utils.T;
import com.ramo.view.CustomDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramo on 2016/3/10.
 */
@EFragment(R.layout.send_all_multimedia)
public class AllAudioFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @ViewById
    ListView send_all_multimedia_lv;
    @ViewById
    TextView send_all_multimedia_tv;
    private List<Multimedia> allAudioList;
    private Gson gson = new Gson();
    private SendAllMultimediaAdapter multimediaAdapter;
    /*文件操作*/
    private String[] fileManagerDialogItem = {"删除文件", "重命名", "移动到"};
    private AlertDialog.Builder fileManagerDialog;
    private int currentLongClickPos = 0;
    private FileManageUtil fileManageUtil;

    private int[] check_tag_audio;
    private boolean isCheckAll = false;
    @ViewById
    ImageView send_all_multimedia_check_iv;
    @ViewById
    SwipeRefreshLayout refresh_layout;// 刷新控件
    private Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    T.showShort(getActivity(), "刷新出错");
                case 1:
                    refresh_layout.setRefreshing(false);
                    multimediaAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @AfterViews
    protected void init() {
        fileManageUtil = new FileManageUtil();
        initData();
        send_all_multimedia_tv.setText("全部音频(" + allAudioList.size() + ")");
        initRefreshLayout();
        initListener();
    }

    private void initCheckTag() {
        if (CheckTag.isTagNull(CheckTag.check_tag_audio))
            CheckTag.check_tag_audio = new int[allAudioList.size()];
        initThisCheckTag();
    }

    private void initThisCheckTag() {
        check_tag_audio = CheckTag.check_tag_audio;
    }

    private void initRefreshLayout() {
        refresh_layout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        refresh_layout.setOnRefreshListener(this);// 设置下拉的监听
    }

    private void initListener() {
        fileManagerDialog = new AlertDialog.Builder(getActivity()).
                setItems(fileManagerDialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        file_manager_dialog_onClickEvent(i);
                    }
                });

        send_all_multimedia_lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                fileManagerDialog.show();
                currentLongClickPos = position;
                return false;
            }
        });

        multimediaAdapter.setOnItemClickListener(new OnClickSendImgListener() {
            @Override
            public void onItemClick(View view, Integer tag) {
            }

            @Override
            public void onCheckClick(View v, Integer tag) {
                ImageView check = (ImageView) v;
                if (check_tag_audio[tag] == 0) {
                    check.setImageResource(R.drawable.common_check_on);
                    check_tag_audio[tag] = 1;
                    ImageView multImg = (ImageView) ((RelativeLayout) v.getParent()).findViewById(R.id.multImg);
                    MainActivity.addSendListAndChangeStyle(multImg, allAudioList.get(tag));
                } else {
                    check.setImageResource(R.drawable.common_check_off);
                    check_tag_audio[tag] = 0;
                    MainActivity.removeSendListAndChangeStyle(allAudioList.get(tag));
                }
            }
        });

        send_all_multimedia_check_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCheckAll) {
                    send_all_multimedia_check_iv.setImageResource(R.drawable.common_check_on);
                    for (int i = 0; i < CheckTag.check_tag_audio.length; i++) {
                        CheckTag.check_tag_audio[i] = 1;
                        MainActivity.addSendListAndChangeStyle(null, allAudioList.get(i));
                    }

                } else {
                    send_all_multimedia_check_iv.setImageResource(R.drawable.common_check_off);
                    for (int i = 0; i < CheckTag.check_tag_audio.length; i++) {
                        CheckTag.check_tag_audio[i] = 0;
                        MainActivity.removeSendListAndChangeStyle(allAudioList.get(i));
                    }
                }
                multimediaAdapter.notifyDataSetChanged();
                isCheckAll = !isCheckAll;
            }
        });
    }

    private void file_manager_dialog_onClickEvent(int i) {

        final Multimedia multimedia = allAudioList.get(currentLongClickPos);
        final String fileUrl = multimedia.getFileUrl();
        final CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        switch (i) {
            case 0:
                builder.setMessage("确定删除吗？");
                builder.setTitle("提示");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        fileManageUtil.delete(fileUrl);
                        allAudioList.remove(multimedia);
                        changStyleAndData();
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case 1:
                builder.setMessage(multimedia.getFileName());
                builder.setTitle("新文件名");
                builder.setIsInput(true);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        getNewNameAndRename();
                        dialog.dismiss();
                    }

                    private void getNewNameAndRename() {
                        String newName = builder.getInputMessage();
                        fileManageUtil.rename(fileUrl, newName);
                        multimedia.setFileName(newName);
                        changStyleAndData();
                    }
                });
                builder.create().show();

                break;
            case 2:
                break;
        }


    }

    private void changStyleAndData() {
        multimediaAdapter.notifyDataSetChanged();
    }

    private void initData() {
        allAudioList = new ArrayList<>();
        getFromSystemDB();

        boolean isAudioActivity = true;
        multimediaAdapter = new SendAllMultimediaAdapter(getContext(),
                allAudioList, isAudioActivity);
        send_all_multimedia_lv.setAdapter(multimediaAdapter);
    }

    public void getFromSystemDB() {
        SystemFilesGetter getter = new SystemFilesGetter(getActivity());
        allAudioList.clear();
        allAudioList.addAll(getter.getAllAudio());
        initCheckTag();
    }


    @Override
    public void onRefresh() {
        new Thread(new Runnable() {// 下拉触发的函数，这里是谁1s然后加入一个数据，然后更新界面
            @Override
            public void run() {
                try {
                    getFromSystemDB();
                    refreshHandler.sendEmptyMessage(1);
                } catch (Exception e) {
                    refreshHandler.sendEmptyMessage(0);
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
