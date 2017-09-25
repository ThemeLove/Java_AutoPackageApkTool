package com.themelove.tool.my.bean;
/**
 *  游戏母包封装对象，包含apk ,channel ,keystore
 *	@author:qingshanliao
 *  @date  :2017年3月20日
 */
public class Game {
	private Apk apk;
	private Channel channel;
	private Keystore keystore;
	private String   gamePath;
	
	public String getGamePath() {
		return gamePath;
	}
	public void setGamePath(String gamePath) {
		this.gamePath = gamePath;
	}
	public Apk getApk() {
		return apk;
	}
	public void setApk(Apk apk) {
		this.apk = apk;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public Keystore getKeystore() {
		return keystore;
	}
	public void setKeystore(Keystore keystore) {
		this.keystore = keystore;
	}
	
	@Override
	public String toString() {
		return apk.getName()+"("+apk.getFullName()+")";
	}
}
