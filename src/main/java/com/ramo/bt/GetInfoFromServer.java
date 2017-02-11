package com.ramo.bt;

import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

public class GetInfoFromServer {
	
	public final static String queryURL = "http://192.168.191.1:8080/match_BTServer/BTServerAction!getBTFile";
	public final static String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	protected static byte[] getBTFileBytes() throws Exception {
		URL url = new URL(queryURL);
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
		System.out.println("返回结果：" + obj1.toString());
		return obj1.toString().getBytes();
	}

	public static File getFileFromBytes(byte[] b, String outputFile) {
		BufferedOutputStream stream = null;
		File file = null;
		try {
			file = new File(outputFile);
			FileOutputStream fstream = new FileOutputStream(file);
			
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
	}
}
