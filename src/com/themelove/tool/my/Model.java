package com.themelove.tool.my;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.themelove.tool.my.bean.Apk;
import com.themelove.tool.my.bean.ApktoolVersion;
import com.themelove.tool.my.bean.Channel;
import com.themelove.tool.my.bean.Game;
import com.themelove.tool.my.bean.Keystore;
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
//			1.解析游戏apk的配置文件
			Apk apk = generateApkFromConfig(gameFile);
			game.setApk(apk);
			
//			2.解析channel的配置文件
			Channel channel = generateChannelFromConfig(gameFile);
			game.setChannel(channel);
//			3.解析keystore的配置文件
			Keystore keystore = generateKeystoreFromConfig(gameFile);
			
			game.setKeystore(keystore);
			gameList.add(game);
		}
		
		return gameList;
	}

	/**
	 * 从配置文件中生成Keystore对象
	 * @param gameFile
	 * @return
	 */
	private static Keystore generateKeystoreFromConfig(File gameFile) {
		Keystore keystore = new Keystore();
		String keystoreConfigPath=gameFile.getAbsolutePath()+MyPackageFrame.FILE_SEPRATOR+"keystore"+MyPackageFrame.FILE_SEPRATOR+"keystore.xml";
		
		SAXReader saxReader = new SAXReader();
		Document document;
		try {
			document = saxReader.read(new File(keystoreConfigPath));
			Element keystoreElement = document.getRootElement();
			String password = keystoreElement.attributeValue("password");
			String alias = keystoreElement.attributeValue("alias");
			String aliasPassword = keystoreElement.attributeValue("aliasPassword");
			keystore.setPassword(password);
			keystore.setAliasPassword(aliasPassword);
			keystore.setAlias(alias);
			keystore.setKeystorePath(gameFile.getAbsolutePath()+MyPackageFrame.FILE_SEPRATOR+"keystore"+MyPackageFrame.FILE_SEPRATOR+gameFile.getName()+".keystore");
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return keystore;
	}

	/**
	 * 从配置文件中生成Channel对象
	 * @param gameFile
	 * @return
	 */
	private static Channel generateChannelFromConfig(File gameFile) {
		Channel channel = new Channel();
		String channelConfigPath=gameFile.getAbsolutePath()+MyPackageFrame.FILE_SEPRATOR+"channel"+MyPackageFrame.FILE_SEPRATOR+"channel.xml";
		ArrayList<Map<String, String>> channelsData = new ArrayList<Map<String,String>>();
		SAXReader saxReader = new SAXReader();
		try {
			Document reader = saxReader.read(new File(channelConfigPath));
			Element rootElement = reader.getRootElement();
			List<Element> channelList = rootElement.elements("channel");
			for (Element channelTemp : channelList) {
				HashMap<String,String> channelMap = new HashMap<>();
				List<Attribute> attributes = channelTemp.attributes();
				for (Attribute attribute : attributes) {
					channelMap.put(attribute.getName(), attribute.getValue());
				}
				channelsData.add(channelMap);
			}
			channel.setChannelList(channelsData);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return channel;
	}

	/**
	 * 从配置文件中生成Apk对象
	 * @param gameFile
	 * @return
	 * @throws DocumentException
	 */
	private static Apk generateApkFromConfig(File gameFile) {
		Apk apk = new Apk();
		try {
			String apkConfigPath=gameFile.getAbsoluteFile()+MyPackageFrame.FILE_SEPRATOR+"apk"+MyPackageFrame.FILE_SEPRATOR+"apk.xml";
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(new File(apkConfigPath));
			Element apkElement = document.getRootElement();
			String apkName = apkElement.attributeValue("name");
			String fullName = apkElement.attributeValue("fullName");
			String desc = apkElement.attributeValue("desc");
			apk.setName(apkName);
			apk.setFullName(fullName);
			apk.setDesc(desc);
			apk.setApkPath(gameFile.getAbsolutePath()+MyPackageFrame.FILE_SEPRATOR+"apk"+MyPackageFrame.FILE_SEPRATOR+apkName+".apk");
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return apk;
	}
	
	
}
