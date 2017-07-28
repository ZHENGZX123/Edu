package cn.kiway.login;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class ForgetPasswordActivity extends BaseActivity {
	EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (time > 0) {
			time = -1;
			h.sendEmptyMessage(0);
		}
	}

	void initView() throws Exception {
		findViewById(R.id.resiger).setVisibility(View.GONE);
		findViewById(R.id.previos).setVisibility(View.VISIBLE);
		findViewById(R.id.layouts).setVisibility(View.VISIBLE);
		ViewUtil.setContent(this, R.id.login, R.string.dialog_yes_button);
		ViewUtil.setContent(this, R.id.title, R.string.forget_password_t);
		findViewById(R.id.send).setOnClickListener(this);
		findViewById(R.id.login).setOnClickListener(this);
		app.setInit(false);
		editText = ViewUtil.findViewById(this, R.id.user_password);
		editText.setHint("请输入新密码");
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.send:
			if (!StringUtil.isMobileNum(ViewUtil.getContent(this,
					R.id.user_name))) {
				ViewUtil.setContent(this, R.id.info, R.string.right_tellphone);
				findViewById(R.id.info).setVisibility(View.VISIBLE);
				return;
			}
			if (findViewById(R.id.info).getVisibility() == View.VISIBLE)
				findViewById(R.id.info).setVisibility(View.GONE);
			Map<String, String> map = new HashMap<>();
			map.put("phone", ViewUtil.getContent(this, R.id.user_name));
			IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.VALIDATE_URL,
					map, activityHandler);
			break;
		case R.id.login:
			ViewUtil.hideKeyboard(this);
			if (findViewById(R.id.info).getVisibility() == View.VISIBLE)
				findViewById(R.id.info).setVisibility(View.GONE);
			if (!StringUtil.isMobileNum(ViewUtil.getContent(this,
					R.id.user_name))) {
				ViewUtil.setContent(this, R.id.info, R.string.right_tellphone);
				findViewById(R.id.info).setVisibility(View.VISIBLE);
				return;
			}
			if (StringUtil.isEmpty(ViewUtil
					.getContent(this, R.id.user_password))) {
				ViewUtil.setContent(this, R.id.info, R.string.password);
				findViewById(R.id.info).setVisibility(View.VISIBLE);
				return;
			}
			if (StringUtil.isEmpty(ViewUtil.getContent(this, R.id.user_core))) {// 验证码为空
				ViewUtil.setContent(this, R.id.info, R.string.core);
				findViewById(R.id.info).setVisibility(View.VISIBLE);
				return;
			}
			Map<String, String> param = new HashMap<>();
			param.put("phone", ViewUtil.getContent(this, R.id.user_name));
			param.put("password", ViewUtil.getContent(this, R.id.user_password));
			param.put("vm", ViewUtil.getContent(this, R.id.user_core));
			param.put("type", "2");
			IConstant.HTTP_CONNECT_POOL
					.addRequest(IUrContant.FORGETPASSWORD_URL, param,
							activityHandler, true);
			break;
		}
	}

	static long time;
	SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.getDefault());
	@SuppressLint("HandlerLeak")
	Handler h = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				long t = System.currentTimeMillis();
				if (t - time < 60 * 1000) {
					ViewUtil.setContent(ForgetPasswordActivity.this, R.id.send,
							sdf.format(new Date((time + 60 * 1000) - t)));
					findViewById(R.id.send).setEnabled(false);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							h.sendEmptyMessage(0);
						}
					}, 1000);
				} else {// 按钮可用
					time = -1;
					findViewById(R.id.send).setEnabled(true);
					ViewUtil.setContent(ForgetPasswordActivity.this, R.id.send,
							R.string.yanzhengma);
				}
				break;
			}
		}
	};

	public void httpSuccess(HttpResponseModel message) throws Exception {
		if (message.getUrl().equals(IUrContant.FORGETPASSWORD_URL)) {// 注册成功
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null) {
				if (data.optInt("retcode") == 1) {
					ViewUtil.showMessage(this, "修改成功");
					finish();
				} else if (data.optInt("retcode") == 22) {
					ViewUtil.setContent(this, R.id.info, "用户不存在");
					findViewById(R.id.info).setVisibility(View.VISIBLE);
				} else if (data.optInt("retcode") == 11) {
					ViewUtil.setContent(this, R.id.info, "验证码错误");
					findViewById(R.id.info).setVisibility(View.VISIBLE);
				}
			}
			time = -1;
			h.sendEmptyMessage(0);
		} else if (message.getUrl().equals(IUrContant.VALIDATE_URL)) {// 获取验证码成功
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null) {
				if (data.optInt("retcode") == 1) {
					time = System.currentTimeMillis();
					h.sendEmptyMessage(0);
				}
			}
		}
	};
}
