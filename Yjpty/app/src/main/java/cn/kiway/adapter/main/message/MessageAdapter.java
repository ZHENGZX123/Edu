package cn.kiway.adapter.main.message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.common.ViewPhotosActivity;
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
			if (model.getStatu() == MesageStatus.SEND_ERR) {
				holder.sendStatus.setVisibility(View.VISIBLE);
				holder.sendStatus.setTag(R.id.bundle_params, position);
				holder.sendStatus.setOnClickListener(this);
			} else {
				holder.sendStatus.setVisibility(View.GONE);
			}
			if (model.getStatu() == MesageStatus.SEND_ING) {
				if (Math.ceil(System.currentTimeMillis() / 60 / 1000.0f)
						- Math.ceil(model.getTime() / 60 / 1000.0f) > 1) {
					holder.sendStatus.setVisibility(View.VISIBLE);
					holder.sendIng.setVisibility(View.GONE);
					holder.sendStatus.setTag(R.id.bundle_params, position);
					holder.sendStatus.setOnClickListener(this);
				} else {
					holder.sendIng.setVisibility(View.VISIBLE);
					holder.sendStatus.setVisibility(View.GONE);
				}

			} else {
				holder.sendIng.setVisibility(View.GONE);
			}
			holder.layout_left.setVisibility(View.GONE);
			holder.layout_right.setVisibility(View.VISIBLE);
			if (model.getMsgContentType() == 4) {
				holder.right_content.setVisibility(View.VISIBLE);
				holder.right_pic.setVisibility(View.GONE);
				holder.right_content.setGravity(Gravity.LEFT);
				view.findViewById(R.id.look_home).setVisibility(View.GONE);
				Spanned spanned = Html.fromHtml(
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
				ViewUtil.setArroundDrawable(holder.right_content, -1, -1, -1,
						-1);
				ViewUtil.setContent(holder.right_content, spanned);
			} else if (model.getMsgContentType() == 3) {
				holder.right_content.setVisibility(View.GONE);
				holder.right_pic.setVisibility(View.VISIBLE);
				view.findViewById(R.id.look_home).setVisibility(View.GONE);
				if (model.getStatu() == MesageStatus.SEND_ING
						|| model.getStatu() == MesageStatus.SEND_ERR) {
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
				ViewUtil.setArroundDrawable(holder.right_content, -1, -1, -1,
						-1);
			} else if (model.getMsgContentType() == 1) {
				holder.right_pic.setVisibility(View.GONE);
				holder.right_content.setVisibility(View.VISIBLE);
				view.findViewById(R.id.look_home).setVisibility(View.VISIBLE);
				view.setTag(R.id.bundle_params, position);
				view.setOnClickListener(this);
				ViewUtil.setArroundDrawable(holder.right_content, -1,
						R.drawable.homework_se, -1, -1);
				ViewUtil.setContent(holder.right_content, model.getContent());
				holder.right_content.setTag(R.id.bundle_params, position);
				holder.right_content.setOnClickListener(this);
			} else if (model.getMsgContentType() == 2) {
				holder.right_pic.setVisibility(View.GONE);
				holder.right_content.setVisibility(View.VISIBLE);
				view.findViewById(R.id.look_home).setVisibility(View.GONE);
				ViewUtil.setArroundDrawable(holder.right_content, -1,
						R.drawable.notiy_se, -1, -1);
				ViewUtil.setContent(holder.right_content, model.getContent());
			}
			activity.imageLoader.displayImage(
					StringUtil.imgUrl(activity, model.getHeadUrl()),
					holder.right_user_pic, activity.options);
		} else {
			holder.layout_left.setVisibility(View.VISIBLE);
			holder.layout_right.setVisibility(View.GONE);
			if (model.getMsgContentType() == 4) {
				holder.left_pic.setVisibility(View.GONE);
				holder.left_content.setVisibility(View.VISIBLE);
				holder.left_content.setGravity(Gravity.LEFT);
				Spanned spanned = Html.fromHtml(
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
				ViewUtil.setContent(holder.left_content, spanned);
				ViewUtil.setArroundDrawable(holder.left_content, -1, -1, -1, -1);
			} else if (model.getMsgContentType() == 3) {
				holder.left_content.setVisibility(View.GONE);
				holder.left_pic.setVisibility(View.VISIBLE);
				activity.imageLoader.displayImage(
						StringUtil.imgUrl(activity, model.getMsgPic()),
						holder.left_pic, activity.options);
				holder.left_pic.setTag(R.id.bundle_params, position);
				holder.left_pic.setOnClickListener(this);
				ViewUtil.setArroundDrawable(holder.left_content, -1, -1, -1, -1);
			} else if (model.getMsgContentType() == 1) {
				holder.left_pic.setVisibility(View.GONE);
				holder.left_content.setVisibility(View.VISIBLE);
				ViewUtil.setArroundDrawable(holder.left_content, -1,
						R.drawable.homework_se, -1, -1);
				ViewUtil.setContent(holder.left_content, model.getContent());
				holder.left_content.setTag(R.id.bundle_params, position);
				holder.left_content.setOnClickListener(this);
			} else if (model.getMsgContentType() == 2) {
				holder.left_pic.setVisibility(View.GONE);
				holder.left_content.setVisibility(View.VISIBLE);
				ViewUtil.setArroundDrawable(holder.left_content, -1,
						R.drawable.notiy_se, -1, -1);
				ViewUtil.setContent(holder.left_content, model.getContent());
			} else if (model.getMsgContentType() == 5) {
				holder.left_content.setVisibility(View.VISIBLE);
				holder.left_pic.setVisibility(View.VISIBLE);
				ViewUtil.setContent(holder.left_content, "点击查看作业详情");
				activity.imageLoader.displayImage(
						StringUtil.imgUrl(activity, model.getMsgPic()),
						holder.left_pic, activity.options);
				ViewUtil.setArroundDrawable(holder.left_content, -1,
						R.drawable.homework_se, -1, -1);
				holder.left_pic.setTag(R.id.bundle_params, position);
				holder.left_pic.setOnClickListener(this);
				holder.left_content.setTag(R.id.bundle_params, position);
				holder.left_content.setOnClickListener(this);
			}
			activity.imageLoader.displayImage(
					StringUtil.imgUrl(activity, model.getHeadUrl()),
					holder.left_user_pic, activity.options);
			ViewUtil.setContent(holder.left_user_name, model.getUserName());
		}
		if (position % 20 == 0) {
			holder.time.setVisibility(View.VISIBLE);
			holder.time.setText(StringUtil.getStandardDate(model.getTime()));
		} else {
			holder.time.setVisibility(View.GONE);
		}
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
		case R.id.right_pic:
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
				activity.startActivity(ViewPhotosActivity.class, bundle);
			} else {
				byteData=listMessage.get(position).getImgBtye();
				bundle.putInt(IConstant.BUNDLE_PARAMS1, 11);
				activity.startActivity(ViewPhotosActivity.class, bundle);
			}
			break;
		case R.id.right_status:// 重发
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
		case R.id.left_content:
		case R.id.right_content:
			try {
				SelectHomeOrNotify(listMessage.get(position)
						.getMsgContentType() + "", listMessage.get(position)
						.getMId());
			} catch (Exception e) {
				e.printStackTrace();
			}
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
				"_mid=?  and ( _msgctype=? or _msgctype=? )",
				new String[] { mid + "", "1", "5" }, " _time desc ");
		while (cursor.moveToNext()) {
			MessageModel model = new MessageModel();
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
