package cn.kiway.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.model.ClassModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.ViewUtil;

public class ScanActivity extends BaseActivity {
	ImageView imageView;// 二维码图片
	ClassModel classModel;
	String url;// 二维码的地址

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		classModel = (ClassModel) bundle
				.getSerializable(IConstant.BUNDLE_PARAMS);
		if (classModel != null)
			url = IUrContant.BASE_URL + "?app=kids-t&ref=class&classid="
					+ classModel.getId() + "&schoolname="
					+ classModel.getSchoolName() + "&classname="
					+ classModel.getClassName() + "&teacher=" + app.getName();
		// url =
		// "http://www.kiway.cn/dl?app=kids-t&ref=box&ip=192.168.43.1&port=12332&ssid=KiwayHezi&pwd=12345678&cid=1b534d30303030301029db557201012f&class=2&version=1";
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initView() throws Exception {
		imageView = ViewUtil.findViewById(this, R.id.scan);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				displayMetrics.widthPixels - 80, displayMetrics.widthPixels);
		imageView.setLayoutParams(layoutParams);
		imageView.setImageBitmap(AppUtil.createQRImage(url,
				displayMetrics.widthPixels, displayMetrics.widthPixels));// 设置二维码图片
		ViewUtil.setContent(this, R.id.class_name, classModel.getClassName());// 班级名字
		ViewUtil.setContent(this, R.id.school_name, classModel.getSchoolName());// 学校名字
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("url", url);
		outState.putSerializable("classModel", classModel);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("url"))
			url = savedInstanceState.getString("url");
		if (savedInstanceState.containsKey("classModel"))
			classModel = (ClassModel) savedInstanceState
					.getSerializable("savedInstanceState");
	}
}
