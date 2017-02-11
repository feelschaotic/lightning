package com.ramo.fragment;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ramo.activity.MainActivity;
import com.ramo.adapter.SendAllAppAdapter;
import com.ramo.bean.AppInfo;
import com.ramo.file_transfer.R;
import com.ramo.utils.L;
import com.ramo.utils.PackageSizeUtil;
import com.ramo.utils.PreferenceUtils;
import com.ramo.utils.SystemFilesGetter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramo on 2016/3/10.
 */
@EFragment(R.layout.send_all_app)
public class AllAppFragment extends Fragment {

    @ViewById
    TextView send_all_app_tv;
    @ViewById
    GridView send_all_app_gv;

    private List<AppInfo> willSendApps;
    public static List<AppInfo> nonSystemAppList;

    @ViewById
    ImageView send_all_app_check_iv;
    private static boolean checkAll = true;
    public static SendAllAppAdapter sendAllAppAdapter;
    private String preName = "appList";
    private Gson gson;
    private int[] check_tag_app;

    private void changeStyle(int R) {
        send_all_app_check_iv.setImageResource(R);
    }

    SystemFilesGetter getter;

    @AfterViews
    protected void init() {
        gson = new Gson();

        initData();

        send_all_app_tv.setText("全部应用(" + nonSystemAppList.size() + ")");

        willSendApps = new ArrayList<AppInfo>();
        initListener();
    }

    private void initData() {

        //先判断SharedPreference里有没有
        getFromSharedPre();
        if (nonSystemAppList == null || nonSystemAppList.size() == 0) {
            //若没有 再去系统数据库取
            getFromSystemDB();
            initAppSize();
        }
        initCheckTag();
        sendAllAppAdapter = new SendAllAppAdapter(getContext(), nonSystemAppList);
        send_all_app_gv.setAdapter(sendAllAppAdapter);
    }

    private void initListener() {
        send_all_app_check_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAll) {
                    changeStyle(R.drawable.common_check_on);
                    willSendApps.addAll(nonSystemAppList);
                } else {
                    changeStyle(R.drawable.common_check_off);
                    willSendApps.removeAll(nonSystemAppList);
                }
                checkAll = !checkAll;
            }
        });
        send_all_app_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout linearLayout = (LinearLayout) view;
                ImageView appImg = (ImageView) linearLayout.getChildAt(0);
                ImageView check = (ImageView) linearLayout.getChildAt(1);
                if (check_tag_app[position] == 0) {
                    check.setVisibility(View.VISIBLE);
                    check_tag_app[position] = 1;
                    MainActivity.addSendListAndChangeStyle(appImg, nonSystemAppList.get(position));
                } else {
                    check.setVisibility(View.INVISIBLE);
                    check_tag_app[position] = 0;
                    MainActivity.removeSendListAndChangeStyle(nonSystemAppList.get(position));
                }
            }
        });
    }

    public void getFromSystemDB() {
        L.e("从系统数据库取");
        getter = new SystemFilesGetter(getActivity());
        nonSystemAppList = getter.getNonSystemAppInfo();
    }

    public void getFromSharedPre() {

        String appListJson = PreferenceUtils.getPrefString(preName);
        nonSystemAppList = gson.fromJson(appListJson, new TypeToken<List<AppInfo>>() {
        }.getType());
        L.e("从SharedPre取");
    }

    private void initCheckTag() {
        check_tag_app = CheckTag.check_tag_app;
        if (CheckTag.isTagNull(check_tag_app))
            CheckTag.check_tag_app = new int[nonSystemAppList.size()];
        check_tag_app = CheckTag.check_tag_app;
    }

    private void initAppSize() {
        new AppSizeAsyncTask().execute();
    }

    class AppSizeAsyncTask extends AsyncTask<Void, Void, Void> {
        PackageSizeUtil packageSizeUtil = new PackageSizeUtil(getContext());

        @Override
        protected Void doInBackground(Void... params) {
            for (AppInfo app : nonSystemAppList) {
                try {
                    L.e("app.PackageName:" + app.getPackageName());
                    packageSizeUtil.getPacakgeSize(app.getPackageName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            List<Long> allAppSizeList = packageSizeUtil.getAllAppSizeList();
            L.e("allAppSizeList size" + allAppSizeList.size());
            for (int i = 0; i < allAppSizeList.size(); i++) {
                nonSystemAppList.get(i).setFileSize(allAppSizeList.get(i));
            }
            PreferenceUtils.setPrefString(preName, gson.toJson(nonSystemAppList));
            getFromSharedPre();

            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
