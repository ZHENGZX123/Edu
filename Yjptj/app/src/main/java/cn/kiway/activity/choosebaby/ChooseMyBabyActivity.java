package cn.kiway.activity.choosebaby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.adapter.ChooseBabyAdapter;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.BoyModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.ViewUtil;

public class ChooseMyBabyActivity extends BaseNetWorkActicity {
	ListView listView;
	View head;
	ChooseBabyAdapter adapter;
	JSONObject data;
	boolean isBan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!AppUtil.isNetworkAvailable(this)) {
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
			loadData();
		} catch (Exception e) {
		}
	}

	void initView() throws Exception {
		setContentView(R.layout.activity_choose_mybaby);
		listView = ViewUtil.findViewById(this, R.id.list);
		head = ViewUtil.inflate(this, R.layout.head_choose_my_baby);
		adapter = new ChooseBabyAdapter(this, new ArrayList<BoyModel>());
		listView.addHeaderView(head);
		listView.setAdapter(adapter);
		ViewUtil.setContent(head, R.id.class_name,
				bundle.getString(IConstant.BUNDLE_PARAMS1));
		ViewUtil.setContent(head, R.id.teacher,
				"建班老师  " + bundle.getString(IConstant.BUNDLE_PARAMS2));
	}

	@Override
	public void loadData() throws Exception {
		super.loadData();
		Map<String, String> map = new HashMap<>();
		map.put("classId", bundle.getString(IConstant.BUNDLE_PARAMS));
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_CLASS_CHLID_URL,
				map, activityHandler, true);
	}

	void setData() throws Exception {	
		if (data != null) {
			JSONArray array = data.optJSONArray("childrenList");
			for (int i = 0; i < array.length(); i++) {
				isBan = false;
				JSONObject item = array.optJSONObject(i);
				for (int j = 0; j < app.getBoyModels().size(); j++) {// 检查是否已经绑定了改宝贝
					if (app.getBoyModels().get(j).getChildId() == item
							.optInt("id"))
						isBan = true;
				}
				BoyModel model = new BoyModel();
				model.setisBan(isBan);
				model.setName(item.optString("child_name"));// 孩子名
				model.setSex(item.optInt("sex"));// 性别
				model.setBirthday(item.optString("birthday"));// 生日
				model.setUid(item.optInt("id"));// id
				model.setUrl(item.optString("avatar"));// 头像
				model.setClassId(item.optInt("class_id"));// 班级id
				model.setParentList(item.optString("parentTypeList"));// 关系
				adapter.list.add(model);
			}
			adapter.notifyDataSetChanged();
			if (adapter.getCount() <= 0) {
				listView.setVisibility(View.GONE);
				findViewById(R.id.no_data).setVisibility(View.VISIBLE);
				ViewUtil.setContent(this, R.id.no_data, "该班级还又没添加学生,赶快让老师添加吧");
			} else {
				listView.setVisibility(View.VISIBLE);
				findViewById(R.id.no_data).setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.GET_CLASS_CHLID_URL)) {
			data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				setData();
			}
		}
	}
}
