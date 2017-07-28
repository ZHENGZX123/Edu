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
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class ResigerActivity extends BaseActivity {
	EditText userCore;

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
			h.sendEmptyMessage(0);
		}
	}

	@Override
	public void initView() throws Exception {
		findViewById(R.id.resiger).setVisibility(View.GONE);
		findViewById(R.id.previos).setVisibility(View.VISIBLE);
		findViewById(R.id.layouts).setVisibility(View.VISIBLE);
		ViewUtil.setContent(this, R.id.login, R.string.zhuce);
		ViewUtil.setContent(this, R.id.title, R.string.zhuce);
		userCore = ViewUtil.findViewById(this, R.id.user_core);
		userCore.setHint(resources.getString(R.string.qingsryz));
		findViewById(R.id.send).setOnClickListener(this);
		findViewById(R.id.login).setOnClickListener(this);
		findViewById(R.id.scan).setVisibility(View.GONE);
		app.setInit(false);
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
					R.id.user_name))) {// 用户名为空
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
			param.put("type", "1");
			IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.REGIGER_URL,
					param, activityHandler, true);
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
					ViewUtil.setContent(ResigerActivity.this, R.id.send,
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
					ViewUtil.setContent(ResigerActivity.this, R.id.send,
							R.string.yanzhengma);
				}
				break;
			}
		}
	};

	public void httpSuccess(HttpResponseModel message) throws Exception {
		if (message.getUrl().equals(IUrContant.REGIGER_URL)) {// 注册成功
			JSONObject data = new JSONObject(new String(message.getResponse()));
			time = 0;
			if (data != null) {
				if (data.optInt("retcode") == 1) {
					finishAllAct();
					finish();
					app.setUid(data.optInt("userId"));
					Bundle bundle = new Bundle();
					bundle.putInt(IConstant.BUNDLE_PARAMS, 1);// 1注册进去，2登录后创建班级进入
					startActivity(PrifileNameActivity.class, bundle);
				} else if (data.optInt("retcode") == 22) {
					ViewUtil.setContent(this, R.id.info, "用户名已被注册");
					findViewById(R.id.info).setVisibility(View.VISIBLE);
				}
			}
		} else if (message.getUrl().equals(IUrContant.VALIDATE_URL)) {// 获取验证码成功
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null) {
				if (data.optInt("retcode") == 1) {
					time = System.currentTimeMillis();
					h.sendEmptyMessage(0);
				} else {
					ViewUtil.showMessage(this, "发送失败，请稍后再试");
				}
			}
		}
	};

}
