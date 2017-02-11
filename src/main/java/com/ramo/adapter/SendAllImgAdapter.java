package com.ramo.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ramo.bean.File;
import com.ramo.cache.ImageLoader;
import com.ramo.file_transfer.R;
import com.ramo.fragment.CheckTag;
import com.ramo.utils.StringUtil;

import java.util.List;

/**
 * Created by ramo on 2016/3/24.
 */
public class SendAllImgAdapter extends RecyclerView.Adapter<SendAllImgAdapter.SendAllImgHolder> {
    public static String[] byteStrs;
    private List<File> imgInfoList;
    private OnClickSendImgListener onItemClickListener;
    private ImageLoader mImageLoader;
    private boolean mFirst = true;//是否第一次启动
    private RecyclerView recyclerView;
    private int dataSize;

    public SendAllImgAdapter(final List<File> imgList, RecyclerView recyclerView) {
        this.imgInfoList = imgList;
        this.recyclerView = recyclerView;
        newImageLoader(imgList, this.recyclerView);


        recyclerView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                mStart = layoutManager.findFirstVisibleItemPosition();
                int tempEnd = layoutManager.findLastVisibleItemPosition() + 1;

                mEnd = tempEnd > dataSize ? dataSize : tempEnd;

                //第一次显示调用
                if (mFirst) {
                    mImageLoader.loadImages(mStart, mEnd);
                    mFirst = false;
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //停止滚动时加载可见项
                    if (dataSize != 1)
                        mImageLoader.loadImages(mStart, mEnd);
                } else {
                    //停止加载
                    mImageLoader.cancelAllTasks();
                }

            }
        });
        mFirst = true;
    }

    private void newImageLoader(List<File> imgList, RecyclerView recyclerView) {
        mFirst = true;

        mImageLoader = new ImageLoader(recyclerView);
        dataSize = imgList.size();
        byteStrs = new String[dataSize];
        for (int i = 0; i < dataSize; i++) {
            byte[] fileImg = imgList.get(i).getFileImg();
            if (fileImg == null)
                byteStrs[i] = "";
            else
                byteStrs[i] = StringUtil.byteToStr(fileImg);
        }
        if(dataSize==1)
            mImageLoader.loadImages(0,1);
    }

    public void beforeNotifyAndChangeImgLoader(List<File> imgList) {
        newImageLoader(imgList, recyclerView);
    }

    @Override
    public SendAllImgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_all_img_item, parent, false);
        return new SendAllImgHolder(v);
    }

    @Override
    public void onBindViewHolder(final SendAllImgHolder holder, final int position) {
        File info = imgInfoList.get(position);
        holder.send_all_img_check.setVisibility(View.GONE);

        holder.send_all_img_check.setTag(position);
        holder.iv.setTag(byteStrs[position]);
        byte[] fileImg = info.getFileImg();
        if (fileImg != null)
            mImageLoader.showImageByAsynctask(holder.iv, fileImg);


        if (CheckTag.check_tag_img[position] == 1)
            holder.send_all_img_check.setVisibility(View.VISIBLE);
    }

    public void setOnItemClickListener(OnClickSendImgListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return imgInfoList.size();
    }

    //起始项和结束项（不含）
    private int mStart, mEnd;


    class SendAllImgHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv;
        ImageView send_all_img_check;

        public SendAllImgHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.imageView);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setPadding(5, 5, 5, 5);
            iv.setOnClickListener(this);
            send_all_img_check = (ImageView) itemView.findViewById(R.id.send_all_img_check);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                ImageView check = (ImageView) ((LinearLayout) view.getParent()).findViewById(R.id.send_all_img_check);
                onItemClickListener.onItemClick(view, (Integer) check.getTag());
            }
        }
    }


}


