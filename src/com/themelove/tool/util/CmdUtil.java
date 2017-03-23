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
	public static void exeCmd(String command,String[] envp,File file){
		try {
			Runtime.getRuntime().exec(command, envp, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
