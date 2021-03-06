package cn.kiway.login;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.main.creatclass.JoinClassActivity;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.utils.ViewUtil;

public class PrifileNameActivity extends BaseActivity implements TextWatcher {
	EditText editText;
	int type;// 1为注册时候进入，2为登录时候检查没有用户名进入

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_name);
		type = bundle.getInt(IConstant.BUNDLE_PARAMS);
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
		int text_length = 10 - editText.length();
		ViewUtil.setContent(this, R.id.text_number, "还可以输入" + text_length + "字");
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.sure:
			if (ViewUtil.getContent(editText) == null) {
				ViewUtil.showMessage(this, "名字不能为空");
				return;
			}
			Map<String, String> map = new HashMap<>();
			map.put("userId", app.getUid() + "");
			map.put("realName", ViewUtil.getContent(editText));
			IConstant.HTTP_CONNECT_POOL.addRequest(
					IUrContant.UPDATEUSERINFO_URL, map, activityHandler, true);
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
				// if (type == 1) {
				Bundle bundle = new Bundle(); 
				bundle.putInt(IConstant.BUNDLE_PARAMS, 1);// 1注册进去，2登录后创建班级进入
				startActivity(JoinClassActivity.class, bundle);
				/*
				 * } else if (type == 2) { startActivity(MainActivity.class); }
				 */
			}
		}
	}
}
