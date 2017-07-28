package cn.kiway.activity.main.creatclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.activity.MipcaCaptureActivity;
import cn.kiway.activity.ScanActivity;
import cn.kiway.adapter.ClassInfoAdapter;
import cn.kiway.dialog.ClearDataDialog;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.dialog.ClearDataDialog.ClearDataCallBack;
import cn.kiway.dialog.classd.EditBabyDialog;
import cn.kiway.dialog.classd.EditBabyDialog.EditBabyNameCallBack;
import cn.kiway.dialog.NewVersionDialog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.message.model.MessageChatProvider;
import cn.kiway.message.model.MessageProvider;
import cn.kiway.model.BoyModel;
import cn.kiway.model.ClassModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class ClassInfoActivity extends BaseNetWorkActicity implements
		EditBabyNameCallBack, ClearDataCallBack {
	ExpandableListView listView;
	ClassInfoAdapter adapter;
	View head;// 头部的图像
	EditText addBaby;// 增加宝贝
	EditBabyDialog addClassDialog;// 创建班级dialog
	ClassModel classModel;
	ClearDataDialog dialog;// 解除绑定盒子
	View view;
	public static boolean isLoad = false;
	NewVersionDialog dialog2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		classModel = (ClassModel) bundle
				.getSerializable(IConstant.BUNDLE_PARAMS);
		if (!AppUtil.isNetworkAvailable(this)// 判断网络是否可用
				&& mCache.getAsJSONObject(IUrContant.CALSS_URL
						+ classModel.getId()) == null) {
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
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("classModel", classModel);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("classModel"))
			classModel = (ClassModel) savedInstanceState
					.getSerializable("classModel");
	}

	@Override
	public void initView() throws Exception {
		setContentView(R.layout.activity_class_info);
		head = ViewUtil.inflate(this, R.layout.class_info_header);
		listView = ViewUtil.findViewById(this, R.id.listview);
		addBaby = ViewUtil.findViewById(this, R.id.add_baby);
		view = ViewUtil.findViewById(head, R.id.remove);
		listView.addHeaderView(head);
		adapter = new ClassInfoAdapter(this, new ArrayList<BoyModel>(),
				new ArrayList<BoyModel>(), classModel.getCreateId());
		listView.setAdapter(adapter);
		listView.setDivider(null);
		addClassDialog = new EditBabyDialog(this,
				resources.getString(R.string.jions_class),
				resources.getString(R.string.tcbj), this);
		dialog = new ClearDataDialog(this, this,
				resources.getString(R.string.quedingjieb), view);
		dialog2 = new NewVersionDialog(this, this);
		findViewById(R.id.remove).setOnClickListener(this);
		findViewById(R.id.notify_parent).setOnClickListener(this);
		findViewById(R.id.new_baby).setOnClickListener(this);
		findViewById(R.id.more).setOnClickListener(this);
		findViewById(R.id.scan_head).setOnClickListener(this);
		for (int i = 0; i < 2; i++) {// 展开ExpandableListView子集
			listView.expandGroup(i);
		}
		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader,
				false, false));
	}

	@Override
	public void loadData() throws Exception {
		super.loadData();
		Map<String, String> map = new HashMap<>();
		map.put("classId", classModel.getId() + "");
		map.put("userId", app.getUid() + "");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.CALSS_URL, map,
				activityHandler, true);
	}

	@Override
	public void setData() throws Exception {
		JSONObject jsonObject = mCache.getAsJSONObject(IUrContant.CALSS_URL
				+ classModel.getId());
		if (jsonObject != null) {
			ViewUtil.setContent(
					head,
					R.id.baby,
					jsonObject.optString("childrenNum") + "\n"
							+ resources.getString(R.string.baby));// 宝贝数
			ViewUtil.setContent(
					head,
					R.id.teacher,
					jsonObject.optString("teacherNum") + "\n"
							+ resources.getString(R.string.teacher));// 老师数
			ViewUtil.setContent(head, R.id.class_i, classModel.getClassName());// 班级名字
			ViewUtil.setContent(head, R.id.title, classModel.getClassName());// 标题名字
			ViewUtil.setContent(head, R.id.class_name,
					classModel.getSchoolName());// 幼儿园名字
			switch (classModel.getYear()) {
			case 1:
				ViewUtil.setContent(head, R.id.class_year, R.string.ddb);
				break;
			case 2:
				ViewUtil.setContent(head, R.id.class_year, R.string.db);
				break;
			case 3:
				ViewUtil.setContent(head, R.id.class_year, R.string.zb);
				break;
			case 4:
				ViewUtil.setContent(head, R.id.class_year, R.string.xb);
				break;
			}
			if (classModel.getHeZiCode().equals("null")) {// 盒子编号
				ViewUtil.setContent(head, R.id.box,
						R.string.haimeiyoubandinghezi);
				ViewUtil.setContent(head, R.id.remove, R.string.bandinghezi);
			} else {
				ViewUtil.setContent(head, R.id.box, classModel.getHeZiCode());
				ViewUtil.setContent(head, R.id.remove, R.string.remove);
			}
			JSONArray teacher = null;
			teacher = jsonObject.optJSONArray("teacherList");
			if (teacher != null) {// 老师列表
				adapter.teacherlist.clear();
				for (int i = 0; i < teacher.length(); i++) {
					JSONObject item = teacher.getJSONObject(i);
					BoyModel boyModel = new BoyModel();
					boyModel.setImg(item.optString("photo"));// 老师头像
					boyModel.setName(item.optString("name"));// 老师名字
					boyModel.setUid(item.optLong("user_id"));// 老师Id
					boyModel.setClassId(item.optInt("class_id"));// 班级Id
					boyModel.setPhone(item.optString("phone"));// 电话
					adapter.teacherlist.add(boyModel);
				}
			}
			JSONArray childern = null;
			childern = jsonObject.optJSONArray("childrenList");
			if (teacher != null) {// 学生列表
				adapter.childerlist.clear();
				for (int i = 0; i < childern.length(); i++) {
					JSONObject item = childern.getJSONObject(i);
					BoyModel boyModel = new BoyModel();
					boyModel.setImg(item.optString("avatar"));// 孩子头像
					boyModel.setName(item.optString("child_name"));// 孩子名字
					boyModel.setUid(item.optLong("id"));// 孩子id
					boyModel.setSex(item.optInt("sex"));// 孩子性别
					boyModel.setBrithday(item.optString("birthday"));// 孩子生日
					adapter.childerlist.add(boyModel);
				}
			}
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Map<String, String> map = new HashMap<>();
		Bundle bundle = new Bundle();
		switch (v.getId()) {
		case R.id.new_baby:
			if (StringUtil.isEmpty(ViewUtil.getContent(this, R.id.add_baby))) {// 新加宝贝
																				// 判断宝贝名字是否为空
				ViewUtil.showMessage(this, R.string.please_input_baby_name);
				return;
			}
			map.put("classId", classModel.getId() + "");
			map.put("childName", StringUtil.replaceBlank(ViewUtil.getContent(
					this, R.id.add_baby)));
			IConstant.HTTP_CONNECT_POOL.addRequest(
					IUrContant.ADD_CLASS_BABY_URL, map, activityHandler, true);
			break;
		case R.id.more:
			if (addClassDialog != null && !addClassDialog.isShowing()) {// 新加班级
				addClassDialog.show();
			}
			break;
		case R.id.scan_head:
			bundle.putSerializable(IConstant.BUNDLE_PARAMS, classModel);// 生成二维码
			startActivity(ScanActivity.class, bundle);
			break;
		case R.id.remove:
			if (classModel.getHeZiCode().equals("null")) {// 绑定盒子
				app.setClassModel(classModel);
				bundle.putInt(IConstant.BUNDLE_PARAMS, 2);// 1为上课 2看备课
				startActivity(MipcaCaptureActivity.class, bundle);
			} else {
				if (dialog != null && !dialog.isShowing()) {// 解绑盒子
					dialog.show();
				}
			}
			break;
		case R.id.notify_parent:// 发送eimal
			bundle.putSerializable(IConstant.BUNDLE_PARAMS, classModel);// 生成二维码
			startActivity(EmialActivity.class, bundle);
			break;
		}
	}

	@Override
	public void editBabyNameCallBack() throws Exception {
		Bundle bundle = new Bundle();
		bundle.putInt(IConstant.BUNDLE_PARAMS, 2);// 1注册进去，2登录后创建班级进入
		startActivity(JoinClassActivity.class, bundle);
		finish();
	}

	@Override
	public void deleteBabyCallBack() throws Exception {// 退出/删除班级
		if (dialog2 == null)
			return;
		if (app.getUid() == app.getClassModel().getCreateId()) {
			dialog2.setTitle("确定删除" + app.getClassModel().getClassName() + "?");
		} else {
			dialog2.setTitle("确定退出" + app.getClassModel().getClassName() + "?");
		}
		dialog2.setIsShow(true);
		if (!dialog2.isShowing()) {
			dialog2.show();
		}
	}

	@Override
	public void clearDataCallBack(View vx) throws Exception {// 解绑盒子
		Map<String, String> map = new HashMap<>();// 这里调用的是绑定盒子的接口，因为后台只
		map.put("classId", app.getClassModel().getId() + "");// 是存个盒子编号的字段，所以解绑的话，只是把后台的字段改为null
		map.put("hCode", "null");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.BANG_DING_HE_ZI_URL,
				map, activityHandler, true);
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.CALSS_URL)) {// 获取到班级数据
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				mCache.put(IUrContant.CALSS_URL + classModel.getId(),
						new JSONObject(new String(message.getResponse())));
				setData();
			}
		} else if (message.getUrl().equals(IUrContant.ADD_CLASS_BABY_URL)) {// 添加宝贝成功
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				ViewUtil.hideKeyboard(this);
				addBaby.setText("");
				loadData();
				app.getClassModel().setChildNum(
						app.getClassModel().getChildNum() + 1);
			}
		} else if (message.getUrl().equals(IUrContant.BANG_DING_HE_ZI_URL)) {// 解除绑定盒子成功
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {//
				ViewUtil.setContent(head, R.id.box,
						R.string.haimeiyoubandinghezi);
				ViewUtil.setContent(head, R.id.remove, R.string.bandinghezi);
				app.getClassModel().setHeZiCode("null");
				classModel.setHeZiCode("null");
			}
		} else if (message.getUrl().equals(IUrContant.EXIT_CLASS_URL)) {// 退出班级成功
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				getContentResolver().delete(
						Uri.parse(MessageProvider.MESSAGES_URL),
						"(_touid=? or _touid=?) and  _msgtype=? ",
						new String[] { "" + app.getUid(),
								"" + classModel.getId() + "" });
				getContentResolver().delete(
						Uri.parse(MessageChatProvider.MESSAGECHATS_URL),
						"_touid=?", new String[] { "" + classModel.getId() });
				ViewUtil.showMessage(this, R.string.tcbjcg);
				finish();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isLoad) {
			try {
				if (app.getClassModel() != null)
					classModel = app.getClassModel();
				loadData();
				isLoad = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void newVersionOkCallBack() throws Exception {
		super.newVersionOkCallBack();
		Map<String, String> map = new HashMap<>();
		map.put("classId", classModel.getId() + "");// 班级id
		map.put("teacherId", app.getUid() + "");// 老师id
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.EXIT_CLASS_URL, map,
				activityHandler, true);
	}
}
