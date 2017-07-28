package cn.kiway.activity.me.growth;

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
import cn.kiway.Yjptj.R;
import cn.kiway.adapter.GrowthProfileAdapter;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.GrowthListModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class GrowthActivity extends BasePhotoActivity implements
		OnRefreshListener2<ListView> {
	PullToRefreshListView listView;
	ListView lv;
	GrowthProfileAdapter adapter;
	int page = 1;// 当前加载的页数
	int pageCount = 0;
	public static String dayCount;// 记录多少天的时光
	public static boolean isLoad = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!AppUtil.isNetworkAvailable(this)
				&& mCache.getAsJSONObject(IUrContant.GET_GROWTH_LIST_URL) == null) {
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
		float w = displayMetrics.widthPixels;
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				(int) w, (int) (w / 3));
		View view = ViewUtil.inflate(this, R.layout.growing_path);
		view.findViewById(R.id.bg).setLayoutParams(layoutParams);
		adapter = new GrowthProfileAdapter(this,
				new ArrayList<GrowthListModel>());
		listView = ViewUtil.findViewById(this, R.id.boy_list);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(this);
		lv = listView.getRefreshableView();
		lv.addHeaderView(view);
		lv.setAdapter(adapter);
		lv.setOnScrollListener(new PauseOnScrollListener(imageLoader, true,
				true));
		ViewUtil.setContent(this, R.id.title, R.string.czda);
		imageLoader.displayImage(
				StringUtil.imgUrl(this,
						bundle.getString(IConstant.BUNDLE_PARAMS)),
				(ImageView) view.findViewById(R.id.profile), fadeOptions);
	}

	@Override
	public void loadData() throws Exception {
		super.loadData();
		isRefresh = true;
		Map<String, String> map = new HashMap<>();
		map.put("childId", app.getBoyModels().get(app.getPosition())
				.getChildId()
				+ "");
		map.put("pageNo", 1 + "");
		map.put("pageSize", "10");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_GROWTH_LIST_URL,
				map, activityHandler);
	};

	void setData() throws Exception {
		if (adapter == null)
			return;
		JSONObject data = mCache.getAsJSONObject(IUrContant.GET_GROWTH_LIST_URL
				+ app.getBoyModels().get(app.getPosition()).getChildId());
		if (isRefresh)
			adapter.list.clear();
		if (data != null) {
			JSONArray growthList = data.optJSONArray("growthJList");
			if (data.optString("startTime") != null) {
				dayCount = data.optString("startTime");
			} else {
				dayCount = "0";
			}
			for (int i = 0; i < growthList.length(); i++) {
				JSONObject item = growthList.optJSONObject(i);
				if (item != null) {
					GrowthListModel model = new GrowthListModel();
					model.setContent(item.optString("content"));
					model.setCreateTime(item.optString("create_time"));
					model.setId(item.optInt("id"));
					model.setUid(item.optInt("owner"));
					JSONArray imgList = item.optJSONArray("imagList");
					List<String> list = new ArrayList<String>();
					for (int j = 0; j < imgList.length(); j++) {
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
		if (message.getUrl().equals(IUrContant.GET_GROWTH_LIST_URL)) {
			listView.onRefreshComplete();
			if (!isRefresh)
				page = page + 1;
			else
				page = 2;
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null) {
				if (data.optInt("retcode") == 1) {
					pageCount = data.optInt("pageCount");
					if (pageCount + 1 < page) { // 如果超过 了最大页数，则不保存，也不设置数据
						page = page - 1;
						return;
					}
					mCache.put(IUrContant.GET_GROWTH_LIST_URL
							+ app.getBoyModels().get(app.getPosition())
									.getChildId(), data);
					setData();
				}
			}
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		isRefresh = true;
		Map<String, String> map = new HashMap<>();
		map.put("pageNo", 1 + "");
		map.put("pageSize", "10");
		map.put("childId", app.getBoyModels().get(app.getPosition())
				.getChildId()
				+ "");
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
		map.put("pageNo", page + "");
		map.put("childId", app.getBoyModels().get(app.getPosition())
				.getChildId()
				+ "");
		map.put("pageSize", "10");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_GROWTH_LIST_URL,
				map, activityHandler);
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			if (isLoad) {
				isLoad = false;
				loadData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ViewUtil.showMessage(GrowthActivity.this, "已加载全部");
			listView.onRefreshComplete();
		};
	};
}
