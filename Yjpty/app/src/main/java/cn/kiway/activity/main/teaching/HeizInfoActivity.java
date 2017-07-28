package cn.kiway.activity.main.teaching;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.fragment.teacher.HeizInfoAdapter;
import cn.kiway.model.ClassModel;
import cn.kiway.model.HeziStautsModel;
import cn.kiway.utils.ViewUtil;
import handmark.pulltorefresh.library.PullToRefreshListView;

public class HeizInfoActivity extends BaseActivity {
	PullToRefreshListView listView;
	List<ClassModel> list = new ArrayList<ClassModel>();
	WifiManager.MulticastLock lock;
	static DatagramSocket udpSocket = null;
	static DatagramPacket udpPacket = null;
	UDPClientThread clientThread;
	HeizInfoAdapter adapter;
	boolean isRun;
	String codeString, codeResoucre;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_list);
		if (app.admin.getWifitate() == 0 || app.admin.getWifitate() == 1)
			app.admin.openWifi();
		list = (List<ClassModel>) bundle// 获取到班级列表的数据
				.getSerializable(IConstant.BUNDLE_PARAMS);
		WifiManager manager = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);
		lock = manager.createMulticastLock("localWifi");
		if (null != bundle && null != bundle.getString(IConstant.BUNDLE_PARAMS1) &&
				!bundle.getString(IConstant.BUNDLE_PARAMS1).equals(""))
			app.admin.addNetwork(app.admin.CreateWifiInfo(bundle.getString(IConstant.BUNDLE_PARAMS1), bundle.getString
					(IConstant.BUNDLE_PARAMS2), bundle.getInt(IConstant.BUNDLE_PARAMS3)));
		try {
			initView();
			setData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		new UDPClientThread().start();
		handle.sendEmptyMessageDelayed(0, 20000);// 20秒检查更新盒子状态
	}

	@Override
	public void initView() throws Exception {
		super.initView();
		findViewById(R.id.next_class).setVisibility(View.GONE);
		findViewById(R.id.wifi_info).setVisibility(View.VISIBLE);
		listView = ViewUtil.findViewById(this, R.id.boy_list);
		ViewUtil.setContent(this, R.id.title, "连接盒子");
		adapter = new HeizInfoAdapter(this, new ArrayList<HeziStautsModel>(),
				list);
		listView.setAdapter(adapter);
	}

	@Override
	public void setData() throws Exception {
		super.setData();
		adapter.list.clear();
		for (int i = 0; i < list.size(); i++) {
			HeziStautsModel model = new HeziStautsModel();
			model.setClassName(list.get(i).getClassName());
			model.setGrade(list.get(i).getYear());
			model.setHeziCode(list.get(i).getHeZiCode());
			model.setHeziType(2);
			adapter.list.add(model);
		}
		adapter.notifyDataSetChanged();
	}

	private class UDPClientThread extends Thread {
		public UDPClientThread() {
			/* 开启线程 */
			System.out.println("监听广播开启");
			isRun = true;
		}

		@Override
		public void run() {
			byte[] data = new byte[256];
			try {
				udpSocket = new DatagramSocket(43708);
				udpPacket = new DatagramPacket(data, data.length);
			} catch (SocketException e1) {
				e1.printStackTrace();
			}
			while (isRun) {
				try {
					lock.acquire();
					udpSocket.receive(udpPacket);
				} catch (Exception e) {
				}
				if (udpPacket != null && null != udpPacket.getAddress()) {
					codeString = new String(data, 0, udpPacket.getLength());
					System.out.println("内容：：：" + codeString);
					final String ip = udpPacket.getAddress().toString()
							.substring(1);
					runOnUiThread(new Runnable() {
						public void run() {
							if (codeString.indexOf(":::") > 0) {
								codeResoucre = codeString.split(":::")[1];
								codeString = codeString.split(":::")[0];
							}
							for (int i = 0; i < adapter.list.size(); i++) {
								if (codeString.equals(adapter.list.get(i)
										.getHeziCode())
										&& !ip.equals("192.168.43.1")) {
									adapter.list.get(i).setHeziIP(ip);
									adapter.list.get(i).setHeziType(1);
									adapter.list.get(i).setHeziResoures(
											codeResoucre);
									adapter.list.get(i).setAcceptUdpTime(
											System.currentTimeMillis());
								}
							}
							adapter.notifyDataSetChanged();
						}
					});
				}
				lock.release();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (udpSocket != null) {
			udpSocket.close();
			udpSocket.disconnect();
			isRun = false;
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handle = new Handler() {
		public void handleMessage(android.os.Message msg) {
			for (int i = 0; i < adapter.list.size(); i++) {
				if (System.currentTimeMillis()
						- adapter.list.get(i).getAcceptUdpTime() > 20 * 1000L) {
					adapter.list.get(i).setHeziType(2);
				}
			}
			adapter.notifyDataSetChanged();
			handle.sendEmptyMessageDelayed(0, 20000);
		};
	};
}
