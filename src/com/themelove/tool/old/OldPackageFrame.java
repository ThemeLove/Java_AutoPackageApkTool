package com.themelove.tool.old;


import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.NumberFormat;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.python.util.PythonInterpreter;

import sun.net.ftp.FtpClient;

public class OldPackageFrame extends JFrame
{
  private static final long serialVersionUID = 1L;
  private JTextField gameName;
  private JTextField versionCode;
  private JTextField code1;
  private JTextField code2;
  private JTextArea channelTf;
  private JLabel resultLable;
  private JScrollPane jscollpannel;
  private JScrollPane jscollpannelchannel;
  private JButton surebtn;
  private JButton addChannelBtn;
  private JButton genChannelBtn;
  private JButton resetBtn;
  private JComboBox debugMode;
  private boolean debugModeResult = false;
  private String result;
  private String keypath = null;
  private String inputPath = null;
  private StringBuilder channelCode = new StringBuilder();
  private String resourcePath = null;
  private String toolPath = null;
  private String channelFilePath = null;
  private String logpath = null;
  private String serverUrl = "192.168.13.11";
  private int serverPort = 21;
  private String userName = "vas";
  private String passWord = "123456";
  private String uploadFile = null;
  private String serverPath = "ftp/PM/mobilegame/apk/sign";
  private FtpClient ftpClient = null;
  
  public OldPackageFrame()
  {
    this.resourcePath = OldPackageFrame.class.getResource(".").getPath();
    String index = this.resourcePath.substring(0, 1);
    if (index.equals("/")) {
      this.resourcePath = this.resourcePath.substring(1);
    }
    this.toolPath = (this.resourcePath + "tools/");
    this.channelFilePath = (this.toolPath + "cfg/channel.txt");
    this.logpath = (this.toolPath + "log.txt");
    setDefaultCloseOperation(3);//设置点击JFrame的关闭按钮时的策略
    
    deleteFile(this.logpath);
    if (new File(this.channelFilePath).exists())//清空log日志
    {
      if (!deleteFile(this.channelFilePath)) {
        JOptionPane.showConfirmDialog(null, "删除。/tools/cfg/channel.txt失败，请先手动删除", "提示", 0);
      } else {
        init();
      }
    }
    else {
      init();
    }
    
    
  }
  
  /**
   * 初始化
   */
  public void init()
  {
    setResizable(false);
    setSize(320, 400);
    setLocation(300, 150);
    getContentPane().setLayout(null);
    add(getlable("名字", 10, 10, 40, 30));
    this.gameName = getTextView("输入名称", 60, 10, 90, 30);
    add(this.gameName);
    add(getlable("版本号", 160, 10, 40, 30));
    this.versionCode = getTextView("输入版本号", 210, 10, 90, 30);
    this.resultLable = getlable("结果:", 120, 210, 300, 100);
    
    add(getlable("添加渠道号", 10, 50, 120, 30));
    this.code1 = getNumberTextView("guid", 10, 80, 40, 30);
    this.code2 = getNumberTextView("id", 70, 80, 40, 30);
    code1.setBackground(Color.RED);
    code2.setBackground(Color.BLUE);
    this.code1.addKeyListener(new KeyListener()
    {
      public void keyTyped(KeyEvent e) {}
      
      public void keyReleased(KeyEvent e)
      {
        if (!OldPackageFrame.this.code1.getText().isEmpty()) {
          OldPackageFrame.this.addChannelBtn.setEnabled(true);
        } else {
          OldPackageFrame.this.addChannelBtn.setEnabled(false);
        }
      }
      
      public void keyPressed(KeyEvent e) {}
    });
    add(this.code1);
    add(this.code2);
/*    this.code1.setVisible(false);
    this.code2.setVisible(false);*/
    this.addChannelBtn = getButton("添加", 240, 80, 60, 30);
    add(this.addChannelBtn);
//    this.addChannelBtn.setEnabled(false);
//    this.addChannelBtn.setVisible(false);
    this.addChannelBtn.addMouseListener(new MouseListener()
    {
      public void mouseReleased(MouseEvent e) {}
      
      public void mousePressed(MouseEvent e) {}
      
      public void mouseExited(MouseEvent e) {}
      
      public void mouseEntered(MouseEvent e) {}
      
      public void mouseClicked(MouseEvent e)
      {
        if (OldPackageFrame.this.addChannelBtn.isEnabled())
        {
          String result = null;
          if (OldPackageFrame.this.code2.getText().isEmpty())
          {
            OldPackageFrame.this.channelCode.append(OldPackageFrame.this.code1.getText() + "   none" + "   " + OldPackageFrame.this.debugModeResult + " <br>");
            result = OldPackageFrame.this.code1.getText() + "   none" + "  " + OldPackageFrame.this.code1.getText() + "       " + OldPackageFrame.this.debugModeResult;
          }
          else
          {
            OldPackageFrame.this.channelCode.append(OldPackageFrame.this.code1.getText() + "  " + OldPackageFrame.this.code2.getText() + "   " + OldPackageFrame.this.code1.getText() + "_" + 
              OldPackageFrame.this.code2.getText() + "   " + OldPackageFrame.this.debugModeResult + " <br>");
            result = OldPackageFrame.this.code1.getText() + "   " + OldPackageFrame.this.code2.getText() + "   " + OldPackageFrame.this.code1.getText() + "_" + 
              OldPackageFrame.this.code2.getText() + "   " + OldPackageFrame.this.debugModeResult;
          }
          OldPackageFrame.this.setResult(OldPackageFrame.this.getResult());
          OldPackageFrame.this.reset();
          OldPackageFrame.this.generateTxtFile(result);
        }
      }
    });
    this.channelTf = getTextArea("channelcode here:", 10, 120, 200, 60);
//  设置文本区域换行策略,既当文字比控件的宽度还长时会自动换行
    this.channelTf.setLineWrap(true);
    
    this.jscollpannelchannel = new JScrollPane();
    this.jscollpannelchannel.setBounds(10, 120, 200, 60);
    this.jscollpannelchannel.getViewport().add(this.channelTf);
    add(this.jscollpannelchannel);
    
    this.genChannelBtn = getButton("生成", 240, 120, 60, 60);
    add(this.genChannelBtn);
    this.genChannelBtn.setEnabled(false);
    this.jscollpannel = new JScrollPane();
    this.jscollpannel.setBounds(10, 210, 300, 100);
//  getViewport()返回当前的JViewport
    this.jscollpannel.getViewport().add(this.resultLable);
    add(this.jscollpannel);
    
    this.surebtn = getButton("确定", 210, 330, 100, 30);
    this.resetBtn = getButton("重设", 10, 330, 100, 30);
    add(this.resetBtn);
    
    this.resetBtn.addMouseListener(new MouseListener()
    {
      public void mouseReleased(MouseEvent e) {}
      
      public void mousePressed(MouseEvent e) {}
      
      public void mouseExited(MouseEvent e) {}
      
      public void mouseEntered(MouseEvent e) {}
      
      public void mouseClicked(MouseEvent e)
      {
        OldPackageFrame.this.reset();
      }
    });
    this.surebtn.addMouseListener(new MouseListener()
    {
      public void mouseReleased(MouseEvent e) {}
      
      public void mousePressed(MouseEvent e) {}
      
      public void mouseExited(MouseEvent e) {}
      
      public void mouseEntered(MouseEvent e) {}
      
      public void mouseClicked(MouseEvent e)
      {
//    	真正循环打包操作
//    	获取游戏名
        String game = OldPackageFrame.this.gameName.getText();
//      获取版本号
        String version = OldPackageFrame.this.versionCode.getText();
        OldPackageFrame.this.result = (game + " " + version);
        OldPackageFrame.this.resultLable.setText(OldPackageFrame.this.result);
//      根据游戏名获取签名文件路径
        OldPackageFrame.this.keypath = (OldPackageFrame.this.toolPath + "keystore/" + OldPackageFrame.this.gameName.getText() + ".keystore");
//      根据游戏名和版本号获取将要打包的母包路径
        OldPackageFrame.this.inputPath = 
          (OldPackageFrame.this.toolPath + "input/" + OldPackageFrame.this.gameName.getText() + "/" + OldPackageFrame.this.versionCode.getText() + "/" + OldPackageFrame.this.gameName.getText() + "_" + OldPackageFrame.this.versionCode.getText() + ".apk");
        if (OldPackageFrame.this.checkFile(OldPackageFrame.this.keypath))//校验签名文件是否存在
        {
          if (OldPackageFrame.this.checkFile(OldPackageFrame.this.inputPath))//校验母包是否存在
          {
            try
            {
//            PythonInterpreter类是用于嵌入在Java应用程序中的Jython解释器的标准包装器
              PythonInterpreter interpreter = new PythonInterpreter();
              Properties props = new Properties();
              props.setProperty("python.path", "/home/modules:scripts");
//            初始化Jython运行时
              PythonInterpreter.initialize(System.getProperties(), props, new String[] { "" });
              interpreter.set("toolPath", OldPackageFrame.this.toolPath);
              interpreter.set("apkId", OldPackageFrame.this.gameName.getText());
              interpreter.set("version", OldPackageFrame.this.versionCode.getText());
              interpreter.exec("import os");
              interpreter.exec("import re");
              interpreter.exec("import shutil");
              interpreter.exec("import sys");
              interpreter.exec("import zipfile");
              interpreter.exec("import traceback");
              interpreter.execfile(OldPackageFrame.this.toolPath + "sign.py ");
            }
            catch (Exception err)
            {
              JOptionPane.showConfirmDialog(null, err.toString(), "提示", 0);
            }
            if (OldPackageFrame.this.readTextFile(OldPackageFrame.this.toolPath + "log.txt") != null) {
              OldPackageFrame.this.resultLable.setText(OldPackageFrame.this.readTextFile(OldPackageFrame.this.toolPath + "log.txt").toString());
            }
//          打包之前准备目录的工作
            OldPackageFrame.delFolder(OldPackageFrame.this.resourcePath + "tools/sign");
            OldPackageFrame.delFolder(OldPackageFrame.this.resourcePath + "tools/tmp/");
            OldPackageFrame.delFolder(OldPackageFrame.this.resourcePath + "tools/unsign");
            String filename = null;
            File file = new File(OldPackageFrame.this.toolPath);
            String[] filenameArray = file.list();
            String[] arrayOfString1;
            int j = (arrayOfString1 = filenameArray).length;
            for (int i = 0; i < j; i++)
            {
              String fileitem = arrayOfString1[i];
              if (fileitem.startsWith(OldPackageFrame.this.gameName.getText() + "_" + OldPackageFrame.this.versionCode.getText()))
              {
                filename = fileitem;
                OldPackageFrame.this.writeTextFile("file founded!", OldPackageFrame.this.logpath);
                break;
              }
            }
            if (filename != null)
            {
              OldPackageFrame.this.uploadFile = (OldPackageFrame.this.toolPath + filename);
              try
              {
                OldPackageFrame.this.writeTextFile("uploadfile path:" + OldPackageFrame.this.uploadFile + "\n", OldPackageFrame.this.logpath);
                OldPackageFrame.this.uploadToFtpServer(OldPackageFrame.this.serverUrl, OldPackageFrame.this.userName, OldPackageFrame.this.passWord, OldPackageFrame.this.uploadFile, filename);
              }
              catch (Exception e2)
              {
                OldPackageFrame.this.writeTextFile(e2.toString(), OldPackageFrame.this.logpath);
              }
            }
            else
            {
              OldPackageFrame.this.channelCode.append("ftp failed ,can not find file!");
              OldPackageFrame.this.writeTextFile(OldPackageFrame.this.channelCode.toString(), OldPackageFrame.this.logpath);
            }
          }
          else
          {
            JOptionPane.showConfirmDialog(null, "请将源文件放入" + OldPackageFrame.this.toolPath + "input/" + OldPackageFrame.this.gameName.getText() + "/" + 
              OldPackageFrame.this.versionCode.getText() + "/下", "提示", 0);
          }
        }
        else if (new File(OldPackageFrame.this.resourcePath + OldPackageFrame.this.gameName.getText() + ".keystore").exists())
        {
          File key = new File(OldPackageFrame.this.resourcePath + OldPackageFrame.this.gameName.getText() + ".keystore");
          
          File fnew = new File(OldPackageFrame.this.toolPath + "keystore/" + key.getName());
          key.renameTo(fnew);
        }
        else
        {
          JOptionPane.showConfirmDialog(null, "keysotore未生成", "提示", 0);
          
          String cmd = "cmd.exe /c start " + OldPackageFrame.this.toolPath + "/gen.bat " + OldPackageFrame.this.gameName.getText() + " pptvvas";
          try
          {
            Runtime.getRuntime().exec(cmd);
          }
          catch (IOException e1)
          {
            e1.printStackTrace();
            OldPackageFrame.this.channelCode.append(e1.toString());
            OldPackageFrame.this.writeTextFile(OldPackageFrame.this.channelCode.toString(), "log.txt");
          }
        }
      }
    });
    this.channelTf.addKeyListener(new KeyListener()
    {
      public void keyTyped(KeyEvent arg0) {}
      
      public void keyReleased(KeyEvent arg0)
      {
        if (!OldPackageFrame.this.channelTf.getText().isEmpty()) {
          OldPackageFrame.this.genChannelBtn.setEnabled(true);
        } else {
          OldPackageFrame.this.genChannelBtn.setEnabled(false);
        }
      }
      
      public void keyPressed(KeyEvent arg0) {}
    });
    this.genChannelBtn.addMouseListener(new MouseListener()
    {
      public void mouseReleased(MouseEvent arg0) {}
      
      public void mousePressed(MouseEvent arg0) {}
      
      public void mouseExited(MouseEvent arg0) {}
      
      public void mouseEntered(MouseEvent arg0) {}
      
      public void mouseClicked(MouseEvent arg0)
      {
        String result = OldPackageFrame.this.parseString(OldPackageFrame.this.channelTf.getText().trim());
        OldPackageFrame.this.channelCode = new StringBuilder();
        OldPackageFrame.this.deleteFile(OldPackageFrame.this.channelFilePath);
        OldPackageFrame.this.generateTxtFile(result);
      }
    });
    add(this.versionCode);
    add(this.surebtn);
    
    this.debugMode = new JComboBox();
    this.debugMode.addItem("true");
    this.debugMode.addItem("false");
    this.debugMode.setSelectedIndex(1);
    this.debugMode.addItemListener(new ItemListener()
    {
      public void itemStateChanged(ItemEvent arg0)
      {
        int i = OldPackageFrame.this.debugMode.getSelectedIndex();
        if (i == 0) {
          OldPackageFrame.this.debugModeResult = true;
        } else {
          OldPackageFrame.this.debugModeResult = false;
        }
      }
    });
    this.debugMode.setBounds(120, 80, 60, 30);
    add(this.debugMode);
//    this.debugMode.setVisible(false);
    setVisible(true);
  }
  
  protected boolean checkFile(String filePath)
  {
    File keyfile = new File(filePath);
    if (keyfile.exists()) {
      return true;
    }
    return false;
  }
  
  protected boolean generateTxtFile(String result2)
  {
    boolean flag = false;
    File file = new File(this.channelFilePath);
    if (!file.exists()) {
      try
      {
        file.createNewFile();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    flag = writeTextFile(result2, this.channelFilePath);
    return flag;
  }
  
  /**
   * 返回一个设置了name,x,y,宽高的JLabel
   * @param name
   * @param x
   * @param y
   * @param width
   * @param height
   * @return
   */
  private JLabel getlable(String name, int x, int y, int width, int height)
  {
    JLabel name1 = new JLabel();
    name1.setBounds(x, y, width, height);
    name1.setText(name);
    return name1;
  }
  
  /**
   * 返回一个设置了name,x,y,宽高的JButton
   * @param name
   * @param x
   * @param y
   * @param width
   * @param height
   * @return
   */
  private JButton getButton(String name, int x, int y, int width, int height)
  {
    JButton button = new JButton();
    button.setText(name);
    button.setBounds(x, y, width, height);
    return button;
  }
  
  private JTextField getNumberTextView(String name, int x, int y, int width, int height)
  {
    JTextField button = new JFormattedTextField(NumberFormat.getIntegerInstance());
    button.setToolTipText(name);
    button.setColumns(1);
    button.setBounds(x, y, width, height);
    return button;
  }
  
  private JTextArea getTextArea(String name, int x, int y, int width, int height)
  {
    JTextArea button = new JTextArea();
    button.setToolTipText(name);
    button.setColumns(1);
    button.setBounds(x, y, width, height);
    return button;
  }
  
  private JTextField getTextView(String name, int x, int y, int width, int height)
  {
    JTextField button = new JTextField();
    button.setToolTipText(name);
    button.setColumns(1);
    button.setBounds(x, y, width, height);
    return button;
  }
  
  public String getResult()
  {
    StringBuilder result = new StringBuilder();
    result.append("<html><body>基本信息：" + this.gameName.getText() + "   " + this.versionCode.getText() + "<br>渠道号：<br>" + 
      this.channelCode.toString() + "<body></html>");
    return result.toString();
  }
  
  private void reset()
  {
    this.code1.setText("");
    this.code2.setText("");
    this.gameName.setText("");
    this.versionCode.setText("");
    this.channelTf.setText("");
    this.resultLable.setText("");
    this.addChannelBtn.setEnabled(false);
    this.genChannelBtn.setEnabled(false);
    this.channelCode = new StringBuilder();
    deleteFile(this.logpath);
  }
  
  public void setResult(String txt)
  {
    this.resultLable.setText(txt);
  }
  
  private boolean writeTextFile(String content, String path)
  {
    boolean flag = false;
    try
    {
      BufferedWriter bw = new BufferedWriter(new FileWriter(path, true));
      bw.append(content + "\r\n");
      bw.flush();
      bw.close();
      flag = true;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return flag;
  }
  
  public StringBuilder readTextFile(String filePath)
  {
    StringBuilder result = null;
    FileReader fr = null;
    try
    {
      fr = new FileReader(filePath);
      
      BufferedReader br = new BufferedReader(fr);
      result = new StringBuilder();
      result.append("<html><body>");
      while (br.readLine() != null)
      {
        String s = br.readLine();
        System.out.println(s);
        result.append(s + "<br>");
      }
      result.append("</body></html>");
      
      br.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return result;
  }
  
  public boolean deleteFile(String filepath)
  {
    boolean flag = false;
    File f = new File(filepath);
    if (f.delete()) {
      flag = true;
    }
    return flag;
  }
  
  public static void delFolder(String folderPath)
  {
    try
    {
      delAllFile(folderPath);
      String filePath = folderPath;
      filePath = filePath.toString();
      File myFilePath = new File(filePath);
      myFilePath.delete();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static boolean delAllFile(String path)
  {
    boolean flag = false;
    File file = new File(path);
    if (!file.exists()) {
      return flag;
    }
    if (!file.isDirectory()) {
      return flag;
    }
    String[] tempList = file.list();
    File temp = null;
    for (int i = 0; i < tempList.length; i++)
    {
      if (path.endsWith(File.separator)) {
        temp = new File(path + tempList[i]);
      } else {
        temp = new File(path + File.separator + tempList[i]);
      }
      if (temp.isFile()) {
        temp.delete();
      }
      if (temp.isDirectory())
      {
        delAllFile(path + "/" + tempList[i]);
        delFolder(path + "/" + tempList[i]);
        flag = true;
      }
    }
    return flag;
  }
  
  public InputStream getLocalFileInputStream(String fileName)
    throws FileNotFoundException
  {
    return new FileInputStream(new File(fileName));
  }
  
  public void writeToFtpServer(OutputStream outputStream, InputStream inputStream)
    throws IOException
  {
    writeTextFile("ftp write begin", this.toolPath + "log.txt");
    byte[] bytes = new byte['?'];
    while (inputStream.read(bytes) != -1) {
      outputStream.write(bytes, 0, bytes.length);
    }
    outputStream.flush();
    writeTextFile("ftp  write end!", this.toolPath + "log.txt");
  }
  
  public void writeToLocal(InputStream inputStream, OutputStream outputStream)
    throws IOException
  {
    byte[] bytes = new byte['?'];
    while (inputStream.read(bytes) != -1) {
      outputStream.write(bytes, 0, bytes.length);
    }
  }
  
  public void close(FtpClient ftpClient, OutputStream outputStream, InputStream inputStream)
    throws IOException
  {
    if (inputStream != null) {
      inputStream.close();
    }
    if (outputStream != null) {
      outputStream.close();
    }
    if (ftpClient != null) {
      ftpClient.close();
    }
  }
  
  public void uploadToFtpServer(String serverUrl, String userName, String passWord, String uploadFile, String remoteFileName)
  {
    try
    {
      writeTextFile("upload file begin", this.logpath);
      if (this.ftpClient == null)
      {
        this.ftpClient = FtpClient.create(serverUrl);
        
        writeTextFile("create ftp client..", this.logpath);
      }
      else
      {
        writeTextFile("reuse  ftp client..", this.logpath);
      }
      SocketAddress address = new InetSocketAddress(serverUrl, this.serverPort);
      
      this.ftpClient.connect(address);
      this.ftpClient.login(userName, passWord.toCharArray());
      this.ftpClient.changeDirectory(this.serverPath);
      
      writeTextFile("ftp login success!", this.logpath);
      uploadFile(uploadFile, remoteFileName);
      
      String path = this.toolPath + "output/";
      moveFile(uploadFile, path);
      deleteFile(uploadFile);
    }
    catch (Exception e)
    {
      writeTextFile("upload exception：" + e.toString(), this.logpath);
    }
  }
  
  /* Error */
  private void uploadFile(String localFile, String remoreFile)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aload_0
    //   6: getfield 94	MyFrame:ftpClient	Lsun/net/ftp/FtpClient;
    //   9: aload_2
    //   10: invokevirtual 631	sun/net/ftp/FtpClient:putFileStream	(Ljava/lang/String;)Ljava/io/OutputStream;
    //   13: astore_3
    //   14: new 153	java/io/File
    //   17: dup
    //   18: aload_1
    //   19: invokespecial 155	java/io/File:<init>	(Ljava/lang/String;)V
    //   22: astore 5
    //   24: new 543	java/io/FileInputStream
    //   27: dup
    //   28: aload 5
    //   30: invokespecial 545	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   33: astore 4
    //   35: sipush 1024
    //   38: newarray <illegal type>
    //   40: astore 6
    //   42: goto +12 -> 54
    //   45: aload_3
    //   46: aload 6
    //   48: iconst_0
    //   49: iload 7
    //   51: invokevirtual 553	java/io/OutputStream:write	([BII)V
    //   54: aload 4
    //   56: aload 6
    //   58: invokevirtual 635	java/io/FileInputStream:read	([B)I
    //   61: dup
    //   62: istore 7
    //   64: iconst_m1
    //   65: if_icmpne -20 -> 45
    //   68: aload_0
    //   69: ldc_w 636
    //   72: aload_0
    //   73: getfield 72	MyFrame:logpath	Ljava/lang/String;
    //   76: invokespecial 371	MyFrame:writeTextFile	(Ljava/lang/String;Ljava/lang/String;)Z
    //   79: pop
    //   80: goto +67 -> 147
    //   83: astore 5
    //   85: aload_0
    //   86: ldc_w 638
    //   89: aload_0
    //   90: getfield 72	MyFrame:logpath	Ljava/lang/String;
    //   93: invokespecial 371	MyFrame:writeTextFile	(Ljava/lang/String;Ljava/lang/String;)Z
    //   96: pop
    //   97: aload_0
    //   98: aload_0
    //   99: getfield 94	MyFrame:ftpClient	Lsun/net/ftp/FtpClient;
    //   102: aload_3
    //   103: aload 4
    //   105: invokevirtual 640	MyFrame:close	(Lsun/net/ftp/FtpClient;Ljava/io/OutputStream;Ljava/io/InputStream;)V
    //   108: goto +60 -> 168
    //   111: astore 9
    //   113: aload 9
    //   115: invokevirtual 366	java/io/IOException:printStackTrace	()V
    //   118: goto +50 -> 168
    //   121: astore 8
    //   123: aload_0
    //   124: aload_0
    //   125: getfield 94	MyFrame:ftpClient	Lsun/net/ftp/FtpClient;
    //   128: aload_3
    //   129: aload 4
    //   131: invokevirtual 640	MyFrame:close	(Lsun/net/ftp/FtpClient;Ljava/io/OutputStream;Ljava/io/InputStream;)V
    //   134: goto +10 -> 144
    //   137: astore 9
    //   139: aload 9
    //   141: invokevirtual 366	java/io/IOException:printStackTrace	()V
    //   144: aload 8
    //   146: athrow
    //   147: aload_0
    //   148: aload_0
    //   149: getfield 94	MyFrame:ftpClient	Lsun/net/ftp/FtpClient;
    //   152: aload_3
    //   153: aload 4
    //   155: invokevirtual 640	MyFrame:close	(Lsun/net/ftp/FtpClient;Ljava/io/OutputStream;Ljava/io/InputStream;)V
    //   158: goto +10 -> 168
    //   161: astore 9
    //   163: aload 9
    //   165: invokevirtual 366	java/io/IOException:printStackTrace	()V
    //   168: return
    // Line number table:
    //   Java source line #897	-> byte code offset #0
    //   Java source line #898	-> byte code offset #2
    //   Java source line #902	-> byte code offset #5
    //   Java source line #904	-> byte code offset #14
    //   Java source line #905	-> byte code offset #24
    //   Java source line #907	-> byte code offset #35
    //   Java source line #909	-> byte code offset #42
    //   Java source line #912	-> byte code offset #45
    //   Java source line #909	-> byte code offset #54
    //   Java source line #915	-> byte code offset #68
    //   Java source line #917	-> byte code offset #80
    //   Java source line #918	-> byte code offset #83
    //   Java source line #920	-> byte code offset #85
    //   Java source line #927	-> byte code offset #97
    //   Java source line #928	-> byte code offset #108
    //   Java source line #929	-> byte code offset #111
    //   Java source line #932	-> byte code offset #113
    //   Java source line #923	-> byte code offset #121
    //   Java source line #927	-> byte code offset #123
    //   Java source line #928	-> byte code offset #134
    //   Java source line #929	-> byte code offset #137
    //   Java source line #932	-> byte code offset #139
    //   Java source line #959	-> byte code offset #144
    //   Java source line #927	-> byte code offset #147
    //   Java source line #928	-> byte code offset #158
    //   Java source line #929	-> byte code offset #161
    //   Java source line #932	-> byte code offset #163
    //   Java source line #961	-> byte code offset #168
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	169	0	this	MyFrame
    //   0	169	1	localFile	String
    //   0	169	2	remoreFile	String
    //   1	152	3	os	OutputStream
    //   3	151	4	fis	FileInputStream
    //   22	7	5	filein	File
    //   83	3	5	e	Exception
    //   40	17	6	bytes	byte[]
    //   45	5	7	c	int
    //   62	3	7	c	int
    //   121	24	8	localObject	Object
    //   111	3	9	e	IOException
    //   137	3	9	e	IOException
    //   161	3	9	e	IOException
    // Exception table:
    //   from	to	target	type
    //   5	80	83	java/lang/Exception
    //   97	108	111	java/io/IOException
    //   5	97	121	finally
    //   123	134	137	java/io/IOException
    //   147	158	161	java/io/IOException
  }
  
  private void moveFile(String filepath, String destinationFolderPath)
  {
    File original = new File(filepath);
    if (original.exists())
    {
      File fnew = new File(destinationFolderPath + original.getName());
      original.renameTo(fnew);
    }
  }
  
  private String parseString(String channel)
  {
    StringBuilder result = new StringBuilder();
    channel.trim();
    String breakline = "\n";
    String breakline2 = "\r\n";
    String[] channels = channel.split(breakline, 100);
    if ((channels != null) && (channels.length > 0))
    {
      System.out.print(channels.length + "--length--" + breakline);
      String[] arrayOfString1;
      int j = (arrayOfString1 = channels).length;
      for (int i = 0; i < j; i++)
      {
        String item = arrayOfString1[i];
        
        String itemnew = null;
        String breakspace = "    ";
        if (item.contains("_"))
        {
          String first = item.substring(0, item.indexOf("_"));
          String second = item.substring(item.indexOf("_") + 1);
          String third = item;
          String fouth = "false";
          itemnew = first + breakspace + second + breakspace + " " + third + breakspace + fouth + breakline2;
        }
        else
        {
          String first = item;
          String second = "none";
          String third = item;
          String fouth = "false";
          itemnew = first + breakspace + second + breakspace + third + breakspace + fouth + breakline2;
        }
        System.out.print(itemnew + breakline);
        this.channelCode.append(itemnew + "<br>");
        result.append(itemnew);
      }
    }
    setResult(getResult());
    return result.toString().trim();
  }
  
  private String parseString2(String channel)
  {
    StringBuilder result = new StringBuilder();
    channel.trim();
    String space = " ";
    String breakline = "\r\n";
    String[] channels = channel.split(space, 100);
    if ((channels != null) && (channels.length > 0))
    {
      String[] arrayOfString1;
      int j = (arrayOfString1 = channels).length;
      for (int i = 0; i < j; i++)
      {
        String item = arrayOfString1[i];
        
        String itemnew = null;
        String breakspace = "    ";
        if (item.contains("_"))
        {
          String first = item.substring(0, item.indexOf("_"));
          String second = item.substring(item.indexOf("_") + 1);
          String third = item;
          String fouth = "false";
          itemnew = first + breakspace + second + breakspace + third + breakspace + fouth + breakline;
        }
        else
        {
          String first = item;
          String second = "none";
          String third = item;
          String fouth = "false";
          itemnew = first + breakspace + second + breakspace + third + breakspace + fouth + breakline;
        }
        System.out.print(itemnew);
        this.channelCode.append(itemnew + "<br>");
        result.append(itemnew);
      }
    }
    setResult(getResult());
    return result.toString().trim();
  }
}
