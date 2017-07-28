package cn.kiway.activity.main.creatclass;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.model.BoyModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class TeacherDetailActivity extends BaseNetWorkActicity {
	ImageView userImg;// 老师头像
	BoyModel boyModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teacher_detail);
		boyModel = (BoyModel) bundle.getSerializable(IConstant.BUNDLE_PARAMS);
		try {
			initView();
			setData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("boyModel", boyModel);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("boyModel"))
			boyModel = (BoyModel) savedInstanceState
					.getSerializable("boyModel");
	}

	@Override
	public void initView() throws Exception {
		userImg = ViewUtil.findViewById(this, R.id.teacher_img);
		findViewById(R.id.call).setOnClickListener(this);
	}

	@Override
	public void setData() throws Exception {
		if (boyModel == null)
			return;
		imageLoader.displayImage(StringUtil.imgUrl(this, boyModel.getImg()),
				userImg, fadeOptions);
		if (boyModel.getName().equals("null")) {
			switch (boyModel.getType()) {// 如果名字为null的话，就展示与孩子的关系
			case 1:
				boyModel.setName(resources.getString(R.string.dad));
				break;
			case 2:
				boyModel.setName(resources.getString(R.string.mun));
				break;
			case 3:
				boyModel.setName(resources.getString(R.string.qin));
				break;
			}
		}
		ViewUtil.setContent(this, R.id.name, boyModel.getName());
		ViewUtil.setContent(this, R.id.title, boyModel.getName());
		ViewUtil.setContent(this, R.id.phone, boyModel.getPhone() + "");
		if (boyModel.getPhone().equals("") || boyModel.getPhone() == null
				|| boyModel.getPhone().equals("null")) {
			ViewUtil.setContent(this, R.id.phone, "");
			findViewById(R.id.call).setEnabled(false);
			ViewUtil.setTextFontColor(this, R.id.call, null,
					resources.getColor(R.color._666666));
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.call:
			if (boyModel.getPhone().equals("") || boyModel.getPhone() == null) {
				ViewUtil.showMessage(this, "无效电话");
				return;
			}
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ boyModel.getPhone()));
			startActivity(intent);
			break;
		}
	}
}
