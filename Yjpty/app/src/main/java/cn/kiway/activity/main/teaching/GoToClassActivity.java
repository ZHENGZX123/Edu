package cn.kiway.activity.main.teaching;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.activity.MipcaCaptureActivity;
import cn.kiway.dialog.classd.ChooseClassDialog;
import cn.kiway.model.ClassModel;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.ViewUtil;

public class GoToClassActivity extends BaseNetWorkActicity {
	TextView title;
	ChooseClassDialog chooseClassDialog;
	List<ClassModel> list = new ArrayList<ClassModel>();
	String wifiName;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		list = (List<ClassModel>) bundle// 获取到班级列表的数据
				.getSerializable(IConstant.BUNDLE_PARAMS);
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initView() throws Exception {
		setContentView(R.layout.activity_goto_class);
		chooseClassDialog = new ChooseClassDialog(context, list, true);
		if (chooseClassDialog != null && !chooseClassDialog.isShowing()
				&& list.size() > 1) {// 判断班级数是否为1，为1则不弹出选择班级的页面，直接默认选中
			chooseClassDialog.show();
		} else if (list.size() == 1) {
			app.setClassModel(list.get(0));
			if (list.get(0).getHeZiCode().equals("null")) {// 判断是否有绑定盒子
				findViewById(R.id.layout2).setVisibility(View.VISIBLE);// 没绑定盒子的界面
				findViewById(R.id.layout).setVisibility(View.GONE);// 绑定了盒子的界面
			} else {
				findViewById(R.id.layout2).setVisibility(View.GONE);// 没绑定盒子的界面
				findViewById(R.id.layout).setVisibility(View.VISIBLE);// 绑定了盒子的界面
			}
		}
		findViewById(R.id.teaching_plans).setOnClickListener(this);
		findViewById(R.id.go_to_class).setOnClickListener(this);
		findViewById(R.id.sao_yi_sao).setOnClickListener(this);
		findViewById(R.id.tellphone).setOnClickListener(this);
		title = ViewUtil.findViewById(this, R.id.name);
		ViewUtil.setTextFontColor(title, null,
				resources.getColor(R.color._f41100), 1, title.length() - 4);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Bundle bundle = new Bundle();
		switch (v.getId()) {
		case R.id.teaching_plans:
			bundle.putBoolean(IConstant.BUNDLE_PARAMS, false);// 1为上课 2看备课
			startActivity(TeachingPlansActivity.class, bundle);
			finish();
			break;
		case R.id.go_to_class:
			wifiName = SharedPreferencesUtil.getString(this,
					IConstant.WIFI_NEME + app.getClassModel().getId());
			if (wifiName.split(":::")[0].equals("")) {// 判断是否保存盒子的wifi有的话则直接进入，否的话则扫描后进入
				bundle.putInt(IConstant.BUNDLE_PARAMS, 1);
				startActivity(MipcaCaptureActivity.class, bundle);
			} else {
				if (Boolean.parseBoolean(wifiName.split(":::")[1])
						&& !SharedPreferencesUtil.getBoolean(this,
								SendWifiNameActivity.IS_NOTIFY
										+ app.getClassModel().getHeZiCode())) {
					startActivity(SendWifiNameActivity.class);
				} else {
					bundle.putBoolean(IConstant.BUNDLE_PARAMS, true);// 1为上课2看备课
					startActivity(TeachingPlansActivity.class, bundle);
				}
				finish();
			}
			break;
		case R.id.sao_yi_sao:// 扫一扫
			bundle.putInt(IConstant.BUNDLE_PARAMS, 1);
			startActivity(MipcaCaptureActivity.class, bundle);
			finish();
			break;
		case R.id.tellphone:
			Toast.makeText(getApplicationContext(), "暂无订购电话",
					Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
