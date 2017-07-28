package cn.kiway.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SchoolNewsModel implements Serializable {
	/**
	 * 新闻标题
	 * */
	String schoolTitle;
	/**
	 * 新闻id
	 * */
	int schoolId;
	/**
	 * 新闻图像
	 * */
	String schoolImg;

	public void setSchoolTitle(String schoolTitle) {
		this.schoolTitle = schoolTitle;
	}

	public String getSchoolTitle() {
		return this.schoolTitle;
	}

	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}

	public int getSchoolId() {
		return this.schoolId;
	}

	public void setSchoolImg(String schoolImg) {
		this.schoolImg = schoolImg;
	}

	public String getSchoolImg() {
		return this.schoolImg;
	}
}
