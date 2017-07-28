package cn.kiway.activity.main.teaching;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.activity.main.teaching.netty.NettyClientBootstrap;
import cn.kiway.activity.main.teaching.netty.NettyClientHandler;
import cn.kiway.activity.main.teaching.netty.PushClient;
import cn.kiway.adapter.main.teacher.GoldAdapter;
import cn.kiway.dialog.ClearDataDialog;
import cn.kiway.dialog.ClearDataDialog.ClearDataCallBack;
import cn.kiway.http.BaseHttpConnectPool;
import cn.kiway.message.model.SessionProvider;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.GlobeVariable;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;
import cn.kiway.utils.WifiAdmin;

public class OnClassActivity extends BaseNetWorkActicity implements
		OnTouchListener, ClearDataCallBack {
	ImageView imageView;// 背景图片
	GoldAdapter adapter;
	IListView listView;
	List<String> vidoeList = new ArrayList<String>();// 视频id集合
	List<String> videoIconList = new ArrayList<String>();// 视频后缀集合
	int postion = 0;// 当前播放的位置
	boolean play;// 是否播放
	TextView open_txt;// 点击键盘按钮
	ClearDataDialog dialog;// 退出弹框
	long time = 0;// 记录点击时间
	Handler handler = new Handler();
	View view;// list头部
	boolean hasMeasured = false;// 只获取一次键盘的宽高
	LinearLayout layout;// 键盘
	public static int height;// 键盘的高度
	public static int width;// 键盘的宽度
	public static OnClassActivity onClassActivity;
	long timeOut = 0;
	SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.getDefault());
	String wifiname;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onClassActivity = this;
		wifiname = SharedPreferencesUtil.getString(this, IConstant.WIFI_NEME
				+ app.getClassModel().getId());
		if (wifiname == null || wifiname.equals(""))
			wifiname = "11:::false";// 应急措施
		setContentView(R.layout.activity_on_class);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:// 从数据库中取数据 根据lessinid查
					Cursor cursor = getContentResolver().query(
							Uri.parse(SessionProvider.SESSONS_URL), null,
							"_lessionid=?",// 课程的id
							new String[] { app.getLessionId() + "" }, null);
					while (cursor.moveToNext()) {
						ViewUtil.setContent(OnClassActivity.this, R.id.title,
								cursor.getString(2));// 标题
						ViewUtil.setContent(view, R.id.gold_content,
								cursor.getString(3));// 教学目标
						ViewUtil.setContent(view, R.id.really_content,
								cursor.getString(4));// 教学准备
						if (cursor.getString(3).equals(""))
							view.findViewById(R.id.gold).setVisibility(
									View.GONE);
						if (cursor.getString(4).equals(""))
							view.findViewById(R.id.really).setVisibility(
									View.GONE);
						String content = cursor.getString(5);// 课程内容
						if (content.length() > 2) {
							adapter.list.clear();
							adapter.list2.clear();
							String stringList = content.substring(1,
									content.length() - 1);
							String[] arr = stringList.split(",");
							List<String> list = java.util.Arrays.asList(arr);
							for (int i = 0; i < list.size(); i++) {// 设置正在上的课节
																	// 默认第一个，展示播放的动画
								if (i == 0)
									adapter.list2.add(true);
								else
									adapter.list2.add(false);
							}
							adapter.list.addAll(list);
						}
						String video = cursor.getString(6);
						String videoIcon = cursor.getString(7);
						if (videoIcon.length() > 2) {
							String videolist = videoIcon.substring(1,
									videoIcon.length() - 1);
							String[] arr = videolist.split(",");
							videoIconList = java.util.Arrays.asList(arr);
						}
						if (video.length() > 2) {// 视频id列表 因为取出来的是字符串,得转换为List
							String videolist = video.substring(1,
									video.length() - 1);
							String[] arr = videolist.split(",");
							vidoeList = java.util.Arrays.asList(arr);
						}
						if (vidoeList.size() > 0)
							OnClassActivity.this
									.sendHeziMessage(GlobeVariable.PLAY_VIDEO
											+ vidoeList.get(postion));// 发送播放的视频请求
					}
					cursor.close();
					adapter.notifyDataSetChanged();
					break;
				case 4:
					if (!PushClient.isOpen()) {
						if (Boolean.parseBoolean(wifiname.split(":::")[1])) {
							NettyClientBootstrap.host = "192.168.43.1";
						} else {
							NettyClientBootstrap.host = app.getSessionIp();
						}
						PushClient.start();
						handler.sendEmptyMessageDelayed(4, 1000);
					} else {
						handler.removeMessages(4);
						NettyClientHandler.sendMessage(playData);
					}
					break;
				}
			}
		};
		if (!bundle.getBoolean(IConstant.BUNDLE_PARAMS1)) {// 判断是否在上课，决定是否显示控制键盘
			findViewById(R.id.layout).setVisibility(View.GONE);
		} else {
			if (Boolean.parseBoolean(wifiname.split(":::")[1])) {
				NettyClientBootstrap.host = "192.168.43.1";
				PushClient.create();
				PushClient.start();
				handler.sendEmptyMessageDelayed(4, 1000);
			}
			View v = ViewUtil.findViewById(this, R.id.previos_class);// 初始化退出的dialog
			dialog = new ClearDataDialog(this, this,
					resources.getString(R.string.exit_this_session), v);
			if (Boolean.parseBoolean(wifiname.split(":::")[1])) {
				app.setIsOnClass(true);
			}
		}
		try {
			getKeyBoradHW();
			initView();
			loadData();
			setData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取控制键盘的宽高
	 * 
	 * @author Administrator 直接layout.getMesaguse获取出来是0
	 * */
	void getKeyBoradHW() throws Exception {
		layout = ViewUtil.findViewById(this, R.id.layout);
		ViewTreeObserver vto = layout.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (hasMeasured == false) {
					height = layout.getMeasuredHeight();
					width = layout.getMeasuredWidth();
					hasMeasured = true;
				}
				return true;
			}
		});
	}

	@Override
	public void initView() throws Exception {
		findViewById(R.id.last).setOnClickListener(this);
		findViewById(R.id.play).setOnClickListener(this);
		findViewById(R.id.next).setOnClickListener(this);
		// findViewById(R.id.play_sr).setOnTouchListener(this);
		// findViewById(R.id.p_or_n).setOnClickListener(this);
		findViewById(R.id.next).setOnClickListener(this);
		findViewById(R.id.pause).setOnClickListener(this);
		findViewById(R.id.resume).setOnClickListener(this);
		findViewById(R.id.video_add).setOnClickListener(this);
		findViewById(R.id.video_last).setOnClickListener(this);
		findViewById(R.id.open).setOnClickListener(this);
		findViewById(R.id.previos_class).setOnClickListener(this);
		findViewById(R.id.darkscreen).setOnClickListener(this);
		findViewById(R.id.brightenscreen).setOnClickListener(this);
		open_txt = ViewUtil.findViewById(this, R.id.open);
		adapter = new GoldAdapter(this, new ArrayList<String>(),
				bundle.getBoolean(IConstant.BUNDLE_PARAMS1),
				new ArrayList<Boolean>());
		listView = ViewUtil.findViewById(this, R.id.list);
		view = ViewUtil.inflate(this, R.layout.head_session_plan);
		listView.addHeaderView(view);
		listView.setAdapter(adapter);
		listView.setSelected(true);
	}

	@Override
	public void loadData() throws Exception {
		super.loadData();
		handler.sendEmptyMessage(1);// handler从数据获取数据
	};

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (!PushClient.isOpen()) {
			if (Boolean.parseBoolean(wifiname.split(":::")[1])) {
				NettyClientBootstrap.host = "192.168.43.1";
			} else {
				NettyClientBootstrap.host = app.getSessionIp();
			}
			PushClient.start();
		}
		switch (v.getId()) {
		case R.id.last:
			if (vidoeList.size() <= 0)
				return;
			if (postion - 1 < 0) {// 如果已经是第一个视频了，则提示
				ViewUtil.showMessage(context, R.string.first_video);
				AppUtil.Vibrate(this, 100);
			} else {
				postion = postion - 1;
				if (postion == 0) {
					sendHeziMessage(GlobeVariable.PLAY_VIDEO
							+ vidoeList.get(postion));// 上个视频
				} else {
					sendHeziMessage(GlobeVariable.PLAY_VIDEO
							+ vidoeList.get(postion).substring(1));// 上个视频
																	// substring(1)，因为存的时候，是存类型stirng
																	// 在转换的时候，有“；”未去，这里去“；”
				}
				adapter.list2.remove(postion + 1);// 更新数据，把当前播放的状态更新
				adapter.list2.add(postion + 1, false);// 移除当前的播放，更新下个播放
				adapter.list2.remove(postion);
				adapter.list2.add(postion, true);
				adapter.notifyDataSetChanged();
			}
			listView.setSelection(postion + 1);
			break;
		case R.id.next:
			if (vidoeList.size() <= 0)
				return;
			if (postion + 2 > vidoeList.size()) {// 如果已经是最后视频了，则提示
				AppUtil.Vibrate(this, 100);
				ViewUtil.showMessage(context, R.string.last_video);
			} else {
				postion = postion + 1;
				sendHeziMessage(GlobeVariable.PLAY_VIDEO
						+ vidoeList.get(postion).substring(1));// 下个视频
				adapter.list2.remove(postion - 1);// 更新数据，把当前播放的状态更新
				adapter.list2.add(postion - 1, false);// 移除当前的播放，更新下个播放
				adapter.list2.remove(postion);
				adapter.list2.add(postion, true);
				adapter.notifyDataSetChanged();
			}
			listView.setSelection(postion + 1);
			break;
		case R.id.play:
			if (vidoeList.size() <= 0)
				return;
			if (postion == 0) {
				sendHeziMessage(GlobeVariable.PLAY_VIDEO
						+ vidoeList.get(postion));// 重新播放视频
			} else {
				sendHeziMessage(GlobeVariable.PLAY_VIDEO
						+ vidoeList.get(postion).substring(1));// 重新播放视频
			}
			listView.setSelection(postion + 1);
			break;
		case R.id.video_add:
			sendHeziMessage(GlobeVariable.ADD_VOLUME);// 增加声音
			break;
		case R.id.video_last:
			sendHeziMessage(GlobeVariable.DECREASE_VOLUME);// 减少声音
			break;
		case R.id.open:// 打开关闭键盘
			listView.setSelection(postion + 1);
			if (findViewById(R.id.layout1).getVisibility() == View.VISIBLE) {
				findViewById(R.id.layout1).setVisibility(View.GONE);
				ViewUtil.setContent(this, R.id.open, R.string.open);
				ViewUtil.setArroundDrawable(open_txt, -1, -1,// 更改按钮文字，与图形
						R.drawable.ic_hardware_keyboard_arrow_up, -1);
				if (GoldAdapter.viewSpace != null// 隐藏空白
						&& GoldAdapter.viewSpace.getVisibility() == View.VISIBLE)
					GoldAdapter.viewSpace.setVisibility(View.GONE);
			} else {
				findViewById(R.id.layout1).setVisibility(View.VISIBLE);
				ViewUtil.setContent(this, R.id.open, R.string.close);
				ViewUtil.setArroundDrawable(open_txt, -1, -1,
						R.drawable.ic_hardware_keyboard_arrow_down, -1);
				if (GoldAdapter.viewSpace != null// 打开的时候给下方留空白，不留的话，播放到最后的时候，文字会被键盘挡住
						&& GoldAdapter.viewSpace.getVisibility() == View.GONE)
					GoldAdapter.viewSpace.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.previos_class:
			if (!bundle.getBoolean(IConstant.BUNDLE_PARAMS1))// 如果不是在上课则不发送请求
			{
				finish();
			} else {// 确定退出的dialog
				if (dialog != null && !dialog.isShowing()) {
					dialog.show();
				}
			}
			break;
		case R.id.pause:
			sendHeziMessage(GlobeVariable.PAUSE);
			break;
		case R.id.resume:
			sendHeziMessage(GlobeVariable.NEXT);
			break;
		case R.id.darkscreen:
			if (videoIconList.size() <= 0)
				return;
			if (videoIconList.get(postion).replace(" ", "").equals("mp4")) {
				sendHeziMessage(GlobeVariable.DARK);// 变黑
			} else if (videoIconList.get(postion).replace(" ", "")
					.equals("ppt")) {
				sendHeziMessage(GlobeVariable.PPT_LASR);// 上一页
			}
			break;
		case R.id.brightenscreen:
			if (videoIconList.size() <= 0)
				return;
			if (videoIconList.get(postion).replace(" ", "").equals("mp4")) {
				sendHeziMessage(GlobeVariable.BRIGHT);// 变亮
			} else if (videoIconList.get(postion).replace(" ", "")
					.equals("ppt")) {
				sendHeziMessage(GlobeVariable.PPT_NEXT);// 下一页
			}
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	String data;
	String playData = null;

	// 想盒子发送请求
	void sendHeziMessage(String data) {
		if (!bundle.getBoolean(IConstant.BUNDLE_PARAMS1))// 如果不是在上课则不发送请求
			return;
		if (vidoeList.size() <= 0)
			return;// 当列表id为0或没有时不发送请求
		if (StringUtil.toInt(data.split(":::")[0]) == 1) {// 如果是播放请求，先判断类型
			/*
			 * if (videoIconList.get(postion).replace(" ", "").equals("mp4")) {
			 * 
			 * } else
			 */if (videoIconList.get(postion).replace(" ", "").equals("ppt")) {
				playData = GlobeVariable.PLAY_PPT + data.split(":::")[1]
						+ ":::" + app.getClassModel().getYear();
				ViewUtil.setContent(OnClassActivity.this, R.id.darkscreen,
						R.string.last);
				ViewUtil.setContent(OnClassActivity.this, R.id.brightenscreen,
						R.string.next);
			} else if (videoIconList.get(postion).replace(" ", "")
					.equals("jpeg")
					|| videoIconList.get(postion).replace(" ", "")
							.equals("png")
					|| videoIconList.get(postion).replace(" ", "")
							.equals("jpg")) {
				playData = GlobeVariable.PLAY_IMG + data.split(":::")[1] + "."
						+ videoIconList.get(postion).replace(" ", "") + ":::"
						+ app.getClassModel().getYear();
			} else {
				playData = GlobeVariable.PLAY_VIDEO + data.split(":::")[1]
						+ "." + videoIconList.get(postion).replace(" ", "")
						+ ":::" + app.getClassModel().getYear();
				ViewUtil.setContent(OnClassActivity.this, R.id.darkscreen,
						R.string.darkscreen);
				ViewUtil.setContent(OnClassActivity.this, R.id.brightenscreen,
						R.string.brightenscreen);
			}
		} else {
			playData = data;
		}
		NettyClientHandler.sendMessage(playData);
	}

	// 手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
	float x1 = 0;
	float x2 = 0;
	float y1 = 0;
	float y2 = 0;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (MotionEvent.ACTION_DOWN == event.getAction()) {
			x1 = event.getX();
			y1 = event.getY();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// 当手指离开的时候
			x2 = event.getX();
			y2 = event.getY();
			if (y1 - y2 > 30) {
			} else if (y2 - y1 > 30) {
			} else if (x1 - x2 > 0) {
			} else if (x2 - x1 > 0) {
			}
		}
		return true;
	}

	@Override
	public void clearDataCallBack(View vx) throws Exception {// 退出上课的回调
		sendHeziMessage(GlobeVariable.EXIT);
		if (timeOut == 1) {
			finish();
			return;
		}
		BaseHttpConnectPool.loodingDialog.setTitle("努力退出中");
		BaseHttpConnectPool.loodingDialog.show();
		if (app.getNowWifi().equals("noWifi")) {// 如果之前不是wifi网咯则断开wifi不启动wifi连接
			WifiAdmin wa = new WifiAdmin(this);// 退出上课自动连保存的用户的网络,因为盒子的wifi不能连接互联网，所以得切换回去
			finish();
			wa.closeWifi();
			BaseHttpConnectPool.loodingDialog.close();
		} else if (app.getNowWifi().equals(wifiname.split(":::")[0])
				|| app.getNowWifi().equals(AppUtil.getConnectWifiSsid(this))) {
			finish();
			BaseHttpConnectPool.loodingDialog.close();
		} else {// 连接保存的网咯
			WifiAdmin wa = new WifiAdmin(this);// 退出上课自动连保存的用户的网络,因为盒子的wifi不能连接互联网，所以得切换回去
			wa.connectConfiguratedWifi(app.getNowWifi());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!bundle.getBoolean(IConstant.BUNDLE_PARAMS1))// 如果不是在上课则不发送请求
			{
				finish();
			} else {// 确定退出的dialog
				if (dialog != null && !dialog.isShowing()) {
					dialog.show();
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		onClassActivity = null;
		app.setIsOnClass(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (bundle.getBoolean(IConstant.BUNDLE_PARAMS1)
				&& Boolean.parseBoolean(wifiname.split(":::")[1]))
			PushClient.close();
	}
}
