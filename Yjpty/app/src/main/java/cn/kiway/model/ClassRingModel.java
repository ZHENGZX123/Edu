package cn.kiway.model;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class ClassRingModel implements Serializable {
	/**
	 * 班级圈Id
	 * */
	int classRingId;
	/**
	 * 谁发的班级圈
	 * */
	int userId;
	/**
	 * 谁发的班级全的名字
	 * */
	String userName;
	/**
	 * 班级圈的内容
	 * */
	String content;
	/**
	 * 谁发的班级的头像
	 * */
	String userImg;
	/**
	 * 班级圈的图片列表
	 * */
	List<String> listPhoto;
	/**
	 * 什么时候发的班级圈
	 * */
	long time;
	/**
	 * 班级圈赞的列表
	 * */
	String Prasie;
	/**
	 * 评论的id
	 * */
	int commentId;
	/**
	 * 评论用户的Id
	 * */
	int commentUserId;
	/**
	 * 评论人的名字
	 * */
	String commentName;
	/**
	 * 评论的时间
	 * */
	long commentTime;
	/**
	 * 评论的内容
	 * */
	String commentContent;
	/**
	 * 评论列表
	 * */
	List<ClassRingModel> models;
	/**
	 * 赞的人列表
	 * */
	boolean isPriase;
	/**
	 * 是否显示全文
	 * */
	boolean isQuan;

	public void setClassRingId(int classRingId) {
		this.classRingId = classRingId;
	}

	public int getClassRingId() {
		return this.classRingId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return this.content;
	}

	public void setListPhoto(List<String> photoList) {
		this.listPhoto = photoList;
	}

	public List<String> getLisPhoto() {
		return this.listPhoto;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return this.time;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public int getCommentId() {
		return this.commentId;
	}

	public void setPriase(String Priase) {
		this.Prasie = Priase;
	}

	public String getPriase() {
		return this.Prasie;
	}

	public void setCommentUserId(int commentUserId) {
		this.commentUserId = commentUserId;
	}

	public int getCommentUserId() {
		return this.commentUserId;
	}

	public void setCommentName(String commnentName) {
		this.commentName = commnentName;
	}

	public String getCommentName() {
		return this.commentName;
	}

	public void setCommentTime(long commentTime) {
		this.commentTime = commentTime;
	}

	public long getCommentTime() {
		return this.commentTime;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	public String getCommentContent() {
		return this.commentContent;
	}

	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}

	public String getUserImg() {
		return this.userImg;
	}

	public void setReplyList(List<ClassRingModel> models) {
		this.models = models;
	}

	public List<ClassRingModel> getReplyList() {
		return this.models;
	}

	public void setPraiseId(boolean isPriase) {
		this.isPriase = isPriase;
	}

	public boolean getRraise() {
		return this.isPriase;
	}
	public void setIsQuan(boolean isQuan){
		this.isQuan=isQuan;
	}
	public boolean getIsQuan(){
		return this.isQuan;
	}
}
