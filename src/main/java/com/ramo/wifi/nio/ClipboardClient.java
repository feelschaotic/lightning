package com.ramo.wifi.nio;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class ClipboardClient {

    private NIOUtil nioUtil = new NIOUtil();

    public void init(String clipboardContent,String ip) {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            SocketAddress socketAddress = new InetSocketAddress(ip,
                    NIOConstant.PORT_Clip);
            socketChannel.connect(socketAddress);
            nioUtil.sendData(socketChannel, clipboardContent);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (socketChannel != null)
                    socketChannel.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}