package cn.kiway.activity.mian;

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
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.adapter.InSchoolInfoAdapter;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.InSchoolInfoModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.ViewUtil;

public class InSchoolInfoActivity extends BaseNetWorkActicity implements
		OnRefreshListener2<ListView> {
	PullToRefreshListView listView;
	ListView lv;
	InSchoolInfoAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!AppUtil.isNetworkAvailable(this)
				&& mCache.getAsJSONObject(IUrContant.GET_SHCOOLS_INFO_URL) == null) {
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
		setContentView(R.layout.activity_chats_list);
		adapter = new InSchoolInfoAdapter(this,
				new ArrayList<InSchoolInfoModel>());
		listView = ViewUtil.findViewById(this, R.id.boy_list);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(this);
		lv = listView.getRefreshableView();
		lv.setAdapter(adapter);
		ViewUtil.setContent(this, R.id.title, R.string.inschool_info);
		findViewById(R.id.layouts).setVisibility(View.VISIBLE);
	}

	@Override
	public void loadData() throws Exception {
		super.loadData();
		isRefresh = true;
		Map<String, String> map = new HashMap<>();
		map.put("childId",app.getBoyModels().get(app.getPosition()).getChildId() + "");
		map.put("minDate", "null");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_SHCOOLS_INFO_URL,
				map, activityHandler);
	}

	void setData() throws Exception {
		JSONObject data = mCache
				.getAsJSONObject(IUrContant.GET_SHCOOLS_INFO_URL);
		if (isRefresh)
			adapter.list.clear();
		if (data != null) {
			JSONArray Splist = data.optJSONArray("SPList");
			for (int i = 0; i < Splist.length(); i++) {
				JSONObject item = Splist.optJSONObject(i);
				if (item != null) {
					InSchoolInfoModel model = new InSchoolInfoModel();
					if (item.has("1")) {// 因后台只传老师端有提交的数据，所以这里得判读是否有该值
						model.setWs(item.optInt("1"));
					} else {
						model.setWs(0);
					}
					if (item.has("2")) {
						model.setLy(item.optInt("2"));
					} else {
						model.setLy(0);
					}
					if (item.has("3")) {
						model.setYc(item.optInt("3"));
					} else {
						model.setYc(0);
					}
					if (item.has("4")) {
						model.setJl(item.optInt("4"));
					} else {
						model.setJl(0);
					}
					if (item.has("5")) {
						model.setFy(item.optInt("5"));
					} else {
						model.setFy(0);
					}
					if (item.has("6")) {
						model.setKq(item.optInt("6"));
					} else {
						model.setKq(0);
					}
					model.setData(item.optString("date"));
					adapter.list.add(model);
				}
			}
			adapter.notifyDataSetChanged();
		}
		if (adapter.getCount() <= 0) {
			listView.setVisibility(View.GONE);
			findViewById(R.id.no_data).setVisibility(View.VISIBLE);
			ViewUtil.setContent(this, R.id.no_data,"还没有宝贝的在校情况呢");
		} else {
			listView.setVisibility(View.VISIBLE);
			findViewById(R.id.no_data).setVisibility(View.GONE);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		isRefresh = true;
		Map<String, String> map = new HashMap<>();
		map.put("childId", app.getBoyModels().get(app.getPosition()).getChildId() + "");
		map.put("minDate", "null");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_SHCOOLS_INFO_URL,
				map, activityHandler);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		isRefresh = false;
		Map<String, String> map = new HashMap<>();
		map.put("childId", app.getBoyModels().get(app.getPosition()).getChildId() + "");
		map.put("minDate", adapter.list.get(adapter.getCount() - 1).getData());
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_SHCOOLS_INFO_URL,
				map, activityHandler);
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.GET_SHCOOLS_INFO_URL)) {
			listView.onRefreshComplete();
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				mCache.put(IUrContant.GET_SHCOOLS_INFO_URL, data);
				setData();
			}
		}
	}
}
