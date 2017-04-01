package com.themelove.tool.my;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.RestoreAction;

import com.themelove.tool.gui.MyEditComboBox;
import com.themelove.tool.my.bean.ApktoolVersion;
import com.themelove.tool.my.bean.Game;
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
	private String LINE_SEPRATOR;
	/**
	 * 路径分割符
	 */
	private String FILE_SEPRATOR;
	
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
	 * 已经选择的渠道列表
	 */
	private List<String> gameChannels;
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
		FILE_SEPRATOR = System.getProperty("file.separator");
		LINE_SEPRATOR=System.getProperty("line.separator");
		
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
				gameChannels.clear();
				resultInfo.setText("");
				packageBtn.setEnabled(false);
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
		System.out.println(LINE_SEPRATOR+LINE_SEPRATOR+LINE_SEPRATOR);
		System.out.println("步骤四：根据渠道号循环打包...");
		
		for (String channel : gameChannels) {
			System.out.println("准备打---"+channel+"---渠道包...");
			switch (currentPackageMethod.getMethod()) {
			case PackageMethod.METHOD_META:
				System.out.println("	(1):---正在修改meta对应的元数据...");
				
				
				
				
				
				
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
		gameApkPath = currentGame.getGamePath()+FILE_SEPRATOR+"apk"+FILE_SEPRATOR+currentGame.getName()+".apk";
		File apkFile = new File(gameApkPath);
		
		if (apkFile.exists()){
			System.out.println("	游戏---"+currentGame.getName()+"---母包存在");
		} else{
			System.out.println("	游戏---"+currentGame.getName()+"---母包不存在,请检查后重试...");
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
		metaList = new ArrayList<String>();
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
		File gameOutDir = new File(BASE_OUT_PATH+FILE_SEPRATOR+currentGame.getName());
		
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
			System.out.println("	清空---"+currentGame.getName()+"---out目录成功...");
		}else{
			System.out.println("	清空---"+currentGame.getName()+"---out目录成功,请手动清空后重试...");
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
		
	}
	
	/**
	 * 快速 打包方式
	 */
	private void quickPackageChannels(){
		
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
//			生成对应游戏要打的渠道包
			File channelFile = new File(game.getGamePath()+FILE_SEPRATOR+"channel"+FILE_SEPRATOR+"channel.txt");
			if (channelFile.exists()){
				gameChannels = getChannelsFormFile(channelFile);
				if (gameChannels!=null) {
					StringBuffer info=new StringBuffer();
					for (String channel : gameChannels) {
						if (channel.isEmpty()) continue;
						info.append(channel).append(LINE_SEPRATOR);
					}
					channelsInfo.setText(info.toString());
				}
			}else{
				System.out.println("游戏:-----"+game.getName()+"----渠道文件不存在");
			}
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
	private ArrayList<String> metaList;
	



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
	 * 多渠道循环自动打包
	 */
	protected void autoPackageChannels() {
//		一：初始化
//		Step1_init();
//		二：根据打包方式，反编译或者解压母包到bak目录
//		三：拷贝bak目录到temp目录
//		step3_copyBak2Temp();
//		四：根据打包方式，循环修改渠道号
		step4_loopPackageWithChannels();
	}

	
	/**
	 * 步骤四
	 * 根据渠道号循环打包
	 */
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
	}
}
