package com.ramo.wifi.nio;

import android.content.Intent;

import com.ramo.application.MyApplication;
import com.ramo.asynctask.Task;
import com.ramo.service.SendingFilesService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NIOUtil {

    public String receiveData(SocketChannel socketChannel) throws IOException {
        String string = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        try {
            byte[] bytes;
            int size = 0;
            while ((size = socketChannel.read(buffer)) >= 0) {
                buffer.flip();
                bytes = new byte[size];
                buffer.get(bytes);
                baos.write(bytes);
                buffer.clear();
            }
            bytes = baos.toByteArray();
            string = new String(bytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return string;
    }

    public void sendData(SocketChannel socketChannel, String string)
            throws IOException {
        byte[] bytes = string.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        socketChannel.write(buffer);
        socketChannel.socket().shutdownOutput();
    }

    public void receiveFile(SocketChannel socketChannel, Task task, int pos)
            throws IOException {
        Intent intent = new Intent();
        intent.setAction(SendingFilesService.ACTION_UPDATE);

        FileOutputStream fos = null;
        FileChannel channel = null;

        try {
            fos = new FileOutputStream(task.getFileUrl());
            channel = fos.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            int size = 0;
            long time = System.currentTimeMillis();
            while ((size = socketChannel.read(buffer)) != -1) {
                buffer.flip();
                if (size > 0) {
                    buffer.limit(size);
                    channel.write(buffer);
                    task.setFinished(task.getFinished() + size);
                    buffer.clear();

                    sendUIBroadcast(task, pos, intent, time);
                }
            }
            task.delete();
            sendEndBroadcast(pos, task.getFile());

        } finally {
            try {
                channel.close();
                fos.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void sendFile(SocketChannel socketChannel, Task task, int pos)
            throws IOException {
        Intent intent = new Intent();
        intent.setAction(SendingFilesService.ACTION_UPDATE);

        File file = new File(task.getFileUrl());
        FileInputStream fis = null;
        FileChannel channel = null;
        try {
            fis = new FileInputStream(file);
            channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            int size = 0;
            long time = System.currentTimeMillis();
            while ((size = channel.read(buffer)) != -1) {

                buffer.rewind();
                buffer.limit(size);
                socketChannel.write(buffer);
                task.setFinished(task.getFinished() + size);
                buffer.clear();

                sendUIBroadcast(task, pos, intent, time);

                if (task.isPause) {
                    task.updateProgress();
                    return;
                }
            }
            socketChannel.socket().shutdownOutput();
            task.delete();
            sendEndBroadcast(pos, task.getFile());

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                channel.close();
                fis.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void sendUIBroadcast(Task task, int pos, Intent intent, long time) {
        if (System.currentTimeMillis() - time > 500) {//减少UI负载
            time = System.currentTimeMillis();
            intent.putExtra("finished", (int) (task.getFinished() * 100 / task.getFileLength()));
            intent.putExtra("id", pos);
            MyApplication.getContext().sendBroadcast(intent);
        }
    }

    private void sendEndBroadcast(int pos, com.ramo.bean.File f) {
        Intent intent1 = new Intent(SendingFilesService.ACTION_FINISHED);
        intent1.putExtra("id", pos);
        intent1.putExtra("isInOrOut", 1);
        intent1.putExtra("fileInfo", f);
        MyApplication.getContext().sendBroadcast(intent1);
    }
}
