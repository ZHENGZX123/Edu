package cn.kiway.message.model;

/**
 * 消息发送状态
 * */
public class MesageStatus {
	public static final int SEND_ING = 0;// 正在发送
	public static final int SEND_ERR = 1;// 发送出错
	public static final int SEND_SUCC = 2;// 发送成功
	public static final int READ = 3;// 已读
	public static final int UN_READ = 4;// 未读
}
