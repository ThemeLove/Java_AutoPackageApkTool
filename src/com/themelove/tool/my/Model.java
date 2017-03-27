package com.themelove.tool.my;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import com.themelove.tool.my.bean.ApktoolVersion;
import com.themelove.tool.my.bean.Game;
import com.themelove.tool.my.bean.PackageMethod;

/**
 * @author:lqs
 * date	  :2017年3月20日
 */
public class Model {
	/**
	 * 获取打包方式集合
	 */
	public static List<PackageMethod>  getPackageMethods() {
		ArrayList<PackageMethod> packageMethodList = new ArrayList<PackageMethod>();
		
		PackageMethod metaMethod = new PackageMethod();
		metaMethod.setMethod(PackageMethod.METHOD_META);
		metaMethod.setDesc("方式一：修改meta-data");
		
		PackageMethod assetMethod = new PackageMethod();
		assetMethod.setMethod(PackageMethod.METHOD_ASSET);
		assetMethod.setDesc("方式二：修改assets配置");
		
		PackageMethod quickMethod = new PackageMethod();
		quickMethod.setMethod(PackageMethod.METHOD_QUICK);
		quickMethod.setDesc("方式三：修改签名目录");
		
		packageMethodList.add(metaMethod);
		packageMethodList.add(assetMethod);
		packageMethodList.add(quickMethod);
		return packageMethodList;
	}
	
	/**
	 * 获取支持的Apktool版本集合
	 */
	public static List<ApktoolVersion> getApktoolVersions(String baseToolsPath) {
		ArrayList<ApktoolVersion> apktoolVersionList = new ArrayList<ApktoolVersion>();
		
		File toolsFile = new File(baseToolsPath);
		if (!toolsFile.exists()) toolsFile.mkdirs();

		File[] listFiles = toolsFile.listFiles(new java.io.FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory()&&pathname.getName().startsWith("apktool_")) {
					return true;
				}
				return false;
			}
		});
		
		for (File file : listFiles) {
			ApktoolVersion apktoolVersion= new ApktoolVersion();
			apktoolVersion.setPath(file.getAbsolutePath());
			apktoolVersion.setVersion(file.getName());
			apktoolVersionList.add(apktoolVersion);
		}
		return apktoolVersionList;
	}
	
	
	/**
	 * 初始化所有已经配置的游戏
	 */
	public static List<Game> getGames(String baseGamePath) {
		ArrayList<Game> gameList = new ArrayList<Game>();
		
//		初始化
		File baseGameFile = new File(baseGamePath);
		if (!baseGameFile.exists()) baseGameFile.mkdirs();
//		提示
		File[] games = baseGameFile.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					return true;
				}
				return false;
			}
		});
		
		for (File gameFile : games) {
			Game game = new Game();
			game.setName(gameFile.getName());
			game.setGamePath(gameFile.getAbsolutePath());
			gameList.add(game);
		}
		return gameList;
	}
}
