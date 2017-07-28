package cn.kiway.activity.main.creatclass;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.activity.main.WebViewActivity;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.ClassModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class EmialActivity extends BaseNetWorkActicity {
	ClassModel classModel;
	EditText text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emial);
		classModel = (ClassModel) bundle
				.getSerializable(IConstant.BUNDLE_PARAMS);
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("classModel", classModel);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("classModel"))
			classModel = (ClassModel) savedInstanceState
					.getSerializable("classModel");
	}

	@Override
	public void initView() throws Exception {
		text = ViewUtil.findViewById(this, R.id.QQ_emial);
		findViewById(R.id.see).setOnClickListener(this);
		findViewById(R.id.send).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.see:
			Bundle bundle = new Bundle();
			bundle.putString(IConstant.BUNDLE_PARAMS, IUrContant.SEE_EMAIL_URL
					+ StringUtil.MD5(classModel.getId() + ""));
			bundle.putString(IConstant.BUNDLE_PARAMS1,
					resources.getString(R.string.zhijiazhang));
			startActivity(WebViewActivity.class, bundle);// 学校新闻
			break;
		case R.id.send:// 发送邮箱
			if (!StringUtil.isEmail(ViewUtil.getContent(this, R.id.QQ_emial))) {
				ViewUtil.showMessage(this, R.string.qingshuru);
				return;
			}
			Map<String, String> map = new HashMap<>();
			map.put("receiver", ViewUtil.getContent(this, R.id.QQ_emial));
			map.put("classId", classModel.getId() + "");
			IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.SENG_EMAIL_URL,
					map, activityHandler, true);
			break;
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.SENG_EMAIL_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				ViewUtil.showMessage(this, R.string.fasongchengg);
				text.setText("");
				text.setHint(resources.getString(R.string.enter_your_QQ_emial));
			} else {
				ViewUtil.showMessage(this, R.string.fasongshibai);
			}
		}
	}
}
