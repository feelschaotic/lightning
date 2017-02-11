package com.ramo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ramo.file_transfer.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ramo on 2016/7/23.
 */
public class WiFiDirectAdapter extends RecyclerView.Adapter<WiFiDirectAdapter.WiFiDirectHolder> {
    private Context context;
    private List<HashMap<String, String>> peer;

    public WiFiDirectAdapter(Context context, List peer) {
        this.context = context;
        this.peer = peer;
    }

    @Override
    public WiFiDirectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_wifi_direct_item,
                parent, false);
        return new WiFiDirectHolder(view);
    }

    @Override
    public void onBindViewHolder(final WiFiDirectHolder holder, final int position) {
        holder.tvname.setText(peer.get(position).get("name"));
        holder.tvaddress.setText(peer.get(position).get("address"));

        if (mOnItemClickListener != null) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.OnItemClick(holder.itemView, position);
                }

            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.OnItemLongClick(holder.itemView, position);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return peer.size();
    }

    public void addData(int position, HashMap map) {
        peer.add(position, map);
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        peer.remove(position);
        notifyItemRemoved(position);
    }

    public void RefreshView() {
        for (int i = 0; i < getItemCount(); i++)
            removeData(i);
        for (int i = 0; i < getItemCount(); i++)
            addData(i, peer.get(i));
    }


    public class WiFiDirectHolder extends RecyclerView.ViewHolder {
        public TextView tvname;
        public TextView tvaddress;

        public WiFiDirectHolder(View View) {
            super(View);
            tvname = (TextView) View.findViewById(R.id.tv_name);
            tvaddress = (TextView) View.findViewById(R.id.tv_address);

        }
    }

     /*
    * 设置回调接口，实现点击与长按
    * */

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);

        void OnItemLongClick(View view, int position);
    }

    public OnItemClickListener mOnItemClickListener;

    public void SetOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

}
