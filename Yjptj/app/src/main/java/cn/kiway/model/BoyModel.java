package cn.kiway.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BoyModel implements Serializable {
	/**
	 * 用户id
	 * */
	int uid;
	/**
	 * 用户头像
	 * */
	String url;
	/**
	 * 用户名字
	 * **/
	String name;
	/**
	 * 用户性别
	 * */
	int sex;
	/**
	 * 用户生日
	 * */
	String birthday;
	/**
	 * 班级Id
	 * */
	int classId;
	String string;
	/**
	 * 家长类型
	 * */
	int type;
	/**
	 * 孩子的id
	 * */
	int childId;
	/**
	 * 学校的名字
	 * */
	String schoolName;
	/**
	 * 孩子的名字
	 * */
	String childName;
	/**
	 * 班级名字
	 * */
	String className;
	/**
	 * 老师名字
	 * */
	String teacherName;
	/**
	 * 老师的id
	 * */
	int teacherId;
	/**
	 * 班级年级
	 * */
	int grade;
	/**
	 * 是否绑定了
	 * */
	boolean isBan;

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getUid() {
		return this.uid;
	}

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
		if ("null".equals(this.name))
			return " ";
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
		if ("null".equals(this.birthday))
			return " ";
		return this.birthday;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public int getClassId() {
		return this.classId;
	}

	public void setParentList(String string) {
		this.string = string;
	}

	public String getParentList() {
		return this.string;
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

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getSchoolName() {
		if ("null".equals(this.schoolName))
			return " ";
		return this.schoolName;
	}

	public void setChildName(String childName) {
		this.childName = childName;
	}

	public String getChildName() {
		if ("null".equals(this.childName))
			return " ";
		return this.childName;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		if ("null".equals(this.className))
			return " ";
		return this.className;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public String getTeacherName() {
		if ("null".equals(this.teacherName))
			return " ";
		return this.teacherName;
	}

	public void setTeacherId(int teacherId) {
		this.teacherId = teacherId;
	}

	public int getTeacherId() {
		return this.teacherId;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public int getGrade() {
		return this.grade;
	}

	public void setisBan(boolean isBan) {
		this.isBan = isBan;
	}

	public boolean getIsBan() {
		return this.isBan;
	}
}
