package com.themelove.tool.my.manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.themelove.tool.my.bean.ApktoolVersion;
import com.themelove.tool.my.bean.Game;
import com.themelove.tool.util.CmdUtil;
import com.themelove.tool.util.FileUtil;
import com.themelove.tool.util.TextUtil;

/**
 *  反编译动态修改AndroidManifest.xml文件中meta-data方式打包Manager
 *	@author:qingshanliao
 *  @date  :2017年9月20日
 */
public class MetaPackageManager {
	private Game game;//用户当前选择打包的游戏
	private ApktoolVersion apktoolVersion;//用户当前选择的apktool版本
	private static MetaPackageManager instance=null;
	private  String BASE_PATH;//当前工程的根目录
	private String FILE_SEPARATOR;
	private String LINE_SEPARATOR;
	private String BAK_PATH;
	private String TEMP_PATH;
	private String GAME_OUT_PATH;
	
	private MetaPackageManager(){
		
	}
	
	public static MetaPackageManager getInstance(){
		if (instance==null) {
			synchronized (MetaPackageManager.class) {
				if (instance==null) {
					instance=new MetaPackageManager();
				}
			}
		}
		return instance;
	}
	
	public  void init( ){
		
		FILE_SEPARATOR = System.getProperty("file.separator");
		LINE_SEPARATOR = System.getProperty("line.separator");
		BASE_PATH = System.getProperty("user.dir");
		
		String BASE_WORK_PATH=BASE_PATH+FILE_SEPARATOR+"autoPackage"+FILE_SEPARATOR+"work";
		BAK_PATH = BASE_WORK_PATH+FILE_SEPARATOR+"bak";
		TEMP_PATH = BASE_WORK_PATH+FILE_SEPARATOR+"temp";
	}
	
	public void setGame(Game game){
		this.game=game;
		String BASE_OUT_PATH=BASE_PATH+FILE_SEPARATOR+"autoPackage"+FILE_SEPARATOR+"out";
		GAME_OUT_PATH = BASE_OUT_PATH+FILE_SEPARATOR+game.getApk().getName();
	}

	public void setApktoolVersion(ApktoolVersion apktoolVersion){
		this.apktoolVersion=apktoolVersion;
	}
	
	/**
	 * Meta方式循环打包的主程序入口
	 */
	public  boolean  metaAutoLoopPackage_Main(){
//		meta方式打包初始化
		boolean isStep1Success = metaStep1_init();
		if (!isStep1Success) {
			return false;
		}
		
//		反编译游戏母包到bak目录
		metaStep2_deCompileApk2Bak();
//		拷贝Bak目录到Temp目录
		boolean isStep3Success = metaStep3_copyBak2Temp();
		if (!isStep3Success) {
			return false;
		}
		
//		根据渠道号循环修改AndroidManifest.xml打包
		boolean isStep4Success = metaStep4_loopPackageWithChannels();
		if (!isStep4Success) {
			return false;
		}
		return true;
	}

	/**
	 * meta方式打包的初始化
	 */
	private boolean metaStep1_init(){
		
		System.out.println(LINE_SEPARATOR+LINE_SEPARATOR);
		System.out.println(">>>>>>>>>>>>>游戏【" + game.getApk().getFullName() + "】"+">>>>>>开始打包");
		System.out.println("打包方式：MetaData(修改AndroidManifest.xml)");
		System.out.println(LINE_SEPARATOR+LINE_SEPARATOR);
		System.out.println("1:初始化...");
		
//		(1)检查游戏母包是否存在
		System.out.println("*****(1):检查所选游戏母包是否存在");
		File gameApk = new File(game.getApk().getApkPath());
		if (!gameApk.exists()) {
			System.out.println("error:----->游戏母包不存在...");
			return false;
		}else{
			System.out.println("----->成功获取到母包"+gameApk.getName());
		}
//		
////		(2)检查要替换的meta字段格式是否正确
//		System.out.println("*****(2):检查要替换的meta字段格式是否正确");
//		String repaceMetaStr=model.getPPTVReplaceMetaSB().toString().trim();
//		if (repaceMetaStr.isEmpty()) {
//			System.out.println("error:----->替换meta为空");
//			return false;
//		}else if(!repaceMetaStr.startsWith("#")){
//			System.out.println("error:----->替换meta字段没有以#开头");
//			return false;
//		}
//		
//		metas = repaceMetaStr.split("#");
		
//		(2)检查要打的渠道号集合是否存在
		System.out.println("*****(2):检查要打的渠道号集合是否存在");
		List<Map<String, String>> channelList = game.getChannel().getChannelList();
		if (channelList==null||channelList.size()==0) {
			System.out.println("error:----->打包渠道号不存在");
			return false;
		}
		
//		(3)检查当前选择的apktool目录是否存在
		System.out.println("*****(3):检查当前apktool目录是否存在");
		File apktoolDir = new File(apktoolVersion.getPath());
		if (!apktoolDir.exists()) {
			System.out.println("error:----->apktool目录不存在");
			return false;
		}
		
//		(4)清空打包过程中的工作目录bak、temp
		System.out.println("*****(4):清空打包过程中的工作目录bak、temp");
		File bakDir = new File(BAK_PATH);
		if (!FileUtil.deleteFiles(bakDir)) {
			System.out.println("error:----->清空bak目录失败");
			return false;
		}
		
		File tempDir = new File(TEMP_PATH);
		if (!FileUtil.deleteFiles(tempDir)) {
			System.out.println("error:----->清空temp目录失败");
			return false;
		}
		
//		File gameOutDir = new File(GAME_OUT_PATH);
//		if (!FileUtil.deleteFiles(gameOutDir)) {
//			System.out.println("error:----->清空游戏输出目录失败");
//			return false;
//		}
//		(5)添加公共资源依赖
		System.out.println("*****(5):添加公共资源framework-res.apk依赖");
		CmdUtil.exeCmdWithLog("java -jar -Xms512m -Xmx512m apktool.jar if framework-res.apk",null, new File(apktoolVersion.getPath()));
		
		System.out.println("1.初始化成功");
		return true;
	}
	
	/**
	 * 反编译游戏母包到bak目录
	 */
	private void metaStep2_deCompileApk2Bak(){
		System.out.println(LINE_SEPARATOR+LINE_SEPARATOR);
//		反编译游戏母包到bak目录
		System.out.println("2.反编译游戏母包到bak目录");
		File apktoolFile = new File(apktoolVersion.getPath());
		String decompileApkCommand=TextUtil.formatString("java -jar -Xms512m -Xmx512m apktool.jar d -f -s -o %s %s", new String[]{BAK_PATH,game.getApk().getApkPath()});
		System.out.println("decompileApkCommand----->"+decompileApkCommand);
		CmdUtil.exeCmdWithLog(decompileApkCommand, null, apktoolFile);
		System.out.println("2反编译apk到Bak目录成功");
	}
	
	/**
	 * 拷贝Bak目录到Temp目录
	 */
	private boolean  metaStep3_copyBak2Temp(){
		System.out.println(LINE_SEPARATOR+LINE_SEPARATOR);
//		拷贝Bak目录到Temp目录
		System.out.println("3.拷贝Bak目录到Temp目录");
		if (!FileUtil.copyDir(new File(BAK_PATH),new File(TEMP_PATH))) {
			System.out.println("error:----->拷贝Bak目录到Temp目录出错");
			return false;
		}
		System.out.println("3.拷贝Bak目录到Temp目录成功");
		return true;
	}
	
	/**
	 * 根据渠道号循环修改AndroidManifest.xml打包
	 */
	private boolean metaStep4_loopPackageWithChannels(){
		System.out.println(LINE_SEPARATOR+LINE_SEPARATOR);
//		根据渠道号循环修改AndroidManifest.xml打包
		System.out.println("4.根据渠道号循环修改AndroidManifest.xml打包");
		String manifestPath=TEMP_PATH+FILE_SEPARATOR+"AndroidManifest.xml";
		File manifestFile = new File(manifestPath);
		ArrayList<String> finalChannelApks=new ArrayList<String>();//保存最终生成渠道包的名字集合
		
		List<Map<String, String>> channelList = game.getChannel().getChannelList();
		for (int i = 0; i <channelList.size(); i++) {
			Map<String, String> channel = channelList.get(i);
			System.out.println(LINE_SEPARATOR);
			System.out.println("正在打第"+(i+1)+"个渠道包："+channel+"...");
//			(1)修改AndroidManifest.xml元数据
			System.out.println("*****(1):修改AndroidManifest.xml元数据");
			boolean isModifyMetaSuccess = modifyMetaWithChannel(manifestFile, channel);
			if (!isModifyMetaSuccess) {
				return false;
			}
			
//			(2)回编已修改的AndroidManifest.xml成未签名的apk
			System.out.println("*****(2):回编已修改的AndroidManifest.xml成未签名的apk");
			String unSignApkName=game.getApk().getName()+"_unSign.apk";
			String unSignApkPath=GAME_OUT_PATH+FILE_SEPARATOR+unSignApkName;
			String generateUnSignApkCommand=String.format(Locale.getDefault(),"java -jar apktool.jar b -o %s %s", new Object[]{unSignApkPath,TEMP_PATH});
			System.out.println("generateUnSignApkCommand----->"+generateUnSignApkCommand);
			CmdUtil.exeCmdWithLog(generateUnSignApkCommand, null, new File(apktoolVersion.getPath()));
			if (!new File(unSignApkPath).exists()) {
				System.out.println("error:----->生成未签名的apk失败！");
				return false;
			}else{
				System.out.println("----->生成未签名apk-----"+unSignApkName+"-----成功！");
			}
			
//			(3)生成签名包
			System.out.println("*****(3):生成签名包");
//			String jarsignerPath=apktoolVersion.getPath()+FILE_SEPARATOR+"jarsigner.exe";
			String keystorePath=game.getKeystore().getKeystorePath();
			String signApkName=game.getApk().getName()+"_sign.apk";
			String signApkPath=GAME_OUT_PATH+FILE_SEPARATOR+signApkName;
			String generateSignApkCommand=String.format(Locale.getDefault(),"%s -verbose -digestalg SHA1 -sigalg SHA1withRSA -keystore %s -storepass %s -keypass %s -signedjar %s %s %s",
					new Object[]{"jarsigner",keystorePath,game.getKeystore().getPassword(),game.getKeystore().getAliasPassword(),signApkPath,unSignApkPath,game.getKeystore().getAlias()});
			System.out.println("generateSignApkCommand----->"+generateSignApkCommand);
			CmdUtil.exeCmdWithLog(generateSignApkCommand, null, new File(apktoolVersion.getPath()));
			
			File signApk = new File(signApkPath);
			if (!signApk.exists()) {
				System.out.println("error:----->生成签名包失败！");
				return false;
			}else{
				System.out.println("----->生成签名apk-----"+signApkName+"-----成功！");
//				删除之前的未签名的apk
				FileUtil.deleteFile(new File(unSignApkPath));
			}
			
//			(4)签名、优化生成最终包
			System.out.println("*****(4):对齐优化生成最终渠道包");
			String zipAlignPath=apktoolVersion.getPath()+FILE_SEPARATOR+"zipalign.exe";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
			String timeStamp = simpleDateFormat.format(new Date());
			
			String channelApkName=game.getApk().getName()+"_"+channel.get("PptvVasSdk_CID")+"_"+channel.get("PptvVasSdk_CCID")+"_"+channel.get("PptvVasSdk_DebugMode")+"_"+timeStamp+".apk";
			String channelApkPath=GAME_OUT_PATH+FILE_SEPARATOR+channelApkName;
			String generateChannelApkCommand=String.format(Locale.getDefault(),"%s -v 4 %s %s",new Object[]{zipAlignPath,signApkPath,channelApkPath});
			System.out.println("generateChannelApkCommand----->"+generateChannelApkCommand);
			CmdUtil.exeCmdWithLog(generateChannelApkCommand, null, new File(apktoolVersion.getPath()));
			if (!new File(channelApkPath).exists()) {
				System.out.println("error:----->生成渠道包失败！");
				return false;
			}else{
				System.out.println("----->生成最终渠道包-----"+channelApkName+"-----成功！");
//				删除之前的签名的apk
				FileUtil.deleteFile(signApk);
				finalChannelApks.add(channelApkName);
			}
		}
		
		System.out.println("------多渠道打包完成，共"+finalChannelApks.size()+"个渠道包如下------");
		for (String finalChannelApk : finalChannelApks) {
			System.out.println(finalChannelApk);
		}
		
		return true;
	}

	/**
	 * 根据channel修改Manifest.xml中的meta
	 * @param manifestFile		AndroidManifest.xml文件
	 * @param channel			渠道号集合
	 * @return					是否修改成功
	 */
	private boolean modifyMetaWithChannel(File manifestFile,Map<String, String> channel) {
		boolean isModifySuccess =false;
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(manifestFile);
			Element rootElement = document.getRootElement();
			List<Element> metas = rootElement.element("application").elements("meta-data");
			for (Element meta : metas) {
				for (String key : channel.keySet()) {
					if (key.equals(meta.attributeValue("name"))) {
						meta.remove(meta.attribute("value"));
						meta.addAttribute("android:value", channel.get(key));
						isModifySuccess=true;
					}
				}
			}
			OutputFormat xmlFormat = new OutputFormat("    ", true, "UTF-8");
			XMLWriter xmlWriter = new XMLWriter(new FileWriter(manifestFile),xmlFormat);
			xmlWriter.write(document);
			xmlWriter.close();
			if (!isModifySuccess) {
				System.out.println("error:----->AndroidManifest.xml中没有找到要修改的meta字段");
				return false;
			}
		} catch (DocumentException|IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
