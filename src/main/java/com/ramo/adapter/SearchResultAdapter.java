package com.ramo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramo.bean.File;
import com.ramo.file_transfer.R;
import com.ramo.utils.ImageManageUtil;

import java.util.List;

/**
 * Created by ramo on 2016/4/19.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultHolder> {
    private List<File> resultList;

    public SearchResultAdapter(List imgList) {
        this.resultList = imgList;
    }

    @Override
    public SearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_file_result_detail_item, parent, false);
        return new SearchResultHolder(v);
    }

    @Override
    public void onBindViewHolder(SearchResultHolder holder, int position) {
        File info = (File) resultList.get(position);

        if (info.getFileImg() == null)
            holder.search_imageView.setImageResource(R.mipmap.ic_launcher);
        else
            holder.search_imageView.setImageDrawable(ImageManageUtil.Byte2Drawable(info.getFileImg()));

        holder.search_fileName.setText(info.getFileName());
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }


}
class SearchResultHolder extends RecyclerView.ViewHolder {
    ImageView search_imageView;
    TextView search_fileName;

    public SearchResultHolder(View itemView) {
        super(itemView);
        search_imageView = (ImageView) itemView.findViewById(R.id.search_imageView);
        search_imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        search_imageView.setPadding(5, 5, 5, 5);

        search_fileName = (TextView) itemView.findViewById(R.id.search_fileName);
    }
}