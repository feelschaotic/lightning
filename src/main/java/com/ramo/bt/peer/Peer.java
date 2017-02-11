package com.ramo.bt.peer;

/**
 * Peer类，主要用来定义一个Peer的信息，Peer的信息这里只取两个，一个是IP地址，另一个是端口号
 * 
 */
public class Peer {

	private String ip; // 定义IP
	private long port; // 定义端口

	//针对IP和Port的存取操作
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public long getPort() {
		return port;
	}
	public void setPort(long port) {
		this.port = port;
	}

}
