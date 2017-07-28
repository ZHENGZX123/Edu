package cn.kiway.model;


public class OnClassModel {
	/**
	 * 文字介绍
	 * */
	String string;

	/**
	 * 是否在进行
	 * */
	boolean bool;

	public void setSting(String string) {
		this.string = string;
	}

	public String getString() {
		return this.string;
	}

	public void setBool(boolean b) {
		this.bool = b;
	}

	public boolean getBool() {
		return this.bool;
	}
}
