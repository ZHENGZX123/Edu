package com.sortlistview;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GroupMemberBean implements Serializable {

	private String name; // 显示的数据
	private String sortLetters; // 显示数据拼音的首字母
	private int id;// 用户的id
	private boolean isSelector;// 是否选择
	private boolean isAdd;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setIsSelector(boolean isSelector) {
		this.isSelector = isSelector;
	}

	public boolean getIsSelector() {
		return this.isSelector;
	}

	public void setIsAdd(boolean isAdd) {
		this.isAdd = isAdd;
	}

	public boolean getIsAdd(){
		return this.isAdd;
	}
}
