package cn.kiway.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VideoModel implements Serializable {
	/**
	 * 视频的课程id
	 * */
	int lessonId;
	/***
	 * 视频课程名字
	 * */
	String lessonName;
	/**
	 * 视频图像
	 * */
	String preview;
	/**
	 * 视频播放时间
	 * */
	String finishData;

	/**
	 * 课程目标
	 * */
	String teachingAim;
	/**
	 * 课程时长
	 * */
	String requireTime;

	public void setLessionId(int lessonId) {
		this.lessonId = lessonId;
	}

	public int getLessionId() {
		return this.lessonId;
	}

	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}

	public String getLessonName() {
		return this.lessonName ;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public String getPreview() {
		return this.preview;
	}

	public void setFinishData(String finishData) {
		this.finishData = finishData;
	}

	public String getFinishData() {
		return this.finishData;
	}

	public void setTeachingAim(String teachingAim) {
		this.teachingAim = teachingAim;
	}

	public String getTeachingAim() {
		return this.teachingAim;
	}

	public void setRequireTime(String requireTime) {
		this.requireTime = requireTime;
	}

	public String getRequireTime() {
		return this.requireTime+ "分钟";
	}
}
