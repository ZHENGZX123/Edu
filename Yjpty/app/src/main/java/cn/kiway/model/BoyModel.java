package cn.kiway.model;

import java.io.Serializable;

/**
 * 全局用户信息的model
 * */
@SuppressWarnings("serial")
public class BoyModel implements Serializable {
	/**
	 * 用户ID
	 * */
	long Uid;
	/**
	 * 用户头像
	 * */
	String Img;
	/**
	 * 用户名字
	 * */
	String name;
	/**
	 * 用户手机号
	 * 
	 * @see 在老师详情处用到
	 * */
	String phone;
	/**
	 * 是否选择
	 * 
	 * @see 在点名出用到
	 * */
	boolean ischeck;
	/**
	 * 用户性别
	 * 
	 * @category 男2女
	 * */
	int sex;
	/**
	 * 用户生日
	 * */
	String brithday;
	/**
	 * 用户id
	 * */
	int userId;
	/**
	 * 班级id
	 * */
	int classId;

	/**
	 * 宝贝的谁
	 * */
	int type;
	/**
	 * 孩子Id
	 * */
	int childId;

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public int getClassId() {
		return this.classId;
	}

	public void setUid(long Uid) {
		this.Uid = Uid;
	}

	public long getUid() {
		return Uid;
	}

	public void setImg(String Img) {
		this.Img = Img;
	}

	public String getImg() {
		return Img;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setIscheck(boolean ischeck) {
		this.ischeck = ischeck;
	}

	public boolean getIscheck() {
		return ischeck;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getSex() {
		return this.sex;
	}

	public void setBrithday(String brithday) {
		this.brithday = brithday;
	}

	public String getBrithday() {
		return this.brithday;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return this.type;
	}

	public void setChildId(int childId) {
		this.childId = childId;
	}

	public int getChildId() {
		return this.childId;
	}
}
