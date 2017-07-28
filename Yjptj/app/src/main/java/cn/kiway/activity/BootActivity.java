package cn.kiway.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.login.LoadingActivity;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.ViewUtil;

public class BootActivity extends BaseActivity {
	Handler handler;
	static boolean isOnline;
	ImageView view;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		view = ViewUtil.findViewById(this, R.id.img);
		imageLoader.displayImage("drawable://" + R.drawable.login, view);
		app.setInit(false);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					if (!SharedPreferencesUtil.getBoolean(context,
							IConstant.NEW_VERSION)) {
						SharedPreferencesUtil.save(context,
								IConstant.NEW_VERSION, true);
						// startActivity(GuideActivity.class);进去引导页，这边先取消
						startActivity(LoadingActivity.class);
						finish();
					} else {
						if (!SharedPreferencesUtil.getString(BootActivity.this,
								IConstant.USER_NAME).equals("")
								|| !SharedPreferencesUtil.getString(
										BootActivity.this, IConstant.PASSWORD)
										.equals("")) {
							isOnline = true;
							Map<String, String> map = new HashMap<>();
							map.put("userName", SharedPreferencesUtil
									.getString(BootActivity.this,
											IConstant.USER_NAME));
							map.put("password", SharedPreferencesUtil
									.getString(BootActivity.this,
											IConstant.PASSWORD));
							map.put("type", "2");
							map.put("code", null);
							IConstant.HTTP_CONNECT_POOL.addRequest(
									IUrContant.LOGIN_URL, map, activityHandler);
						} else {
							startActivity(LoadingActivity.class);
							finish();
						}
					}
					break;
				case 1:
					if (!SharedPreferencesUtil.getBoolean(context,
							IConstant.NEW_VERSION)) {
						SharedPreferencesUtil.save(context,
								IConstant.NEW_VERSION, true);
						startActivity(GuideActivity.class);
						finish();
					} else {
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
					}
					break;
				}
			}
		};
		if (AppUtil.isNetworkAvailable(this))
			handler.sendEmptyMessageDelayed(0, 2000);
		else
			handler.sendEmptyMessageDelayed(1, 2000);
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.LOGIN_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null) {
				if (data.optInt("retcode") == 1 && isOnline) {
					mCache.put(IUrContant.LOGIN_URL, data);
					app.setUid(data.optInt("userId"));
					finishAllAct();
					finish();
					startActivity(MainActivity.class);
					isOnline = false;
				} else if (data.optInt("retcode") == 11) {
					finishAllAct();
					finish();
					ViewUtil.showMessage(this, "网络异常,请重新登陆");
					startActivity(LoadingActivity.class);
				}
			}
		}
	}

	@Override
	public void HttpError(HttpResponseModel message) throws Exception {
		super.HttpError(message);
		finishAllAct();
		finish();
		startActivity(MainActivity.class);
	}
}
