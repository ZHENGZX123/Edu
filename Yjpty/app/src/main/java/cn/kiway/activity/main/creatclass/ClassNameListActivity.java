package cn.kiway.activity.main.creatclass;

import handmark.pulltorefresh.library.PullToRefreshBase;
import handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
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
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.adapter.ClassNameAdapter;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.ClassModel;
import cn.kiway.utils.ViewUtil;

public class ClassNameListActivity extends BaseNetWorkActicity implements OnRefreshListener2<ListView> {
	ClassNameAdapter adapter;
	PullToRefreshListView listView;
	ListView lv;
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

	@SuppressWarnings("deprecation")
	@Override
	public void initView() throws Exception {
		super.initView();
		setContentView(R.layout.activity_common_list);
		ViewUtil.setContent(this, R.id.title,
				resources.getString(R.string.kindergarten));
		listView = ViewUtil.findViewById(this, R.id.boy_list);
		adapter = new ClassNameAdapter(this, new ArrayList<ClassModel>());
		listView.setMode(Mode.PULL_DOWN_TO_REFRESH);
		listView.setOnRefreshListener(this);
		lv = listView.getRefreshableView();
		lv.setAdapter(adapter);
		findViewById(R.id.next_class).setVisibility(View.GONE);
		findViewById(R.id.add).setVisibility(View.GONE);
	}

	@Override
	public void loadData() throws Exception {
		super.loadData();
		Map<String, String> map = new HashMap<>();
		map.put("address", bundle.getString(IConstant.BUNDLE_PARAMS));
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_SCHOOL_LIST_URL,
				map, activityHandler,true);
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.GET_SCHOOL_LIST_URL)) {
			listView.onRefreshComplete();
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null) {
				if (data.optInt("setcode") == 1) {
					JSONArray array = data.optJSONArray("schools");
					if (array != null) {// 所有的学校列表
						adapter.list.clear();
						for (int i = 0; i < array.length(); i++) {
							JSONObject item = array.optJSONObject(i);
							ClassModel model = new ClassModel();
							model.setSchoolId(item.optInt("id"));// 学校id
							model.setSchoolName(item.optString("name"));// 学校地址
							model.setNo(item.optString("address"));// 地址
							adapter.list.add(model);
						}
						adapter.notifyDataSetChanged();
					}
				}
			}
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		try {
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		
	}
}
