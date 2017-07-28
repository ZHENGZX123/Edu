package cn.kiway.fragment.main;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.MainActivity;
import cn.kiway.activity.WebViewActivity;
import cn.kiway.activity.choosebaby.EditTeacherPhoneActivity;
import cn.kiway.activity.common.MipcaCaptureActivity;
import cn.kiway.activity.mian.ClassRingActivity;
import cn.kiway.activity.mian.InSchoolInfoActivity;
import cn.kiway.adapter.BabyViewPagerAdapter;
import cn.kiway.adapter.SchoolNewsAdapter;
import cn.kiway.adapter.message.ChatListAdapter;
import cn.kiway.dialog.MianDialog;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.message.MinaClientHandler;
import cn.kiway.message.model.MessageChatProvider;
import cn.kiway.message.model.MessageObserver;
import cn.kiway.message.util.WriteMsgUitl;
import cn.kiway.model.BoyModel;
import cn.kiway.model.MessageModel;
import cn.kiway.model.SchoolNewsModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.ViewUtil;
import handmark.pulltorefresh.library.PullToRefreshListView;

@SuppressLint("SetJavaScriptEnabled")
public class MainFragment extends BaseFragment implements OnPageChangeListener {
    PullToRefreshListView listView;// 整体的listview
    ListView listMessage, listSchool;// 消息列表与学校新闻列表
    ChatListAdapter adapter;// 消息适配器
    ImageView img;// 标题
    MianDialog dialog;
    SchoolNewsAdapter newsAdapter;
    View head, foot;// 头部与尾部
    ContentResolver contentResolver;
    Handler handler;
    /**
     * 消息观察者
     */
    MessageObserver messageObserver;
    List<BoyModel> list = new ArrayList<BoyModel>();
    private BabyViewPagerAdapter babyViewPagerAdapter;// 列表适配器
    ViewPager viewPager;
    RadioGroup group;
    LinearLayout.LayoutParams layoutParams;
    public static int pos;

    public MainFragment() {
        super();
    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = ViewUtil.inflate(activity, R.layout.fragment_main);
        contentResolver = activity.getContentResolver();
        float w = activity.displayMetrics.widthPixels;
        layoutParams = new LinearLayout.LayoutParams((int) w, (int) (w / 3));
        messageObserver = new MessageObserver(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                isRefresh = true;
                handler.sendEmptyMessage(0);
            }
        });
        contentResolver.registerContentObserver(
                Uri.parse(MessageChatProvider.MESSAGECHATS_URL), true,
                messageObserver);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        if (adapter == null)
                            return;
                        List<MessageModel> models = new ArrayList<MessageModel>();
                        Cursor cursor = contentResolver.query(
                                // 根据消息的类型查找数据，讨论组与班级群的类型分别为 2，3
                                Uri.parse(MessageChatProvider.MESSAGECHATS_URL),
                                null, "_msgtype=? or _msgtype=?", new String[]{
                                        "3", "2"}, " _time desc");
                        while (cursor.moveToNext()) {
                            MessageModel model = new MessageModel();
                            model.setUid(cursor.getLong(1));// 发送人的id
                            model.setMid(cursor.getLong(2));// 消息的id
                            model.setName(cursor.getString(3));// 发给谁的名字
                            model.setContent(cursor.getString(4));// 消息内容
                            model.setTime(cursor.getLong(5));// 消息时间
                            model.setHeadUrl(cursor.getString(6));// 消息人的头像
                            model.setMessageNumber(cursor.getInt(7));// 消息未读数
                            model.setToUid(cursor.getLong(8));// 发给谁的id
                            model.setMsgType(cursor.getInt(10));// 消息的类型
                            model.setHomerWorkNumber(cursor.getInt(11));// 作业数
                            model.setNotifyNumber(cursor.getInt(12));// 通知数
                            models.add(model);
                        }
                        cursor.close();
                        adapter.messagelist.clear();
                        adapter.messagelist.addAll(models);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        };
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
        dialog = new MianDialog(activity);
        view.findViewById(R.id.add).setOnClickListener(this);
        view.findViewById(R.id.scan_a).setOnClickListener(this);
        view.findViewById(R.id.scan).setOnClickListener(this);
        view.findViewById(R.id.phone).setOnClickListener(this);
        head = ViewUtil.inflate(activity, R.layout.head_view);
        foot = ViewUtil.inflate(activity, R.layout.foot_view);
        img = ViewUtil.findViewById(head, R.id.img);
        viewPager = ViewUtil.findViewById(head, R.id.baby_view);
        group = ViewUtil.findViewById(head, R.id.group);
        img.setLayoutParams(layoutParams);
        adapter = new ChatListAdapter(activity, new ArrayList<MessageModel>());// 消息列表
        newsAdapter = new SchoolNewsAdapter(activity,
                new ArrayList<SchoolNewsModel>());// 学校新闻
        listView = ViewUtil.findViewById(view, R.id.boy_list);
        listSchool = ViewUtil.findViewById(foot, R.id.school_news);
        listSchool.setAdapter(newsAdapter);
        listMessage = listView.getRefreshableView();
        listMessage.addHeaderView(head);// 增加头部
        listMessage.addFooterView(foot);// 增加尾部
        listMessage.setAdapter(adapter);
        if (listMessage.getHeaderViewsCount() > 2) {
            listMessage.removeHeaderView(head);
        }
        if (listMessage.getFooterViewsCount() > 2) {
            listMessage.removeFooterView(foot);
        }
        foot.findViewById(R.id.in_shcool).setOnClickListener(this);
        foot.findViewById(R.id.class_ring).setOnClickListener(this);
        foot.findViewById(R.id.news_more).setOnClickListener(this);
        viewPager.setOnPageChangeListener(this);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void loadData() throws Exception {
        handler.sendEmptyMessage(0);
        Map<String, String> map = new HashMap<>();
        map.put("userId", activity.app.getUid() + "");
        IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_CHILD_INFO_URL,
                map, fragmentHandler);
    }

    void setData() throws Exception {
        JSONObject data = activity.mCache
                .getAsJSONObject(IUrContant.GET_CHILD_INFO_URL);
        if (data != null) {
            JSONArray array = data.optJSONArray("childsInfo");
            list.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.optJSONObject(i);
                BoyModel boyModel = new BoyModel();
                boyModel.setUid(item.optInt("userId"));
                boyModel.setChildId(item.optInt("childId"));
                boyModel.setChildName(item.optString("childName"));
                boyModel.setClassId(item.optInt("classId"));
                boyModel.setClassName(item.optString("className"));
                boyModel.setTeacherName(item.optString("teacherName"));
                boyModel.setSchoolName(item.optString("schoolName"));
                boyModel.setBirthday(item.optString("birthday"));
                boyModel.setTeacherId(item.optInt("teacherId"));
                list.add(boyModel);
                WriteMsgUitl.WriteClassData(activity, activity.app,
                        boyModel.getClassName(), boyModel.getClassId(), 3);
            }
        }
        activity.app.setBoyList(list);
        if (activity.app.getBoyModels().size() <= 0) {// 没有绑定班级的时候
            view.findViewById(R.id.no_class).setVisibility(View.VISIBLE);
            view.findViewById(R.id.boy_list).setVisibility(View.GONE);
            view.findViewById(R.id.add).setVisibility(View.GONE);
            img = ViewUtil.findViewById(view, R.id.img);
            img.setLayoutParams(layoutParams);
            AppUtil.deleteChatData(activity.getContentResolver());
        } else {
            view.findViewById(R.id.no_class).setVisibility(View.GONE);
            view.findViewById(R.id.boy_list).setVisibility(View.VISIBLE);
            view.findViewById(R.id.add).setVisibility(View.VISIBLE);
            babyViewPagerAdapter = new BabyViewPagerAdapter(
                    activity.fragmentManager, list);
            viewPager.setAdapter(babyViewPagerAdapter);
            viewPager.setCurrentItem(activity.app.getPosition());
            if (list.size() >= 2) {// 班级数为一不创建
                if (group.getChildCount() == list.size())// 如果数目一样则不再创建
                    return;
                group.removeAllViews();// 动态增加radiobutton
                for (int i = 0; i < list.size(); i++) {
                    RadioButton tempButton = new RadioButton(activity);
                    tempButton.setId(i);
                    tempButton
                            .setButtonDrawable(R.drawable.viewpage_selector_bg); // 设置按钮的样式
                    tempButton.setPadding(30, 0, 0, 0); // 设置文字距离按钮四周的距离
                    group.addView(tempButton,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                }
            }
            loadSchoolNew();
            if (SharedPreferencesUtil.getBoolean(activity,
                    MinaClientHandler.ATTEND_CLASS_BEAT
                            + "#"
                            + activity.app.getBoyModels().get(activity.app.getPosition())
                            .getClassId())) {
                MainActivity.studioText.setVisibility(View.VISIBLE);
            } else {
                MainActivity.studioText.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.scan:
                activity.startActivity(MipcaCaptureActivity.class);// 扫描二维码
                break;
            case R.id.scan_a:
                activity.startActivity(MipcaCaptureActivity.class);// 扫描二维码
                break;
            case R.id.add:
                if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                }
                break;
            case R.id.in_shcool:
                activity.startActivity(InSchoolInfoActivity.class);// 在校情况
                break;
            case R.id.class_ring:
                activity.startActivity(ClassRingActivity.class);// 班级圈
                break;
            case R.id.news_more:
                bundle.putString(
                        IConstant.BUNDLE_PARAMS,
                        IUrContant.GET_SCHOOL_WEB_URL
                                + "?userId="
                                + activity.app.getBoyModels()
                                .get(activity.app.getPosition())
                                .getTeacherId()
                                + "&classId="
                                + activity.app.getBoyModels()
                                .get(activity.app.getPosition())
                                .getClassId());
                bundle.putString(IConstant.BUNDLE_PARAMS1,
                        activity.resources.getString(R.string.school_news));
                activity.startActivity(WebViewActivity.class, bundle);// 学校新闻
                break;
            case R.id.phone:
                activity.startActivity(EditTeacherPhoneActivity.class);
                break;
        }
    }

    @Override
    public void httpSuccess(HttpResponseModel message) throws Exception {
        super.httpSuccess(message);
        if (message.getUrl().equals(IUrContant.GET_SCHOOL_NEWS)) {
            JSONObject data = new JSONObject(new String(message.getResponse()));
            if (data.optInt("retcode") == 1) {
                JSONArray array = data.optJSONArray("newsJList");
                if (newsAdapter.list != null) {
                    newsAdapter.list.clear();
                }
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.optJSONObject(i);
                    SchoolNewsModel model = new SchoolNewsModel();
                    model.setSchoolId(item.optInt("id"));
                    model.setSchoolTitle(item.optString("title"));
                    model.setSchoolImg(item.optString("img_path"));
                    newsAdapter.list.add(model);
                }
                newsAdapter.notifyDataSetChanged();
                if (newsAdapter.getCount() <= 0) {
                    foot.findViewById(R.id.school_news_t).setVisibility(
                            View.GONE);
                } else {
                    foot.findViewById(R.id.school_news_t).setVisibility(
                            View.VISIBLE);
                    listSchool.setLayoutParams(new LinearLayout.LayoutParams(
                            (int) activity.displayMetrics.widthPixels,
                            (int) (activity.resources
                                    .getDimension(R.dimen._60dp) * newsAdapter
                                    .getCount())));
                }
            }
        } else if (message.getUrl().equals(IUrContant.GET_CHILD_INFO_URL)) {
            JSONObject data = new JSONObject(new String(message.getResponse()));
            if (data.optInt("retcode") == 1) {
                activity.mCache.put(IUrContant.GET_CHILD_INFO_URL, data);
                setData();
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    void loadSchoolNew() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", activity.app.getUid() + "");
        map.put("classId",
                activity.app.getBoyModels().get(activity.app.getPosition())
                        .getClassId()
                        + "");
        map.put("isParent", "1");
        IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_SCHOOL_NEWS, map,
                fragmentHandler);
    }

    @Override
    public void onPageSelected(int position) {
        group.check(position);
        pos = activity.app.getPosition();
        activity.app.setPosition(position);
        if (activity.app.getBoyModels().get(pos).getChildId() != activity.app
                .getBoyModels().get(position).getChildId()) {
            loadSchoolNew();
        }
        if (SharedPreferencesUtil.getBoolean(activity,
                MinaClientHandler.ATTEND_CLASS_BEAT + "#"
                        + activity.app.getBoyModels().get(position).getClassId())) {
            MainActivity.studioText.setVisibility(View.VISIBLE);
        } else {
            MainActivity.studioText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        try {
            if (!hidden)
                loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
