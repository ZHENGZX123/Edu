package cn.kiway.yjhz.model;

import java.io.Serializable;

import cn.kiway.yjhz.R;

@SuppressWarnings("serial")
public class VideoCateMode implements Serializable {
    /**
     * 视频分类id
     */
    int id;
    /**
     * 视频分类名称
     */
    String name;
    /**
     * 视频分类图像
     */
    String preview="";
    String Kcln;// 课程理念
    String Kcjy;// 课程建议
    String Type;//视频归类
    /**
     * 课程总数
     */
    String allCount;
    /**
     * 课程上的课数
     */
    String readCount;
    /**
     * 视频的年级
     */
    String gradeId;

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public String getAllCount() {
        return allCount;
    }

    public String getReadCount() {
        return readCount;
    }

    public void setReadCount(String readCount) {
        this.readCount = readCount;
    }

    public void setAllCount(String allCount) {
        this.allCount = allCount;
    }

    public String getType() {
        if (Type == null)
            Type = "";
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getKcln() {
        if (null == Kcln || "null".equals(Kcln))
            Kcln = "";
        return Kcln;
    }

    public void setKcln(String kcln) {
        Kcln = kcln;
    }

    public String getKcjy() {
        if (null == Kcjy || "null".equals(Kcjy))
            Kcjy = "";
        return Kcjy;
    }

    public void setKcjy(String kcjy) {
        Kcjy = kcjy;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getPreview() {

        return this.preview;
    }

}
