package cn.kiway.fragment.studio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import handmark.pulltorefresh.library.PullToRefreshBase;
import handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import handmark.pulltorefresh.library.PullToRefreshListView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.choosebaby.EditTeacherPhoneActivity;
import cn.kiway.activity.common.MipcaCaptureActivity;
import cn.kiway.adapter.ClassAdapter;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.fragment.main.MainFragment;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.VideoModel;
import cn.kiway.utils.ViewUtil;

public class ClassFragment extends BaseFragment implements
		OnRefreshListener2<ListView> {
	PullToRefreshListView listView;
	ListView lv;
	ClassAdapter adapter;
	boolean ifRefesh = true;

	public ClassFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = ViewUtil.inflate(activity, R.layout.fragment_main);
		try {
			initView();
			setData();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	void initView() throws Exception {
		view.findViewById(R.id.session).setVisibility(View.VISIBLE);
		view.findViewById(R.id.relative).setVisibility(View.GONE);
		if (activity.app.getBoyModels().size() <= 0) {
			view.findViewById(R.id.no_class).setVisibility(View.VISIBLE);
			view.findViewById(R.id.boy_list).setVisibility(View.GONE);
			view.findViewById(R.id.img).setVisibility(View.GONE);
			view.findViewById(R.id.scan).setOnClickListener(this);
			view.findViewById(R.id.phone).setOnClickListener(this);
		}else{
		view.findViewById(R.id.no_class).setVisibility(View.GONE);
		view.findViewById(R.id.boy_list).setVisibility(View.VISIBLE);
		view.findViewById(R.id.img).setVisibility(View.VISIBLE);
		adapter = new ClassAdapter(activity, new ArrayList<VideoModel>());
		listView = ViewUtil.findViewById(view, R.id.boy_list);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(this);
		lv = listView.getRefreshableView();
		lv.setAdapter(adapter);
	}
	}

	@Override
	public void loadData() throws Exception {
		isRefresh = true;
		Map<String, String> map = new HashMap<>();
		map.put("classId",
				activity.app.getBoyModels().get(activity.app.getPosition())
						.getClassId()
						+ "");
		map.put("minDate", "null");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_SESSION_URL, map,
				fragmentHandler);
	}

	void setData() throws Exception {
		JSONObject data = activity.mCache
				.getAsJSONObject(IUrContant.GET_SESSION_URL);
		if (data != null) {
			JSONArray lessonList = data.optJSONArray("lessonList");
			if (isRefresh) {
				adapter.list.clear();
			}
			for (int i = 0; i < lessonList.length(); i++) {
				JSONObject item = lessonList.optJSONObject(i);
				VideoModel model = new VideoModel();
				model.setLessionId(item.optInt("lessonId"));
				model.setLessonName(item.optString("lessonName"));
				model.setPreview(item.optString("preview"));
				model.setFinishData(item.optString("finishDate"));
				model.setRequireTime(item.optString("requireTime"));
				model.setTeachingAim(item.optString("teachingAim"));
				adapter.list.add(model);
			}
			adapter.notifyDataSetChanged();
		}
		if (adapter.getCount() <= 0) {
			listView.setVisibility(View.GONE);
			view.findViewById(R.id.no_data).setVisibility(View.VISIBLE);
			ViewUtil.setContent(view, R.id.no_data, "老师还没上课");
		} else {
			listView.setVisibility(View.VISIBLE);
			view.findViewById(R.id.no_data).setVisibility(View.GONE);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		isRefresh = true;
		Map<String, String> map = new HashMap<>();
		map.put("classId",
				activity.app.getBoyModels().get(activity.app.getPosition())
						.getClassId()
						+ "");
		map.put("minDate", "null");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_SESSION_URL, map,
				fragmentHandler);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		isRefresh = false;
		Map<String, String> map = new HashMap<>();
		map.put("classId",
				activity.app.getBoyModels().get(activity.app.getPosition())
						.getClassId()
						+ "");
		map.put("minDate", adapter.list.get(adapter.getCount() - 1)
				.getFinishData());
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_SESSION_URL, map,
				fragmentHandler);
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.GET_SESSION_URL)) {
			listView.onRefreshComplete();
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				activity.mCache.put(IUrContant.GET_SESSION_URL, data);
				setData();
			}
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.scan:
			activity.startActivity(MipcaCaptureActivity.class);// 扫描二维码
			break;
		case R.id.phone:
			activity.startActivity(EditTeacherPhoneActivity.class);
			break;
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (activity.app.getBoyModels().size() > 0
				&& activity.app.getBoyModels().get(MainFragment.pos)
						.getClassId() != activity.app.getBoyModels()
						.get(activity.app.getPosition()).getClassId())
			try {
				loadData();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
