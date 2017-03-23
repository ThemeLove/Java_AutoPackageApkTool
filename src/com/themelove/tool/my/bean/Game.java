package com.themelove.tool.my.bean;
/**
 *	@author:qingshanliao
 *  @date  :2017年3月20日
 */
public class Game {
	/**
	 * 首字母简写英文名
	 */
	private String name;
	/**
	 * 中文名全称
	 */
	private String fullName;
	
	/**
	 * 游戏根目录
	 */
	private String gamePath;
	
	/**
	 * 获取首字母简写英文名
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 设置首字母简写英文名
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 获取中文名全称
	 * @return
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * 获取游戏根目录
	 * @return
	 */
	public String getGamePath() {
		return gamePath;
	}

	/**
	 * 设置游戏根目录
	 * @param gamePath
	 */
	public void setGamePath(String gamePath) {
		this.gamePath = gamePath;
	}

	/**
	 * 设置中文名全称
	 * @param fullname
	 */
	public void setFullName(String fullname) {
		this.fullName = fullname;
	}
	
	/**
	 * 获取comboxItem的显示名字
	 */
	@Override
	public String toString() {
		if (fullName==null) fullName="";
		return name+"---("+fullName+")";
	}
}
