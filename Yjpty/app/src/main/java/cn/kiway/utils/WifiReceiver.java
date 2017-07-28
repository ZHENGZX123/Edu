package cn.kiway.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import cn.kiway.App;
import cn.kiway.IConstant;
import cn.kiway.activity.main.teaching.OnClassActivity;
import cn.kiway.activity.main.teaching.tcpClientThread;
import cn.kiway.adapter.main.teacher.TeacherTableAdapter;
import cn.kiway.message.MinaClientHandler;

public class WifiReceiver extends BroadcastReceiver {
	App app;
	String wifiName;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (app == null)
			app = (App) context.getApplicationContext();
		if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {// wifi连接上与否
			Parcelable parcelableExtra = intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (app.getClassModel() == null)
				return;
			if (null != parcelableExtra) {
				NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
				State state = networkInfo.getState();
				boolean isConnected = state == State.CONNECTED;
				if (isConnected && !state.equals(State.CONNECTING)
						&& !state.equals(State.DISCONNECTING)
						&& !state.equals(State.DISCONNECTED)) {
					wifiName = SharedPreferencesUtil.getString(App
							.getInstance().getApplicationContext(),
							IConstant.WIFI_NEME + app.getClassModel().getId());
					if (AppUtil.getConnectWifiSsid(
							App.getInstance().getApplicationContext()).equals(
							wifiName.split(":::")[0])
							&& SharedPreferencesUtil.getBoolean(App
									.getInstance().getApplicationContext(),
									IConstant.IS_ON_CLASS)
							&& Boolean.parseBoolean(wifiName.split(":::")[1])) {// 连接上wifi
						Bundle bundle = new Bundle();
						bundle.putBoolean(IConstant.BUNDLE_PARAMS1, true);
						Intent intent2 = new Intent(context,
								OnClassActivity.class);
						intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent2.putExtras(bundle);
						context.startActivity(intent2);
						if (TeacherTableAdapter.dialog != null)
							TeacherTableAdapter.dialog.close();
						SharedPreferencesUtil.save(App.getInstance()
								.getApplicationContext(),
								IConstant.IS_ON_CLASS, false);
					} else if (AppUtil.getConnectWifiSsid(
							App.getInstance().getApplicationContext()).equals(
							app.getNowWifi())) {
						if (OnClassActivity.onClassActivity != null) {
							OnClassActivity.onClassActivity.finish();
							SharedPreferencesUtil.save(App.getInstance()
									.getApplicationContext(),
									IConstant.IS_ON_CLASS, false);
						}
					}
				}
			}
		}

		if (app.isInit()) {
			if (AppUtil.isNetworkAvailable(context)) {
				if ((app.getIoSession() == null || app.getIoSession()
						.isClosing()) && app.getIsConnect()) {
					IConstant.executorService.execute(new Runnable() {
						public void run() {
							try {
								Logger.log("失效了，再创建一个");
								MinaClientHandler.openMessage(app);
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					});
				} else {
					Logger.log("有效，不创建");
				}
			} else {
				if (app.getIoSession() != null) {
					if (app.getIoSession().isActive()) {
						app.getIoSession().closeOnFlush();
						Logger.log("断网了，关闭");
						app.getNioSocketConnector();
					}
				}
				if (tcpClientThread.socketClient != null)// 在上课的时候如果突然断网则需重新连
					tcpClientThread.closeScoket();
			}
			app.setInit(true);
		}

	}
}
