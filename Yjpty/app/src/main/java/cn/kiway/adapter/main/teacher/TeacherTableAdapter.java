package cn.kiway.adapter.main.teacher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.main.teaching.OnClassActivity;
import cn.kiway.activity.main.teaching.netty.NettyClientBootstrap;
import cn.kiway.activity.main.teaching.netty.PushClient;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.dialog.IsNetWorkDialog.IsNetWorkCallBack;
import cn.kiway.dialog.LoginDialog;
import cn.kiway.fragment.teacher.SessionTable2Fragment;
import cn.kiway.http.BaseHttpHandler;
import cn.kiway.http.HttpHandler;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.message.model.SessionProvider;
import cn.kiway.message.util.WriteMsgUitl;
import cn.kiway.model.VideoModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;
import cn.kiway.utils.WifiAdmin;

public class TeacherTableAdapter extends ArrayAdapter<VideoModel> implements
		OnClickListener, HttpHandler, IsNetWorkCallBack {
	BaseActivity activity;
	TeacherTableHolder holder;
	boolean isAttendClass;// 1 上课 2 看详情
	public List<VideoModel> list;// 显示在界面上的数据
	public List<VideoModel> listData;// 原始全部数据
	private MyFilter mFilter;
	/**
	 * 请求的线程
	 * */
	protected BaseHttpHandler adapterhandler = new BaseHttpHandler(this) {
	};
	/**
	 * 等待框
	 * */
	public static LoginDialog dialog;
	/**
	 * 是否为看视频
	 * */
	boolean view;
	IsNetWorkDialog netWorkDialog;
	Map<String, String> map = new HashMap<>();
	boolean isKCK;
	SessionTable2Fragment fragment;
	public int position;
	String wifiName;

	public TeacherTableAdapter(Context context, SessionTable2Fragment fragment,
			boolean isAttendClass, List<VideoModel> list, boolean isKCK) {
		super(context, -1);
		this.activity = (BaseActivity) context;
		this.isAttendClass = isAttendClass;
		this.list = list;
		listData = new ArrayList<VideoModel>();
		this.isKCK = isKCK;
		this.fragment = fragment;
	}

	public TeacherTableAdapter(Context context, boolean isAttendClass,
			List<VideoModel> list, boolean isKCK) {
		super(context, -1);
		this.activity = (BaseActivity) context;
		this.isAttendClass = isAttendClass;
		this.list = list;
		listData = new ArrayList<VideoModel>();
		this.isKCK = isKCK;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity, R.layout.teacher_table_list_item);
			holder = new TeacherTableHolder();
			holder.pic = ViewUtil.findViewById(view, R.id.pic);
			holder.videoName = ViewUtil.findViewById(view, R.id.name);
			holder.videoTime = ViewUtil.findViewById(view, R.id.time);
			holder.view = ViewUtil.findViewById(view, R.id.view);
			holder.look = ViewUtil.findViewById(view, R.id.look);
			holder.type = ViewUtil.findViewById(view, R.id.type);
			holder.typetxt = ViewUtil.findViewById(view, R.id.type_txt);
			holder.fex = ViewUtil.findViewById(view, R.id.fgx);
			holder.session_data = ViewUtil
					.findViewById(view, R.id.session_data);
			holder.isPlay = ViewUtil.findViewById(view, R.id.isplay);
			holder.Jw = ViewUtil.findViewById(view, R.id.jw);
			holder.sessionName = ViewUtil.findViewById(view, R.id.session_name);
			view.setTag(holder);
		} else {
			holder = (TeacherTableHolder) view.getTag();
		}
		if (isAttendClass) // 是否处于上课状态
			holder.view.setVisibility(View.VISIBLE);
		else
			holder.view.setVisibility(View.GONE);
		VideoModel model = list.get(position);
		ViewUtil.setContent(holder.videoName, model.getName());// 名字
		if (model.getSessionName() == null || model.getSessionName().equals("")) {
			holder.sessionName.setVisibility(View.GONE);
		} else {
			ViewUtil.setContent(holder.sessionName, model.getSessionName());// 名字
			holder.sessionName.setVisibility(View.VISIBLE);
		}

		ViewUtil.setContent(holder.videoTime, model.getRequireTime() + " 分钟");// 所需时间
		// 视频图像
		activity.imageLoader.displayImage(
				StringUtil.imgUrl(activity, model.getPreview()), holder.pic,
				activity.options);
		// 课程类型
		switch (model.getDirId()) {
		case 1:// 主课
			activity.imageLoader.displayImage("drawable://" + R.drawable.mian,
					holder.type, activity.options);
			break;
		case 2:// 微课
			activity.imageLoader.displayImage("drawable://" + R.drawable.wei,
					holder.type, activity.options);
			break;
		case 3:// 资源
			activity.imageLoader.displayImage("drawable://"
					+ R.drawable.resouts, holder.type, activity.options);
			break;
		}
		if (model.getIsUser() == 2) {
			view.findViewById(R.id.zidingyi).setVisibility(View.VISIBLE);
		} else {
			view.findViewById(R.id.zidingyi).setVisibility(View.GONE);
		}
		if (model.getSeqNo() == 1)// 教案与目标
			ViewUtil.setContent(holder.look, R.string.see_golde);// 目标
		else
			ViewUtil.setContent(holder.look, R.string.ses_mubiao);// 教案
		if (!model.getTypeName().equals("")) {// 归类的名字
			ViewUtil.setContent(holder.typetxt, model.getTypeName());
			holder.typetxt.setVisibility(View.VISIBLE);
		} else
			holder.typetxt.setVisibility(View.GONE);
		view.setTag(R.id.bundle_params, position);
		holder.look.setTag(R.id.bundle_params, position);
		holder.look.setOnClickListener(this);
		holder.view.setTag(R.id.bundle_params, position);
		holder.view.setOnClickListener(this);
		view.setOnClickListener(this);
		if (isKCK) {
			if (model.getSeesionPlayTime().equals("")) {
				ViewUtil.setContent(holder.isPlay, "");
			} else {
				ViewUtil.setContent(holder.isPlay, "已播放\n"
						+ model.getSeesionPlayTime().split(" ")[0]);
				try {
					ViewUtil.setTextFontColor(holder.isPlay,
							activity.resources, R.color._00cc99, 0, 3);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (position != 0) {
				VideoModel model2 = list.get(position - 1);
				if (StringUtil.stringTimeToLong(model2.getSessionTime()
						+ " 00:00:00") < StringUtil.stringTimeToLong(model
						.getSessionTime() + " 00:00:00")) {
					holder.session_data.setVisibility(View.VISIBLE);
					if (position < SessionTable2Fragment.isSessionPosition
							|| position < SessionTable2Fragment.Totalposition) {
						holder.session_data
								.setBackgroundResource(R.color._529599);
					} else {
						holder.session_data
								.setBackgroundResource(R.color._e5e5e5);
					}
				} else {
					holder.session_data.setVisibility(View.GONE);
				}
			} else {
				holder.session_data.setVisibility(View.VISIBLE);
			}
			if (model.isTotalSession()) {
				if (SessionTable2Fragment.isSessionPosition < position) {
					holder.fex.setVisibility(View.VISIBLE);
					this.position = position;
					ViewUtil.setContent(holder.fex, "今日课程");// 目标
				} else {
					holder.fex.setVisibility(View.GONE);
				}
			} else {
				if (!model.getSeesionPlayTime().equals("")
						&& position == (listData.size() - 1)) {
					if (position == listData.size() - 1) {
						holder.Jw.setVisibility(View.VISIBLE);
						holder.fex.setVisibility(View.GONE);
						ViewUtil.setContent(holder.Jw, "已到底");// 目标
					} else {
						holder.fex.setVisibility(View.VISIBLE);
						holder.Jw.setVisibility(View.GONE);
						ViewUtil.setContent(holder.fex, "当前课程进度");// 目标
					}
					this.position = position;
				} else {
					holder.fex.setVisibility(View.GONE);
					holder.Jw.setVisibility(View.GONE);
				}
			}
		}
		return view;
	}

	static class TeacherTableHolder {
		/**
		 * 视频图片
		 * */
		ImageView pic;
		/**
		 * 视频名称
		 * */
		TextView videoName;
		/**
		 * 视频时长
		 * */
		TextView videoTime;
		/**
		 * 视频按钮
		 * */
		ImageView view;
		/**
		 * 查看按钮
		 * */
		TextView look;
		/**
		 * 课程类型
		 * */
		ImageView type;
		/**
		 * 类型文字
		 * */
		TextView typetxt;
		/**
		 * 分割线
		 * */
		TextView fex;
		/**
		 * 课时每天的分割线
		 * */
		TextView session_data;
		/**
		 * 是否播放
		 * */
		TextView isPlay;
		/**
		 * 结尾
		 * */
		TextView Jw;
		/**
		 * 课程名字
		 * */
		TextView sessionName;
	}

	@Override
	public void onClick(View v) {
		if (list.size() == 0)
			return;
		int postion = StringUtil.toInt(v.getTag(R.id.bundle_params).toString());
		VideoModel model = list.get(postion);
		activity.app.setLessionId(model.getId());// 保存当前课程id
		wifiName = SharedPreferencesUtil.getString(activity,
				IConstant.WIFI_NEME + activity.app.getClassModel().getId());
		if (wifiName == null || wifiName.equals(""))
			wifiName = "11:::false";// 应急措施
		if (AppUtil.isWifiActive(activity)) // 检查是否为wifi 网咯 在退出上课的地方判断时候用到
			activity.app.setNowWifi(AppUtil.getConnectWifiSsid(activity));// 保存当前wifi名字
		else
			activity.app.setNowWifi("noWifi");// 不是wifi网咯
		switch (v.getId()) {
		case R.id.look:// 看教案
			view = false;
			map.put("type", 1 + "");
			break;
		case R.id.view:// 看视频
			if (!NettyClientBootstrap.isConnect
					&& !Boolean.parseBoolean(wifiName.split(":::")[1])) {
				PushClient.create();
				if (PushClient.isOpen())
					PushClient.close();
				PushClient.start();
				ViewUtil.showMessage(activity, "连接不上盒子,请稍后");
				return;
			}
			view = true;
			model.setSeesionPlayTime(StringUtil.getDateField(
					System.currentTimeMillis(), 9));
			notifyDataSetChanged();
			map.put("type", 2 + "");
			dialog = new LoginDialog(activity);
			dialog.setTitle("努力上课中");
			dialog.show();
			break;
		}
		map.put("classId", activity.app.getClassModel().getId() + "");
		map.put("lessonId", model.getId() + "");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.SESSION_LIST_URL,
				map, adapterhandler);
	}

	void statActivity() throws Exception {

		Bundle bundle = new Bundle();
		if (view) {// 看视频
			SharedPreferencesUtil.save(activity, IConstant.IS_ON_CLASS, true);
			if (/*
				 * !Boolean.parseBoolean(wifiName.split(":::")[1]) ||
				 * (Boolean.parseBoolean(wifiName.split(":::")[1]) && AppUtil
				 * .getConnectWifiSsid(activity).equals(
				 * wifiName.split(":::")[0])) ||
				 */ !Boolean.parseBoolean(wifiName.split(":::")[1])) {
				if (dialog != null)
					dialog.close();
				bundle.putBoolean(IConstant.BUNDLE_PARAMS1, true);
				activity.startActivity(OnClassActivity.class, bundle);
			} else {
				WifiAdmin wa = new WifiAdmin(activity);// 连接wifi *******
				if (wa.getWifitate() != WifiAdmin.IS_OPENED)// 检查wifi是否打开
					wa.openWifi();
				wa.addNetwork(wa.CreateWifiInfo(wifiName.split(":::")[0],
						SharedPreferencesUtil.getString(activity,
								IConstant.WIFI_PASSWORD), 3));
			}
		} else {// 看教案
			bundle.putBoolean(IConstant.BUNDLE_PARAMS1, false);
			activity.startActivity(OnClassActivity.class, bundle);
		}
		view=false;
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		if (message.getUrl().equals(IUrContant.SESSION_LIST_URL)) {
			try {
				JSONObject jsonObject = new JSONObject(new String(
						message.getResponse()));
				if (jsonObject.optInt("retcode") == 1) {// 请求都数据后解析存入数据库
					VideoModel model = new VideoModel();
					if (jsonObject != null) {
						JSONObject lesson = jsonObject.optJSONObject("lesson");
						if (lesson != null) {// 课程上方的目标跟介绍
							model.setId(lesson.optInt("lessonId"));// 课程id
							model.setDirName(lesson.optString("lessonName"));// 课程名字
							model.setTeachingPreare(lesson
									.optString("teachingPrepare"));// 课程准备
							model.setTeachingAim(lesson
									.optString("teachingAim"));// 课程目标
						}
					}
					List<String> vidoeList = new ArrayList<String>();// 视频id集合
					List<String> vidoeContent = new ArrayList<String>();// 视频id集合
																		// 转为string
																		// 存入数据库
					List<String> videoIcon = new ArrayList<String>(); // 视频后缀名
					JSONArray video = jsonObject.optJSONArray("sectinList");
					if (video != null && video.length() > 0) {
						for (int i = 0; i < video.length(); i++) {
							JSONObject item = video.getJSONObject(i);
							if (item != null) {
								JSONObject videoDetail = item
										.optJSONObject("resource");// 视频的信息
								if (videoDetail != null) {
									// 视频id列表
									vidoeList
											.add(videoDetail.optString("uuid"));// 视频uuid列表
									videoIcon.add(videoDetail
											.optString("suffix"));// 视频后缀名
									if (!item.optString("content").equals("")) {
										vidoeContent.add(item
												.optString("content").replace(",",";"));// 内容
									} else {
										vidoeContent.add(videoDetail.optString(
												"name").split("\\.")[0]);// 内容
									}
								}
							}
						}
					}
					WriteMsgUitl.WriteSesson(
							activity.getContentResolver(),
							model,// 写入数据
							vidoeList.toString(), vidoeContent.toString(),
							videoIcon.toString());
				}
				statActivity();
			} catch (Exception e) {
				e.printStackTrace();
				Map<String, String> map = new HashMap<>();
				map.put("userName", SharedPreferencesUtil.getString(activity,
						IConstant.USER_NAME));
				map.put("password", SharedPreferencesUtil.getString(activity,
						IConstant.PASSWORD));
				map.put("type", "1");
				map.put("code", null);
				IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.LOGIN_URL,
						map, adapterhandler, false);
			}
		} else if (message.getUrl().equals(IUrContant.LOGIN_URL)) {// 重新登录后的请求数据
			if (map != null)
				IConstant.HTTP_CONNECT_POOL.addRequest(
						IUrContant.SESSION_LIST_URL, map, adapterhandler);
		}
	}

	@Override
	public void HttpError(HttpResponseModel message) throws Exception {
		Cursor cursor = activity.getContentResolver().query(
				Uri.parse(SessionProvider.SESSONS_URL), null, "_lessionid=?", // 课程的id
				new String[] { activity.app.getLessionId() + "" }, null);
		if (cursor.getCount() > 0// 有数据切连接的是上课wifi 则直接进入上课
				&& AppUtil.getConnectWifiSsid(activity).equals(
						wifiName.split(":::")[0])) {
			try {
				statActivity();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			/*
			 * netWorkDialog = new IsNetWorkDialog(activity, this, "网络异常",
			 * "手动恢复网络"); if (netWorkDialog != null &&
			 * !netWorkDialog.isShowing()) { netWorkDialog.show();
			 * dialog.dismiss(); return; }
			 */
			ViewUtil.showMessage(activity, "服务繁忙,请重试");
		}
	}

	@Override
	public void httpErr(HttpResponseModel message) throws Exception {
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
		activity.startActivity(intent);
	}

	@Override
	public void cancel() throws Exception {
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (dialog != null && dialog.isShowing()) {
				// dialog.dismiss();
			}
		};
	};

	@Override
	public Filter getFilter() {
		if (null == mFilter) {
			mFilter = new MyFilter();
		}
		return mFilter;
	}

	// 自定义Filter类
	class MyFilter extends Filter {

		@SuppressLint("DefaultLocale")
		@Override
		// 该方法在子线程中执行
		// 自定义过滤规则
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			List<VideoModel> newValues = new ArrayList<VideoModel>();
			String filterString = constraint.toString().trim().toLowerCase();
			// 如果搜索框内容为空，就恢复原始数据
			if (TextUtils.isEmpty(filterString)) {
				newValues = listData;
			} else {
				// 过滤出新数据
				for (VideoModel str : listData) {
					if ((-1 != str.getName().toLowerCase()
							.indexOf(filterString))
							|| (-1 != str.getSessionName().toLowerCase()
									.indexOf(filterString))
							|| (-1 != str.getTypeName().toLowerCase()
									.indexOf(filterString))) {
						newValues.add(str);
					}
				}
			}
			results.values = newValues;
			results.count = newValues.size();
			if (fragment != null)
				fragment.hasSessionData();
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			list = (List<VideoModel>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged(); // 通知数据发生了改变
				if (fragment != null)
					fragment.hasSessionData();
			} else {
				notifyDataSetInvalidated(); // 通知数据失效
			}
		}
	}
}
