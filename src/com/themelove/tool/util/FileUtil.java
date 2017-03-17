package com.themelove.tool.util;

import java.io.File;

/**
 *  File相关工具类
 *	@author:qingshanliao
 *  @date  :2017年3月17日
 */
public class FileUtil {
	/**
	 * 删除文件
	 * @param file
	 * @return 
	 */
	public boolean deleteFile(File file){
		if (file.exists()) {
			return file.delete();
		}
		//如果不存在,直接返回成功
		return true;
	}
	
	
	/**
	 * 删除文件
	 * @param path	将要删除的文件路径
	 * @return
	 */
	public boolean deleteFile(String path){
		File file = new File(path);
		if (file.exists()) {
			return file.delete();
		}
		//如果不存在，直接返回成功	
		return true;
	}
	
	/**
	 * 创建文件
	 * @param file
	 * @return
	 */
	public boolean createFile(File file){
		return file.mkdirs();
	}
	
	/**
	 * 创建文件
	 * @param path
	 * @return
	 */
	public boolean createFile(String path){
		File file = new File(path);
		return file.mkdirs();
	}
}
