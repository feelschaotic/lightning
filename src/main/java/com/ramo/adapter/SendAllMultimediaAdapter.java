package com.ramo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramo.bean.Multimedia;
import com.ramo.file_transfer.R;
import com.ramo.fragment.CheckTag;
import com.ramo.utils.ImageManageUtil;
import com.ramo.utils.StringUtil;

import java.util.List;

/**
 * Created by ramo on 2016/3/24.
 */
public class SendAllMultimediaAdapter extends BaseAdapter {
    private Context context;
    private List<Multimedia> imgInfoList;
    private LayoutInflater inflater;
    private OnClickSendImgListener onItemClickListener;
    private boolean tag;
    private boolean isAudioActivity = true;

    public SendAllMultimediaAdapter(Context context, List multimediaList, boolean tag) {
        this.context = context;
        this.imgInfoList = multimediaList;
        this.tag = tag;
        inflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnClickSendImgListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return imgInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return imgInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SendAllMultimediaHolder holder;
        View v;
        if (view == null) {
            v = inflater.inflate(R.layout.send_all_multimedia_item, viewGroup, false);
            holder = new SendAllMultimediaHolder();
            holder.filaImg = (ImageView) v.findViewById(R.id.multImg);

            holder.filaName = (TextView) v.findViewById(R.id.multName);
            holder.filaSize = (TextView) v.findViewById(R.id.multSize);
            holder.multimediaLength = (TextView) v.findViewById(R.id.multimediaLength);
            holder.audio_check = (ImageView) v.findViewById(R.id.audio_check);
            // 把容器和View 关系保存起来
            v.setTag(holder);
        } else {
            v = view;
            holder = (SendAllMultimediaHolder) view.getTag();
        }
        Multimedia info = imgInfoList.get(i);
        if (info.getFileImg() == null) {
            if (tag == isAudioActivity)
                holder.filaImg.setImageDrawable(ImageManageUtil.RToDrawable(R.drawable.file_music));
            else
                holder.filaImg.setImageDrawable(ImageManageUtil.RToDrawable(R.drawable.file_video));
        } else
            holder.filaImg.setImageDrawable(ImageManageUtil.Byte2Drawable(info.getFileImg()));

        holder.audio_check.setTag(i);
        holder.audio_check.setImageResource(R.drawable.common_check_off);

        if (tag == isAudioActivity) {
            if (CheckTag.check_tag_audio[i] == 1)
                holder.audio_check.setImageResource(R.drawable.common_check_on);
        } else {
            if (CheckTag.check_tag_video[i] == 1)
                holder.audio_check.setImageResource(R.drawable.common_check_on);
        }

        holder.filaName.setText(info.getFileName());
        holder.filaSize.setText(StringUtil.formateFileSize(info.getFileSize()));
        holder.multimediaLength.setText(info.getMultimediaLength());

        holder.audio_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onCheckClick(v, (Integer) v.getTag());
            }
        });
        return v;
    }
}

class SendAllMultimediaHolder {
    ImageView filaImg;
    ImageView audio_check;
    TextView filaName;
    TextView filaSize;
    TextView multimediaLength;
}
