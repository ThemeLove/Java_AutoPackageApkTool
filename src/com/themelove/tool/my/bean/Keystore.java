package com.themelove.tool.my.bean;
/**
 *	@author:qingshanliao
 *  @date  :2017年4月6日
 */
public class Keystore {
	/**
	 * keystore的密码
	 */
	private String password;
	/**
	 * keystore的别名
	 */
	private String alias;
	/**
	 * keystore的别名密码
	 */
	private String aliasPassword;
	/**
	 * keystore的路径
	 */
	private String keystorePath;
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getAliasPassword() {
		return aliasPassword;
	}
	public void setAliasPassword(String aliasPassword) {
		this.aliasPassword = aliasPassword;
	}
	public String getKeystorePath() {
		return keystorePath;
	}
	public void setKeystorePath(String keystorePath) {
		this.keystorePath = keystorePath;
	}
	
	
}
