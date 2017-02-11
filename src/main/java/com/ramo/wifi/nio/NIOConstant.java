package com.ramo.wifi.nio;

import android.os.Environment;

public class NIOConstant {
    public static  String IP = "192.168.1.144";//手机热点连上电脑的ip
    public static final int PORT = 1991;
    public static final int PORT2 = 9527;
    public static final String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";//接收到的文件的保存地址
    public static final int PORT_Clip = 9000;
}
