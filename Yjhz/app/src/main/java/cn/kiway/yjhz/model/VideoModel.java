package cn.kiway.yjhz.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VideoModel implements Serializable {
    /**
     * 视频id
     */
    int id;
    /**
     * 视频类型type
     */
    int type;
    /**
     * 视频名字
     */
    String name;
    /**
     * 视频时间
     */
    String requireTime;
    /**
     * 视频图像
     */
    String preview;
    /**
     * 教学目标
     */
    String teachingAim;
    /**
     * 教学计划
     */
    String teachingPreare;
    /**
     * 家庭作业
     */
    String homework;
    /**
     * 分类图像
     */
    String typeName;
    /**
     * 课程属于哪个年级
     */
    String gragde;
    /**
     * 所属的课程名字
     */
    String sessionName;
    /**
     * 体感课程包名
     */
    String kinectPackageName;
    /**
     * 是否为体感课程
     */
    boolean isKiectSession;
    /**
     * 播放的次数
     */
    int readCount;
    /***
     * 体感课程介绍
     * */
    String KiectSessionContent;
    /***
     * 体感课程apk下载地址
     * */
    String KiectApkDownLoadUrl;

    public String getPingYin() {
        return pingYin.replace("#","").replace("-","");
    }

    public void setPingYin(String pingYin) {
        this.pingYin = pingYin;
    }

    /**
     * 拼音
     * */
    String pingYin;

    public String getKiectApkDownLoadUrl() {
        if (KiectApkDownLoadUrl.equals(""))
            KiectApkDownLoadUrl = "null";
        return KiectApkDownLoadUrl;
    }

    public void setKiectApkDownLoadUrl(String kiectApkDownLoadUrl) {
        KiectApkDownLoadUrl = kiectApkDownLoadUrl;
    }

    public int getIsKT() {
        return isKT;
    }

    public void setIsKT(int isKT) {
        this.isKT = isKT;
    }

    /**
     * 体感课程是否开通
     */
    int isKT;

    public String getKiectSessionContent() {
        if (KiectSessionContent.equals("null"))
            KiectSessionContent = "暂无介绍";
        return KiectSessionContent;
    }

    public void setKiectSessionContent(String kiectSessionContent) {
        KiectSessionContent = kiectSessionContent;
    }

    public boolean isKiectSession() {
        return isKiectSession;
    }

    public void setKiectSession(boolean kiectSession) {
        isKiectSession = kiectSession;
    }

    public String getKinectPackageName() {
        return kinectPackageName;
    }

    public void setKinectPackageName(String kinectPackageName) {
        this.kinectPackageName = kinectPackageName;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public String getSessionName() {
        if (sessionName == null)
            sessionName = "";
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        if (name == null)
            this.name = "";
        return this.name;
    }

    public String getRequireTime() {
        return this.requireTime;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getPreview() {
        return this.preview;
    }

    public void setTeachingAim(String teachingAim) {
        this.teachingAim = teachingAim;
    }

    public String getTeachingAim() {
        return this.teachingAim;
    }

    public void setTeachingPreare(String teachingPreare) {
        this.teachingPreare = teachingPreare;
    }

    public String getTeachingPreare() {
        return this.teachingPreare;
    }

    public void setHomework(String homework) {
        this.homework = homework;
    }

    public String getTypeName() {
        if (null == typeName) {
            typeName = "";
        }
        if (typeName != null && !typeName.equals("")) {
            typeName.replace(",", " ").replace("，", " ");
        }
        return typeName;
    }


    public void setGrader(String grader) {
        this.gragde = grader;
    }

    public String getGrader() {
        return this.gragde;
    }
}
