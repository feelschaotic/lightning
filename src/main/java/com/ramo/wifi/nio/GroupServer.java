package com.ramo.wifi.nio;

import com.ramo.service.SendingFilesService;
import com.ramo.utils.L;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.logging.Level;

public class GroupServer extends Server{


	public void init(int clientNum) {

		L.e("开启服务器，客户端个数："+clientNum);
		Selector selector = null;
		ServerSocketChannel serverSocketChannel = null;

		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().setReuseAddress(true);
			serverSocketChannel.socket().bind(
					new InetSocketAddress(NIOConstant.PORT));
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			initListener(selector,clientNum);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "3", ex);
		} finally {
			try {
				System.out.println("close");
				selector.close();
				serverSocketChannel.close();
			} catch (Exception ex) {
				logger.log(Level.SEVERE, "5", ex);
			}
		}
	}

	private void initListener(Selector selector,int clientNum) throws IOException {
		int flag = 0;
		while (selector.select() > 0 ) {
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while (it.hasNext()) {
				SelectionKey readyKey = it.next();
				it.remove();
				beginSend(readyKey);
				flag++;
				if (flag >= (SendingFilesService.sendTaskList.size() +1)* clientNum) {
					System.exit(0);
				}
			}
		}
	}

}
