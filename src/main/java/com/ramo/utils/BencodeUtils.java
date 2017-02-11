package com.ramo.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 
 * BencodeUtils，一个工具类，主要用于一些编码的操作，如SHA1散列、URL的字符转义、得到20位的Peer_id等
 */
public class BencodeUtils {

	/**
	 * Method name:readCompleteFile
	 * TODO : 用于读取一个文件，并将其转换为字节数组
	 * return_type:byte[]
	 */
	public static byte[] readCompleteFile(File file) throws IOException {
		long l = file.length();
		if (l > 0x7fffffffL)
			throw new IllegalArgumentException("文件太大了！");
		byte abyte0[] = new byte[(int) l];
		DataInputStream datainputstream = new DataInputStream(new FileInputStream(file));
		try {
			datainputstream.readFully(abyte0);
		} finally {
			datainputstream.close();
		}
		return abyte0;
	}
	
	 /**
	 * Method name:shaHash
	 * TODO : 将一个字节数组的内容进行SHA散列，这里主要用来将torrent文件中的info内容时行B编码后散列
	 * return_type:byte[]
	 */
	public static byte[] shaHash(byte abyte0[], int i)
	    {
	        MessageDigest messagedigest;
	        try
	        {
	            messagedigest = MessageDigest.getInstance("SHA");
	        }
	        catch(NoSuchAlgorithmException nosuchalgorithmexception)
	        {
	            throw new RuntimeException(nosuchalgorithmexception.getMessage());
	        }
	        messagedigest.update(abyte0, 0, i);
	        return messagedigest.digest();
	    }

	  public static byte[] shaHash(byte abyte0[])
	    {
	        return shaHash(abyte0, abyte0.length);
	    }
	  
	  /**
		 * Method name:getRandomString
		 * TODO : 用于得到随机的字符串
		 * return_type:String
		 */
		public static String getRandomString(int size) {   
	        char[] c = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'q',
	                'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd',
	                'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm' };
	        Random random = new Random(); 
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < size; i++){
	            sb.append(c[Math.abs(random.nextInt()) % c.length]);
	        }
	        return sb.toString();
	    }
		/**
		 * Method name:escapeString
		 * TODO : 主要用于字符的转义，用于HTTP请示Tracker的URL中。
		 * return_type:String
		 */
		public static String escapeString(String s) {
			StringBuffer stringbuffer = new StringBuffer(s);
			for (int i = 0; i < stringbuffer.length(); i += 3)
				stringbuffer.insert(i, '%');
			return stringbuffer.toString();
		}
		
		public static List<String> parseAnnounce(List<String> announcelist) {
			List<String> tempList = new ArrayList<String>();
			for(int i=0; i<announcelist.size(); i++) {
				String announce = announcelist.get(i);
				if(announce.indexOf("http:") == -1) {
					i = i +1;
				}else if(announce.indexOf('\\') != -1){
					String[] temp = announce.split("\\\\");
					for(int j=0; j<temp.length; j++) {
						tempList.add(temp[j]);
					}
				}
				else {
					tempList.add(announce);
				}
			}
			return tempList;
		}
		
		
		
		
		public static void main(String[] args) {
			int c = '\\';
			System.out.println(c);
		}
}
