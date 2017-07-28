package cn.kiway.activity.main.teaching;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import java.io.Serializable;

import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.main.teaching.netty.NettyClientBootstrap;
import cn.kiway.activity.main.teaching.netty.NettyClientHandler;
import cn.kiway.activity.main.teaching.netty.NettyClientHandler.NettyMessageCallBack;
import cn.kiway.activity.main.teaching.netty.PushClient;
import cn.kiway.adapter.common.SpinnerAdapter;
import cn.kiway.dialog.LoginDialog;
import cn.kiway.fragment.mian.MianFragment;
import cn.kiway.message.MessageServer;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.GlobeVariable;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class SendWifiNameActivity extends BaseActivity implements
		OnCheckedChangeListener, NettyMessageCallBack {
	private Spinner spinner;
	private SpinnerAdapter adapter;
	private WifiManager wm;
	private boolean cracking;
	private WifiReceiver wifiReceiver;
	private IntentFilter intentFilter;
	private String wifiName;
	private boolean isConnect = false;
	private boolean isOpenConnect = false;
	private CheckBox box;
	public static String IS_NOTIFY = "is_notify";
	private LoginDialog dialog;
	private boolean isStart = true;
	private boolean isRightPs = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_wifiname);
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initView() throws Exception {
		super.initView();
		cracking = false;
		wm = (WifiManager) getSystemService(WIFI_SERVICE);
		spinner = ViewUtil.findViewById(this, R.id.wifiTp);// 下来列表
		box = ViewUtil.findViewById(this, R.id.check);
		findViewById(R.id.login).setOnClickListener(this);
		findViewById(R.id.no_send).setOnClickListener(this);
		box.setOnCheckedChangeListener(this);
		adapter = new SpinnerAdapter(this, -1);
		NettyClientHandler.setCallBack(this);
		dialog = new LoginDialog(this);
		spinner.setAdapter(adapter);
		IConstant.executorService.execute(new Runnable() {
			public void run() {
				ViewUtil.setContent(SendWifiNameActivity.this, R.id.wifiName,
						AppUtil.getConnectWifiSsid(SendWifiNameActivity.this));
				spinner.setSelection(AppUtil.getCipherType(
						SendWifiNameActivity.this, ViewUtil.getContent(
								SendWifiNameActivity.this, R.id.wifiName)) - 1);
				wifiReceiver = new WifiReceiver();
				intentFilter = new IntentFilter(
						WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
				intentFilter
						.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
				registerReceiver(wifiReceiver, intentFilter);
			}
		});
		wifiName = SharedPreferencesUtil.getString(this, IConstant.WIFI_NEME
				+ app.getClassModel().getId());
		SharedPreferencesUtil.save(this, IConstant.IS_ON_CLASS, false);
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.login:
			if (spinner.getSelectedItemPosition() != 0
					&& StringUtil.isEmpty(ViewUtil
							.getContent(this, R.id.wifiPs))) {
				ViewUtil.showMessage(this, "请输入密码");
				return;
			}
			if (ViewUtil.getContent(this, R.id.wifiName).isEmpty()
					|| ViewUtil.getContent(this, R.id.wifiName).equals("")) {
				ViewUtil.showMessage(this, "网络名字不能为空");
				return;
			}
			if (AppUtil.getCipherType(this,
					ViewUtil.getContent(this, R.id.wifiName)) != spinner
					.getSelectedItemPosition() + 1) {
				ViewUtil.showMessage(this, "加密类型错误");
				return;
			}
			if (MessageServer.client != null)
				MessageServer.client.close();
			isConnect = false;
			isOpenConnect = true;
			IConstant.executorService.execute(new Runnable() {
				public void run() {
					PushClient.create();
					app.admin.addNetwork(app.admin.CreateWifiInfo(ViewUtil
							.getContent(SendWifiNameActivity.this,
									R.id.wifiName), ViewUtil.getContent(
							SendWifiNameActivity.this, R.id.wifiPs), spinner
							.getSelectedItemPosition() + 1));
				}
			});
			if (dialog != null) {
				dialog.setTitle("开始验证密码");
				dialog.show();
			}
			break;
		case R.id.no_send:// 不绑定
			Bundle bundle = new Bundle();
			bundle.putBoolean(IConstant.BUNDLE_PARAMS, true);// 1上课， 2看备课
			startActivity(TeachingPlansActivity.class, bundle);
			finish();
			break;
		}
	}

	class WifiReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
			} else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION
					.equals(action)) {
				WifiInfo info = wm.getConnectionInfo();
				SupplicantState state = info.getSupplicantState();
				String str = null;
				if (state == SupplicantState.ASSOCIATED) {
					str = "关联AP完成";
					Log.e("Wifi:::", str);
				} else if (state.toString().equals("AUTHENTICATING")) {
					str = "正在验证";
					Log.e("Wifi:::", str);
				} else if (state == SupplicantState.ASSOCIATING) {
					str = "正在关联AP...";
					Log.e("Wifi:::", str);
				} else if (state == SupplicantState.COMPLETED) {
					if (cracking) {
						str = "不知道是啥东西：：：：";
					} else {
						str = "已连接";
						if (AppUtil.getConnectWifiSsid(
								SendWifiNameActivity.this).equals(
								ViewUtil.getContent(SendWifiNameActivity.this,
										R.id.wifiName))) {
							if (MessageServer.client != null)
								MessageServer.client.close();
							if (isConnect && !isStart && isRightPs) {
								/*
								 * finish(); Bundle bundle = new Bundle();
								 * bundle
								 * .putSerializable(IConstant.BUNDLE_PARAMS,
								 * (Serializable) MianFragment.list);
								 * startActivity(HeizInfoActivity.class,
								 * bundle);// 我要上课
								 */} else {
								if (isOpenConnect) {
									isRightPs = true;
									app.admin
											.addNetwork(app.admin.CreateWifiInfo(
													wifiName.split(":::")[0],
													"12345678",
													spinner.getSelectedItemPosition() + 1));
									Log.e("Wifi:::", "开始连接盒子Wifi了");
									dialog.setTitle("密码正确，开始连接盒子wifi");
								}
							}
						} else if (AppUtil.getConnectWifiSsid(
								SendWifiNameActivity.this).equals(
								wifiName.split(":::")[0])
								&& isOpenConnect) {
							Log.e("Wifi:::", "该发送密码了：：：：：");
							handler.sendEmptyMessageDelayed(0, 1500);
						}
					}
					Log.e("Wifi:::", str);
				} else if (state == SupplicantState.DISCONNECTED) {
					str = "已断开";
					Log.e("Wifi:::", str);
				} else if (state == SupplicantState.DORMANT) {
					str = "暂停活动";
					Log.e("Wifi:::", str);
				} else if (state == SupplicantState.FOUR_WAY_HANDSHAKE) {
					str = "四路握手中...";
				} else if (state == SupplicantState.GROUP_HANDSHAKE) {
					str = "GROUP_HANDSHAKE";
					Log.e("Wifi:::", str);
				} else if (state == SupplicantState.INACTIVE) {
					str = "休眠中...";
					Log.e("Wifi:::", str);
				} else if (state == SupplicantState.INVALID) {
					str = "无效";
					Log.e("Wifi:::", str);
				} else if (state == SupplicantState.SCANNING) {
					str = "";
					Log.e("Wifi:::", str);
				} else if (state == SupplicantState.UNINITIALIZED) {
					str = "未初始化";
					Log.e("Wifi:::", str);
				}
				final int errorCode = intent.getIntExtra(// 验证失败
						WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
				if (errorCode == WifiManager.ERROR_AUTHENTICATING) {
					Log.e("Wifi:::", "验证失败");
					dialog.close();
					ViewUtil.showMessage(SendWifiNameActivity.this, "密码错误");
				}
			}
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (!PushClient.isOpen()) {
				NettyClientBootstrap.host = "192.168.43.1";
				PushClient.start();
			} else {
				NettyClientHandler.sendMessage(GlobeVariable.SEND_WIFI
						+ ViewUtil.getContent(SendWifiNameActivity.this,
								R.id.wifiName)
						+ ":::"
						+ ViewUtil.getContent(SendWifiNameActivity.this,
								R.id.wifiPs) + ":::"
						+ (spinner.getSelectedItemPosition() + 1));
				dialog.setTitle("发送Wifi信息");
			}
			handler.sendEmptyMessageDelayed(0, 1000);
		};

	};

	// 暂停监听
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(wifiReceiver);
	}

	// 重启监听
	@Override
	protected void onResume() {
		if (wifiReceiver != null && intentFilter != null)
			IConstant.executorService.execute(new Runnable() {
				public void run() {
					registerReceiver(wifiReceiver, intentFilter);
				}
			});
		super.onResume();
	}
   
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		if (arg1) {
			SharedPreferencesUtil.save(this, IS_NOTIFY
					+ app.getClassModel().getHeZiCode(), true);
		} else {
			SharedPreferencesUtil.save(this, IS_NOTIFY
					+ app.getClassModel().getHeZiCode(), false);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		NettyClientHandler.setCallBack(null);
	}

	@Override
	public void Message(String string) throws Exception {
		PushClient.close();
		handler.removeMessages(0);
		if (dialog.isShowing() && dialog != null)
			dialog.close();
		if (isConnect == true)
			return;
		app.admin.disconnectWifi();
		SharedPreferencesUtil.save(SendWifiNameActivity.this,
				IConstant.WIFI_NEME + app.getClassModel().getId(),
				ViewUtil.getContent(SendWifiNameActivity.this, R.id.wifiName)
						+ ":::" + "false");// 连接成功，更新保存wifi信息
		finish();
		Bundle bundle = new Bundle();
		bundle.putSerializable(IConstant.BUNDLE_PARAMS,
				(Serializable) MianFragment.list);
		bundle.putString(IConstant.BUNDLE_PARAMS1, ViewUtil.getContent(SendWifiNameActivity.this, R.id.wifiName));
		bundle.putString(IConstant.BUNDLE_PARAMS2, ViewUtil.getContent(
				SendWifiNameActivity.this, R.id.wifiPs));
		bundle.putInt(IConstant.BUNDLE_PARAMS3,spinner
				.getSelectedItemPosition() + 1);
		startActivity(HeizInfoActivity.class, bundle);// 我要上课
		ViewUtil.showMessage(this, "发送成功");
		isConnect = true;
		isStart = false;
		Log.e("Wifi:::", "连接成功：：进去到上课");
	};
}
