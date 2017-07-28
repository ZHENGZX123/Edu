package cn.kiway.activity.main.message;

import java.util.HashMap;
import java.util.Map;

import kankan.wheel.widget.SwitchButton;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.activity.me.EditNameActivity;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.message.model.MessageChatProvider;
import cn.kiway.message.model.MessageProvider;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.ViewUtil;

public class MessagSettingActivity extends BaseNetWorkActicity implements
		OnCheckedChangeListener {
	MessageModel model;
	IsNetWorkDialog dialog;
	SwitchButton button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = (MessageModel) bundle.getSerializable(IConstant.BUNDLE_PARAMS);
		setContentView(R.layout.activity_message_setting);
		try {
			initView();
			setData();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initView() throws Exception {
		super.initView();
		dialog = new IsNetWorkDialog(this, this, "确定清空当前聊天记录么?",
				resources.getString(R.string.sure));
		findViewById(R.id.wdqmp).setOnClickListener(this);
		findViewById(R.id.qcy_btn).setOnClickListener(this);
		findViewById(R.id.qkjlu_btn).setOnClickListener(this);
		button = ViewUtil.findViewById(this, R.id.mTogBtn);
		button.setOnCheckedChangeListener(this);
		if ((boolean) mCache.getAsObject(IConstant.MESSAGE_NOTIFY
				+ model.getToUid())) {// 获取保存的数据，设置按钮的开关
			button.setChecked(true);
		} else {
			button.setChecked(false);
		}
	}

	@Override
	public void setData() throws Exception {
		super.setData();
		ViewUtil.setContent(this, R.id.title, model.getName());
	};

	@Override
	public void loadData() throws Exception {
		super.loadData();
		Map<String, String> map = new HashMap<>();
		map.put("userId", app.getUid() + "");
		if (model.getMsgType() == 2) {// 获取讨论组信息
			map.put("discussId", model.getToUid() + "");
			IConstant.HTTP_CONNECT_POOL.addRequest(
					IUrContant.GET_TAO_LUN_ZU_URL, map, activityHandler);
		} else {// 获取班级群信息
			map.put("classId", model.getToUid() + "");
			IConstant.HTTP_CONNECT_POOL.addRequest(
					IUrContant.GET_CLASS_NUMBER_URL, map, activityHandler);
		}
	};

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Bundle b = new Bundle();
		switch (v.getId()) {
		case R.id.wdqmp:
			b.putString(IConstant.BUNDLE_PARAMS,
					ViewUtil.getContent(this, R.id.qmb));
			b.putBoolean(IConstant.BUNDLE_PARAMS1, false);
			b.putSerializable(IConstant.BUNDLE_PARAMS2, model);
			Intent intent = new Intent(this, EditNameActivity.class);
			intent.putExtras(b);
			startActivityForResult(intent, 1);
			break;
		case R.id.qcy_btn:
			Bundle bundle = new Bundle();
			if (model.getMsgType() == 2) {
				bundle.putBoolean(IConstant.BUNDLE_PARAMS, true);
			} else {
				bundle.putBoolean(IConstant.BUNDLE_PARAMS, false);
			}
			bundle.putBoolean(IConstant.BUNDLE_PARAMS3, true);
			bundle.putString(IConstant.BUNDLE_PARAMS2, model.getName());
			bundle.putString(IConstant.BUNDLE_PARAMS1, model.getToUid() + "");
			startActivity(AddPeopleActivity.class, bundle);
			break;
		case R.id.qkjlu_btn:
			if (dialog != null && !dialog.isShowing())
				dialog.show();
			break;
		}
	}

	@Override
	public void isNetWorkCallBack() throws Exception {
		getContentResolver()
				.delete(Uri.parse(MessageProvider.MESSAGES_URL),
						" _touid=? and _msgtype=? ",
						new String[] { "" + model.getToUid(),
								model.getMsgType() + "" });
		ContentValues values = new ContentValues();
		values.put("_content", "");
		getContentResolver()
				.update(Uri.parse(MessageChatProvider.MESSAGECHATS_URL),
						values,
						"_touid=? and _msgtype=?",
						new String[] { "" + model.getToUid(),
								model.getMsgType() + "" });
		ViewUtil.showMessage(this, "清除成功");
	}

	@Override
	public void cancel() throws Exception {

	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean bool) {
		if (bool) {
			mCache.put(IConstant.MESSAGE_NOTIFY + model.getToUid(), true);
		} else {
			mCache.put(IConstant.MESSAGE_NOTIFY + model.getToUid(), false);
		}
	}

	@Override
	protected void onActivityResult(int requstCode, int resultCode, Intent data) {
		super.onActivityResult(requstCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		ViewUtil.setContent(this, R.id.qmb,
				data.getStringExtra(IConstant.BUNDLE_PARAMS));
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.GET_TAO_LUN_ZU_URL)) {// 获取到讨论组信息
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				if (data.optJSONObject("discussJ").optString("display_name")
						.equals("")) {
					ViewUtil.setContent(this, R.id.qmb,
							data.optJSONObject("discussJ")
									.optString("realname"));
				} else {
					ViewUtil.setContent(
							this,
							R.id.qmb,
							data.optJSONObject("discussJ").optString(
									"display_name"));
				}
				ViewUtil.setContent(this, R.id.qcy_text,
						data.optJSONObject("discussJ").optString("userNumber")
								+ "人");
			}
		} else if (message.getUrl().equals(IUrContant.GET_CLASS_NUMBER_URL)) {// 获取到班级信息
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				if (data.optJSONObject("discuss").optString("display_name")
						.equals("")
						|| data.optJSONObject("discuss")
								.optString("display_name").equals("null")) {
					ViewUtil.setContent(this, R.id.qmb,
							data.optJSONObject("discuss").optString("realname"));
				} else {
					ViewUtil.setContent(
							this,
							R.id.qmb,
							data.optJSONObject("discuss").optString(
									"display_name"));
				}
				ViewUtil.setContent(this, R.id.qcy_text,
						data.optJSONObject("discuss").optString("userNumber")
								+ "人");
			}
		}
	}
}
