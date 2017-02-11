package com.ramo.wifi.nio;

import com.ramo.application.MyApplication;
import com.ramo.asynctask.Task;
import com.ramo.service.SendingFilesService;
import com.ramo.utils.L;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * Created by ramo on 2016/5/22.
 */
public class Server {
    protected final static Logger logger = Logger.getLogger(GroupServer.class
            .getName());
    private NIOUtil nioUtil = new NIOUtil();

    protected void beginSend(SelectionKey readyKey) throws IOException {
        SocketChannel socketChannel = null;
        String string = "";
        try {
            socketChannel = ((ServerSocketChannel) readyKey.channel()).accept();
            StringBuffer buffer = new StringBuffer();
            string = nioUtil.receiveData(socketChannel);
            if (string.equals("filename")) {
                for (Task d : SendingFilesService.sendTaskList) {
                    File f = new File(d.getFileUrl());
                    if (f.exists() && f.isFile()) {
                        buffer.append(d.getFileRealName() + ":" + d.getFileLength() + ",");
                    } else {
                        L.e("file doesn't exist or is not a file");
                    }
                }
                nioUtil.sendData(socketChannel, buffer.toString());
            }
            for (int pos = 0; pos < SendingFilesService.sendTaskList.size(); pos++) {
                if (string.equals(SendingFilesService.sendTaskList.get(pos).getFileRealName())) {
                    nioUtil.sendFile(socketChannel, SendingFilesService.sendTaskList.get(pos), pos);
                }
            }

        } finally {
            MyApplication.isSendingFiles = false;
            socketChannel.close();
        }
    }
}
