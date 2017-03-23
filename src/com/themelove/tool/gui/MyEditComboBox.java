package com.themelove.tool.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;

/**
 *	@author:qingshanliao
 *  @date  :2017年3月21日
 */
public class  MyEditComboBox <E> extends JComboBox<E>{
	private static final long serialVersionUID = 1L;
	private String mTitleBorder;
	private String mHintMessage;
	private OnComboBoxItemClickListener<E> mListener;
	private List<E> mItemDatas=new ArrayList<E>();
	public MyEditComboBox(String titleBorder,String hintMessage,OnComboBoxItemClickListener<E> listener){
		this.mTitleBorder=titleBorder;
		this.mHintMessage=hintMessage;
		this.mListener=listener;
//		设置默认不选中第一项
		setSelectedIndex(-1);
		setBorder(BorderFactory.createTitledBorder(mTitleBorder));
		setEditable(true);
		configureEditor(getEditor(), mHintMessage);
		addListener();
		updateComboBox(mItemDatas);
	}
	
	/**
	 * 添加监听
	 */
	private void addListener() {
		getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			

			@Override
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
			}

			/**
			 * 键盘按键弹起的监听
			 */
			@Override
			public void keyReleased(KeyEvent e) {
				String inputText = getEditor().getItem().toString().trim();
				if (mListener!=null) {
					mListener.OnEditInputListener(inputText);
				}
			}
		});

		addItemListener(new ItemListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					E item = (E) e.getItem();
					if (mListener != null) {
						mListener.OnItemClickListener(item);
					}
				}
			}
		});
	}

	/**
	 * 更新ComboBoxItems
	 * @param itemDatas
	 */
	public void updateComboBox(List<E> itemDatas){
		if (itemDatas==null) return;
		mItemDatas=itemDatas;
		removeAllItems();
		for (E e : itemDatas) {
			addItem(e);
		}
	}
	
	public   interface  OnComboBoxItemClickListener<E>{
		  void OnItemClickListener(E e);
		  void OnEditInputListener(String inputText);
	}
}
