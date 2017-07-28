package cn.kiway.model;

import java.util.List;

public class GrowthModel {
	/**
	 * 发布人头像地址
	 * */
	String url;
	/**
	 * 发布人名字
	 * */
	String name;
	/**
	 * 发布内容
	 * */
	String content;
	/**
	 * 赞的数量
	 * */
	int zan;
	/**
	 * 评论的数量
	 * */
	int comment;
	/**
	 * 赞的人
	 * */
	String people;
	/**
	 * 时间
	 * */
	long time;
	/**
	 * 相册列表
	 * */
	List<String> list;

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getConent() {
		return this.content;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public List<String> getList() {
		return this.list;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return this.time;
	}

	public void setZan(int zan) {
		this.zan = zan;
	}

	public int getZan() {
		return this.zan;
	}

	public void setComment(int comment) {
		this.comment = comment;
	}

	public int getComment() {
		return this.comment;
	}

	public void setPeople(String people) {
		this.people = people;
	}

	public String getPeople() {
		return this.people;
	}
}
