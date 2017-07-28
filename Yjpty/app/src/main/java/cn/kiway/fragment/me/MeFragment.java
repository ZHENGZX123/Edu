package cn.kiway.fragment.me;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.me.EditAeraActivity;
import cn.kiway.activity.me.EditNameActivity;
import cn.kiway.activity.me.EditSexActivity;
import cn.kiway.activity.me.setting.SettingActivity;
import cn.kiway.dialog.picture.SelectPictureDialog;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.http.UploadFile;
import cn.kiway.http.UploadFile.UploadCallBack;
import cn.kiway.message.model.MessageProvider;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.Logger;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class MeFragment extends BaseFragment implements UploadCallBack {
	/**
	 * 相片地址
	 * */
	String picPath, picPath1;
	/**
	 * 选择相片弹框
	 * */
	SelectPictureDialog selectPictureDialog;
	ImageView imageView;

	JSONObject userdata;

	public MeFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
		view.findViewById(R.id.name).setOnClickListener(this);
		view.findViewById(R.id.area).setOnClickListener(this);
		view.findViewById(R.id.sex).setOnClickListener(this);
		view.findViewById(R.id.head_protrait).setOnClickListener(this);
		view.findViewById(R.id.setting).setOnClickListener(this);
		selectPictureDialog = new SelectPictureDialog(activity, this);
		imageView = ViewUtil.findViewById(view, R.id.head_img);
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
					// 头像
					StringUtil.imgUrl(activity, userdata.optString("photo")),
					imageView, activity.fadeOptions);
			activity.app.setAvatar(userdata.optString("photo"));
			if (!data.optString("realname").equals("null")) {// 名字
				ViewUtil.setContent(activity, R.id.name_ovl,
						userdata.optString("realname"));
			}
			if (!userdata.optString("sex").equals("null")) {// 性别
				switch (StringUtil.toInt(userdata.optString("sex"))) {
				case 1:
					ViewUtil.setContent(activity, R.id.sex_ovl, R.string.boy);
					break;
				case 2:
					ViewUtil.setContent(activity, R.id.sex_ovl, R.string.gril);
					break;
				}
			}
			if (!userdata.optString("county_id").equals("null")) {// 地区
				ViewUtil.setContent(activity, R.id.area_ovl, userdata
						.optString("county_id").replace("*", " "));
			}
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Bundle b = new Bundle();
		switch (v.getId()) {
		case R.id.name:
			if (userdata == null)
				return;
			b.putString(IConstant.BUNDLE_PARAMS,
					ViewUtil.getContent(activity, R.id.name_ovl));
			b.putBoolean(IConstant.BUNDLE_PARAMS1, true);
			Intent intent = new Intent(activity, EditNameActivity.class);
			intent.putExtras(b);
			startActivityForResult(intent, 1);
			break;
		case R.id.sex:
			if (userdata == null)
				return;
			b.putInt(IConstant.BUNDLE_PARAMS, 0);
			if (ViewUtil.getContent(activity, R.id.sex_ovl).equals(
					activity.resources.getString(R.string.boy))) {
				b.putInt(IConstant.BUNDLE_PARAMS, 1);
			} else {
				b.putInt(IConstant.BUNDLE_PARAMS, 2);
			}
			Intent intent2 = new Intent(activity, EditSexActivity.class);
			intent2.putExtras(b);
			startActivityForResult(intent2, 2);
			break;
		case R.id.area:
			if (userdata == null)
				return;
			b.putBoolean(IConstant.BUNDLE_PARAMS, true);
			Intent intent3 = new Intent(activity, EditAeraActivity.class);
			intent3.putExtras(b);
			startActivityForResult(intent3, 3);
			break;
		case R.id.head_protrait:
			if (userdata == null)
				return;
			picPath = AppUtil.createNewPhoto();
			picPath1 = AppUtil.createNewPhoto();
			selectPictureDialog.setPicPath(picPath);
			if (selectPictureDialog != null && !selectPictureDialog.isShowing()) {
				selectPictureDialog.show();
			}
			break;
		case R.id.setting:
			activity.startActivity(SettingActivity.class);
			break;
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(final int requstCode, int resultCode,
			final Intent data) {
		super.onActivityResult(requstCode, resultCode, data);
		if (resultCode != activity.RESULT_OK)
			return;
		if (requstCode == 1) {// 名字
			ViewUtil.setContent(view, R.id.name_ovl,
					data.getStringExtra(IConstant.BUNDLE_PARAMS));
		} else if (requstCode == 2) {// 性别
			if (StringUtil.toInt(data.getStringExtra(IConstant.BUNDLE_PARAMS)) == 1) {
				ViewUtil.setContent(view, R.id.sex_ovl, R.string.boy);
			} else {
				ViewUtil.setContent(view, R.id.sex_ovl, R.string.gril);
			}

		} else if (requstCode == 3) {// 地区
			ViewUtil.setContent(view, R.id.area_ovl,
					data.getStringExtra(IConstant.BUNDLE_PARAMS));
		}
		try {
			IConstant.executorService.execute(new Runnable() {// 头像
						public void run() {
							switch (requstCode) {
							case IConstant.FOR_CAMERA:// 拍照
								AppUtil.performCrop(picPath, picPath1,
										IConstant.FOR_CROP, activity,
										MeFragment.this);
								break;
							case IConstant.FOR_PHOTO:// 相册
								Uri uri = data.getData();
								if (uri != null) {
									ContentResolver contentResolver = activity
											.getContentResolver();
									String path = StringUtil.getPicPath(uri,
											contentResolver);
									AppUtil.performCrop(path, picPath1,
											IConstant.FOR_CROP, activity,
											MeFragment.this);
								}
								break;
							case IConstant.FOR_CROP:// 切图
								activity.runOnUiThread(new Runnable() {
									public void run() {
										activity.imageLoader
												.displayImage(
														"file://" + picPath1,
														(ImageView) activity
																.findViewById(R.id.head_img),
														activity.options);
									}
								});
								if (picPath != null) {
									new Thread(networkTask).start();
								}
								break;
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上传头像
	 * */
	Runnable networkTask = new Runnable() {
		@Override
		public void run() {
			File f = new File(picPath1);
			if (!f.exists()) {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						ViewUtil.showMessage(activity, "上传失败");
					}
				});
				return;
			}
			Map<String, File> upfiles = new HashMap<String, File>();
			upfiles.put(f.getName(), f);
			Logger.log("图像地址:::::::" + f);
			try {
				String userId = URLEncoder.encode(activity.app.getUid() + "",
						"utf-8");
				Map<String, String> params = new HashMap<String, String>();
				params.put("userId", userId);
				UploadFile.post(activity, IUrContant.UPDATEUSERINFO_URL,
						params, upfiles, MeFragment.this, activity.app);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

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

	@Override
	public void uploadCallBack(String data, String actionUrl) throws Exception {
		final JSONObject da = new JSONObject(data);
		if (da.optInt("retcode") == 1) {
			activity.runOnUiThread(new Runnable() {
				public void run() {
					ViewUtil.showMessage(activity, R.string.xgcg);
					activity.mCache.put(IUrContant.GET_MY_INFO_URL
							+ activity.app.getUid(), da);
					ContentValues values = new ContentValues();// 成功后手动更新本地聊天头像
					values.put("_url",
							da.optJSONObject("userInfo").optString("photo"));
					activity.getContentResolver().update(
							Uri.parse(MessageProvider.MESSAGES_URL), values,
							"_uid=?",
							new String[] { "" + activity.app.getUid() });
				}
			});
		}
	}
}
