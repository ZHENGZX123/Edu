package cn.kiway.model;

public class BabyUploadModel {
	/**
	 * 宝贝的id
	 * */
	int uid;
	/**
	 * 宝贝的名字
	 * */
	String name;
	/**
	 * 宝贝的性别
	 * */
	int sex;
	/**
	 * 宝贝的生日
	 * */
	String birthday;
	/**
	 * 宝贝的谁
	 * */
	int type;

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getUid() {
		return this.uid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getSex() {
		return this.sex;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getBirthday() {
		return this.birthday;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return this.type;
	}
}
