package cn.kiway.dialog.choosebaby;

import cn.kiway.Yjptj.R;
import cn.kiway.activity.choosebaby.ProfileBabyActivity;
import cn.kiway.dialog.BaseDialog;
import cn.kiway.utils.ViewUtil;
import android.content.Context;
import android.view.View;

public class ChooseBabySexDialog extends BaseDialog {
	ProfileBabyActivity activity;
	int sex;

	public ChooseBabySexDialog(Context context, int sex) {
		super(context);
		this.activity = (ProfileBabyActivity) context;
		view = ViewUtil.inflate(context, R.layout.dialog_choose_sex);
		fullWindowBottom(context);
		setContentView(view, layoutParams);
		view.findViewById(R.id.boy).setOnClickListener(this);
		view.findViewById(R.id.girl).setOnClickListener(this);
		this.sex = sex;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.boy:
			activity.setSex(1);
			ViewUtil.setContent(activity, R.id.sex_val, R.string.boy);
			break;
		case R.id.girl:
			activity.setSex(2);
			ViewUtil.setContent(activity, R.id.sex_val, R.string.girl);
			break;
		}
		dismiss();
	}
}
