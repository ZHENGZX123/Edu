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
import cn.kiway.message.model.SessionProvider;
import cn.kiway.message.model.VideoProvider;
import cn.kiway.model.MessageModel;
import cn.kiway.model.VideoModel;
import cn.kiway.utils.Logger;

public class WriteMsgUitl {

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
		values.put("_uid", msg.getUid());// 发送的id
		values.put("_msgid", msg.getMid());// 消息的id
		values.put("_name", msg.getName());// 发给谁的名字
		values.put("_content", msg.getContent());// 发送的内容
		values.put("_time", msg.getTime());// 发送的时间
		values.put("_url", msg.getHeadUrl());// 发送人的头像
		values.put("_touid", msg.getToUid());// 发送给谁的id
		values.put("_msgtype", msg.getMsgType());// 消息的类型
		values.put("_msgctype", msg.getMsgContentType());// 消息内容的类型
		values.put("_msgpic", msg.getMsgPic());// 图片消息的地址
		values.put("_statue", status);// 发送消息的状态
		values.put("_userid", msg.getUid());// 发送人的id
		values.put("_username", msg.getUserName());// 发送人的，名字
		values.put("_mid", msg.getMId());// 作业或者通知的id
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
			if (msg.getMsgContentType() == 1)
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
	 * 写入视频数据
	 * */
	public synchronized static long WriteVideo(ContentResolver contentResolver,
			List<VideoModel> msgs, int year, int month, int day) {
		List<ContentValues> contentValues = new ArrayList<ContentValues>();
		for (int i = 0; i < msgs.size(); i++) {
			VideoModel msg = msgs.get(i);
			Cursor csr = contentResolver.query(
					Uri.parse(VideoProvider.VIDEOS_URL), null, "_vid=?",
					new String[] { "" + msg.getId() }, null);
			int cut = csr.getCount();
			csr.close();
			ContentValues values = new ContentValues();
			values.put("_vid", msg.getId());
			values.put("_dirId", msg.getDirId());
			values.put("_type", msg.getType());
			values.put("_name", msg.getName());
			values.put("_requiretime", msg.getRequireTime());
			values.put("_preview", msg.getPreview());
			values.put("_seqno", msg.getSeqNo());
			values.put("_typename", msg.getTypeName());
			values.put("_year", year);
			values.put("_month", month);
			values.put("_day", day);
			values.put("_gradeid", msg.getGrader());
			values.put("_isuser", msg.getIsUser());
			if (cut <= 0) {
				contentValues.add(values);
			} else {
				contentResolver.update(Uri.parse(VideoProvider.VIDEOS_URL),
						values, "_vid=?", new String[] { "" + msg.getId() });
			}
		}
		ContentValues[] contentValues2 = new ContentValues[contentValues.size()];
		return contentResolver.bulkInsert(Uri.parse(VideoProvider.VIDEOS_URL),
				contentValues.toArray(contentValues2));
	}

	/**
	 * 写入课程数据
	 * */
	public synchronized static long WriteSesson(
			ContentResolver contentResolver, VideoModel video, String list,
			String content, String videoIcon) {
		List<ContentValues> contentValues = new ArrayList<ContentValues>();
		Cursor csr = contentResolver.query(
				Uri.parse(SessionProvider.SESSONS_URL), null, "_lessionid=?",
				new String[] { "" + video.getId() }, null);
		int cut = csr.getCount();
		csr.close();
		ContentValues values = new ContentValues();
		values.put("_lessionid", video.getId());// 课程id
		values.put("_sessiongold", video.getTeachingAim());// 课程目标
		values.put("_sessionreally", video.getTeachingPreare());// 课程准备
		values.put("_content", content);// 课程内容
		values.put("_vid", list);// 视频id（list转为string存 ,用逗号隔开）
		values.put("_sessiontitle", video.getDirName());
		values.put("_vicon", videoIcon);
		if (cut <= 0) {
			contentValues.add(values);
		} else {
			contentResolver
					.update(Uri.parse(SessionProvider.SESSONS_URL), values,
							"_lessionid=?", new String[] { "" + video.getId() });
		}
		ContentValues[] contentValues2 = new ContentValues[contentValues.size()];
		return contentResolver.bulkInsert(
				Uri.parse(SessionProvider.SESSONS_URL),
				contentValues.toArray(contentValues2));
	}

	public synchronized static void WriteClassData(Context context, App app,
			String className, int classId) {
		if (context == null || app == null)
			return;
		Cursor cursor = context.getContentResolver().query(
				Uri.parse(MessageChatProvider.MESSAGECHATS_URL), null,
				" _touid=? ", new String[] { classId + "" }, null);
		if (cursor.getCount() > 0)
			return;
		MessageModel mId = new MessageModel();// 创建班级成功后添加聊天班级的数据
		mId.setName(className);
		mId.setTime(System.currentTimeMillis());
		mId.setToUid(classId);
		mId.setMid(System.currentTimeMillis());
		mId.setContent("欢迎来到" + className);
		mId.setMsgType(3);
		mId.setMsgContentType(4);
		mId.setUid(classId);
		mId.setUserName(className);
		WriteMsgUitl.writeMsg(context.getContentResolver(), mId, app,
				app.getUid(), null, MesageStatus.READ);
	}
}
