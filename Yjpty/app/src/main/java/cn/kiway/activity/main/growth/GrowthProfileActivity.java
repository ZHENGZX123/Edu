package cn.kiway.activity.main.growth;

import handmark.pulltorefresh.library.PullToRefreshBase;
import handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.adapter.main.growth.GrowthProfileAdapter;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.GrowthListModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.ViewUtil;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class GrowthProfileActivity extends BaseNetWorkActicity implements
		OnRefreshListener2<ListView> {
	GrowthProfileAdapter adapter;
	PullToRefreshListView listView;
	ListView lv;
	int page = 1;// 当前加载的页数
	int pageCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!AppUtil.isNetworkAvailable(this)// 判断网络是否可用
				&& mCache.getAsJSONObject(IUrContant.GET_GROWTH_LIST_URL
						+ app.getClassModel().getId()) == null) {
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
			setData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initView() throws Exception {
		setContentView(R.layout.activity_growth_profile);
		listView = ViewUtil.findViewById(this, R.id.boy_list);
		adapter = new GrowthProfileAdapter(this,
				new ArrayList<GrowthListModel>());
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(this);
		lv = listView.getRefreshableView();
		lv.setAdapter(adapter);
		lv.setOnScrollListener(new PauseOnScrollListener(imageLoader, true,
				true));
	}

	@Override
	public void loadData() throws Exception {
		super.loadData();
		isRefresh = true;
		Map<String, String> map = new HashMap<>();
		map.put("classId", app.getClassModel().getId() + "");// 班级id
		map.put("pageNo", "1");// 加载第几页的页数
		map.put("pageSize", "10");// 加载多少条
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_GROWTH_LIST_URL,
				map, activityHandler);
	};

	@Override
	public void setData() throws Exception {
		JSONObject data = mCache.getAsJSONObject(IUrContant.GET_GROWTH_LIST_URL
				+ app.getClassModel().getId());
		if (isRefresh)
			adapter.list.clear();
		if (data != null) {
			JSONArray growthList = data.optJSONArray("growthJList");
			if (growthList != null) {
				for (int i = 0; i < growthList.length(); i++) {
					JSONObject item = growthList.optJSONObject(i);
					if (item != null) {
						GrowthListModel model = new GrowthListModel();
						model.setContent(item.optString("content"));// 内容
						model.setCreateTime(item.optString("create_time"));// 创建时间
						model.setId(item.optInt("id"));// id
						model.setUid(item.optInt("owner"));// 创建人的id
						model.setChildId(item.optInt("child_id"));// 孩子的id
						model.setUserName(item.optString("child_name"));// 孩子的名字
						model.setUserImg(item.optString("avatar"));// 孩子头像
						JSONArray imgList = item.optJSONArray("imagList");
						List<String> list = new ArrayList<String>();// 图像的列表
						for (int j = 0; j < imgList.length(); j++) {
							JSONObject it = imgList.optJSONObject(j);
							list.add(it.optString("image_path"));
						}
						model.setPicList(list);
						adapter.list.add(model);
					}
				}
			}
			adapter.notifyDataSetChanged();
		}
		if (adapter.getCount() <= 0) {// 判断是否有数据展示不同的视图
			findViewById(R.id.no_data).setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		} else {
			findViewById(R.id.no_data).setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.GET_GROWTH_LIST_URL)) {// 获取到班级成长日志列表
			listView.onRefreshComplete();
			if (!isRefresh) {
				page = page + 1;
			} else
				page = 2;
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				pageCount = data.optInt("pageCount");// 获取到总共有多少页数
				if (pageCount + 1 < page) {// 如果超过了最大页数，则不保存，也不设置数据
					page = page - 1;
					return;
				}
				mCache.put(IUrContant.GET_GROWTH_LIST_URL
						+ app.getClassModel().getId(), data);
				setData();

			}
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		isRefresh = true;
		Map<String, String> map = new HashMap<>();
		map.put("classId", app.getClassModel().getId() + "");
		map.put("pageNo", "1");
		map.put("pageSize", "10");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_GROWTH_LIST_URL,
				map, activityHandler);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (page > pageCount) {
			handler.sendEmptyMessage(0);
			return;
		}
		isRefresh = false;
		Map<String, String> map = new HashMap<>();
		map.put("classId", app.getClassModel().getId() + "");
		map.put("pageNo", page + "");
		map.put("pageSize", "10");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_GROWTH_LIST_URL,
				map, activityHandler);
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ViewUtil.showMessage(GrowthProfileActivity.this, "已加载全部");
			listView.onRefreshComplete();
		};
	};
}
