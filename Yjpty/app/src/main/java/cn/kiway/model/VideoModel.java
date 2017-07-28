package cn.kiway.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VideoModel implements Serializable {
	/**
	 * 视频id
	 * */
	int id;
	/**
	 * 视频dir_id
	 * */
	int dirId;
	/**
	 * 视频类型type
	 * */
	int type;
	/**
	 * 视频名字
	 * */
	String name;
	/**
	 * 视频时间
	 * */
	String requireTime;
	/**
	 * 视频图像
	 * */
	String preview;
	/**
	 * cateId
	 * */
	int cate;
	/**
	 * 教学目标
	 * */
	String teachingAim;
	/**
	 * 教学计划
	 * */
	String teachingPreare;
	/**
	 * 家庭作业
	 * */
	String homework;
	/**
	 * 主课
	 * */
	String dirName;
	/**
	 * 分类图像
	 * */
	String dirPreview;
	String typeName;
	int seqno;
	/**
	 * 课程属于哪个年级
	 * */
	int gragde;
	/**
	 * 是否为自定义
	 * */
	int isUser;
	/**
	 * 所属的课程名字
	 * */
	String sessionName;
	/**
	 * 课程定义播放的时间
	 * */
	String sessionTime;
	/**
	 * 课程上课的播放时间
	 * */
	String seesionPlayTime;
	/**
	 * 是否为当前课程
	 * */
	boolean isTotalSession;

	public boolean isTotalSession() {
		return isTotalSession;
	}

	public void setTotalSession(boolean isTotalSession) {
		this.isTotalSession = isTotalSession;
	}

	public String getSeesionPlayTime() {
		if ("null".equals(seesionPlayTime) || seesionPlayTime == null)
			seesionPlayTime = "";
		return seesionPlayTime;
	}

	public void setSeesionPlayTime(String seesionPlayTime) {
		this.seesionPlayTime = seesionPlayTime;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getSessionTime() {
		if ("null".equals(sessionTime) || sessionTime == null)
			sessionTime = "";
		return sessionTime;
	}

	public void setSessionTime(String sessionTime) {
		this.sessionTime = sessionTime;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setDirId(int dirId) {
		this.dirId = dirId;
	}

	public int getDirId() {
		return this.dirId;
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
		return this.name;
	}

	public void setRequireTime(String requireTime) {
		this.requireTime = requireTime;
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

	public void setCate(int cate) {
		this.cate = cate;
	}

	public int getCate() {
		return this.cate;
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

	public String getHomework() {
		return this.homework;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	public String getDirName() {
		return this.dirName;
	}

	public void setDirPreview(String dirPreview) {
		this.dirPreview = dirPreview;
	}

	public String getDirPreview() {
		return this.dirPreview;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return this.typeName.replace(",", " ").replace("，", " ");
	}

	public void setSeqNo(int seqno) {
		this.seqno = seqno;
	}

	public int getSeqNo() {
		return this.seqno;
	}

	public void setGrader(int grader) {
		this.gragde = grader;
	}

	public int getGrader() {
		return this.gragde;
	}

	public void setIsUser(int isUser) {
		this.isUser = isUser;
	}

	public int getIsUser() {
		return this.isUser;
	}
}
