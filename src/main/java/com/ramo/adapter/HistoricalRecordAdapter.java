package com.ramo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ramo.bean.File;
import com.ramo.bean.HistoricalRecord;
import com.ramo.bean.State;
import com.ramo.database.RecordDao;
import com.ramo.database.RecordDaoImpl;
import com.ramo.file_transfer.R;
import com.ramo.utils.ImageManageUtil;
import com.ramo.utils.StringUtil;

import java.util.List;


public class HistoricalRecordAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<HistoricalRecord> records;
    private List<HistoricalRecord> allRecord;

    public HistoricalRecordAdapter(Context context, List<HistoricalRecord> records) {
        inflater = LayoutInflater.from(context);
        this.records = records;
        RecordDao recordDao = new RecordDaoImpl(context);
        allRecord = recordDao.findAllRecord();
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int arg0) {
        return records.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoricalRecord record = records.get(position);
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            if (isRecordFromMe(position)) {
                convertView = inflater.inflate(R.layout.activity_record_historical_item_to, parent, false);
                convertView.findViewById(R.id.historical_record_details_rl).setBackgroundResource(R.drawable.chatto_bg_normal);
                vh.date = (TextView) convertView.findViewById(R.id.item_to_msg_date);
                vh.userName = (TextView) convertView.findViewById(R.id.item_to_msg);
            } else {
                convertView = inflater.inflate(R.layout.activity_record_historical_item_form, parent, false);
                convertView.findViewById(R.id.historical_record_details_rl).setBackgroundResource(R.drawable.chatfrom_bg_focused);
                vh.date = (TextView) convertView.findViewById(R.id.item_form_msg_date);
                vh.userName = (TextView) convertView.findViewById(R.id.item_form_msg);
            }

            vh.fileName = (TextView) convertView.findViewById(R.id.record_file_name);
            vh.fileSize = (TextView) convertView.findViewById(R.id.record_file_size);
            vh.record_receiverName = (TextView) convertView.findViewById(R.id.record_receiverName);
            vh.fileImg = (ImageView) convertView.findViewById(R.id.record_fileImg);
            vh.record_handle_btn = (ImageView) convertView.findViewById(R.id.record_handle_btn);
            vh.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            vh.record_state = (TextView) convertView.findViewById(R.id.record_file_state);

            convertView.setTag(vh);
        } else
            vh = (ViewHolder) convertView.getTag();

        vh.date.setText(record.getDate().toLocaleString());
        vh.progressBar.setMax(100);
        vh.userName.setText(record.getSender());

        File file = record.getFile();
        if (file != null) {
            vh.fileName.setText(file.getFileName());
            vh.fileSize.setText(StringUtil.formateFileSize(file.getFileSize()));
            if (file.getFileImg() != null)
                vh.fileImg.setImageDrawable(ImageManageUtil.Byte2Drawable(file.getFileImg()));
            else
                vh.fileImg.setImageResource(R.mipmap.ic_launcher);

            vh.progressBar.setProgress(file.getFinished());
            vh.progressBar.setVisibility(View.VISIBLE);
            vh.record_handle_btn.setVisibility(View.VISIBLE);

            if (record.getState() == State.FINISH) {
                vh.progressBar.setVisibility(View.INVISIBLE);
                vh.record_state.setText("完成");
                vh.record_handle_btn.setVisibility(View.INVISIBLE);
            } else if (record.getState() == State.DOWNLOADING) {
                vh.record_state.setText(file.getFinished() + "%");
                vh.record_handle_btn.setImageResource(R.drawable.download_pause);
            } else if (record.getState() == State.WAIT) {
                vh.record_state.setText("等待中");
                vh.record_handle_btn.setImageResource(R.drawable.download_cancel);
            } else if (record.getState() == State.PAUSE) {
                vh.record_state.setText("暂停");
                vh.record_handle_btn.setImageResource(R.drawable.download_continue);
            } else if (record.getState() == State.ERROR) {
                vh.record_state.setText("下载失败");
                vh.record_handle_btn.setImageResource(R.drawable.download_cancel);
            }
        }


        if (isRecordFromMe(position))
            vh.record_receiverName.setText("发送给：" + record.getReceiver());
        else
            vh.record_receiverName.setText(record.getSender() + "传来：");


        return convertView;
    }

    private boolean isRecordFromMe(int position) {
        return getItemViewType(position) == 1;
    }

    @Override
    public int getItemViewType(int position) {

        HistoricalRecord record = records.get(position);
        if (record.getType() == HistoricalRecord.Type.INCOME)
            return 0;
        return 1;
    }

    /**
     * 更新列表项中的进度条
     *
     * @param id
     * @param progress
     */
    public void updateProgress(int id, int progress) {
        if(id + allRecord.size()>records.size())
            return;
        HistoricalRecord record = records.get(id + allRecord.size());
        if (progress == 100)
            record.setState(State.FINISH);
        else if (progress > 0)
            record.setState(State.DOWNLOADING);
        File file = record.getFile();
        if (file != null)
            file.setFinished(progress);
        notifyDataSetChanged();
    }


    private final class ViewHolder {
        TextView date;
        TextView fileName;
        TextView fileSize;
        TextView record_receiverName;
        TextView record_state;
        TextView userName;
        ImageView fileImg;
        ImageView record_handle_btn;
        ProgressBar progressBar;

    }

}
