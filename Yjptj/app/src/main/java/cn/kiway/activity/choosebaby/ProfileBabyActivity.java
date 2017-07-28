package cn.kiway.activity.choosebaby;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.activity.MainActivity;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.dialog.choosebaby.ChooseBabySexDialog;
import cn.kiway.dialog.choosebaby.ChooseDataDialog;
import cn.kiway.dialog.choosebaby.ChoosePraentDialog;
import cn.kiway.dialog.picture.SelectPictureDialog;
import cn.kiway.http.UploadFile;
import cn.kiway.http.UploadFile.UploadCallBack;
import cn.kiway.message.util.WriteMsgUitl;
import cn.kiway.model.BoyModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class ProfileBabyActivity extends BaseNetWorkActicity implements
		UploadCallBack {
	static BoyModel model;
	SelectPictureDialog dialog;
	/**
	 * 相片地址
	 * */
	String picPath, picPath1;
	ChooseBabySexDialog chooseBabySexDialog;
	ChoosePraentDialog choosePraentDialog;
	ChooseDataDialog chooseDataDialog;
	int sex = 1, praent = 1;
	ProgressDialog dialog2;

	public void setPraent(int praent) {
		this.praent = praent;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = (BoyModel) bundle.getSerializable(IConstant.BUNDLE_PARAMS);
		if (!AppUtil.isNetworkAvailable(this)) {
			newWorkdialog = new IsNetWorkDialog(context, this,
					resources.getString(R.string.dqsjmylrhlwqljhlwl),
					resources.getString(R.string.ljhlw));
			if (newWorkdialog != null && !newWorkdialog.isShowing()) {
				newWorkdialog.show();
				return;
			}
		}
		try {
			initView();
			setData();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void initView() throws Exception {
		if (model == null) {
			return;
		}
		setContentView(R.layout.activity_profile_baby);
		dialog = new SelectPictureDialog(this);
		chooseBabySexDialog = new ChooseBabySexDialog(this, sex);
		choosePraentDialog = new ChoosePraentDialog(this, praent);
		chooseDataDialog = new ChooseDataDialog(this);
		findViewById(R.id.profile).setOnClickListener(this);
		findViewById(R.id.send).setOnClickListener(this);
		findViewById(R.id.sex).setOnClickListener(this);
		findViewById(R.id.brithday).setOnClickListener(this);
		findViewById(R.id.who).setOnClickListener(this);
	}

	void setData() throws Exception {
		if (bundle.getBoolean(IConstant.BUNDLE_PARAMS1)) {
			ViewUtil.setContent(this, R.id.name, model.getName());
			ViewUtil.setContent(this, R.id.brithday_val, model.getBirthday());
			ViewUtil.setContent(this, R.id.title, model.getName());
			switch (model.getSex()) {
			case 1:
				ViewUtil.setContent(this, R.id.sex_val, R.string.boy);
				sex = 1;
				break;
			case 2:
				ViewUtil.setContent(this, R.id.sex_val, R.string.girl);
				sex = 2;
				break;
			}
			findViewById(R.id.who).setVisibility(View.GONE);
			imageLoader.displayImage(StringUtil.imgUrl(this, model.getUrl()),
					(ImageView) findViewById(R.id.profile), fadeOptions);
			ViewUtil.setContent(this, R.id.send, R.string.finish);
			findViewById(R.id.text).setVisibility(View.GONE);
		}
		imageLoader.displayImage(StringUtil.imgUrl(this, model.getUrl()),
				(ImageView) findViewById(R.id.profile), fadeOptions);
		ViewUtil.setContent(this, R.id.name, model.getName());
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.profile:
			picPath = AppUtil.createNewPhoto();
			picPath1 = AppUtil.createNewPhoto();
			dialog.setPicPath(picPath);
			if (dialog != null && !dialog.isShowing()) {
				dialog.show();
			}
			break;
		case R.id.sex:
			if (chooseBabySexDialog != null && !chooseBabySexDialog.isShowing()) {
				chooseBabySexDialog.show();
			}
			break;
		case R.id.brithday:
			if (chooseDataDialog != null && !chooseDataDialog.isShowing()) {
				chooseDataDialog.show();
			}
			break;
		case R.id.who:
			if (choosePraentDialog != null && !choosePraentDialog.isShowing()) {
				choosePraentDialog.show();
			}
			break;
		case R.id.send:
			/*
			 * if (picPath == null &&
			 * !bundle.getBoolean(IConstant.BUNDLE_PARAMS1)) {
			 * ViewUtil.showMessage(this, R.string.proflie_head);// 没有头像 return;
			 * } else
			 */if (ViewUtil.getContent(this, R.id.sex_val)// 没有性别
					.equals(resources.getString(R.string.must))) {
				ViewUtil.showMessage(this, R.string.proflie_sex);
				return;
			} else if (ViewUtil.getContent(this, R.id.brithday_val).equals(// 没有生日
					resources.getString(R.string.must))) {
				ViewUtil.showMessage(this, R.string.proflie_brithday);
				return;
			} else if (ViewUtil.getContent(this, R.id.who_val)// 没有选择关系
					.equals(resources.getString(R.string.must))
					&& !bundle.getBoolean(IConstant.BUNDLE_PARAMS1)) {
				ViewUtil.showMessage(this, R.string.proflie_bax);
				return;
			}
			dialog2 = new ProgressDialog(this);
			dialog2.show();
			new Thread(networkTask).start();
			break;
		}
	}

	@Override
	public void onActivityResult(final int requstCode, int resultCode,
			final Intent data) {
		super.onActivityResult(requstCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		try {
			IConstant.executorService.execute(new Runnable() {
				public void run() {
					switch (requstCode) {
					case IConstant.FOR_CAMERA:
						AppUtil.performCrop(picPath, picPath1,
								IConstant.FOR_CROP, ProfileBabyActivity.this);
						break;
					case IConstant.FOR_PHOTO:
						Uri uri = data.getData();
						if (uri != null) {
							ContentResolver contentResolver = getContentResolver();
							String path = StringUtil.getPicPath(uri,
									contentResolver);
							AppUtil.performCrop(path, picPath1,
									IConstant.FOR_CROP,
									ProfileBabyActivity.this);
						}
						break;
					case IConstant.FOR_CROP:
						runOnUiThread(new Runnable() {
							public void run() {
								imageLoader.displayImage("file://" + picPath1,
										(ImageView) findViewById(R.id.profile),
										options);
							}
						});
						break;
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上传照片功能
	 * */
	Map<String, File> upfiles = new HashMap<String, File>();
	Runnable networkTask = new Runnable() {

		@Override
		public void run() {
			if (picPath != null) {
				File f = new File(picPath1);
				upfiles.put(f.getName(), f);
			}
			try {
				Map<String, String> params = new HashMap<String, String>();
				params.put("userId", app.getUid() + "");
				params.put("classId", model.getClassId() + "");
				params.put("childId", model.getUid() + "");
				if (!bundle.getBoolean(IConstant.BUNDLE_PARAMS1)) {
					params.put("parentType", praent + "");
				}
				params.put("sex", sex + "");
				params.put("birthday", ViewUtil.getContent(
						ProfileBabyActivity.this, R.id.brithday_val));
				if (bundle.getBoolean(IConstant.BUNDLE_PARAMS1)) {
					UploadFile.post(ProfileBabyActivity.this,
							IUrContant.UPDATE_BABY_INFO_URL, params, upfiles,
							ProfileBabyActivity.this, app);
				} else {
					UploadFile.post(ProfileBabyActivity.this,
							IUrContant.JOIN_CLASS_URL, params, upfiles,
							ProfileBabyActivity.this, app);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void uploadCallBack(String data, String actionUrl) throws Exception {
		if (actionUrl.equals(IUrContant.LOGIN_URL)) {
			new Thread(networkTask).start();
			return;
		}
		String string = new String(data);
		if (!string.subSequence(0, 1).equals("{")) {
			Map<String, String> map = new HashMap<>();
			map.put("userName",
					SharedPreferencesUtil.getString(this, IConstant.USER_NAME));
			map.put("password",
					SharedPreferencesUtil.getString(this, IConstant.PASSWORD));
			map.put("type", "2");
			map.put("code", null);
			IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.LOGIN_URL, map,
					activityHandler, false);
		} else {
			try {
				setResult(RESULT_OK);
				if (dialog != null) {
					dialog.dismiss();
					finish();
				}
				JSONObject jObject = new JSONObject(data);
				if (jObject.optInt("retcode") == 1) {
					if (bundle.getBoolean(IConstant.BUNDLE_PARAMS3)) {
						return;
					}
					WriteMsgUitl.WriteClassData(this, app, model.getName(),
							model.getClassId(), 3);
					finishAllAct();
					finish();
					startActivity(MainActivity.class);
				} else {
					ViewUtil.showMessage(ProfileBabyActivity.this, "绑定失败");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
