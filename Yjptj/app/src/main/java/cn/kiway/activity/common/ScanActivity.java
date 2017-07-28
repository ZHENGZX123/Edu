package cn.kiway.activity.common;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.ViewUtil;

public class ScanActivity extends BaseActivity {
	ImageView imageView;
	String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		url = IUrContant.BASE_URL + "?app=kids-t&ref=class&classid="
				+ app.getBoyModels().get(app.getPosition()).getClassId()
				+ "&schoolname="
				+ app.getBoyModels().get(app.getPosition()).getSchoolName()
				+ "&classname="
				+ app.getBoyModels().get(app.getPosition()).getClassName()
				+ "&teacher="
				+ app.getBoyModels().get(app.getPosition()).getTeacherName();
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	void initView() throws Exception {
		imageView = ViewUtil.findViewById(this, R.id.scan);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				displayMetrics.widthPixels - 80, displayMetrics.widthPixels);
		imageView.setLayoutParams(layoutParams);
		imageView.setImageBitmap(AppUtil.createQRImage(url,
				displayMetrics.widthPixels, displayMetrics.widthPixels));
		ViewUtil.setContent(this, R.id.class_name,
				app.getBoyModels().get(app.getPosition()).getClassName());
		ViewUtil.setContent(this, R.id.school_name,
				app.getBoyModels().get(app.getPosition()).getSchoolName());
	}
}
