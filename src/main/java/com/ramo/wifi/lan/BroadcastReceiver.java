package com.ramo.wifi.lan;

import com.ramo.utils.IPUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class BroadcastReceiver {
    private static OnSearchListener onSearchListener;
    private static boolean isStop = false;

    public static void setOnSearchListener(OnSearchListener onSearchListener) {
        BroadcastReceiver.onSearchListener = onSearchListener;
    }

    public static void stopReceiver() {
        isStop = true;
    }

    public static void lanchApp() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (!isStop) {
                    try {
                        receiveIP();
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private static void receiveIP() throws Exception {
        DatagramSocket dgSocket = new DatagramSocket(9527);
        byte[] by = new byte[1024];
        DatagramPacket packet = new DatagramPacket(by, by.length);
        dgSocket.receive(packet);

        String str = new String(packet.getData(), 0, packet.getLength());

        System.out.println("接收到数据大小:" + str.length());
        String result = new String(str.getBytes(), "utf-8");
        System.out.println("接收到的数据为：" + result);

        if (onSearchListener != null) {
            String[] split = result.split("/");
            if (!(split[1].equals(IPUtil.getLocalHostIp()))) {
                onSearchListener.onSuess(split[1], split[0]);
            }
        }
        dgSocket.close();
        // System.out.println("recevied message is ok.");
    }

}
