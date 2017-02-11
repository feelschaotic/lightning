package com.ramo.wifi.nio;

import android.content.Intent;

import com.ramo.application.MyApplication;
import com.ramo.asynctask.Task;
import com.ramo.file_transfer.R;
import com.ramo.utils.ImageManageUtil;
import com.ramo.utils.L;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class MyClient {

    private NIOUtil nioUtil = new NIOUtil();

    public void init(String ip,Integer port) {
        L.e("客户端启动。。 ip:"+ip);
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            SocketAddress socketAddress;
            if (ip == null || "".equals(ip))
                socketAddress = new InetSocketAddress(
                        NIOConstant.IP, port);
            else
                socketAddress = new InetSocketAddress(
                        ip, port);

            socketChannel.connect(socketAddress);
            beginReceive(socketChannel, socketAddress);

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

    private void beginReceive(SocketChannel socketChannel,
                              SocketAddress socketAddress) throws IOException {
        L.e("beginRece");
        nioUtil.sendData(socketChannel, "filename");
        String string = "";
        string = nioUtil.receiveData(socketChannel);
        String[] fileInfo = string.split(",");

        for (int i = 0; i < fileInfo.length; i++) {
            L.e(fileInfo[i]);
            String[] nameAndSize = fileInfo[i].split(":");
            String fileName = nameAndSize[0];
            Long fileSize = Long.parseLong(nameAndSize[1]);
            if (!fileInfo[i].isEmpty()) {
                socketChannel = SocketChannel.open();
                socketChannel.connect(socketAddress);
                nioUtil.sendData(socketChannel, fileName);
                com.ramo.bean.File file = new com.ramo.bean.File();
                file.setFileUrl(NIOConstant.savePath + fileName);
                file.setFileName(fileName);
                file.setFileSize(fileSize);
                file.setFileImg(ImageManageUtil.drawable2Byte(ImageManageUtil.RToDrawable(R.drawable.count_file_ic_book)));
                Intent intent = new Intent("updateReceivListReceiver");
                intent.putExtra("fileInfo", file);
                MyApplication.getContext().sendBroadcast(intent);

                Task task = new Task(MyApplication.getContext(), file, 1);
                nioUtil.receiveFile(socketChannel, task, i);
            }
        }
    }
}