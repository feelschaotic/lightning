package com.ramo.wifi;

import android.os.Environment;

import com.ramo.utils.IPUtil;
import com.ramo.utils.L;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by ramo on 2016/4/29.
 */
public class UDPReceive {


    static String ip = "192.168.191.1";
    static int portNum = 1991;
    static String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";

/*    public static void receiveFileFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SocketChannel socketChannel = null;
                try {
                    socketChannel = SocketChannel.open();

                    SocketAddress socketAddress = new InetSocketAddress(ip, portNum);
                    socketChannel.connect(socketAddress);

                    sendData(socketChannel, "filename");
                    String string = "";
                    string = receiveData(socketChannel);
                    if (!string.isEmpty()) {
                        socketChannel = SocketChannel.open();
                        socketChannel.connect(new InetSocketAddress(ip, portNum));
                        sendData(socketChannel, string);

                        receiveFile(socketChannel, new File(savePath + string));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        socketChannel.close();
                    } catch (Exception ex) {
                    }
                }
            }
        }).start();
    }*/

   public static void receiveFileFromPhone() {
        final String hostIP = IPUtil.getLocalHostIp();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SocketChannel socketChannel = null;
                try {
                    socketChannel = SocketChannel.open();

                    SocketAddress socketAddress = new InetSocketAddress(hostIP, portNum);
                    socketChannel.connect(socketAddress);

                    sendData(socketChannel, "filename");
                    String string = "";
                    string = receiveData(socketChannel);
                    if (!string.isEmpty()) {
                        socketChannel = SocketChannel.open();
                        socketChannel.connect(new InetSocketAddress(hostIP, portNum));
                        sendData(socketChannel, string);
                        L.e("string :"+string);
                        receiveFile(socketChannel, new File(savePath + string));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        socketChannel.close();
                    } catch (Exception ex) {
                    }
                }
            }
        }).start();
    }



    public static void sendFileToServer2() {
        final File aTxt = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/14.jpg");
        new Thread(new Runnable() {
            @Override
            public void run() {

                Selector selector = null;
                ServerSocketChannel serverSocketChannel = null;

                try {
                    selector = Selector.open();
                    serverSocketChannel = ServerSocketChannel.open();
                    serverSocketChannel.configureBlocking(false);
                    serverSocketChannel.socket().setReuseAddress(true);
                    serverSocketChannel.socket().bind(new InetSocketAddress(portNum));
                    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                    L.e("开启安卓端服务器");
                    while (selector.select() > 0) {
                        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                        L.e("监听>0");
                        while (it.hasNext()) {
                            L.e("监听hasNext");
                            SelectionKey readyKey = it.next();
                            it.remove();

                            SocketChannel socketChannel = null;
                            String string = "";
                            try {
                                socketChannel = ((ServerSocketChannel) readyKey.channel()).accept();
                                string = receiveData(socketChannel);
                                L.e("string:" + string);
                                if(string.equals("filename")){
                                    File f= new File(aTxt.getPath());
                                    if (f.exists() && f.isFile()){
                                        sendData(socketChannel, aTxt.getName());
                                    }else{
                                        L.e("file doesn't exist or is not a file");
                                    }
                                }
                                if(string.equals(aTxt.getName())){
                                    sendFile(socketChannel, aTxt);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            } finally {
                                try {
                                    socketChannel.close();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        selector.close();
                        serverSocketChannel.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private static void sendData(SocketChannel socketChannel, String string) throws IOException {
        byte[] bytes = string.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        socketChannel.write(buffer);
        socketChannel.socket().shutdownOutput();
    }

    private static String receiveData(SocketChannel socketChannel) throws IOException {
        String string = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            byte[] bytes;
            int count = 0;
            while ((count = socketChannel.read(buffer)) >= 0) {
                buffer.flip();
                bytes = new byte[count];
                buffer.get(bytes);
                baos.write(bytes);
                buffer.clear();
            }
            bytes = baos.toByteArray();
            string = new String(bytes);
        } finally {
            try {
                baos.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return string;
    }

    private static void sendFile(SocketChannel socketChannel, File file) throws IOException {
        FileInputStream fis = null;
        FileChannel channel = null;
        try {
            fis = new FileInputStream(file);
            channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            int size = 0;
            while ((size = channel.read(buffer)) != -1) {
                buffer.rewind();
                buffer.limit(size);
                socketChannel.write(buffer);
                buffer.clear();
            }
            socketChannel.socket().shutdownOutput();
        } finally {
            try {
                channel.close();
                fis.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void receiveFile(SocketChannel socketChannel, File file) throws IOException {
        FileOutputStream fos = null;
        FileChannel channel = null;

        try {
            fos = new FileOutputStream(file);
            channel = fos.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

            int size = 0;
            while ((size = socketChannel.read(buffer)) != -1) {
                buffer.flip();
                if (size > 0) {
                    buffer.limit(size);
                    channel.write(buffer);
                    buffer.clear();
                }
            }
        } finally {
            try {
                channel.close();
                fos.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


}
