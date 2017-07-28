package cn.kiway.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BoyCheckModel implements Serializable {
	/**
	 * 孩子的id
	 * */
	int uid;
	/**
	 * 检查的类型
	 * */
	int type;
	/**
	 * 选择哪一个
	 * */
	int level;

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getUid() {
		return this.uid;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return this.type;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return this.level;
	}

}
