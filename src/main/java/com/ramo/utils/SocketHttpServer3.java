package com.ramo.utils;

import android.content.Context;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class SocketHttpServer3 {
    private Context context;

    public void lanchApp(Context context) {
        receiveThread th = new receiveThread();
        th.start();
    }

    private class receiveThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    receiveIP();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void receiveIP() throws Exception {
            DatagramSocket dgSocket = new DatagramSocket(9527);
            byte[] by = new byte[1024];
            DatagramPacket packet = new DatagramPacket(by, by.length);
            dgSocket.receive(packet);

            String str = new String(packet.getData(), 0, packet.getLength());

            T.showShort(context, "接收到数据大小:" + str.length());
            T.showShort(context, "接收到的数据为：" + new String(str.getBytes(), "utf-8"));
            dgSocket.close();
            T.showShort(context, "recevied message is ok.");
        }
    }


}