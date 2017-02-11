package com.ramo.utils;

import android.content.Context;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SocketHttpServer4 {
    private Context context;

    public void lanchApp(Context context) {
        SendThread th = new SendThread();
        th.start();
    }


    private class SendThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    BroadcastIP();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void BroadcastIP() throws Exception {
            DatagramSocket dgSocket = new DatagramSocket();
            byte b[] = "pc-PC4_Android".getBytes();
            T.showShort(context,"本机ip："+IPUtil.getLocalHostIp());
            DatagramPacket dgPacket = new DatagramPacket(b, b.length, InetAddress.getByName("255.255.255.255"), 9527);
            dgSocket.send(dgPacket);
            dgSocket.close();
            T.showShort(context, "send message is ok.");
        }
    }

}