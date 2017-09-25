package com.themelove.tool.my.model;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.themelove.tool.my.MyPackageFrame;
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
	private static Model instance;
	private Model(){}
	public static Model getInstance(){
		if (instance==null) {
			synchronized (Model.class) {
				if (instance==null) {
					instance=new Model();
				}
			}
		}
		return instance;
	}
	
	private PrintStream standardOutStream;
	
	/**
	 * 获取java标准输出流
	 */
	public PrintStream GetStandardOutStream(){
		return standardOutStream;
	}
	
	/**
	 * 设置保存java标准的输出流
	 * @param standardStream
	 */
	public void setStandardOutStream(PrintStream standardStream){
		this.standardOutStream=standardStream;
	}
	
	/**
	 * 获取打包方式集合
	 */
	public  List<PackageMethod>  getPackageMethods() {
		ArrayList<PackageMethod> packageMethodList = new ArrayList<PackageMethod>();
		
		PackageMethod metaMethod = new PackageMethod();
		metaMethod.setMethod(PackageMethod.METHOD_META);
		metaMethod.setDesc("方式一：修改meta-data");
		
		PackageMethod assetMethod = new PackageMethod();
		assetMethod.setMethod(PackageMethod.METHOD_ASSET);
		assetMethod.setDesc("方式二：修改assets配置");
		
		PackageMethod quickMethod = new PackageMethod();
		quickMethod.setMethod(PackageMethod.METHOD_QUICK);
		quickMethod.setDesc("方式三：快速打包-修改签名目录META-INF");
		
		packageMethodList.add(metaMethod);
		packageMethodList.add(assetMethod);
		packageMethodList.add(quickMethod);
		return packageMethodList;
	}
	
	/**
	 * 获取支持的Apktool版本集合
	 */
	public  List<ApktoolVersion> getApktoolVersions(String baseToolsPath) {
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
	public  List<Game> getGames(String baseGamePath) {
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
//			设置当前游戏的路径
			game.setGamePath(gameFile.getAbsolutePath());
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
	private  Keystore generateKeystoreFromConfig(File gameFile) {
		
		String keystoreName="";
		File keyStoreDirFile=new File(gameFile.getAbsolutePath()+MyPackageFrame.FILE_SEPARATOR+"keystore");
		
		if (keyStoreDirFile.isDirectory()&&keyStoreDirFile.listFiles().length>0) {
			File[] listFiles = keyStoreDirFile.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith(".keystore")||name.endsWith(".jks")) {
						return true;
					}
					return false;
				}
			});
			if (listFiles!=null&&listFiles.length>0) {//如果.keystore 或者.jks的文件，我们只取第一个
				keystoreName=listFiles[0].getName();
			}
		}
		
		String keystoreConfigPath=gameFile.getAbsolutePath()+MyPackageFrame.FILE_SEPARATOR+"keystore"+MyPackageFrame.FILE_SEPARATOR+"keystore.xml";
		File keystoreConfigFile = new File(keystoreConfigPath);
		if (!keystoreConfigFile.exists()) {
			return null;
		}
		
		Keystore keystore=new Keystore();
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
			keystore.setKeystorePath(gameFile.getAbsolutePath()+MyPackageFrame.FILE_SEPARATOR+"keystore"+MyPackageFrame.FILE_SEPARATOR+keystoreName);
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
		String channelConfigPath=gameFile.getAbsolutePath()+MyPackageFrame.FILE_SEPARATOR+"channel"+MyPackageFrame.FILE_SEPARATOR+"channel.xml";
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
			String apkConfigPath=gameFile.getAbsoluteFile()+MyPackageFrame.FILE_SEPARATOR+"apk"+MyPackageFrame.FILE_SEPARATOR+"apk.xml";
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(new File(apkConfigPath));
			Element apkElement = document.getRootElement();
			String apkName = apkElement.attributeValue("name");
			String fullName = apkElement.attributeValue("fullName");
			String desc = apkElement.attributeValue("desc");
			apk.setName(apkName);
			apk.setFullName(fullName);
			apk.setDesc(desc);
			apk.setApkPath(gameFile.getAbsolutePath()+MyPackageFrame.FILE_SEPARATOR+"apk"+MyPackageFrame.FILE_SEPARATOR+apkName+".apk");
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		return apk;
	}
	
	/**
	 * 换行符
	 */
	public static String LINE_SEPRATOR=System.getProperty("line.separator");
	/**
	 * 路径分割符
	 */
	public static String FILE_SEPRATOR=System.getProperty("file.separator");
	
//	/**
//	 * 获取pptv AndroidManifest.xml中的meta-data配置项
//	 * @return
//	 */
//	public  StringBuffer getPPTVReplaceMetaSB(){
//		StringBuffer replaceMetaSb = new StringBuffer();
////		默认值为PptvVasSdk_CID,PptvVasSdk_CCID,PptvVasSdk_DebugMode
//		replaceMetaSb.append("#").append("PptvVasSdk_CID").append(LINE_SEPRATOR)
//		.append("#").append("PptvVasSdk_CCID").append(LINE_SEPRATOR)
//		.append("#").append("PptvVasSdk_DebugMode");
//		return replaceMetaSb;
//	}
}
