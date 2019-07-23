package com.themelove.tool.vas.bean;
/**
 *	@author:qingshanliao
 *  @date  :2017年3月20日
 */
public class PackageMethod {
	public static final String METHOD_META="METHOD_META";
	public static final String METHOD_ASSET="METHOD_ASSET";
	public static final String METHOD_QUICK="METHOD_QUICK";
	
	private String method;
	/**
	 * 打包方式描述
	 */
	private String desc;
	
	
	public String getMethod() {
		return method;
	}


	public void setMethod(String method) {
		this.method = method;
	}


	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}


	@Override
	public String toString() {
		return desc;
	}
}
