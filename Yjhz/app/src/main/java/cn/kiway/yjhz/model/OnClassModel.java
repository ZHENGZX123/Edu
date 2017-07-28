package cn.kiway.yjhz.model;


import java.io.Serializable;

public class OnClassModel implements Serializable {
    /**
     * 文字介绍
     */
    String content;

    /**
     * 是否在进行
     */
    boolean bool;
    /**
     * 视频uuid加后缀名
     */
    String Uuid;

    public String getVideoUrl() {
        if (videoUrl == null || videoUrl.equals(""))
            videoUrl = "null";
        if (!videoUrl.equals("null") && Uuid.contains("mp4") && !videoUrl.contains("?type=show"))
            videoUrl = videoUrl + "?type=show";
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    /**
     * 视频地址
     */
    String videoUrl;

    public String getContent() {
        if (null == content || content.equals(""))
            content = "暂无介绍！";
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUuid() {
        return Uuid;
    }

    public void setUuid(String uuid) {
        Uuid = uuid;
    }


    public void setBool(boolean b) {
        this.bool = b;
    }

    public boolean getBool() {
        return this.bool;
    }
}
