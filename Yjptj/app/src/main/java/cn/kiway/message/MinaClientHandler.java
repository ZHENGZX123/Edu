package cn.kiway.message;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import cn.kiway.App;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.MainActivity;
import cn.kiway.message.model.MesageStatus;
import cn.kiway.message.model.MessageProvider;
import cn.kiway.message.util.WriteMsgUitl;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.Logger;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class MinaClientHandler extends IoHandlerAdapter {
	static App app;
	MediaPlayer player = new MediaPlayer();
	private static final String HEARTBEATREQUEST = "Sky King cover ground tiger!";
	private static final String HEARTBEATRESPONSE = "Precious tower shock river monster!";
	private static final String LOG_IN_OTHER_PLACE = "Account different ground log in!";
	private static final String LOG_IN_SUCCESS = "Login success!";
	public static final String ATTEND_CLASS_BEAT = "Attend class beat!";
	static long id;// 发送消息时候获取的数据库id

	// 当客户端连接进入时
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		Logger.log("incomming 客户端: " + session.getRemoteAddress());
		app.setIoSession(session);
		session.write("userName="
				+ SharedPreferencesUtil.getString(App.getInstance()
						.getApplicationContext(), IConstant.USER_NAME)
				+ "&password="
				+ SharedPreferencesUtil.getString(App.getInstance()
						.getApplicationContext(), IConstant.PASSWORD)
				+ "&type=2");// 创建聊天成功的时候,发送用户名和密码到后台登录聊天,必须是消息的第一条
		Logger.log("发送的数据"
				+ SharedPreferencesUtil.getString(App.getInstance()
						.getApplicationContext(), IConstant.USER_NAME)
				+ "&password="
				+ SharedPreferencesUtil.getString(App.getInstance()
						.getApplicationContext(), IConstant.PASSWORD)
				+ "&type=1");
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		Logger.log("客户端发送信息异常....");
		Logger.log(cause);
	}

	// 当客户端发送消息到达时
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		if ((app != null && session.getId() != app.getIoSession().getId() && app
				.getIoSession() != null) || app == null) {
			session.closeNow();
			Logger.log("关闭了之前的session:::::");
			return;
		}
		Logger.log("自身的Session:::" + app.getIoSession().getId());
		Logger.log("服务的Session:::::" + session.getId());
		Logger.log("服务器返回的数据：" + message.toString());
		if (message.equals(HEARTBEATREQUEST)) {
			Logger.log("发送心跳：：：：：");
			session.write(HEARTBEATRESPONSE);
		} else if (message.equals(HEARTBEATRESPONSE)) {
			Logger.log("发送心跳成功：：：：：");
		} else if (message.equals(LOG_IN_OTHER_PLACE)) {
			Logger.log("账号异常登录：：：：：");
			handler.sendEmptyMessage(0);
		} else if (message.equals(LOG_IN_SUCCESS)) {
			Logger.log("登录聊天成功：：：：：");
		} else if (((String) message).indexOf(ATTEND_CLASS_BEAT) > -1) {
			Logger.log("有老师上课：：：：");
			SharedPreferencesUtil.save(app.getApplicationContext(),
					message.toString(), true);
			if (app.getBoyModels().get(app.getPosition()).getClassId() == StringUtil
					.toInt(((String) message).split("#")[1])) {
				handler.sendEmptyMessage(2);
			}
		} else {
			readMessage((String) message);
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		Logger.log("客户端与服务端断开连接.....");
		if (app != null)
			app.setIoSession(null);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out
				.println("one Client Connection" + session.getRemoteAddress());
	}

	/**
	 * 连接聊天服务
	 * */
	public static void openMessage(App app) throws Throwable {
		MinaClientHandler.app = app;
		if ((app.getIoSession() != null && app.getIoSession().isActive())
				|| app == null || app.getUid() <= 0)
			return;
		if (app.connector.isDisposed() || app.connector.isDisposing())
			handler.sendEmptyMessage(1);
		ConnectFuture cf;
		if (app.connector.getManagedSessionCount() > 0)
			app.connector.getManagedSessions().clear();
		InetSocketAddress inetSocketAddress = new InetSocketAddress(
				IUrContant.CHAT_URL, 9999);
		for (;;) {
			try {
				cf = app.connector.connect(inetSocketAddress);
				cf.awaitUninterruptibly();
				break;
			} catch (RuntimeIoException e) {
				e.printStackTrace();
				app.setIsConnect(false);
				Thread.sleep(5000);
			}
		}
		app.setIsConnect(true);
		cf.getSession().getCloseFuture().awaitUninterruptibly();
		app.connector.dispose();
	}

	/**
	 * 发送消息
	 * 
	 * @param msg
	 *            消息模型
	 * @param title
	 *            消息的类型， 1 文字 2图片
	 * @param contentResolver
	 *            消息监听
	 * */
	@SuppressWarnings("rawtypes")
	public static void sendMessage(final MessageModel msg, String title,
			ContentResolver contentResolver) {
		id = WriteMsgUitl.writeMsg(contentResolver, msg, app, app.getUid(),
				null, MesageStatus.SEND_ING);
		if (app == null)
			return;
		if (app.getIoSession() == null || app.getIoSession().isClosing()) {
			IConstant.executorService.execute(new Runnable() {
				public void run() {
					try {
						MinaClientHandler.openMessage(app);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			});
			return;
		}
		WriteFuture writeFuture = null;
		if (msg.getMsgContentType() == 3) {
			byte[] pic = ViewUtil.BitmapToByte(ViewUtil.getSmallBitmap(msg
					.getMsgPic()));
			byte[] data = new byte[title.getBytes().length + pic.length];
			Logger.log(data.length - title.getBytes().length);
			System.arraycopy(title.getBytes(), 0, data, 0,
					title.getBytes().length);
			Logger.log(title.getBytes().length - pic.length);
			System.arraycopy(pic, 0, data, title.getBytes().length, data.length
					- (data.length - pic.length));
			writeFuture = app.getIoSession().write(data);
		} else if (msg.getMsgContentType() == 4) {
			writeFuture = app.getIoSession().write(title + msg.getContent());
		}
		writeFuture.addListener(new IoFutureListener() {
			@Override
			public void operationComplete(IoFuture future) {
				App.getInstance()
						.getApplicationContext()
						.getContentResolver()
						.delete(Uri.parse(MessageProvider.MESSAGES_URL),
								"_id=?", new String[] { "" + id });// 删除发送的消息
				WriteFuture writeFuture = (WriteFuture) future;
				if (writeFuture.isWritten()) {// 写入成功
					if (msg.getMsgContentType() != 3)
						WriteMsgUitl.writeMsg(App.getInstance()
								.getApplicationContext().getContentResolver(),
								msg, app, app.getUid(), null,
								MesageStatus.SEND_SUCC);// 更新发送的消息
					else
						WriteMsgUitl.writeMsg(App.getInstance()
								.getApplicationContext().getContentResolver(),
								msg, app, app.getUid(),
								ViewUtil.BitmapToByte(ViewUtil
										.getSmallBitmap(msg.getMsgPic())),
								MesageStatus.SEND_SUCC);// 更新发送的消息
					Logger.log("发送成功：：：：：");
					return;
				} else {// 写入失败
					WriteMsgUitl.writeMsg(App.getInstance()
							.getApplicationContext().getContentResolver(), msg,
							app, app.getUid(), null, MesageStatus.SEND_ERR);// 更新发送的消息
					Logger.log("发送失败：：：：：");
				}
			}
		});
	}

	/**
	 * 收到信息后的声音
	 * */
	MediaPlayer ring() throws Exception, IOException {
		if (player != null) {
			player.reset();
			Uri alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			player.setDataSource(App.getInstance().getApplicationContext(),
					alert);
			final AudioManager audioManager = (AudioManager) App.getInstance()
					.getApplicationContext()
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
				player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
				player.prepare();
				player.start();
			}
			return player;
		}
		Uri alert = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		player.setDataSource(App.getInstance().getApplicationContext(), alert);
		final AudioManager audioManager = (AudioManager) App.getInstance()
				.getApplicationContext()
				.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
			player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
			player.prepare();
			player.start();
		}
		return player;
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
		if (msgJsObject.optLong("userId") == app.getUid()
				&& !msgJsObject.optString("messageType")
						.equals("homeworkReply"))// 如果是自己发的消息则不再写入
			return;
		msg.setMid(System.currentTimeMillis());// 消息的id ,暂时不用用到，用时间戳代替
		msg.setTime(System.currentTimeMillis());// 发送消息的时间
		msg.setName(msgJsObject.optString("name"));// 消息的名字
		msg.setToUid(msgJsObject.optLong("tId"));// 发个谁的id 包裹 班级id 讨论组的id 私信的id
		msg.setUid(msgJsObject.optLong("userId"));// 消息发送人的id
		msg.setHeadUrl(msgJsObject.optString("photo"));// 消息发送人的头像
		msg.setUserName(msgJsObject.optString("userName"));// 消息发送人的名字
		if (msgJsObject.optString("type").equals("class")) {// 班级群
			msg.setMsgType(3);
		} else if (msgJsObject.optString("type").equals("primessage")) {// 私信
			msg.setMsgType(1);
			msg.setName(msgJsObject.optString("userName"));
		} else if (msgJsObject.optString("type").equals("discuss")) {// 讨论组
			msg.setMsgType(2);
		}
		if (msgJsObject.optString("messageType").equals("homework")) {// 作业
			msg.setMsgContentType(1);
			msg.setMId(msgJsObject.optInt("mId"));
			msg.setContent(msgJsObject.optString("message"));
		} else if (msgJsObject.optString("messageType").equals("notice")) {// 通知
			msg.setMsgContentType(2);
			msg.setMId(msgJsObject.optInt("mId"));
			msg.setContent(msgJsObject.optString("message"));
		} else if (msgJsObject.optString("messageType").equals("img")) {// 图片信息
			msg.setContent("图片消息");
			msg.setMsgContentType(3);
			msg.setMsgPic(msgJsObject.optString("message"));
		} else if (msgJsObject.optString("messageType").equals("text")) {// 文本信息
			msg.setContent(msgJsObject.optString("message"));
			msg.setMsgContentType(4);
		} else if (msgJsObject.optString("messageType").equals("homeworkReply")) {// 提交作业
			msg.setMsgContentType(5);
			msg.setContent("作业消息");
			msg.setMId(msgJsObject.optInt("mId"));
			msg.setMsgPic(msgJsObject.optString("message"));
		}
		WriteMsgUitl.writeMsg(App.getInstance().getApplicationContext()
				.getContentResolver(), msg, app, app.getUid(), null,
				MesageStatus.UN_READ);// 接受消息
		if (app.getIntMsgId() != msgJsObject.optLong("tId")
				&& !(boolean) BaseActivity.baseActivityInsantnce.mCache
						.getAsObject(IConstant.MESSAGE_NOTIFY
								+ msgJsObject.optLong("tId"))
				&& msgJsObject.optLong("userId") != app.getUid()) {
			ring();// 消息提示声
		}
	}

	@SuppressLint("HandlerLeak")
	static Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0)
				BaseActivity.baseActivityInsantnce.showDialog();
			if (msg.what == 1)
				app.getNioSocketConnector();
			if (msg.what == 2) {
				if (MainActivity.studioText != null)
					MainActivity.studioText.setVisibility(View.VISIBLE);
			}
		};
	};
}
