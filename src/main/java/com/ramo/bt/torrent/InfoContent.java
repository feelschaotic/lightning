package com.ramo.bt.torrent;
/**
 * InfoContent类，主要用来定义一个info字典的内容，根据BT协议规范，info字典里包含以下结构:
 * piece length 整数,每块的大小 
 * pieces  整数, 块数 
 * private 整数，如果设置为1,客户端必要从 metafile 文件中的tracker 得到种子, 如果没有设置或，设置为零，则可从任何途径获得种子 
 *************描述单个文件的时候***********
 * name 字符串,文件名 
 * length 整数，文件大小 
 * md5sum 可选,文件的md5值， 32个字符 
 **************描述多个文件的时候**********
 * name 文件夹的名字 
 * files 一个字典组成的列表, 每一项有以下关键字 
 * lenght 整数, 文件大小 
 * md5sum, 字符串， 可选 
 * path 路径，文件夹和文件名的 bencode 编码的列表 
 */
public class InfoContent {
	//针对info字典的结构，构造以下的属性
	private int pieceLength;       //定义pieceLength,块大小
	private String pieces;         //定义pieces，一个长度为20的整数倍的字符串。
	private int piecesNum;         //定义分块数，由pieces计算得到
	private String name;           //定义name
	private long fileLength;       //定义fileLength
    private String md5Sum;         //定义md5Sum
    private MultiFile multiFile[]; //定义多文件结构，用来存储所有的多文件内容
    private boolean isSingleFile;  //判断是否是单文件
	public long getFileLength() {
		return fileLength;
	}
	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}
	public String getMd5Sum() {
		return md5Sum;
	}
	public void setMd5Sum(String md5Sum) {
		this.md5Sum = md5Sum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPieceLength() {
		return pieceLength;
	}
	public void setPieceLength(int pieceLength) {
		this.pieceLength = pieceLength;
	}
	public String getPieces() {
		return pieces;
	}
	public void setPieces(String pieces) {
		this.pieces = pieces;
	}
	public boolean isSingleFile() {
		return isSingleFile;
	}
	public void setSingleFile(boolean isSingleFile) {
		this.isSingleFile = isSingleFile;
	}
	public MultiFile[] getMultiFile() {
		return multiFile;
	}
	public void setMultiFile(MultiFile[] multiFile) {
		this.multiFile = multiFile;
	}
	public void setPiecesNum(int piecesNum) {
		this.piecesNum = piecesNum;
	}
	/**
	 * Method name:getPiecesNum
	 * TODO : 此方法用于计算文件的分块数，由Pieces计算得到，
	 * return_type:int
	 */
	public int getPiecesNum() {
		return this.getPieces().getBytes().length / 20 ;
	}
}
