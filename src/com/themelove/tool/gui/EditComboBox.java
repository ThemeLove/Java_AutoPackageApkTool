package com.themelove.tool.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;

import com.themelove.tool.gui.MyComboBoxModel.ComboBoxItemListener;

/**
 *	@author:qingshanliao
 *  @date  :2017年3月20日
 */
public class EditComboBox <E> extends JComboBox<E>{
	private static final long serialVersionUID = 1L;
	private List<E> items=new ArrayList<E>();
	private String  titleBorder;
	private String  hintMessage;
	private MyComboBoxModel.ComboBoxItemListener itemClickListener;
	private MyComboBoxModel<E> model;
	
	public EditComboBox(String titleBorder,String hintMessage,MyComboBoxModel.ComboBoxItemListener itemClickListener){
		this.titleBorder=titleBorder;
		this.hintMessage=hintMessage;
		this.itemClickListener=itemClickListener;
		initView();
	}

	private void initView() {
		setEditable(true);
		setBorder(BorderFactory.createTitledBorder(titleBorder));
		configureEditor(getEditor(), hintMessage);
		updateComboBox(items);
	}
	
	@SuppressWarnings("unchecked")
	private void updateComboBox(List<E> comboBoxItems){
		if (comboBoxItems==null) return;
		items=comboBoxItems;
		model = (MyComboBoxModel<E>) getModel();
		if (model==null) {
			model=new MyComboBoxModel<E>();
		}
		ComboBoxItemListener onItemClickListner = model.getOnItemClickListner();
		if (onItemClickListner==null&&itemClickListener!=null) {
			model.setOnItemClickListener(itemClickListener);
		}
		model.setComboBoxItems(items);
	}
}
