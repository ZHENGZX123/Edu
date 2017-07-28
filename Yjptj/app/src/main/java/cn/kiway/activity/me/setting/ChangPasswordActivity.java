package cn.kiway.activity.me.setting;

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
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class ChangPasswordActivity extends BaseActivity implements TextWatcher {
	EditText oldPw, newPw, new2Pw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chang_password);
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void initView() throws Exception {
		oldPw = ViewUtil.findViewById(this, R.id.old_password);
		newPw = ViewUtil.findViewById(this, R.id.new_password);
		new2Pw = ViewUtil.findViewById(this, R.id.new_password_again);
		oldPw.addTextChangedListener(this);
		newPw.addTextChangedListener(this);
		new2Pw.addTextChangedListener(this);
		findViewById(R.id.login).setOnClickListener(this);
		findViewById(R.id.login).setEnabled(false);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.login:
			Map<String, String> map = new HashMap<>();
			map.put("oldPassword", oldPw.getText().toString());
			map.put("newPassword", new2Pw.getText().toString());
			IConstant.HTTP_CONNECT_POOL.addRequest(
					IUrContant.CHANGE_PASSWORD_URL, map, activityHandler);
			break;
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.CHANGE_PASSWORD_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				ViewUtil.showMessage(this, "修改成功");
				SharedPreferencesUtil.save(this, IConstant.PASSWORD, new2Pw
						.getText().toString());
				finish();
			} else if (data.optInt("retcode") == 22) {
				ViewUtil.showMessage(this, "旧密码错误");
			} else if (data.optInt("retcode") == 0) {
				ViewUtil.showMessage(this, "服务器出错");
			}
		}
	}

	@Override
	public void afterTextChanged(Editable edit) {
		if (StringUtil.isNotEmpty(oldPw.getText().toString())
				&& !oldPw
						.getText()
						.toString()
						.equals(SharedPreferencesUtil.getString(this,
								IConstant.PASSWORD))) {
			ViewUtil.setContent(this, R.id.message, "旧密码错误");
		} else if (StringUtil.isNotEmpty(new2Pw.getText().toString())
				&& StringUtil.isNotEmpty(newPw.getText().toString())
				&& !newPw.getText().toString()
						.equals(new2Pw.getText().toString())) {
			ViewUtil.setContent(this, R.id.message, "两次新密码输入不一致");
		}
		if (StringUtil.isNotEmpty(oldPw.getText().toString())
				&& !oldPw
						.getText()
						.toString()
						.equals(SharedPreferencesUtil.getString(this,
								IConstant.PASSWORD))
				&& StringUtil.isEmpty(new2Pw.getText().toString())
				&& StringUtil.isEmpty(newPw.getText().toString())
				|| !newPw.getText().toString()
						.equals(new2Pw.getText().toString())) {
			findViewById(R.id.login).setEnabled(false);
		} else {
			findViewById(R.id.login).setEnabled(true);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		ViewUtil.setContent(this, R.id.message, "");
	}
}
