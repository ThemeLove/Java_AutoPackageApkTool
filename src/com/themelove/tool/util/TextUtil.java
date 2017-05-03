package com.themelove.tool.util;

import java.util.Locale;

import org.python.core.StringFormatTest;

/**
 *  文本相关工具类
 *	@author:qingshanliao
 *  @date  :2017年3月23日
 */
public class TextUtil {
	/**
	 * 格式化字符串
	 * @param format		
	 * @param args
	 * @return
	 */
	public static String formatString(String format,String... args){
		return String.format(Locale.getDefault(), format, args);
	}
	public static boolean isEmpty(String str){
		if (str==null) {
			return true;
		}
		return str.isEmpty();
	}
}
