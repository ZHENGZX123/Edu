package cn.kiway.adapter.main.message;

import java.io.IOException;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.main.message.ChatGroupMessageListActivity;
import cn.kiway.activity.main.message.PrivateMessageActivity;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.dialog.IsNetWorkDialog.IsNetWorkCallBack;
import cn.kiway.message.model.MessageChatProvider;
import cn.kiway.message.model.MessageProvider;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class ChatListAdapter extends ArrayAdapter<MessageModel> implements
		OnClickListener, OnLongClickListener, IsNetWorkCallBack {
	BaseActivity activity;
	ChatListHolder holder;
	public List<MessageModel> messagelist;
	String[] facesKey, faces;
	int h;
	IsNetWorkDialog dialog;

	public ChatListAdapter(Context context, List<MessageModel> messagelist) {
		super(context, -1);
		this.activity = (BaseActivity) context;
		this.messagelist = messagelist;
		h = (int) activity.resources.getDimension(R.dimen._20dp);
		facesKey = context.getResources().getStringArray(R.array.faces_key);
		try {
			faces = context.getAssets().list("faces");
		} catch (IOException e) {
			e.printStackTrace();
		}
		dialog = new IsNetWorkDialog(activity, this, "删除该消息?",
				activity.resources.getString(R.string.sure));
	}

	@Override
	public int getCount() {
		return messagelist.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity, R.layout.chat_list_list_item);
			holder = new ChatListHolder();
			holder.name = ViewUtil.findViewById(view, R.id.chat_name);
			holder.content = ViewUtil.findViewById(view, R.id.content);
			holder.time = ViewUtil.findViewById(view, R.id.chat_time);
			holder.homeNumber = ViewUtil.findViewById(view,
					R.id.homework_number);
			holder.notifyNumber = ViewUtil.findViewById(view,
					R.id.notify_number);
			holder.messageNumber = ViewUtil.findViewById(view,
					R.id.message_number);
			view.setTag(holder);
		} else {
			holder = (ChatListHolder) view.getTag();
		}
		MessageModel model = messagelist.get(position);
		ViewUtil.setContent(holder.name, model.getName());
		ViewUtil.setContent(holder.time,
				StringUtil.getStandardDate(model.getMid()));
		Spanned spanned = Html.fromHtml(
				AppUtil.strToHtml(model.getContent(), facesKey),
				new ImageGetter() {
					@Override
					public Drawable getDrawable(String source) {
						try {
							Drawable drawable = AppUtil.loadFaceResourse(
									activity.getAssets(), activity, source,
									facesKey, faces);
							drawable.setBounds(0, 0, h, h);
							return drawable;
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}
				}, null);
		if (model.getMsgType() == 1) {
			view.findViewById(R.id.homework).setVisibility(View.GONE);
			view.findViewById(R.id.notify).setVisibility(View.GONE);
		} else {
			if (model.getHomerWorkNumber() <= 0) {
				view.findViewById(R.id.homework).setVisibility(View.GONE);
			} else {
				view.findViewById(R.id.homework).setVisibility(View.VISIBLE);
				if (model.getHomerWorkNumber() >= 99) {
					ViewUtil.setContent(holder.homeNumber, "99");
				} else {
					ViewUtil.setContent(holder.homeNumber,
							model.getHomerWorkNumber() + "");
				}
			}
			if (model.getNotifyNumber() <= 0) {
				view.findViewById(R.id.notify).setVisibility(View.GONE);
			} else {
				view.findViewById(R.id.notify).setVisibility(View.VISIBLE);
				if (model.getNotifyNumber() >= 99) {
					ViewUtil.setContent(holder.notifyNumber, "99");
				} else {
					ViewUtil.setContent(holder.notifyNumber,
							model.getNotifyNumber() + "");
				}
			}
		}
		if (model.getMessageNumber() <= 0) {
			view.findViewById(R.id.message_number).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.message_number).setVisibility(View.VISIBLE);
			if (model.getMessageNumber() >= 99) {
				ViewUtil.setContent(holder.messageNumber, "99");
			} else {
				ViewUtil.setContent(holder.messageNumber,
						model.getMessageNumber() + "");
			}
		}
		ViewUtil.setContent(holder.content, spanned);
		view.setTag(R.id.bundle_params, position);
		view.setOnClickListener(this);
		view.setOnLongClickListener(this);
		return view;
	}

	static class ChatListHolder {
		/**
		 * 私信名字
		 * */
		TextView name;
		/**
		 * 私信时间
		 * */
		TextView time;
		/**
		 * 私信内容
		 * */
		TextView content;
		/**
		 * 消息数，作业数，通知数
		 * */
		TextView messageNumber, homeNumber, notifyNumber;
	}

	@Override
	public void onClick(View v) {
		int position = StringUtil
				.toInt(v.getTag(R.id.bundle_params).toString());
		final MessageModel model = messagelist.get(position);
		Bundle bundle = new Bundle();
		bundle.putSerializable(IConstant.BUNDLE_PARAMS,
				messagelist.get(position));
		if (model.getMsgType() == 1) {// 私聊界面
			activity.startActivity(PrivateMessageActivity.class, bundle);
		} else {// 群聊界面
			activity.startActivity(ChatGroupMessageListActivity.class, bundle);
		}
		new Handler().postDelayed(new Runnable() {// 未读消息清零
					@Override
					public void run() {
						ContentValues values = new ContentValues();
						values.put("_unread", 0);
						values.put("_msghome", 0);
						values.put("_msnotify", 0);
						model.setMessageNumber(0);
						activity.getContentResolver()
								.update(Uri
										.parse(MessageChatProvider.MESSAGECHATS_URL),
										values, "_touid=?",
										new String[] { "" + model.getToUid() });
					}
				}, 500);
	}

	int position = -1;

	@Override
	public boolean onLongClick(View v) {
		position = StringUtil.toInt(v.getTag(R.id.bundle_params).toString());
		if (dialog != null && !dialog.isShowing()) {
			dialog.show();
		}
		return false;
	}

	@Override
	public void isNetWorkCallBack() throws Exception {
		if (position == -1)
			return;// 删除消息
		MessageModel model = messagelist.get(position);
		activity.getContentResolver().delete(
				Uri.parse(MessageProvider.MESSAGES_URL),
				"(_touid=? or _touid=?) and  _msgtype=? ",
				new String[] { "" + activity.app.getUid(),
						"" + model.getToUid(), model.getMsgType() + "" });
		activity.getContentResolver().delete(
				Uri.parse(MessageChatProvider.MESSAGECHATS_URL), "_touid=?",
				new String[] { "" + model.getToUid() });
		messagelist.remove(position);
		notifyDataSetChanged();
	}

	@Override
	public void cancel() throws Exception {

	}
}
