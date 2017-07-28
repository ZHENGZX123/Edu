package cn.kiway.activity.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.adapter.main.inschoolinfo.InSchoolInfoViewPagerAdapter;
import cn.kiway.adapter.main.teacher.SpecialCalendar;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.BoyCheckModel;
import cn.kiway.model.BoyModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class InShcoolnfoActivity extends BaseNetWorkActicity implements
		OnCheckedChangeListener, OnPageChangeListener {
	private InSchoolInfoViewPagerAdapter adapter;// 列表适配器
	RadioGroup radioGroup;
	RadioButton rb1, rb2, rb3, rb4, rb5, rb6;
	ViewPager viewPager;
	List<BoyModel> boyModels = new ArrayList<BoyModel>();
	SpecialCalendar calendar;
	public static String chooseDay, nowDay;
	public static List<BoyCheckModel> checkModels = new ArrayList<BoyCheckModel>();
	public static List<String> list1 = new ArrayList<String>();
	public static List<String> list2 = new ArrayList<String>();
	public static List<String> list3 = new ArrayList<String>();
	public static List<String> list4 = new ArrayList<String>();
	public static List<String> list5 = new ArrayList<String>();
	public static List<String> list6 = new ArrayList<String>();
	List<Map<String, String>> liston = new ArrayList<Map<String, String>>();// 提交列表
	boolean isSend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!AppUtil.isNetworkAvailable(this)) {// 判断是否有网
			newWorkdialog = new IsNetWorkDialog(context, this,
					resources.getString(R.string.dqsjmylrhlwqljhlwl),
					resources.getString(R.string.ljhlw));
			if (newWorkdialog != null && !newWorkdialog.isShowing()) {
				newWorkdialog.show();
				return;
			}
		}
		try {
			initView();
			loadData();
			setData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initView() throws Exception {
		setContentView(R.layout.activity_inshcool_info);
		viewPager = ViewUtil.findViewById(this, R.id.gift_view);
		radioGroup = ViewUtil.findViewById(this, R.id.radio_group2);
		rb1 = ViewUtil.findViewById(this, R.id.rb1);// 卫生
		rb2 = ViewUtil.findViewById(this, R.id.rb2);// 礼仪
		rb3 = ViewUtil.findViewById(this, R.id.rb3);// 用餐
		rb4 = ViewUtil.findViewById(this, R.id.rb4);// 纪律
		rb5 = ViewUtil.findViewById(this, R.id.rb5);// 发言
		rb6 = ViewUtil.findViewById(this, R.id.rb6);// 考勤
		radioGroup.setOnCheckedChangeListener(this);
		findViewById(R.id.left).setOnClickListener(this);
		findViewById(R.id.right).setOnClickListener(this);
		findViewById(R.id.tv_date).setOnClickListener(this);
		findViewById(R.id.actlayout).setOnClickListener(this);
		data();
		calendar = new SpecialCalendar();
		ViewUtil.setContent(this, R.id.title, "在校情况");
	}

	@Override
	public void loadData() throws Exception {
		super.loadData();// 获取班级孩子列表数据
		Map<String, String> map = new HashMap<>();
		map.put("classId", app.getClassModel().getId() + "");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_CLASS_URL, map,
				activityHandler, true);
		loadCheckeData();
	};

	void loadCheckeData() {
		if (isLast()) {
			findViewById(R.id.actlayout).setVisibility(View.GONE);
		} else {
			findViewById(R.id.actlayout).setVisibility(View.VISIBLE);
		}
		checkModels.clear();
		Map<String, String> map = new HashMap<>();
		map.put("classId", app.getClassModel().getId() + "");
		map.put("date", chooseDay);
		IConstant.HTTP_CONNECT_POOL.addRequest(
				IUrContant.GET_SCHOOL_INFO_HISSTRONG_URL, map, activityHandler,
				true);
	}

	public boolean isFuture() {
		return StringUtil.stringTimeToLong(chooseDay + " 00:00:00") > StringUtil
				.stringTimeToLong(nowDay + " 00:00:00");
	}

	public static boolean isLast() {
		return StringUtil.stringTimeToLong(chooseDay + " 00:00:00") < StringUtil
				.stringTimeToLong(nowDay + " 00:00:00");
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left:
			chooseDay = calendar.getData(
					StringUtil.toInt(chooseDay.split("-")[0]),
					StringUtil.toInt(chooseDay.split("-")[1]),
					StringUtil.toInt(chooseDay.split("-")[2]), 1);
			ViewUtil.setContent(this, R.id.tv_date, chooseDay);
			loadCheckeData();
			break;
		case R.id.right:
			chooseDay = calendar.getData(
					StringUtil.toInt(chooseDay.split("-")[0]),
					StringUtil.toInt(chooseDay.split("-")[1]),
					StringUtil.toInt(chooseDay.split("-")[2]), 2);
			if (isFuture()) {
				ViewUtil.showMessage(this, "不可选择明天的日期");
				chooseDay = nowDay;
				return;
			}
			ViewUtil.setContent(this, R.id.tv_date, chooseDay);
			loadCheckeData();
			break;
		case R.id.tv_date:
			final AlertDialog dialog = new AlertDialog.Builder(
					InShcoolnfoActivity.this).create();
			dialog.show();
			DatePicker picker = new DatePicker(InShcoolnfoActivity.this);
			picker.setDate(StringUtil.toInt(chooseDay.split("-")[0]),
					StringUtil.toInt(chooseDay.split("-")[1]));
			picker.setMode(DPMode.SINGLE);
			picker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
				@Override
				public void onDatePicked(String date) {
					chooseDay = date;
					if (isFuture()) {
						ViewUtil.showMessage(InShcoolnfoActivity.this,
								"当前选择日期不可用");
						chooseDay = nowDay;
						return;
					}
					dialog.dismiss();
					ViewUtil.setContent(InShcoolnfoActivity.this, R.id.tv_date,
							chooseDay);
					loadCheckeData();
				}
			});
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			dialog.getWindow().setContentView(picker, params);
			dialog.getWindow().setGravity(Gravity.CENTER);
			break;
		case R.id.actlayout:
			Map<String, String> map = new HashMap<>();
			map.put("classId", app.getClassModel().getId() + "");
			map.put("checkList", setInfoData() + "");// 提交数据的列表
			IConstant.HTTP_CONNECT_POOL.addRequest(
					IUrContant.IN_SCHOOL_INFO_URL, map, activityHandler, true);
			break;
		}
	}

	@Override
	public void setData() throws Exception {
		JSONObject data = mCache.getAsJSONObject(IUrContant.GET_CLASS_URL
				+ app.getClassModel());
		if (data != null) {
			boyModels.clear();
			JSONArray array = data.optJSONArray("childrenList");
			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.optJSONObject(i);
				BoyModel model = new BoyModel();
				model.setUid(item.optInt("id"));// 孩子id
				model.setName(item.optString("childName"));// 孩子名字
				boyModels.add(model);
			}
			if (boyModels.size() <= 0) {
				findViewById(R.id.actlayout).setVisibility(View.GONE);
			} else {
				findViewById(R.id.actlayout).setVisibility(View.VISIBLE);
			}
		}
		list1.clear();
		list2.clear();
		list3.clear();
		list4.clear();
		list5.clear();
		list6.clear();
		for (int i = 1; i < 7; i++) {
			int string = SharedPreferencesUtil.getInteger(this,
					IConstant.PING_JIA + i);
			String str = 1 + "";
			if (string == 1 || string == 0) {
				str = 1 + "";
			} else if (string == 2) {
				str = 2 + "";
			} else if (string == 3) {
				str = 3 + "";
			}
			for (int j = 0; j < boyModels.size(); j++) {
				switch (i) {
				case 1:
					if (boyModels.size() != InShcoolnfoActivity.list1.size())
						InShcoolnfoActivity.list1.add(str);
					break;
				case 2:
					if (boyModels.size() != InShcoolnfoActivity.list2.size())
						InShcoolnfoActivity.list2.add(str);
					break;
				case 3:
					if (boyModels.size() != InShcoolnfoActivity.list3.size())
						InShcoolnfoActivity.list3.add(str);
					break;
				case 4:
					if (boyModels.size() != InShcoolnfoActivity.list4.size())
						InShcoolnfoActivity.list4.add(str);
					break;
				case 5:
					if (boyModels.size() != InShcoolnfoActivity.list5.size())
						InShcoolnfoActivity.list5.add(str);
					break;
				case 6:
					if (boyModels.size() != InShcoolnfoActivity.list6.size())
						InShcoolnfoActivity.list6.add(str);
					break;
				}
			}
		}
		adapter = new InSchoolInfoViewPagerAdapter(fragmentManager, boyModels,
				isSend);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(this);
		ViewUtil.setContent(this, R.id.title, app.getClassModel()
				.getClassName());

	}

	@Override
	public void onPageScrollStateChanged(int page) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	int position;

	@Override
	public void onPageSelected(int page) {
		position = page + 1;
		switch (page) {
		case 0:
			rb1.setChecked(true);
			rb2.setChecked(false);
			rb3.setChecked(false);
			rb4.setChecked(false);
			rb5.setChecked(false);
			rb6.setChecked(false);
			break;
		case 1:
			rb2.setChecked(true);
			rb1.setChecked(false);
			rb3.setChecked(false);
			rb4.setChecked(false);
			rb5.setChecked(false);
			rb6.setChecked(false);
			break;
		case 2:
			rb3.setChecked(true);
			rb2.setChecked(false);
			rb1.setChecked(false);
			rb4.setChecked(false);
			rb5.setChecked(false);
			rb6.setChecked(false);
			break;
		case 3:
			rb4.setChecked(true);
			rb2.setChecked(false);
			rb3.setChecked(false);
			rb1.setChecked(false);
			rb5.setChecked(false);
			rb6.setChecked(false);
			break;
		case 4:
			rb5.setChecked(true);
			rb2.setChecked(false);
			rb3.setChecked(false);
			rb4.setChecked(false);
			rb1.setChecked(false);
			rb6.setChecked(false);
			break;
		case 5:
			rb6.setChecked(true);
			rb2.setChecked(false);
			rb3.setChecked(false);
			rb4.setChecked(false);
			rb5.setChecked(false);
			rb1.setChecked(false);
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int id) {
		switch (id) {// 点击切换到哪一个
		case R.id.rb1:
			viewPager.setCurrentItem(0);
			break;
		case R.id.rb2:
			viewPager.setCurrentItem(1);
			break;
		case R.id.rb3:
			viewPager.setCurrentItem(2);
			break;
		case R.id.rb4:
			viewPager.setCurrentItem(3);
			break;
		case R.id.rb5:
			viewPager.setCurrentItem(4);
			break;
		case R.id.rb6:
			viewPager.setCurrentItem(5);
			break;
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.GET_CLASS_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {// 获取到班级列表数据
				mCache.put(IUrContant.GET_CLASS_URL + app.getClassModel(), data);
				String type = data.optString("typeList");// 是否已经提交过了
				if (type.equals("[]") || type.length() < 3) {
					ViewUtil.setContent(this, R.id.actsend, R.string.send);
				} else {
					ViewUtil.setContent(this, R.id.actsend,
							R.string.inschool_alter);
				}
				setData();
			}
		} else if (message.getUrl().equals(
				IUrContant.GET_SCHOOL_INFO_HISSTRONG_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {// 获取到班级列表数据
				mCache.put(IUrContant.GET_SCHOOL_INFO_HISSTRONG_URL + chooseDay
						+ app.getClassModel(), data);
				setinfoList();
			}
		} else if (message.getUrl().equals(IUrContant.IN_SCHOOL_INFO_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {// 提交成功，提示成功提示，更新数据
				ViewUtil.setContent(InShcoolnfoActivity.this, R.id.actsend,
						R.string.inschool_alter);
				ViewUtil.showMessage(this, R.string.tjcg);
			}
		}
	}

	@SuppressLint("SimpleDateFormat")
	void data() throws Exception {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		chooseDay = nowDay = sdf.format(date);
		ViewUtil.setContent(this, R.id.tv_date, chooseDay);
	}

	public List<Map<String, String>> setInfoData() {
		liston.clear();
		if (checkModels != null && checkModels.size() > 0) {
			for (int j = 0; j < checkModels.size(); j++) {
				Map<String, String> map = new HashMap<>();
				map.put("childId", checkModels.get(j).getUid() + "");
				map.put("level", checkModels.get(j).getLevel() + "");
				map.put("type", checkModels.get(j).getType() + "");
				liston.add(map);
			}
		} else {
			List<String> list = new ArrayList<String>();
			for (int i = 1; i < 7; i++) {
				switch (i) {
				case 1:
					list = InShcoolnfoActivity.list1;
					break;
				case 2:
					list = InShcoolnfoActivity.list2;
					break;
				case 3:
					list = InShcoolnfoActivity.list3;
					break;
				case 4:
					list = InShcoolnfoActivity.list4;
					break;
				case 5:
					list = InShcoolnfoActivity.list5;
					break;
				case 6:
					list = InShcoolnfoActivity.list6;
					break;
				}
				for (int j = 0; j < boyModels.size(); j++) {
					Map<String, String> map = new HashMap<>();
					map.put("childId", boyModels.get(j).getUid() + "");
					map.put("level", list.get(j));
					map.put("type", i + "");
					liston.add(map);
				}
			}
		}
		return liston;
	}

	void setinfoList() {
		JSONObject data = mCache
				.getAsJSONObject(IUrContant.GET_SCHOOL_INFO_HISSTRONG_URL
						+ chooseDay + app.getClassModel());
		checkModels.clear();
		if (data != null) {
			JSONArray array = data.optJSONArray("spList");
			for (int i = 0; i < array.length(); i++) {
				JSONObject level = array.optJSONObject(i);
				BoyCheckModel model = new BoyCheckModel();
				model.setLevel(level.optInt("level"));
				model.setType(level.optInt("type"));
				model.setUid(level.optInt("child_id"));
				checkModels.add(model);
			}
		}
		adapter = new InSchoolInfoViewPagerAdapter(fragmentManager, boyModels,
				isSend);
		viewPager.setAdapter(adapter);
	}
}