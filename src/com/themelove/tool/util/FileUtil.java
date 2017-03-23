package com.themelove.tool.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
	public static boolean deleteFile(File file){
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
	
	/**
	 * 递归删除目录下的所有文件，如果没有，就创建
	 */
	public static  boolean deleteFiles(File dir){
		if (!dir.exists()) {
			return dir.mkdirs();
		}
		if (dir.isDirectory()) {//是目录	
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					boolean deleteFilesFlag = deleteFiles(file);
					if (!deleteFilesFlag) return false;
				}else{
					boolean deleteFileFlag = deleteFile(file);
					if (!deleteFileFlag)  return false;
				}
			}
		}
		return true;
	};
	
	/**
	 * 拷贝一个目录
	 * @param source
	 * @param target
	 * @return true 成功，false 失败
	 */
	public static boolean copyDir(File sourceDir,File targetDir){
		if (!sourceDir.exists()) return false;
		if (!targetDir.exists()) targetDir.mkdirs();//如果目标目录不存在，则创建
		if (sourceDir.isFile()) {//如果是文件
			return copyFile(sourceDir, targetDir);
		}
		
		if (sourceDir.isDirectory()) {
			File[] listFiles = sourceDir.listFiles();
			for (File file : listFiles) {
				File targetFile=new File(targetDir.getAbsolutePath()+File.separator+file.getName());
				
				if (file.isFile()) {
					boolean copyFile = copyFile(file, targetFile);
					if (!copyFile) return false;
				}
				if (file.isDirectory()) {
					boolean copyDir = copyDir(file, targetFile);
					if(copyDir)return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 拷贝一个文件
	 * @param source		源文件引用
	 * @param target		目标文件引用
	 * @return				true 成功，false 失败
	 */
	@SuppressWarnings("resource")
	public static boolean copyFile(File source,File target){
//		源文件不存在直接返回false
		if (!source.exists()) return false;
		if (!target.exists()) target.mkdirs();//如果目标文件不存在，则创建
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(source));
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));
			byte[] bys=new byte[1024];
			int len=-1;
			while ((len=bis.read(bys))!=-1) {
				bos.write(bys, 0, len);
			}
			bos.flush();
			bis.close();
			bos.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
