package cn.kiway.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.activity.BaseActivity;
import cn.kiway.http.BaseHttpHandler;
import cn.kiway.http.HttpHandler;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.utils.SharedPreferencesUtil;

/**
 * 帧布局基类
 * 
 * @author YI
 * */
public abstract class BaseFragment extends Fragment implements OnClickListener,
		HttpHandler {
	protected BaseActivity activity;
	protected View view;
	protected boolean isRefresh;
	protected BaseHttpHandler fragmentHandler = new BaseHttpHandler(this) {
	};

	public BaseFragment() {
		super();
	}

	@Override
	public  void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (BaseActivity) getActivity();
	}

	@Override
	public void onClick(View v) {

	}

	/**
	 * 数据的加载
	 * */
	public abstract void loadData() throws Exception;

	@Override
	public void httpErr(HttpResponseModel message) throws Exception {

	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		String string = new String(message.getResponse());
		if (message.getUrl().equals(IUrContant.LOGIN_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null) {
				if (data.optInt("retcode") == 1) {
					activity.app.setUid(data.optInt("userId"));
					activity.app.setName(data.optString("userName"));
					activity.mCache.put(IUrContant.LOGIN_URL, data);
				}
			}
		} else {
			if (!string.subSequence(0, 1).equals("{")
					&& !SharedPreferencesUtil.getString(activity,
							IConstant.USER_NAME).equals("")
					&& !message.getUrl().equals(IUrContant.MEASSGE_URL)) {
				Map<String, String> map = new HashMap<>();
				map.put("userName", SharedPreferencesUtil.getString(activity,
						IConstant.USER_NAME));
				map.put("password", SharedPreferencesUtil.getString(activity,
						IConstant.PASSWORD));
				map.put("type", "1");
				map.put("code", null);
				IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.LOGIN_URL,
						map, fragmentHandler, false);
			}
		}
	}

	@Override
	public void HttpError(HttpResponseModel message) throws Exception {
		//if (AppUtil.isNetworkAvailable(activity)) {
		//	ViewUtil.showMessage(activity, R.string.no_network);
		//}
	}

	public void hideFragment(FragmentTransaction transaction,
			List<Fragment> fragments) {
		if (transaction == null)
			return;
		if (fragments == null || fragments.size() == 0)
			return;
		for (Fragment fragment : fragments) {
			if (fragment != null && fragment.isVisible())
				transaction.hide(fragment);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
}
