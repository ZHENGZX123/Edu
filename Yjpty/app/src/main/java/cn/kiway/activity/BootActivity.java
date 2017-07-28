package cn.kiway.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.login.LoadingActivity;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.ViewUtil;

public class BootActivity extends BaseActivity {
	Handler handler;
	boolean isOnline;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		app.setInit(false);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					if (!SharedPreferencesUtil.getBoolean(context,//判断是否为首次打开
							IConstant.NEW_VERSION)) {
						SharedPreferencesUtil.save(context,
								IConstant.NEW_VERSION, true);
						//startActivity(GuideActivity.class);进去引导页，这边先取消
						startActivity(LoadingActivity.class);
						finish();
					} else if (!SharedPreferencesUtil.getString(//登录请求  判断是否有用户名密码
							BootActivity.this, IConstant.USER_NAME).equals("")
							&& !SharedPreferencesUtil.getString(
									BootActivity.this, IConstant.PASSWORD)
									.equals("")) {
						isOnline = true;
						Map<String, String> map = new HashMap<>();
						map.put("userName", SharedPreferencesUtil.getString(
								BootActivity.this, IConstant.USER_NAME));
						map.put("password", SharedPreferencesUtil.getString(
								BootActivity.this, IConstant.PASSWORD));
						map.put("type", "1");
						map.put("code", null);
						IConstant.HTTP_CONNECT_POOL.addRequest(
								IUrContant.LOGIN_URL, map, activityHandler,
								false);
					} else {
						startActivity(LoadingActivity.class);
						finish();
					}
					break;
				case 1:
					if (SharedPreferencesUtil.getString(BootActivity.this,
							IConstant.USER_NAME).equals("")
							|| SharedPreferencesUtil.getString(
									BootActivity.this, IConstant.PASSWORD)
									.equals("")) {
						startActivity(LoadingActivity.class);
						finish();
					} else {
						startActivity(MainActivity.class);
					}
					break;
				}
			}
		};
		if (AppUtil.isNetworkAvailable(this)) {//判断是否有网，进入不同的请求
			handler.sendEmptyMessageDelayed(0, 2000);
		} else {
			handler.sendEmptyMessageDelayed(1, 2000);
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.LOGIN_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null) {
				if (data.optInt("retcode") == 1 && isOnline) {
					app.setUid(data.optInt("userId"));
					app.setName(data.optString("userName"));
					finish();
					finishAllAct();
					startActivity(MainActivity.class);
					mCache.put(IUrContant.LOGIN_URL, data);
					isOnline = false;
				} else if (data.optInt("retcode") == 11
						|| data.optInt("retcode") == 33) {
					if (SharedPreferencesUtil.getBoolean(context,
							IConstant.NEW_VERSION))
						ViewUtil.showMessage(this, "网络异常,请重新登录");
					finish();
					finishAllAct();
					startActivity(LoadingActivity.class);
				}
			}
		}
	}

	@Override
	public void HttpError(HttpResponseModel message) throws Exception {
		super.HttpError(message);
		JSONObject data = mCache.getAsJSONObject(IUrContant.LOGIN_URL);
		if (data != null) {
			app.setUid(data.optInt("userId"));
			app.setName(data.optString("userName"));
			finishAllAct();
			finish();
			startActivity(MainActivity.class);
		} else {
			ViewUtil.showMessage(this, "网络异常,请重新登陆");
			finishAllAct();
			finish();
			startActivity(LoadingActivity.class);
		}
	}
}
