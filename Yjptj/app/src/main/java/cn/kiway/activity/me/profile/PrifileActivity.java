package cn.kiway.activity.me.profile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.dialog.picture.SelectPictureDialog;
import cn.kiway.http.UploadFile;
import cn.kiway.http.UploadFile.UploadCallBack;
import cn.kiway.message.model.MessageProvider;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class PrifileActivity extends BaseActivity implements UploadCallBack {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		try {
			initView();
			setData();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void initView() throws Exception {
		findViewById(R.id.name).setOnClickListener(this);
		findViewById(R.id.area).setOnClickListener(this);
		findViewById(R.id.sex).setOnClickListener(this);
		findViewById(R.id.head_protrait).setOnClickListener(this);
		selectPictureDialog = new SelectPictureDialog(this);
		imageView = ViewUtil.findViewById(this, R.id.head_img);
	}

	void setData() throws Exception {
		JSONObject data = mCache.getAsJSONObject(IUrContant.GET_MY_INFO_URL
				+ app.getUid());
		if (data != null) {
			userdata = data.optJSONObject("userInfo");
			imageLoader.displayImage(
					StringUtil.imgUrl(this, userdata.optString("photo")),
					imageView, fadeOptions);
			if (!userdata.optString("realname").equals("null")) {// 名字
				ViewUtil.setContent(this, R.id.name_ovl,
						userdata.optString("realname"));
			}
			if (!userdata.optString("sex").equals("null")) {// 性别
				switch (StringUtil.toInt(userdata.optString("sex"))) {
				case 1:
					ViewUtil.setContent(this, R.id.sex_ovl, R.string.boy);
					break;
				case 2:
					ViewUtil.setContent(this, R.id.sex_ovl, R.string.girl);
					break;
				}
			}
			if (!userdata.optString("county_id").equals("null")) {// 地区
				ViewUtil.setContent(this, R.id.area_ovl,
						userdata.optString("county_id").replace("*", " "));
			}
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (userdata == null)
			return;
		Bundle b = new Bundle();
		switch (v.getId()) {
		case R.id.name:
			b.putString(IConstant.BUNDLE_PARAMS,
					ViewUtil.getContent(this, R.id.name_ovl));
			b.putBoolean(IConstant.BUNDLE_PARAMS1, true);
			Intent intent = new Intent(this, EditNameActivity.class);
			intent.putExtras(b);
			startActivityForResult(intent, 1);
			break;
		case R.id.sex:
			b.putInt(IConstant.BUNDLE_PARAMS, 0);
			if (ViewUtil.getContent(this, R.id.sex_ovl).equals(
					resources.getString(R.string.boy))) {
				b.putInt(IConstant.BUNDLE_PARAMS, 1);
			} else {
				b.putInt(IConstant.BUNDLE_PARAMS, 2);
			}
			Intent intent2 = new Intent(this, EditSexActivity.class);
			intent2.putExtras(b);
			startActivityForResult(intent2, 2);
			break;
		case R.id.area:
			b.putBoolean(IConstant.BUNDLE_PARAMS, true);
			Intent intent3 = new Intent(this, EditAeraActivity.class);
			intent3.putExtras(b);
			startActivityForResult(intent3, 3);
			break;
		case R.id.head_protrait:
			picPath = AppUtil.createNewPhoto();
			picPath1 = AppUtil.createNewPhoto();
			selectPictureDialog.setPicPath(picPath);
			if (selectPictureDialog != null && !selectPictureDialog.isShowing())
				selectPictureDialog.show();
			break;
		}
	}

	@Override
	public void onActivityResult(final int requstCode, int resultCode,
			final Intent data) {
		super.onActivityResult(requstCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		setResult(RESULT_OK);
		if (requstCode == 1) {
			ViewUtil.setContent(this, R.id.name_ovl,
					data.getStringExtra(IConstant.BUNDLE_PARAMS));
		} else if (requstCode == 2) {
			if (StringUtil.toInt(data.getStringExtra(IConstant.BUNDLE_PARAMS)) == 1) {
				ViewUtil.setContent(this, R.id.sex_ovl, R.string.boy);
			} else {
				ViewUtil.setContent(this, R.id.sex_ovl, R.string.girl);
			}
		} else if (requstCode == 3) {
			ViewUtil.setContent(this, R.id.area_ovl,
					data.getStringExtra(IConstant.BUNDLE_PARAMS));
		}
		try {
			IConstant.executorService.execute(new Runnable() {
				public void run() {
					switch (requstCode) {
					case IConstant.FOR_CAMERA:
						AppUtil.performCrop(picPath, picPath1,
								IConstant.FOR_CROP, PrifileActivity.this);
						break;
					case IConstant.FOR_PHOTO:
						Uri uri = data.getData();
						if (uri != null) {
							ContentResolver contentResolver = getContentResolver();
							String path = StringUtil.getPicPath(uri,
									contentResolver);
							AppUtil.performCrop(path, picPath1,
									IConstant.FOR_CROP, PrifileActivity.this);
						}
						break;
					case IConstant.FOR_CROP:
						if (picPath != null) {
							runOnUiThread(new Runnable() {
								public void run() {
									dialog = new ProgressDialog(
											PrifileActivity.this);
									dialog.show();
								}
							});
							thread = new Thread(networkTask);
							thread.start();
						}
						break;
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	Thread thread;
	ProgressDialog dialog;
	/**
	 * 上传头像
	 * */
	Runnable networkTask = new Runnable() {

		@Override
		public void run() {
			File f = new File(picPath1);
			if (!f.exists()) {
				runOnUiThread(new Runnable() {
					public void run() {
						if (dialog.isShowing())
							dialog.dismiss();
						ViewUtil.showMessage(PrifileActivity.this, "上传失败");
					}
				});
				return;
			}
			Map<String, File> upfiles = new HashMap<String, File>();
			upfiles.put(f.getName(), f);
			try {
				Map<String, String> params = new HashMap<String, String>();
				params.put("userId", app.getUid() + "");
				UploadFile.post(PrifileActivity.this,
						IUrContant.UPDATEUSERINFO_URL, params, upfiles,
						PrifileActivity.this,app);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void uploadCallBack(String data, String actionUrl) throws Exception {
		final JSONObject da = new JSONObject(data);
		if (da.optInt("retcode") == 1) {
			runOnUiThread(new Runnable() {
				public void run() {
					if (dialog.isShowing())
						dialog.dismiss();
					imageLoader.displayImage("file://" + picPath1,
							(ImageView) findViewById(R.id.head_img),
							fadeOptions);
					ViewUtil.showMessage(PrifileActivity.this, R.string.xgcg);
					Intent intent = getIntent();
					intent.putExtra(IConstant.BUNDLE_PARAMS, picPath);
					setResult(RESULT_OK, intent);
					ContentValues values = new ContentValues();// 成功后手动更新本地聊天头像
					values.put("_url",
							da.optJSONObject("userInfo").optString("photo"));
					getContentResolver().update(
							Uri.parse(MessageProvider.MESSAGES_URL), values,
							"_uid=?", new String[] { "" + app.getUid() });
				}
			});
		}
	}

}
