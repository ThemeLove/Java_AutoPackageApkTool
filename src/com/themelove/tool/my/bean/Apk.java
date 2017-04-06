package com.themelove.tool.my.bean;
/**
 *	@author:qingshanliao
 *  @date  :2017年4月6日
 */
public class Apk {
	/**
	 * apk名称(英文首字母简写)
	 */
	private String name;
	/**
	 * apk中文全称
	 */
	private String fullName;
	/**
	 * apk的功能描述
	 */
	private String desc;
	/**
	 * apk路径
	 */
	private String apkPath;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getApkPath() {
		return apkPath;
	}
	public void setApkPath(String apkPath) {
		this.apkPath = apkPath;
	}
	
}
