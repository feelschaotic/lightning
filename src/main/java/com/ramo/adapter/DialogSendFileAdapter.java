package com.ramo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ramo.activity.MainActivity;
import com.ramo.application.MyApplication;
import com.ramo.bean.File;
import com.ramo.file_transfer.R;
import com.ramo.fragment.AllAppFragment;
import com.ramo.fragment.AllImgFragment;
import com.ramo.fragment.CheckTag;
import com.ramo.utils.ImageManageUtil;
import com.ramo.utils.StringUtil;

import java.util.List;

/**
 * Created by ramo on 2016/4/8.
 */
public class DialogSendFileAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<File> fileList;
    private Gson gson = new Gson();

    public DialogSendFileAdapter(Context context, List<File> fileList) {
        this.fileList = fileList;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return fileList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        DialogSendFileHolder holder;
        View v;
        if (convertView == null) {
            v = inflater.inflate(R.layout.dialog_send_file_shopping_cart_item, parent, false);
            holder = new DialogSendFileHolder();
            holder.filaImg = (ImageView) v.findViewById(R.id.send_file_fileImg);
            holder.filaImg.setScaleType(ImageView.ScaleType.CENTER);
            holder.filaName = (TextView) v.findViewById(R.id.send_file_fileName);
            holder.filaSize = (TextView) v.findViewById(R.id.send_file_fileSize);
            holder.deleteIt = (ImageView) v.findViewById(R.id.send_file_delete_iv);
            // 把容器和View 关系保存起来
            v.setTag(holder);
        } else {
            v = convertView;
            holder = (DialogSendFileHolder) convertView.getTag();
        }
        final File info = fileList.get(position);

        if (info.getFileImg() == null)
            holder.filaImg.setImageResource(R.drawable.file_img_small);
        else
            holder.filaImg.setImageDrawable(ImageManageUtil.Byte2Drawable(info.getFileImg()));

        holder.filaName.setText(info.getFileName());
        holder.filaSize.setText(StringUtil.formateFileSize(info.getFileSize()));
        holder.deleteIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileList.remove(position);
                notifyDataSetChanged();

                MyApplication.sendList.remove(position);
                int positionInImgList = AllImgFragment.allImgListCopy.indexOf(info);
                if (positionInImgList == -1) {
                    positionInImgList = AllAppFragment.nonSystemAppList.indexOf(info);
                    if (positionInImgList != -1) {
                        CheckTag.check_tag_app[positionInImgList] = 0;
                        AllAppFragment.sendAllAppAdapter.notifyDataSetChanged();
                    }
                } else {
                    CheckTag.check_tag_img[positionInImgList] = 0;
                    AllImgFragment.imgAdapter.notifyDataSetChanged();
                }
                MainActivity.changeSendFileNum();

            }
        });

        return v;
    }
}

class DialogSendFileHolder {
    ImageView filaImg;
    TextView filaName;
    TextView filaSize;
    ImageView deleteIt;
}
