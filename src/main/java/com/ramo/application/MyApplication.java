package com.ramo.application;

import android.app.Application;
import android.content.Context;

import com.ramo.bean.File;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ramo on 2016/3/25.
 */
public class MyApplication extends Application {
    private static Context context;
    public static boolean isSendingFiles = false;
    public static List<File> sendList = new ArrayList<File>();
    public static List<String> groupList = new ArrayList<String>();
    public final static String INTENT_EXTRA_NAME = "sendFiles";
    public static boolean isDiect ;
    public static boolean connectSuccessd = false;
    public static String junior_address;

    @Override
    public void onCreate() {
        groupList.add("192.168.191.1");
        groupList.add("10.0.0.27");
        super.onCreate();
        context = getApplicationContext();
    }


    public static Context getContext() {
        return context;
    }


}
