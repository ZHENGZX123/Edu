package cn.kiway.login;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.MainActivity;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class LoadingActivity extends BaseActivity {
	ImageView img;
	int errorNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void initView() throws Exception {
		findViewById(R.id.login).setOnClickListener(this);
		findViewById(R.id.resiger).setOnClickListener(this);
		findViewById(R.id.fooget_password).setOnClickListener(this);
		img = ViewUtil.findViewById(this, R.id.img);
		findViewById(R.id.img).setOnClickListener(this);
		ViewUtil.setContent(this, R.id.send, "更换验证码");
		app.setInit(false);
		findViewById(R.id.fooget_password).setVisibility(View.VISIBLE);
		findViewById(R.id.send).setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
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
					.getContent(this, R.id.user_password))) {// 密码为空
				ViewUtil.setContent(this, R.id.info, R.string.password);
				findViewById(R.id.info).setVisibility(View.VISIBLE);
				return;
			}
			if (errorNum >= 3
					&& StringUtil.isEmpty(ViewUtil.getContent(this,
							R.id.user_core))) {// 验证码为空
				ViewUtil.setContent(this, R.id.info, R.string.core);
				findViewById(R.id.info).setVisibility(View.VISIBLE);
				return;
			}
			Map<String, String> map = new HashMap<>();
			map.put("userName", ViewUtil.getContent(this, R.id.user_name));
			map.put("password", ViewUtil.getContent(this, R.id.user_password));
			map.put("code", ViewUtil.getContent(this, R.id.user_core));
			map.put("type", "2");
			IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.LOGIN_URL, map,
					activityHandler, true);
			break;
		case R.id.resiger:
			startActivity(ResigerActivity.class);
			break;
		case R.id.img:
			IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.MEASSGE_URL,
					null, activityHandler);
			break;
		case R.id.fooget_password:
			startActivity(ForgetPasswordActivity.class);
			break;
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.LOGIN_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null) {
				if (data.optInt("retcode") == 1) {
					mCache.put(IUrContant.LOGIN_URL, data);
					app.setUid(data.optInt("userId"));
					finishAllAct();
					finish();
					startActivity(MainActivity.class);
					SharedPreferencesUtil.save(this, IConstant.USER_NAME,
							ViewUtil.getContent(this, R.id.user_name));
					SharedPreferencesUtil.save(this, IConstant.PASSWORD,
							ViewUtil.getContent(this, R.id.user_password));
				} else if (data.optInt("retcode") == 11) {
					if (data.optInt("errorNum") >= 3) {
						IConstant.HTTP_CONNECT_POOL.addRequest(
								IUrContant.MEASSGE_URL, null, activityHandler);
						if (StringUtil.isEmpty(ViewUtil.getContent(this,
								R.id.user_core))) {
							ViewUtil.setContent(this, R.id.info,
									"错误次数太多，请输入验证码");
							findViewById(R.id.info).setVisibility(View.VISIBLE);
						} else {
							ViewUtil.setContent(this, R.id.info, "密码或用户名错误");
							findViewById(R.id.info).setVisibility(View.VISIBLE);
						}
						findViewById(R.id.layouts).setVisibility(View.VISIBLE);
					} else {
						ViewUtil.setContent(this, R.id.info, "用户名或密码错误");
						findViewById(R.id.info).setVisibility(View.VISIBLE);
					}
				} else if (data.optInt("retcode") == 22) {
					ViewUtil.setContent(this, R.id.info, "验证码错误");
					findViewById(R.id.info).setVisibility(View.VISIBLE);
					findViewById(R.id.layouts).setVisibility(View.VISIBLE);
				} else if (data.optInt("retcode") == 33) {
					ViewUtil.setContent(this, R.id.info, "你还没通过审核，请耐心等候");
					findViewById(R.id.info).setVisibility(View.VISIBLE);
				}
			}
		} else if (message.getUrl().equals(IUrContant.MEASSGE_URL)) {
			Bitmap btp = BitmapFactory.decodeStream(new ByteArrayInputStream(
					message.getResponse()));
			img.setImageBitmap(btp);
			btp = null;
		}
	}

	@Override
	public void httpErr(HttpResponseModel message) throws Exception {
		super.httpErr(message);
	}
}
