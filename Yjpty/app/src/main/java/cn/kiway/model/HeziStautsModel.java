package cn.kiway.model;

public class HeziStautsModel {
	/**
	 * 盒子Id
	 * */
	String HeziCode;
	/**
	 * 盒子连接状态
	 * */
	int heziType;
	/**
	 * 班级名字
	 * */
	String className;
	/**
	 * 盒子的年级
	 * */
	int grade;
	/**
	 * 盒子的ip地址
	 * */
	String heziIP;
	/**
	 * 盒子资源
	 * */
	String heziResoures;

	/**
	 * 对应资源
	 * */
	String string = "";
	/**
	 * udp广播接收时间
	 * */
	long acceptUdpTime;

	public long getAcceptUdpTime() {
		return acceptUdpTime;
	}

	public void setAcceptUdpTime(long acceptUdpTime) {
		this.acceptUdpTime = acceptUdpTime;
	}

	public String getHeziResoures() {
		if (heziResoures == null)
			heziResoures = getString();
		return heziResoures;
	}

	public void setHeziResoures(String heziResoures) {
		this.heziResoures = heziResoures;
	}

	public String getHeziIP() {
		return heziIP;
	}

	public void setHeziIP(String heziIP) {
		this.heziIP = heziIP;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public String getHeziCode() {
		if ("null".equals(HeziCode) || "".equals(HeziCode)) {
			return "未绑定";
		}
		return HeziCode;
	}

	public void setHeziCode(String heziCode) {
		HeziCode = heziCode;
	}

	public int getHeziType() {
		return heziType;
	}

	public void setHeziType(int heziType) {
		this.heziType = heziType;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getString() {
		String string = "";
		if (grade == 0) {
			string = "";
		} else if (grade == 1) {
			string = "大大班";
		}
		if (grade == 2) {
			string = "大班";
		}
		if (grade == 3) {
			string = "中班";
		}
		if (grade == 4) {
			string = "小班";
		}
		return string;
	}
}
