package com.themelove.tool.my.bean;

import java.util.List;
import java.util.Map;

/**
 *  渠道类
 *	@author:qingshanliao
 *  @date  :2017年3月20日
 */
public class Channel {
	/**
	 * 将要打的所有渠道包
	 */
	private List<Map<String,String>> channelList;

	public List<Map<String, String>> getChannelList() {
		return channelList;
	}

	public void setChannelList(List<Map<String, String>> channelList) {
		this.channelList = channelList;
	}
}
