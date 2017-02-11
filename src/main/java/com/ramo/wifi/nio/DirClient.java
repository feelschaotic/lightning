package com.ramo.wifi.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class DirClient {

    private NIOUtil nioUtil = new NIOUtil();

    public String  init(String nowDir) throws Exception {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            SocketAddress socketAddress = new InetSocketAddress(NIOConstant.IP,
                    NIOConstant.PORT);
            socketChannel.connect(socketAddress);

            return beginReceive(socketChannel, nowDir);

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
        return null;
    }

    private String beginReceive(SocketChannel socketChannel, String nowDir) throws IOException {
        nioUtil.sendData(socketChannel, "dirPath:" + nowDir);
        String string = nioUtil.receiveData(socketChannel);
        return string;
    }

}