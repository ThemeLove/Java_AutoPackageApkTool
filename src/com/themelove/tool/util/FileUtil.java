package com.themelove.tool.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

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
//					清空文件夹里的内容成功之后，再将文件夹目录删除
					boolean deleteDir = file.delete();
					if (!deleteDir) return false;
//					System.out.println("清空目录---"+file.getName()+"---成功");
				}else{
					boolean deleteFileFlag = deleteFile(file);
					if (!deleteFileFlag)  return false;
//					System.out.println("清空文件---"+file.getName()+"---成功");
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
		if (!targetDir.exists()) 
		//如果目标目录不存在，则创建,如果创建失败，直接返回false;
		if(!targetDir.mkdirs()) return false;
		if (sourceDir.isFile()) {//如果是文件
			return copyFile(sourceDir, targetDir);
		}
		
		if (sourceDir.isDirectory()) {
			File[] listFiles = sourceDir.listFiles();
			System.out.println(Arrays.asList(listFiles));
			for (File file : listFiles) {
				File targetFile=new File(targetDir.getAbsolutePath()+File.separator+file.getName());
				if (file.isFile()) {
					boolean copyFile = copyFile(file, targetFile);
					if (!copyFile) return false;
				}
				if (file.isDirectory()) {
					boolean copyDir = copyDir(file, targetFile);
					if(!copyDir)return false;
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
		try {
			if (!target.exists()) 
			//如果目标文件不存在，则创建,如果创建不成功，直接返回false;
			if(!target.createNewFile())  return false;
//			if(!target.mkdir())  return false;
//			if(!target.mkdirs())  return false;
			
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
			System.out.println("copyFile---"+source.getName()+"---成功");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
