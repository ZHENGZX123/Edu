package cn.kiway.fragment.teacher;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.sortlistview.ClearEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.adapter.main.teacher.TeacherTableAdapter;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.VideoModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

@SuppressLint("HandlerLeak")
public class SessionTable2Fragment extends BaseFragment implements TextWatcher,
        OnScrollListener {

    boolean isAttendClass;
    TeacherTableAdapter adapter;
    ContentResolver contentResolver;
    public static ListView listView;
    private ClearEditText srv1;
    public boolean selectPosition = false;
    public static int Totalposition;
    public static int isSessionPosition;

    @SuppressLint("SimpleDateFormat")
    public SessionTable2Fragment() {
    }


    public static SessionTable2Fragment newInstance(boolean isAttendClass) {
        Bundle args = new Bundle();
        args.putBoolean("isAttendClass", isAttendClass);
        SessionTable2Fragment fragment = new SessionTable2Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            this.isAttendClass = getArguments().getBoolean("isAttendClass");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentResolver = activity.getContentResolver();
        view = ViewUtil.inflate(activity, R.layout.fragment_tabel2);
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
        adapter = new TeacherTableAdapter(activity, this, isAttendClass,
                new ArrayList<VideoModel>(), true);
        listView = ViewUtil.findViewById(view, R.id.listview);
        srv1 = ViewUtil.findViewById(view, R.id.filter_edit);
        ViewUtil.setContent(view, R.id.no_data, "没有相关课程");
        view.findViewById(R.id.select).setOnClickListener(this);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
        listView.setSelected(true);
        listView.setOnScrollListener(this);
        // 根据输入框输入值的改变来过滤搜索
        srv1.addTextChangedListener(this);
    }

    @Override
    public void loadData() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("classId", activity.app.getClassModel().getId() + "");
        IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_ALL_SESSION_URL,
                map, fragmentHandler);
    }

    void setData() throws Exception {
        JSONArray array = activity.mCache
                .getAsJSONArray(IUrContant.GET_ALL_SESSION_URL
                        + activity.app.getClassModel().getId());
        if (array != null) {
            loadLessonFromCache(array);
        }
        hasSessionData();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.select:
                listView.setSelection(adapter.position);
                view.findViewById(R.id.select).setVisibility(View.GONE);
                break;
        }
    }

    private void loadLessonFromCache(JSONArray array) {
        adapter.list.clear();
        adapter.listData.clear();
        selectPosition = false;
        Totalposition = 0;
        isSessionPosition = 0;
        for (int i = 0; i < array.length(); i++) {
            VideoModel model = new VideoModel();
            JSONObject item = array.optJSONObject(i);
            model.setId(item.optInt("lesson_id"));// 课程id
            model.setDirId(item.optInt("dir_type"));// 分类id
            model.setName(item.optString("name"));// 课时名字
            model.setRequireTime(item.optString("require_time"));// 所需时间
            try {
                model.setPreview(item.getString("preview"));
            } catch (JSONException e) {
                e.printStackTrace();
            }// 课程图像
            model.setSeqNo(item.optInt("seq_no"));// 课程查看
            model.setTypeName(item.optString("types"));// 课程特点
            model.setGrader(activity.app.getClassModel().getYear());
            if (item.optInt("type_def") == 0) {
                model.setIsUser(1);
            } else {
                model.setIsUser(2);
            }
            model.setSessionTime(item.optString("attend_date"));
            model.setSeesionPlayTime(item.optString("finish_date"));
            if (!item.optString("finish_date").equals("null")
                    && !item.optString("finish_date").equals("")) {
                isSessionPosition = i;
            }
            model.setSessionName(item.optString("dir_name"));
            if (StringUtil.stringTimeToLong(item.optString("attend_date")
                    + " 00:00:00") >= StringUtil
                    .stringTimeToLong((StringUtil.getDateField(
                            System.currentTimeMillis(), 6) + " 00:00:00"))
                    && !selectPosition
                    && (item.optString("finish_date").equals("null") || item
                    .optString("finish_date").equals(""))) {
                selectPosition = true;
                model.setTotalSession(true);
                Totalposition = i;
            } else {
                model.setTotalSession(false);
            }
            adapter.list.add(model);
            adapter.listData.add(model);
        }
        adapter.notifyDataSetChanged();
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.requestFocusFromTouch();// 获取焦点
                if (isSessionPosition > Totalposition) {
                    listView.setSelection(isSessionPosition);
                } else {
                    listView.setSelection(Totalposition);
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() != 0 || s.toString().equals("")) {
            adapter.getFilter().filter(s.toString());
        } else {
            listView.clearTextFilter();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView v, int scrollState) {
        if (srv1.getText().toString().length() <= 0)
            view.findViewById(R.id.select).setVisibility(View.VISIBLE);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void httpSuccess(HttpResponseModel message) throws Exception {
        super.httpSuccess(message);
        JSONObject data = new JSONObject(new String(message.getResponse()));
        if (message.getUrl().equals(IUrContant.GET_ALL_SESSION_URL)
                && data.optInt("retcode") == 1) {
            activity.mCache.put(IUrContant.GET_ALL_SESSION_URL
                            + activity.app.getClassModel().getId(),
                    data.optJSONArray("courseList"));
            setData();
        }
    }

    public void hasSessionData() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (adapter.getCount() <= 0) {
                    view.findViewById(R.id.relative).setVisibility(View.GONE);
                    view.findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.relative)
                            .setVisibility(View.VISIBLE);
                    view.findViewById(R.id.no_data).setVisibility(View.GONE);
                }
            }
        });
    }
}
