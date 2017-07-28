package cn.kiway.activity.choosebaby;

import handmark.pulltorefresh.library.PullToRefreshListView;

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
import cn.kiway.activity.BaseActivity;
import cn.kiway.adapter.ChooseClassAdapter;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.BoyModel;
import cn.kiway.utils.ViewUtil;

public class ChooseClassActivity extends BaseActivity {
	PullToRefreshListView listView;
	ListView lv;
	ChooseClassAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			initView();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void initView() throws Exception {
		setContentView(R.layout.activity_chats_list);
		listView = ViewUtil.findViewById(this, R.id.boy_list);
		lv = listView.getRefreshableView();
		ViewUtil.setContent(this, R.id.title, "选择班级");
		adapter = new ChooseClassAdapter(context, new ArrayList<BoyModel>());
		lv.setAdapter(adapter);
	}

	@Override
	public void loadData() throws Exception {
		super.loadData();
		Map<String, String> map = new HashMap<>();
		map.put("phone", bundle.getString(IConstant.BUNDLE_PARAMS));
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_CLASS_LIST_URL,
				map, activityHandler, true);
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.GET_CLASS_LIST_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				JSONArray array = data.optJSONArray("classList");
				if (array != null) {
					for (int i = 0; i < array.length(); i++) {
						JSONObject item = array.optJSONObject(i);
						BoyModel model = new BoyModel();
						model.setClassId(item.optInt("id"));
						model.setClassName(item.optString("className"));
						model.setTeacherName(data.optString("teacherName"));
						model.setGrade(item.optInt("gradeId"));
						adapter.list.add(model);
					}
				}
				adapter.notifyDataSetChanged();
				if (adapter.getCount() <= 0) {
					findViewById(R.id.no_data).setVisibility(View.VISIBLE);
					findViewById(R.id.boy_list).setVisibility(View.GONE);
					ViewUtil.setContent(this, R.id.no_data, "该老师还没建班级");
				} else {
					findViewById(R.id.no_data).setVisibility(View.GONE);
					findViewById(R.id.boy_list).setVisibility(View.VISIBLE);
				}
			} else {
				findViewById(R.id.no_data).setVisibility(View.VISIBLE);
				findViewById(R.id.boy_list).setVisibility(View.GONE);
				ViewUtil.setContent(this, R.id.no_data, "获取不到该账号的班级,请检查该账号是否正确");
			}
		}
	}

	@Override
	public void HttpError(HttpResponseModel message) throws Exception {
		ViewUtil.showMessage(this, R.string.no_network);
	}
}
