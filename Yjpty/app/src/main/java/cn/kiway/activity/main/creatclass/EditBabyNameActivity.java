package cn.kiway.activity.main.creatclass;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import cn.kiway.message.model.MessageChatProvider;
import cn.kiway.model.BoyModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class EditBabyNameActivity extends BaseNetWorkActicity implements
		TextWatcher {
	EditText editText;
	BoyModel boyModel;
	boolean b;// t 修改名字 f修改班级名字

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		b = bundle.getBoolean(IConstant.BUNDLE_PARAMS1);
		if (!AppUtil.isNetworkAvailable(this)) {
			newWorkdialog = new IsNetWorkDialog(context, this,
					resources.getString(R.string.dqsjmylrhlwqljhlwl),
					resources.getString(R.string.ljhlw));
			if (newWorkdialog != null && !newWorkdialog.isShowing()) {
				newWorkdialog.show();
				return;
			}
		}
		setContentView(R.layout.activity_edit_name);
		if (b) {
			boyModel = (BoyModel) bundle
					.getSerializable(IConstant.BUNDLE_PARAMS);
		}
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("boyModel", boyModel);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("boyModel"))
			boyModel = (BoyModel) savedInstanceState
					.getSerializable("boyModel");
	}

	@Override
	public void initView() throws Exception {
		editText = ViewUtil.findViewById(this, R.id.notify_edit);
		editText.addTextChangedListener(this);
		findViewById(R.id.sure).setOnClickListener(this);
		int text_length = 10 - editText.length();
		ViewUtil.setContent(this, R.id.text_number, "还可以输入" + text_length + "字");
		if (!b) {
			ViewUtil.setContent(editText, app.getClassModel().getClassName());
			ViewUtil.setContent(this, R.id.title, "班级名字");
		} else {
			ViewUtil.setContent(editText, boyModel.getName());
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.sure:
			if (ViewUtil.getContent(editText) == null
					|| StringUtil.isEmpty(editText.getText().toString())) {
				ViewUtil.showMessage(this, "名字不能为空");
				return;
			}
			if (b) {
				Map<String, String> map = new HashMap<>();
				map.put("childId", boyModel.getUid() + "");
				map.put("childName", ViewUtil.getContent(editText));
				IConstant.HTTP_CONNECT_POOL.addRequest(
						IUrContant.UPDATE_BABY_INFO_URL, map, activityHandler,
						true);
			} else {
				Map<String, String> map = new HashMap<>();
				map.put("schoolId", app.getClassModel().getSchoolId() + "");
				map.put("classId", app.getClassModel().getId() + "");
				map.put("className", ViewUtil.getContent(editText));
				IConstant.HTTP_CONNECT_POOL.addRequest(
						IUrContant.UPDATE_CLASS_INFO, map, activityHandler,
						true);
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
		if (message.getUrl().equals(IUrContant.UPDATE_BABY_INFO_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				Intent da = getIntent();
				da.putExtra(IConstant.BUNDLE_PARAMS,
						ViewUtil.getContent(this, R.id.notify_edit));
				setResult(RESULT_OK, da);
				finish();
			} else if (data.optInt("retcode") == 0) {
				ViewUtil.showMessage(this, "修改失败");
			}
		} else if (message.getUrl().equals(IUrContant.UPDATE_CLASS_INFO)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				app.getClassModel().setClassName(ViewUtil.getContent(editText));
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						ContentValues values = new ContentValues();
						values.put("_name", ViewUtil.getContent(editText));
						getContentResolver()
								.update(Uri
										.parse(MessageChatProvider.MESSAGECHATS_URL),
										values,
										"_touid=?",
										new String[] { ""
												+ app.getClassModel().getId()
												+ "" });
					}
				}, 500);
				ClassInfoActivity.isLoad = true;
				finish();
			} else if (data.optInt("retcode") == 0) {
				ViewUtil.showMessage(this, "修改失败");
			} else if (data.optInt("retcode") == 22) {
				ViewUtil.showMessage(this, "班级名字已存在,请更换名字");
			}
		}
	}
}
