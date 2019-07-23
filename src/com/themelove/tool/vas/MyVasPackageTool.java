package com.themelove.tool.vas;

import java.awt.EventQueue;


/**
 * Android打包工具
 * @author qingshanliao
 * 目前主要打包代码是放在主线程执行的，打包时间过长会卡主GUI界面，后续改进
 */
public class MyVasPackageTool {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyPackageFrame frame = new MyPackageFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
