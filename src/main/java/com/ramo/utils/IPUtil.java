package com.ramo.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.ramo.application.MyApplication;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by ramo on 2016/4/27.
 */
public class IPUtil {
    public static String getPhoneName(){
        return new Build().MODEL;
    }
    public static String getLocalHostIp()
    {
        String ipaddress = "";
        try
        {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements())
            {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements())
                {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ip
                            .getHostAddress()))
                    {
                        return ip.getHostAddress();
                    }
                }

            }
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }
        return ipaddress;

    }

    // 得到本机Mac地址
    public static  String getLocalMac()
    {
        String mac = "";
        // 获取wifi管理器
        WifiManager wifiMng = (WifiManager) MyApplication.getContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfor = wifiMng.getConnectionInfo();
        mac = "本机的mac地址是：" + wifiInfor.getMacAddress();
        return mac;
    }
}
