package cn.kiway.adapter.message;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.common.ViewPhotosActivity;
import cn.kiway.activity.message.ChatGroupMessageListActivity;
import cn.kiway.message.MinaClientHandler;
import cn.kiway.message.model.MesageStatus;
import cn.kiway.message.model.MessageProvider;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class MessageAdapter extends ArrayAdapter<MessageModel> implements
		OnClickListener {
	MessageHolder holder;
	BaseActivity activity;
	public List<MessageModel> listMessage;
	String[] facesKey, faces;
	int h;
	public static byte[] byteData;
	public MessageAdapter(Context context, List<MessageModel> listMessage) {
		super(context, -1);
		this.activity = (BaseActivity) context;
		this.listMessage = listMessage;
		h = (int) activity.resources.getDimension(R.dimen._20dp);
		facesKey = context.getResources().getStringArray(R.array.faces_key);
		try {
			faces = context.getAssets().list("faces");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getCount() {
		return listMessage.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity, R.layout.message_list_item);
			holder = new MessageHolder();
			holder.layout_left = ViewUtil.findViewById(view, R.id.left);
			holder.layout_right = ViewUtil.findViewById(view, R.id.right);
			holder.time = ViewUtil.findViewById(view, R.id.time);
			holder.left_content = ViewUtil
					.findViewById(view, R.id.left_content);
			holder.right_pic = ViewUtil.findViewById(view, R.id.right_pic);
			holder.left_pic = ViewUtil.findViewById(view, R.id.left_pic);
			holder.right_content = ViewUtil.findViewById(view,
					R.id.right_content);
			holder.sendStatus = ViewUtil.findViewById(view, R.id.right_status);
			holder.left_user_pic = ViewUtil.findViewById(view,
					R.id.left_user_pic);
			holder.right_user_pic = ViewUtil.findViewById(view,
					R.id.right_user_pic);
			holder.left_user_name = ViewUtil.findViewById(view,
					R.id.left_user_name);
			holder.sendIng = ViewUtil.findViewById(view, R.id.sendIng);
			view.setTag(holder);
		} else {
			holder = (MessageHolder) view.getTag();
		}
		MessageModel model = listMessage.get(position);
		if (model.getUid() == activity.app.getUid()) {
			if (model.getStatu() == MesageStatus.SEND_ERR) {// 设置消息的发送状态
				holder.sendStatus.setVisibility(View.VISIBLE);// 发送失败
				holder.sendStatus.setTag(R.id.bundle_params, position);
				holder.sendStatus.setOnClickListener(this);
			} else {
				holder.sendStatus.setVisibility(View.GONE);
			}
			if (model.getStatu() == MesageStatus.SEND_ING) {// 正在发送
				if (Math.ceil(System.currentTimeMillis() / 60 / 1000.0f)
						- Math.ceil(model.getTime() / 60 / 1000.0f) > 1) {// 当正在发送的状态距这条消息发送的时间大于一分钟则变为发送失败
					holder.sendStatus.setVisibility(View.VISIBLE);
					holder.sendIng.setVisibility(View.GONE);

				} else {
					holder.sendIng.setVisibility(View.VISIBLE);
					holder.sendStatus.setVisibility(View.GONE);
				}

			} else {
				holder.sendIng.setVisibility(View.GONE);
			}
			holder.layout_left.setVisibility(View.GONE);
			holder.layout_right.setVisibility(View.VISIBLE);
			if (model.getMsgContentType() == 4) {// 文字消息
				holder.right_content.setVisibility(View.VISIBLE);
				holder.right_content.setGravity(Gravity.LEFT);
				holder.right_pic.setVisibility(View.GONE);
				Spanned spanned = Html.fromHtml(
						// 解析表情符号
						AppUtil.strToHtml(model.getContent(), facesKey),
						new ImageGetter() {
							@Override
							public Drawable getDrawable(String source) {
								try {
									Drawable drawable = AppUtil
											.loadFaceResourse(
													activity.getAssets(),
													activity, source, facesKey,
													faces);
									drawable.setBounds(0, 0, h, h);
									return drawable;
								} catch (Exception e) {
									e.printStackTrace();
								}
								return null;
							}
						}, null);
				ViewUtil.setContent(holder.right_content, spanned);
				ViewUtil.setArroundDrawable(holder.right_content, -1, -1, -1,
						-1);
			} else if (model.getMsgContentType() == 3) {// 图片消息
				holder.right_content.setVisibility(View.GONE);
				holder.right_pic.setVisibility(View.VISIBLE);
				ViewUtil.setArroundDrawable(holder.right_content, -1, -1, -1,
						-1);
				if (model.getStatu() == MesageStatus.SEND_ING
						|| model.getStatu() == MesageStatus.SEND_ERR) {// 如果正在发生则路径为本地路径
					activity.imageLoader.displayImage(
							"file://" + model.getMsgPic(), holder.right_pic,
							activity.options);
				} else {
					if ((model.getImgBtye() != null)) {
						Bitmap btp = BitmapFactory
								.decodeStream(new ByteArrayInputStream(model
										.getImgBtye()));
						holder.right_pic.setImageBitmap(btp);
					}
				}
				holder.right_pic.setTag(R.id.bundle_params, position);
				holder.right_pic.setOnClickListener(this);
			} else if (model.getMsgContentType() == 1) {// 作业
				holder.right_pic.setVisibility(View.GONE);
				holder.right_content.setVisibility(View.VISIBLE);
				view.setTag(R.id.bundle_params, position);
				view.setOnClickListener(this);
				ViewUtil.setArroundDrawable(holder.right_content, -1,
						R.drawable.homework_se, -1, -1);
				ViewUtil.setContent(holder.right_content, model.getContent());
				view.setTag(R.id.bundle_params, position);
				view.setOnClickListener(this);
			} else if (model.getMsgContentType() == 2) {// 通知
				holder.right_pic.setVisibility(View.GONE);
				ViewUtil.setArroundDrawable(holder.right_content, -1,
						R.drawable.notiy_se, -1, -1);
				ViewUtil.setContent(holder.right_content, model.getContent());
			} else if (model.getMsgContentType() == 5) {// 提交作业
				ViewUtil.setArroundDrawable(holder.right_content, -1,
						R.drawable.homework_se, -1, -1);
				ViewUtil.setContent(holder.right_content, model.getContent());
				holder.right_pic.setVisibility(View.VISIBLE);
				holder.right_content.setVisibility(View.VISIBLE);
				activity.imageLoader.displayImage(
						StringUtil.imgUrl(activity, model.getMsgPic()),
						holder.right_pic, activity.options);
				ViewUtil.setContent(holder.right_content, "点击查看作业详情");
				holder.right_pic.setTag(R.id.bundle_params, position);
				holder.right_content.setTag(R.id.bundle_params, position);
				holder.right_pic.setOnClickListener(this);
				holder.right_content.setOnClickListener(this);
			}
			activity.imageLoader.displayImage(
					StringUtil.imgUrl(activity, model.getHeadUrl()),
					holder.right_user_pic, activity.options);

		} else {
			holder.layout_left.setVisibility(View.VISIBLE);
			holder.layout_right.setVisibility(View.GONE);
			if (model.getMsgContentType() == 4) {// 文字消息
				holder.left_content.setGravity(Gravity.LEFT);
				holder.left_pic.setVisibility(View.GONE);
				holder.left_content.setVisibility(View.VISIBLE);
				view.findViewById(R.id.reply_homework).setVisibility(View.GONE);
				Spanned spanned = Html.fromHtml(
						AppUtil.strToHtml(model.getContent(), facesKey),
						new ImageGetter() {
							@Override
							public Drawable getDrawable(String source) {
								try {// 解析表情符
									Drawable drawable = AppUtil
											.loadFaceResourse(
													activity.getAssets(),
													activity, source, facesKey,
													faces);
									drawable.setBounds(0, 0, h, h);
									return drawable;
								} catch (Exception e) {
									e.printStackTrace();
								}
								return null;
							}
						}, null);
				ViewUtil.setContent(holder.left_content, spanned);
				ViewUtil.setArroundDrawable(holder.left_content, -1, -1, -1, -1);
			} else if (model.getMsgContentType() == 3) {// 图片消息
				holder.left_content.setVisibility(View.GONE);
				holder.left_pic.setVisibility(View.VISIBLE);
				view.findViewById(R.id.reply_homework).setVisibility(View.GONE);
				activity.imageLoader.displayImage(
						StringUtil.imgUrl(activity, model.getMsgPic()),
						holder.left_pic, activity.options);
				holder.left_pic.setTag(R.id.bundle_params, position);
				holder.left_pic.setOnClickListener(this);
				ViewUtil.setArroundDrawable(holder.left_content, -1, -1, -1, -1);
			} else if (model.getMsgContentType() == 1) {// 作业
				view.findViewById(R.id.reply_homework).setVisibility(
						View.VISIBLE);
				holder.left_pic.setVisibility(View.GONE);
				holder.left_content.setVisibility(View.VISIBLE);
				ViewUtil.setArroundDrawable(holder.left_content, -1,
						R.drawable.homework_se, -1, -1);
				ViewUtil.setContent(holder.left_content, model.getContent());
				view.setTag(R.id.bundle_params, position);
				view.setOnClickListener(this);
			} else if (model.getMsgContentType() == 2) {// 通知
				view.findViewById(R.id.reply_homework).setVisibility(View.GONE);
				holder.left_pic.setVisibility(View.GONE);
				holder.left_content.setVisibility(View.VISIBLE);
				ViewUtil.setArroundDrawable(holder.left_content, -1,
						R.drawable.notiy_se, -1, -1);
				ViewUtil.setContent(holder.left_content, model.getContent());
			} else if (model.getMsgContentType() == 5) {// 提交作业
				ViewUtil.setArroundDrawable(holder.left_content, -1,
						R.drawable.homework_se, -1, -1);
				ViewUtil.setContent(holder.left_content, model.getContent());
				holder.left_pic.setVisibility(View.VISIBLE);
				holder.left_content.setVisibility(View.VISIBLE);
				activity.imageLoader.displayImage(
						StringUtil.imgUrl(activity, model.getMsgPic()),
						holder.left_pic, activity.options);
				ViewUtil.setContent(holder.left_content, "点击查看作业详情");
				holder.left_pic.setTag(R.id.bundle_params, position);
				holder.left_content.setTag(R.id.bundle_params, position);
				holder.left_pic.setOnClickListener(this);
				holder.left_content.setOnClickListener(this);
			}
			activity.imageLoader.displayImage(
					StringUtil.imgUrl(activity, model.getHeadUrl()),
					holder.left_user_pic, activity.options);

			ViewUtil.setContent(holder.left_user_name, model.getUserName());
		}
		if (position % 20 == 0) {// 每隔20条消息显示发送时间
			holder.time.setVisibility(View.VISIBLE);
			holder.time.setText(StringUtil.getStandardDate(model.getTime()));
		} else {
			holder.time.setVisibility(View.GONE);
		}
		holder.sendStatus.setTag(R.id.bundle_params, position);
		holder.sendStatus.setOnClickListener(this);
		return view;
	}

	class MessageHolder {
		/**
		 * 时间
		 * */
		TextView time;
		// 左边
		/**
		 * 左边布局
		 * */
		LinearLayout layout_left;
		/**
		 * 左边消息内容
		 * */
		TextView left_content;
		/**
		 * 左边图片
		 * */
		ImageView left_pic;
		/**
		 * 左边消息人头像
		 * */
		ImageView left_user_pic;
		/**
		 * 左边消息人名字
		 * */
		TextView left_user_name;
		// 右边
		/**
		 * 右边布局
		 * */
		LinearLayout layout_right;
		/**
		 * 右边消息内容
		 * */
		TextView right_content;
		/***
		 * 右边图片
		 * */
		ImageView right_pic;
		/**
		 * 是否发送成功
		 * */
		ImageView sendStatus;
		/**
		 * 右边消息人头像
		 * */
		ImageView right_user_pic;
		/**
		 * 正在发送图标
		 * */
		ProgressBar sendIng;
	}

	@Override
	public void onClick(View v) {
		int position = StringUtil
				.toInt(v.getTag(R.id.bundle_params).toString());
		switch (v.getId()) {
		case R.id.left_pic:
		case R.id.right_pic:// 查看图片
			if (listMessage.get(position).getStatu() == MesageStatus.SEND_ING)
				return;
			Bundle bundle = new Bundle();
			if (listMessage.get(position).getImgBtye() == null
					|| listMessage.get(position).getImgBtye().equals("")) {
				List<String> list = new ArrayList<String>();
				list.add(listMessage.get(position).getMsgPic());
				bundle.putStringArrayList(IConstant.BUNDLE_PARAMS,
						(ArrayList<String>) list);
				bundle.putInt(IConstant.BUNDLE_PARAMS1, 1);
			} else {
				
				bundle.putInt(IConstant.BUNDLE_PARAMS1, 11);

			}
			byteData=listMessage.get(position).getImgBtye();
			activity.startActivity(ViewPhotosActivity.class, bundle);
			break;
		case R.id.right_status:// 发送失败后的重发
			activity.getContentResolver().delete(
					Uri.parse(MessageProvider.MESSAGES_URL), "_id=?",
					new String[] { "" + listMessage.get(position).getDbId() });// 删除当前的消息
			String id = "";
			if (listMessage.get(position).getMsgType() == 2) {
				id = "discussId=";
			} else {
				id = "classId=";
			}
			if (listMessage.get(position).getMsgContentType() == 4) {// 重发文字消息
				MinaClientHandler.sendMessage(listMessage.get(position), id
						+ listMessage.get(position).getToUid() + "\n" + "\n",
						activity.getContentResolver());
			} else if (listMessage.get(position).getMsgContentType() == 3) {// 重发图片消息
				MinaClientHandler.sendMessage(
						listMessage.get(position),
						id
								+ listMessage.get(position).getToUid()
								+ ",filename="
								+ AppUtil.getFileName(listMessage.get(position)
										.getMsgPic()) + "\n",
						activity.getContentResolver());
			}
			break;
		case R.id.right_content:// 查找某个作业的已交作业
			try {
				SelectHomeOrNotify(listMessage.get(position)
						.getMsgContentType() + "", listMessage.get(position)
						.getMId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:// 点击交作业的拍照
			ChatGroupMessageListActivity.picPath = AppUtil.createNewPhoto();
			ChatGroupMessageListActivity.homeworkId = listMessage.get(position)
					.getMId();
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
					ChatGroupMessageListActivity.picPath)));
			activity.startActivityForResult(intent, IConstant.FOR_HOMEWORK);
			break;
		}
	}

	/**
	 * 查找作业
	 * */
	public void SelectHomeOrNotify(String msgtype, int mid) throws Exception {
		listMessage.clear();
		Cursor cursor = activity.getContentResolver().query(
				Uri.parse(MessageProvider.MESSAGES_URL), null,
				"( ( _msgctype=? or _msgctype=? ) and _mid=?)",
				new String[] { "1", "5", mid + "" }, " _time desc ");
		while (cursor.moveToNext()) {
			MessageModel model = new MessageModel();
			model.setDbId(cursor.getInt(0));
			model.setUid(cursor.getLong(1));
			model.setMid(cursor.getLong(2));
			model.setName(cursor.getString(3));
			model.setContent(cursor.getString(4));
			model.setTime(cursor.getLong(5));
			model.setHeadUrl(cursor.getString(6));
			model.setToUid(cursor.getLong(7));
			model.setMsgContentType(cursor.getInt(12));
			model.setMsgPic(cursor.getString(13));
			model.setStatu(cursor.getInt(14));
			model.setUserName(cursor.getString(15));
			model.setMId(cursor.getInt(16));
			listMessage.add(0, model);
		}
		if (cursor.getCount() <= 1) {
			ViewUtil.showMessage(activity, "还没有人交作业哦");
		}
		cursor.close();
		notifyDataSetChanged();
	}

}
