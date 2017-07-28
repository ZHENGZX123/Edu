package cn.kiway.fragment.me;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.choosebaby.EditTeacherPhoneActivity;
import cn.kiway.activity.me.MyBabyInfoActivity;
import cn.kiway.activity.me.growth.GrowthActivity;
import cn.kiway.activity.me.profile.PrifileActivity;
import cn.kiway.activity.me.setting.SettingActivity;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class MeFragment extends BaseFragment {
	ImageView userImg;
	JSONObject userdata;

	public MeFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = ViewUtil.inflate(activity, R.layout.fragment_me);
		try {
			initView();
			setData();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	void initView() throws Exception {
		view.findViewById(R.id.head_protrait).setOnClickListener(this);
		view.findViewById(R.id.scan).setOnClickListener(this);
		view.findViewById(R.id.my_baby).setOnClickListener(this);
		view.findViewById(R.id.setting).setOnClickListener(this);
		view.findViewById(R.id.grally).setOnClickListener(this);
		userImg = ViewUtil.findViewById(view, R.id.profile);
	}

	@Override
	public void loadData() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("userId", activity.app.getUid() + "");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_MY_INFO_URL, map,
				fragmentHandler);
	}

	void setData() throws Exception {
		JSONObject data = activity.mCache
				.getAsJSONObject(IUrContant.GET_MY_INFO_URL
						+ activity.app.getUid());
		if (data != null) {
			userdata = data.optJSONObject("userInfo");
			activity.imageLoader.displayImage(
					StringUtil.imgUrl(activity, userdata.optString("photo")),
					userImg, activity.fadeOptions);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.head_protrait:
			if (userdata == null)
				return;
			Intent intent = new Intent(activity, PrifileActivity.class);// 修改个人信息
			startActivityForResult(intent, 1);
			break;
		case R.id.scan:
			activity.startActivity(EditTeacherPhoneActivity.class);// 扫描二维码
			break;
		case R.id.my_baby:
			if (activity.app.getBoyModels().size() <= 0) {
				ViewUtil.showMessage(activity, R.string.nhmybdbb);
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putInt(IConstant.BUNDLE_PARAMS, (int) activity.app
					.getBoyModels().get(activity.app.getPosition())
					.getChildId());
			bundle.putBoolean(IConstant.BUNDLE_PARAMS1, false);
			bundle.putString(IConstant.BUNDLE_PARAMS2, activity.app
					.getBoyModels().get(activity.app.getPosition())
					.getClassName());
			activity.startActivity(MyBabyInfoActivity.class, bundle);// 我的宝贝
			break;
		case R.id.setting:
			activity.startActivity(SettingActivity.class);// 设置
			break;
		case R.id.grally:
			if (activity.app.getBoyModels().size() <= 0) {
				ViewUtil.showMessage(activity, R.string.nhmybdbb);
				return;
			}
			Bundle bundle2 = new Bundle();
			if (userdata != null)
				bundle2.putString(IConstant.BUNDLE_PARAMS,
						userdata.optString("photo"));
			else
				bundle2.putString(IConstant.BUNDLE_PARAMS, "");
			activity.startActivity(GrowthActivity.class, bundle2);// 我的相册
			break;
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.GET_MY_INFO_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				activity.mCache.put(
						IUrContant.GET_MY_INFO_URL + activity.app.getUid(),
						data);
				setData();
			}
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(final int requstCode, int resultCode,
			final Intent data) {
		super.onActivityResult(requstCode, resultCode, data);
		if (resultCode != activity.RESULT_OK)
			return;
		if (data != null) {
			final String url = data.getStringExtra(IConstant.BUNDLE_PARAMS);
			activity.runOnUiThread(new Runnable() {
				public void run() {
					activity.imageLoader.displayImage("file://" + url, userImg,
							activity.fadeOptions);
				}
			});
		}
		try {
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
