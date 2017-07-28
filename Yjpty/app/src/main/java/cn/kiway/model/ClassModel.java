package cn.kiway.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ClassModel implements Serializable {
	/**
	 * 班级id
	 * */
	int id;
	/**
	 * 班级盒子编号
	 * */
	String no;
	/**
	 * 学校id
	 * */
	int school;
	/**
	 * 班级名字
	 * */
	String class_name;
	/**
	 * 盒子二维码
	 * */
	String hezi_code;
	/**
	 * 孩子数
	 * */
	int childNum;
	/**
	 * 幼儿园名字
	 * */
	String schoolName;
	/**
	 * 班级谁创建的
	 * */
	int createId;
	/**
	 * 年级
	 * */
	int year;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getNo() {
		return this.no;
	}

	public void setSchoolId(int schoolId) {
		this.school = schoolId;
	}

	public int getSchoolId() {
		return this.school;
	}

	public void setClassName(String className) {
		this.class_name = className;
	}

	public String getClassName() {
		return this.class_name;
	}

	public void setHeZiCode(String heZiCode) {
		this.hezi_code = heZiCode;
	}

	public String getHeZiCode() {
		return this.hezi_code;
	}

	public void setChildNum(int childNum) {
		this.childNum = childNum;
	}

	public int getChildNum() {
		return this.childNum;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getSchoolName() {
		return this.schoolName;
	}

	public void setCreateId(int createId) {
		this.createId = createId;
	}

	public int getCreateId() {
		return this.createId;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getYear() {
		return this.year;
	}
}
