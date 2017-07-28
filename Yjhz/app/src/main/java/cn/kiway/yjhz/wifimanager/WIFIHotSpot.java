package cn.kiway.yjhz.wifimanager;

import java.lang.reflect.Method;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * 开启wifi热点
 * 
 * @author Administrator
 * 
 */
public class WIFIHotSpot {

	public static boolean hotSpot;

	/**
	 * 开启wifi热点
	 * 
	 * @param enable
	 * @param wifiManager
	 */
	public void setWifiApEnabled(boolean enable, WifiManager wifiManager,
			String ssid, String pwd) {
		if (enable) {
			// 如果wifi已经打开则关闭wifi
			wifiManager.setWifiEnabled(false);
		}
		try {
			closeWIFIHotspot(wifiManager);
			WifiConfiguration apConfig = new WifiConfiguration();
			Log.i("设置wifi", "设置wifi");
			apConfig.SSID = ssid;
			apConfig.preSharedKey = pwd;
			// 反射
			Method method = wifiManager.getClass().getMethod(
					"setWifiApEnabled", WifiConfiguration.class, boolean.class);
			apConfig.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			apConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			apConfig.allowedKeyManagement
					.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			apConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			apConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			apConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.CCMP);
			apConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.TKIP);
			Method method1 = wifiManager.getClass()
					.getMethod("isWifiApEnabled");
			method1.setAccessible(true);
			method.invoke(wifiManager, apConfig, true);
			hotSpot = true;

		} catch (Exception e) {

		}
	}

	public static  void closeWIFIHotspot(WifiManager wifiManager) {
		try {
			WifiConfiguration apConfig = null;
			Method method = wifiManager.getClass().getMethod(
					"setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
			method.invoke(wifiManager, apConfig, false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
