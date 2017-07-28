package cn.kiway.model;

public class CommentModel {
	/**
	 * 评论头像
	 * */
	String url;
	/**
	 * 评论人名字
	 * */
	String name;
	/**
	 * 评论时间
	 * */
	long commentTime;
	/**
	 * 评论内容
	 * */
	String content;

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setCommentTime(long commentTime) {
		this.commentTime = commentTime;
	}

	public long getCommentTime() {
		return this.commentTime;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return this.content;
	}
}
