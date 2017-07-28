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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.adapter.main.growth.GrowthAdapter;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.GrowthListModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class GrowthActivity extends BaseNetWorkActicity implements
		OnRefreshListener2<ListView> {
	PullToRefreshListView listView;
	ListView lv;
	GrowthAdapter adapter;
	View view;// listview头部的view
	int page = 1;// 当前加载的页数
	int pageCount;// 总共有多少页
	GrowthListModel model;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = (GrowthListModel) bundle
				.getSerializable(IConstant.BUNDLE_PARAMS);
		if (!AppUtil.isNetworkAvailable(this)// 判断是否有网咯
				&& mCache.getAsJSONObject(IUrContant.GET_GROWTH_LIST_URL
						+ model.getChildId()) == null) {
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
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("model", model);
	}

	@Override
	public void restoreInstanceState(Bundle inState) {
		super.restoreInstanceState(inState);
		if (inState.containsKey("model"))
			model = (GrowthListModel) inState.getSerializable("model");
	}

	@Override
	public void initView() throws Exception {
		setContentView(R.layout.activity_growth_profile);
		float w = displayMetrics.widthPixels;
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				(int) w, (int) (w / 3));
		view = ViewUtil.inflate(this, R.layout.class_ring_header);
		view.findViewById(R.id.bg).setLayoutParams(layoutParams);
		adapter = new GrowthAdapter(this, new ArrayList<GrowthListModel>());
		listView = ViewUtil.findViewById(this, R.id.boy_list);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(this);
		lv = listView.getRefreshableView();
		lv.addHeaderView(view);
		lv.setAdapter(adapter);
		lv.setOnScrollListener(new PauseOnScrollListener(imageLoader, true,
				true));// 滑动不加载图片
		ViewUtil.setContent(view, R.id.name, model.getUserName());
		imageLoader.displayImage(StringUtil.imgUrl(this, model.getUserImg()),
				(ImageView) view.findViewById(R.id.profile), fadeOptions);
	}

	@Override
	public void loadData() throws Exception {
		super.loadData();
		if (model == null)
			return;
		isRefresh = true;
		Map<String, String> map = new HashMap<>();
		map.put("childId", model.getChildId() + "");
		map.put("pageNo", "1");
		map.put("pageSize", "10");
		IConstant.HTTP_CONNECT_POOL.addRequest(
				IUrContant.GET_CHILDE_GROWTH_URL, map, activityHandler);
	};

	@Override
	public void setData() throws Exception {
		if (model == null)
			return;
		JSONObject data = mCache.getAsJSONObject(IUrContant.GET_GROWTH_LIST_URL
				+ model.getChildId());
		if (isRefresh)
			adapter.list.clear();
		if (data != null) {
			JSONArray growthList = data.optJSONArray("growthJList");
			for (int i = 0; i < growthList.length(); i++) {
				JSONObject item = growthList.optJSONObject(i);
				if (item != null) {
					GrowthListModel model = new GrowthListModel();
					model.setContent(item.optString("content"));// 创建内容
					model.setCreateTime(item.optString("create_time"));// 创建时间
					model.setId(item.optInt("id"));// 足迹的id
					model.setUid(item.optInt("owner"));// 谁发的id
					JSONArray imgList = item.optJSONArray("imagList");// 图像列表
					List<String> list = new ArrayList<String>();
					for (int j = 0; j < imgList.length(); j++) {// 图像地址
						JSONObject it = imgList.optJSONObject(j);
						list.add(it.optString("image_path"));
					}
					model.setPicList(list);
					adapter.list.add(model);
				}
			}
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.GET_CHILDE_GROWTH_URL)) {
			listView.onRefreshComplete();
			if (!isRefresh)
				page = page + 1;
			else
				page = 2;
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				pageCount = data.optInt("pageCount");// 获取到总共有多少页数
				if (pageCount + 1 < page) {// 如果超过 了最大页数，则不保存，也不设置数据
					page = page - 1;
					return;
				}
				mCache.put(IUrContant.GET_GROWTH_LIST_URL + model.getChildId(),
						data);
				setData();
			}
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		isRefresh = true;// 刷新
		Map<String, String> map = new HashMap<>();
		map.put("childId", model.getChildId() + "");// 孩子id
		map.put("pageNo", "1");// 加载第几页
		map.put("pageSize", "10");// 加载多少条
		IConstant.HTTP_CONNECT_POOL.addRequest(
				IUrContant.GET_CHILDE_GROWTH_URL, map, activityHandler);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {// 加载
		if (page > pageCount) {
			handler.sendEmptyMessage(0);
			return;
		}
		isRefresh = false;
		Map<String, String> map = new HashMap<>();
		map.put("childId", model.getChildId() + "");
		map.put("pageNo", page + "");
		map.put("pageSize", "10");
		IConstant.HTTP_CONNECT_POOL.addRequest(
				IUrContant.GET_CHILDE_GROWTH_URL, map, activityHandler);
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ViewUtil.showMessage(GrowthActivity.this, "已加载全部");
			listView.onRefreshComplete();
		};
	};
}
