package cn.kiway.model;

import java.io.Serializable;

/**
 * 多重相册选择数据模型
 * */
@SuppressWarnings("serial")
public class SelectPictureModel implements Serializable{
	/**
	 * 相片地址
	 * */
	private String picPath;
	/**
	 * 是否选择
	 * */
	private boolean selectStatus;

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public boolean isSelectStatus() {
		return selectStatus;
	}

	public void setSelectStatus(boolean selectStatus) {
		this.selectStatus = selectStatus;
	}

	public SelectPictureModel(String picPath, boolean selectStatus) {
		super();
		this.picPath = picPath;
		this.selectStatus = selectStatus;
	}

}
