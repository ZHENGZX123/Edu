package cn.kiway.dialog.classd;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.main.creatclass.EditBabyNameActivity;
import cn.kiway.dialog.BaseDialog;
import cn.kiway.utils.ViewUtil;

public class EditBabyDialog extends BaseDialog {
	EditBabyNameCallBack callBack;
	BaseActivity activity;

	public EditBabyDialog(Context context, String string, String s,
			EditBabyNameCallBack callBack) {
		super(context);
		view = ViewUtil.inflate(context, R.layout.dialog_baby_detail);
		fullWindowTop(context);
		setContentView(view, layoutParams);
		this.callBack = callBack;
		this.activity = (BaseActivity) context;
		view.findViewById(R.id.edit_name).setOnClickListener(this);
		view.findViewById(R.id.detele_baby).setOnClickListener(this);
		ViewUtil.setContent(view, R.id.edit_name, string);
		ViewUtil.setContent(view, R.id.detele_baby, s);
		if (s.equals(((BaseActivity) context).resources
				.getString(R.string.tcbj))) {
			view.findViewById(R.id.view).setVisibility(View.VISIBLE);
			view.findViewById(R.id.edit_classname).setVisibility(View.VISIBLE);
			view.findViewById(R.id.edit_classname).setOnClickListener(this);
			if (activity.app.getUid() == activity.app.getClassModel()
					.getCreateId()) {
				view.findViewById(R.id.view).setVisibility(View.GONE);
				view.findViewById(R.id.detele_baby).setVisibility(View.GONE);
				ViewUtil.setContent(view, R.id.detele_baby, "删除班级");
			}
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.edit_name:
			if (callBack != null) {
				try {
					callBack.editBabyNameCallBack();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.detele_baby:
			if (callBack != null) {
				try {
					callBack.deleteBabyCallBack();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.edit_classname:// 只在班级信息修改班级名字的时候用到
			Bundle b = new Bundle();
			b.putBoolean(IConstant.BUNDLE_PARAMS1, false);
			activity.startActivity(EditBabyNameActivity.class, b);
			break;
		case R.id.jion_class:
			break;
		}
		dismiss();
	}

	public interface EditBabyNameCallBack {
		public void editBabyNameCallBack() throws Exception;

		public void deleteBabyCallBack() throws Exception;
	}
}
