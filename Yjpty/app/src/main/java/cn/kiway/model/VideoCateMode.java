package cn.kiway.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VideoCateMode implements Serializable {
	/**
	 * 视频分类id
	 * */
	int id;
	/**
	 * 视频分类类型
	 * */
	int dirType;
	/**
	 * 视频分类名称
	 * */
	String name;
	/**
	 * 视频分类图像
	 * */
	String preview;
	/**
	 * 分类Id
	 * */
	int dirId;
	/**
	 * 是否为自定义
	 * */
	int isUser;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setDirType(int dirType) {
		this.dirType = dirType;
	}

	public int getDirType() {
		return this.dirType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public String getPreview() {
		return this.preview;
	}

	public void setDirId(int dirId) {
		this.dirId = dirId;
	}

	public int getDirId() {
		return this.dirId;
	}

	public void setIsUser(int isUser) {
		this.isUser = isUser;
	}

	public int getIsUser() {
		return this.isUser;
	}
}
