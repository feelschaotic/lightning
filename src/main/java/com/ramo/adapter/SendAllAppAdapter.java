package com.ramo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ramo.bean.AppInfo;
import com.ramo.file_transfer.R;
import com.ramo.fragment.CheckTag;
import com.ramo.utils.ImageManageUtil;
import com.ramo.utils.StringUtil;

import java.util.List;

/**
 * Created by ramo on 2016/3/24.
 */
public class SendAllAppAdapter extends BaseAdapter {
    private Context context;
    private List<AppInfo> appList;
    private LayoutInflater inflater;
    private Gson gson;

    public SendAllAppAdapter(Context context, List<AppInfo> nonSystemAppInfo) {
        this.context = context;
        this.appList = nonSystemAppInfo;
        gson = new Gson();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public Object getItem(int i) {
        return appList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SendAllAppHolder holder;
        View v;
        if (view == null) {

            v = inflater.inflate(R.layout.send_all_app_item, viewGroup, false);
            holder = new SendAllAppHolder();
            holder.send_all_app_appImg = (ImageView) v.findViewById(R.id.imageView);
            holder.send_all_app_check = (ImageView) v.findViewById(R.id.send_all_app_check);
            holder.send_all_app_appName = (TextView) v.findViewById(R.id.send_all_app_appName);
            holder.send_all_app_appSize = (TextView) v.findViewById(R.id.send_all_app_appSize);
            // 把容器和View 关系保存起来
            v.setTag(holder);
        } else {
            v = view;
            holder = (SendAllAppHolder) view.getTag();
        }

        AppInfo info = appList.get(i);

        if (info.getFileImg() == null)
            holder.send_all_app_appImg.setImageResource(R.mipmap.ic_launcher);
        else
            holder.send_all_app_appImg.setImageDrawable(ImageManageUtil.Byte2Drawable(info.getFileImg()));
        holder.send_all_app_appName.setText(info.getFileName());

        holder.send_all_app_appSize.setText(StringUtil.formateFileSize(info.getFileSize()));
        holder.send_all_app_check.setVisibility(View.INVISIBLE);

        if (CheckTag.check_tag_app[i] == 1)
            holder.send_all_app_check.setVisibility(View.VISIBLE);

        return v;
    }

}

class SendAllAppHolder {
    ImageView send_all_app_appImg;
    ImageView send_all_app_check;
    TextView send_all_app_appName;
    TextView send_all_app_appSize;
}
