package com.themelove.tool.my;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.themelove.tool.my.bean.Apk;
import com.themelove.tool.my.bean.ApktoolVersion;
import com.themelove.tool.my.bean.Channel;
import com.themelove.tool.my.bean.Game;
import com.themelove.tool.my.bean.PackageMethod;
import com.themelove.tool.my.gui.MyEditComboBox;
import com.themelove.tool.my.manager.AssetPackageManager;
import com.themelove.tool.my.manager.LogManager;
import com.themelove.tool.my.manager.MetaPackageManager;
import com.themelove.tool.my.manager.QuickPackageManager;
import com.themelove.tool.my.model.Model;
import com.themelove.tool.util.CmdUtil;

/**
 *	@author:qingshanliao
 *  @date  :2017年3月16日
 */
public class MyPackageFrame extends JFrame {
	private static final long serialVersionUID = 1L;
//*****************	路径相关 *****************
	public static String LINE_SEPARATOR=System.getProperty("line.separator");	//换行符
	public static String FILE_SEPARATOR=System.getProperty("file.separator");	//路径分割符
	private String BASE_PATH=System.getProperty("user.dir");					//当前项目的根目录
	private String BASE_GAME_PATH= BASE_PATH+FILE_SEPARATOR+"autoPackage"+FILE_SEPARATOR+"games";	//游戏根目录
	private String BASE_TOOLS_PATH=BASE_PATH+FILE_SEPARATOR+"autoPackage"+FILE_SEPARATOR+"tools";	//Tools根目录
	private String BASE_WORK_PATH=BASE_PATH+FILE_SEPARATOR+"autoPackage"+FILE_SEPARATOR+"work";		//打包过程工作目录
	private String BASE_OUT_PATH=BASE_PATH+FILE_SEPARATOR+"autoPackage"+FILE_SEPARATOR+"out";		//渠道包输出根目录
	private String GAME_OUT_PATH;//当前游戏的渠道包输出目录																				
	private String KEYSTORE_OUT_PATH=BASE_PATH+FILE_SEPARATOR+"autoPackage"+FILE_SEPARATOR+"keystore";  //生成keystore目录的根目录
	private String KEYSTORE_TOOL_PATH=BASE_TOOLS_PATH+FILE_SEPARATOR+"keystore";
	
//*****************	按钮相关 *****************
	private JButton 	packageBtn;    			//打包按钮
	private JTextArea 	channelsInfo;			//将要打包的渠道信息
	private JTextArea 	optionInfo;  			//打包过程信息
	private JButton 	optionLogBtn;			//操作log查看按钮
	private JButton 	settingGameParamsBtn;	//配置游戏参数按钮
	private JButton 	packageLogBtn;			//打包过程log文件查看按钮
	private JButton 	resetBtn;				//重置按钮
	private JButton 	generateKeystoreBtn;	//生成签名按钮
	private JButton 	keystoreDirBtn;			//查看签名文件按钮
	private MyEditComboBox<PackageMethod> 	packageMethodComboBox;	//选择  "打包方式"  的EditComboBox
	private MyEditComboBox<ApktoolVersion> 	apktoolVersionComboBox;	//选择  "apktool版本"  的EditComboBox
	private MyEditComboBox<Game> 			gameComboBox;			//选择  "游戏"  的EditComboBox
	
	
//*****************	数据相关 *****************
	private Model model;//数据模型model
	private List<PackageMethod> 	packageMethodList=new ArrayList<PackageMethod>();	//支持的打包方式集合
	private List<ApktoolVersion> 	apktoolVersionList=new ArrayList<ApktoolVersion>();	//支持的apktool版本集合
	private List<Game> 				gameList=new ArrayList<Game>();						//支持的游戏集合
	
	private PackageMethod  currentPackageMethod;	//当前选择的打包方式
	private ApktoolVersion currentApktoolVersion;	//当前选择的Apktool版本
	private Game 		   currentGame;				//当前打包的游戏
	private Apk			   currentApk;				//当前游戏对应的apk
	private Channel        currentChannel;			//当前apk要打的渠道包集合
	
	private AssetPackageManager assetPackageManager;//Asset打包方式Manager
	private MetaPackageManager 	metaPackageManager; //meta-data打包方式Manager
	private QuickPackageManager quickPackageManager;//quick打包方式Manager
	private LogManager 	logManager;   //用户操作记录gManager

	public MyPackageFrame() {
		model = Model.getInstance();
		model.setStandardOutStream(System.out);
		assetPackageManager = AssetPackageManager.getInstance();
		metaPackageManager = MetaPackageManager.getInstance();
		quickPackageManager = QuickPackageManager.getInstance();
		logManager = LogManager.getInstance();
		
		assetPackageManager.init();
		metaPackageManager.init();
		quickPackageManager.init();
		logManager.init();
		logManager.setFlag(true);
		
		refreshData();
		initView();
		addListener();
		logManager.setOptionView(optionInfo);
		resetView();
	}
	
	/**
	 * 初始化或者用户点击充值按钮时，重新加载数据
	 */
	private void refreshData(){
		packageMethodList = model.getPackageMethods();
		apktoolVersionList = model.getApktoolVersions(BASE_TOOLS_PATH);
		gameList = model.getGames(BASE_GAME_PATH);
		
		currentGame=null;
		currentApk=null;
		currentChannel=null;
		currentPackageMethod=null;
		currentApktoolVersion=null;
		currentGame=null;
	}
	
	/**
	 * 初始化或者用户点击重置按钮时，重置各个按钮view的状态
	 */
	private void resetView(){
		packageMethodComboBox.setSelectedIndex(-1);
		apktoolVersionComboBox.setSelectedIndex(-1);
		gameComboBox.setSelectedIndex(-1);

		channelsInfo.setText("");
		optionInfo.setText("");
		packageBtn.setEnabled(false);
		
		
		logManager.appendLog("当前项目根目录:"+BASE_PATH);
		logManager.appendLog("游戏根目录:"+BASE_GAME_PATH);
		logManager.appendLog("Tools根目录:"+BASE_TOOLS_PATH);
		logManager.appendLog("********************************************"+LINE_SEPARATOR);
		
		logManager.appendLog("当前支持的打包方式如下：");
		for (PackageMethod packageMethod : packageMethodList) {
			logManager.appendLog(packageMethod.getDesc());
		}
		logManager.appendLog("********************************************"+LINE_SEPARATOR);
		
		logManager.appendLog("当前支持的apktool版本如下：");
		for (ApktoolVersion apktoolVersion : apktoolVersionList) {
			logManager.appendLog(apktoolVersion.getVersion());
		}
		logManager.appendLog("********************************************"+LINE_SEPARATOR);
		
		logManager.appendLog("打包游戏列表如下：");
		for (int i = 0; i < gameList.size(); i++) {
			logManager.appendLog(i+"-----【"+gameList.get(i).getApk().getFullName()+"】");
		}
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
		packageMethodComboBox.setBounds(0, 4, 176, 48);
		packageMethodComboBox.setVisible(true);
		
		packageMethodComboBox.updateComboBox(packageMethodList);
		getContentPane().add(packageMethodComboBox);
//		apktool版本
		apktoolVersionComboBox = new MyEditComboBox<ApktoolVersion>("选择apktool版本","请选择游戏",apktoolVersionItemListener);
		apktoolVersionComboBox.setBounds(0, 56, 176, 48);
		apktoolVersionComboBox.updateComboBox(apktoolVersionList);
		getContentPane().add(apktoolVersionComboBox);
//		选择游戏
		gameComboBox = new MyEditComboBox<Game>("请选择游戏","请选择游戏",gameItemListener);
		gameComboBox.setBounds(0, 114, 176, 47);
		gameComboBox.updateComboBox(gameList);
		getContentPane().add(gameComboBox);
		
		JLabel channelLabel = new JLabel("渠道信息如下：");
		channelLabel.setForeground(new Color(255, 0, 255));
		channelLabel.setBounds(0, 158, 107, 18);
		getContentPane().add(channelLabel);
		
		
//		显示将要打包的渠道
		JScrollPane channelsScrollPane = new JScrollPane();
		channelsScrollPane.setBounds(0, 179, 176, 321);
		channelsInfo = new JTextArea();
		channelsInfo.setForeground(Color.BLUE);
		channelsInfo.setBackground(new Color(0xcccccc, false));
		channelsInfo.setEditable(false);//设置用户不可编辑
		channelsInfo.setLineWrap(true);//行换行
		channelsInfo.setWrapStyleWord(true);//单词换行
		channelsScrollPane.setViewportView(channelsInfo);
		getContentPane().add(channelsScrollPane);
		
//		动态显示打包过程
		JScrollPane resultScrollPane = new JScrollPane();
		resultScrollPane.setBounds(180, 52, 506, 399);
		optionInfo = new JTextArea();
		optionInfo.setForeground(Color.BLACK);
		optionInfo.setBackground(new Color(0xccffcc));
		optionInfo.setEditable(false);//设置用户不可编辑
		optionInfo.setLineWrap(true);//行换行
		optionInfo.setWrapStyleWord(true);//单词换行
		resultScrollPane.setViewportView(optionInfo);
		getContentPane().add(resultScrollPane);
		
//		设置游戏参数按钮
		settingGameParamsBtn = new JButton("设置游戏参数");
		settingGameParamsBtn.setBounds(180, 452, 120, 48);
		getContentPane().add(settingGameParamsBtn);
		
//		查看log文件按钮
		packageLogBtn = new JButton("打包log");
		packageLogBtn.setBounds(383, 452, 81, 48);
		getContentPane().add(packageLogBtn);
		
		resetBtn = new JButton("重置");
		resetBtn.setForeground(Color.BLUE);
		resetBtn.setBounds(607, 452, 79, 48);
		getContentPane().add(resetBtn);
		
//		打包按钮
		packageBtn = new JButton("打包");
		packageBtn.setBounds(310, 452, 63, 48);
		getContentPane().add(packageBtn);
		
//		生成签名按钮
		generateKeystoreBtn = new JButton("生成签名");
		generateKeystoreBtn.setForeground(Color.BLUE);
		generateKeystoreBtn.setBounds(455, 4, 88, 45);
		getContentPane().add(generateKeystoreBtn);
		keystoreDirBtn = new JButton("签名目录");
		keystoreDirBtn.setForeground(new Color(0, 0, 0));
		keystoreDirBtn.setBounds(563, 4, 88, 45);
		getContentPane().add(keystoreDirBtn);
		
		JLabel optionLabel = new JLabel("操作过程：");
		optionLabel.setBackground(Color.WHITE);
		optionLabel.setForeground(new Color(255, 0, 255));
		optionLabel.setBounds(186, 23, 88, 18);
		getContentPane().add(optionLabel);
		
//		用户操作log按钮
		optionLogBtn = new JButton("操作log");
		optionLogBtn.setBounds(262, 21, 81, 23);
		getContentPane().add(optionLogBtn);	
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
		
		settingGameParamsBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
//				点击游戏参数的时候，打开当前游戏对应目录
				if (currentGame==null) {
					JOptionPane.showMessageDialog(null, "请选择游戏", "warning", JOptionPane.ERROR_MESSAGE); 
					return;
				}
				
				logManager.appendLog("option:您点击了-----【设置游戏参数】");
				
				try {
					Runtime.getRuntime().exec("explorer.exe "+currentGame.getGamePath());
				} catch (IOException e) {
					e.printStackTrace();
					logManager.appendLog("error:----->目录："+LINE_SEPARATOR+currentGame.getGamePath()+LINE_SEPARATOR+"error:----->打开出错...");
				}

			}
		});
		
		packageLogBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
//				用户点击,打开游戏打包过程中log文件所在目录（打开游戏输出目录）
				if (currentGame==null) {
					JOptionPane.showMessageDialog(null, "请选择游戏", "warning", JOptionPane.ERROR_MESSAGE); 
					return;
				}
				
				File gameOutFile = new File(GAME_OUT_PATH);
				if (!gameOutFile.exists()) {
					JOptionPane.showMessageDialog(null, "游戏目录："+LINE_SEPARATOR+GAME_OUT_PATH+LINE_SEPARATOR+"不存在"+LINE_SEPARATOR+"请手动创建！", "warning", JOptionPane.ERROR_MESSAGE); 
					return;
				}
				logManager.appendLog("Option:您点击了-----【打包log】");
				
				try {
					Runtime.getRuntime().exec("explorer.exe "+GAME_OUT_PATH);
				} catch (IOException e) {
					e.printStackTrace();
					logManager.appendLog("error:----->目录："+LINE_SEPARATOR+GAME_OUT_PATH+LINE_SEPARATOR+"error:----->打开出错...");
				}
			}
		});
		
		optionLogBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
//				用户点击,打开用户操作log文件所在目录（work目录下）
				File baseWorkDir = new File(BASE_WORK_PATH);
				if (!baseWorkDir.exists()) {
					JOptionPane.showMessageDialog(null, "操作log文件目录："+LINE_SEPARATOR+BASE_WORK_PATH+LINE_SEPARATOR+"不存在"+LINE_SEPARATOR+"请手动创建！", "warning", JOptionPane.ERROR_MESSAGE); 
					return;
				}
				logManager.appendLog("option:您点击了-----【操作log】");
				try {
					Runtime.getRuntime().exec("explorer.exe "+BASE_WORK_PATH);
				} catch (IOException e) {
					e.printStackTrace();
					logManager.appendLog("error:----->目录："+LINE_SEPARATOR+BASE_WORK_PATH+LINE_SEPARATOR+"error:----->打开出错...");
				}
			}
		});
		
		resetBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				logManager.appendLog("option:您点击了-----【重置】");
				//重新加载数据和视图
				refreshData();
				resetView();
			}
		});
		
		generateKeystoreBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				File keystoreDir = new File(KEYSTORE_OUT_PATH);
				if (!keystoreDir.exists()) {
					keystoreDir.mkdirs();
				}
				
				logManager.appendLog("option:您点击了-----【生成签名】");
//				默认签名会生成在autoPackage\keystore目录下
				String inputGid = JOptionPane.showInputDialog("请输入生成签名用的gid"); 
				System.out.println(inputGid);
				if (inputGid==null||inputGid.isEmpty()) {
					JOptionPane.showMessageDialog(null, "gid不能为空", "warning", JOptionPane.ERROR_MESSAGE); 
					return;
				}
				String gameKeystorePath=KEYSTORE_OUT_PATH+FILE_SEPARATOR+inputGid+".keystore";
//				命令行一次性生成签名文件
				String generateKeystoreCommand=String.format(getLocale(),"keytool -genkey -keyalg RSA -validity 36500 -alias %s -keystore %s -storepass %s -keypass %s -dname \"CN=pptvvas,OU=pptvvas,O=pptvvas,L=shanghai,ST=shanghai,C=CN\"",
						new Object[]{inputGid+".keystore",gameKeystorePath,inputGid+"pptvvas",inputGid+"pptvvas"});
				System.out.println("generateKeystoreCommand-->"+generateKeystoreCommand);
				System.out.println("KEYSTORE_TOOL_PATH-->"+KEYSTORE_TOOL_PATH);
				CmdUtil.exeCmdWithLog(generateKeystoreCommand, null, new File(KEYSTORE_TOOL_PATH));
			}
		});
		
		keystoreDirBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				logManager.appendLog("option:您点击了-----【签名目录】");
				try {
					Runtime.getRuntime().exec("explorer.exe "+KEYSTORE_OUT_PATH);
				} catch (IOException e) {
					e.printStackTrace();
					logManager.appendLog("error:----->目录："+LINE_SEPARATOR+KEYSTORE_OUT_PATH+LINE_SEPARATOR+"error:----->打开出错...");
				}
			}
		});
		
		packageBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				logManager.appendLog("option:您点击了-----【打包】");
				logManager.appendLog("开始打包：游戏-----【"+currentApk.getFullName()+"】");
				logManager.appendLog("打包详情请查看log文件...");
				logManager.appendLog(".....正在打包....");
				logManager.setFlag(false);
				
				
				boolean isPackageSuccess=false;
//				根据打包方式多渠道打包
				switch (currentPackageMethod.getMethod()) {
				case PackageMethod.METHOD_META:
					isPackageSuccess = metaPackageManager.metaAutoLoopPackage_Main();
					break;
					
				case PackageMethod.METHOD_ASSET:
					isPackageSuccess = assetPackageManager.assetAutoLoopPackage_Main();
					break;
					
				case PackageMethod.METHOD_QUICK:
					isPackageSuccess = quickPackageManager.quickAutoLoopPackage_Main();
					break;
					
				default:
					break;
				}
				
//				打包结束 设置输出为标注输出流
				logManager.setFlag(true);
				if (!isPackageSuccess) {
					logManager.appendLog("error----->打包出错，详情查看log...");
				}else{
					logManager.appendLog("打包完成：游戏-----【"+currentApk.getFullName()+"】");
				}
			}
		});
	}

	private MyEditComboBox.OnComboBoxItemClickListener<PackageMethod> packageMethodItemListener=new MyEditComboBox.OnComboBoxItemClickListener<PackageMethod>(){
		@Override
		public void OnItemClickListener(PackageMethod packageMethod) {
//			重新设置输出流为标准输出流
			logManager.setFlag(true);
			logManager.appendLog("Option:您选择了PackageMethod-----【"+packageMethod.getDesc()+"】");
			
			currentPackageMethod=packageMethod;
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
	
	private MyEditComboBox.OnComboBoxItemClickListener<ApktoolVersion> apktoolVersionItemListener=new MyEditComboBox.OnComboBoxItemClickListener<ApktoolVersion>(){

		@Override
		public void OnItemClickListener(ApktoolVersion apktoolVersion) {
//			重新设置输出流为标准输出流
			logManager.setFlag(true);
			logManager.appendLog("Option:您选择了ApktoolVersion-----【"+apktoolVersion.getVersion()+"】");
			
			currentApktoolVersion=apktoolVersion;
			
			assetPackageManager.setApktoolVersion(currentApktoolVersion);
			metaPackageManager.setApktoolVersion(currentApktoolVersion);
			quickPackageManager.setApktoolVersion(currentApktoolVersion);
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
			System.out.println("回调：apktoolVersion   OnEditInputListener-----"+LINE_SEPARATOR+temp.toString());
			apktoolVersionComboBox.updateComboBox(temp);
		}
	};
	
	private MyEditComboBox.OnComboBoxItemClickListener<Game> gameItemListener=new MyEditComboBox.OnComboBoxItemClickListener<Game>(){

		public void OnItemClickListener(Game game) {
//			重新设置输出流为标准输出流
			logManager.setFlag(true);
			logManager.appendLog("Option:您选择了Game-----【"+game.getApk().getFullName()+"】");
			
			channelsInfo.setText("");
			currentGame=game;
			currentApk=game.getApk();
			currentChannel=game.getChannel();
			GAME_OUT_PATH = BASE_OUT_PATH+FILE_SEPARATOR+currentApk.getName();
			
			assetPackageManager.setGame(currentGame);
			metaPackageManager.setGame(currentGame);
			quickPackageManager.setGame(currentGame);
			logManager.setGame(currentGame);
			
			List<Map<String, String>> channelList = currentChannel.getChannelList();
			StringBuffer sb = new StringBuffer();
			for (Map<String, String> ChannelMap : channelList) {
				sb.append("channel:").append(LINE_SEPARATOR);
				for (Entry<String,String> entry : ChannelMap.entrySet()) {
					sb.append(entry.getKey()).append(":").append(entry.getValue()).append(LINE_SEPARATOR);
				}
			}
			channelsInfo.setText(sb.toString());
		}

		@Override
		public void OnEditInputListener(String inputText) {
			
		}
	};

}
