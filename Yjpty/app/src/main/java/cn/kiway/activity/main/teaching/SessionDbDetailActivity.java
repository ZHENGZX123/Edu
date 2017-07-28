package cn.kiway.activity.main.teaching;

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
import cn.kiway.adapter.main.teacher.TeacherTableAdapter;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.VideoCateMode;
import cn.kiway.model.VideoModel;
import cn.kiway.utils.ViewUtil;

public class SessionDbDetailActivity extends BaseNetWorkActicity implements
		OnRefreshListener2<ListView> {
	TeacherTableAdapter adapter;
	PullToRefreshListView listView;
	ListView lv;
	VideoCateMode videoCateMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_growth_profile);
		videoCateMode = (VideoCateMode) bundle
				.getSerializable(IConstant.BUNDLE_PARAMS);
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
		outState.putSerializable("videoCateMode", videoCateMode);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("videoCateMode"))
			videoCateMode = (VideoCateMode) savedInstanceState
					.getSerializable("videoCateMode");
	}

	@Override
	public void initView() throws Exception {
		adapter = new TeacherTableAdapter(this,
				bundle.getBoolean(IConstant.BUNDLE_PARAMS1),
				new ArrayList<VideoModel>(),false);
		listView = ViewUtil.findViewById(this, R.id.boy_list);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(this);
		lv = listView.getRefreshableView();
		lv.setAdapter(adapter);
		ViewUtil.setContent(this, R.id.title, videoCateMode.getName());
	}

	@Override
	public void loadData() throws Exception {
		super.loadData();
		Map<String, String> map = new HashMap<>();
		map.put("dirId", videoCateMode.getId() + "");
		map.put("gradeId", app.getClassModel().getYear() + "");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.SESSION_DB_LIST_URL,
				map, activityHandler);
	}

	@Override
	public void setData() throws Exception {
		JSONObject jsonObject = mCache
				.getAsJSONObject(IUrContant.SESSION_DB_LIST_URL
						+ videoCateMode.getId());
		if (jsonObject != null) {
			JSONArray datalist = jsonObject.optJSONArray("lessonList");
			if (datalist != null) {
				adapter.list.clear();
				for (int i = 0; i < datalist.length(); i++) {
					JSONObject item = datalist.optJSONObject(i);
					if (item != null) {
						VideoModel model = new VideoModel();
						model.setId(item.optInt("id"));// 课程id
						model.setDirId(videoCateMode.getDirId());// 分类id
						model.setName(item.optString("name"));// 课程名字
						model.setRequireTime(item.optString("require_time"));// 所需时间
						model.setPreview(item.getString("preview"));// 课程图像
						model.setSeqNo(item.optInt("dir_type"));// 课程查看
						model.setTypeName(item.optString("type_name"));// 课程特点
						model.setIsUser(videoCateMode.getIsUser());
						adapter.list.add(model);
					}
				}
				adapter.notifyDataSetChanged();
			}
		}
		if (adapter.getCount() <= 0) {// 判断是否有数据展示不同的视图
			listView.setVisibility(View.GONE);
			findViewById(R.id.no_data).setVisibility(View.VISIBLE);
			ViewUtil.setContent(this, R.id.no_data, "暂时还没有课程内容，敬请期待");
		} else {
			listView.setVisibility(View.VISIBLE);
			findViewById(R.id.no_data).setVisibility(View.GONE);
		}
	}

	@Override
	// 下来刷新
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		Map<String, String> map = new HashMap<>();
		map.put("dirId", videoCateMode.getId() + "");
		map.put("gradeId", app.getClassModel().getYear() + "");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.SESSION_DB_LIST_URL,
				map, activityHandler);
	}

	@Override
	// 上来加载更多
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		Map<String, String> map = new HashMap<>();
		map.put("dirId", videoCateMode.getId() + "");
		map.put("gradeId", app.getClassModel().getYear() + "");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.SESSION_DB_LIST_URL,
				map, activityHandler);
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.SESSION_DB_LIST_URL)) {
			JSONObject jsonObject = new JSONObject(new String(
					message.getResponse()));
			listView.onRefreshComplete();
			if (jsonObject.optInt("retcode") == 1) {
				mCache.put(
						IUrContant.SESSION_DB_LIST_URL
								+ videoCateMode.getId(), jsonObject);
				setData();
			}
		}
	}

	@SuppressWarnings("static-access")
	@Override
	protected void onPause() {
		super.onPause();
		if (adapter.dialog != null && adapter.dialog.isShowing()) {
			adapter.dialog.dismiss();
		}
	}
}
