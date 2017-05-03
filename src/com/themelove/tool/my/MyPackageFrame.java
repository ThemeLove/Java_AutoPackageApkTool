package com.themelove.tool.my;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.themelove.tool.gui.MyEditComboBox;
import com.themelove.tool.my.bean.Apk;
import com.themelove.tool.my.bean.ApktoolVersion;
import com.themelove.tool.my.bean.Channel;
import com.themelove.tool.my.bean.Game;
import com.themelove.tool.my.bean.Keystore;
import com.themelove.tool.my.bean.PackageMethod;
import com.themelove.tool.util.CmdUtil;
import com.themelove.tool.util.FileUtil;
import com.themelove.tool.util.TextUtil;

/**
 *	@author:qingshanliao
 *  @date  :2017年3月16日
 */
public class MyPackageFrame extends JFrame {
	/**
	 * 换行符
	 */
	public static String LINE_SEPRATOR=System.getProperty("line.separator");
	/**
	 * 路径分割符
	 */
	public static String FILE_SEPRATOR=System.getProperty("file.separator");
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 打包按钮
	 */
	private JButton packageBtn;
	/**
	 * 将要打包的渠道信息
	 */
	private JTextArea channelsInfo;
	/**
	 * 打包过程信息
	 */
	private JTextArea resultInfo;
	/**
	 * 保存渠道号按钮
	 */
	private JButton saveChannelBtn;
	/**
	 * log文件查看按钮
	 */
	private JButton logBtn;

	
	/**
	 * 当前项目的根目录
	 */
	private String BASE_PATH;
	/**
	 * 游戏根目录
	 */
	private String BASE_GAME_PATH;
	
	/**
	 * 工具根目录
	 */
	private String BASE_TOOLS_PATH;
	/**
	 * apktool根目录
	 */
	private String APKTOOL_PATH;
	
	/**
	 * work根目录
	 */
	private String BASE_WORK_PATH;
	/**
	 * bak目录，用来存放母包反编译或解压生成的文件
	 */
	private String BAK_PATH;
	/**
	 * 多渠道打包的临时目录
	 */
	private String TEMP_PATH;
	/**
	 * 渠道包输出根目录
	 */
	private String BASE_OUT_PATH;
	
	/**
	 * 当前选择游戏母包路径
	 */
	private String gameApkPath;
	/**
	 * 7Zip的路径
	 */
	private String zipFilePath;
	
	private MyEditComboBox<PackageMethod> packageMethodComboBox;
	private MyEditComboBox<ApktoolVersion> apktoolVersionComboBox;
	private MyEditComboBox<Game> gameComboBox;
	
	/**
	 * 支持的打包方式集合
	 */
	private List<PackageMethod> packageMethodList=new ArrayList<PackageMethod>();
	/**
	 * 支持的apktool版本集合
	 */
	private List<ApktoolVersion> apktoolVersionList=new ArrayList<ApktoolVersion>();
	/**
	 * 支持的游戏集合
	 */
	private List<Game> gameList=new ArrayList<Game>();

	/**
	 * 当前选择的打包方式
	 */
	private PackageMethod currentPackageMethod;
	/**
	 * 当前选择的Apktool版本
	 */
	private ApktoolVersion currentApktoolVersion;
	/**
	 * 当前打包的游戏
	 */
	private Game		   currentGame;
	/**
	 * 当前游戏对应的apk
	 */
	private Apk			   currentApk;
	/**
	 * 当前apk要打的渠道包集合
	 */
	private Channel        currentChannel;
	/**
	 * 当前apk对应的keystore信息
	 */
	private Keystore	   currentKeystore;

	/**
	 * Create the frame.
	 */
	public MyPackageFrame() {
		initData();
		initView();
		addListener();
	}

	/**
	 * 初始化数据，路径什么的
	 */
	private void initData() {
		
		BASE_PATH = System.getProperty("user.dir");
		BASE_GAME_PATH = BASE_PATH+FILE_SEPRATOR+"autoPackage"+FILE_SEPRATOR+"games";
		BASE_TOOLS_PATH=BASE_PATH+FILE_SEPRATOR+"autoPackage"+FILE_SEPRATOR+"tools";
		BASE_WORK_PATH = BASE_PATH+FILE_SEPRATOR+"autoPackage"+FILE_SEPRATOR+"work";
		BASE_OUT_PATH=BASE_PATH+FILE_SEPRATOR+"autoPackage"+FILE_SEPRATOR+"out";
		BAK_PATH = BASE_WORK_PATH+FILE_SEPRATOR+"bak";
		TEMP_PATH = BASE_WORK_PATH+FILE_SEPRATOR+"temp";
		
		System.out.println("basePath:"+BASE_PATH);
		System.out.println("gamePath:"+BASE_GAME_PATH);
		System.out.println("toolPath:"+BASE_TOOLS_PATH);
		
		replaceMetaSb = new StringBuffer();
//		默认值为PptvVasSdk_CID,PptvVasSdk_CCID,PptvVasSdk_DebugMode
		replaceMetaSb.append("#").append("PptvVasSdk_CID").append(LINE_SEPRATOR)
		.append("#").append("PptvVasSdk_CCID").append(LINE_SEPRATOR)
		.append("#").append("PptvVasSdk_DebugMode");
		
		packageMethodList = Model.getPackageMethods();
	    apktoolVersionList = Model.getApktoolVersions(BASE_TOOLS_PATH);
		gameList = Model.getGames(BASE_GAME_PATH);
	}

	/**
	 * 初始化UI视图
	 */
	private void initView() {
		setTitle("MyVasPackageTool");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 702, 538);
		getContentPane().setLayout(null);
		
//		打包方式
		packageMethodComboBox = new MyEditComboBox<PackageMethod>("打包方式","请选择打包方式", packageMethodItemListener);
		packageMethodComboBox.setBounds(10, 11, 187, 45);
		packageMethodComboBox.setVisible(true);
		
		packageMethodComboBox.updateComboBox(packageMethodList);
		getContentPane().add(packageMethodComboBox);
//		apktool版本
		apktoolVersionComboBox = new MyEditComboBox<ApktoolVersion>("选择apktool版本","请选择游戏",apktoolVersionItemListener);
		apktoolVersionComboBox.setBounds(10, 64, 187, 48);
		apktoolVersionComboBox.updateComboBox(apktoolVersionList);
		getContentPane().add(apktoolVersionComboBox);
//		选择游戏
		gameComboBox = new MyEditComboBox<Game>("请选择游戏","请选择游戏",gameItemListener);
		gameComboBox.setBounds(10, 119, 187, 47);
		gameComboBox.updateComboBox(gameList);
		getContentPane().add(gameComboBox);
		
		metaLabel = new JLabel("替换的meta字段");
		metaLabel.setBounds(204, 31, 107, 18);
		getContentPane().add(metaLabel);
		
		replaceMetaPane = new JTextPane();
		replaceMetaPane.setBounds(327, 0, 343, 69);
		replaceMetaPane.setText(replaceMetaSb.toString());
		getContentPane().add(replaceMetaPane);
		
		JLabel label = new JLabel("渠道信息如下：");
		label.setBounds(10, 171, 107, 18);
		getContentPane().add(label);
		
//		显示将要打包的渠道
		JScrollPane channelsScrollPane = new JScrollPane();
		channelsScrollPane.setBounds(10, 189, 187, 289);
		channelsInfo = new JTextArea();
		channelsInfo.setForeground(Color.BLUE);
		channelsInfo.setBackground(new Color(0xcccccc, false));
		channelsScrollPane.setViewportView(channelsInfo);
		getContentPane().add(channelsScrollPane);
		
//		动态显示打包过程
		JScrollPane resultScrollPane = new JScrollPane();
		resultScrollPane.setBounds(204, 71, 466, 352);
		resultInfo = new JTextArea();
		resultInfo.setForeground(Color.BLACK);
		resultInfo.setBackground(new Color(0xccffcc));
		resultScrollPane.setViewportView(resultInfo);
		getContentPane().add(resultScrollPane);
		
//		保存渠道按钮
		saveChannelBtn = new JButton("保存渠道号");
		saveChannelBtn.setBounds(204, 430, 119, 48);
		getContentPane().add(saveChannelBtn);
		
//		查看log文件按钮
		logBtn = new JButton("log文件");
		logBtn.setBounds(358, 430, 102, 48);
		getContentPane().add(logBtn);
		
		resetBtn = new JButton("重置");
		resetBtn.setBounds(474, 430, 94, 48);
		getContentPane().add(resetBtn);
		
		
//		打包按钮
		packageBtn = new JButton("打包");
		packageBtn.setBounds(582, 430, 88, 48);
		getContentPane().add(packageBtn);
	}
	
	
	/**
	 * 添加点击事件
	 */
	private void addListener() {
		
		channelsInfo.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// 删除时的监听
				// 内容变化时的监听
				packageBtn.setEnabled(!(e.getLength()==0));
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// 插入时的监听
				// 内容变化时的监听
				packageBtn.setEnabled(!(e.getLength()==0));
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// 内容变化时的监听,一般用不到
//					packageBtn.setEnabled(e.getLength()==0);
			}
		});
		
		saveChannelBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//点击保存渠道号按钮
				
			}
		});
		
		logBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		resetBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub ,重置选项
				packageMethodComboBox.setSelectedIndex(-1);
				apktoolVersionComboBox.setSelectedIndex(-1);
				gameComboBox.setSelectedIndex(-1);
				currentPackageMethod=null;
				currentApktoolVersion=null;
				currentGame=null;
				replaceMetaPane.setText("");
				
				metaList.clear();
				channelsInfo.setText("");
				resultInfo.setText("");
				packageBtn.setEnabled(false);
				
				initData();
				replaceMetaPane.setText(replaceMetaSb.toString());
			}
		});
		
		packageBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
//				根据打包方式多渠道打包
				switch (currentPackageMethod.getMethod()) {
				case PackageMethod.METHOD_META:
					metaPackageChannels();
					
					break;
				case PackageMethod.METHOD_ASSET:
					assetPackageChannels();
					
					break;
				case PackageMethod.METHOD_QUICK:
					quickPackageChannels();
					
					break;
				default:
					break;
				}
				
//				真正多渠道打包
				autoPackageChannels();
			}

		});
	}
	

	/**
	 * meta元数据 打包方式
	 */
	private void metaPackageChannels() {
//		初始化目录
		metaStep1Init();
		metaStep2DeCompileApk2Bak();
		copyBak2Temp();
		metaStep4LoopPackageWithChannels();
	}
	
	/**
	 * 循环修改AndroidManifest.xml打包
	 */
	private void metaStep4LoopPackageWithChannels() {
		System.out.println(LINE_SEPRATOR + LINE_SEPRATOR + LINE_SEPRATOR);
		System.out.println("步骤四：根据渠道号循环打包...");
		
//		1.修改meta元数据
			for (Map<String, String> channelMap : currentChannel.getChannelList()) {
				System.out.println("准备打---" + channelMap + "---渠道包---");
				System.out.println("	(1):---正在修改meta对应的元数据...");
	
				StringBuffer channel = new StringBuffer();
				// AndroidManifest.xml文件目录
				String manifestPath = TEMP_PATH + FILE_SEPRATOR+ "AndroidManifest.xml";
				SAXReader saxReader = new SAXReader();
				try {
					Document document = saxReader.read(new File(manifestPath));
					Element rootElement = document.getRootElement();
					List<Element> metas = rootElement.element("application").elements("meta-data");
					for (Element meta : metas) {
						for (String name : channelMap.keySet()) {
							if (name.equals(meta.attributeValue("name"))) {
								meta.remove(meta.attribute("value"));
								meta.addAttribute("android:value", channelMap.get(name));
								channel.append("_").append(channelMap.get(name));
							}
						}
					}
					 OutputFormat xmlFormat = new OutputFormat("    ", true, "UTF-8");
					
				    XMLWriter writer = new XMLWriter(new FileWriter(new File(manifestPath)), xmlFormat);
	                writer.write(document);
	                writer.close();
				} catch (Exception e) {
	
				}
				System.out.println("修改meta成功");
	//			2.根据当前选择的apktool版本用apktool.jar给修改过渠道号的资源汇编成一个未签名的apk
				System.out.println("	(2):---正在回编成一个未签名的apk...");
				String unSignApkPath=BASE_OUT_PATH+FILE_SEPRATOR+currentApk.getName()+FILE_SEPRATOR+currentApk.getName()+"_unsign.apk";
				File apktoolFile = new File(currentApktoolVersion.getPath());
	//			未签名apk的保存路径
				String generateUnSignApkCommand = TextUtil.formatString("java -jar apktool.jar b -o %s %s", new String[] {unSignApkPath, TEMP_PATH});
				CmdUtil.exeCmdWithLog(generateUnSignApkCommand, null, apktoolFile);
				System.out.println("生成未签名:"+currentApk.getName()+"_unsign.apk"+"成功！");
				
	//			3.用jarsigner.jar给未签名apk签名
				File unSignApkFile = new File(unSignApkPath);
				
				if (!unSignApkFile.exists()) {
					System.out.println("没有找到---未签名---的渠道包");
					return;
				}
				System.out.println("	(3):---正在签名apk...");
				String keystorePath=currentKeystore.getKeystorePath();
				String signApkPath=BASE_OUT_PATH+FILE_SEPRATOR+currentApk.getName()+FILE_SEPRATOR+currentApk.getName()+"_sign.apk";
	//			String jarsignerPath=currentApktoolVersion.getPath()+FILE_SEPRATOR+"jarsigner.exe";
	//			String jarsignerPath="E:/Develop/jdk1.8/bin/jarsigner.exe";
				String jarsignerPath="D:/jdk/bin/jarsigner.exe";
				String generateSignApkCommand = TextUtil.formatString("%s -digestalg SHA1 -sigalg SHA1withRSA -keystore %s -storepass %s -keypass %s -signedjar %s %s %s",
	                    new String[] {jarsignerPath,keystorePath, currentKeystore.getPassword(), currentKeystore.getAliasPassword(), signApkPath,
						unSignApkPath,currentKeystore.getAlias()});
				System.out.println("generateSignApkCommand:---"+generateSignApkCommand);
	//			CmdUtil.exeCmd(generateSignApkCommand, null, apktoolFile);
				CmdUtil.exeCmdWithLog(generateSignApkCommand);
				System.out.println("生成签名---"+currentApk.getName()+"_sign.apk---成功");
	//			删除之前的未签名包
	//			FileUtil.deleteFile(new File(unSignApkPath));
				
				File signApkFile = new File(signApkPath);
				
				if (!signApkFile.exists()) {
					System.out.println("没有找到---签名---的渠道包");
					return;
				}
				
	//			4.对已签名包对其优化
				String channelApkPath=BASE_OUT_PATH+FILE_SEPRATOR+currentApk.getName()+FILE_SEPRATOR+currentApk.getName()+channel.toString()+".apk";
				System.out.println("	(4)---:对齐优化签名包...");
				String zipalignPath=currentApktoolVersion.getPath()+FILE_SEPRATOR+"zipalign.exe";
				String generateChannelApkCommand = TextUtil.formatString("%s -v 4 %s %s", new String[] {zipalignPath,signApkPath, channelApkPath});
	//			CmdUtil.exeCmd(generateSignApkCommand, null, apktoolFile);
				CmdUtil.exeCmdWithLog(generateChannelApkCommand);
				System.out.println("生成最终渠道包---"+currentApk.getName()+channel.toString()+".apk---成功");
				
				File channelApkFile = new File(channelApkPath);
				
				
				if (!channelApkFile.exists()) {
					System.out.println("没有找到---最终的---渠道包");
					return;
				}
				
	//			删除之前签名包
				FileUtil.deleteFile(new File(signApkPath));
			}
			
			System.out.println("循环打包完成。。。");
		}

	/**
	 * copy Bak目录到Temp目录
	 */
	private void copyBak2Temp() {
		System.out.println(LINE_SEPRATOR+LINE_SEPRATOR+LINE_SEPRATOR);
		System.out.println("步骤三：拷贝bak目录到temp目录...");
		
		File bakDir = new File(BAK_PATH);
		File tempDir = new File(TEMP_PATH);
		boolean copyDir = FileUtil.copyDir(bakDir, tempDir);
		if (copyDir) {
			System.out.println("	拷贝---bak----->temp----成功！");
		} else {
			System.out.println("	拷贝---bak----->temp----失败！，请手动操作后重试...");
			return;
		}
	}

	/**
	 * 反编译apk到Bak目录
	 */
	private void metaStep2DeCompileApk2Bak() {
		System.out.println(LINE_SEPRATOR+LINE_SEPRATOR+LINE_SEPRATOR);
		System.out.println("步骤二：---反编译apk到Bak目录");
		File apktoolFile = new File(currentApktoolVersion.getPath());
		String decompileApkCommand=TextUtil.formatString("java -jar -Xms512m -Xmx512m apktool.jar d -f -s -o %s %s", new String[]{BAK_PATH,gameApkPath});
		CmdUtil.exeCmdWithLog(decompileApkCommand, null, apktoolFile);
		System.out.println("	反编译apk到Bak目录成功");
	}
	
	/**
	 * meta 元数据打包方式 step1 init
	 */
	private void metaStep1Init() {
		System.out.println("开始打包...");
		System.out.println("打包方式:---"+currentPackageMethod.getDesc());
		System.out.println(LINE_SEPRATOR+LINE_SEPRATOR+LINE_SEPRATOR);
		System.out.println("步骤一:---初始化");
		
		System.out.println("	(1):---正在检查游戏母包是否存在...");
//		1.检查母包是否存在
		gameApkPath = currentApk.getApkPath();
		File apkFile = new File(gameApkPath);
		
		if (apkFile.exists()){
			System.out.println("	游戏---"+currentApk.getName()+"---母包存在");
		} else{
			System.out.println("	游戏---"+currentApk.getName()+"---母包不存在,请检查后重试...");
			return;
		}
		
//		2.检查要替换的Meta字段格式是否正确
		System.out.println();
		System.out.println("	(2):---正在检查将要替换的Meta字段是否正确...");
		String replaceMetaStr = replaceMetaPane.getText().trim();
		if (!replaceMetaStr.contains("#")) {
			System.out.println("	请用#号分隔");
			return;
		}
		if (!replaceMetaStr.startsWith("#")) {
			System.out.println("	请以#号开头");
			return;
		}
		String[] split = replaceMetaStr.split("#");
		for (String metaStr : split) {
			if (!metaStr.isEmpty()) {
				metaList.add(metaStr);
			}
		}
		
		
//		3.清空打包过程中用到的目录
		System.out.println();
		System.out.println("	(3):清空打包过程中用到的目录...");
		
		File bakDir = new File(BAK_PATH);
		File tempDir = new File(TEMP_PATH);
		File gameOutDir = new File(BASE_OUT_PATH+FILE_SEPRATOR+currentApk.getName());
		
		boolean deleteBakDir = FileUtil.deleteFiles(bakDir);
		if (deleteBakDir) {
			System.out.println("	清空bak目录成功...");
		}else{
			System.out.println("	清空bak目录不成功，请手动清空后重试...");
			return;
		}
		
		boolean deleteTempDir = FileUtil.deleteFiles(tempDir);
		if (deleteTempDir) {
			System.out.println("	清空temp目录成功...");
		}else{
			System.out.println("	清空temp目录不成功，请手动清空后重试...");
			return;
		}
		
		boolean deleteGameOutDir = FileUtil.deleteFiles(gameOutDir);
		if (deleteGameOutDir) {
			System.out.println("	清空---"+currentApk.getName()+"---out目录成功...");
		}else{
			System.out.println("	清空---"+currentApk.getName()+"---out目录成功,请手动清空后重试...");
			return;
		}
//		4.添加公共资源依赖
		System.out.println();
		System.out.println("	(4)添加公共资源---framework-res.apk---依赖...");
		CmdUtil.exeCmdWithLog("java -jar -Xms512m -Xmx512m apktool.jar if framework-res.apk",null, new File(currentApktoolVersion.getPath()));
		System.out.println("	添加公共资源---framework-res.apk---依赖成功");
	}

	/**
	 * Asset配置文件 打包方式
	 */
	private void assetPackageChannels(){
		assetStep1Init();
		copyApk2BakAndTemp();
		assetStep3LoopPackageWithChannels();
	}
	
	/**
	 * asset 资产目录打包方式 step Init
	 */
	private void assetStep1Init() {
		System.out.println("开始打包...");
		System.out.println("打包方式:---"+currentPackageMethod.getDesc());
		System.out.println(LINE_SEPRATOR+LINE_SEPRATOR+LINE_SEPRATOR);
		System.out.println("步骤一:---初始化");
		
		System.out.println("	(1):---正在检查游戏母包是否存在...");
//		1.检查母包是否存在
		gameApkPath = currentApk.getApkPath();
		File apkFile = new File(gameApkPath);
		
		if (apkFile.exists()){
			System.out.println("	游戏---"+currentApk.getName()+"---母包存在");
		} else{
			System.out.println("	游戏---"+currentApk.getName()+"---母包不存在,请检查后重试...");
			return;
		}
		
//		2.检查打包渠道是否存在
		System.out.println();
		System.out.println("	(2):---正在检查打包渠道是否存在...");
		if (currentChannel.getChannelList()==null||currentChannel.getChannelList().size()==0) {
			System.out.println("	打包渠道不存在，请检查...");
			return;
		}
		System.out.println("	打包渠道存在...");
		
//		3.清空打包过程中用到的目录
		System.out.println();
		System.out.println("	(3):清空打包过程中用到的目录...");
		
		File bakDir = new File(BAK_PATH);
		File tempDir = new File(TEMP_PATH);
		File gameOutDir = new File(BASE_OUT_PATH+FILE_SEPRATOR+currentApk.getName());
		
		boolean deleteBakDir = FileUtil.deleteFiles(bakDir);
		if (deleteBakDir) {
			System.out.println("	清空bak目录成功...");
		}else{
			System.out.println("	清空bak目录不成功，请手动清空后重试...");
			return;
		}
		
		boolean deleteTempDir = FileUtil.deleteFiles(tempDir);
		if (deleteTempDir) {
			System.out.println("	清空temp目录成功...");
		}else{
			System.out.println("	清空temp目录不成功，请手动清空后重试...");
			return;
		}
		
		boolean deleteGameOutDir = FileUtil.deleteFiles(gameOutDir);
		if (deleteGameOutDir) {
			System.out.println("	清空---"+currentApk.getName()+"---out目录成功...");
		}else{
			System.out.println("	清空---"+currentApk.getName()+"---out目录成功,请手动清空后重试...");
			return;
		}
//		4.添加公共资源依赖
		System.out.println();
		System.out.println("	(4)添加公共资源---framework-res.apk---依赖...");
		CmdUtil.exeCmdWithLog("java -jar -Xms512m -Xmx512m apktool.jar if framework-res.apk",null, new File(currentApktoolVersion.getPath()));
		System.out.println("	添加公共资源---framework-res.apk---依赖成功");
		
	}
	/**
	 * 拷贝apk到Bak目录和Temp目录
	 */
	private void copyApk2BakAndTemp() {
		System.out.println(LINE_SEPRATOR+LINE_SEPRATOR+LINE_SEPRATOR);
		System.out.println("步骤二：---copy母包apk到bak和temp目录");		
		File sourceApk = new File(currentApk.getApkPath());
		File bakApk = new File(BAK_PATH+FILE_SEPRATOR+currentApk.getName()+".apk");
		File tempApk = new File(TEMP_PATH+FILE_SEPRATOR+currentApk.getName()+".apk");
		boolean copyBak = FileUtil.copyFile(sourceApk, bakApk);
		if (copyBak) {
			System.out.println("	copy母包apk到bak目录成功");
		}else{
			System.out.println("	copy母包apk到bak目录失败...,请重试！");
			return;
		}
		
		boolean copyTemp = FileUtil.copyFile(sourceApk, tempApk);
		if (copyTemp) {
			System.out.println("	copy母包apk到Temp目录成功");
		}else{
			System.out.println("	copy母包apk到Temp目录失败，请重试！");
			return;
		}
	}
	/**
	 * 循环生成配置文件多渠道打包
	 */
	@SuppressWarnings("resource")
	private void assetStep3LoopPackageWithChannels() {
		System.out.println(LINE_SEPRATOR+LINE_SEPRATOR+LINE_SEPRATOR);
		System.out.println("步骤三：---根据渠道循环打包...");		
		for (Map<String,String> channelMap : currentChannel.getChannelList()) {
			System.out.println("准备打---" + channelMap + "---渠道包---");
			System.out.println("	(1):---根据渠道信息生成assets\\vasconfig\\channel.ini文件");
			
			File channelConfigFile = new File(TEMP_PATH+FILE_SEPRATOR+"assets"+FILE_SEPRATOR+"vasconfig"+FILE_SEPRATOR+"channel.ini");
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
						.append(LINE_SEPRATOR)
						.append("#")
						.append(channelMap.get("PptvVasSdk_CCID"))
						.append(LINE_SEPRATOR)
						.append("#")
						.append(channelMap.get("PptvVasSdk_DebugMode"));
						bw.write(sb.toString());
						bw.flush();
						bw.close();
						System.out.println("	生成"+channelMap+"渠道信息到assets\\vasconfig\\channel.ini文件成功！");
					}else{
						System.out.println("	创建assets\\vasconfig\\channel.ini文件失败！请重试...");
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("	生成"+channelMap+"渠道信息到assets\\vasconfig\\channel.ini文件失败！请重试...");
					return;
				}
			
//			用生成的assets\\vasconfig\\channel.ini配置文件替换，temp目录中apk中assets\vasconfig\channel.ini文件，因为apk中assets目录下的文件，打包过程中的不做特殊编译处理，
//			所以这个过程不用反编译，直接用jdk 的jar命名即可完成
			System.out.println("	(2):---用生成的渠道配置文件channel.ini替换apk中的配置文件");
			String jarPath=currentApktoolVersion.getPath()+FILE_SEPRATOR+"jar.exe";
			String tempApkPath=currentApk.getName()+".apk";
//			注意这里要用"/"
			String configPath="assets/vasconfig/channel.ini";
//			exeCommandPath代表切换到该目录下执行jar命令
			String exeCommandPath=TEMP_PATH;
			String updateChannelCommand = String.format("%s uvf %s %s", new String[]{jarPath,tempApkPath,configPath});
//			String updateChannelCommand = String.format("%s uvf %s %s", new String[]{jarPath,tempApkPath,configPath});
			System.out.println(updateChannelCommand);
//			CmdUtil.exeCmdWithLog(updateChannelCommand);
			CmdUtil.exeCmdWithLog(updateChannelCommand, null, new File(TEMP_PATH));
			System.out.println("	渠道配置文件channel.ini替换apk中的配置文件成功！");
			
//			删除之前的apk中的签名目录 META-INF
			
			
			//3.用jarsigner.jar给未签名apk签名，应为apk中文件被更新了，所以要重新签名
			System.out.println("	(3):---正在签名apk...");
			String unSignApkPath=TEMP_PATH+FILE_SEPRATOR+currentApk.getName()+".apk";
			File unSignApkFile = new File(unSignApkPath);
			
			if (!unSignApkFile.exists()) {
				System.out.println("没有找到---未签名---的渠道包");
				return;
			}
			String keystorePath=currentKeystore.getKeystorePath();
			String signApkPath=BASE_OUT_PATH+FILE_SEPRATOR+currentApk.getName()+FILE_SEPRATOR+currentApk.getName()+"_sign.apk";
//			String jarsignerPath=currentApktoolVersion.getPath()+FILE_SEPRATOR+"jarsigner.exe";
//			String jarsignerPath="E:/Develop/jdk1.8/bin/jarsigner.exe";
			String jarsignerPath="D:/jdk/bin/jarsigner.exe";
			String generateSignApkCommand = TextUtil.formatString("%s -digestalg SHA1 -sigalg SHA1withRSA -keystore %s -storepass %s -keypass %s -signedjar %s %s %s",
                    new String[] {jarsignerPath,keystorePath, currentKeystore.getPassword(), currentKeystore.getAliasPassword(), signApkPath,
					unSignApkPath,currentKeystore.getAlias()});
			System.out.println("generateSignApkCommand:---"+generateSignApkCommand);
//			CmdUtil.exeCmd(generateSignApkCommand, null, apktoolFile);
			CmdUtil.exeCmdWithLog(generateSignApkCommand);
			System.out.println("生成签名---"+currentApk.getName()+"_sign.apk---成功");
//			删除之前的未签名包
//			FileUtil.deleteFile(new File(unSignApkPath));
			
//			4.对已签名包对其优化
			System.out.println("	(4)---:对齐优化签名包...");
			File signApkFile = new File(signApkPath);
			if (!signApkFile.exists()) {
				System.out.println("没有找到---签名---的渠道包");
				return;
			}
			
			StringBuffer channel = new StringBuffer();
			channel.append("_")
			.append(channelMap.get("PptvVasSdk_CID"))
			.append("_")
			.append(channelMap.get("PptvVasSdk_CCID"))
			.append("_")
			.append(channelMap.get("PptvVasSdk_DebugMode"));
			
			String channelApkPath=BASE_OUT_PATH+FILE_SEPRATOR+currentApk.getName()+FILE_SEPRATOR+currentApk.getName()+channel.toString()+".apk";
			String zipalignPath=currentApktoolVersion.getPath()+FILE_SEPRATOR+"zipalign.exe";
			String generateChannelApkCommand = TextUtil.formatString("%s -v 4 %s %s", new String[] {zipalignPath,signApkPath, channelApkPath});
//			CmdUtil.exeCmd(generateSignApkCommand, null, apktoolFile);
			CmdUtil.exeCmdWithLog(generateChannelApkCommand);
			System.out.println("生成最终渠道包---"+currentApk.getName()+channel.toString()+".apk---成功");
			
			File channelApkFile = new File(channelApkPath);
			
			
			if (!channelApkFile.exists()) {
				System.out.println("没有找到---最终的---渠道包");
				return;
			}
			
//			删除之前签名包
			FileUtil.deleteFile(new File(signApkPath));
//			删除渠道配置文件channel.ini文件
			FileUtil.deleteFile(new File(channelConfigFile.getAbsolutePath()));
		}
			
		System.out.println("循环打包完成。。。");
	}
	
	/**
	 * 快速 打包方式：修改apk中META-INF目录，在其中新增空文件，文件名用来表示渠道信息
	 */
	private void quickPackageChannels(){
		quickStep1Init();
		copyApk2BakAndTemp();
		quickStep3LoopPackageWithChannels();
	}

	/**
	 * 快速打包方式：Step1 init
	 */
	private void quickStep1Init() {
		System.out.println("开始打包...");
		System.out.println("打包方式:---"+currentPackageMethod.getDesc());
		System.out.println(LINE_SEPRATOR+LINE_SEPRATOR+LINE_SEPRATOR);
		System.out.println("步骤一:---初始化");
		
		System.out.println("	(1):---正在检查游戏母包是否存在...");
//		1.检查母包是否存在
		gameApkPath = currentApk.getApkPath();
		File apkFile = new File(gameApkPath);
		
		if (apkFile.exists()){
			System.out.println("	游戏---"+currentApk.getName()+"---母包存在");
		} else{
			System.out.println("	游戏---"+currentApk.getName()+"---母包不存在,请检查后重试...");
			return;
		}
		
		
//		2.检查打包渠道是否存在
		System.out.println();
		System.out.println("	(2):---正在检查打包渠道是否存在...");
		if (currentChannel.getChannelList()==null||currentChannel.getChannelList().size()==0) {
			System.out.println("	打包渠道不存在，请检查...");
			return;
		}
		System.out.println("	打包渠道存在...");
		
//		3.清空打包过程中用到的目录
		System.out.println();
		System.out.println("	(3):清空打包过程中用到的目录...");
		
		File bakDir = new File(BAK_PATH);
		File tempDir = new File(TEMP_PATH);
		File gameOutDir = new File(BASE_OUT_PATH+FILE_SEPRATOR+currentApk.getName());
		
		boolean deleteBakDir = FileUtil.deleteFiles(bakDir);
		if (deleteBakDir) {
			System.out.println("	清空bak目录成功...");
		}else{
			System.out.println("	清空bak目录不成功，请手动清空后重试...");
			return;
		}
		
		boolean deleteTempDir = FileUtil.deleteFiles(tempDir);
		if (deleteTempDir) {
			System.out.println("	清空temp目录成功...");
		}else{
			System.out.println("	清空temp目录不成功，请手动清空后重试...");
			return;
		}
		
		boolean deleteGameOutDir = FileUtil.deleteFiles(gameOutDir);
		if (deleteGameOutDir) {
			System.out.println("	清空---"+currentApk.getName()+"---out目录成功...");
		}else{
			System.out.println("	清空---"+currentApk.getName()+"---out目录成功,请手动清空后重试...");
			return;
		}

	}
	
	private void quickStep3LoopPackageWithChannels() {
		System.out.println(LINE_SEPRATOR+LINE_SEPRATOR+LINE_SEPRATOR);
		System.out.println("步骤三：---根据渠道循环打包...");		
		
		for (Map<String,String> channelMap : currentChannel.getChannelList()) {
			System.out.println("准备打---" + channelMap + "---渠道包---");
			System.out.println("	(1):---根据渠道信息生成META-INF\\channel_xxx_xxx_xxx.txt渠道文件");
			StringBuffer channelSb = new StringBuffer();
			for (Entry<String,String> entry : channelMap.entrySet()) {
				if (entry.getKey().equals("PptvVasSdk_CID")&&!TextUtil.isEmpty(entry.getValue())) {
					channelSb.append("_")
					.append(entry.getValue());
				}
				if (entry.getKey().equals("PptvVasSdk_CCID")&&!TextUtil.isEmpty(entry.getValue())) {
					channelSb.append("_")
					.append(entry.getValue());
				}
				if (entry.getKey().equals("PptvVasSdk_DebugMode")&&!TextUtil.isEmpty(entry.getValue())) {
					channelSb.append("_")
					.append(entry.getValue());
				}
			}
			String channelConfigFilePath=TEMP_PATH+FILE_SEPRATOR+"META-INF"+FILE_SEPRATOR+"channel"+channelSb.toString()+".txt";
			File channelConfigFile = new File(channelConfigFilePath);
			if (!channelConfigFile.getParentFile().exists()) {
				channelConfigFile.getParentFile().mkdirs();
			}
			try {
				boolean createNewFile = channelConfigFile.createNewFile();
				if (createNewFile) {
					System.out.println("	---生成渠道配置文件META-INF\\channel"+channelSb.toString()+".txt成功!");
				}else{
					System.out.println("	---生成渠道配置文件META-INF\\channel"+channelSb.toString()+".txt失败!,请重试");
					return;
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("	---生成渠道配置文件META-INF\\channel"+channelSb.toString()+".txt失败!,请重试");
				return;
			}
			
			System.out.println("	(2):---将生成的渠道配置文件channel"+channelSb.toString()+".txt"+"加到META-INF目录中");
			String sevenZipPath=BASE_TOOLS_PATH+FILE_SEPRATOR+"7Zip"+FILE_SEPRATOR+"7z.exe";
			String apkPath=TEMP_PATH+FILE_SEPRATOR+currentApk.getName()+".apk";
			String channelPath="META-INF/channel"+channelSb.toString()+".txt";
			
//			用7zip命名行向apk的META-INF目录添加生成的渠道配置文件
			String updateChannelFile2METAINFCommand=String.format("%s u %s %s", new String[]{sevenZipPath,apkPath,channelPath});
			CmdUtil.exeCmdWithLog(updateChannelFile2METAINFCommand, null, new File(TEMP_PATH));
			System.out.println("	---添加渠道配置文件channel"+channelSb.toString()+".txt"+"到META-INF目录中成功");
			
//			Syso
			System.out.println("	(3)---拷贝渠道apk到输出目录");
			
			String channelApkPath=BASE_OUT_PATH+FILE_SEPRATOR+currentApk.getName()+FILE_SEPRATOR+currentApk.getName()+channelSb.toString()+".apk";
			boolean copyChannelApk = FileUtil.copyFile(new File(apkPath), new File(channelApkPath));
			if (copyChannelApk) {
				System.out.println("	---拷贝渠道apk到输出目录成功!");
			}else{
				System.out.println("	---拷贝渠道apk到输出目录失败!");
				return;
			}
			
//			用7zip命令删除META-INF目录下的渠道配置文件
			
			System.out.println("	(4)---用7zip命令删除META-INF目录下的渠道配置文件");
			String deleteChannelFileFromMETAINFCommand = String.format("%s d %s %s", new String[]{sevenZipPath,apkPath,channelPath});
			CmdUtil.exeCmdWithLog(deleteChannelFileFromMETAINFCommand, null, new File(TEMP_PATH));
			System.out.println("	删除META-INF目录下的渠道配置文件成功!");
		}
		System.out.println("多渠道打包完成!");
	}

	@SuppressWarnings({ "unchecked", "unchecked" })
	private MyEditComboBox.OnComboBoxItemClickListener<PackageMethod> packageMethodItemListener=new MyEditComboBox.OnComboBoxItemClickListener<PackageMethod>(){
		@Override
		public void OnItemClickListener(PackageMethod packageMethod) {
			currentPackageMethod=packageMethod;
			metaLabel.setVisible(currentPackageMethod.getMethod()==PackageMethod.METHOD_META);
			replaceMetaPane.setVisible(currentPackageMethod.getMethod()==PackageMethod.METHOD_META);
		}
		
		@Override
		public void OnEditInputListener(String inputText) {
			List<PackageMethod> temp=new ArrayList<>();
			if (inputText.isEmpty()) {
				temp=packageMethodList;
			}else{
				for (PackageMethod packageMethod : packageMethodList) {
					if (packageMethod.getDesc().contains(inputText)) {
						temp.add(packageMethod);
					};
				}
			}
			packageMethodComboBox.updateComboBox(temp);
		}
	};
	
	@SuppressWarnings({ "unchecked", "unchecked" })
	private MyEditComboBox.OnComboBoxItemClickListener<ApktoolVersion> apktoolVersionItemListener=new MyEditComboBox.OnComboBoxItemClickListener<ApktoolVersion>(){

		@Override
		public void OnItemClickListener(ApktoolVersion apktoolVersion) {
			currentApktoolVersion=apktoolVersion;
			
		}

		@Override
		public void OnEditInputListener(String inputText) {
			List<ApktoolVersion> temp=new ArrayList<ApktoolVersion>();
			if (inputText.isEmpty()) {
				System.out.println("回调：apktoolVersion		OnEditInputListener------"+"empty");
				temp=apktoolVersionList;
			}else{
				for (ApktoolVersion apktoolVersion : apktoolVersionList) {
					String version = apktoolVersion.getVersion();
					if (version.contains(inputText)) {
						temp.add(apktoolVersion);
					}
				}
			}
			System.out.println("回调：apktoolVersion   OnEditInputListener-----"+LINE_SEPRATOR+temp.toString());
			apktoolVersionComboBox.updateComboBox(temp);
		}
	};
	
	@SuppressWarnings({ "unchecked", "unchecked" })
	private MyEditComboBox.OnComboBoxItemClickListener<Game> gameItemListener=new MyEditComboBox.OnComboBoxItemClickListener<Game>(){


		public void OnItemClickListener(Game game) {
			channelsInfo.setText("");
			System.out.println("回调：game		OnItemClickListener------"+game.toString());
			currentGame=game;
			currentApk=game.getApk();
			currentChannel=game.getChannel();
			currentKeystore=game.getKeystore();
			
			List<Map<String, String>> channelList = currentChannel.getChannelList();
			StringBuffer sb = new StringBuffer();
			for (Map<String, String> ChannelMap : channelList) {
				sb.append("channel:").append(LINE_SEPRATOR);
				for (Entry<String,String> entry : ChannelMap.entrySet()) {
					sb.append(entry.getKey()).append(":").append(entry.getValue()).append(LINE_SEPRATOR);
				}
			}
			channelsInfo.setText(sb.toString());
		}

		@Override
		public void OnEditInputListener(String inputText) {
			
		}
	};
	/**
	 * 重置按钮
	 */
	private JButton resetBtn;
	private JLabel metaLabel;
	/**
	 * 显示替换Meta的Pane
	 */
	private JTextPane replaceMetaPane;
	/**
	 * 替换meta中的字段信息
	 */
	private StringBuffer replaceMetaSb;
	/**
	 * 保存替换meta中的字段集合
	 */
	private ArrayList<String> metaList = new ArrayList<String>();
	



	/**
	 * 从文件中解析渠道列表
	 * @param selectedFile
	 */
	@SuppressWarnings("resource")
	protected List<String> getChannelsFormFile(File file) {
		ArrayList<String> channels=new ArrayList<String>();
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
			BufferedReader br = new BufferedReader(inputStreamReader);
			String line=null;
			while ((line=br.readLine())!=null) {
				String channel = line.trim();
				if (channel.isEmpty()) continue;
				channels.add(channel);
			}
			return channels;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从xml配置文件中获取渠道信息
	 * @param file
	 * @return
	 */
	private List<Map<String,String>> getChannelsFormXml(File file){
		ArrayList<Map<String, String>> channelsData = new ArrayList<Map<String,String>>();
		
		SAXReader saxReader = new SAXReader();
		try {
			Document reader = saxReader.read(file);
			Element rootElement = reader.getRootElement();
			List<Element> channelList = rootElement.elements("channel");
			for (Element channel : channelList) {
				HashMap<String,String> channelMap = new HashMap<>();
				List<Attribute> attributes = channel.attributes();
				for (Attribute attribute : attributes) {
					channelMap.put(attribute.getName(), attribute.getValue());
				}
				channelsData.add(channelMap);
			}
			
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return channelsData;
	}
	
	/**
	 * 多渠道循环自动打包
	 */
	protected void autoPackageChannels() {
//		一：初始化
//		Step1_init();
//		二：根据打包方式，反编译或者解压母包到bak目录
//		三：拷贝bak目录到temp目录
//		step3_copyBak2Temp();
//		四：根据打包方式，循环修改渠道号
//		step4_loopPackageWithChannels();
	}

/*	
	*//**
	 * 步骤四
	 * 根据渠道号循环打包
	 *//*
	private void step4_loopPackageWithChannels() {
		System.out.println(LINE_SEPRATOR+LINE_SEPRATOR+LINE_SEPRATOR);
		System.out.println("步骤四：根据渠道号循环打包...");
		
		for (String channel : gameChannels) {
			System.out.println("准备打---"+channel+"---渠道包...");
			switch (currentPackageMethod.getMethod()) {
			case PackageMethod.METHOD_META:
				
				break;
			case PackageMethod.METHOD_ASSET:
//				修改
//				1.配置文件目录
				File channelFile = new File(TEMP_PATH+FILE_SEPRATOR+"assets"+FILE_SEPRATOR+"vasconfig"+FILE_SEPRATOR+"channel.ini");
				try {
					BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(channelFile)));
					StringBuffer sb = new StringBuffer();
					String[] channelArray = channel.split("_");
					if (channelArray.length==1) {
						sb.append("#").append(channelArray[0]).append(LINE_SEPRATOR)
						.append("#").append(LINE_SEPRATOR)
						.append("#").append("0");
					}else{
						sb.append("#").append(channelArray[0]).append(LINE_SEPRATOR)
						.append("#").append(channelArray[1]).append(LINE_SEPRATOR)
						.append("#").append("0");
					}
					br.write(sb.toString().trim());
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("1.成功修改渠道号为---"+channel);
				
//				2.根据当前选择的apktool版本用apktool.jar给修改过渠道号的资源汇编成一个未签名的apk
				String unSignApkPath=BASE_OUT_PATH+FILE_SEPRATOR+currentGame.getName()+FILE_SEPRATOR+currentGame.getName()+"_unsign.apk";
				File apktoolFile = new File(currentApktoolVersion.getPath());
//				未签名apk的保存路径
				String generateUnSignApkCommand = TextUtil.formatString("java -jar apktool.jar b -o %s %s", new String[] {unSignApkPath, TEMP_PATH});
				CmdUtil.exeCmdWithLog(generateUnSignApkCommand, null, apktoolFile);
				
//				用7zip将修改过渠道号的资源回压缩成一个未签名的apk
//				String generateUnSignApkWith7ZipCommand=TextUtil.formatString("%s a %s %s", new String[]{zipFilePath,unSignApkPath,TEMP_PATH});
//				CmdUtil.exeCmdWithLog(generateUnSignApkWith7ZipCommand);
				
				System.out.println("生成未签名---"+currentGame.getName()+"_unsign.apk---成功");
				
//				3.用jarsigner.jar给未签名apk签名
				String keystorePath=currentGame.getGamePath()+FILE_SEPRATOR+"keystore"+FILE_SEPRATOR+currentGame.getName()+".keystore";
				String signApkPath=BASE_OUT_PATH+FILE_SEPRATOR+currentGame.getName()+FILE_SEPRATOR+currentGame.getName()+"_sign.apk";
//				String jarsignerPath=currentApktoolVersion.getPath()+FILE_SEPRATOR+"jarsigner.exe";
//				String jarsignerPath="E:/Develop/jdk1.8/bin/jarsigner.exe";
				String jarsignerPath="D:/jdk/bin/jarsigner.exe";
				String generateSignApkCommand = TextUtil.formatString("%s -digestalg SHA1 -sigalg SHA1withRSA -keystore %s -storepass %s -keypass %s -signedjar %s %s %s",
	                    new String[] {jarsignerPath,keystorePath, currentGame.getName(), currentGame.getName(), signApkPath,
						unSignApkPath,"ThemeLove"});
//				CmdUtil.exeCmd(generateSignApkCommand, null, apktoolFile);
				CmdUtil.exeCmdWithLog(generateSignApkCommand);
				System.out.println("生成签名---"+currentGame.getName()+"_sign.apk---成功");
//				删除之前的未签名包
//				FileUtil.deleteFile(new File(unSignApkPath));
				
//				4.对已签名包对其优化
				String channelApkPath=BASE_OUT_PATH+FILE_SEPRATOR+currentGame.getName()+FILE_SEPRATOR+currentGame.getName()+"_"+channel+".apk";
				String zipalignPath=currentApktoolVersion.getPath()+FILE_SEPRATOR+"zipalign.exe";
				String generateChannelApkCommand = TextUtil.formatString("%s -v 4 %s %s", new String[] {zipalignPath,signApkPath, channelApkPath});
//				CmdUtil.exeCmd(generateSignApkCommand, null, apktoolFile);
				CmdUtil.exeCmdWithLog(generateChannelApkCommand);
				System.out.println("生成最终渠道包---"+currentGame.getName()+"_"+channel+".apk---成功");
//				删除之前签名包
//				FileUtil.deleteFile(new File(signApkPath));
				
				break;
			case PackageMethod.METHOD_QUICK:
				
				break;

			default:
				break;
			}
		}
	}*/
}
