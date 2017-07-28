package cn.kiway.activity.main.creatclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.activity.MainActivity;
import cn.kiway.activity.me.EditAeraActivity;
import cn.kiway.adapter.JoinsClassAdapter;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.login.LoadingActivity;
import cn.kiway.model.ClassModel;
import cn.kiway.model.JoinsClassModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class JoinClassActivity extends BaseNetWorkActicity {
	ClassModel classModel;
	ListView listView;
	JoinsClassAdapter classAdapter;
	JSONArray arrayJsonArray;
	EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!AppUtil.isNetworkAvailable(this)) {// 判断网络是否可用
			newWorkdialog = new IsNetWorkDialog(context, this,
					resources.getString(R.string.dqsjmylrhlwqljhlwl),
					resources.getString(R.string.ljhlw));
			if (newWorkdialog != null && !newWorkdialog.isShowing()) {
				newWorkdialog.show();
				return;
			}
		}
		setContentView(R.layout.activity_create_class_edit);
		try {
			initView();
			setData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initView() throws Exception {
		findViewById(R.id.create_class).setOnClickListener(this);// 创建班级
		findViewById(R.id.area_lin).setOnClickListener(this);// 选择地区
		findViewById(R.id.yey_lin).setOnClickListener(this);// 选择幼儿园
		findViewById(R.id.join).setOnClickListener(this);
		listView = ViewUtil.findViewById(this, R.id.listview);
		editText = ViewUtil.findViewById(this, R.id.code);
		classAdapter = new JoinsClassAdapter(this,
				new ArrayList<JoinsClassModel>());
		listView.setAdapter(classAdapter);
	}

	@Override
	public void setData() throws Exception {
		arrayJsonArray = mCache.getAsJSONObject(IUrContant.GET_CLASS_LIST_URL)
				.optJSONArray("classList");
		if (bundle.getInt(IConstant.BUNDLE_PARAMS) != 1
				&& bundle.getInt(IConstant.BUNDLE_PARAMS) != 3) {// 判断是否加入了班级，是的话只展示属于自己的学校名字
			ViewUtil.setContent(this, R.id.yey_val, app.getClassModel()
					.getSchoolName());
			findViewById(R.id.yey_lin).setEnabled(false);
			classModel = app.getClassModel();
			findViewById(R.id.area_lin).setVisibility(View.GONE);
			editText.setVisibility(View.GONE);
			ViewUtil.setContent(this, R.id.yey, "幼儿园");
			ViewUtil.setArroundDrawable(this, R.id.yey_val, -1, -1, -1, -1);
			findViewById(R.id.view1).setVisibility(View.GONE);
			findViewById(R.id.view2).setVisibility(View.GONE);
			Map<String, String> map = new HashMap<String, String>();
			map.put("schoolId", classModel.getSchoolId() + "");
			IConstant.HTTP_CONNECT_POOL.addRequest(
					IUrContant.GET_ALL_CLASS_URL, map, activityHandler, true);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Bundle b = new Bundle();
		switch (v.getId()) {
		case R.id.area_lin:// 选择地区
			b.putBoolean(IConstant.BUNDLE_PARAMS, false);
			Intent intent3 = new Intent(this, EditAeraActivity.class);
			intent3.putExtras(b);
			startActivityForResult(intent3, 3);
			break;
		case R.id.yey_lin:// 选择幼儿园
			if (StringUtil.isEmpty(ViewUtil.getContent(this, R.id.area_val))) {
				ViewUtil.showMessage(this, "请先选择地区");
				return;
			}
			b.putString(IConstant.BUNDLE_PARAMS,
					ViewUtil.getContent(this, R.id.area_val));
			Intent intent2 = new Intent(this, ClassNameListActivity.class);
			intent2.putExtras(b);
			startActivityForResult(intent2, 2);
			break;

		case R.id.join:
			if (classAdapter.getCount() <= 0 || getSelectClassId().equals("")) {
				ViewUtil.showMessage(this, "请选择班级");
				return;
			}
			if (editText.getVisibility() == View.VISIBLE
					&& editText.getText().toString().length() <= 0) {
				ViewUtil.showMessage(this, "请输入邀请码");
				return;
			}
			Map<String, String> map = new HashMap<>();
			map.put("inviteCode", editText.getText().toString());
			map.put("schoolId", classModel.getSchoolId() + "");
			map.put("userId", app.getUid() + "");
			map.put("classIds", getSelectClassId());
			IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.JOINS_CLASS_URL,
					map, activityHandler, true);
			break;
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.GET_ALL_CLASS_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null) {
				if (data.optInt("retcode") == 1) {
					JSONArray array = data.optJSONArray("classList");
					for (int i = 0; i < array.length(); i++) {
						JoinsClassModel model = new JoinsClassModel();
						JSONObject item = array.optJSONObject(i);
						boolean isture = false;
						if (data != null) {
							if (arrayJsonArray != null) {
								for (int j = 0; j < arrayJsonArray.length(); j++) {
									if (arrayJsonArray.optJSONObject(j).optInt(
											"id") == item.optInt("id")) {
										isture = true;
									}
								}
							}
						}
						if (!isture) {
							model.setClassGraid(item.optInt("grade_id"));
							model.setClassId(item.optInt("id"));
							model.setClassName(item.optString("class_name"));
							model.setSelect(false);
							classAdapter.list.add(model);
						}
					}
				}
				classAdapter.notifyDataSetChanged();
				if (classAdapter.getCount() <= 0) {
					ViewUtil.showMessage(JoinClassActivity.this,
							"该学校还没有班级，联系我们创建班级吧");
				}
			}
		} else if (message.getUrl().equals(IUrContant.JOINS_CLASS_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null) {
				if (data.optInt("retcode") == 1) {
					finishAllAct();
					finish();
					/*
					 * if (bundle.getInt(IConstant.BUNDLE_PARAMS) == 1) {//
					 * 判断是否在注册进入，是的话创建成功放回到登录页面，否进入班级详情
					 * ViewUtil.showMessage(this, R.string.tijiaposhenghe);//
					 * 创建班级成功，提示需要审核，然后回到登录页面
					 * startActivity(LoadingActivity.class); } else {
					 */
					ViewUtil.showMessage(this, "加入成功");
					startActivity(MainActivity.class);
					// }
				} else if (data.optInt("retcode") == 11) {
					ViewUtil.showMessage(this, "邀请码错误");
				} else {
					ViewUtil.showMessage(this, "加入班级失败,请重试");
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requstCode, int resultCode, Intent data) {
		super.onActivityResult(requstCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		if (requstCode == 3) {// 地区
			ViewUtil.setContent(this, R.id.area_val,
					data.getStringExtra(IConstant.BUNDLE_PARAMS));
			ViewUtil.setContent(this, R.id.yey_val, "");
			classAdapter.list.clear();
			classAdapter.notifyDataSetChanged();
		} else if (requstCode == 2) {// 幼儿园
			classModel = (ClassModel) data
					.getSerializableExtra(IConstant.BUNDLE_PARAMS);
			ViewUtil.setContent(this, R.id.yey_val, classModel.getSchoolName());
			Map<String, String> map = new HashMap<String, String>();
			map.put("schoolId", classModel.getSchoolId() + "");
			IConstant.HTTP_CONNECT_POOL.addRequest(
					IUrContant.GET_ALL_CLASS_URL, map, activityHandler, true);
			classAdapter.list.clear();
			classAdapter.notifyDataSetChanged();
		}
	}

	String getSelectClassId() {
		String string = "";
		for (int i = 0; i < classAdapter.list.size(); i++) {
			if (classAdapter.list.get(i).isSelect()) {
				if (string.equals("")) {
					string = classAdapter.list.get(i).getClassId() + "";
				} else {
					string = string + "#"
							+ classAdapter.list.get(i).getClassId();
				}
			}
		}
		return string;
	}
}
