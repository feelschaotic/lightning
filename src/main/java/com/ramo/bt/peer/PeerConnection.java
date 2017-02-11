package com.ramo.bt.peer;


import com.ramo.bt.torrent.Torrent;

//节点网络连接
public class PeerConnection {
	public final static int KMessageIdChoke = 0;
	public final static int KMessageIdUnchoke = 1;
	public final static int KMessageIdInterested = 2;
	public final static int KMessageIdNotInterested = 3;
	public final static int KMessageIdHave = 4;
	public final static int KMessageIdBitfield = 5;
	public final static int KMessageIdRequest = 6;
	public final static int KMessageIdPiece = 7;
	public final static int KMessageIdCancel = 8;
	public final static int KFlagAmChoking = 1;
	public final static int KFlagAmInterested = 2;
	public final static int KFlagPeerChoking = 4;
	public final static int KFlagPeerInterested = 8;
	public final static int EPeerNotConnected = 0;
	public final static int EPeeTcpConnecting = 1;
	public final static int EPeerConnected = 2;
	public final static int EPeerPwHandshaking = 3;
	public final static int FPeerPwConnected = 4;
	public final static int EPeerClosing = 5;
	private Peer peer;// 从属的节点和torrent对象
	private Torrent torrent;

	public void onTimer() {// 节点定时查询处理网络连接情况

	}

	private boolean readData(byte[] data) {// 在网络连接上读取输入流到字节数组data中
		return true;
	}

	private void read() {// 读取网络连接上的消息并处理

	}

	public void issueDownload() {// 通知客户端进行下载

	}

	public void issueUpload() {// 通知客户端进行上传

	}

	public void startDownloading() {// 发送握手信息，准备下载

	}

	public void connect() {//生成网络连接

	}
}
