package com.themelove.tool.util;

import java.io.File;
import java.io.FileOutputStream;

import com.themelove.tool.my.thread.StreamThread;

/**
 * 	cmd相关
 *	@author:qingshanliao
 *  @date  :2017年3月23日
 */
public class CmdUtil {

	
	/**
	 * 执行命名并输出执行过程到控制台
	 * @param command
	 */
	public static void exeCmdWithLog(String command){
		try {
			Process process = Runtime.getRuntime().exec(command);
			StreamThread inputThread = new StreamThread(process.getInputStream());
			inputThread.start();
			StreamThread errorThread = new StreamThread(process.getErrorStream());
			errorThread.start();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 执行命令并输出执行过程到控制台和指定log文件
	 * @param command
	 * @param logtxt
	 */
	@SuppressWarnings("resource")
	public static void exeCmdWithLogFile(String command,File logtxt){
		try {
			FileOutputStream logFos = new FileOutputStream(logtxt);
			Process process = Runtime.getRuntime().exec(command);
			StreamThread inputThread = new StreamThread(process.getInputStream(),logFos);
			inputThread.start();
			StreamThread errorThread = new StreamThread(process.getErrorStream(),logFos);
			errorThread.start();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 执行命令并输出执行过程到控制台
	 * @param command
	 * @param envp
	 * @param file
	 * @return
	 */
	public static void exeCmdWithLog(String command,String[] envp,File file){
		try {
			Process process = Runtime.getRuntime().exec(command, envp, file);
			StreamThread inputThread = new StreamThread(process.getInputStream());
			inputThread.start();
			StreamThread errorThread = new StreamThread(process.getErrorStream());
			errorThread.start();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 执行命令并输出执行过程到控制台和指定log文件
	 * @param command
	 * @param envp
	 * @param file
	 * @param logtxt
	 */
	@SuppressWarnings("resource")
	public static void exeCmdWithLogFile(String command,String[] envp,File file,File logtxt){
		try {
			FileOutputStream logFos = new FileOutputStream(logtxt);
			
			Process process = Runtime.getRuntime().exec(command, envp, file);
			StreamThread inputThread = new StreamThread(process.getInputStream(),logFos);
			inputThread.start();
			StreamThread errorThread = new StreamThread(process.getErrorStream(),logFos);
			errorThread.start();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
