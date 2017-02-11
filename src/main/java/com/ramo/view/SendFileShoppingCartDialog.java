package com.ramo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ramo.adapter.DialogSendFileAdapter;
import com.ramo.adapter.SendAllAppAdapter;
import com.ramo.bean.File;
import com.ramo.file_transfer.R;
import com.ramo.utils.T;

import java.util.List;


public class SendFileShoppingCartDialog extends Dialog {

    public SendFileShoppingCartDialog(Context context) {
        super(context);
    }

    public SendFileShoppingCartDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private View contentView;
        private List<File> fileList;
        private DialogSendFileAdapter adapter;
        private ListView dialog_shopping_cart_lv;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        public void setData(List<File> fileList) {
            this.fileList = fileList;
            adapter = new DialogSendFileAdapter(context, fileList);

        }


        public SendFileShoppingCartDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final SendFileShoppingCartDialog dialog = new SendFileShoppingCartDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_send_file_shopping_cart, null);


            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            dialog_shopping_cart_lv = (ListView) layout.findViewById(R.id.dialog_shopping_cart_lv);
            if (adapter != null) {
                dialog_shopping_cart_lv.setAdapter(adapter);
            }

            dialog.setContentView(layout);
            return dialog;
        }

    }
}
