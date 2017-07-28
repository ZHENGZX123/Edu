package cn.kiway.dialog;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.common.MipcaCaptureActivity;
import cn.kiway.http.BaseHttpHandler;
import cn.kiway.http.HttpHandler;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.utils.ViewUtil;

public class WebLoginDailog extends BaseDialog implements HttpHandler,
		OnShowListener {
	/**
	 * 请求回调
	 */
	protected BaseHttpHandler dialogHandler = new BaseHttpHandler(this) {
	};
	String logintext;
	String tokenId;
	MipcaCaptureActivity activity;

	public void setLoginText(String logintext) {
		this.logintext = logintext;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public WebLoginDailog(Context context) {
		super(context);
		this.activity = (MipcaCaptureActivity) context;
		view = ViewUtil.inflate(context, R.layout.dialog_web_login);
		setContentView(view, layoutParams);
		view.findViewById(R.id.login).setOnClickListener(this);
		view.findViewById(R.id.exit).setOnClickListener(this);
		fullWindowWH(context);
		setOnShowListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.login:
			if (ViewUtil.getContent(view, R.id.login).equals("重新扫描")) {
				dismiss();
				return;
			}
			HashMap<String, String> map = new HashMap<>();
			map.put("tokenId", tokenId);
			IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.CONFIRMLOGIN_URL,
					map, dialogHandler);
			break;
		case R.id.exit:
			HashMap<String, String> map1 = new HashMap<>();
			map1.put("tokenId", tokenId);
			IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.SCAN_CANCLE_URL,
					map1, dialogHandler);
			dismiss();
			break;
		}
	}

	@Override
	public void httpErr(HttpResponseModel message) throws Exception {

	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		if (message.getUrl().equals(IUrContant.CONFIRMLOGIN_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				ViewUtil.showMessage(activity, "登录成功");
				activity.finish();
				dismiss();
			} else {
				view.findViewById(R.id.error_login).setVisibility(View.VISIBLE);
				ViewUtil.setContent(view, R.id.login, "重新扫描");
			}
		}
	}

	@Override
	public void HttpError(HttpResponseModel message) throws Exception {

	}

	@Override
	public void onShow(DialogInterface dialog) {
		ViewUtil.setContent(view, R.id.login, logintext);
		if (logintext.equals("重新扫描")) {
			view.findViewById(R.id.error_login).setVisibility(View.VISIBLE);
			view.findViewById(R.id.exit).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.error_login).setVisibility(View.GONE);
			view.findViewById(R.id.exit).setVisibility(View.VISIBLE);
		}
	}
}
