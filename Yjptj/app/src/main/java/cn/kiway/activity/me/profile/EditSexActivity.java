package cn.kiway.activity.me.profile;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.utils.ViewUtil;

public class EditSexActivity extends BaseActivity {
	CheckBox boy_che, gril_che;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_sex);
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void initView() throws Exception {
		boy_che = ViewUtil.findViewById(this, R.id.check_boy);
		gril_che = ViewUtil.findViewById(this, R.id.check_gril);
		findViewById(R.id.gril).setOnClickListener(this);
		findViewById(R.id.boy).setOnClickListener(this);
		switch (bundle.getInt(IConstant.BUNDLE_PARAMS)) {
		case 1:
			boy_che.setChecked(true);
			break;
		case 2:
			gril_che.setChecked(true);
			break;
		}
		boy_che.setEnabled(false);
		gril_che.setEnabled(false);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Map<String, String> map = new HashMap<>();
		map.put("userId", app.getUid() + "");
		switch (v.getId()) {
		case R.id.boy:
			boy_che.setChecked(true);
			gril_che.setChecked(false);
			map.put("sex", "1");
			IConstant.HTTP_CONNECT_POOL.addRequest(
					IUrContant.UPDATEUSERINFO_URL, map, activityHandler, true);
			break;
		case R.id.gril:
			boy_che.setChecked(false);
			gril_che.setChecked(true);
			map.put("sex", "2");
			IConstant.HTTP_CONNECT_POOL.addRequest(
					IUrContant.UPDATEUSERINFO_URL, map, activityHandler, true);
			break;
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.UPDATEUSERINFO_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				Intent da = getIntent();
				if (boy_che.isChecked()) {
					da.putExtra(IConstant.BUNDLE_PARAMS, "1");
				} else {
					da.putExtra(IConstant.BUNDLE_PARAMS, "2");
				}
				setResult(RESULT_OK, da);
				finish();
			}
		}
	}
}
