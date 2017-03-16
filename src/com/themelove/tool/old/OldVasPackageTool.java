package com.themelove.tool.old;

import java.awt.EventQueue;

import com.themelove.tool.my.MyPackageFrame;

/**
 *	@author:qingshanliao
 *  @date  :2017年3月16日
 */
public class OldVasPackageTool {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OldPackageFrame frame = new OldPackageFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
