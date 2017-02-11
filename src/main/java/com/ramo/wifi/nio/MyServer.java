package com.ramo.wifi.nio;


import com.ramo.utils.L;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

public class MyServer extends Server{

    public void init() {
        Selector selector = null;
        ServerSocketChannel serverSocketChannel = null;

        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().setReuseAddress(true);
            serverSocketChannel.socket().bind(
                    new InetSocketAddress(NIOConstant.PORT2));
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

    private void initListener(Selector selector) throws IOException {
        L.e("开启安卓端服务器");
        while (selector.select() > 0) {
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                L.e("监听到请求");
                SelectionKey readyKey = it.next();
                it.remove();
                super.beginSend(readyKey);
            }
        }
    }



}
