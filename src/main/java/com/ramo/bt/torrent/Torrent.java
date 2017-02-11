package com.ramo.bt.torrent;

import com.ramo.utils.BencodeUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;



public class Torrent {

	private static String Separator = System.getProperty("file.separator");

	//定义torrent文件中几个基本的属性
	private String announce;                  //字符串形式的annuounce
	private List<String> announceList;        //列表形式的annuounce_list
	private Date creationDate;                //创建日期（可选），torrent文件是以长整形数据存储的。
	private String comment;                   //字符串形式的comment
	private String createdBy;                 //字符串形式表示此torrent文件的作者
	private Map info;                         //定义Info的结构，字典型的，用Map表示
	
	private boolean noAnnounceList = false;    //用于判断是否有AnnounceList
	
	//torrent文件中，info也表示的是个字典，文件内容信息也存储在这个结构里，所以用一个InfoContent类来专门表示info的内容。
	private InfoContent infoContent;
	//整个torrent文件就是一个字典结构，所以定义一个描述整个结构的Map型fullInfo，用于存放torrent文件的全部内容。
	private Map fullInfo; 
    /* 在BT协议中规定，info_hash，是URL编码的20字节SHA1散列，这个散列是元信息文件中info键所对应的值的SHA1散列，
	 * 这里直接将其定义为一个属性，在后面进行访问tracker的操作时需要用到这个值。
	 */
	private byte InfoHash[];

	//如果是列表是announce，则将其存放在一个字符串数组里
	private String[] listAnnounce;
	
	//构造方法,接收文件作为参数
	public Torrent(File file) throws IOException {
		this(BencodeUtils.readCompleteFile(file));
	}

	//构造方法,接收字节数组作为参数
	public Torrent(byte abyte0[]) {
		//调用BCode类的BDecode解码方法，得到一个Map型的整个torrent文件的内容
		fullInfo = (Map) BCode.BDecode(abyte0);
		//根据key='info',从fullInfo这个Map中得到info的值
		info = (Map) fullInfo.get("info");
		//infohash是对info字典内容进行B编码后，再由SHA1函数进行散列得到的，BCode的BEncode方法是进行B编码，BencodeUtils的shaHash方法，是将编码内容进行SHA1散列。
		InfoHash = BencodeUtils.shaHash(BCode.BEncode(info));
		//根据key='announce',从fullInfo这个Map中得到announce的值
		announce = new String((byte[]) fullInfo.get("announce"));
		/*
		 * 以下的标签在BT协议规范中，都是可选的，因而需要执行一个判断。
		 */
		//如果有，则取出announce-list的值
		if (fullInfo.containsKey("announce-list")) {
			announceList = (List) fullInfo.get("announce-list");
		}else {
			this.setNoAnnounceList(true);
		}
		//如果有，则取出comment的值
		if (fullInfo.containsKey("comment")) {
			comment = new String((byte[]) fullInfo.get("comment"));
		}
		//如果有，则取出creation date的值
		if (fullInfo.containsKey("creation date")) {
			creationDate = new Date(((Number) fullInfo.get("creation date"))
					.longValue() * 1000L);
		}
		//如果有，则取出created by的值
		if (fullInfo.containsKey("created by")) {
			createdBy = new String((byte[]) fullInfo.get("created by"));
		}
	}
	
	/**
	 * Method name:getTorrentInfo
	 * TODO : 将info所表示的字典内容再进行进一步的处理，info字典里表示的都是文件信息，这里就是处理这些信息的过程
	 * return_type:InfoContent
	 */
	public InfoContent getTorrentInfo() {
		info = this.getInfo();
		long fileLength;
		long singleFileLength;
		String md5Sum;
		//首先要进行判断，确保所要处理的内容不为空
		if (infoContent == null) {
			infoContent = new InfoContent();
			if (info == null) {
				throw new RuntimeException(	"在此torrent文件中，没有发现'info'字典！");
			}
			//根据B编码中定义的info的结构，分别取出相应的值。
			String name = new String((byte[]) info.get("name"));
			Integer pieceLength = ((Number) info.get("piece length")).intValue();
			String pieces = new String((byte[]) info.get("pieces"));
			
			//这三个字段在info字典中都是必须的，所以需要进行一次判断，如果没有这些键值证明torrent文件存在问题。出现问题时抛出自定义的TorrentException异常
			if (name == null) {
				throw new RuntimeException(	"在info字典中没有找到键值name");
			}
			if (pieceLength == null) {
				throw new RuntimeException(	"在info字典中没有找到键值pieceLength");
			}
			if (pieces == null) {
				throw new RuntimeException("在info字典中没有找到键值pieces");
			}
			//将取得的值set到infoContent里，在读取文件信息的时候会用到
			infoContent.setName(name);
			infoContent.setPieceLength(pieceLength.intValue());
			infoContent.setPieces(pieces);

			//下面这段代码就是读取文件信息的核心代码，首先根据filelist来判断是多文件还是单文件。
			List filesList = null;
			if (info.containsKey("files")) {
				filesList = (List) info.get("files");
			} 
			//如果torrent文件信息为多文件，则单独用一个MultiFile类来表示，MultiFile数组用来存储多文件里所有的文件。
			MultiFile files[];
			if (filesList == null) {
				// 处理单文件的内容，根据BT协议规范，单文件有三个键，分别为：name，length和md5sum，
				if (info.containsKey("name")) {
					String filename = new String((byte[]) info.get("name"));
					infoContent.setName(filename);
				}//在if后面，也可以用一个else语句打印一条提示信息，或进行其它操作均可
				if (info.containsKey("length")) {
					fileLength = ((Number) info.get("length")).longValue();
					infoContent.setFileLength(fileLength);
				}				
				if (info.containsKey("md5sum")) {
					md5Sum = new String((byte[]) info.get("md5sum"));
					infoContent.setMd5Sum(md5Sum);
				} 
				//标识一下，info字典里存储的是单文件
				infoContent.setSingleFile(true);
			} else {
				// 处理多文件的过程，根据BT协议规范，多文件中也有name,length,md5sum,path等
				files = new MultiFile[filesList.size()];
				Map fileListmap[] = (Map[]) filesList.toArray(new Map[filesList.size()]);
				for (int i = 0; i < fileListmap.length; i++) {
					Map listMap = fileListmap[i];
					MultiFile torrentFile = new MultiFile();
					files[i] = torrentFile;
					if (listMap.containsKey("length")) {
						singleFileLength = ((Number) listMap.get("length"))	.longValue();
						torrentFile.setSingleFileLength(singleFileLength);
					} 
					if (listMap.containsKey("md5sum")) {
						String md5sum = new String((byte[]) listMap.get("md5sum"));
						torrentFile.setMd5sum(md5sum);
					} 
					if (listMap.containsKey("path")) {
						List pathList = (List) listMap.get("path");
						StringBuffer path = new StringBuffer();
						for (int j = 0; j < pathList.size(); j++) {
							path.append(new String((byte[]) pathList.get(j)));
							path.append("/");
						}
						path.deleteCharAt(path.length() - 1);
						torrentFile.setPath(path.toString());
					} 
				}
				infoContent.setMultiFile(files);
			}
		}
		return infoContent;
	}
	/**
	 * Method name:getListAnnounce
	 * TODO : 取得torrent文件中announce_list的方法，因为announce-list的内容是一个B编码的列表，要取出字符串类型的值，还需要进一步处理程序算法都是依据B编码规则实现的。
	 * return_tyep:返回一个字符串数组，用于存储列表中所有的announce
	 */
	public String[] getListAnnounce() {
		String as[] = new String[this.getAnnounceList().size()];
		for (ListIterator listiterator = this.getAnnounceList().listIterator(); listiterator
				.hasNext();) {
			List list = (List) listiterator.next();
			String s = "";
			for (Iterator it = list.iterator(); it.hasNext();) {
				String s1 = new String((byte[]) it.next());
				if (s != "")
					s = s + Separator;
				s = s + s1;
			}
			as[listiterator.previousIndex()] = s;
		}
		return as;
	}

	/**
	 * Method name:getInfoHashHex
	 * TODO : 处理info_hash的方法，因为在访问tracker的时候，HTTP的请求链接地址需将info_hash的值进行转义，这是一个将info_hash的每个字节变成16进制的操作
	 * return_type:String
	 */
	public String getInfoHashHex() {
		char ac[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
				'b', 'c', 'd', 'e', 'f' };
		StringBuffer stringbuffer = new StringBuffer(InfoHash.length * 2);
		for (int i = 0; i < InfoHash.length; i++) {
			byte byte0 = InfoHash[i];
			stringbuffer.append(ac[(byte0 & 0xf0) >> 4]);
			stringbuffer.append(ac[byte0 & 0xf]);
		}
		return stringbuffer.toString();
	}
	
	
	/*
	 * 以下都是针对torrent类各个属性的存取方法，主要用于各种读取操作。
	 */
	public void setListAnnounce(String[] listAnnounce) {
		this.listAnnounce = listAnnounce;
	}
	public void setFullInfo(Map fullInfo) {
		this.fullInfo = fullInfo;
	}
	public Map getInfo() {
		return info;
	}
	public void setInfo(Map info) {
		this.info = info;
	}
	public void setTorrentInfo(InfoContent torrentInfo) {
		this.infoContent = torrentInfo;
	}
	public String getAnnounce() {
		return announce;
	}
	public void setAnnounce(String announce) {
		this.announce = announce;
	}
	public List getAnnounceList() {
		return announceList;
	}
	public void setAnnounceList(List announceList) {
		this.announceList = announceList;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public byte[] getInfoHash() {
		return InfoHash;
	}
	public void setInfoHash(byte[] infoHash) {
		InfoHash = infoHash;
	}
	public Map getFullInfo() {
		return fullInfo;
	}
	public boolean isNoAnnounceList() {
		return noAnnounceList;
	}
	public void setNoAnnounceList(boolean noAnnounceList) {
		this.noAnnounceList = noAnnounceList;
	}
	//存取方法结束
}
