package cn.kiway.model;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class GrowthListModel implements Serializable {
	/**
	 * 足迹的id
	 * */
	int id;
	/**
	 * 谁发的足迹的id
	 * */
	int uid;
	/**
	 * 足迹的创建时间
	 * */
	String creatTime;
	/**
	 * 足迹内容
	 * */
	String content;
	/**
	 * 足迹是否公开
	 * */
	int ispublic;
	/**
	 * 足迹图像地址
	 * */
	List<String> picList;
	/**
	 * 孩子头像
	 * */
	String usrImg;
	/**
	 * 孩子名字
	 * */
	String userName;
	/**
	 * 孩子的id
	 * */
	int childId;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getUid() {
		return this.uid;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return this.content;
	}

	public void setIsPublic(int isPublic) {
		this.ispublic = isPublic;
	}

	public int getIsPublic() {
		return this.ispublic;
	}

	public void setPicList(List<String> piclist) {
		this.picList = piclist;
	}

	public List<String> getPicList() {
		return this.picList;
	}

	public void setCreateTime(String createTime) {
		this.creatTime = createTime;
	}

	public String getCreateTime() {
		return this.creatTime;
	}

	public void setUserImg(String userImg) {
		this.usrImg = userImg;
	}

	public String getUserImg() {
		return this.usrImg;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return this.userName;
	}
	public void setChildId(int childId){
		this.childId=childId;
	}
	public int getChildId(){
		return this.childId;
	}
}
