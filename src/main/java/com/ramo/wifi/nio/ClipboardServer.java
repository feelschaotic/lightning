package com.ramo.wifi.nio;

import com.ramo.utils.ClipboardUtil;
import com.ramo.utils.L;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class ClipboardServer {
    private NIOUtil nioUtil = new NIOUtil();

    public void init() {
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

                        serverSocketChannel.socket().bind(
                                new InetSocketAddress(NIOConstant.PORT_Clip));

                    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

                    initListener(selector);

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

    private void initListener(Selector selector) throws IOException {
        L.e("等待客户端接入");
        while (selector.select() > 0) {
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey readyKey = it.next();
                it.remove();

                SocketChannel socketChannel = null;
                try {
                    socketChannel = ((ServerSocketChannel) readyKey.channel()).accept();
                    String str = nioUtil.receiveData(socketChannel);
                    L.e("剪贴板数据：" + str);
                    ClipboardUtil.write(str);
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
    }


}
