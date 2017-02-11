package com.ramo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramo.bean.File;
import com.ramo.file_transfer.R;

import java.util.List;

/**
 * Created by ramo on 2016/5/18.
 */
public class PCDirAdapter extends RecyclerView.Adapter<PCDirAdapter.PCDirHolder> {
    private List<File> dirList;
    private OnClickSendImgListener onItemClickListener;
    private int isDetailed;

    public PCDirAdapter(List<File> list, int isDetailed) {
        this.isDetailed = isDetailed;
        dirList = list;
    }

    @Override
    public PCDirHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (isDetailed == 0)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_dir_pc_item, parent, false);
        else
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_dir_pc_item2, parent, false);
        return new PCDirHolder(v);
    }

    @Override
    public void onBindViewHolder(PCDirHolder holder, int position) {
        File file = dirList.get(position);
        holder.itemView.setTag(position);
        holder.fileName.setText(file.getFileName());
        switch (file.getFlag()) {
            case 0:
                holder.fileImg.setImageResource(R.drawable.x_ic_folde_document);
                break;
            case 1:
                holder.fileImg.setImageResource(R.drawable.x_content_file_pic_more);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dirList.size();
    }

    public void setOnItemClickListener(OnClickSendImgListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class PCDirHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView fileName;
        ImageView fileImg;
        View itemView;

        public PCDirHolder(View itemView) {
            super(itemView);
            fileName = (TextView) itemView.findViewById(R.id.pc_dir_item_tv);
            fileImg = (ImageView) itemView.findViewById(R.id.pc_dir_item_iv);
            this.itemView = itemView;
            this.itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }
}


