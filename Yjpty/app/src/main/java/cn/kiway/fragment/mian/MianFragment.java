package cn.kiway.fragment.mian;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.MipcaCaptureActivity;
import cn.kiway.activity.main.InShcoolnfoActivity;
import cn.kiway.activity.main.WebViewActivity;
import cn.kiway.activity.main.classring.ClassRingActivity;
import cn.kiway.activity.main.creatclass.JoinClassActivity;
import cn.kiway.activity.main.growth.GrowthProfileActivity;
import cn.kiway.activity.main.message.ChatMessageListActivity;
import cn.kiway.activity.main.teaching.GoToClassActivity;
import cn.kiway.activity.main.teaching.HeizInfoActivity;
import cn.kiway.adapter.main.ClassViewPagerAdapter;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.message.model.MessageChatProvider;
import cn.kiway.message.model.MessageObserver;
import cn.kiway.message.util.WriteMsgUitl;
import cn.kiway.model.ClassModel;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class MianFragment extends BaseFragment implements OnPageChangeListener {
	LinearLayout layout, layout2;// 9宫格布局的
	ScrollView scrollView;
	ImageView img;
	ViewPager viewPager;
	private ClassViewPagerAdapter adapter;// 列表适配器
	TextView textOne, textTwo;
	int position;// 默认选中的班级
	public static List<ClassModel> list = new ArrayList<ClassModel>();
	RadioGroup group;
	MessageObserver messageObserver;
	ContentResolver contentResolver;

	public MianFragment() {
		super();
	}

	@SuppressLint("HandlerLeak")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		contentResolver = activity.getContentResolver();
		messageObserver = new MessageObserver(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					setMsgCount();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		contentResolver.registerContentObserver(
				Uri.parse(MessageChatProvider.MESSAGECHATS_URL), true,
				messageObserver);
		view = ViewUtil.inflate(activity, R.layout.fragment_mian);
		try {
			params();
			initView();
			setMsgCount();
			setData();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	/**
	 * 初始化
	 * */
	void initView() throws Exception {
		group = ViewUtil.findViewById(view, R.id.group);
		viewPager = ViewUtil.findViewById(view, R.id.gift_view);
		view.findViewById(R.id.class_ring).setOnClickListener(this);
		view.findViewById(R.id.message).setOnClickListener(this);
		view.findViewById(R.id.develop).setOnClickListener(this);
		view.findViewById(R.id.in_school).setOnClickListener(this);
		view.findViewById(R.id.school_new).setOnClickListener(this);
		view.findViewById(R.id.scan_code).setOnClickListener(this);
		view.findViewById(R.id.go_class).setOnClickListener(this);
		view.findViewById(R.id.create_class).setOnClickListener(this);
		view.findViewById(R.id.joins_class).setOnClickListener(this);
		scrollView = ViewUtil.findViewById(view, R.id.scroll);
		scrollView.setVerticalScrollBarEnabled(false);
		viewPager.setOnPageChangeListener(this);
	}

	@Override
	public void loadData() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("userId", activity.app.getUid() + "");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_CLASS_LIST_URL,
				map, fragmentHandler, false);
	}

	void setData() throws Exception {
		list.clear();
		JSONObject data = activity.mCache
				.getAsJSONObject(IUrContant.GET_CLASS_LIST_URL);
		if (data != null) {
			JSONArray array = data.optJSONArray("classList");
			if (array != null) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject item = array.optJSONObject(i);
					ClassModel model = new ClassModel();
					model.setId(item.optInt("id"));// 班级id
					model.setClassName(item.optString("className"));// 班级名字
					model.setChildNum(StringUtil.toInt(item
							.getString("childNum")));// 班级 宝贝数
					model.setSchoolName(data.optString("school"));// 学校名字
					model.setHeZiCode(item.optString("hCode"));// 盒子编号
					model.setCreateId(item.optInt("owner"));// 是谁创建的
					model.setSchoolId(item.optInt("schoolId"));// 学校id
					model.setYear(item.optInt("gradeId"));// 年级
					list.add(model);
					WriteMsgUitl.WriteClassData(activity, activity.app,// 创建班级圈的聊天记录
							item.optString("className"), item.optInt("id"));
				}
			}
		}
		adapter = new ClassViewPagerAdapter(activity.fragmentManager, list);
		viewPager.setAdapter(adapter);
		if (list.size() > 0) {// 判断是否有班级，展示不同的视图
			if (position >= list.size()) {
				position = 0;
				SharedPreferencesUtil.save(activity, IConstant.CHANGE_CLASS,
						position);
			}
			activity.app.setClassModel(list.get(position));
			view.findViewById(R.id.class_ha).setVisibility(View.VISIBLE);
			view.findViewById(R.id.no_class).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.class_ha).setVisibility(View.GONE);
			view.findViewById(R.id.no_class).setVisibility(View.VISIBLE);
			textOne = ViewUtil.findViewById(view, R.id.create_class_one);
			textTwo = ViewUtil.findViewById(view, R.id.create_class_two);
			ViewUtil.setTextFontColor(textOne, null,// 设置颜色
					activity.resources.getColor(R.color._00cc99), 8, 12);
			ViewUtil.setTextFontColor(textTwo, null,
					activity.resources.getColor(R.color._00cc99), 10, 14);
		}
		viewPager.setCurrentItem(position);
		if (list.size() >= 2) {// 班级数为一不创建
			if (group.getChildCount() == list.size())// 如果数目一样则不再创建
				return;
			group.removeAllViews();// 动态增加radiobutton
			for (int i = 0; i < list.size(); i++) {
				RadioButton tempButton = new RadioButton(activity);
				tempButton.setId(i);
				tempButton.setButtonDrawable(R.drawable.viewpage_selector_bg); // 设置按钮的样式
				tempButton.setPadding(30, 0, 0, 0); // 设置文字距离按钮四周的距离
				group.addView(tempButton,
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
			}
			group.check(position);
		}
	}

	/**
	 * 设置9宫格每行的高度
	 * */
	void params() throws Exception {
		position = SharedPreferencesUtil.getInteger(activity,
				IConstant.CHANGE_CLASS);
		layout = ViewUtil.findViewById(view, R.id.layout);
		layout2 = ViewUtil.findViewById(view, R.id.layout2);
		img = ViewUtil.findViewById(view, R.id.img);
		float w = activity.displayMetrics.widthPixels;
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				(int) w, (int) (w / 3));
		layout.setLayoutParams(layoutParams);
		layout2.setLayoutParams(layoutParams);
		img.setLayoutParams(layoutParams);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Bundle bundle = new Bundle();
		switch (v.getId()) {
		case R.id.go_class:
			bundle.putSerializable(IConstant.BUNDLE_PARAMS, (Serializable) list);
			activity.startActivity(HeizInfoActivity.class, bundle);// 我要上课
			break;
		case R.id.develop:
			activity.startActivity(GrowthProfileActivity.class);// 成长档案
			break;
		case R.id.scan_code:
			bundle.putInt(IConstant.BUNDLE_PARAMS, 2);
			activity.startActivity(MipcaCaptureActivity.class, bundle);// 扫描二维码
			break;
		case R.id.class_ring:
			activity.startActivity(ClassRingActivity.class);// 班级圈
			break;
		case R.id.create_class:
			bundle.putInt(IConstant.BUNDLE_PARAMS, 3);// 1注册进去，2登录后创建班级进入
			activity.startActivity(JoinClassActivity.class, bundle);
			break;
		case R.id.joins_class:
			/*bundle.putInt(IConstant.BUNDLE_PARAMS, 2);// 加入班级
			activity.startActivity(MipcaCaptureActivity.class, bundle);*/
			bundle.putInt(IConstant.BUNDLE_PARAMS, 3);// 1注册进去，2登录后创建班级进入
			activity.startActivity(JoinClassActivity.class, bundle);
			break;
		case R.id.message:
			activity.startActivity(ChatMessageListActivity.class);// 私信
			break;
		case R.id.in_school:
			activity.startActivity(InShcoolnfoActivity.class);// 在校情况
			break;
		case R.id.school_new:
			bundle.putString(
					IConstant.BUNDLE_PARAMS,
					IUrContant.GET_SCHOOL_WEB_URL + "?userId="
							+ activity.app.getUid() + "&classId="
							+ activity.app.getClassModel().getId());// 携带的url
			bundle.putString(IConstant.BUNDLE_PARAMS1,// 加入用户id 与班级，后端获取数据需要
					activity.resources.getString(R.string.school_news));
			System.out.println(IUrContant.GET_SCHOOL_WEB_URL + "?userId="
					+ activity.app.getUid() + "&classId="
					+ activity.app.getClassModel().getId());
			activity.startActivity(WebViewActivity.class, bundle);// 学校新闻
			break;
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.GET_CLASS_LIST_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				activity.mCache.put(IUrContant.GET_CLASS_LIST_URL, data);
				setData();
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {// 切换班级，保存当前的班级
		SharedPreferencesUtil.save(activity, IConstant.CHANGE_CLASS, position);
		activity.app.setClassModel(list.get(position));
		group.check(position);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			try {
				group.check(position);
				params();
				loadData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			group.check(position);
			params();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置消息数量
	 * */
	void setMsgCount() throws Exception {
		Cursor cursor = contentResolver.query(
				Uri.parse(MessageChatProvider.MESSAGECHATS_URL),
				new String[] { "sum(_unread) as unreadcount" }, " _msgtype=? ",
				new String[] { "1" }, null);
		while (cursor.moveToNext()) {
			int count = cursor.getInt(0);
			if (count > 0) {
				String str = String.valueOf(count);
				if (count > 99)
					str = "99+";
				ViewUtil.setContent(view, R.id.message_number, str);
				view.findViewById(R.id.message_number).setVisibility(
						View.VISIBLE);
			} else {
				view.findViewById(R.id.message_number).setVisibility(View.GONE);
			}
		}
		cursor.close();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			if (messageObserver != null)
				contentResolver.unregisterContentObserver(messageObserver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
