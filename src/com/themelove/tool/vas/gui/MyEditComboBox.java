package com.themelove.tool.vas.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
	private ComboBoxEditor mEditor;
	private JTextField mEditText;
	
	private ItemListener mItemListener = new ItemListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("itemStateChanged-----Selected");
				System.out.println(getSelectedItem().toString());
				System.out.println("item 类型-----"+getSelectedItem().getClass().getSimpleName());
				if(getSelectedItem() instanceof String){
					
					System.out.println("String");
				}
				
				E selectedItem = (E) getSelectedItem();
				
				if (mListener != null) {
					mListener.OnItemClickListener(selectedItem);
				}
			}
		}
	};
	
	private KeyAdapter mKeyAdapter=new KeyAdapter() {
		

		@Override
		public void keyTyped(KeyEvent e) {
			super.keyTyped(e);
		}

		/**
		 * 键盘按键弹起的监听,这里主要监听删除按键
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			//如果是Backspace和Delete键才处理
			if (e.getKeyChar()==KeyEvent.VK_BACK_SPACE||e.getKeyChar()==KeyEvent.VK_DELETE) {
				String searchText = mEditText.getText().trim();
				System.out.println("keyReleased-----searchText-----"+searchText);
				if (mListener!=null) {
					mListener.OnEditInputListener(searchText);
				}
			}
		}
	};
	
	private DocumentListener mDocumentListener=new DocumentListener() {
		//文本移除监听
		@Override
		public void removeUpdate(DocumentEvent e) {
			System.out.println("removeUpdate:------>"+mEditText.getText());
		}
		
		//文本插入监听
		@Override
		public void insertUpdate(DocumentEvent e) {
			System.out.println("insertUpdate:------>"+mEditText.getText());
			String searchText = mEditText.getText().trim();
			if (mListener!=null) {
				mListener.OnEditInputListener(searchText);
			}
		}
		
		@Override
		public void changedUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub  基本不用
		}
	};
	
	public MyEditComboBox(String titleBorder,String hintMessage,OnComboBoxItemClickListener<E> listener){
		this.mTitleBorder=titleBorder;
		this.mHintMessage=hintMessage;
		this.mListener=listener;

		mEditor = getEditor();
		mEditText = (JTextField) mEditor.getEditorComponent();
		initView();
	}
	private void initView(){
		setBorder(BorderFactory.createTitledBorder(mTitleBorder));
		setEditable(true);
		configureEditor(mEditor, mHintMessage);
		updateComboBox(mItemDatas);
		addALLListener();
	}
	
	/**
	 * 添加监听
	 */
	private void addALLListener() {
//		监听键盘事件
		mEditText.addKeyListener(mKeyAdapter);
//		mEditText.getDocument().addDocumentListener(mDocumentListener);
		addItemListener(mItemListener);
	}

	private void removeAllListener(){
		mEditText.removeKeyListener(mKeyAdapter);
		mEditText.getDocument().removeDocumentListener(mDocumentListener);
		removeItemListener(mItemListener);
	}
	
	
	/**
	 * 更新ComboBoxItems
	 * @param itemDatas
	 */
	public void updateComboBox(List<E> itemDatas){
		if (itemDatas==null||itemDatas.size()==0) return;
		mItemDatas=itemDatas;
		removeAllListener();
//		removeAll();
		removeAllItems();
		
		for (E e : mItemDatas) {
			addItem(e);
		}
		addALLListener();
		setSelectedIndex(-1);
	}
	
	public   interface  OnComboBoxItemClickListener<E>{
		  void OnItemClickListener(E e);
		  void OnEditInputListener(String inputText);
	}
}
