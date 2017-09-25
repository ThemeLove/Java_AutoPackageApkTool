package com.themelove.tool.my.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.themelove.tool.my.bean.Apk;
import com.themelove.tool.my.bean.ApktoolVersion;
import com.themelove.tool.my.bean.Channel;
import com.themelove.tool.my.bean.Game;
import com.themelove.tool.my.bean.Keystore;
import com.themelove.tool.util.CmdUtil;
import com.themelove.tool.util.FileUtil;
import com.themelove.tool.util.TextUtil;

/**
 *  动态修改apk包中asset目录下渠道配置文件方式打包Manager
 *	@author:qingshanliao
 *  @date  :2017年9月21日
 */
public class AssetPackageManager {
	private static AssetPackageManager instance=null;
	private AssetPackageManager(){}
	public static AssetPackageManager getInstance(){
		if (instance==null) {
			synchronized (AssetPackageManager.class) {
				if (instance==null) {
					instance=new AssetPackageManager();
				}
			}
		}
		return instance;
	}
	
	private Game game;
	private ApktoolVersion apktoolVersion;
	private String FILE_SEPARATOR;
	private String LINE_SEPARATOR;
	private String BASE_PATH;//当前项目根目录
	private String BAK_PATH;//Bak 根目录
	private String TEMP_PATH;//Temp 根目录
	private String BASE_TOOLS_PATH;//Tool 根目录
	private String GAME_OUT_PATH;//渠道包输出目录
	
	private Apk apk;
	private Channel channel;
	private Keystore keystore;
	
	public void init( ){
		FILE_SEPARATOR = System.getProperty("file.separator");
		LINE_SEPARATOR = System.getProperty("line.separator");
		BASE_PATH = System.getProperty("user.dir");
		BASE_TOOLS_PATH=BASE_PATH+FILE_SEPARATOR+"autoPackage"+FILE_SEPARATOR+"tools";
		
		String BASE_WORK_PATH=BASE_PATH+FILE_SEPARATOR+"autoPackage"+FILE_SEPARATOR+"work";
		BAK_PATH = BASE_WORK_PATH+FILE_SEPARATOR+"bak";
		TEMP_PATH = BASE_WORK_PATH+FILE_SEPARATOR+"temp";
	}

	public  void setGame(Game game) {
		this.game=game;
		this.apk=game.getApk();
		this.channel=game.getChannel();
		this.keystore=game.getKeystore();
		
		String BASE_OUT_PATH=BASE_PATH+FILE_SEPARATOR+"autoPackage"+FILE_SEPARATOR+"out";
		GAME_OUT_PATH = BASE_OUT_PATH+FILE_SEPARATOR+game.getApk().getName();
	}
	
	public void setApktoolVersion(ApktoolVersion apktoolVersion) {
		this.apktoolVersion=apktoolVersion;
	}

	public boolean assetAutoLoopPackage_Main(){
		boolean isStep1Success = assetStep1_init();
		if (!isStep1Success) {
			return false;
		}
		
		boolean isStep2Success = assetStep2_copyApk2BakAndTemp();
		if (!isStep2Success) {
			return false;
		}
		
		boolean isStep3Success = assetStep3_loopPackageWithChannels();
		if (!isStep3Success) {
			return false;
		}
		
		return true;
	}
	/**
	 * Asset方式打包初始化
	 * @return
	 */
	private boolean assetStep1_init(){
		System.out.println(LINE_SEPARATOR+LINE_SEPARATOR);
		System.out.println(">>>>>>>>>>>>>游戏【" + game.getApk().getFullName() + "】"+">>>>>>开始打包");
		System.out.println("打包方式：Asset(修改Asset下配置文件)");
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
		
//		(2)检查要打的渠道号集合是否存在
		System.out.println("*****(2):检查要打的渠道号集合是否存在");
		List<Map<String, String>> channelList = game.getChannel().getChannelList();
		if (channelList==null||channelList.size()==0) {
			System.out.println("error:----->打包渠道号不存在");
			return false;
		}
		
////		(3)检查当前选择的apktool目录是否存在
//		System.out.println("*****(3):检查当前apktool目录是否存在");
//		File apktoolDir = new File(apktoolVersion.getPath());
//		if (!apktoolDir.exists()) {
//			System.out.println("error:----->apktool目录不存在");
//			return false;
//		}
		
//		(3)检查检查7z.exe是否存在
		System.out.println("*****(3):检查7z.exe是否存在");
		String sevenZipPath=BASE_TOOLS_PATH+FILE_SEPARATOR+"7zip"+FILE_SEPARATOR+"7z.exe";
		File sevenZipFile = new File(sevenZipPath);
		if (!sevenZipFile.exists()) {
			System.out.println("error:----->7zip.exe不存在");
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
//		System.out.println("*****(5):添加公共资源framework-res.apk依赖");
//		CmdUtil.exeCmdWithLog("java -jar -Xms512m -Xmx512m apktool.jar if framework-res.apk",null, new File(apktoolVersion.getPath()));
//		
		System.out.println("1.初始化成功");
		return true;
	}
	
	/**
	 * 拷贝母包到Bak、Temp目录
	 * @return
	 */
	private boolean assetStep2_copyApk2BakAndTemp(){
		System.out.println(LINE_SEPARATOR+LINE_SEPARATOR);
		System.out.println("2:copy母包apk到bak和temp目录");		
		File sourceApk = new File(game.getApk().getApkPath());
		File bakApk = new File(BAK_PATH+FILE_SEPARATOR+game.getApk().getName()+".apk");
		File tempApk = new File(TEMP_PATH+FILE_SEPARATOR+game.getApk().getName()+".apk");
		boolean copyBak = FileUtil.copyFile(sourceApk, bakApk);
		if (copyBak) {
			System.out.println("----->copy母包apk到bak目录成功");
		}else{
			System.out.println("error:----->copy母包apk到bak目录失败...,请重试！");
			return false;
		}
		
		boolean copyTemp = FileUtil.copyFile(sourceApk, tempApk);
		if (copyTemp) {
			System.out.println("----->copy母包apk到Temp目录成功");
		}else{
			System.out.println("error:----->copy母包apk到Temp目录失败，请重试！");
			return false;
		}
		
		return true;
	}
	
	/**
	 * 根据渠道号循环修改asset目录下配置文件打包
	 * @return
	 */
	private boolean assetStep3_loopPackageWithChannels(){
		System.out.println(LINE_SEPARATOR+LINE_SEPARATOR);
		System.out.println("3：根据渠道号循环修改assets配置文件打包...");		
		ArrayList<String> finalChannelApks=new ArrayList<String>();//保存最终生成渠道包的名字集合
		
		for (int i=0;i<channel.getChannelList().size();i++) {
			Map<String,String> channelMap=channel.getChannelList().get(i);
			System.out.println(LINE_SEPARATOR);
			System.out.println("正在打第"+(i+1)+"个渠道包："+channelMap+"...");
			System.out.println("*****(1):根据渠道信息生成assets\\vasconfig\\channel.ini文件");
			
			File channelConfigFile = new File(TEMP_PATH+FILE_SEPARATOR+"assets"+FILE_SEPARATOR+"vasconfig"+FILE_SEPARATOR+"channel.ini");
			if (!channelConfigFile.getParentFile().exists()) {
				channelConfigFile.getParentFile().mkdirs();
			}
			try {
			boolean createConfig = channelConfigFile.createNewFile();
			if (createConfig) {
						BufferedWriter bw = new BufferedWriter(new FileWriter(channelConfigFile));
	//					以下拼接只适合Pptv渠道
						StringBuffer sb = new StringBuffer();
						sb.append("#")
						.append(channelMap.get("PptvVasSdk_CID"))
						.append(LINE_SEPARATOR)
						.append("#")
						.append(channelMap.get("PptvVasSdk_CCID"))
						.append(LINE_SEPARATOR)
						.append("#")
						.append(channelMap.get("PptvVasSdk_DebugMode").equals("false")?"0":"1");
						bw.write(sb.toString());
						bw.flush();
						bw.close();
						System.out.println("----->写入"+channelMap+"渠道信息到assets\\vasconfig\\channel.ini成功！");
					}else{
						System.out.println("error:----->创建assets\\vasconfig\\channel.ini文件失败！请重试...");
						return false;
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("error:----->写入"+channelMap+"渠道信息到assets\\vasconfig\\channel.ini失败！请重试...");
					return false;
				}
			
//			用生成的assets\\vasconfig\\channel.ini配置文件替换，temp目录中apk中assets\vasconfig\channel.ini文件，因为apk中assets目录下的文件，打包过程中的不做特殊编译处理，
//			所以这个过程不用反编译，直接用jdk 的jar命名即可完成
			System.out.println("*****(2):用生成的渠道配置文件channel.ini替换apk中的配置文件");
			String jarPath=apktoolVersion.getPath()+FILE_SEPARATOR+"jar.exe";
			String tempApkPath=apk.getName()+".apk";
//			注意这里要用"/"
			String configPath="assets/vasconfig/channel.ini";
//			exeCommandPath代表切换到该目录下执行jar命令
//			String exeCommandPath=TEMP_PATH;
//			String updateChannelCommand = String.format("%s uvfm %s %s", new String[]{jarPath,tempApkPath,configPath});
			String updateChannelCommand = String.format("%s uvf %s %s", new Object[]{jarPath,tempApkPath,configPath});
			System.out.println("updateChannelCommand:"+updateChannelCommand);
//			CmdUtil.exeCmdWithLog(updateChannelCommand);
			CmdUtil.exeCmdWithLog(updateChannelCommand, null, new File(TEMP_PATH));
			System.out.println("----->渠道配置文件channel.ini替换apk中的配置文件成功！");
			
//			删除之前的apk中的签名目录 META-INF,用空文件代替,因为jar命名没有直接删除的命令行，这里用7Zip来操作
			System.out.println("*****(3):用7zip删除apk中META-INF签名目录，用来重新签名...");
			String sevenZipPath=BASE_TOOLS_PATH+FILE_SEPARATOR+"7zip"+FILE_SEPARATOR+"7z.exe";
			String unSignApkPath=TEMP_PATH+FILE_SEPARATOR+apk.getName()+".apk";
			String metaInfoPath="META-INF";
			String deleteMetaINFCommand=String.format("%s d %s %s",new Object[]{sevenZipPath,unSignApkPath,metaInfoPath});
			CmdUtil.exeCmdWithLog(deleteMetaINFCommand, null, new File(TEMP_PATH));
			
			File unSignApkFile = new File(unSignApkPath);
			if (!unSignApkFile.exists()) {
				System.out.println("error:----->没有找到---未签名---的渠道包");
				return false;
			}
			
/*			String replaceMetaInfCommand = String.format("%s uvf %s %s",new String[]{jarPath,tempApkPath,metaInfPath});
			CmdUtil.exeCmdWithLog(replaceMetaInfCommand, null, new File(TEMP_PATH));
			System.out.println("	META-INF替换apk中的META-INF成功...");*/
			//3.用jarsigner.jar给未签名apk签名，应为apk中文件被更新了，所以要重新签名
//			String unSignApkPath=TEMP_PATH+FILE_SEPRATOR+currentApk.getName()+".apk";
			
			
			System.out.println("*****(4):正在重新签名apk...");
			String keystorePath=keystore.getKeystorePath();
			String signApkPath=GAME_OUT_PATH+FILE_SEPARATOR+apk.getName()+"_sign.apk";
//			String jarsignerPath=currentApktoolVersion.getPath()+FILE_SEPRATOR+"jarsigner.exe";
//			String jarsignerPath="E:/Develop/jdk1.8/bin/jarsigner.exe";
			String jarsignerPath="D:/jdk/bin/jarsigner.exe";
			String generateSignApkCommand = TextUtil.formatString("%s -digestalg SHA1 -sigalg SHA1withRSA -keystore %s -storepass %s -keypass %s -signedjar %s %s %s",
                    new String[] {jarsignerPath,keystorePath, keystore.getPassword(), keystore.getAliasPassword(), signApkPath,
					unSignApkPath,keystore.getAlias()});
			System.out.println("generateSignApkCommand:---"+generateSignApkCommand);
//			CmdUtil.exeCmd(generateSignApkCommand, null, apktoolFile);
			CmdUtil.exeCmdWithLog(generateSignApkCommand);
			
			File signApk = new File(signApkPath);
			if (!signApk.exists()) {
				System.out.println("error:----->生成签名包失败！");
				return false;
			}else{
				System.out.println("----->生成签名apk-----"+apk.getName()+"_sign.apk---成功");
			}
			
//			删除之前的未签名包
//			FileUtil.deleteFile(new File(unSignApkPath));
			
//			4.对已签名包对其优化
			System.out.println("*****(5):对齐优化生成最终渠道包...");
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
			String timeStamp = simpleDateFormat.format(new Date());
			String channelApkName=game.getApk().getName()+"_"+channelMap.get("PptvVasSdk_CID")+"_"+channelMap.get("PptvVasSdk_CCID")+"_"+channelMap.get("PptvVasSdk_DebugMode")+"_"+timeStamp+".apk";
			
			String channelApkPath=GAME_OUT_PATH+FILE_SEPARATOR+channelApkName;
			String zipalignPath=apktoolVersion.getPath()+FILE_SEPARATOR+"zipalign.exe";
			String generateChannelApkCommand = TextUtil.formatString("%s -v 4 %s %s", new String[] {zipalignPath,signApkPath, channelApkPath});
//			CmdUtil.exeCmd(generateSignApkCommand, null, apktoolFile);
			CmdUtil.exeCmdWithLog(generateChannelApkCommand);
			
			
			File channelApkFile = new File(channelApkPath);
			if (!channelApkFile.exists()) {
				System.out.println("error:----->生成渠道包失败！");
				return false;
			}else{
				System.out.println("----->生成最终渠道包-----"+channelApkName+"-----成功！");
//				删除之前签名的apk
				FileUtil.deleteFile(signApk);
//				删除临时渠道配置文件channel.ini文件
				FileUtil.deleteFile(new File(channelConfigFile.getAbsolutePath()));
//				记录生成的渠道包
				finalChannelApks.add(channelApkName);
			}
		}
		System.out.println("------多渠道打包完成，共"+finalChannelApks.size()+"个渠道包如下------");
		for (String finalChannelApk : finalChannelApks) {
			System.out.println(finalChannelApk);
		}
		return true;
	}
}
