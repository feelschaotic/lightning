package com.ramo.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ramo.activity.MainActivity;
import com.ramo.adapter.OnClickSendImgListener;
import com.ramo.adapter.SendAllMultimediaAdapter;
import com.ramo.bean.Multimedia;
import com.ramo.file_transfer.R;
import com.ramo.utils.PreferenceUtils;
import com.ramo.utils.SystemFilesGetter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by ramo on 2016/3/10.
 */
@EFragment(R.layout.send_all_multimedia)
public class AllVideoFragment extends Fragment {

    @ViewById
    ListView send_all_multimedia_lv;
    @ViewById
    TextView send_all_multimedia_tv;
    private List<Multimedia> allVideoList;
    SystemFilesGetter getter;
    Gson gson;
    private String preName = "videoList";
    private SendAllMultimediaAdapter multimediaAdapter;
    private int[] check_tag_video;
    @ViewById
    ImageView send_all_multimedia_check_iv;
    private boolean isCheckAll=false;

    @AfterViews
    public void initData() {
        gson = new Gson();
        getFromSharedPre();
        if (allVideoList == null || allVideoList.size() == 0) {
            //若没有 再去系统数据库取
            getFromSystemDB();
            getFromSharedPre();
        }
        initCheckTag();
        boolean isAudioActivity = false;
        multimediaAdapter = new SendAllMultimediaAdapter(getContext(),
                allVideoList, isAudioActivity);
        send_all_multimedia_lv.setAdapter(multimediaAdapter);
        initView();
        initListener();
    }

    private void initListener() {
        multimediaAdapter.setOnItemClickListener(new OnClickSendImgListener() {
            @Override
            public void onItemClick(View v, Integer tag) {
            }

            @Override
            public void onCheckClick(View v, Integer tag) {
                ImageView check = (ImageView) v;
                if (check_tag_video[tag] == 0) {
                    check.setImageResource(R.drawable.common_check_on);
                    check_tag_video[tag] = 1;
                    ImageView multImg = (ImageView) ((RelativeLayout) v.getParent()).findViewById(R.id.multImg);
                    MainActivity.addSendListAndChangeStyle(multImg, allVideoList.get(tag));
                } else {
                    check.setImageResource(R.drawable.common_check_off);
                    check_tag_video[tag] = 0;
                    MainActivity.removeSendListAndChangeStyle(allVideoList.get(tag));
                }
            }
        });

        send_all_multimedia_check_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCheckAll) {
                    send_all_multimedia_check_iv.setImageResource(R.drawable.common_check_on);
                    for (int i = 0; i < CheckTag.check_tag_video.length; i++) {
                        CheckTag.check_tag_video[i] = 1;
                        MainActivity.addSendListAndChangeStyle(null, allVideoList.get(i));
                    }

                } else {
                    send_all_multimedia_check_iv.setImageResource(R.drawable.common_check_off);
                    for (int i = 0; i < CheckTag.check_tag_video.length; i++) {
                        CheckTag.check_tag_video[i] = 0;
                        MainActivity.removeSendListAndChangeStyle(allVideoList.get(i));
                    }

                }
                multimediaAdapter.notifyDataSetChanged();
                isCheckAll = !isCheckAll;
            }
        });
    }

    protected void initView() {
        send_all_multimedia_tv.setText("全部视频(" + allVideoList.size() + ")");
    }

    public void getFromSystemDB() {
        getter = new SystemFilesGetter(getActivity());
        //存在Pre里
        PreferenceUtils.setPrefString(preName, gson.toJson(getter.getAllVideo()));
    }

    public void getFromSharedPre() {

        String appListJson = PreferenceUtils.getPrefString(preName);
        allVideoList = gson.fromJson(appListJson, new TypeToken<List<Multimedia>>() {
        }.getType());
    }

    private void initCheckTag() {
        if (CheckTag.isTagNull(CheckTag.check_tag_video))
            CheckTag.check_tag_video = new int[allVideoList.size()];
        check_tag_video = CheckTag.check_tag_video;
    }
}
