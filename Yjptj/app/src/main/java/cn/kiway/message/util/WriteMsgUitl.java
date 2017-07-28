package cn.kiway.message.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import cn.kiway.App;
import cn.kiway.message.model.MesageStatus;
import cn.kiway.message.model.MessageChatProvider;
import cn.kiway.message.model.MessageProvider;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.Logger;

public class WriteMsgUitl {
	/**
	 * 写入多重消息
	 * */
	public synchronized static long WriteMsg(ContentResolver contentResolver,
			List<MessageModel> msgs, App app, long myId, int status) {

		List<ContentValues> contentValues = new ArrayList<ContentValues>();
		for (int i = 0; i < msgs.size(); i++) {
			MessageModel msg = msgs.get(i);
			Cursor csr = contentResolver.query(
					Uri.parse(MessageProvider.MESSAGES_URL), null, "_msgid=?",
					new String[] { "" + msg.getMid() }, null);
			int cut = csr.getCount();
			csr.close();
			if (cut <= 0) {
				ContentValues values = new ContentValues();
				values.put("_uid", msg.getUid());
				values.put("_msgid", msg.getMid());
				values.put("_name", msg.getName());
				values.put("_content", msg.getContent());
				values.put("_time", msg.getTime());
				values.put("_url", msg.getHeadUrl());
				contentValues.add(values);
				long uid = msg.getUid();
				long toUid = msg.getToUid();
				Cursor cursor = contentResolver
						.query(Uri.parse(MessageChatProvider.MESSAGECHATS_URL),
								null,
								"(_uid=? and _touid=? and _userid=?) or (_uid=? and _touid=? and _userid=?)",
								new String[] { "" + uid, "" + toUid, "" + myId,
										"" + toUid, "" + uid, "" + myId }, null);
				long chatId = -1;
				long unread = 0;
				while (cursor.moveToNext()) {
					chatId = cursor.getLong(0);
					unread = cursor.getLong(7);// 未读消息数
				}
				cursor.close();
				if (chatId != -1) {
					contentResolver.delete(
							Uri.parse(MessageChatProvider.MESSAGECHATS_URL),
							"_id=?", new String[] { "" + chatId });
				}
				unread++;
				ContentValues values1 = new ContentValues();
				values1.put("_uid", msg.getUid());
				values1.put("_msgid", msg.getMid());
				values1.put("_name", msg.getName());
				values1.put("_content", msg.getContent());
				values1.put("_time", msg.getTime());
				values1.put("_url", msg.getHeadUrl());
				values1.put("_unread", unread);
				contentResolver.insert(
						Uri.parse(MessageChatProvider.MESSAGECHATS_URL),
						values1);
			}
		}
		ContentValues[] contentValues2 = new ContentValues[contentValues.size()];
		return contentResolver.bulkInsert(
				Uri.parse(MessageProvider.MESSAGES_URL),
				contentValues.toArray(contentValues2));
	}

	/**
	 * 写入单条消息
	 * */
	public synchronized static long writeMsg(ContentResolver contentResolver,
			MessageModel msg, App app, long mId, byte[] bs, int status) {
		Cursor csr = contentResolver.query(
				Uri.parse(MessageProvider.MESSAGES_URL), null, "_msgid=?",
				new String[] { "" + msg.getMid() }, null);
		int cut = csr.getCount();
		csr.close();
		if (cut > 0)
			return -1;
		ContentValues values = new ContentValues();
		values.put("_uid", msg.getUid());
		values.put("_msgid", msg.getMid());
		values.put("_name", msg.getName());
		values.put("_content", msg.getContent());
		values.put("_time", msg.getTime());
		values.put("_url", msg.getHeadUrl());
		values.put("_touid", msg.getToUid());
		values.put("_msgtype", msg.getMsgType());
		values.put("_msgctype", msg.getMsgContentType());
		values.put("_msgpic", msg.getMsgPic());
		values.put("_statue", status);
		values.put("_userid", msg.getUid());
		values.put("_username", msg.getUserName());
		values.put("_mid", msg.getMId());
		if (bs != null) {
			values.put("_imge", bs);// 作业或者通知的id
		}
		Uri uri = contentResolver.insert(
				Uri.parse(MessageProvider.MESSAGES_URL), values);
		Logger.log("--------------------插入的值--------------------");
		Logger.log("-------------------" + values + "---------------");
		long toUid = msg.getToUid();
		long msgtype = msg.getMsgType();
		Cursor cursor = null;
		if (msg.getMsgType() == 1)
			cursor = contentResolver
					.query(Uri.parse(MessageChatProvider.MESSAGECHATS_URL),
							null,
							"((_touid=? and _uid=?) or( _touid=? and _uid=?)) and _msgtype=?",
							new String[] { "" + toUid, msg.getUid() + "",
									msg.getUid() + "", "" + toUid, "" + msgtype },
							null);
		else
			cursor = contentResolver.query(
					Uri.parse(MessageChatProvider.MESSAGECHATS_URL), null,
					"(_touid=? or _uid=?) and _msgtype=?", new String[] {
							"" + toUid, "" + toUid + "", "" + msgtype }, null);
		Logger.log("是否查找到值：：：：：：：" + cursor.getCount());
		long chatId = -1;
		long unread = 0;
		long unhome = 0;
		long unnotify = 0;
		while (cursor.moveToNext()) {
			chatId = cursor.getLong(0);
			unread = cursor.getLong(7);// 未读消息数
			unhome = cursor.getLong(11);// 未读作业数
			unnotify = cursor.getLong(12);// 未读通知数
		}
		cursor.close();
		if (status == MesageStatus.READ || (status == MesageStatus.SEND_ERR)
				|| (status == MesageStatus.SEND_ING)
				|| (status == MesageStatus.SEND_SUCC)
				|| app.getIntMsgId() == msg.getToUid()) {// 表示当前正与他聊天或者我发送的消息
			unread = 0;
		} else {
			unread++;
			if (msg.getMsgContentType() == 1 || msg.getMsgContentType() == 5)
				unhome++;
			if (msg.getMsgContentType() == 2)
				unnotify++;
		}
		values = new ContentValues();
		values.put("_uid", msg.getUid());
		values.put("_msgid", msg.getMid());
		values.put("_name", msg.getName());
		if (msg.getMsgType() != 1) {
			values.put("_content", msg.getUserName() + ":" + msg.getContent());
		} else {
			values.put("_content", msg.getContent());
		}
		values.put("_time", msg.getTime());
		values.put("_url", msg.getHeadUrl());
		values.put("_touid", msg.getToUid());
		values.put("_unread", unread);
		values.put("_msghome", unhome);
		values.put("_msnotify", unnotify);
		values.put("_msgtype", msg.getMsgType());
		values.put("_msgctype", msg.getMsgContentType());
		values.put("_statue", status);
		values.put("_userid", msg.getUid());
		if (chatId != -1) {
			contentResolver.update(
					Uri.parse(MessageChatProvider.MESSAGECHATS_URL), values,
					"_id=?", new String[] { "" + chatId });
			Logger.log("更新数据聊天列表数据：：：" + values);
		} else {
			contentResolver.insert(
					Uri.parse(MessageChatProvider.MESSAGECHATS_URL), values);// 插入消息列表表
			Logger.log("插入数据聊天列表数据：：：" + values);
		}
		return ContentUris.parseId(uri);
	}

	/**
	 * 创建班级圈和讨论组数据
	 * 
	 * @param msgType
	 *            1 私信 2家长讨论 3 班级
	 * @param className
	 *            班级名字
	 * @param classId
	 *            班级id
	 * */
	public synchronized static void WriteClassData(Context context, App app,
			String className, int classId, int msgType) {
		if (context == null || app == null)
			return;
		Cursor cursor = context.getContentResolver().query(
				Uri.parse(MessageChatProvider.MESSAGECHATS_URL), null,
				" _touid=? and _msgtype=? ",
				new String[] { classId + "", msgType + "" }, null);
		if (cursor.getCount() > 0)
			return;
		MessageModel mId = new MessageModel();// 创建班级成功后添加聊天班级的数据
		mId.setName(className);
		mId.setUserName(className);
		mId.setTime(System.currentTimeMillis());
		mId.setUid(classId);
		mId.setToUid(classId);
		mId.setMid(System.currentTimeMillis());
		if (msgType == 2) {
			mId.setContent("讨论组创建成功");
		} else {
			mId.setContent("欢迎来到" + className);
		}
		mId.setMsgType(msgType);
		mId.setMsgContentType(4);
		WriteMsgUitl.writeMsg(context.getContentResolver(), mId, app,
				app.getUid(), null, MesageStatus.READ);
	}
}
