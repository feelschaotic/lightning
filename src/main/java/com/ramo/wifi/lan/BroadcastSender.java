package com.ramo.wifi.lan;

import com.ramo.utils.IPUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastSender {
    private static boolean isStop = false;

    public static void stopSend() {
        isStop = true;
    }

    public static void lanchApp() {
        SendThread th = new SendThread();
        th.start();
    }

    private static class SendThread extends Thread {
        @Override
        public void run() {
            while (!isStop) {
                try {
                    BroadcastIP();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        private void BroadcastIP() throws Exception {
            DatagramSocket dgSocket = new DatagramSocket();
            byte b[] = new String(IPUtil.getPhoneName() + "/" + IPUtil.getLocalHostIp()).getBytes();

            DatagramPacket dgPacket = new DatagramPacket(b, b.length,
                    InetAddress.getByName("255.255.255.255"), 9527);
            dgSocket.send(dgPacket);
            dgSocket.close();
            // System.out.println("send message is ok.");
        }
    }
}
