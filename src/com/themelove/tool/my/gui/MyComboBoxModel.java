package com.themelove.tool.my.gui;

import java.util.List;

import javax.swing.DefaultComboBoxModel;

/**
 * @author:lqs
 * date	  :2017年3月20日
 */
@SuppressWarnings("serial")
public class MyComboBoxModel<E> extends DefaultComboBoxModel<Object>{
	private ComboBoxItemListener mItemClickListener;

	public void setComboBoxItems(List<E> items){
		removeAllElements();
		for (Object object : items) {
			addElement(object);
		}
	}
	
	@Override
	public Object getSelectedItem() {
		if (mItemClickListener!=null) {
			mItemClickListener.OnComboBoxItemClickListener(super.getSelectedItem());
		}
		return super.getSelectedItem();
	}



	public void setOnItemClickListener(ComboBoxItemListener listener){
		mItemClickListener=listener;
	}
	public ComboBoxItemListener getOnItemClickListner(){
		return mItemClickListener;
	}
	
	public interface  ComboBoxItemListener{
		<E> void  OnComboBoxItemClickListener(E item);
	}
}
