package com.ramo.bean;
/**
 * 磁盘信息类
 * @author 小四
 * name:磁盘名
 * totalSpaceNum:转化后的磁盘大小
 * totalSpace:磁盘大小
 * usedSpaceNum:转化后的可用空间
 * usedSpace:可用空间
 * usableSpaceNum:转化后的已用空间
 * usableSpace:已用空间
 */
public class PcInfoVO {
	private String name;
	private String totalSpaceNum;
	private Long totalSpace;
	private String usedSpaceNum;
	private Long usedSpace;
	private String usableSpaceNum;
	private Long usableSpace;
	public PcInfoVO(){
		
	}
	public PcInfoVO(String name, String totalSpaceNum, Long totalSpace, String usedSpaceNum, Long usedSpace,
			String usableSpaceNum, Long usableSpace) {
		super();
		this.name = name;
		this.totalSpaceNum = totalSpaceNum;
		this.totalSpace = totalSpace;
		this.usedSpaceNum = usedSpaceNum;
		this.usedSpace = usedSpace;
		this.usableSpaceNum = usableSpaceNum;
		this.usableSpace = usableSpace;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTotalSpaceNum() {
		return totalSpaceNum;
	}
	public void setTotalSpaceNum(String totalSpaceNum) {
		this.totalSpaceNum = totalSpaceNum;
	}
	public Long getTotalSpace() {
		return totalSpace;
	}
	public void setTotalSpace(Long totalSpace) {
		this.totalSpace = totalSpace;
	}
	public String getUsedSpaceNum() {
		return usedSpaceNum;
	}
	public void setUsedSpaceNum(String usedSpaceNum) {
		this.usedSpaceNum = usedSpaceNum;
	}
	public Long getUsedSpace() {
		return usedSpace;
	}
	public void setUsedSpace(Long usedSpace) {
		this.usedSpace = usedSpace;
	}
	public String getUsableSpaceNum() {
		return usableSpaceNum;
	}
	public void setUsableSpaceNum(String usableSpaceNum) {
		this.usableSpaceNum = usableSpaceNum;
	}
	public Long getUsableSpace() {
		return usableSpace;
	}
	public void setUsableSpace(Long usableSpace) {
		this.usableSpace = usableSpace;
	}
	
}
