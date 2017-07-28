package cn.kiway.activity.me;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.activity.MainActivity;
import cn.kiway.activity.choosebaby.ProfileBabyActivity;
import cn.kiway.activity.common.ScanActivity;
import cn.kiway.dialog.ClearDataDialog;
import cn.kiway.dialog.ClearDataDialog.ClearDataCallBack;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.dialog.choosebaby.EditBabyDialog;
import cn.kiway.dialog.choosebaby.EditBabyDialog.EditBabyNameCallBack;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.message.model.MessageChatProvider;
import cn.kiway.message.model.MessageProvider;
import cn.kiway.model.BoyModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class MyBabyInfoActivity extends BaseNetWorkActicity implements
		ClearDataCallBack, EditBabyNameCallBack {
	ImageView babyImg, dadImg, munImg, qinImg;
	TextView sex;
	boolean b;
	ClearDataDialog dialog;// 绑定关系的时候dialog
	int parentType;// 绑定孩子的类型
	JSONObject childeda;// 孩子数据
	EditBabyDialog babyDialog;// 修改信息的dialog

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		b = bundle.getBoolean(IConstant.BUNDLE_PARAMS1);
		if (!AppUtil.isNetworkAvailable(this)
				&& mCache.getAsJSONObject(IUrContant.GET_MY_BABY_URL
						+ bundle.getInt(IConstant.BUNDLE_PARAMS)) == null) {
			newWorkdialog = new IsNetWorkDialog(context, this,
					resources.getString(R.string.dqsjmylrhlwqljhlwl),
					resources.getString(R.string.ljhlw));
			if (newWorkdialog != null && !newWorkdialog.isShowing()) {
				newWorkdialog.show();
				return;
			}
		}
		try {
			initView();
			setData();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void initView() throws Exception {
		setContentView(R.layout.activity_baby_info);
		babyImg = ViewUtil.findViewById(this, R.id.profile);
		dadImg = ViewUtil.findViewById(this, R.id.baba_img);
		munImg = ViewUtil.findViewById(this, R.id.mun_img);
		qinImg = ViewUtil.findViewById(this, R.id.qinqi_img);
		sex = ViewUtil.findViewById(this, R.id.sex);
		findViewById(R.id.class_room).setOnClickListener(this);
		findViewById(R.id.more).setOnClickListener(this);
		babyDialog = new EditBabyDialog(context, R.string.xgbbxx,
				R.string.qxgzbb, this);
		if (b) {
			findViewById(R.id.dad_l).setOnClickListener(this);
			findViewById(R.id.mun_l).setOnClickListener(this);
			findViewById(R.id.qin_l).setOnClickListener(this);
			findViewById(R.id.more).setVisibility(View.GONE);
			findViewById(R.id.class_room).setEnabled(false);
			findViewById(R.id.class_room).setVisibility(View.GONE);
			ViewUtil.setArroundDrawable(this, R.id.class_room, -1, -1, -1, -1);
			dialog = new ClearDataDialog(this, this, R.string.qdbdgxm, null);
		}
	}

	@Override
	public void loadData() throws Exception {
		super.loadData();
		Map<String, String> map = new HashMap<>();
		map.put("childId", bundle.getInt(IConstant.BUNDLE_PARAMS) + "");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_MY_BABY_URL, map,
				activityHandler);
	}

	void setData() throws Exception {
		JSONObject data = mCache.getAsJSONObject(IUrContant.GET_MY_BABY_URL
				+ bundle.getInt(IConstant.BUNDLE_PARAMS));
		if (data != null) {
			childeda = data.optJSONObject("children");
			if (childeda != null) {
				ViewUtil.setContent(this, R.id.name,
						childeda.optString("child_name"));// 宝贝名字
				ViewUtil.setContent(this, R.id.title,
						childeda.optString("child_name"));// 宝贝名字
				ViewUtil.setContent(this, R.id.time,
						childeda.optString("birthday"));// 宝贝生日
				imageLoader.displayImage(
						StringUtil.imgUrl(this, childeda.optString("avatar")),
						babyImg, fadeOptions);
				ViewUtil.setSexTag(sex, childeda.optInt("sex"));// 设置性别图标
			}
			JSONArray parentlist = data.optJSONArray("parentList");
			if (parentlist != null) {
				for (int i = 0; i < parentlist.length(); i++) {
					JSONObject item = parentlist.optJSONObject(i);
					if (item.optInt("parent_type") == 1) {// 爸爸
						imageLoader.displayImage(StringUtil.imgUrl(this,
								item.optString("photo")), dadImg, fadeOptions);
						ViewUtil.setTextFontColor(this, R.id.dad, null,
								resources.getColor(R.color._333333));
						findViewById(R.id.dad_in).setVisibility(View.GONE);
						findViewById(R.id.dad_l).setEnabled(false);
					} else if (item.optInt("parent_type") == 2) {// 妈妈
						imageLoader.displayImage(StringUtil.imgUrl(this,
								item.optString("photo")), munImg, fadeOptions);
						ViewUtil.setTextFontColor(this, R.id.mun, null,
								resources.getColor(R.color._333333));
						findViewById(R.id.mun_in).setVisibility(View.GONE);
						findViewById(R.id.mun_l).setEnabled(false);
					} else if (item.optInt("parent_type") == 3) {// 亲戚
						imageLoader.displayImage(StringUtil.imgUrl(this,
								item.optString("photo")), qinImg, fadeOptions);
						ViewUtil.setTextFontColor(this, R.id.qing, null,
								resources.getColor(R.color._333333));
						findViewById(R.id.qing_in).setVisibility(View.GONE);
						findViewById(R.id.qin_l).setEnabled(false);
					}
				}
			}
		}
		if (app.getBoyModels() != null && app.getBoyModels().size() > 0)
			ViewUtil.setContent(this, R.id.class_room,
					app.getBoyModels().get(app.getPosition()).getClassName());
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.dad_l:
			parentType = 1;
			if (dialog != null && !dialog.isShowing()) {
				dialog.show();
			}
			break;
		case R.id.mun_l:
			parentType = 2;
			if (dialog != null && !dialog.isShowing()) {
				dialog.show();
			}
			break;
		case R.id.qin_l:
			parentType = 3;
			if (dialog != null && !dialog.isShowing()) {
				dialog.show();
			}
			break;
		case R.id.class_room:
			startActivity(ScanActivity.class);
			break;
		case R.id.more:
			if (babyDialog != null && !babyDialog.isShowing()) {
				babyDialog.show();
			}
			break;
		}
	}

	@Override
	public void clearDataCallBack(View vx) throws Exception {
		if (parentType == 0)
			return;
		Map<String, String> map = new HashMap<>();
		map.put("parentType", parentType + "");
		map.put("childId", childeda.optString("id"));
		map.put("userId", app.getUid() + "");
		map.put("classId", childeda.optString("class_id"));
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.JOIN_CLASS_URL, map,
				activityHandler, true);
	}

	@Override
	public void editBabyNameCallBack() throws Exception {
		BoyModel boyModel = new BoyModel();
		boyModel.setName(childeda.optString("child_name"));
		boyModel.setUid((int) app.getBoyModels().get(app.getPosition())
				.getChildId());
		boyModel.setClassId((int) app.getBoyModels().get(app.getPosition())
				.getClassId());
		boyModel.setUrl(childeda.optString("avatar"));
		boyModel.setBirthday(childeda.optString("birthday"));
		boyModel.setSex(childeda.optInt("sex"));
		Intent intent = new Intent(MyBabyInfoActivity.this,
				ProfileBabyActivity.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean(IConstant.BUNDLE_PARAMS1, true);
		bundle.putBoolean(IConstant.BUNDLE_PARAMS3, true);
		bundle.putSerializable(IConstant.BUNDLE_PARAMS, boyModel);
		intent.putExtras(bundle);
		startActivityForResult(intent, 1);
	}

	@Override
	public void deleteBabyCallBack() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("classId", app.getBoyModels().get(app.getPosition())
				.getClassId()
				+ "");
		map.put("childId", app.getBoyModels().get(app.getPosition())
				.getChildId()
				+ "");
		map.put("parentId", app.getUid() + "");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.DELTET_PARENT_URL,
				map, activityHandler, true);
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.GET_MY_BABY_URL)) {// 获取宝贝信息
			JSONObject data = new JSONObject(new String(message.getResponse()));
			mCache.put(
					IUrContant.GET_MY_BABY_URL
							+ bundle.getInt(IConstant.BUNDLE_PARAMS), data);
			setData();
		} else if (message.getUrl().equals(IUrContant.JOIN_CLASS_URL)) {// 加入班级
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				finishAllAct();
				finish();
				startActivity(MainActivity.class);
			}
		} else if (message.getUrl().equals(IUrContant.DELTET_PARENT_URL)) {// 取消关注
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				getContentResolver().delete(
						Uri.parse(MessageProvider.MESSAGES_URL),
						"(_touid=? or _touid=?) and  _msgtype=? ",
						new String[] {
								"" + app.getUid(),
								""
										+ app.getBoyModels()
												.get(app.getPosition())
												.getClassId() + "" });
				getContentResolver().delete(
						Uri.parse(MessageChatProvider.MESSAGECHATS_URL),
						"_touid=?",
						new String[] { ""
								+ app.getBoyModels().get(app.getPosition())
										.getClassId() });
				ViewUtil.showMessage(this, R.string.qxgz);
				finish();
				mCache.clear();
				getContentResolver().delete(
						Uri.parse(MessageProvider.MESSAGES_URL),
						"_msgtype=? or _msgtype=? or _msgtype=?",
						new String[] { "1", "2", "3" });
				getContentResolver().delete(
						Uri.parse(MessageChatProvider.MESSAGECHATS_URL),
						"_msgtype=? or _msgtype=? or _msgtype=?",
						new String[] { "1", "2", "3" });
			}
		}
	}

	@Override
	public void onActivityResult(int requstCode, int resultCode, Intent data) {
		super.onActivityResult(requstCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		try {
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
