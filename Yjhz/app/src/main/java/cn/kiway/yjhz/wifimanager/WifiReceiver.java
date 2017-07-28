package cn.kiway.yjhz.wifimanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import cn.kiway.yjhz.MainActivity;
import cn.kiway.yjhz.YjhzAppication;
import cn.kiway.yjhz.utils.CommonUitl;
import cn.kiway.yjhz.utils.GlobeVariable;
import cn.kiway.yjhz.utils.SharedPreferencesUtil;

public class WifiReceiver extends BroadcastReceiver {
	YjhzAppication app;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (app == null)
			app = (YjhzAppication) context.getApplicationContext();
		if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {// wifi连接上与否
			Parcelable parcelableExtra = intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (null != parcelableExtra) {
				NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
				State state = networkInfo.getState();
				boolean isConnected = state == State.CONNECTED;
				if (isConnected && !state.equals(State.CONNECTING)
						&& !state.equals(State.DISCONNECTING)
						&& !state.equals(State.DISCONNECTED)) {
					/*
					 * if (CommonUitl.getConnectWifiSsid(
					 * context.getApplicationContext()).equals(
					 * app.getWifiName())) {// 连接上wifi
					 */SharedPreferencesUtil
							.save(context.getApplicationContext(),
									GlobeVariable.WIFI_INFO,
									CommonUitl.getConnectWifiSsid(context)
											+ ":::" + app.getwifiPs() + ":::"
											+ app.getWifiTp());
					if (MainActivity.mainActivity != null) {
						MainActivity.mainActivity.setWifiName();
					}
					MainActivity.isHot = false;
					// }
				}
			}
		}
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
			String packageName = intent.getDataString();
			if (packageName.equals("cn.kiway.yjhz")) {
				Intent launchIntent = context.getPackageManager()
						.getLaunchIntentForPackage("cn.kiway.yjhz");
				if (launchIntent != null) {
					context.startActivity(launchIntent);
				}
			}
		}
	}
}
