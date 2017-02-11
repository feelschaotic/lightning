package com.ramo.utils;

import android.content.ClipboardManager;
import android.content.Context;

import com.ramo.application.MyApplication;

/**
 * 剪贴板工具类
 * Created by ramo on 2016/7/21.
 */

public class ClipboardUtil {
    public static CharSequence read() {
        ClipboardManager clipboardManager = (ClipboardManager) MyApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        return clipboardManager.getText();
    }

    public static void write(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) MyApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(text);
    }
}
