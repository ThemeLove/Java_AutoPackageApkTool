package com.themelove.tool.my.bean;
/**
 *	@author:qingshanliao
 *  @date  :2017年3月20日
 */
public class ApktoolVersion {
	private String version;
	private String path;
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public String toString() {
		return version;
	}
}
