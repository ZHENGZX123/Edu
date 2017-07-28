package cn.kiway.activity;

import android.content.Intent;
import android.os.Bundle;
import cn.kiway.Yjpty.R;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.dialog.IsNetWorkDialog.IsNetWorkCallBack;

public class BaseNetWorkActicity extends BaseActivity implements
		IsNetWorkCallBack {
	public IsNetWorkDialog newWorkdialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network);
	}

	@Override
	public void isNetWorkCallBack() throws Exception {
		// 跳转到系统的网络设置界面
		Intent intent = null;
		// 先判断当前系统版本
		if (android.os.Build.VERSION.SDK_INT > 10) { // 3.0以上
			intent = new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS);
		} else {
			intent = new Intent();
			intent.setClassName("com.android.settings",
					"com.android.settings.WirelessSettings");
		}
		startActivity(intent);
		finish();
	}

	@Override
	public void cancel() throws Exception {
		finish();
	}
}
