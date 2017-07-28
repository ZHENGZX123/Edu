package cn.kiway.yjhz.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ClassModel implements Serializable {
    /**
     * 班级id
     */
    String id;
    /**
     * 班级盒子编号
     */
    String no;
    /**
     * 学校id
     */
    String schoolId;
    /**
     * 班级名字
     */
    String class_name;
    /**
     * 盒子二维码
     */
    String hezi_code;
    /**
     * 幼儿园名字
     */
    String schoolName;
    /**
     * 年级
     */
    String year;

    public String getIsActivateKinect() {
        return isActivateKinect;
    }

    public void setIsActivateKinect(String isActivateKinect) {
        this.isActivateKinect = isActivateKinect;
    }

    /**
     * 是否开通了体感课程
     * */

    String isActivateKinect;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getNo() {
        return this.no;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolId() {
        return this.schoolId;
    }

    public void setClassName(String className) {
        this.class_name = className;
    }

    public String getClassName() {
        return this.class_name;
    }

    public String getHezi_code() {
        if (null == hezi_code || hezi_code.equals("null")||hezi_code.equals("未绑定"))
            hezi_code = "";
        return hezi_code;
    }

    public void setHezi_code(String hezi_code) {
        this.hezi_code = hezi_code;
    }


    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolName() {
        return this.schoolName;
    }


    public void setYear(String year) {
        this.year = year;
    }

    public String getYear() {
        return this.year;
    }
}
