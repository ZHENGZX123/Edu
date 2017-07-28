package cn.kiway.activity.main.message;

import handmark.pulltorefresh.library.PullToRefreshBase;
import handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.File;
import java.util.ArrayList;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.adapter.common.FacesAdapter;
import cn.kiway.adapter.main.message.MessageAdapter;
import cn.kiway.message.MinaClientHandler;
import cn.kiway.message.model.MessageObserver;
import cn.kiway.message.model.MessageProvider;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.Logger;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class PrivateMessageActivity extends BaseActivity implements
		OnRefreshListener2<ListView>, TextWatcher, OnEditorActionListener {
	EditText messageContent;
	GridView faceList;
	MessageAdapter adapter;
	PullToRefreshListView listView;
	ListView lv;
	MessageModel messageModel;
	Handler handler;
	static ContentResolver contentResolver;
	/**
	 * 消息观察者
	 * */
	MessageObserver messageObserver;
	String picPath;// 拍照地址

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_private_message);
		messageModel = (MessageModel) bundle
				.getSerializable(IConstant.BUNDLE_PARAMS);
		contentResolver = getContentResolver();
		app.setIntMsgId(messageModel.getToUid());
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					listView.onRefreshComplete();
					if (isRefresh)
						adapter.listMessage.clear();
					Cursor cursor = getContentResolver()
							.query(Uri.parse(MessageProvider.MESSAGES_URL),
									null,
									"(_touid=? and  _uid=?) or (_touid=? and  _uid=?) and  _msgtype=? ",
									new String[] {
											messageModel.getToUid() + "",
											messageModel.getUid() + "",
											messageModel.getUid() + "",
											messageModel.getToUid() + "", "1" },
									" _time desc limit " + 20 + " offset "
											+ adapter.getCount());
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
						model.setMsgType(cursor.getInt(9));
						model.setMsgContentType(cursor.getInt(12));
						model.setMsgPic(cursor.getString(13));
						model.setStatu(cursor.getInt(14));
						model.setUserName(cursor.getString(15));
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
					break;
				}
			}
		};
		try {
			initView();
			loadData();
			setData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void initView() throws Exception {
		messageContent = ViewUtil.findViewById(this, R.id.edit);
		messageContent.addTextChangedListener(this);
		messageContent.setOnEditorActionListener(this);
		findViewById(R.id.emoticon).setOnClickListener(this);
		findViewById(R.id.send).setOnClickListener(this);
		findViewById(R.id.camare).setOnClickListener(this);
		findViewById(R.id.photo).setOnClickListener(this);
		findViewById(R.id.add_more).setOnClickListener(this);
		findViewById(R.id.edit).setOnClickListener(this);
		faceList = ViewUtil.findViewById(this, R.id.faces_list);
		try {
			faceList.setAdapter(new FacesAdapter(this, messageContent
					.getEditableText()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		adapter = new MessageAdapter(this, new ArrayList<MessageModel>());
		listView = ViewUtil.findViewById(this, R.id.message_list);
		listView.setMode(Mode.PULL_DOWN_TO_REFRESH);
		listView.setOnRefreshListener(this);
		lv = listView.getRefreshableView();
		lv.setAdapter(adapter);
		ViewUtil.setContent(this, R.id.title, messageModel.getName());
		findViewById(R.id.homework).setVisibility(View.GONE);
		findViewById(R.id.notify).setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.emoticon:
			if (faceList.getVisibility() == View.GONE) {
				faceList.setVisibility(View.VISIBLE);
				ViewUtil.hideKeyboard(this);
			} else {
				faceList.setVisibility(View.GONE);
			}
			break;
		case R.id.send:
			try {
				sendMessage();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
		case R.id.add_more:// 点击加
			if (faceList.getVisibility() == View.VISIBLE)
				faceList.setVisibility(View.GONE);
			if (findViewById(R.id.layouts).getVisibility() == View.GONE) {
				findViewById(R.id.layouts).setVisibility(View.VISIBLE);
				ViewUtil.hideKeyboard(this);
			} else {
				findViewById(R.id.layouts).setVisibility(View.GONE);
			}
			break;
		case R.id.edit:
			faceList.setVisibility(View.GONE);
			findViewById(R.id.layouts).setVisibility(View.GONE);
			break;
		}
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void loadData() throws Exception {
		super.loadData();
		isRefresh = true;
		messageObserver = new MessageObserver(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					isRefresh = true;
					handler.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		contentResolver.registerContentObserver(
				Uri.parse(MessageProvider.MESSAGES_URL), true, messageObserver);
		handler.sendEmptyMessage(0);
		ViewUtil.setContent(this, R.id.title, messageModel.getName());
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
	protected void onActivityResult(final int requstCode, int resultCode,
			final Intent data) {
		super.onActivityResult(requstCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		IConstant.executorService.execute(new Runnable() {
			public void run() {
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
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (messageObserver != null)
			contentResolver.unregisterContentObserver(messageObserver);
		app.setIntMsgId(-1);
	}

	/**
	 * 发送图片消息
	 * */
	void sendPic(String picPath) throws Exception {
		if (picPath == null || picPath.equals(""))
			return;
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
		if (app.getUid() == messageModel.getToUid()) {
			mId.setToUid(messageModel.getUid());
		} else {
			mId.setToUid(messageModel.getToUid());
		}
		mId.setUid(app.getUid());
		mId.setMsgType(1);
		mId.setMsgContentType(3);
		mId.setMsgPic(picPath);
		messageContent.getText().clear();
		Logger.log("classID::::::::" + messageModel.getToUid());
		Logger.log("文件名字::::::::" + AppUtil.getFileName(picPath));
		MinaClientHandler.sendMessage(mId, "primessage=" + mId.getToUid()
				+ ",filename=" + AppUtil.getFileName(picPath) + "\n",
				contentResolver);
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
		mId.setTime(System.currentTimeMillis());
		mId.setUserName(app.getName());
		if (app.getUid() == messageModel.getToUid()) {
			mId.setToUid(messageModel.getUid());
		} else {
			mId.setToUid(messageModel.getToUid());
		}
		mId.setUid(app.getUid());
		mId.setMsgContentType(4);
		mId.setMsgType(1);
		messageContent.getText().clear();
		Logger.log("用户id::::::::" + mId.getToUid());
		MinaClientHandler.sendMessage(mId, "primessage=" + mId.getToUid()
				+ "\n", contentResolver);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEND
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
	public void afterTextChanged(Editable ed) {
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
}
