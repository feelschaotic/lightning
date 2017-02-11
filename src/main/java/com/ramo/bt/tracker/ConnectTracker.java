package com.ramo.bt.tracker;

import com.ramo.bt.torrent.BCode;
import com.ramo.bt.torrent.MultiFile;
import com.ramo.bt.torrent.Torrent;
import com.ramo.utils.BencodeUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;


/**
 * 
 * ConnectTracker类，主要用于连接Tracker服务器的操作，并从Tracker里得到Peer列表的信息
 * 本类主要有两个方法，一个方法是查询Tracker服务器，另一个方法用于得到Tracker的响应结果
 * 
 */
public class ConnectTracker {

	@SuppressWarnings("unchecked")
	private List peerList;
	long interval;

	/**
	 * Method name:queryTracker TODO :
	 * 用于查询Tracker服务器，接收一个File类型的torrent文件参数，返回一个set集合，用于存储Peer节点信息
	 * return_type:Set<String>，将得到的Peer信息都放到一个Set集合里
	 */
	public Set<String> queryTracker(File file) throws IOException {
		Set peerSet = new TreeSet(); // 定义一个TreeSet集合
		Torrent torrent = null; // 定义一个torrent文件结构
		long filelength = 0; // 定义文件的长度
		String peerStr = ""; // 定义一个表示Peer信息的字符串
		Map temmap = null; // 定义一个临时的Map结构
		List<String> templist = new ArrayList<String>(); // 定义一个临时的List结构
		List<String> announcelist = new ArrayList<String>();// 定义存储announce_list的列表
		try {
			// 调用Torrent方法，解析此文件
			torrent = new Torrent(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(torrent.isNoAnnounceList()){
			announcelist.add(torrent.getAnnounce());
		}else {
			// 得到所有的announce列表,用于组建请求Tracker的URL
			String[] str = torrent.getListAnnounce();
			for (int i = 0; i < str.length; i++) {
				announcelist.add(str[i]);
			}
		}
		
		// 取得info_hash值，用于组建请求Tracker的URL
		String hash = "&info_hash="
				+ BencodeUtils.escapeString(torrent.getInfoHashHex());

		// 取得文件长度，用于组建请求Tracker的URL，如果是单文件，则就是文件的长度，如果是多文件，则是每个文件长度的和
		if (torrent.getTorrentInfo().isSingleFile()) {
			filelength = torrent.getTorrentInfo().getFileLength();
		} else {
			MultiFile[] temp = torrent.getTorrentInfo().getMultiFile();
			for (int i = 0; i < temp.length; i++) {
				// 多文件的时候，所有文件的长度和
				filelength = filelength + temp[i].getSingleFileLength();
			}
		}
		announcelist = BencodeUtils.parseAnnounce(announcelist);
		// 对每个annouce都发出Tracker的查询请求，也就是说，所有的服务器都请求一遍
		for (Iterator it = announcelist.iterator(); it.hasNext();) {
			String announce = (String) it.next();
			try {
				// 得到Tracker的响应信息，Tracker的响应也是B编码格式的数据，还需要进行解码操作
				Object obj = BCode.BDecode(ConnectTracker.getTrackerResponse(
						file, announce, hash, filelength));
				if (obj instanceof Map) {
					temmap = (Map) BCode.BDecode(ConnectTracker
							.getTrackerResponse(file, announce, hash, filelength));
					System.out.println(temmap);
				} else {
					System.out.println("Tracker服务器返回无法解析的文件！");
					continue;
				}
			} catch (IOException e) {
				System.out.println("与Tracket服务器的连接出现异常，此announce地址：" + announce
						+ "无法与服务器连接");
				continue;
			}
			// 解析Tracker的响应信息，从中得到Peer的信息，是根据Tracker的响应的结构进行求值的
			if (temmap.containsKey("peers")) {
				try {
					peerList = (List) temmap.get("peers");
				} catch (ClassCastException e) {
					System.out.println("得到Tracker的响应，但无法解析");
				}
			} else {
				System.out.println("此announce地址：" + announce + "的应答文件中不含键值peers");
				continue;
			}
			if (temmap.containsKey("interval")) {
				interval = ((Number) temmap.get("interval")).longValue();
			} else {
				System.out
						.println("此announce地址：" + announce + "的应答文件中不含键值interval");
				continue;
			}
			// 将所有Peer的IP地址、端口号存放到PeerSet里
			for (int i = 0; i < peerList.size(); i++) {
				Map map1 = (Map) peerList.get(i);
				byte abyte0[] = (byte[]) map1.get("ip");
				long port = ((Number) map1.get("port")).longValue();
				String tmp = new String(abyte0) + ":" + port;
				if (peerSet.isEmpty() || !peerSet.contains(tmp)) {
					peerSet.add(tmp);
				}
			}
		}
		return peerSet;
	}

	/**
	 * Method name:getTrackerResponse TODO : 得到Tracker服务器的响应结果
	 * return_type:byte[]
	 */
	public static byte[] getTrackerResponse(File file, String announce,
			String infohash, long filelength) throws IOException {
		// 定义请求Tracker服务器的URL链接结构，此结构在文中有详细的说明。
		String queryURL = announce + "?peer_id="
				+ BencodeUtils.getRandomString(20) + infohash
				+ "&port=6885&uploaded=0&downloaded=0&left=" + filelength
				+ "0&compact=1&event=started&numwant=5000";
		URL url = null;
	    System.out.println("当前查询Tracker的URL： " + queryURL);
		try {
			url = new URL(queryURL);
		} catch (MalformedURLException malformedurlexception) {
			throw new RuntimeException(malformedurlexception);
		}
		Object obj1;
		// 与Tracker服务器建立连接
		URLConnection urlconnection = url.openConnection();
		// 设置请求的格式
		urlconnection.setRequestProperty("User-Agent", "TrackPeer v0.1");
		urlconnection.setRequestProperty("Accept-Encoding", "gzip");
		// 得到请求结果
		Object obj = urlconnection.getInputStream();
		obj1 = urlconnection.getHeaderField("Content-Encoding");
		// 处理请求结果，请求结果是gzip格式的压缩包，还需要进行解包操作
		if (obj1 != null && ((String) (obj1)).equals("gzip"))
			obj = new GZIPInputStream(((InputStream) (obj)));
		obj1 = new ByteArrayOutputStream();
		int i;
		// 读取请求结果，也是B编码格式的结果
		while ((i = ((InputStream) (obj)).read()) != -1)
			((ByteArrayOutputStream) (obj1)).write(i);
		System.out.println("返回结果："+obj1.toString());
		return ((ByteArrayOutputStream) (obj1)).toByteArray();
	}

}
