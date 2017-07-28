package cn.kiway.activity.message;

import handmark.pulltorefresh.library.PullToRefreshBase;
import handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.adapter.common.FacesAdapter;
import cn.kiway.adapter.message.MessageAdapter;
import cn.kiway.dialog.MessageSeclectDialog;
import cn.kiway.dialog.MessageSeclectDialog.MessageSelectCallBack;
import cn.kiway.http.BaseHttpConnectPool;
import cn.kiway.http.UploadFile;
import cn.kiway.http.UploadFile.UploadCallBack;
import cn.kiway.message.MinaClientHandler;
import cn.kiway.message.model.MessageObserver;
import cn.kiway.message.model.MessageProvider;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.Logger;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class ChatGroupMessageListActivity extends BaseActivity implements
		OnRefreshListener2<ListView>, MessageSelectCallBack, TextWatcher,
		OnEditorActionListener, UploadCallBack {
	EditText messageContent;
	GridView faceList;
	MessageAdapter adapter;
	PullToRefreshListView listView;
	ListView lv;
	MessageSeclectDialog dialog;
	MessageModel messageModel;
	public static String picPath;// 拍照地址
	public static int homeworkId = -1;
	Handler handler;
	ContentResolver contentResolver;
	/**
	 * 消息观察者
	 * */
	MessageObserver messageObserver;
	/**
	 * 是否查找作业或通知
	 * */
	boolean isSelect = false;
	/**
	 * 底层布局
	 * */
	LinearLayout footLinear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		messageModel = (MessageModel) bundle
				.getSerializable(IConstant.BUNDLE_PARAMS);
		contentResolver = getContentResolver();
		app.setIntMsgId(messageModel.getToUid());
		setContentView(R.layout.activity_chat_group);
		try {
			hander();
			initView();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("HandlerLeak")
	void hander() throws Exception {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:// 查找聊天数据
					if (isSelect) {
						listView.onRefreshComplete();
						return;
					}
					if (isRefresh)
						adapter.listMessage.clear();
					Cursor cursor = contentResolver.query(
							// 根据发给谁和 消息的类型来查找数据
							Uri.parse(MessageProvider.MESSAGES_URL),
							null,
							"( _touid=? and _msgtype=? )",
							new String[] { messageModel.getToUid() + "",
									messageModel.getMsgType() + "", },
							" _time desc limit " + 20 + " offset "
									+ adapter.getCount());
					while (cursor.moveToNext()) {
						MessageModel model = new MessageModel();
						model.setDbId(cursor.getInt(0));// 数据库自然增长的id
						model.setUid(cursor.getLong(1));// 用户的id
						model.setMid(cursor.getLong(2));// 消息id
						model.setName(cursor.getString(3));// 消息的名字
						model.setContent(cursor.getString(4));// 消息的内容
						model.setTime(cursor.getLong(5));// 消息的时间
						model.setHeadUrl(cursor.getString(6));// 消息人的头像
						model.setToUid(cursor.getLong(7));// 发个谁的id
						model.setMsgContentType(cursor.getInt(12));// 消息的内容类型
						model.setMsgPic(cursor.getString(13));// 消息人的头像
						model.setStatu(cursor.getInt(14));// 消息的状态
						model.setUserName(cursor.getString(15));// 消息人的名字
						model.setMId(cursor.getInt(16));// 作业或者通知的id
						model.setImgBtye(cursor.getBlob(17));
						adapter.listMessage.add(0, model);
					}
					cursor.close();
					if (isRefresh) {
						adapter.notifyDataSetChanged();
						lv.setSelection(adapter.getCount() - 1);
					} else {
						lv.setSelection(adapter.getCount() - 1);
						adapter.notifyDataSetChanged();
					}
					listView.onRefreshComplete();
					break;
				case 1:// 更新视图
					adapter.notifyDataSetChanged();
					lv.setSelection(adapter.getCount() - 1);
					break;
				case 2:// 查找作业数据
					try {
						SelectHomeOrNotify("1");
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case 3:// 查找通知数据
					try {
						SelectHomeOrNotify("2");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	@SuppressWarnings("deprecation")
	public void initView() throws Exception {
		messageContent = ViewUtil.findViewById(this, R.id.edit);
		footLinear = ViewUtil.findViewById(this, R.id.layouts);
		messageContent.addTextChangedListener(this);
		messageContent.setOnEditorActionListener(this);
		dialog = new MessageSeclectDialog(this,
				messageModel.getHomerWorkNumber(),
				messageModel.getNotifyNumber(), this);
		findViewById(R.id.emoticon).setOnClickListener(this);
		findViewById(R.id.send).setOnClickListener(this);
		findViewById(R.id.search).setOnClickListener(this);
		findViewById(R.id.add).setOnClickListener(this);
		findViewById(R.id.camare).setOnClickListener(this);
		findViewById(R.id.photo).setOnClickListener(this);
		findViewById(R.id.add_more).setOnClickListener(this);
		findViewById(R.id.edit).setOnClickListener(this);
		faceList = ViewUtil.findViewById(this, R.id.faces_list);
		try {// 初始化表情
			faceList.setAdapter(new FacesAdapter(this, messageContent
					.getEditableText()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		adapter = new MessageAdapter(this, new ArrayList<MessageModel>());
		listView = ViewUtil.findViewById(this, R.id.boy_list);
		listView.setMode(Mode.PULL_DOWN_TO_REFRESH);
		listView.setOnRefreshListener(this);
		lv = listView.getRefreshableView();
		lv.setAdapter(adapter);
		lv.setSelected(true);
		if (messageModel.getMsgType() != 3)
			findViewById(R.id.search).setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.emoticon:// 表情按钮
			if (footLinear.getVisibility() == View.VISIBLE)
				footLinear.setVisibility(View.GONE);
			if (faceList.getVisibility() == View.GONE) {
				faceList.setVisibility(View.VISIBLE);
				ViewUtil.hideKeyboard(this);
			} else {
				faceList.setVisibility(View.GONE);
			}
			break;
		case R.id.send:// 发送按钮
			try {
				sendMessage();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.search:// 查找通知与作业的按钮
			if (dialog != null && !dialog.isShowing())
				dialog.show();
			break;
		case R.id.add:// 进入设置按钮
			Bundle bundle = new Bundle();
			bundle.putSerializable(IConstant.BUNDLE_PARAMS, messageModel);
			startActivity(MessagSettingActivity.class, bundle);
			break;
		case R.id.camare:// 拍照
			picPath = AppUtil.createNewPhoto();
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(picPath)));
			startActivityForResult(intent, IConstant.FOR_CAMERA);
			break;
		case R.id.photo:// 图库选择
			startActivityForResult(new Intent(Intent.ACTION_PICK,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
					IConstant.FOR_PHOTO);
			break;
		case R.id.add_more:// 下方拍照的显示按钮
			if (faceList.getVisibility() == View.VISIBLE)
				faceList.setVisibility(View.GONE);
			if (footLinear.getVisibility() == View.GONE) {
				footLinear.setVisibility(View.VISIBLE);
				ViewUtil.hideKeyboard(this);
			} else {
				footLinear.setVisibility(View.GONE);
			}
			break;
		case R.id.edit:
			faceList.setVisibility(View.GONE);
			footLinear.setVisibility(View.GONE);
			break;
		}
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void loadData() throws Exception {
		super.loadData();
		isRefresh = true;
		messageObserver = new MessageObserver(new Handler() {// 初始化数据更新监听器
					@Override
					public void handleMessage(Message msg) {
						handler.sendEmptyMessage(0);
					}
				});
		contentResolver.registerContentObserver(
				Uri.parse(MessageProvider.MESSAGES_URL), true, messageObserver);
		handler.sendEmptyMessage(0);
		ViewUtil.setContent(this, R.id.title, messageModel.getName());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		app.setIntMsgId(-1);
		if (messageObserver != null)
			contentResolver.unregisterContentObserver(messageObserver);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		isRefresh = false;
		handler.sendEmptyMessageDelayed(0, 1000);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

	}

	@Override
	public void selectHomeWork() throws Exception {// 查找作业
		handler.sendEmptyMessageDelayed(2, 500);
	}

	@Override
	public void selectNotify() throws Exception {// 查找通知
		handler.sendEmptyMessageDelayed(3, 500);
	}

	@Override
	public void afterTextChanged(Editable ed) {// 监听下方文本编辑器 显示发送按钮
		if (ed.length() > 0) {
			findViewById(R.id.send).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.send).setVisibility(View.GONE);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEND// 软键盘的发送按钮
				|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
			try {
				sendMessage();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(final int requstCode, int resultCode,
			final Intent data) {
		super.onActivityResult(requstCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		IConstant.executorService.execute(new Runnable() {
			public void run() {
				handler.sendEmptyMessage(1);
				switch (requstCode) {
				case IConstant.FOR_CAMERA:// 拍照
					try {
						sendPic(picPath);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case IConstant.FOR_PHOTO:// 相册
					try {
						sendPic(StringUtil.getPicPath(data.getData(),
								contentResolver));
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case IConstant.FOR_HOMEWORK:// 提交作业
					if (homeworkId == -1)
						return;
					thread = new Thread(networkTask);
					thread.start();
					break;
				}
			}
		});
	}

	/**
	 * 发送文字消息
	 * */
	void sendMessage() throws Exception {
		if (StringUtil.isEmpty(ViewUtil.getContent(this, R.id.edit)))
			return;
		MessageModel mId = new MessageModel();
		mId.setContent(ViewUtil.getContent(this, R.id.edit));
		if (mCache.getAsJSONObject(IUrContant.GET_MY_INFO_URL + app.getUid()) != null
				&& mCache.getAsJSONObject(
						IUrContant.GET_MY_INFO_URL + app.getUid())
						.optJSONObject("userInfo") != null)
			mId.setHeadUrl(mCache
					.getAsJSONObject(IUrContant.GET_MY_INFO_URL + app.getUid())
					.optJSONObject("userInfo").optString("photo"));
		mId.setMid(System.currentTimeMillis());
		mId.setName(messageModel.getName());
		mId.setUserName("我");
		mId.setTime(System.currentTimeMillis());
		mId.setToUid(messageModel.getToUid());
		mId.setUid(app.getUid());
		mId.setMsgContentType(4);
		adapter.listMessage.add(mId);
		handler.sendEmptyMessage(1);
		lv.setSelection(adapter.getCount() - 1);
		messageContent.getText().clear();
		if (messageModel.getMsgType() == 3) {
			Logger.log("班级id::::::::" + messageModel.getToUid());
			mId.setMsgType(3);
			MinaClientHandler.sendMessage(mId,
					"classId=" + messageModel.getToUid() + "\n",
					contentResolver);
		} else {
			mId.setMsgType(2);
			Logger.log("讨论组id::::::::" + messageModel.getToUid());
			MinaClientHandler.sendMessage(mId,
					"discussId=" + messageModel.getToUid() + "\n",
					contentResolver);
		}
	}

	/**
	 * 发送图片消息
	 * */
	void sendPic(String picPath) throws Exception {
		if (picPath == null || picPath.equals(""))
			return;
		if (faceList.getVisibility() == View.VISIBLE)
			faceList.setVisibility(View.GONE);
		ViewUtil.hideKeyboard(this);
		MessageModel mId = new MessageModel();
		mId.setContent("图片消息");
		if (mCache.getAsJSONObject(IUrContant.GET_MY_INFO_URL + app.getUid()) != null
				&& mCache.getAsJSONObject(
						IUrContant.GET_MY_INFO_URL + app.getUid())
						.optJSONObject("userInfo") != null)
			mId.setHeadUrl(mCache
					.getAsJSONObject(IUrContant.GET_MY_INFO_URL + app.getUid())
					.optJSONObject("userInfo").optString("photo"));
		mId.setMid(System.currentTimeMillis());
		mId.setName(messageModel.getName());
		mId.setTime(System.currentTimeMillis());
		mId.setToUid(messageModel.getToUid());
		mId.setUid(app.getUid());
		mId.setMsgType(3);
		mId.setUserName("我");
		mId.setMsgContentType(3);
		mId.setMsgPic(picPath);
		adapter.listMessage.add(mId);
		handler.sendEmptyMessage(1);
		messageContent.getText().clear();
		Logger.log("文件名字::::::::" + AppUtil.getFileName(picPath));
		if (messageModel.getMsgType() == 3) {
			mId.setMsgType(3);
			Logger.log("班级id::::::::" + messageModel.getToUid());
			MinaClientHandler.sendMessage(mId,
					"classId=" + messageModel.getToUid() + ",filename="
							+ AppUtil.getFileName(picPath) + "\n",
					contentResolver);
		} else {
			mId.setMsgType(2);
			Logger.log("讨论组Id::::::::" + messageModel.getToUid());
			MinaClientHandler.sendMessage(mId,
					"discussId=" + messageModel.getToUid() + ",filename="
							+ AppUtil.getFileName(picPath) + "\n",
					contentResolver);
		}
	}

	/**
	 * 查找作业或者通知
	 * */
	public void SelectHomeOrNotify(String msgtype) throws Exception {
		isSelect = true;
		adapter.listMessage.clear();
		Cursor cursor;
		if (msgtype.equals("1")) {// 查找作业
			cursor = contentResolver
					.query(Uri.parse(MessageProvider.MESSAGES_URL),
							null,
							"( _touid=? and _msgtype=? and ( _msgctype=? or _msgctype=?) )",
							new String[] { messageModel.getToUid() + "",
									messageModel.getMsgType() + "", "1", "5" },
							" _time desc ");
		} else {// 查找通知
			cursor = contentResolver.query(
					Uri.parse(MessageProvider.MESSAGES_URL),
					null,
					"( _touid=? and _msgtype=? and _msgctype=? )",
					new String[] { messageModel.getToUid() + "",
							messageModel.getMsgType() + "", msgtype },
					" _time desc ");
		}
		while (cursor.moveToNext()) {
			MessageModel model = new MessageModel();
			model.setDbId(cursor.getInt(0));// 数据库存的id
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
			adapter.listMessage.add(0, model);
		}
		cursor.close();
		adapter.notifyDataSetChanged();
		lv.setSelection(adapter.getCount() - 1);
	}

	Thread thread;
	Map<String, File> upfiles = new HashMap<String, File>();
	/**
	 * 上传作业照片
	 * */
	Runnable networkTask = new Runnable() {

		@Override
		public void run() {
			File f = new File(picPath);
			upfiles.put(f.getName(), f);
			try {
				Map<String, String> params = new HashMap<String, String>();
				params.put("homeworkId", homeworkId + "");// 作业的id
				params.put("userId", app.getUid() + "");// 用户的id
				params.put("classId", app.getBoyModels().get(app.getPosition())
						.getClassId()
						+ "");// 班级的id
				UploadFile.post(ChatGroupMessageListActivity.this,
						IUrContant.REPLY_HOMEWORK_URL, params, upfiles,
						ChatGroupMessageListActivity.this,app);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void uploadCallBack(String data, String actionUrl) throws Exception {// 上传作业照片成功后的回调
		if (actionUrl.equals(IUrContant.REPLY_HOMEWORK_URL)) {
			BaseHttpConnectPool.loodingDialog.dismiss();
		}
	}
}
