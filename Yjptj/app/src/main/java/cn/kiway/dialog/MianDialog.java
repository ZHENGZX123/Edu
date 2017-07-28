package cn.kiway.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import cn.kiway.IConstant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.message.AddPeopleActivity;
import cn.kiway.utils.ViewUtil;

public class MianDialog extends BaseDialog {
	BaseActivity activity;

	public MianDialog(Context context) {
		super(context);
		this.activity = (BaseActivity) context;
		view = ViewUtil.inflate(context, R.layout.dialog_mian);
		fullWindowTop(context);
		setContentView(view, layoutParams);
		view.findViewById(R.id.create).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.create:
			dismiss();
			Bundle bundle = new Bundle();
			bundle.putBoolean(IConstant.BUNDLE_PARAMS, false);
			activity.startActivity(AddPeopleActivity.class, bundle);
			break;
		}
	}
}
