package com.themelove.tool.util;

import java.io.File;
import java.io.IOException;

/**
 * 	cmd相关
 *	@author:qingshanliao
 *  @date  :2017年3月23日
 */
public class CmdUtil {
	
	/**
	 * 执行命令
	 */
	public static Process exeCmd(String command,String[] envp,File file){
		try {
			return Runtime.getRuntime().exec(command, envp, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static Process exeCmd(String command){
		try {
			return Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
