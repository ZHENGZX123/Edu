package cn.kiway.utils;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

public class WifiAdmin {
	// 定义WifiManager对象
	public WifiManager mWifiManager;
	// 定义WifiInfo对象
	public WifiInfo mWifiInfo;
	// 扫描出的网络连接列表
	private List<ScanResult> mWifiList;
	// 网络连接列表
	private List<WifiConfiguration> mWifiConfiguration;
	// 定义一个WifiLock
	WifiLock mWifiLock;
	private final static String TAG = "WifiAdmin";
	public static final int IS_OPENING = 1, IS_CLOSING = 2, IS_OPENED = 3,
			IS_CLOSED = 4;
	private StringBuffer mStringBuffer = new StringBuffer();
	private List<ScanResult> scanResultList;
	private ScanResult mScanResult;
	// 网络连接列表
	private List<WifiConfiguration> mWifiConfigList;

	// 构造函数
	public WifiAdmin(Context context) {
		// 取得WifiManager对象
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// 取得WifiInfo对象
		mWifiInfo = mWifiManager.getConnectionInfo();
		mWifiConfigList = mWifiManager.getConfiguredNetworks();
	}

	// 打开WIFI
	public void openWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	// 关闭WIFI
	public void closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	// 检查当前WIFI状态
	public int checkState() {
		return mWifiManager.getWifiState();
	}

	// 锁定WifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁WifiLock
	public void releaseWifiLock() {
		// 判断时候锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// 创建一个WifiLock
	public void creatWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfiguration;
	}

	// 指定配置好的网络进行连接
	public void connectConfiguration(int index) {
		// 索引大于配置好的网络索引返回
		if (index > mWifiConfiguration.size()) {
			return;
		}
		// 连接配置好的指定ID的网络
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
				true);
	}

	public void startScan() {
		mWifiManager.startScan();
		// 得到扫描结果
		mWifiList = mWifiManager.getScanResults();
		// 得到配置好的网络连接
		mWifiConfiguration = mWifiManager.getConfiguredNetworks();
	}

	// 得到网络列表
	public List<ScanResult> getWifiList() {
		return mWifiList;
	}

	// 查看扫描结果
	@SuppressLint("UseValueOf")
	public StringBuilder lookUpScan() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < mWifiList.size(); i++) {
			stringBuilder
					.append("Index_" + new Integer(i + 1).toString() + ":");
			// 将ScanResult信息转换成一个字符串包
			// 其中把包括：BSSID、SSID、capabilities、frequency、level
			stringBuilder.append((mWifiList.get(i)).toString());
			stringBuilder.append("/n");
		}
		return stringBuilder;
	}

	// 得到MAC地址
	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	// 得到接入点的BSSID
	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	// 得到IP地址
	public int getIPAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// 得到连接的ID
	public int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	// 得到WifiInfo的所有信息包
	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	// 添加一个网络并连接
	public void addNetwork(WifiConfiguration wcg) {
		int wcgID = mWifiManager.addNetwork(wcg);
		boolean b = mWifiManager.enableNetwork(wcgID, true);
		System.out.println("addNetwork--" + wcgID);
		System.out.println("enableNetwork--" + b);
	}

	// 断开指定ID的网络
	public void disconnectWifi(int netId) {
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}

	// 然后是一个实际应用方法，只验证过没有密码的情况：

	public WifiConfiguration CreateWifiInfo(String SSID, String Password,
			int Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = this.IsExsits(SSID);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}

		if (Type == 1) // 没有密码的情况
		{
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 2) // WEP加密的情况
		{
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 3) // WPA加密的情况
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}

	private WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	/**
	 * 打开Wifi网卡，能打开就返回true，无法打开返回false
	 */
	public boolean openNetCard() {
		if (!mWifiManager.isWifiEnabled()) {
			return mWifiManager.setWifiEnabled(true);
		} else {
			return false;
		}
	}

	/**
	 * 关闭Wifi网卡，能关闭返回true，不能关就返回false
	 */
	public boolean closeNetCard() {
		if (mWifiManager.isWifiEnabled()) {
			return mWifiManager.setWifiEnabled(false);
		} else {
			return false;
		}
	}

	/**
	 * 检查当前Wifi网卡状态，返回四种状态，如果出错返回-1
	 */
	public int getWifitate() {
		int result = -1;
		switch (mWifiManager.getWifiState()) {
		case 0:
			Log.i(TAG, "网卡正在关闭");
			result = IS_CLOSING;
			break;
		case 1:
			Log.i(TAG, "网卡已经关闭");
			result = IS_CLOSED;
			break;
		case 2:
			Log.i(TAG, "网卡正在打开");
			result = IS_OPENING;
			break;
		case 3:
			Log.i(TAG, "网卡已经打开");
			result = IS_OPENED;
			break;
		default:
			Log.i(TAG, "---_---晕......没有获取到状态---_---");
			result = -1;
			break;
		}
		return result;
	}

	/**
	 * 扫描周边网络，判断周边是否有wifi，有就返回true，没有就返回false
	 */
	public boolean scan() {
		if (getWifitate() == IS_OPENED) {
			// 开始扫描
			mWifiManager.startScan();
			// 将扫描结果存入数据列中
			scanResultList = mWifiManager.getScanResults();
			if (scanResultList != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 得到附近wifi的扫描结果，是ScanResult对象 得到的是附近网络的结果集，没有就返回null
	 */
	public ArrayList<ScanResult> getScanResult() {
		// 每次点击扫描之前清空上一次的扫描结果
		if (mStringBuffer != null) {
			mStringBuffer = new StringBuffer();
		}

		scan();// 开始扫描网络
		ArrayList<ScanResult> scanResultsList = new ArrayList<ScanResult>();
		if (scanResultList != null) {
			for (int i = 0; i < scanResultList.size(); i++) {
				mScanResult = scanResultList.get(i);
				scanResultsList.add(mScanResult);

			}
			return scanResultsList;
		} else {
			return null;
		}
	}
	/**
	 * 判断指定的网络是否能被扫描到
	 * 
	 * @param wifi_SSID
	 * @return 如果能够在周边发现指定的网络就返回true，否则返回false
	 */
	public boolean canScannable(String wifi_SSID) {
		boolean canScannable = false;
		scan();// 开始扫描周边网络
		// 得到扫描到的wifi列表
		if (scanResultList != null) {
			for (int i = 0; i < scanResultList.size(); i++) {
				System.out.println("scanResultList " + i + "----->"
						+ scanResultList.get(i).SSID);
				if (scanResultList.get(i).SSID.equals(wifi_SSID)) {
					canScannable = true;// 如果想要链接的wifi能够扫描到，那么就说明能够链接
					break;
				}
			}
		}
		return canScannable;
	}

	/**
	 * 得到指定网络的index（从0开始计数），找不到就返回-1
	 */
	public int getTagWifiId(String netName) {
		// 开始扫描网络
		scan();
		scanResultList = mWifiManager.getScanResults();
		if (scanResultList != null) {
			for (int i = 0; i < scanResultList.size(); i++) {
				mScanResult = scanResultList.get(i);
				if (mScanResult.SSID.equals(netName)) {
					return i;
				}
				String show = "No = " + i + " SSID = " + mScanResult.SSID
						+ " capabilities = " + mScanResult.capabilities
						+ " level = " + mScanResult.level;
				Log.i(TAG, show);
			}
		}
		return -1;
	}

	/**
	 * 断开当前连接的网络
	 */
	public void disconnectWifi() {
		int netId = getCurrentNetworkId();
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
		mWifiInfo = null;
	}

	/**
	 * 检查当前网络状态 如果有wifi链接，返回true，如果没有就返回false
	 */
	public boolean getWifiConnectState() {
		return mWifiInfo != null ? true : false;
	}

	/**
	 * @return 当前网络的名字，如果没有就返回null，否则返回string
	 */
	public String getCurrentSSID() {
		return (mWifiInfo == null) ? null : mWifiInfo.getSSID();
	}

	/**
	 * 得到连接的ID，如果没有就返回0，否则返回正确的id
	 */
	public int getCurrentNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	/**
	 * 得到IP地址，出错时返回0
	 */
	public int getCurrentIPAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	/**
	 * 得到MAC地址
	 * 
	 * @return 出錯了返回null
	 */
	public String getCurrentMacAddress() {
		return (mWifiInfo == null) ? null : mWifiInfo.getMacAddress();
	}

	/**
	 * 得到接入点的BSSID
	 * 
	 * @return 出錯返回null
	 */
	public String getCurrentBSSID() {
		return (mWifiInfo == null) ? null : mWifiInfo.getBSSID();
	}

	/**
	 * 得到WifiInfo的所有信息包
	 * 
	 * @return 出错了返回null
	 */
	public WifiInfo getCurrentWifiInfo() {
		return (mWifiInfo == null) ? null : mWifiInfo;
	}

	/**
	 * @param index
	 * @return 没有连接到返回false，正在连接则返回true
	 */
	public boolean connectConfiguratedWifi(String wifi_SSID) {
		System.out.println("-------------------------");
		// 如果当前网络不是想要链接的网络，要连接的网络是配置过的，并且要连接的网络能够被扫描到
		if (getCurrentSSID().indexOf(wifi_SSID) == -1) {
			if (getWifiConfigurated(wifi_SSID) != -1 && canScannable(wifi_SSID)) {
				mWifiManager
						.enableNetwork(getWifiConfigurated(wifi_SSID), true);
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断要连接的wifi名是否已经配置过了
	 * 
	 * @return 返回要连接的wifi的ID，如果找不到就返回-1
	 */
	public int getWifiConfigurated(String wifi_SSID) {
		int id = -1;
		if (mWifiConfigList != null) {
			for (int j = 0; j < mWifiConfigList.size(); j++) {
				if (mWifiConfigList.get(j).SSID.equals("\"" + wifi_SSID + "\"")) {
					// 如果要连接的wifi在已经配置好的列表中，那就设置允许链接，并且得到id
					id = mWifiConfigList.get(j).networkId;
					break;
				}
			}
		}
		return id;
	}

}
