package cn.kiway.yjhz.activity.simulator;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.kiway.yjhz.R;
import cn.kiway.yjhz.activity.BaseActivity;
import cn.kiway.yjhz.adapter.MyViewPagerAdapter;
import cn.kiway.yjhz.adapter.session.GTeacherTableAdapter;
import cn.kiway.yjhz.adapter.session.ZimuAdapter;
import cn.kiway.yjhz.model.ClassModel;
import cn.kiway.yjhz.model.VideoCateMode;
import cn.kiway.yjhz.model.VideoModel;
import cn.kiway.yjhz.utils.CommonUitl;
import cn.kiway.yjhz.utils.IConstant;
import cn.kiway.yjhz.utils.Logger;
import cn.kiway.yjhz.utils.PinyinUtils;
import cn.kiway.yjhz.utils.okhttp.HttpRequestUrl;
import cn.kiway.yjhz.utils.okhttp.HttpUtils;
import cn.kiway.yjhz.utils.views.viewPager.FixedSpeedScroller;
import cn.kiway.yjhz.utils.views.viewPager.StereoPagerTransformer;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/4.
 */

public class GSessionDetailActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    GridView pgridView;
    public VideoCateMode videoCateMode;
    ClassModel classModel;
    // GTeacherTableAdapter adapter;
    boolean hasMeasured = false;// 只获取一次键盘的宽高
    public int height = 0;// 键盘的高度
    public int width = 0;// 键盘的宽度
    private RelativeLayout relative;
    ZimuAdapter zimuAdapter;
    EditText editText;
    private ViewPager viewPager;
    private LinearLayout group;//圆点指示器
    private ImageView[] ivPoints;//小圆点图片的集合
    private int totalPage; //总的页数
    private List<View> viewPagerList;//GridView作为一个View对象添加到ViewPager集合中
    public List<VideoModel> listv = new ArrayList<VideoModel>();// 显示在界面上的数据
    public List<VideoModel> listData = new ArrayList<VideoModel>();// 显示在界面上的数据
    private boolean isTeacherSession = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_sessiondetail);
        videoCateMode = (VideoCateMode) getIntent().getExtras()
                .getSerializable(IConstant.BUNDLE_PARAMS);
        classModel = (ClassModel) getIntent().getExtras().getSerializable(IConstant.BUNDLE_PARAMS1);
        getKeyBoradHW();
        initView();
        if (getIntent().getExtras().getInt("Collect") == 3) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setData();
                }
            }, 1000);
        } else {
            loadData();
        }
    }


    void initView() {
        group = (LinearLayout) findViewById(R.id.points);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pgridView = (GridView) findViewById(R.id.girdViewP);
        editText = (EditText) findViewById(R.id.editText);
        editText.setOnClickListener(this);
        editText.addTextChangedListener(this);
        zimuAdapter = new ZimuAdapter(this, CommonUitl.listZi());
        pgridView.setAdapter(zimuAdapter);
        pgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (zimuAdapter.list.get(i).equals("123")) {
                    zimuAdapter.list = CommonUitl.listShu();
                    zimuAdapter.notifyDataSetChanged();
                    return;
                }
                if (zimuAdapter.list.get(i).equals("ABC")) {
                    zimuAdapter.list = CommonUitl.listZi();
                    zimuAdapter.notifyDataSetChanged();
                    return;
                }
                int index = editText.getSelectionStart();
                if (zimuAdapter.list.get(i).equals("←")) {
                    if (editText.getText().length() <= 0)
                        return;
                    editText.setText(editText.getText().toString().substring(0, editText.getText().toString().length
                            () - 1));
                    return;
                }
                Editable edit = editText.getEditableText();//获取EditText的文字
                if (index < 0 || index >= edit.length()) {
                    edit.append(zimuAdapter.list.get(i));
                } else {
                    edit.insert(index, zimuAdapter.list.get(i));//光标所在位置插入文字
                }
            }
        });
        TextView textView = (TextView) findViewById(R.id.title);
        if (videoCateMode != null) {
                textView.setText(videoCateMode.getName());
        }else {
            if (getIntent().getExtras().getInt("Collect") == 3) {
                textView.setText("最近播放");
            } else if (getIntent().getExtras().getInt("Collect") == 1) {
                textView.setText("我的收藏");
            } else if (getIntent().getExtras().getInt("Collect") == 2) {
                textView.setText("我的课程");
            }
        }
    }

    void loadData() {
        if (videoCateMode == null) {
            if (getIntent().getExtras().getInt("isKinect") == 0) {
                yjhzAppication.mHttpClient.newCall(HttpUtils.get(HttpRequestUrl.GET_MY_SESSION + classModel.getId(),
                        yjhzAppication.session)).enqueue(this);
            } else {
                yjhzAppication.mHttpClient.newCall(HttpUtils.get(HttpRequestUrl.GET_KINECTSESSION_URL,
                        yjhzAppication.session)).enqueue(this);
            }
        } else {
            yjhzAppication.mHttpClient.newCall(HttpUtils.get(HttpRequestUrl.GET_ONE_SESSION.replace
                            ("{courseId}", videoCateMode.getId() + "") + classModel.getId(),
                    yjhzAppication.session)).enqueue(this);
        }
    }

    void setData() {
        JSONArray array;
        if (getIntent().getExtras().getInt("Collect") != 3) {
            if (videoCateMode == null) {
                if (getIntent().getExtras().getInt("isKinect") == 0) {//我的课程
                    JSONObject da = mCache
                            .getAsJSONObject(HttpRequestUrl.GET_MY_SESSION);
                    if (da != null) {
                        if (getIntent().getExtras().getInt("Collect") == 2) {
                            array = da.optJSONObject("data").optJSONArray("createSection");
                            if (array != null) {
                                loadLessonFromCache(array, true);
                            }
                        } else {
                            array = da.optJSONObject("data").optJSONArray("collectSection");
                            if (array != null) {
                                loadLessonFromCache(array, false);
                            }
                        }
                    }
                } else {//体感课程
                    JSONObject data = mCache.getAsJSONObject(HttpRequestUrl.GET_KINECTSESSION_URL);
                    if (data != null) {
                        listv.clear();
                        listData.clear();
                        JSONArray array1 = data.optJSONArray("data");
                        for (int i = 0; i < array1.length(); i++) {
                            VideoModel model = new VideoModel();
                            JSONObject item = array1.optJSONObject(i);
                            //  if (item.optInt("isKT") == 1) {//判断是否开通了该课程，来显示隐藏，如果需要全部显示，去掉
                            model.setId(item.optInt("id"));
                            model.setName(item.optString("name"));
                            model.setPreview(item.optString("icon"));
                            model.setSessionName(item.optString("name"));
                            model.setTeachingPreare(item.optString("teachingPrepare"));
                            model.setTeachingAim(item.optString("aim"));
                            model.setReadCount(item.optInt("readCount"));
                            model.setKinectPackageName(item.optString("packageName"));
                            model.setKiectSession(true);
                            model.setKiectSessionContent(item.optString("production"));
                            model.setIsKT(item.optInt("isKT"));
                            model.setKiectApkDownLoadUrl(item.optString("downloadUrl"));
                            model.setPingYin(PinyinUtils.getPinyin2(item.optString("name")));
                            listv.add(model);
                            listData.add(model);
                        }
                        // }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initData();
                            }
                        });
                    }
                }
            } else {//安排的课程
                JSONObject da = mCache
                        .getAsJSONObject(HttpRequestUrl.GET_ONE_SESSION.replace("{courseId}", videoCateMode.getId() +
                                ""));
                if (da != null) {
                    array = da.optJSONArray("courseSectionList");
                    if (array != null) {
                        loadLessonFromCache(array, true);
                    }
                }
            }
        } else {
            array = mCache.getAsJSONArray(classModel.getId());
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.optJSONObject(i);
                VideoModel model = new VideoModel();
                model.setId(item.optInt("id"));
                model.setName(item.optString("name"));
                model.setHomework(item.optString("homework"));
                model.setPreview(item.optString("preview"));
                model.setTeachingPreare(item.optString("teachingPreare"));
                model.setTeachingAim(item.optString("teachingAim"));
                model.setReadCount(item.optInt("readCount"));
                model.setGrader(item.optString("gragde"));
                model.setPingYin(item.optString("pingYin"));
                listv.add(model);
                listData.add(model);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initData();
                }
            });
        }
    }

    private void loadLessonFromCache(JSONArray array, boolean isClear) {
        if (isClear) {
            listv.clear();
            listData.clear();
        }
        if (videoCateMode == null) {
            isTeacherSession = true;
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.optJSONObject(i);
                VideoModel model = new VideoModel();
                model.setId(item.optInt("id"));
                model.setName(item.optString("name"));
                model.setHomework(item.optString("homework"));
                model.setPreview(item.optString("icon"));
                model.setTeachingPreare(item.optString("teaching_prepare"));
                model.setTeachingAim(item.optString("aim"));
                model.setReadCount(item.optInt("count"));
                model.setGrader(item.optString("grade_id"));
                model.setPingYin(PinyinUtils.getPinyin2(item.optString("name")));
                listv.add(model);
                listData.add(model);
            }
        } else {
            isTeacherSession = false;
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.optJSONObject(i);
                VideoModel model = new VideoModel();
                model.setId(item.optInt("id"));
                model.setName(item.optString("name"));
                model.setHomework(item.optString("homework"));
                model.setPreview(item.optString("icon"));
                model.setSessionName(videoCateMode.getName());
                model.setTeachingPreare(item.optString("teaching_prepare"));
                model.setTeachingAim(item.optString("aim"));
                model.setReadCount(item.optInt("count"));
                model.setGrader(videoCateMode.getGradeId());
                model.setPingYin(PinyinUtils.getPinyin2(item.optString("name")));
                listv.add(model);
                listData.add(model);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        });
    }

    /**
     * 设置setcurrent的速度
     */
    private void setViewPagerScrollSpeed() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext());
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
    }

    private void initData() {
        totalPage = (int) Math.ceil(listv.size() * 1.0 / 6);
        if (totalPage == 0) {
            findViewById(R.id.no_data).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.no_data).setVisibility(View.GONE);
        }
        viewPagerList = new ArrayList<View>();
        for (int i = 0; i < totalPage; i++) {
            //每个页面都是inflate出一个新实例
            final GridView gridView = (GridView) View.inflate(this, R.layout.gird_view, null);
            gridView.setNumColumns(2);
            gridView.setAdapter(new GTeacherTableAdapter(this, listv, isTeacherSession, classModel, width, height, i, 6));
            //添加item点击监听
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View view,
                                        int position, long arg3) {
                    view.performClick();
                }
            });//每一个GridView作为一个View对象添加到ViewPager集合中
            viewPagerList.add(gridView);
        }
        viewPager.setAdapter(new MyViewPagerAdapter(viewPagerList));//设置ViewPager适配器
        viewPager.setPageTransformer(false, new StereoPagerTransformer(width));
        setViewPagerScrollSpeed();
        group.removeAllViews();
        ivPoints = new ImageView[totalPage];//添加小圆点
        for (int i = 0; i < totalPage; i++) {
            ivPoints[i] = new ImageView(this);
            if (i == 0) {
                ivPoints[i].setImageResource(R.drawable.ic_brightness_2);
            } else {
                ivPoints[i].setImageResource(R.drawable.ic_brightness_1);
            }
            ivPoints[i].setPadding(8, 8, 8, 8);
            group.addView(ivPoints[i]);
        }
        //设置ViewPager的滑动监听，主要是设置点点的背景颜色的改变
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < totalPage; i++) {
                    if (i == position) {
                        ivPoints[i].setImageResource(R.drawable.ic_brightness_2);
                    } else {
                        ivPoints[i].setImageResource(R.drawable.ic_brightness_1);
                    }
                }
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Logger.log(call.request().url().toString());
        try {
            JSONObject data = new JSONObject(response.body().string());
            Logger.log(data);
            if (call.request().url().toString().equals(HttpRequestUrl.GET_MY_SESSION + classModel.getId())
                    && data.optInt("StatusCode") == 200) {
                mCache.put(HttpRequestUrl.GET_MY_SESSION,
                        data);
                setData();
            } else if (call.request().url().toString().equals(HttpRequestUrl.GET_KINECTSESSION_URL)) {
                if (data.optInt("StatusCode") == 200) {
                    mCache.put(HttpRequestUrl.GET_KINECTSESSION_URL, data);
                    setData();
                }
            } else {
                mCache.put(HttpRequestUrl.GET_ONE_SESSION.replace("{courseId}", videoCateMode.getId() + ""),
                        data.optJSONObject("data"));
                setData();
            }
        } catch (Exception e) {
            login();
            setData();
            e.printStackTrace();
        }
    }

    /**
     * 获取控制键盘的宽高
     *
     * @author Administrator 直接layout.getMesaguse获取出来是0
     */
    void getKeyBoradHW() {
        relative = (RelativeLayout) findViewById(R.id.relative);
        ViewTreeObserver vto = relative.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (hasMeasured == false) {
                    height = relative.getMeasuredHeight();
                    width = relative.getMeasuredWidth();
                    hasMeasured = true;
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editText:
                findViewById(R.id.layouts).setVisibility(View.VISIBLE);
                pgridView.setSelected(true);
                pgridView.setSelection(0);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && findViewById(R.id.layouts).getVisibility() == View.VISIBLE) {
            findViewById(R.id.layouts).setVisibility(View.GONE);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        List<VideoModel> newValues = new ArrayList<VideoModel>();
        if (TextUtils.isEmpty(s.toString()) || s.toString().equals("")) {
            newValues = listData;
        } else {
            for (VideoModel str : listData) {
                if ((-1 != str.getName().toLowerCase()
                        .indexOf(s.toString()))
                        || (-1 != str.getSessionName()
                        .toLowerCase()
                        .indexOf(s.toString()))
                        || (-1 != str.getTypeName().toLowerCase()
                        .indexOf(s.toString())) || (-1 != str.getPingYin().toLowerCase()
                        .indexOf(s.toString().toLowerCase()))) {
                    newValues.add(str);
                }
            }
        }
        listv = newValues;
        initData();
    }
}
