package com.themelove.tool.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.ListDataListener;

import com.themelove.tool.my.bean.Game;

/**
 *	@author:qingshanliao
 *  @date  :2017年3月20日
 */
public class EditComboBox <E> extends JComboBox<E>{
	private String titleBorder;
	private String hintMessage;
	public EditComboBox(String titleBorder,String hintMessage){
		this.titleBorder=titleBorder;
		this.hintMessage=hintMessage;
		
		
		initView();
	}

	private void initView() {
		setEditable(true);
		setBorder(BorderFactory.createTitledBorder(titleBorder));
		configureEditor(getEditor(), hintMessage);
		addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
//				e.get
			}
		});
	}
	
	private void updateComboBox(List comboBoxItems){
		
	}
	
}
