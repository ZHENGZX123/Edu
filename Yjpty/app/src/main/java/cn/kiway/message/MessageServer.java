package cn.kiway.message;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import cn.kiway.App;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.activity.BaseActivity;
import cn.kiway.message.model.MesageStatus;
import cn.kiway.message.model.MessageProvider;
import cn.kiway.message.util.WriteMsgUitl;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.Logger;
import cn.kiway.utils.ViewUtil;

public class MessageServer extends Service {
	public static WebSocketClient client = null;
	MediaPlayer player = new MediaPlayer();
	static App app;
	static long id = -1;
	static MessageModel model;
	static ContentResolver contentResolver;
	static BaseActivity activity;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.log("服务被创建");
		app = (App) getApplication();
		contentResolver = getContentResolver();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Logger.log("服务自身停止");
		if (client != null) {
			client.close();
			client = null;
		}
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Logger.log("开始服务");
		if (client == null || client.getConnection() == null
				|| client.getConnection().isClosed()) {
			try {
				Logger.log("初始化服务");
				ClientMeassage();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void ClientMeassage() throws Exception {
		try {
			if (BaseActivity.baseActivityInsantnce == null)
				return;
			if (BaseActivity.baseActivityInsantnce.app.getCookie() == null)
				return;
			if (BaseActivity.baseActivityInsantnce.app.getCookie().getValue() == null)
				return;
			Map<String, String> map = new HashMap<>();
			map.put("Cookie", "JSESSIONID="
					+ BaseActivity.baseActivityInsantnce.app.getCookie()
							.getValue());
			client = new WebSocketClient(new URI(
					IUrContant.MESSAGECHAT_URL.replace("http", "ws")),
					new Draft_17(), map) {
				@Override
				public void onOpen(ServerHandshake server) {
					Logger.log("-----------------------连接到聊天服务器------------------");
					Logger.log(server);
				}

				@Override
				public void onMessage(String message) {
					Logger.log("---------------------接受到聊天服务器的消息-----------------");
					Logger.log(message);
					try {
						readMessage(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(Exception arg0) {
					Logger.log("-----------------连接聊天服务器出错-----------------");
				}

				@Override
				public void onClose(int arg0, String message, boolean arg2) {
					Logger.log("-------------------断开聊天服务器-----------------");
					Logger.log("错误码：" + arg0);
					Logger.log("关闭后的消息：" + message);
					Logger.log(arg2);
					if (client != null)
						client.close();
					client = null;
					if (model != null && id != -1) {
						Logger.log("插入的值：：：：：：" + model);
						contentResolver.delete(
								Uri.parse(MessageProvider.MESSAGES_URL),
								"_id=?", new String[] { "" + id });
						WriteMsgUitl.writeMsg(contentResolver, model, app,
								app.getUid(),null, MesageStatus.SEND_ERR);
						id = -1;
					}
				}
			};
			if (client != null) {
				client.close();
				client.connect();
				Logger.log("-----建立连接-----"
						+ IUrContant.MESSAGECHAT_URL.replace("http", "ws"));
				Logger.log("____"
						+ BaseActivity.baseActivityInsantnce.app.getCookie()
								.getValue());
			}
			Logger.log("----------------连接中-----------------");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取消息
	 * 
	 * @param message
	 *            消息的内容 ,jsonobject 格式
	 * */
	void readMessage(String message) throws Exception {
		JSONObject msgJsObject = new JSONObject(message);
		MessageModel msg = new MessageModel();
		msg.setMid(System.currentTimeMillis());// 消息的id
		msg.setTime(System.currentTimeMillis());// 消息时间
		msg.setName(msgJsObject.optString("name"));// 发个谁的名字
		msg.setToUid(msgJsObject.optLong("tId"));// 发个谁的id,包裹私信的id,讨论组的id
		msg.setUid(msgJsObject.optLong("userId"));// 谁发的人的id
		msg.setHeadUrl(msgJsObject.optString("photo"));// 谁发的人的头像
		msg.setUserName(msgJsObject.optString("userName"));// 谁发的人的名字
		// 消息的类型
		if (msgJsObject.optString("type").equals("class")) {// 班级群
			msg.setMsgType(3);
		} else if (msgJsObject.optString("type").equals("primessage")) {// 私信
			msg.setMsgType(1);
			if (app.getUid() == msgJsObject.optLong("userId") && model != null) {// 如果id相同则这个消息的自己发的
				msg.setName(model.getName());// 发送人的名字
			} else {
				msg.setName(msgJsObject.optString("userName"));// 发送人的名字
			}
		} else if (msgJsObject.optString("type").equals("discuss")) {// 讨论组
			msg.setMsgType(2);
		}
		// 消息的内容类型
		if (msgJsObject.optString("messageType").equals("homework")) {// 作业
			msg.setMsgContentType(1);
			msg.setMId(msgJsObject.optInt("mId"));// 作业或通知的id
			msg.setContent(msgJsObject.optString("message"));// 作业的内容
		} else if (msgJsObject.optString("messageType").equals("notice")) {// 通知
			msg.setMsgContentType(2);
			msg.setMId(msgJsObject.optInt("mId"));// 作业活通知的id
			msg.setContent(msgJsObject.optString("message"));// 通知的内容
		} else if (msgJsObject.optString("messageType").equals("img")) {// 图片信息
			msg.setContent("图片消息");
			msg.setMsgContentType(3);
			msg.setMsgPic(msgJsObject.optString("message"));// 图片地址
		} else if (msgJsObject.optString("messageType").equals("text")) {// 文本信息
			msg.setContent(msgJsObject.optString("message"));// 文本的内容
			msg.setMsgContentType(4);
		} else if (msgJsObject.optString("messageType").equals("homeworkReply")) {// 提交作业
			msg.setMsgContentType(5);
			msg.setMId(msgJsObject.optInt("mId"));// 作业的id
			msg.setContent("作业消息");
			msg.setMsgPic(msgJsObject.optString("message"));// 作业的内容
		}
		if (id != -1) {
			contentResolver.delete(Uri.parse(MessageProvider.MESSAGES_URL),
					"_id=?", new String[] { "" + id });// 删除发送的消息
			WriteMsgUitl.writeMsg(contentResolver, msg, app, app.getUid(),null,
					MesageStatus.SEND_SUCC);// 更新发送的消息
		} else {
			WriteMsgUitl.writeMsg(getContentResolver(), msg, app, app.getUid(),null,
					MesageStatus.UN_READ);// 接受消息
		}
		if (app.getIntMsgId() != msgJsObject.optLong("tId")
				&& !(boolean) BaseActivity.baseActivityInsantnce.mCache
						.getAsObject(IConstant.MESSAGE_NOTIFY
								+ msgJsObject.optLong("tId"))
				&& msgJsObject.optLong("userId") != app.getUid()) {
			ring();// 消息提示声
		}
		id = -1;
	}

	/**
	 * 收到信息后的声音
	 * */
	MediaPlayer ring() throws Exception, IOException {
		if (player != null) {
			player.reset();
			Uri alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			player.setDataSource(this, alert);
			final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
				player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
				player.prepare();
				player.start();
			}
			return player;
		}
		Uri alert = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		player.setDataSource(this, alert);
		final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
			player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
			player.prepare();
			player.start();
		}
		return player;
	}

	/**
	 * 发送消息
	 * */
	public static void sendMessage(Context context, String ti, MessageModel msg) {
		id = WriteMsgUitl.writeMsg(contentResolver, msg, app, app.getUid(),null,
				MesageStatus.SEND_ING);
		Logger.log("获取的id" + id);
		if (!AppUtil.isNetworkAvailable(context) || client == null
				|| client.getConnection().isClosed()
				|| client.getConnection().isClosing()) {// 判断是否为能发送，不能则更新数据
			contentResolver.delete(Uri.parse(MessageProvider.MESSAGES_URL),
					"_id=?", new String[] { "" + id });
			WriteMsgUitl.writeMsg(contentResolver, msg, app, app.getUid(),null,
					MesageStatus.SEND_ERR);
			if (client != null) {
				client.close();
				client.connect();
			}
			return;
		}
		model = msg;
		activity = (BaseActivity) context;
		try {
			if (client != null && !client.getConnection().isClosed()
					&& !client.getConnection().isClosing())
				client.send(ti + msg.getContent());
			Logger.log("----发送的消息内容：：：" + msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送图片消息
	 * */
	public static void sendPic(Context context, String title, MessageModel msg)
			throws MalformedURLException, IOException {
		id = WriteMsgUitl.writeMsg(context.getContentResolver(), msg, app,
				app.getUid(),null, MesageStatus.SEND_ING);
		Logger.log("获取的id" + id);
		if (!AppUtil.isNetworkAvailable(context) || client == null
				|| client.getConnection().isClosed()
				|| client.getConnection().isClosing()) {
			contentResolver.delete(Uri.parse(MessageProvider.MESSAGES_URL),
					"_id=?", new String[] { "" + id });
			WriteMsgUitl.writeMsg(contentResolver, msg, app, app.getUid(),null,
					MesageStatus.SEND_ERR);
			if (client != null) {
				client.close();
				client.connect();
			}
			return;
		}
		model = msg;
		activity = (BaseActivity) context;// 拼接字节
		byte[] pic = ViewUtil.BitmapToByte(ViewUtil.getSmallBitmap(msg
				.getMsgPic()));
		byte[] data = new byte[title.getBytes().length + pic.length];
		Logger.log(data.length - title.getBytes().length);
		System.arraycopy(title.getBytes(), 0, data, 0, title.getBytes().length);
		Logger.log(title.getBytes().length - pic.length);
		System.arraycopy(pic, 0, data, title.getBytes().length, data.length
				- (data.length - pic.length));
		try {
			if (client != null && !client.getConnection().isClosed()
					&& !client.getConnection().isClosing())
				client.send(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Logger.log("图像地址：：：：：" + msg.getMsgPic().toString());
		Logger.log("----发送的消息内容：：" + data);
		Logger.log("----发送的图片内容长度：：" + data.length);
	}
}
