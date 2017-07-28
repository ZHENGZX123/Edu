package cn.kiway.activity.main.creatclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.adapter.main.PraentAdatper;
import cn.kiway.dialog.classd.EditBabyDialog;
import cn.kiway.dialog.classd.EditBabyDialog.EditBabyNameCallBack;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.BoyModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class BabyDetailActivity extends BaseNetWorkActicity implements
		EditBabyNameCallBack {
	BoyModel boyModel;
	ImageView babyImg;
	EditBabyDialog dialog;// 弹框
	TextView sex;// 性别
	ListView listView;
	PraentAdatper adatper;
	int own;// 是否为自己创建的

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baby_detail);
		boyModel = (BoyModel) bundle.getSerializable(IConstant.BUNDLE_PARAMS);
		own = bundle.getInt(IConstant.BUNDLE_PARAMS1);
		try {
			initView();
			loadData();
			setData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {// 程序回收保存
		super.onSaveInstanceState(outState);
		outState.putSerializable("boyModel", boyModel);
		outState.putInt("own", own);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("boyModel"))
			boyModel = (BoyModel) savedInstanceState
					.getSerializable("boyModel");
		if (savedInstanceState.containsKey("own"))
			own = savedInstanceState.getInt("own");
	}

	@Override
	public void initView() throws Exception {
		babyImg = ViewUtil.findViewById(this, R.id.baby_img);// 孩子头像
		sex = ViewUtil.findViewById(this, R.id.sex);// 孩子性别
		listView = ViewUtil.findViewById(this, R.id.list);
		adatper = new PraentAdatper(this, new ArrayList<BoyModel>(), own);
		findViewById(R.id.more).setOnClickListener(this);
		dialog = new EditBabyDialog(this,// 初始化更多的dialog
				resources.getString(R.string.edit_name),
				resources.getString(R.string.delete_baby), this);

		listView.setAdapter(adatper);
		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader,
				false, false)); // 滑动不加载图片
		if (own != app.getUid()) {
			findViewById(R.id.own).setVisibility(View.GONE);
		}

	}

	@Override
	public void loadData() throws Exception {
		super.loadData();
		Map<String, String> map = new HashMap<>();
		map.put("childId", boyModel.getUid() + "");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_BABY_URL, map,
				activityHandler);
	};

	@Override
	public void setData() throws Exception {
		if (boyModel == null)
			return;
		imageLoader.displayImage(StringUtil.imgUrl(this, boyModel.getImg()),
				babyImg, fadeOptions);// 设置头像
		ViewUtil.setContent(this, R.id.name, boyModel.getName());
		ViewUtil.setSexTag(sex, boyModel.getSex());// 设置性别图标
		if (!boyModel.getBrithday().equals("null")) {// 设置生日
			ViewUtil.setContent(this, R.id.brithday, boyModel.getBrithday());
		} else {
			ViewUtil.setContent(this, R.id.brithday, R.string.anknow);
		}
		JSONObject data = mCache.getAsJSONObject(IUrContant.GET_BABY_URL
				+ boyModel.getUid());
		if (data != null) {// 家长列表
			adatper.list.clear();
			JSONArray parentlist = data.getJSONArray("parentList");
			for (int i = 0; i < parentlist.length(); i++) {
				JSONObject item = parentlist.optJSONObject(i);
				BoyModel moder = new BoyModel();
				moder.setUid(item.optLong("user_id"));// 家长id
				moder.setImg(item.optString("photo"));// 头像
				moder.setName(item.optString("name"));// 名字
				moder.setType(item.getInt("parent_type"));// 家长类型
				moder.setClassId(item.optInt("class_id"));// 所在班级id
				moder.setChildId(item.optInt("child_id"));// 绑定的孩子id
				moder.setPhone(item.optString("phone"));// 家长电话
				adatper.list.add(moder);
			}
			adatper.notifyDataSetChanged();
		}
		if (adatper.list.size() > 0) {// 判断是否有数据，展示不同的视图
			findViewById(R.id.no_data).setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.no_data).setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.more:
			if (dialog != null && !dialog.isShowing()) {
				dialog.show();
			}
			break;
		}
	}

	@Override
	public void editBabyNameCallBack() throws Exception {// 进入修改名字的activity
		boyModel.setName(ViewUtil.getContent(this, R.id.name));
		Bundle b = new Bundle();
		b.putSerializable(IConstant.BUNDLE_PARAMS, boyModel);
		b.putBoolean(IConstant.BUNDLE_PARAMS1, true);
		Intent intent2 = new Intent(this, EditBabyNameActivity.class);
		intent2.putExtras(b);
		startActivityForResult(intent2, 2);
	}

	@Override
	public void deleteBabyCallBack() throws Exception {// 删除孩子
		Map<String, String> map = new HashMap<>();
		map.put("childId", boyModel.getUid() + "");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.DELETE_BABY_URL, map,
				activityHandler);
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.DELETE_BABY_URL)) {// 删除孩子
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				ViewUtil.showMessage(this, R.string.delete_success);
				app.getClassModel().setChildNum(
						app.getClassModel().getChildNum() - 1);
				ClassInfoActivity.isLoad = true;
				finish();
			}
		} else if (message.getUrl().equals(IUrContant.GET_BABY_URL)) {// 获取孩子信息
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				mCache.put(IUrContant.GET_BABY_URL + boyModel.getUid(), data);
				setData();
			}
		}
	}

	@Override
	protected void onActivityResult(int requstCode, int resultCode, Intent data) {
		super.onActivityResult(requstCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		ViewUtil.setContent(this, R.id.name,
				data.getStringExtra(IConstant.BUNDLE_PARAMS));
		ClassInfoActivity.isLoad = true;
	}
}
