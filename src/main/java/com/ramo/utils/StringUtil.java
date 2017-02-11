package com.ramo.utils;

import android.text.format.Formatter;

import com.ramo.application.MyApplication;

import java.io.UnsupportedEncodingException;

/**
 * Created by ramo on 2016/4/21.
 */
public class StringUtil {


    public static String getFolderNameFromUrl(String url) {
        String folderName = null;
        int i = 0;
        while (i < 2) {
            int lastFirst = url.lastIndexOf('/');
            folderName = url.substring(lastFirst + 1);
            url = url.substring(0, lastFirst);
            i++;
        }
        return folderName;
    }

    //系统函数，字符串转换 long -String (kb)
    public static String formateFileSize(long size) {
        return Formatter.formatFileSize(MyApplication.getContext(), size);
    }

    public static byte[] strToByte(String byteStr) {
        try {
            return byteStr.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String byteToStr(byte[] bytes) {
        try {
            return new String(bytes, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
