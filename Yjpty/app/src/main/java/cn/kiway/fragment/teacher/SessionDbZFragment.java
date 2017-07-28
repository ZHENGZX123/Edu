package cn.kiway.fragment.teacher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.adapter.main.teacher.SessionDbAdapter;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.VideoCateMode;
import cn.kiway.utils.ViewUtil;
import handmark.pulltorefresh.library.PullToRefreshBase;
import handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import handmark.pulltorefresh.library.PullToRefreshListView;

public class SessionDbZFragment extends BaseFragment implements
        OnRefreshListener2<ListView> {
    private SessionDbAdapter adapter;// 列表适配器
    int position;
    PullToRefreshListView listView;
    ListView lv;
    boolean isAttendClass;


    public static SessionDbZFragment newInstance(int position, boolean isAttendClass) {

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putBoolean("isAttendClass", isAttendClass);
        SessionDbZFragment fragment = new SessionDbZFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.isAttendClass = getArguments().getBoolean("isAttendClass");
            this.position = getArguments().getInt("position");
        }
    }

    public SessionDbZFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = ViewUtil.inflate(activity, R.layout.fragment_session_db_z);
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
        if (view != null) {
            listView = ViewUtil.findViewById(view, R.id.list);
            adapter = new SessionDbAdapter(activity,
                    new ArrayList<VideoCateMode>(), isAttendClass);
            listView.setMode(Mode.BOTH);
            listView.setOnRefreshListener(this);
            lv = listView.getRefreshableView();
            lv.setAdapter(adapter);
        }
    }

    @Override
    public void loadData() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("dirType", position + "");
        map.put("gradeId", activity.app.getClassModel().getYear() + "");
        IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.SESSION_DB_URL, map,
                fragmentHandler);
    }

    void setData() throws Exception {
        JSONObject jsonObject = activity.mCache// 区分类型、跟年级，所以存的地址不同
                .getAsJSONObject(IUrContant.SESSION_DB_URL + position
                        + activity.app.getClassModel().getYear());
        if (adapter == null)
            return;
        if (jsonObject != null) {
            JSONArray data = jsonObject.optJSONArray("lessonDirList");
            JSONArray uselist = jsonObject.optJSONArray("userdefLessonDirList");
            adapter.list.clear();
            if (data != null) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.optJSONObject(i);
                    if (item != null
                            && item.optInt("grade_id") == activity.app
                            .getClassModel().getYear()) {// 标准课程
                        VideoCateMode model = new VideoCateMode();
                        model.setId(item.optInt("id"));// 课程id
                        model.setDirType(item.optInt("dir_type"));// 分类id
                        model.setName(item.optString("name"));// 课程名字
                        model.setPreview(item.getString("preview"));// 课程图像
                        model.setDirId(position);
                        model.setIsUser(1);
                        adapter.list.add(model);
                    }
                }
                for (int i = 0; i < uselist.length(); i++) {// 自定义课程
                    JSONObject item = uselist.optJSONObject(i);
                    if (item != null
                            && item.optInt("grade_id") == activity.app
                            .getClassModel().getYear()) {
                        VideoCateMode model = new VideoCateMode();
                        model.setId(item.optInt("id"));// 课程id
                        model.setDirType(item.optInt("dir_type"));// 分类id
                        model.setName(item.optString("name"));// 课程名字
                        model.setPreview(item.getString("preview"));// 课程图像
                        model.setDirId(position);
                        model.setIsUser(2);
                        adapter.list.add(model);
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
        if (adapter.getCount() <= 0) {
            view.findViewById(R.id.list).setVisibility(View.GONE);
            view.findViewById(R.id.no_data).setVisibility(View.VISIBLE);
            ViewUtil.setContent(view, R.id.no_data, "暂时还没有课堂，敬请期待");
        } else {
            view.findViewById(R.id.list).setVisibility(View.VISIBLE);
            view.findViewById(R.id.no_data).setVisibility(View.GONE);
        }
    }

    @Override
    public void httpSuccess(HttpResponseModel message) throws Exception {
        super.httpSuccess(message);
        if (message.getUrl().equals(IUrContant.SESSION_DB_URL)) {
            JSONObject jsonObject = new JSONObject(new String(
                    message.getResponse()));
            listView.onRefreshComplete();
            if (jsonObject.optInt("retcode") == 1) {// 区分类型、跟年级，所以存的地址不同
                activity.mCache.put(IUrContant.SESSION_DB_URL + position
                        + activity.app.getClassModel().getYear(), jsonObject);
                setData();
            }
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        Map<String, String> map = new HashMap<>();
        map.put("dirType", position + "");
        map.put("gradeId", activity.app.getClassModel().getYear() + "");
        IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.SESSION_DB_URL, map,
                fragmentHandler);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        Map<String, String> map = new HashMap<>();
        map.put("dirType", position + "");
        map.put("gradeId", activity.app.getClassModel().getYear() + "");
        IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.SESSION_DB_URL, map,
                fragmentHandler);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                initView();
                setData();
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
