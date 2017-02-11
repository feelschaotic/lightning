package com.ramo.activity;

import android.app.Activity;
import android.content.Intent;
import android.webkit.WebView;

import com.ramo.file_transfer.R;
import com.ramo.utils.ServerUtil;
import com.ramo.utils.T;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by  2016/8/16.
 */
@EActivity(R.layout.activity_web_service)
public class WebServiceActivity extends Activity {
    @ViewById
    WebView pc_web_view;

    @AfterViews
    public void init() {
        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra(ServerUtil.URL);
            if (url != null){

                pc_web_view.loadUrl(url);
                pc_web_view.getSettings().setJavaScriptEnabled(true);
            }
            else
                T.showShort(this, getResources().getString(R.string.load_error));
        }
    }
}
