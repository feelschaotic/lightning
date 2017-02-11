package com.ramo.fragment;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ramo.activity.MainActivity;
import com.ramo.adapter.OnClickSendImgListener;
import com.ramo.adapter.SendAllImgAdapter;
import com.ramo.adapter.SendAllImgDetailAdapter;
import com.ramo.application.MyApplication;
import com.ramo.bean.File;
import com.ramo.file_transfer.R;
import com.ramo.listener.BackHandlerInterface;
import com.ramo.utils.StringUtil;
import com.ramo.utils.SystemFilesGetter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ramo on 2016/3/10.
 */
@EFragment(R.layout.send_all_img)
public class AllImgFragment extends Fragment {

    @ViewById(R.id.send_all_img_rv)
    RecyclerView send_all_app_rv;

    public List<File> allImgList;
    @ViewById
    ImageView send_all_img_tab;

    public static SendAllImgAdapter imgAdapter;
    SendAllImgDetailAdapter detailAdapter;

    private boolean isDetailedInformationModel = false;

    @ViewById
    TextView send_all_img_tv;

    private LinkedHashMap<String, List<File>> allFolderMap;

    protected BackHandlerInterface backHandlerInterface;

    private int mHandledPress = 0;

    private final String preName = "imgList";
    private Gson gson = new Gson();
    public static ArrayList<File> allImgListCopy;
    private int check_tag_img[];
    private int[] check_tag_img_folder;

    @ViewById
    ImageView loadView;

    Animation searchAnimation;

    @AfterViews
    public void init() {
        //回调函数赋值
        if (!(getActivity() instanceof BackHandlerInterface)) {
            throw new ClassCastException("Hosting activity must implement BackHandlerInterface");
        } else {
            backHandlerInterface = (BackHandlerInterface) getActivity();
        }
        searchAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.search_anim);
        send_all_app_rv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        send_all_app_rv.setItemAnimator(new DefaultItemAnimator());
        allImgList=new ArrayList<>();
        initData();
    }

    public void initData() {
        new initTask().execute();
    }

    class initTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadView.setVisibility(View.VISIBLE);
            loadView.startAnimation(searchAnimation);
        }

        @Override
        protected Void doInBackground(Void... params) {

            //getFromSharedPre();
            //   if (allImgList == null || allImgList.size() == 0) {
            //若没有 再去系统数据库取F
            getFromSystemDB();
            //     getFromSharedPre();
            //    }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            allImgListCopy = new ArrayList<File>();
            allImgListCopy.addAll(allImgList);
            //check_tag_img = new int[allImgListCopy.size()];

            coutFolder();
            initCheckTag();

            imgAdapter = new SendAllImgAdapter(allImgList, send_all_app_rv);
            send_all_app_rv.setAdapter(imgAdapter);

            loadView.clearAnimation();
            loadView.setVisibility(View.GONE);

            initView();
            initListener();

        }
    }

    private void coutFolder() {
        allFolderMap = new LinkedHashMap<String, List<File>>();
        for (File f : allImgList) {
            String folderName = StringUtil.getFolderNameFromUrl(f.getFileUrl());
            if (allFolderMap.get(folderName) == null) {
                List<File> arrayList = new ArrayList<File>();
                arrayList.add(f);
                allFolderMap.put(folderName, arrayList);
            } else {
                allFolderMap.get(folderName).add(f);
            }
        }
        check_tag_img_folder = new int[allFolderMap.size()];
        detailAdapter = new SendAllImgDetailAdapter(allFolderMap);

    }

    protected void initView() {
        send_all_img_tv.setText("我的图片(" + allImgList.size() + ")");
    }


    private void initListener() {

        send_all_img_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isDetailedInformationModel) {
                    send_all_img_tab.setImageResource(R.drawable.send_all_image_tab_right);
                    send_all_app_rv.setAdapter(detailAdapter);
                    send_all_app_rv.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    send_all_img_tv.setText("图片文件夹(" + allFolderMap.size() + ")");
                } else {
                    send_all_img_tab.setImageResource(R.drawable.send_all_image_tab_left);
                    allImgList.clear();
                    allImgList.addAll(allImgListCopy);
                    notifyDataChanged();

                    send_all_img_tv.setText("我的图片(" + allImgList.size() + ")");
                    send_all_app_rv.setAdapter(imgAdapter);
                    send_all_app_rv.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
                }
                isDetailedInformationModel = !isDetailedInformationModel;
            }
        });

        imgAdapter.setOnItemClickListener(new OnClickSendImgListener() {
            @Override
            public void onItemClick(View view, Integer tag) {
                LinearLayout parent = (LinearLayout) view.getParent();
                ImageView check = (ImageView) parent.findViewById(R.id.send_all_img_check);
                if (check_tag_img[tag] == 0) {
                    check.setVisibility(View.VISIBLE);
                    check_tag_img[tag] = 1;
                    MainActivity.addSendListAndChangeStyle((ImageView) parent.findViewById(R.id.imageView), allImgList.get(tag));
                } else {
                    check.setVisibility(View.GONE);
                    check_tag_img[tag] = 0;
                    MainActivity.removeSendListAndChangeStyle(allImgList.get(tag));
                }
            }

            @Override
            public void onCheckClick(View v, Integer tag) {
            }
        });

        detailAdapter.setOnItemClickListener(new OnClickSendImgListener() {
            @Override
            public void onItemClick(View view, Integer tag) {
                String key = ((TextView) view.findViewById(R.id.send_all_img_detail_fileName)).getText().toString();
                List<File> mfileList = allFolderMap.get(key);
                allImgList.clear();
                allImgList.addAll(mfileList);
                notifyDataChanged();
                send_all_app_rv.setAdapter(imgAdapter);
                send_all_app_rv.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
                mHandledPress++;

                send_all_img_tv.setText(key + "(" + allImgList.size() + ")");
            }

            @Override
            public void onCheckClick(View v, Integer tag) {
                RelativeLayout parent = (RelativeLayout) v.getParent();
                String key = ((TextView) parent.findViewById(R.id.send_all_img_detail_fileName)).getText().toString();
                ImageView animIv = ((ImageView) parent.findViewById(R.id.folder_first_img));
                ImageView check = (ImageView) v;
                if (check_tag_img_folder[tag] == 0) {
                    check.setImageResource(R.drawable.common_check_on);
                    check_tag_img_folder[tag] = 1;
                    addFolderSendListAndChangeStyle(animIv, key);
                } else {
                    check.setImageResource(R.drawable.common_check_off);
                    check_tag_img_folder[tag] = 0;
                    removeFolderSendListAndChangeStyle(key);
                }
            }
        });

    }

    private void notifyDataChanged() {
        imgAdapter.notifyDataSetChanged();
        imgAdapter.beforeNotifyAndChangeImgLoader(allImgList);
    }

    private void removeFolderSendListAndChangeStyle(String key) {
        MyApplication.sendList.removeAll(allFolderMap.get(key));
        MainActivity.changeSendFileNum();
    }

    private void addFolderSendListAndChangeStyle(ImageView animIv, String key) {
        if (!MyApplication.sendList.containsAll(allFolderMap.get(key))) {
            MyApplication.sendList.addAll(allFolderMap.get(key));
        }
        MainActivity.changeSendFileNum();
        MainActivity.isListOneAnim();
        MainActivity.changeStyle(animIv);
    }

    public boolean onBackPressed() {
        if (mHandledPress > 0) {
            send_all_app_rv.setAdapter(detailAdapter);
            send_all_app_rv.setLayoutManager(new LinearLayoutManager(getContext()));
            send_all_img_tv.setText("图片文件夹(" + allFolderMap.size() + ")");
            mHandledPress--;
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        //将自己的实例传出去
        backHandlerInterface.setSelectedFragment(this);
    }

    public void getFromSystemDB() {
        SystemFilesGetter getter = new SystemFilesGetter(getActivity());
        allImgList.clear();
        allImgList.addAll(getter.getAllImg());
    }

    private void initCheckTag() {
        if (CheckTag.isTagNull(CheckTag.check_tag_img))
            CheckTag.check_tag_img = new int[allImgListCopy.size()];
        if (CheckTag.isTagNull(CheckTag.check_tag_img_folder))
            CheckTag.check_tag_img_folder = new int[allFolderMap.size()];
        initThisCheckTag();
    }

    private void initThisCheckTag() {
        check_tag_img = CheckTag.check_tag_img;
        check_tag_img_folder = CheckTag.check_tag_img_folder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}