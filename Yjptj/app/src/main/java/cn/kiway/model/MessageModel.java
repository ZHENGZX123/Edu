package cn.kiway.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MessageModel implements Serializable {
	/**
	 * 消息的id
	 * */
	long mid;
	/**
	 * 发送人名字
	 * */
	String name;
	/**
	 * 发送人id
	 * */
	long uid;
	/**
	 * 发给谁的id
	 * */
	long touid;
	/**
	 * 消息内容
	 * */
	String content;
	/**
	 * 发送消息的时间
	 * */
	long time;
	/**
	 * 发送人的头像地址
	 * */
	String url;
	/**
	 * 消息的类型
	 * 
	 * @category1 私信 2家长讨论 3 班级
	 * */
	int msgType;
	/**
	 * 消息未读数
	 * */
	int messgeNumber;
	/**
	 * 作业数
	 * */
	int homerWorkNumber;
	/**
	 * 通知数
	 * */
	int notifyNumber;
	/**
	 * 具体的消息类型
	 * 
	 * @author Administrator 1作业 2通知 3图片消息 4文字 消息
	 * */
	int msgContentType;
	/**
	 * 消息图片地址
	 * */
	String msgPic;
	/**
	 * 发送的状态
	 * */
	int status;

	/**
	 * 讨论组发的人的名字
	 * */
	String userName;
	/**
	 * 数据库中存放消息的id
	 * */
	int dbId;
	/**
	 * 作业或者通知的id
	 * */
	int mId;
	/**
	 * 图片字节
	 * */
	byte[] bs;
	public void setMid(long mid) {
		this.mid = mid;
	}

	public long getMid() {
		return this.mid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		if ("null".equals(this.name))
			return "";
		return this.name;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		if (this.content == null)
			return "";
		return this.content;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return this.time;
	}

	public void setHeadUrl(String url) {
		this.url = url;
	}

	public String getHeadUrl() {
		if ("null".equals(this.url))
			return "";
		return this.url;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getUid() {
		return this.uid;
	}

	public void setToUid(long touid) {
		this.touid = touid;
	}

	public long getToUid() {
		return this.touid;
	}

	public void setMsgType(int msgtype) {
		this.msgType = msgtype;
	}

	/**
	 * 消息的类型
	 * 
	 * @category1 私信 2家长讨论 3 班级
	 * */
	public int getMsgType() {
		return this.msgType;
	}

	public void setMessageNumber(int messageNumber) {
		this.messgeNumber = messageNumber;
	}

	public int getMessageNumber() {
		return this.messgeNumber;
	}

	public void setHomerWorkNumber(int homeWorkNumber) {
		this.homerWorkNumber = homeWorkNumber;
	}

	public int getHomerWorkNumber() {
		return this.homerWorkNumber;
	}

	public void setNotifyNumber(int notifyNumber) {
		this.notifyNumber = notifyNumber;
	}

	public int getNotifyNumber() {
		return this.notifyNumber;
	}

	/**
	 * 1作业 2通知 3图片消息 4文字 消息
	 * */
	public void setMsgContentType(int msgContentType) {
		this.msgContentType = msgContentType;
	}

	/**
	 * 1作业 2通知 3图片消息 4文字 消息
	 * */
	public int getMsgContentType() {
		return this.msgContentType;
	}

	public void setMsgPic(String msgPic) {
		this.msgPic = msgPic;
	}

	public String getMsgPic() {
		return this.msgPic;
	}

	public void setStatu(int statue) {
		this.status = statue;
	}

	public int getStatu() {
		return this.status;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		if ("null".equals(this.userName)) {
			return "";
		}
		return this.userName;
	}

	public void setDbId(int dbId) {
		this.dbId = dbId;
	}

	public int getDbId() {
		return this.dbId;
	}

	public void setMId(int mId) {
		this.mId = mId;
	}

	public int getMId() {
		return this.mId;
	}
	public void setImgBtye(byte[] bs) {
		this.bs = bs;
	}

	public byte[] getImgBtye() {
		return this.bs;
	}
}
