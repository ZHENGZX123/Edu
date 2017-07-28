package cn.kiway.activity.me.setting;

import kankan.wheel.widget.SwitchButton;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.main.WebViewActivity;
import cn.kiway.activity.me.AboutActivity;
import cn.kiway.dialog.ClearDataDialog;
import cn.kiway.dialog.ClearDataDialog.ClearDataCallBack;
import cn.kiway.dialog.NewVersionDialog;
import cn.kiway.dialog.NewVersionDialog.NewVersionCallBack;
import cn.kiway.http.DownloadService;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.ViewUtil;

public class SettingActivity extends BaseActivity implements
		OnCheckedChangeListener, ClearDataCallBack, NewVersionCallBack {
	SwitchButton button;// 选择开关
	ClearDataDialog cleanDialog, exitDialog;// 清除缓存、退出dialog
	NewVersionDialog versionDialog;// 检查更新dialog

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		try {
			initView();
			setData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initView() throws Exception {
		findViewById(R.id.clear).setOnClickListener(this);
		findViewById(R.id.check).setOnClickListener(this);
		findViewById(R.id.serice).setOnClickListener(this);
		findViewById(R.id.about).setOnClickListener(this);
		// findViewById(R.id.set_number).setOnClickListener(this);
		// findViewById(R.id.changer_number).setOnClickListener(this);
		findViewById(R.id.exit).setOnClickListener(this);
		findViewById(R.id.chang_ps).setOnClickListener(this);
		button = ViewUtil.findViewById(this, R.id.mTogBtn);
		if (SharedPreferencesUtil.getBoolean(this, IConstant.WIFI)) {
			button.setChecked(true);
		} else {
			button.setChecked(false);
		}
		button.setOnCheckedChangeListener(this);
		cleanDialog = new ClearDataDialog(this, this,
				resources.getString(R.string.sure_clear_data),
				findViewById(R.id.clear));
		exitDialog = new ClearDataDialog(this, this,
				resources.getString(R.string.sure_exit),
				findViewById(R.id.exit));
		versionDialog = new NewVersionDialog(this, this);
	}

	@SuppressLint("SdCardPath")
	@Override
	public void setData() throws Exception {
		ViewUtil.setContent(this, R.id.clear_data,
				AppUtil.getTotalCacheSize(this));// 获取缓存大小
		ViewUtil.setContent(
				this,
				R.id.version,
				resources.getString(R.string.version)
						+ AppUtil.getVersion(this));// 获取当前版本号
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.clear:
			if (cleanDialog != null && !cleanDialog.isShowing()) {
				cleanDialog.show();
			}// 清楚缓存
			break;
		case R.id.check:
			// 检查更新
			IConstant.HTTP_CONNECT_POOL.addRequest(
					IUrContant.CHECK_VERSION_URL, null, activityHandler, true);
			break;
		case R.id.serice:
			Bundle bundle = new Bundle();
			bundle.putString(IConstant.BUNDLE_PARAMS,
					IUrContant.SERIVCE_AGREE_URL);
			bundle.putString(IConstant.BUNDLE_PARAMS1,
					resources.getString(R.string.serice));
			startActivity(WebViewActivity.class, bundle);// 服务协议
			break;
		case R.id.about:
			startActivity(AboutActivity.class);// 关于
			break;
		// case R.id.set_number:
		// 设置安全号码
		// break;
		// case R.id.changer_number:
		// 更换号码
		// break;
		case R.id.exit:
			if (exitDialog != null && !exitDialog.isShowing()) {
				exitDialog.show();
			}// 退出
			break;
		case R.id.chang_ps:
			startActivity(ChangPasswordActivity.class);
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttun, boolean bool) {
		if (bool) {
			SharedPreferencesUtil.save(this, IConstant.WIFI, true);
		} else {
			SharedPreferencesUtil.save(this, IConstant.WIFI, false);
		}
	}

	@SuppressLint("SdCardPath")
	@Override
	public void clearDataCallBack(View v) throws Exception {
		switch (v.getId()) {
		case R.id.clear:
			imageLoader.clearDiskCache();
			AppUtil.deleteFiles(Environment.getExternalStorageDirectory()
					.getPath() + "/" + IConstant.ZWHD_ROOT, false);
			AppUtil.deleteFiles(getCacheDir().getAbsolutePath(), false);
			mCache.clear();
			ViewUtil.setContent(this, R.id.clear_data, "0M");// 清缓存
			break;
		case R.id.exit:
			IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.LOGOT_OUT, null,
					activityHandler);
			if (app.getIoSession() != null)
				app.getIoSession().closeNow();
			app.setIoSession(null);
			AppUtil.ExitLoading(this);
			break;
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.CHECK_VERSION_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optDouble("yjptj") > Double.valueOf(AppUtil
					.getVersion(this))) {
				versionDialog.setApkUrl(IUrContant.DOWNLOAD_APK_URL);
				versionDialog.setTitle(resources
						.getString(R.string.new_versione));
				if (versionDialog != null && !versionDialog.isShowing()) {
					versionDialog.show();
				}
			} else {
				ViewUtil.showMessage(this, "已经是最新版本了");
			}
		}
	}

	@Override
	public void newVersionOkCallBack() throws Exception {
		super.newVersionOkCallBack();
		Intent intent = new Intent(this, DownloadService.class);
		intent.putExtra(IConstant.BUNDLE_PARAMS, IUrContant.DOWNLOAD_APK_URL);
		startService(intent);
		finishAllAct();
	}
}
