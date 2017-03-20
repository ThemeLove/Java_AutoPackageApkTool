package com.themelove.tool.my;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;

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
	private String GAME_PATH;
	private JComboBox packageMethod;
	private JComboBox apktoolVersion;


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
		LINE_SEPRATOR=System.getProperty("line.seprator");
		BASE_PATH = System.getProperty("user.dir");
		GAME_PATH = BASE_PATH+FILE_SEPRATOR+"games";
		initConfigGames();
		
	}


	/**
	 * 初始化所有已经配置的游戏
	 */
	private void initConfigGames() {
		
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
		channelsInfo.setForeground(Color.WHITE);
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
		
		JComboBox comboBox = new JComboBox();
//		设置标题
		comboBox.setBorder(BorderFactory.createTitledBorder("请选择游戏"));
//		设置为可编辑状态
		comboBox.setEditable(true);
		ComboBoxEditor editor = comboBox.getEditor();
		comboBox.configureEditor(editor, "请选择游戏");
		comboBox.setBounds(408, 10, 154, 48);
		
		getContentPane().add(comboBox);
		
		packageMethod = new JComboBox();
		packageMethod.setEditable(true);
		packageMethod.setBorder(BorderFactory.createTitledBorder("打包方式"));
		packageMethod.setBounds(23, 10, 154, 48);
		getContentPane().add(packageMethod);
		
		apktoolVersion = new JComboBox();
		apktoolVersion.setEditable(true);
		apktoolVersion.setBorder(BorderFactory.createTitledBorder("选择apktool版本"));
		apktoolVersion.setBounds(212, 10, 154, 48);
		getContentPane().add(apktoolVersion);
		
	}
	
	/**
	 * 添加点击事件
	 */
	private void addListener() {
		
		
		chooseChannelBtn.addActionListener(new ActionListener() {
			
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
			
		});
		
		channelsInfo.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// 删除时的监听
				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// 插入时的监听
				
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// 内容变化时的监听
					packageBtn.setEnabled(e.getLength()==0);
			}
		});
		
		packageBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// 开始打包过程
				
			}
		});
		
		resetBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// 情况游戏名，渠道列表,resultInfo
				gameNameEdit.setText("");
				channelsFilePath.setText("");
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
	protected String[] getChannelsFormFile(File file) {
		String[] channels=new String[]{};
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
			BufferedReader br = new BufferedReader(inputStreamReader);
			String line=null;
			for (int i = 0; (line=br.readLine().trim())!=null; i++) {
				channels[i]=line;
			}
			return channels;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
