package cn.kiway.dialog.choosebaby;

import cn.kiway.Yjptj.R;
import cn.kiway.activity.choosebaby.ProfileBabyActivity;
import cn.kiway.dialog.BaseDialog;
import cn.kiway.utils.ViewUtil;
import android.content.Context;
import android.view.View;

public class ChoosePraentDialog extends BaseDialog {
	ProfileBabyActivity activity;
	int parent;

	public ChoosePraentDialog(Context context, int parent) {
		super(context);
		this.activity = (ProfileBabyActivity) context;
		this.parent = parent;
		view = ViewUtil.inflate(context, R.layout.dialog_choose_praent);
		fullWindowBottom(context);
		setContentView(view, layoutParams);
		view.findViewById(R.id.dad).setOnClickListener(this);
		view.findViewById(R.id.mun).setOnClickListener(this);
		view.findViewById(R.id.qin).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.dad:
			ViewUtil.setContent(activity, R.id.who_val, R.string.dad);
			activity.setPraent(1);
			break;
		case R.id.mun:
			ViewUtil.setContent(activity, R.id.who_val, R.string.mun);
			activity.setPraent(2);
			break;
		case R.id.qin:
			ViewUtil.setContent(activity, R.id.who_val, R.string.qin);
			activity.setPraent(3);
			break;
		}
		dismiss();
	}
}
