package com.themelove.tool.my;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *	@author:qingshanliao
 *  @date  :2017年3月16日
 */
public class MyPackageFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the frame.
	 */
	public MyPackageFrame() {
		setTitle("MyVasPackageTool");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 588, 430);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu menu = new JMenu("\u64CD\u4F5C");
		menuBar.add(menu);
		
		JMenuItem mntmapktool = new JMenuItem("\u9009\u62E9apktool\u7248\u672C");
		menu.add(mntmapktool);
		
		JMenuItem menuItem_2 = new JMenuItem("\u9009\u62E9\u6253\u5305\u65B9\u5F0F");
		menu.add(menuItem_2);
		
		JMenu menu_1 = new JMenu("\u5E2E\u52A9");
		menuBar.add(menu_1);
		
		JMenuItem menuItem = new JMenuItem("\u4F7F\u7528\u8BF4\u660E");
		menu_1.add(menuItem);
		
		JMenuItem menuItem_1 = new JMenuItem("\u5173\u4E8E");
		menu_1.add(menuItem_1);
		getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("\u9009\u62E9\u6E20\u9053\u914D\u7F6E\u6587\u4EF6");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton.setBounds(433, 0, 129, 32);
		getContentPane().add(btnNewButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setToolTipText("\u554A\u554A\u554A\u554A\u554A\u554A\u554A\u554A\u554A\u554A\u554A\u554A\u554A");
		scrollPane.setBounds(0, 42, 562, 209);
		getContentPane().add(scrollPane);
		
		JTextArea area = new JTextArea();
		area.setBackground(SystemColor.inactiveCaptionBorder);
		area.setText("\u5DF2\u9009\u62E9\u6E20\u9053\u5217\u8868");
		scrollPane.setViewportView(area);
		
		JButton button = new JButton("\u6253\u5305");
		button.setBounds(487, 313, 75, 48);
		getContentPane().add(button);
		
		JButton button_1 = new JButton("\u9009\u62E9\u7B7E\u540D\u6587\u4EF6");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		button_1.setBounds(158, 313, 105, 48);
		getContentPane().add(button_1);
		
		JButton btnLog = new JButton("log\u6587\u4EF6");
		btnLog.setBounds(402, 313, 75, 48);
		getContentPane().add(btnLog);
		
		JButton button_2 = new JButton("\u91CD\u7F6E");
		button_2.setBounds(291, 313, 84, 48);
		getContentPane().add(button_2);
		
		JButton button_3 = new JButton("\u6DFB\u52A0\u6BCD\u5305");
		button_3.setBounds(10, 313, 105, 48);
		getContentPane().add(button_3);
		
		JEditorPane editorPane = new JEditorPane();
		editorPane.setBounds(0, 0, 414, 32);
		getContentPane().add(editorPane);
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
