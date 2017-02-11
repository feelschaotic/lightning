package com.ramo.bt;

import com.ramo.bt.torrent.Torrent;
import com.ramo.bt.tracker.ConnectTracker;
import com.ramo.utils.L;
import com.ramo.wifi.nio.MyClient;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class GetPeersInfo extends GetInfoFromServer {

    //用于计算多线程时间
    static ExecutorService executorService = Executors.newFixedThreadPool(30);

    public void getPeer() {
        Torrent ft = null;
        // 定义一个TreeSet()用于存储所有的Peer信息
        Set resSet = new TreeSet();
        File torrent = null;
        // 用于统计当前的Peer数
        int peerNum = 0;
        ConnectTracker pi = null;

        try {
            L.e("向服务器请求种子文件");
            byte[] bytes = getBTFileBytes();
            torrent = getFileFromBytes(bytes, ROOT_PATH + File.separator + "1.torrent");

            // 根据输入流通过FetchTorrentInfo类，取出torren文件所有内容
            ft = new Torrent(torrent);
            pi = new ConnectTracker();
            // 指定一个需要解析的torrent文件

            ft = new Torrent(torrent);
        } catch (Exception e) {
            e.printStackTrace();
        }

		/*
         * 先打印出所有Announce的地址，在查询Tracker的时候分别对每一个announce都发现一个查询：
		 * announce在torrent文件中有两种形式，只有一个announce的时候，它就是以字符串形式存储的
		 * 如果有两个以上的announce的时候，它就是以列表形式存储的
		 */

        L.e("----------------Torrent文件Announce信息--------------------");
        if (ft == null)
            return;
        if (ft.isNoAnnounceList()) {
            // 只有一个Announce的时候，取出此值
            L.e("announce =" + ft.getAnnounce());
        } else {
            String[] str = ft.getListAnnounce();
            // 如果是announce列表，则取每一个announce值
            for (int i = 0; i < str.length; i++) {
                String announcelist = str[i];
                L.e("announcelist_" + i + "=" + announcelist);
            }
        }
        L.e("\n针对以上每个Announce都向Tracker服务器发送查询请求\n");
        // 取得Peer信息
        try {
            // 查询Tracker，得到Peer信息的结果集合
            resSet = pi.queryTracker(torrent);
            peerNum = resSet.size();
            L.e("\n本次连接共返回个" + peerNum + "Peer\n");

            if (resSet.isEmpty()) {
                L.e("当前没有任何Peer！");
            } else {
                traversePeerSet(resSet);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void traversePeerSet(Set resSet) {
        Long begin = System.currentTimeMillis();
        for (Iterator it = resSet.iterator(); it.hasNext(); ) {
            String res = (String) it.next();
            L.e("当前活跃Peer,IP：Port=" + res);
            L.e("-开始连接：" + res);
            connectPeer(res);
        }

        executorService.shutdown();
        try {
            boolean loop = true;
            do { // 等待所有任务完成
                loop = !executorService.awaitTermination(2,
                        TimeUnit.SECONDS);
            } while (loop);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Long end = System.currentTimeMillis();
        L.e("程序运行时间：" + (end - begin) + "ms");
    }

    private static void connectPeer(String res) {
        final String[] ipAndPort = res.split(":");
        Runnable runnable = new Runnable() {
            public void run() {
                MyClient myClient = new MyClient();
                myClient.init(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
            }
        };
        executorService.execute(runnable);

    }

}
