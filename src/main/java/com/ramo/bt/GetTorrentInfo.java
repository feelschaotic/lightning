package com.ramo.bt;

import com.ramo.bt.torrent.InfoContent;
import com.ramo.bt.torrent.MultiFile;
import com.ramo.bt.torrent.Torrent;
import com.ramo.utils.L;

import java.io.File;



public class GetTorrentInfo extends GetInfoFromServer{

	public void getTorrent() {
		try {
			L.e("向服务器请求种子文件");
			byte[] bytes = getBTFileBytes();
			File torrent = getFileFromBytes(bytes, ROOT_PATH+File.separator+"1.torrent");
			// 根据输入流通过FetchTorrentInfo类，取出torren文件所有内容
			Torrent ft = new Torrent(torrent);
			// info 字典结构的内容，里面描述了文件的一些信息， 包括两种情况，一是单个文件，再一个就是包括目录的多个文件
			InfoContent tt = new InfoContent();
			/*
			 * announce在torrent文件中有两种形式，只有一个announce的时候，它就是以字符串形式存储的
			 * 如果有两个以上的announce的时候，它就是以列表形式存储的
			 */
			L.e("Torrent文件全部信息如下：");
			L.e("----------------Torrent文件Announce信息--------------------");

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
			L.e("----------------Torrent文件基本信息-------------------------");
			L.e("getCreationDate=" + ft.getCreationDate());
			L.e("getComment=" + ft.getComment());
			L.e("getCreatedBy=" + ft.getCreatedBy());
			L.e("getInfoHashHex=" + ft.getInfoHashHex());
			L.e("----------------torrent文件info字典信息--------------------");
			tt = ft.getTorrentInfo();
			if (tt.isSingleFile()) {
				L.e("此文件是单文件");
			} else {
				L.e("此文件是多文件");
			}
			L.e("文件分块数：" + tt.getPiecesNum());
			if (tt.isSingleFile()) {
				L.e("----------torrent的单文件信息如下：-------");
				L.e("文件名" + tt.getName() + '\t' + "文件大小"
						+ tt.getFileLength() + '\t' + "分块长度"
						+ tt.getPieceLength());
			} else {
				L.e("----------torrent的多文件信息如下：--------");
				L.e("文件名:" + tt.getName() + '\t' + '\t'
						+ "文件大小:" + tt.getFileLength());
				MultiFile[] temp = tt.getMultiFile();
				L.e("多文件列表信息：");
				for (int i = 0; i < temp.length; i++) {
					String path = temp[i].getPath();
					long length = temp[i].getSingleFileLength();
					L.e("文件名:" + path + '\t' + '\t' + "文件大小:"
							+ length);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
