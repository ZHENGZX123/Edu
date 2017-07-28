/*package cn.kiway.activity.main.creatclass;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.activity.me.EditAeraActivity;
import cn.kiway.adapter.common.SpinnerAdapter;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.login.LoadingActivity;
import cn.kiway.message.util.WriteMsgUitl;
import cn.kiway.model.ClassModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class ClassEditActivity extends BaseNetWorkActicity {
	ClassModel classModel;
	Spinner spinner;
	SpinnerAdapter adapter;

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
		spinner = ViewUtil.findViewById(this, R.id.question_select);// 下来列表
		findViewById(R.id.create_class).setOnClickListener(this);// 创建班级
		findViewById(R.id.area_lin).setOnClickListener(this);// 选择地区
		findViewById(R.id.yey_lin).setOnClickListener(this);// 选择幼儿园
	}

	@Override
	public void setData() throws Exception {
		if (bundle.getInt(IConstant.BUNDLE_PARAMS) != 1
				&& bundle.getInt(IConstant.BUNDLE_PARAMS) != 3) {// 判断是否加入了班级，是的话只展示属于自己的学校名字
			ViewUtil.setContent(this, R.id.yey_val, app.getClassModel()
					.getSchoolName());
			findViewById(R.id.yey_lin).setEnabled(false);
			classModel = app.getClassModel();
			findViewById(R.id.area_lin).setVisibility(View.GONE);
			ViewUtil.setContent(this, R.id.yey, "幼儿园");
			ViewUtil.setArroundDrawable(this, R.id.yey_val, -1, -1, -1, -1);
			findViewById(R.id.view1).setVisibility(View.GONE);
			findViewById(R.id.view2).setVisibility(View.GONE);
		}
		adapter = new SpinnerAdapter(this);
		spinner.setAdapter(adapter);
		spinner.setSelection(3);
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
		case R.id.create_class:// 创建幼儿园
			if (StringUtil.isEmpty(ViewUtil.getContent(this, R.id.yey_val))) {
				ViewUtil.showMessage(this, "请选择幼儿园");
				return;
			}
			if (StringUtil.isEmpty(ViewUtil.getContent(this, R.id.edit_c))) {// 判断班级名字是否为空
				ViewUtil.showMessage(this, R.string.mysrbj);
				return;
			}
			Map<String, String> map = new HashMap<>();
			map.put("schoolId", classModel.getSchoolId() + "");
			map.put("className", ViewUtil.getContent(this, R.id.edit_c));
			map.put("userId", app.getUid() + "");
			map.put("gradeId", (spinner.getSelectedItemPosition() + 1) + "");
			IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.CREAT_CLASSS_URL,
					map, activityHandler, true);
			break;
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.CREAT_CLASSS_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null) {
				if (data.optInt("retcode") == 1) {
					if (bundle.getInt(IConstant.BUNDLE_PARAMS) == 1) {// 判断是否在注册进入，是的话创建成功放回到登录页面，否进入班级详情
						finishAllAct();// 创建班级成功，提示需要审核，然后回到登录页面
						finish();
						ViewUtil.showMessage(this, R.string.tijiaposhenghe);
						startActivity(LoadingActivity.class);
					} else {// 创建新的数据源然后进入到班级详情
						ClassModel model = new ClassModel();
						model.setId(data.optInt("classId"));// 班级id
						model.setClassName(ViewUtil.getContent(
								ClassEditActivity.this, R.id.edit_c));// 班级名字
						model.setSchoolName(classModel.getSchoolName());// 学校名字
						model.setHeZiCode("null");// 盒子编号
						model.setSchoolId(classModel.getSchoolId());// 学校id
						model.setYear(spinner.getSelectedItemPosition() + 1);
						app.setClassModel(model);
						Bundle bundle = new Bundle();
						bundle.putSerializable(IConstant.BUNDLE_PARAMS, model);
						startActivity(ClassInfoActivity.class, bundle);
						finish();
						SharedPreferencesUtil.save(this,
								IConstant.CHANGE_CLASS, SharedPreferencesUtil
										.getInteger(this,
												IConstant.CHANGE_CLASS) + 1);// 设置默认选中的班级
						WriteMsgUitl.WriteClassData(this, app,
								ViewUtil.getContent(ClassEditActivity.this,
										R.id.edit_c), data.optInt("classId"));
					}
				} else if (data.optInt("retcode") == 22) {
					ViewUtil.showMessage(this, R.string.bjmzyjbcj);
				} else {
					ViewUtil.showMessage(this, "创建失败");
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
		} else if (requstCode == 2) {// 幼儿园
			classModel = (ClassModel) data
					.getSerializableExtra(IConstant.BUNDLE_PARAMS);
			ViewUtil.setContent(this, R.id.yey_val, classModel.getSchoolName());
		}
	}
}
*/