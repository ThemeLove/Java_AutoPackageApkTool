package com.themelove.tool.my;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
	 * 选择渠道按钮
	 */
	private JButton chooseChannelBtn;
	/**
	 * 重置按钮
	 */
	private JButton resetBtn;
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
	
	private MyEditComboBox<PackageMethod> packageMethodComboBox;
	private MyEditComboBox<ApktoolVersion> apktoolVersionComboBox;
	private MyEditComboBox<Game> gameComboBox;
	
	private List<PackageMethod> packageMethodsList=new ArrayList<PackageMethod>();
	private List<ApktoolVersion> apktoolVersionList=new ArrayList<ApktoolVersion>();
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
		BASE_OUT_PATH=BASE_PATH+FILE_SEPRATOR+"work";
		BAK_PATH = BASE_WORK_PATH+FILE_SEPRATOR+"bak";
		TEMP_PATH = BASE_WORK_PATH+FILE_SEPRATOR+"temp";
		System.out.println("basePath:"+BASE_PATH);
		System.out.println("gamePath:"+BASE_GAME_PATH);
		System.out.println("toolPath:"+BASE_TOOLS_PATH);
		initPackageMethods();
		initApktoolVersions();
		initConfigGames();
	}


	/**
	 * 初始化支持的Apktool
	 */
	private void initApktoolVersions() {
		File toolsFile = new File(BASE_TOOLS_PATH);
		if (!toolsFile.exists()) toolsFile.mkdirs();
//		TODO  没有时提示

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
	}

	/**
	 * 初始化支持的打包方式
	 */
	private void initPackageMethods() {
		PackageMethod metaMethod = new PackageMethod();
		metaMethod.setMethod(PackageMethod.METHOD_META);
		metaMethod.setDesc("方式一：修改meta-data");
		
		PackageMethod assetMethod = new PackageMethod();
		assetMethod.setMethod(PackageMethod.METHOD_ASSET);
		assetMethod.setDesc("方式二：修改assets配置");
		
		PackageMethod quickMethod = new PackageMethod();
		quickMethod.setMethod(PackageMethod.METHOD_QUICK);
		quickMethod.setDesc("方式三：修改签名目录");
		
		packageMethodsList.add(metaMethod);
		packageMethodsList.add(assetMethod);
		packageMethodsList.add(quickMethod);
	}

	/**
	 * 初始化所有已经配置的游戏
	 */
	private void initConfigGames() {
//		初始化
		File baseGameFile = new File(BASE_GAME_PATH);
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
	}

	/**
	 * 初始化UI视图
	 */
	private void initView() {
		setTitle("MyVasPackageTool");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 460);
		getContentPane().setLayout(null);
		
		chooseChannelBtn = new JButton("选择渠道列表");
		chooseChannelBtn.setBounds(10, 83, 154, 27);
		getContentPane().add(chooseChannelBtn);
		
//		显示将要打包的渠道
		JScrollPane channelsScrollPane = new JScrollPane();
		channelsScrollPane.setBounds(10, 120, 152, 176);
		channelsInfo = new JTextArea();
		channelsInfo.setForeground(Color.BLUE);
		channelsInfo.setBackground(new Color(0xcccccc, false));
		channelsScrollPane.setViewportView(channelsInfo);
		getContentPane().add(channelsScrollPane);
		
//		动态显示打包过程
		JScrollPane resultScrollPane = new JScrollPane();
		resultScrollPane.setBounds(172, 120, 387, 176);
		resultInfo = new JTextArea();
		resultInfo.setForeground(Color.WHITE);
		resultInfo.setBackground(new Color(0xccffcc));
//		resultInfo.setBackground(new Color(0xcccccc, true));
		resultScrollPane.setViewportView(resultInfo);
		getContentPane().add(resultScrollPane);
		
		packageBtn = new JButton("打包");
		packageBtn.setBounds(487, 352, 75, 48);
		getContentPane().add(packageBtn);
		
		resetBtn = new JButton("重置");
		resetBtn.setBounds(14, 352, 84, 48);
		
		getContentPane().add(resetBtn);
		
		resetBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		resetBtn.setBounds(14, 352, 84, 48);
		getContentPane().add(resetBtn);
		
//		打包方式
		packageMethodComboBox = new MyEditComboBox<PackageMethod>("打包方式","请选择打包方式", packageMethodItemListener);
		packageMethodComboBox.setBounds(10, 10, 195, 48);
		packageMethodComboBox.setVisible(true);
		
		packageMethodComboBox.updateComboBox(packageMethodsList);
		getContentPane().add(packageMethodComboBox);
//		apktool版本
		apktoolVersionComboBox = new MyEditComboBox<ApktoolVersion>("选择apktool版本","请选择游戏",apktoolVersionItemListener);
		apktoolVersionComboBox.setBounds(215, 10, 183, 48);
		apktoolVersionComboBox.updateComboBox(apktoolVersionList);
		getContentPane().add(apktoolVersionComboBox);
//		选择游戏
		gameComboBox = new MyEditComboBox<Game>("请选择游戏","请选择游戏",gameItemListener);
		gameComboBox.setBounds(408, 10, 166, 48);
		gameComboBox.updateComboBox(gameList);
		getContentPane().add(gameComboBox);
	}
	
	@SuppressWarnings({ "unchecked", "unchecked" })
	private MyEditComboBox.OnComboBoxItemClickListener<PackageMethod> packageMethodItemListener=new MyEditComboBox.OnComboBoxItemClickListener<PackageMethod>(){

		@Override
		public void OnItemClickListener(PackageMethod packageMethod) {
			currentPackageMethod=packageMethod;
		}

		@Override
		public void OnEditInputListener(String inputText) {
			List<PackageMethod> temp=new ArrayList<>();
			if (inputText.isEmpty()) {
				temp=packageMethodsList;
			}else{
				for (PackageMethod packageMethod : packageMethodsList) {
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
				List<String> gameChannels= getChannelsFormFile(channelFile);
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
	 * 添加点击事件
	 */
	private void addListener() {
		
		
		/*chooseChannelBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				获取当前的游戏名
				String trim = gameNameEdit.getText().trim();
				
				
				// 弹出文件选择框
				JFileChooser jFileChooser = new JFileChooser("");
				
//				只接受文本文件
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"文本文件(*.txt)", "txt");
				jFileChooser.setFileFilter(filter);
				int returnVal = jFileChooser
						.showOpenDialog(MyPackageFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: "+ jFileChooser.getSelectedFile().getName());
//					将选择的文本路径设置到 channelsFilePath上
					channelsFilePath.setText(jFileChooser.getSelectedFile().getAbsolutePath());
//					解析选中文件中的渠道列表，并显示到channelsInfo上
					String[] channels = getChannelsFormFile(jFileChooser.getSelectedFile());
					StringBuffer channelsInftText=new StringBuffer();
					channelsInftText.append("渠道列表如下："+LINE_SEPRATOR);
					for (int i = 0; i < channels.length; i++) {
						channelsInftText.append(channels[i]+LINE_SEPRATOR);
					}
					channelsInfo.setText(channelsInftText.toString());
				}
				
				jFileChooser.addChoosableFileFilter(new FileFilter() {
								
								@Override
								public String getDescription() {
									// 此文件过滤器的描述
									return "accept the file's name that endsWith .txt";
								}
								
								@Override
								public boolean accept(File f) {
									//只接受.txt文件类型的文件
									if (f.isDirectory()) return false;
									if (f.getName().endsWith(".txt")) return true;
									return false;
								}
							});
			}
			
		});*/
		
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
		
		packageBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

//				真正多渠道打包
				autoPackageChannels();
			}
		});
		
		resetBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// 情况游戏名，渠道列表,resultInfo
/*				gameNameEdit.setText("");
				channelsFilePath.setText("");*/
				channelsInfo.setText("");
				resultInfo.setText("");
			}
		});
	}


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
	
	protected void autoPackageChannels() {
		// TODO Auto-generated method stub
//		1.检查母包是否存在
		File apkFile = new File(currentGame.getGamePath()+FILE_SEPRATOR+"apk"+FILE_SEPRATOR+currentGame.getName()+".apk");
		if (!apkFile.exists()) System.out.println("游戏---"+currentGame.getName()+"---母包不存在");
//		2.清空打包过程中用到的目录
		File bakDir = new File(BAK_PATH);
		File tempDir = new File(TEMP_PATH);
		File gameOutDir = new File(BASE_OUT_PATH+FILE_SEPRATOR+currentGame.getName());
		
		boolean deleteBakDir = FileUtil.deleteFiles(bakDir);
		if (deleteBakDir) System.out.println("清空bak目录成功...");
		boolean deleteTemDir = FileUtil.deleteFiles(tempDir);
		if (deleteTemDir) System.out.println("清空temp目录成功...");
		boolean deleteGameOutDir = FileUtil.deleteFiles(gameOutDir);
		if (deleteGameOutDir) System.out.println("清空---"+currentGame.getName()+"---out目录成功...");
			
//		母包路径
		String gameApkPath=BASE_GAME_PATH+FILE_SEPRATOR+currentGame.getName()+FILE_SEPRATOR+"apk"+FILE_SEPRATOR+currentGame.getName()+".apk";
		
//		3.根据打包方式，反编译或者解压母包到bak目录
		switch (currentPackageMethod.getMethod()) {
		case PackageMethod.METHOD_META://修改AndroidManifest.xml中meta方式要用apktool反编译到bak目录

			break;
		case PackageMethod.METHOD_ASSET://修改asset目录中的配置文件和META-INFO文件方式不用反编译，只需解压即可，省去反编译的步骤，加快速度。
		case PackageMethod.METHOD_QUICK:

//			File zipFile = new File(BASE_TOOLS_PATH+FILE_SEPRATOR+"7zip");
			File zipFile = new File("E:/softwore/7zip");
			if(!zipFile.exists()){
				System.out.println("压缩工具不存在，请检查目录：");
				return ;
			}

//			7z x "d:\File.7z" -y -aos -o"d:\Mydir"
			String zipCommand = TextUtil.formatString("7z x \"%s\" -y -aos -o\"%s\" ",new String[]{gameApkPath,BAK_PATH});
			System.out.println("zipCommand---"+zipCommand);
//			Process process = CmdUtil.exeCmd("cmd.exe /c start "+zipCommand, null, zipFile);
			Process process = CmdUtil.exeCmd(zipCommand, null, zipFile);
/*			try {
				process.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			System.out.println("解压母包："+currentGame.getName()+".apk---到--->"+BAK_PATH+"---成功！");
			break;
		default:
			break;
		}
	}
	
	
}
