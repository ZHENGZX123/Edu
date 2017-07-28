package cn.kiway.activity.me;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class EditNameActivity extends BaseNetWorkActicity implements
		TextWatcher {
	EditText editText;
	boolean isEditName;// 是否为修改名字 在群名片也调用到这里，所以做下判断

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isEditName = bundle.getBoolean(IConstant.BUNDLE_PARAMS1);
		setContentView(R.layout.activity_edit_name);
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initView() throws Exception {
		editText = ViewUtil.findViewById(this, R.id.notify_edit);
		editText.addTextChangedListener(this);
		findViewById(R.id.sure).setOnClickListener(this);
		ViewUtil.setContent(this, R.id.notify_edit,
				bundle.getString(IConstant.BUNDLE_PARAMS));
		int text_length = 10 - editText.length();
		ViewUtil.setContent(this, R.id.text_number, "还可以输入" + text_length + "字");
		ViewUtil.setEditTexMarkerPosition(editText);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.sure:
			if (!AppUtil.isNetworkAvailable(this)
					&& mCache.getAsJSONObject(IUrContant.GET_GROWTH_LIST_URL
							+ app.getClassModel().getId()) == null) {
				newWorkdialog = new IsNetWorkDialog(context, this,
						resources.getString(R.string.dqsjmylrhlwqljhlwl),
						resources.getString(R.string.ljhlw));
				if (newWorkdialog != null && !newWorkdialog.isShowing()) {
					newWorkdialog.show();
					return;
				}
			}
			if (!StringUtil.isNotEmpty(ViewUtil.getContent(this,
					R.id.notify_edit))) {
				ViewUtil.showMessage(this, "名字不能为空");
				return;
			}
			Map<String, String> map = new HashMap<>();
			if (isEditName) {
				map.put("userId", app.getUid() + "");
				map.put("realName", ViewUtil.getContent(editText));
				IConstant.HTTP_CONNECT_POOL.addRequest(
						IUrContant.UPDATEUSERINFO_URL, map, activityHandler,
						true);
			} else {
				MessageModel model = (MessageModel) bundle
						.getSerializable(IConstant.BUNDLE_PARAMS2);
				map.put("userId", app.getUid() + "");
				map.put("displayName",
						ViewUtil.getContent(this, R.id.notify_edit));
				if (model.getMsgType() == 2) {
					map.put("discussId", model.getToUid() + "");
					IConstant.HTTP_CONNECT_POOL.addRequest(
							IUrContant.EDIT_QMP_URL, map, activityHandler);
				} else {
					map.put("classId", model.getToUid() + "");
					IConstant.HTTP_CONNECT_POOL
							.addRequest(IUrContant.EDIT_CLASS_QMP_URL, map,
									activityHandler);
				}
			}
			break;
		}
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		int text_length = 10 - editText.length();
		ViewUtil.setContent(this, R.id.text_number, "还可以输入" + text_length + "字");
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.UPDATEUSERINFO_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				Intent da = getIntent();
				da.putExtra(IConstant.BUNDLE_PARAMS,
						ViewUtil.getContent(this, R.id.notify_edit));
				app.setName(ViewUtil.getContent(this, R.id.notify_edit));
				setResult(RESULT_OK, da);
				finish();
			} else {
				ViewUtil.showMessage(this, "修改失败");
			}
		} else if (message.getUrl().equals(IUrContant.EDIT_QMP_URL)
				|| message.getUrl().equals(IUrContant.EDIT_CLASS_QMP_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				Intent da = getIntent();
				da.putExtra(IConstant.BUNDLE_PARAMS,
						ViewUtil.getContent(this, R.id.notify_edit));
				setResult(RESULT_OK, da);
				finish();
			} else {
				ViewUtil.showMessage(this, "修改失败");
			}
		}
	}
}
