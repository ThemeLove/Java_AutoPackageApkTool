package com.themelove.tool.vas.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.swing.JTextArea;

import com.themelove.tool.my.bean.Game;
import com.themelove.tool.my.model.Model;

/**	用户在使用打包工具过程中log记录Manager：
 * 1：用户操作log（比如选择了游戏，切换了apktool版本）输出到控制台并记录到option_log.txt文件
 * 2：用户点击打包按钮（打渠道包详细过程：比如反编译、回编、签名、优化等）
 *	@author:qingshanliao
 *  @date  :2017年9月22日
 */
public class LogManager {
	private String FILE_SEPARATOR;//文件分割符
	private String LINE_SEPARATOR;//换行符
	private String BASE_PATH;	  //当前工程根目录
	private String BASE_WORK_PATH;//打包过程工作目录
	private String BASE_OUT_PATH;//渠道包输出根目录
	private String GAME_OUT_PATH;//渠道包输出目录
	private final String  OPTION_FILE_NAME="option_log.txt";
	private String OPTION_LOG_PATH;
	
	private static LogManager instance=null;
	private PrintWriter logPW;
	private LogManager(){}
	
	public static LogManager getInstance(){
		if (instance==null) {
			synchronized (LogManager.class) {
				if (instance==null) {
					instance=new LogManager();
				}
			}
		}
		return instance;
	}
	
	public void init(){
		FILE_SEPARATOR = System.getProperty("file.separator");
		LINE_SEPARATOR = System.getProperty("line.separator");
		BASE_PATH = System.getProperty("user.dir");
		BASE_WORK_PATH=BASE_PATH+FILE_SEPARATOR+"autoPackage"+FILE_SEPARATOR+"work";
		BASE_OUT_PATH=BASE_PATH+FILE_SEPARATOR+"autoPackage"+FILE_SEPARATOR+"out";		
		OPTION_LOG_PATH=BASE_WORK_PATH+FILE_SEPARATOR+OPTION_FILE_NAME;

		File optionFile = new File(OPTION_LOG_PATH);
		if (!optionFile.getParentFile().exists()) {
			optionFile.getParentFile().mkdirs();
		}
		
		try {
			optionFile.createNewFile();
		} catch (Exception e) {
			
		}
		
		try {
			if (logPW == null) {
				logPW = new PrintWriter(optionFile);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isOptionFlag=true;//标示是否是用户操作
	
	public void setFlag(boolean isOptionFlag){
		this.isOptionFlag=isOptionFlag;
		if (!isOptionFlag) {
//			打包开始 设置输出日志到日志文件
			try {
				File gameOutDir = new File(GAME_OUT_PATH);
				if (!gameOutDir.exists()) {
					gameOutDir.mkdirs();
				}
				File gameLog=new File(GAME_OUT_PATH +FILE_SEPARATOR+game.getApk().getName()+"_log.txt");  
				gameLog.createNewFile();
				FileOutputStream fileOutputStream;
				fileOutputStream = new FileOutputStream(gameLog);
				PrintStream printStream = new PrintStream(fileOutputStream);  
				System.setOut(printStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			System.setOut(Model.getInstance().GetStandardOutStream());
		}
	}
	
	private Game game;
	
	public void setGame(Game game){
		this.game=game;
		GAME_OUT_PATH=BASE_OUT_PATH+FILE_SEPARATOR+game.getApk().getName();
	}
	
	private JTextArea optionArea;
	public void setOptionView(JTextArea optionArea){
		this.optionArea=optionArea;
	}
	/**
	 * 向log文件追加记录
	 * @param log
	 * @return
	 */
	public boolean appendLog(String log){
		if (isOptionFlag) {
//			1.输出到控制台
			System.out.println(log);
			
//			2.追加写入到视图上
			if (optionArea!=null) {
				optionArea.append(log);
				optionArea.append(LINE_SEPARATOR);
			}
	
//			3.写入到option文件
			try {
				logPW.println(log);
				logPW.flush();
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
}
