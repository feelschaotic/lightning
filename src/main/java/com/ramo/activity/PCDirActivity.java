package com.ramo.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ramo.adapter.OnClickSendImgListener;
import com.ramo.adapter.PCDirAdapter;
import com.ramo.bean.File;
import com.ramo.bean.PcInfoVO;
import com.ramo.file_transfer.R;
import com.ramo.utils.L;
import com.ramo.view.CustomDialog;
import com.ramo.wifi.nio.DirClient;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ramo on 2016/5/18.
 */
@EActivity(R.layout.activity_dir_pc)
public class PCDirActivity extends Activity {
    @ViewById
    RecyclerView pc_dir_rv;
    @ViewById
    TextView pc_dir_path_tv;
    private PCDirAdapter pcDirAdapter;
    private PCDirAdapter pcDirDetailedInfoAdapter;

    private List<File> dirFile;
    @ViewById
    ImageView pc_dir_load_gifview;
    Animation searchAnimation;
    @ViewById
    ImageView pc_dir_tab;
    private boolean isDetailedInformationModel = false;

    @AfterViews
    public void init() {
        dirFile = new ArrayList<File>();
        pcDirAdapter = new PCDirAdapter(dirFile,0);
        pcDirDetailedInfoAdapter = new PCDirAdapter(dirFile,1);
        searchAnimation = AnimationUtils.loadAnimation(this, R.anim.search_anim);
        pc_dir_load_gifview.setAnimation(searchAnimation);
        defaultStyle();
        openDir("");

        initListener();
    }

    private void defaultStyle() {
        pc_dir_rv.setAdapter(pcDirAdapter);
        pc_dir_rv.setLayoutManager(new GridLayoutManager(this, 4));
    }

    private void initListener() {
        pc_dir_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDetailedInformationModel) {
                    pc_dir_tab.setImageResource(R.drawable.send_all_image_tab_right);
                    pc_dir_rv.setAdapter(pcDirDetailedInfoAdapter);
                    pc_dir_rv.setLayoutManager(new LinearLayoutManager(PCDirActivity.this));
                } else {
                    pc_dir_tab.setImageResource(R.drawable.send_all_image_tab_left);
                    defaultStyle();
                }
                isDetailedInformationModel = !isDetailedInformationModel;
            }
        });
        pcDirAdapter.setOnItemClickListener(new OnClickSendImgListener() {
            @Override
            public void onItemClick(View v, Integer tag) {
                adapterItemClick(v, tag);
            }
            @Override
            public void onCheckClick(View v, Integer tag) {
            }
        });

        pcDirDetailedInfoAdapter.setOnItemClickListener(new OnClickSendImgListener() {
            @Override
            public void onItemClick(View v, Integer tag) {
                adapterItemClick(v, tag);
            }
            @Override
            public void onCheckClick(View v, Integer tag) {
            }
        });
    }

    private void adapterItemClick(View v, Integer tag) {
        File f = dirFile.get(tag);
        if (f.getFlag() == 1) {
            getNewPathAndOpenDir(v);
        } else {
            hint(f);
        }
    }

    private void hint(final File f) {
        CustomDialog.Builder builder = new CustomDialog.Builder(PCDirActivity.this);
        builder.setMessage("不支持远程查看，下载到本地？");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                beginDownload(f);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
    private void beginDownload(File f) {
        ArrayList<File> files=new ArrayList<File>();
        files.add(f);
        Intent intent = new Intent(PCDirActivity.this, HistoricalRecordActivity_.class);
        intent.putExtra("receiveList",files);
        startActivity(intent);
    }

    private void getNewPathAndOpenDir(View v) {
        TextView tv = (TextView) v.findViewById(R.id.pc_dir_item_tv);
        String path = null;
        if (pc_dir_path_tv.getText().equals(""))
            path = tv.getText().toString();
        else
            path = pc_dir_path_tv.getText() + "/" + tv.getText().toString();
        openDir(path);
    }


    class ReceiveDirAsync extends AsyncTask<String, Void, String> {
        private Gson gson;
        private String nowDir;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pc_dir_load_gifview.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            nowDir = params[0];
            L.e(nowDir);
            DirClient client = new DirClient();
            while (true) {
                try {
                    return client.init(nowDir);
                } catch (Exception e) {
                    try {
                        return client.init(nowDir);
                    } catch (Exception e1) {
                        return null;
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String string) {
            dirFile.clear();
            gson = new Gson();
            if (!("".equals(nowDir)) && nowDir != null) {
                dirFile.addAll(gson.<Collection<? extends File>>fromJson(string,
                        new TypeToken<List<File>>() {}.getType()));
            } else {
                List<PcInfoVO> pcInfoes = gson.fromJson(string,
                        new TypeToken<List<PcInfoVO>>() {}.getType());
                if (pcInfoes != null)
                    for (PcInfoVO p : pcInfoes)
                        dirFile.add(pcInfoToFile(p));
            }
            pcDirAdapter.notifyDataSetChanged();
            pcDirDetailedInfoAdapter.notifyDataSetChanged();
            pc_dir_load_gifview.setVisibility(View.INVISIBLE);
            super.onPostExecute(string);
        }

        private File pcInfoToFile(PcInfoVO p) {
            File f = new File();
            f.setFileName(p.getName() + ":");
            f.setFileSize(p.getTotalSpace());
            f.setFlag(1);
            return f;
        }
    }

    @Override
    public void onBackPressed() {

        String path = pc_dir_path_tv.getText().toString();
        try {
            if (!(path.equals(""))) {
                path = path.substring(0, path.lastIndexOf("/"));
                openDir(path);
            }else
                super.onBackPressed();
        } catch (Exception e) {
            openDir("");
        }
    }

    private void openDir(String path) {
        new ReceiveDirAsync().execute(path);
        pc_dir_path_tv.setText(path);
    }
}