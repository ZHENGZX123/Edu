package cn.kiway.model;

public class JoinsClassModel {
	/**
	 * 班级名字
	 * */
	String className;
	/**
	 * 班级创建者
	 * */
	String createClassPName;
	/**
	 * 班级id
	 * */
	int classId;
	/**
	 * 是否选择
	 * */
	boolean isSelect;
	int classGraid;

	public int getClassGraid() {
		return classGraid;
	}

	public void setClassGraid(int classGraid) {
		this.classGraid = classGraid;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getCreateClassPName() {
		return createClassPName;
	}

	public void setCreateClassPName(String createClassPName) {
		this.createClassPName = createClassPName;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
}
