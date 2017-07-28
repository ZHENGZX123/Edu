package cn.kiway.activity.choosebaby;

import android.os.Bundle;
import android.view.View;
import cn.kiway.IConstant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class EditTeacherPhoneActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_teachername);
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void initView() throws Exception {
		findViewById(R.id.sure).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.sure:
			ViewUtil.hideKeyboard(this);
			if (!StringUtil.isMobileNum(ViewUtil.getContent(this,
					R.id.notify_edit))) {
				ViewUtil.showMessage(this, R.string.right_tellphone);
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putString(IConstant.BUNDLE_PARAMS,
					ViewUtil.getContent(this, R.id.notify_edit));
			startActivity(ChooseClassActivity.class,bundle);
			break;
		}
	}
}
