package cn.kiway.fragment.teacher;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.adapter.main.teacher.DateAdapter;
import cn.kiway.adapter.main.teacher.SpecialCalendar;
import cn.kiway.adapter.main.teacher.TeacherTableAdapter;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.message.model.VideoProvider;
import cn.kiway.message.util.WriteMsgUitl;
import cn.kiway.model.VideoModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.Logger;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

@SuppressLint({"SimpleDateFormat", "HandlerLeak"})
public class SessionTableFragment extends BaseFragment implements
        OnGestureListener {
    private ViewFlipper flipper1 = null;
    private GridView gridView = null;
    private GestureDetector gestureDetector = null;
    private int year_c = 0;
    private int month_c = 0;
    private int day_c = 0;
    private int week_c = 0;
    private int week_num = 0;
    private String currentDate = "";
    private DateAdapter dateAdapter;
    private int daysOfMonth = 0; // 某月的天数
    private int dayOfWeek = 0; // 具体某一天是星期几
    private int weeksOfMonth = 0;
    private SpecialCalendar sc = null;
    private boolean isLeapyear = false; // 是否为闰年
    private int selectPostion = 0;
    private String dayNumbers[] = new String[7];
    private TextView tvDate;
    private int currentYear;
    private int currentMonth;
    private int currentWeek;
    private int currentNum;
    private ImageView left;// 向左滑动
    private ImageView right;// 向右滑动
    private ListView listView;// 列表
    private TeacherTableAdapter adapter;// 列表适配器
    private TextView open, close;// 展开收起按钮
    private LinearLayout layout;
    boolean isAttendClass;
    ContentResolver contentResolver;
    View v;

    @SuppressLint("SimpleDateFormat")
    public SessionTableFragment() {
        try {
            data();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SessionTableFragment newInstance(boolean isAttendClass) {
        Bundle args = new Bundle();
        args.putBoolean("isAttendClass", isAttendClass);
        SessionTableFragment fragment = new SessionTableFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null)
            this.isAttendClass=getArguments().getBoolean("isAttendClass");
        try {
            data();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void data() throws Exception {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        currentDate = sdf.format(date);
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);
        currentYear = year_c;
        currentMonth = month_c;
        sc = new SpecialCalendar();
        getCalendar(year_c, month_c);
        week_num = getWeeksOfMonth();
        currentNum = week_num;
        if (dayOfWeek == 7) {
            week_c = day_c / 7 + 1;
        } else {
            if (day_c <= (7 - dayOfWeek)) {
                week_c = 1;
            } else {
                if ((day_c - (7 - dayOfWeek)) % 7 == 0) {
                    week_c = (day_c - (7 - dayOfWeek)) / 7 + 1;
                } else {
                    week_c = (day_c - (7 - dayOfWeek)) / 7 + 2;
                }
            }
        }
        currentWeek = week_c;
        getCurrent();
    }

    /**
     * 判断某年某月所有的星期数
     *
     * @param year
     * @param month
     */
    public int getWeeksOfMonth(int year, int month) {
        // 先判断某月的第一天为星期几
        int preMonthRelax = 0;
        int dayFirst = getWhichDayOfWeek(year, month);
        int days = sc.getDaysOfMonth(sc.isLeapYear(year), month);
        if (dayFirst != 7) {
            preMonthRelax = dayFirst;
        }
        if ((days + preMonthRelax) % 7 == 0) {
            weeksOfMonth = (days + preMonthRelax) / 7;
        } else {
            weeksOfMonth = (days + preMonthRelax) / 7 + 1;
        }
        return weeksOfMonth;

    }

    /**
     * 判断某年某月的第一天为星期几
     *
     * @param year
     * @param month
     * @return
     */
    public int getWhichDayOfWeek(int year, int month) {
        return sc.getWeekdayOfMonth(year, month);
    }

    /**
     * @param year
     * @param month
     */
    public int getLastDayOfWeek(int year, int month) {
        return sc.getWeekDayOfLastMonth(year, month,
                sc.getDaysOfMonth(isLeapyear, month));
    }

    public void getCalendar(int year, int month) {
        isLeapyear = sc.isLeapYear(year); // 是否为闰年
        daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); // 某月的总天数
        dayOfWeek = sc.getWeekdayOfMonth(year, month); // 某月第一天为星期几
    }

    public int getWeeksOfMonth() {
        int preMonthRelax = 0;
        if (dayOfWeek != 7) {
            preMonthRelax = dayOfWeek;
        }
        if ((daysOfMonth + preMonthRelax) % 7 == 0) {
            weeksOfMonth = (daysOfMonth + preMonthRelax) / 7;
        } else {
            weeksOfMonth = (daysOfMonth + preMonthRelax) / 7 + 1;
        }
        return weeksOfMonth;
    }

    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = ViewUtil.inflate(activity, R.layout.fragment_session_table);
        try {
            contentResolver = activity.getContentResolver();
            initTime();
            initListView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    /**
     * 列表初始化
     */
    void initListView() throws Exception {
        v = ViewUtil.inflate(activity, R.layout.head_teacher_plans);
        open = ViewUtil.findViewById(v, R.id.open);
        close = ViewUtil.findViewById(v, R.id.close);
        layout = ViewUtil.findViewById(v, R.id.layout);
        open.setOnClickListener(this);
        close.setOnClickListener(this);
        listView = ViewUtil.findViewById(view, R.id.list);
        listView.addHeaderView(v);
        adapter = new TeacherTableAdapter(activity, isAttendClass,
                new ArrayList<VideoModel>(), true);
        listView.setAdapter(adapter);
        loadData();
    }

    /**
     * 时间选择器初始化
     */
    @SuppressWarnings("deprecation")
    void initTime() throws Exception {
        tvDate = ViewUtil.findViewById(view, R.id.tv_date);
        left = ViewUtil.findViewById(view, R.id.left);
        right = ViewUtil.findViewById(view, R.id.right);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        gestureDetector = new GestureDetector(this);
        flipper1 = ViewUtil.findViewById(view, R.id.flipper1);

        dateAdapter = new DateAdapter(activity, getResources(), currentYear,
                currentMonth, currentWeek, currentNum, selectPostion,
                currentWeek == 1 ? true : false);
        addGridView();
        dayNumbers = dateAdapter.getDayNumbers();
        gridView.setAdapter(dateAdapter);
        selectPostion = dateAdapter.getTodayPosition();
        gridView.setSelection(selectPostion);
        flipper1.addView(gridView, 0);
        tvDate.setText(dateAdapter.getCurrentMonth(0) + "月"
                + dayNumbers[0] + "日-"
                + dateAdapter.getCurrentMonth(6) + "月"
                + dayNumbers[6] + "日");
    }

    private void addGridView() {
        @SuppressWarnings("deprecation")
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        gridView = new GridView(activity);
        gridView.setNumColumns(7);
        gridView.setGravity(Gravity.CENTER_VERTICAL);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);
        gridView.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return SessionTableFragment.this.gestureDetector
                        .onTouchEvent(event);
            }
        });
        // 点击某个日期
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Logger.log("day:" + dayNumbers[position]);
                selectPostion = position;
                dateAdapter.setSeclection(position);
                dateAdapter.notifyDataSetChanged();
                tvDate.setText(dateAdapter.getCurrentMonth(0) + "月"
                        + dayNumbers[0] + "日-" + dateAdapter.getCurrentMonth(6)
                        + "月" + dayNumbers[6] + "日");
                try {
                    loadData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        gridView.setLayoutParams(params);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (TeacherTableAdapter.dialog != null
                    && TeacherTableAdapter.dialog.isShowing()) {
                TeacherTableAdapter.dialog.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {

        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    /**
     * 重新计算当前的年月
     */
    public void getCurrent() {
        if (currentWeek > currentNum) {
            if (currentMonth + 1 <= 12) {
                currentMonth++;
            } else {
                currentMonth = 1;
                currentYear++;
            }
            currentWeek = 1;
            currentNum = getWeeksOfMonth(currentYear, currentMonth);
        } else if (currentWeek == currentNum) {
            if (getLastDayOfWeek(currentYear, currentMonth) == 6) {
            } else {
                if (currentMonth + 1 <= 12) {
                    currentMonth++;
                } else {
                    currentMonth = 1;
                    currentYear++;
                }
                currentWeek = 1;
                currentNum = getWeeksOfMonth(currentYear, currentMonth);
            }

        } else if (currentWeek < 1) {
            if (currentMonth - 1 >= 1) {
                currentMonth--;
            } else {
                currentMonth = 12;
                currentYear--;
            }
            currentNum = getWeeksOfMonth(currentYear, currentMonth);
            currentWeek = currentNum - 1;
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {

        if (e1.getX() - e2.getX() > 80) {
            // 向左滑
            try {
                ChooseLeftDate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;

        } else if (e1.getX() - e2.getX() < -80) {
            try {
                ChooseRightDate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return this.gestureDetector.onTouchEvent(event);
    }

    /**
     * 选择左边
     */
    void ChooseLeftDate() throws Exception {
        int gvFlag = 0;
        addGridView();
        currentWeek++;
        getCurrent();
        dateAdapter = new DateAdapter(activity, getResources(), currentYear,
                currentMonth, currentWeek, currentNum, selectPostion,
                currentWeek == 1 ? true : false);
        dayNumbers = dateAdapter.getDayNumbers();
        gridView.setAdapter(dateAdapter);
        tvDate.setText(dateAdapter.getCurrentMonth(0) + "月" + dayNumbers[0]
                + "日-" + dateAdapter.getCurrentMonth(6) + "月" + dayNumbers[6]
                + "日");
        gvFlag++;
        flipper1.addView(gridView, gvFlag);
        dateAdapter.setSeclection(selectPostion);
        this.flipper1.setInAnimation(AnimationUtils.loadAnimation(activity,
                R.anim.push_left_in));
        this.flipper1.setOutAnimation(AnimationUtils.loadAnimation(activity,
                R.anim.push_left_out));
        this.flipper1.showNext();
        flipper1.removeViewAt(0);
        loadData();
    }

    /**
     * 选择右边
     */
    void ChooseRightDate() throws Exception {
        int gvFlag = 0;
        addGridView();
        currentWeek--;
        getCurrent();
        dateAdapter = new DateAdapter(activity, getResources(), currentYear,
                currentMonth, currentWeek, currentNum, selectPostion,
                currentWeek == 1 ? true : false);
        dayNumbers = dateAdapter.getDayNumbers();
        gridView.setAdapter(dateAdapter);
        tvDate.setText(dateAdapter.getCurrentMonth(0) + "月" + dayNumbers[0]
                + "日-" + dateAdapter.getCurrentMonth(6) + "月" + dayNumbers[6]
                + "日");
        gvFlag++;
        flipper1.addView(gridView, gvFlag);
        dateAdapter.setSeclection(selectPostion);
        this.flipper1.setInAnimation(AnimationUtils.loadAnimation(activity,
                R.anim.push_right_in));
        this.flipper1.setOutAnimation(AnimationUtils.loadAnimation(activity,
                R.anim.push_right_out));
        this.flipper1.showPrevious();
        flipper1.removeViewAt(0);
        loadData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                try {
                    ChooseRightDate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.right:
                try {
                    ChooseLeftDate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.open:
                if (layout.getVisibility() == View.GONE) {
                    layout.setVisibility(View.VISIBLE);
                    close.setVisibility(View.VISIBLE);
                    open.setVisibility(View.GONE);
                }
                break;
            case R.id.close:
                if (layout.getVisibility() == View.VISIBLE) {
                    layout.setVisibility(View.GONE);
                    close.setVisibility(View.GONE);
                    open.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    String data;

    @Override
    public void loadData() throws Exception {
        String year = dateAdapter.getCurrentYear(selectPostion) + "";// 当前选择的年份
        String month = "-" + dateAdapter.getCurrentMonth(selectPostion);// 当前选择的月份
        String day = "-" + dayNumbers[selectPostion];// 当前选择的日期
        if (dateAdapter.getCurrentMonth(selectPostion) < 10) {
            month = "-0" + dateAdapter.getCurrentMonth(selectPostion);
        }
        if (day.length() == 2) {
            day = "-0" + dayNumbers[selectPostion];
        }
        data = year + month + day;
        Map<String, String> map = new HashMap<>();
        map.put("classId", activity.app.getClassModel().getId() + "");
        map.put("date", data);
        map.put("gradeId", activity.app.getClassModel().getYear() + "");
        map.put("imei", AppUtil.getImei(activity));
        IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.VIDEO_URL, map,
                fragmentHandler);
        setData();
    }

    void setData() throws Exception {
        int tClassId = activity.app.getClassModel().getId();
        JSONObject jsonObject = activity.mCache
                .getAsJSONObject(IUrContant.VIDEO_URL + tClassId + data);
        if (jsonObject != null) {
            loadLessonFromCache(jsonObject);
            JSONObject data = jsonObject.optJSONObject("course");
            if (data != null) {
                if (!data.optString("suggest").equals("null"))// 老师应该知道
                    ViewUtil.setContent(activity, R.id.info,
                            data.optString("hint"));
                else
                    ViewUtil.setContent(activity, R.id.info, "");
                if (!data.optString("suggest").equals("null"))// 使用建议
                {
                    ViewUtil.setContent(activity, R.id.texts,
                            data.optString("suggest"));
                    view.findViewById(R.id.suggset).setVisibility(View.VISIBLE);
                } else
                    view.findViewById(R.id.suggset).setVisibility(View.GONE);
            }

        }
        handler.sendEmptyMessageDelayed(0, 700);
    }

    private boolean loadLessonFromCache(JSONObject jsonObject) {
        List<VideoModel> list = new ArrayList<VideoModel>();
        JSONArray datalist = jsonObject.optJSONArray("lessonList");
        if (datalist != null) {
            adapter.list.clear();
            for (int i = 0; i < datalist.length(); i++) {
                JSONObject item = datalist.optJSONObject(i);
                if (item != null) {
                    VideoModel model = new VideoModel();
                    model.setId(item.optInt("lessonId"));// 课程id
                    model.setDirId(item.optInt("dirType"));// 分类id
                    model.setName(item.optString("lessonName"));// 课程名字
                    model.setRequireTime(item.optString("requireTime"));// 所需时间
                    try {
                        model.setPreview(item.getString("preview"));// 课程图像
                    } catch (Exception ex) {
                    }
                    model.setSeqNo(item.optInt("seqNo"));// 课程查看
                    model.setTypeName(item.optString("typeName"));// 课程特点
                    model.setGrader(activity.app.getClassModel().getYear());
                    list.add(model);
                }
            }
            WriteMsgUitl.WriteVideo(contentResolver, list,
                    dateAdapter.getCurrentYear(selectPostion),
                    dateAdapter.getCurrentMonth(selectPostion),
                    StringUtil.toInt(dayNumbers[selectPostion]));
            return true;
        }
        return false;
    }

    @Override
    public void httpSuccess(HttpResponseModel message) throws Exception {
        super.httpSuccess(message);
        if (message.getUrl().equals(IUrContant.VIDEO_URL)) {
            JSONObject jsonObject = new JSONObject(new String(
                    message.getResponse()));
            if (jsonObject.optInt("retcode") == 1) {
                int tClassId = activity.app.getClassModel().getId();
                activity.mCache.put(IUrContant.VIDEO_URL + tClassId + data,
                        jsonObject);
                List<VideoModel> list = new ArrayList<VideoModel>();
                JSONArray datalist = jsonObject.optJSONArray("lessonList");
                JSONArray userlist = jsonObject
                        .optJSONArray("userdefLessonList");
                if (datalist != null) {
                    adapter.list.clear();
                    for (int i = 0; i < datalist.length(); i++) {
                        JSONObject item = datalist.optJSONObject(i);
                        if (item != null) {
                            VideoModel model = new VideoModel();
                            model.setId(item.optInt("lessonId"));// 课程id
                            model.setDirId(item.optInt("dirType"));// 分类id
                            model.setName(item.optString("lessonName"));// 课程名字
                            model.setRequireTime(item.optString("requireTime"));// 所需时间
                            model.setPreview(item.getString("preview"));// 课程图像
                            model.setSeqNo(item.optInt("seqNo"));// 课程查看
                            model.setTypeName(item.optString("typeName"));// 课程特点
                            model.setGrader(activity.app.getClassModel()
                                    .getYear());
                            model.setIsUser(1);
                            list.add(model);
                        }
                    }
                    if (userlist != null) {
                        for (int i = 0; i < userlist.length(); i++) {
                            JSONObject item = userlist.optJSONObject(i);
                            if (item != null) {
                                VideoModel model = new VideoModel();
                                model.setId(item.optInt("lessonId"));// 课程id
                                model.setDirId(item.optInt("dirType"));// 分类id
                                model.setName(item.optString("lessonName"));// 课程名字
                                model.setRequireTime(item
                                        .optString("requireTime"));// 所需时间
                                model.setPreview(item.getString("preview"));// 课程图像
                                model.setSeqNo(item.optInt("seqNo"));// 课程查看
                                model.setTypeName(item.optString("typeName"));// 课程特点
                                model.setGrader(activity.app.getClassModel()
                                        .getYear());
                                model.setIsUser(2);
                                list.add(model);
                            }
                        }
                    }
                    contentResolver
                            .delete(Uri.parse(VideoProvider.VIDEOS_URL),
                                    "_year=? and _month=? and _day=? and _gradeid=?",
                                    new String[]{
                                            dateAdapter
                                                    .getCurrentYear(selectPostion)
                                                    + "",
                                            dateAdapter
                                                    .getCurrentMonth(selectPostion)
                                                    + "",
                                            dayNumbers[selectPostion],
                                            activity.app.getClassModel()
                                                    .getYear() + ""});
                    WriteMsgUitl.WriteVideo(contentResolver, list,
                            dateAdapter.getCurrentYear(selectPostion),
                            dateAdapter.getCurrentMonth(selectPostion),
                            StringUtil.toInt(dayNumbers[selectPostion]));
                    setData();
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Cursor cursor = contentResolver.query(
                    Uri.parse(VideoProvider.VIDEOS_URL), null,
                    "_year=? and _month=? and _day=? and _gradeid=?",
                    new String[]{
                            dateAdapter.getCurrentYear(selectPostion) + "",
                            dateAdapter.getCurrentMonth(selectPostion) + "",
                            dayNumbers[selectPostion],
                            activity.app.getClassModel().getYear() + ""},
                    " _isuser asc ");
            adapter.list.clear();
            while (cursor.moveToNext()) {
                VideoModel model = new VideoModel();
                model.setId(cursor.getInt(1));
                model.setDirId(cursor.getInt(2));
                model.setType(cursor.getInt(3));
                model.setName(cursor.getString(4));
                model.setRequireTime(cursor.getString(5));
                model.setPreview(cursor.getString(6));
                model.setSeqNo(cursor.getInt(7));
                model.setTypeName(cursor.getString(8));
                model.setIsUser(cursor.getInt(13));
                adapter.list.add(model);
            }
            cursor.close();
            adapter.notifyDataSetChanged();
            if (adapter.list.size() <= 0) {
                view.findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                view.findViewById(R.id.list).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.no_data).setVisibility(View.GONE);
                view.findViewById(R.id.list).setVisibility(View.VISIBLE);
            }
        }
    };
}
