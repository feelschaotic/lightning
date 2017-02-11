package com.ramo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ramo.bean.File;
import com.ramo.file_transfer.R;
import com.ramo.fragment.CheckTag;
import com.ramo.utils.ImageManageUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ramo on 2016/3/24.
 */
public class SendAllImgDetailAdapter extends RecyclerView.Adapter<SendAllImgDetailAdapter.SendAllImgDetailHolder> {
    private Map<String, List<com.ramo.bean.File>> imgInfoMap;
    private List<String> keyList;
    private OnClickSendImgListener onItemClickListener;
    private Gson gson;

    public SendAllImgDetailAdapter(LinkedHashMap imgList) {
        this.imgInfoMap = imgList;
        Set keySet = imgInfoMap.keySet();
        Iterator it = keySet.iterator();
        keyList = new ArrayList<String>();
        while (it.hasNext()) {
            String key = (String) it.next();
            keyList.add(key);
        }
        gson = new Gson();
    }

    @Override
    public SendAllImgDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_all_img_detail_item, parent, false);
        return new SendAllImgDetailHolder(v);
    }

    @Override
    public void onBindViewHolder(SendAllImgDetailHolder holder, int position) {
        holder.check.setTag(position);
        String key = keyList.get(position);

        holder.fileName.setText(key);

        ArrayList<File> files = (ArrayList<File>) imgInfoMap.get(key);
        if (files != null && files.size() > 0) {
            holder.fileNum.setText("（" + files.size() + "）");
        }

        File info = files.get(0);
        if (info.getFileImg() == null)
            holder.fileImg.setImageResource(R.mipmap.ic_launcher);
        else
            holder.fileImg.setImageBitmap(ImageManageUtil.Byte2Bitmap(info.getFileImg()));

        if (CheckTag.check_tag_img_folder[position] == 1)
            holder.check.setImageResource(R.drawable.common_check_on);

    }

    @Override
    public int getItemCount() {
        return imgInfoMap.size();
    }

    public void setOnItemClickListener(OnClickSendImgListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class SendAllImgDetailHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView fileImg;
        TextView fileName;
        TextView fileNum;
        ImageView check;
        RelativeLayout send_all_img_detail_rl;

        public SendAllImgDetailHolder(View itemView) {
            super(itemView);
            fileImg = (ImageView) itemView.findViewById(R.id.folder_first_img);
            fileImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            fileImg.setPadding(5, 5, 5, 5);

            fileName = (TextView) itemView.findViewById(R.id.send_all_img_detail_fileName);
            fileNum = (TextView) itemView.findViewById(R.id.send_all_img_detail_fileNum);
            check = (ImageView) itemView.findViewById(R.id.send_all_img_check_iv);

            send_all_img_detail_rl = (RelativeLayout) itemView.findViewById(R.id.send_all_img_detail_rl);
            send_all_img_detail_rl.setOnClickListener(this);
            check.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null)
                switch (view.getId()) {
                    case R.id.send_all_img_detail_rl:
                        onItemClickListener.onItemClick(view, (Integer) view.getTag());
                        break;
                    case R.id.send_all_img_check_iv:
                        onItemClickListener.onCheckClick(view, (Integer) view.getTag());
                        break;
                }
        }
    }

}


