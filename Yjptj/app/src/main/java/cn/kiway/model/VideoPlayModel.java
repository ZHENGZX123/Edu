package cn.kiway.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VideoPlayModel implements Serializable {
	/**
	 * 视频标题
	 * */
	String content;
	/**
	 * 视频地址
	 * */
	String url;

	/**
	 * 视频的后缀名
	 * */
	String icon;

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return this.content;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIcon() {
		return this.icon.replace(" ", "");
	}
}
