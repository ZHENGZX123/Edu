package cn.kiway.yjhz.activity.simulator;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.kiway.yjhz.R;
import cn.kiway.yjhz.activity.BaseActivity;
import cn.kiway.yjhz.activity.box.ImageViewActivity;
import cn.kiway.yjhz.activity.box.PptActivity;
import cn.kiway.yjhz.activity.box.VideoActivity;
import cn.kiway.yjhz.adapter.MyViewPagerAdapter;
import cn.kiway.yjhz.adapter.session.GAllSessionAdapter;
import cn.kiway.yjhz.adapter.session.GKinectSessionAdapter;
import cn.kiway.yjhz.dialog.LoginDialog;
import cn.kiway.yjhz.model.ClassModel;
import cn.kiway.yjhz.model.VideoCateMode;
import cn.kiway.yjhz.model.VideoModel;
import cn.kiway.yjhz.qrcode.QRCodeUtils;
import cn.kiway.yjhz.tfcard.TFCard;
import cn.kiway.yjhz.utils.CommonUitl;
import cn.kiway.yjhz.utils.GlobeVariable;
import cn.kiway.yjhz.utils.IConstant;
import cn.kiway.yjhz.utils.JsAndroidInterface;
import cn.kiway.yjhz.utils.PinyinUtils;
import cn.kiway.yjhz.utils.SharedPreferencesUtil;
import cn.kiway.yjhz.utils.SilentInstall;
import cn.kiway.yjhz.utils.okhttp.HttpRequestUrl;
import cn.kiway.yjhz.utils.okhttp.HttpUtils;
import cn.kiway.yjhz.utils.views.viewPager.FixedSpeedScroller;
import cn.kiway.yjhz.utils.views.viewPager.StereoPagerTransformer;
import cn.kiway.yjhz.utils.writeErrorLog;
import cn.kiway.yjhz.wifimanager.MessageHander.AccpectMessageHander;
import cn.kiway.yjhz.wifimanager.WIFIHotSpot;
import cn.kiway.yjhz.wifimanager.WifiAdmin;
import cn.kiway.yjhz.wifimanager.netty.PushServer;
import cn.kiway.yjhz.wifimanager.udp.BroadCastUdp;
import cn.kiway.yjhz.wifimanager.websocket.WebsocketControlCenter;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;

import static cn.kiway.yjhz.R.id.userpic;


/**
 * Created by Administrator on 2017/7/4.
 */

public class GSessionActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    boolean hasMeasured = false;// 只获取一次键盘的宽高
    public int height;// 键盘的高度
    public int width;// 键盘的宽度
    public ClassModel classModel;
    // public Dialog webViewDialog;
    WebView webView;
    private ViewPager viewPager;
    private LinearLayout group;//圆点指示器
    private ImageView[] ivPoints;//小圆点图片的集合
    private int totalPage; //总的页数
    private List<View> viewPagerList;//GridView作为一个View对象添加到ViewPager集合中
    private RelativeLayout relative;
    //////
    public static String curWIFISID;// wifi SSID
    public static String WIFI_PWD = "12345678";// wifi密码
    private ImageView imageview1;// 二维码图片
    int connectWifiNumber;//wifi连接次数
    private WifiManager wifiManager;// wifi管理
    private WIFIHotSpot wifiHotManager = new WIFIHotSpot();// wifi热点
    private TextView yjhzTag;// wifi名字
    private static TextView heiziSdInfo;// sd卡信息
    private static boolean hasSDCard = false;// 是否有sd卡
    WifiAdmin admin;// wifi连接
    protected String wifiInfo;// 从内存获取的wifi信息
    public static GSessionActivity gSessionActivity;
    public static boolean isHot = false;// 是否为wifi热点
    public LoginDialog dialog;// 连接wifi时候的dialog
    static List<String> sdpaths = TFCard.getExtSDCardPaths();// TF卡数据路径
    String MAC;// mac地址
    BroadCastUdp broadCastUdp;
    private WebsocketControlCenter mWebsocketControlCenter;
    AccpectMessageHander accpectMessageHander = new AccpectMessageHander();//websocket与tcp协议信息接收协议的


    private boolean isKicnect = false;
    public List<VideoCateMode> list = new ArrayList<VideoCateMode>();// 显示所有上的数据
    public List<VideoModel> listK = new ArrayList<VideoModel>(); //显示体感课程上的数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_first);
        getKeyBoradHW();
        initView();
        CommonUitl.setTimeZone(this);//设置时区
        gSessionActivity = this;
        AccpectMessageHander.setActivity(this);
        admin = new WifiAdmin(this);//获取WiFi对象
        wifiInfo = SharedPreferencesUtil.getString(this,
                GlobeVariable.WIFI_INFO);//获取保存的wifi信息
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        dialog = new LoginDialog(this);
        findSDPath();//获取sd路径
        ChechSd();//检查sdk
        if (CommonUitl.isWifiActive(this)) {//如果当前网络环境是wifi则直接用
            curWIFISID = CommonUitl.getConnectWifiSsid(this);//获取当前的WiFiSSID
            WIFI_PWD = "";
            isHot = false;
            initialScreen();//更新界面二维码
            SharedPreferencesUtil.save(this, GlobeVariable.WIFI_INFO,//保存当前的wifi
                    curWIFISID + ":::" + "1" + ":::" + "2");
            broadCastUdp = new BroadCastUdp(MAC + ":::" + heiziSdInfo.getText());//启动广播
            broadCastUdp.start();
        } else {
            if (!wifiInfo.equals("")
                    && // 判断是否有wifi以及是否能被搜索到，没有自建热点
                    admin.canScannable(wifiInfo.split(":::")[0])
                    && wifiInfo.split(":::").length > 2) {//有保存的WiFi
                WIFIHotSpot.closeWIFIHotspot(wifiManager);//关闭热点
                yjhzAppication.setwifiName(wifiInfo.split(":::")[0]);//设置当期连接wifi
                yjhzAppication.setwifiPs(wifiInfo.split(":::")[1]);
                yjhzAppication.setwifiTp(Integer.parseInt(wifiInfo.split(":::")[2]));
                admin.openWifi();//打开wifi
                admin.connectConfiguratedWifi(wifiInfo.split(":::")[0]);
                dialog.setTitle("初始化中，请稍后");
                dialog.show();
            } else {//没有保存的wifi和当前没有连接wifi 启动热点
                isHot = true;
                openWifiHotSpot();
            }
        }
        forData();
    }

    private void initData() {
        if (!isKicnect)
            totalPage = (int) Math.ceil(list.size() * 1.0 / 6);
        else
            totalPage = (int) Math.ceil(listK.size() * 1.0 / 6);
        viewPagerList = new ArrayList<View>();
        viewPager.setLayoutParams(new RelativeLayout.LayoutParams(height / 2 * 3, height));
        relative.setLayoutParams(new LinearLayout.LayoutParams(height / 2 * 3, height));
        for (int i = 0; i < totalPage; i++) {
            //每个页面都是inflate出一个新实例
            final int page = i;
            final GridView gridView = (GridView) View.inflate(this, R.layout.gird_view, null);
            gridView.setLayoutParams(new LinearLayout.LayoutParams(height / 2 * 3, height));
            if (!isKicnect)
                gridView.setAdapter(new GAllSessionAdapter(this, list, i, 6, height));//添加item点击监听
            else
                gridView.setAdapter(new GKinectSessionAdapter(this, listK, i, 6, height));//添加item点击监听
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    if (!isKicnect) {
                        if (classModel == null) {
                            return;
                        }
                        VideoCateMode model = list.get(position + page * 6);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(IConstant.BUNDLE_PARAMS, model);
                        bundle.putSerializable(IConstant.BUNDLE_PARAMS1, classModel);
                        Intent intent = new Intent(GSessionActivity.this, GSessionDetailActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        VideoModel model = listK.get(position + page * 6);
                        CommonUitl.stratApk(GSessionActivity.this, model.getKinectPackageName());
                    }
                }
            });//每一个GridView作为一个View对象添加到ViewPager集合中
            viewPagerList.add(gridView);
        }
        viewPager.setAdapter(new MyViewPagerAdapter(viewPagerList));//设置ViewPager适配器
        viewPager.setPageTransformer(false, new StereoPagerTransformer(height / 2 * 3));
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

    void initView() {
        group = (LinearLayout) findViewById(R.id.points);
        relative = (RelativeLayout) findViewById(R.id.relative);
        imageview1 = (ImageView) findViewById(R.id.imageView1);
        yjhzTag = (TextView) findViewById(R.id.wifi_name_title);
        heiziSdInfo = (TextView) findViewById(R.id.sd_title);
        MAC = CommonUitl.getLocalEthernetMacAddress();//获取mac地址
        CommonUitl.setContent(this, R.id.hezi_title, MAC);//设置界面上的mac地址
        CommonUitl.setContent(this, R.id.xignq_title, CommonUitl.getWeek()
                + "\n" + CommonUitl.getDate());//设置时间
        RadioGroup rg = (RadioGroup) findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(this);
        ImageView imageButton = (ImageView) findViewById(R.id.Recently);
        imageButton.setFocusable(true);
        imageButton.setFocusableInTouchMode(true);
        imageButton.requestFocus();
        imageButton.requestFocusFromTouch();
    }

    void loadData() {
        if (classModel == null)
            return;
        yjhzAppication.mHttpClient.newCall(HttpUtils.get(HttpRequestUrl.GET_ALL_SESSION + classModel.getId()
                + "&gradeId=" + classModel.getYear(), yjhzAppication.session)).enqueue(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {//如果是返回的标识
            //获取数据
            Bundle bundle = data.getExtras();
            classModel = (ClassModel) bundle.getSerializable(IConstant.BUNDLE_PARAMS);
            TextView named = (TextView) findViewById(R.id.user_name);
            named.setText(named.getText().toString().split("\n")[0] + "\n" + classModel.getClassName());
            loadData();
        }
    }


    void setData() throws Exception {
        if (!isKicnect) {
            if (classModel == null)
                return;
            JSONArray array = mCache
                    .getAsJSONArray(HttpRequestUrl.GET_ALL_SESSION
                            + classModel.getId());
            if (array != null) {
                list.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.optJSONObject(i);
                    VideoCateMode model = new VideoCateMode();
                    model.setKcln(item.optString("idea"));
                    model.setKcjy(item.optString("teaching_suggest"));
                    model.setAllCount(item.optString("section_count"));
                    model.setReadCount(item.optString("count"));
                    model.setGradeId(item.optString("grade_id"));
                    model.setPreview(item.optString("icon"));
                    model.setId(item.optInt("id"));
                    model.setName(item.optString("name"));
                    if (item.optString("name").equals("绘本阅读"))
                        model.setPreview("drawable://" + R.drawable.pic_hbyd);
                    if (item.optString("name").equals("课间等过渡时间教学资源"))
                        model.setPreview("drawable://" + R.drawable.pic_jxzy);
                    if (item.optString("name").equals("奇趣玩字"))
                        model.setPreview("drawable://" + R.drawable.pic_qqwz);
                    if (item.optString("name").equals("我的安全我注意"))
                        model.setPreview("drawable://" + R.drawable.pic_aqjy);
                    if (item.optString("name").equals("游古寻源"))
                        model.setPreview("drawable://" + R.drawable.pic_ygxy);
                    list.add(model);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                    }
                });
            }
        } else {
            JSONObject data = mCache.getAsJSONObject(HttpRequestUrl.GET_KINECTSESSION_URL);
            if (data != null) {
                listK.clear();
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
                    listK.add(model);
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
    }


    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            JSONObject data = new JSONObject(response.body().string());
            if (classModel != null && call.request().url().toString().equals(HttpRequestUrl.GET_ALL_SESSION +
                    classModel.getId() + "&gradeId=" + classModel.getYear())
                    && data.optInt("StatusCode") == 200) {
                mCache.put(HttpRequestUrl.GET_ALL_SESSION
                                + classModel.getId(),
                        data.optJSONArray("data"));
                isKicnect = false;
                setData();
            } else if (call.request().url().toString().equals(HttpRequestUrl.LOGIN_URL) && data.optInt("StatusCode")
                    == 200) {
                Headers headers = response.headers();
                List<String> cookies = headers.values("Set-Cookie");
                String sessionInfo = cookies.get(0);
                yjhzAppication.session = sessionInfo.substring(0, sessionInfo.indexOf(";"));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(GSessionActivity.this, ClassListActivity.class);
                        startActivityForResult(intent, 999);
                    }
                });
            } else if (call.request().url().toString().equals(HttpRequestUrl.GET_KINECTSESSION_URL) && data.optInt
                    ("StatusCode")
                    == 200) {
                mCache.put(HttpRequestUrl.GET_KINECTSESSION_URL, data);
                isKicnect = true;
                setData();
            }
        } catch (Exception e) {
            if (classModel != null && call.request().url().toString().equals(HttpRequestUrl.GET_ALL_SESSION +
                    classModel.getId()
                    + "&gradeId=" + classModel.getYear())) {
                try {
                    setData();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    teaching();
                    Toast.makeText(GSessionActivity.this, "请重新扫描", Toast.LENGTH_SHORT).show();
                }
            });
            writeErrorLog.writeErrorLogger(e);
            e.printStackTrace();
        }
    }


    /**
     * 获取控制键盘的宽高
     *
     * @author Administrator 直接layout.getMesaguse获取出来是0
     */
    void getKeyBoradHW() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewTreeObserver vto = viewPager.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (hasMeasured == false) {
                    height = viewPager.getMeasuredHeight();
                    width = viewPager.getMeasuredWidth();
                    hasMeasured = true;
                }
                return true;
            }
        });
    }

    private void teaching() {
//        if (webViewDialog != null && webViewDialog.isShowing() && webView != null) {
//            webView.loadUrl("file:///android_asset/scan/scan.html");
//            return;
//       }
//        webViewDialog = new Dialog(this, R.style.popupDialog);
//        webViewDialog.setContentView(R.layout.dialog_webview);
        Cancle();
        webView = (WebView) findViewById(R.id.webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        } else {
            try {
                Class<?> clazz = webView.getSettings().getClass();
                Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(webView.getSettings(), true);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setTextSize(WebSettings.TextSize.NORMAL);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.setVerticalScrollBarEnabled(false);
        webView.addJavascriptInterface(new JsAndroidInterface(this), "box");
        webView.loadUrl("file:///android_asset/scan/scan.html");
//        webViewDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN) {
//                    webView.loadUrl("file:///android_asset/scan/scan.html");
//                    return true;
//                }
//                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//                    finish();
//                    return true;
//                }
//                return false;
//            }
//        });
//        webViewDialog.show();
//        webViewDialog.setCancelable(false);
    }

    //扫码登录返回
    public void login(String userName, String password) {
        // webViewDialog.dismiss();
        Log.e("*************", userName);
        Log.e("*************", password);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.user_ok).setVisibility(View.GONE);
            }
        });
        //保存帐号密码
        SharedPreferencesUtil.save(this, "userName", userName);
        SharedPreferencesUtil.save(this, "password", password);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userName", userName);
        map.put("password", password);
        map.put("type", "1");
        handler.removeMessages(20);
        yjhzAppication.mHttpClient.newCall(HttpUtils.post(HttpRequestUrl.LOGIN_URL, map)).enqueue(this);
    }

    //扫码登录返回
    public void UserInfo(final String name, final String pics) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.user).setVisibility(View.VISIBLE);
                findViewById(userpic).setVisibility(View.VISIBLE);
                findViewById(R.id.user_name).setVisibility(View.VISIBLE);
                findViewById(R.id.user_ok).setVisibility(View.VISIBLE);
                findViewById(R.id.imageView1).setVisibility(View.GONE);
                ImageView pic = (ImageView) findViewById(userpic);
                TextView named = (TextView) findViewById(R.id.user_name);
                imageLoader.displayImage(pics, pic, options);
                named.setText(name);
                handler.sendEmptyMessageDelayed(20, 15000);
            }
        });
    }

    //扫码登录返回
    public void BoxCode(String code) {
        saveQRCodeUtils(imageview1, code);
    }

    //扫码登录返回
    public void Cancle() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.user).setVisibility(View.GONE);
                findViewById(userpic).setVisibility(View.GONE);
                findViewById(R.id.user_name).setVisibility(View.GONE);
                findViewById(R.id.user_ok).setVisibility(View.GONE);
                findViewById(R.id.imageView1).setVisibility(View.VISIBLE);
            }
        });
    }

    //界面美观，假数据
    void forData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                list.clear();
                for (int i = 0; i < 6; i++) {
                    VideoCateMode model = new VideoCateMode();
                    model.setKcln("");
                    if (i == 0) {
                        model.setName("绘本阅读");
                        model.setPreview("drawable://" + R.drawable.pic_hbyd);
                    } else if (i == 1) {
                        model.setName("奇趣玩字");
                        model.setPreview("drawable://" + R.drawable.pic_qqwz);
                    } else if (i == 2) {
                        model.setName("我的安全我注意");
                        model.setPreview("drawable://" + R.drawable.pic_aqjy);
                    } else if (i == 3) {
                        model.setName("课间等过渡时间教学资源");
                        model.setPreview("drawable://" + R.drawable.pic_jxzy);
                    } else if (i == 4) {
                        model.setName("游古寻源");
                        model.setPreview("drawable://" + R.drawable.pic_ygxy);
                    } else if (i == 5) {
                        model.setName("绘本阅读");
                        model.setPreview("drawable://" + R.drawable.pic_hbyd);
                    }
                    list.add(model);
                }
                initData();
                teaching();
            }
        }, 500);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (yjhzAppication.session == null || yjhzAppication.session.equals("")) {
            teaching();
            Toast.makeText(this, "请使用开维宝宝老师端扫描二维码登录才能使用遥控上课", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
            if (!yjhzAppication.session.equals("")) {//如果已经扫了，则无需再扫
                Bundle bundle = new Bundle();
                bundle.putInt(IConstant.BUNDLE_PARAMS, 1);
                Intent intent = new Intent(GSessionActivity.this, ClassListActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 999);
                return true;
            }
            //teaching();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //我的课程
    public void MySession(View view) {
        if (classModel == null)
            return;
        Bundle bundle = new Bundle();
        bundle.putSerializable(IConstant.BUNDLE_PARAMS, null);
        bundle.putSerializable(IConstant.BUNDLE_PARAMS1, classModel);
        bundle.putInt("Collect", 2);
        Intent intent = new Intent(this, GSessionDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //我的收藏
    public void MyCollect(View view) {
        if (classModel == null)
            return;
        Bundle bundle = new Bundle();
        bundle.putSerializable(IConstant.BUNDLE_PARAMS, null);
        bundle.putSerializable(IConstant.BUNDLE_PARAMS1, classModel);
        bundle.putInt("Collect", 1);
        Intent intent = new Intent(this, GSessionDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //最近播放
    public void RecentlySession(View view) {
        if (classModel == null)
            return;
        Bundle bundle = new Bundle();
        bundle.putSerializable(IConstant.BUNDLE_PARAMS, null);
        bundle.putSerializable(IConstant.BUNDLE_PARAMS1, classModel);
        bundle.putInt("Collect", 3);
        Intent intent = new Intent(this, GSessionDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GlobeVariable.update_time://更新时间
                    handler.sendEmptyMessageDelayed(GlobeVariable.update_time, 1000);
                    CommonUitl.setContent(GSessionActivity.this, R.id.xignq_time,
                            CommonUitl.getFormattedDate().substring(11, 16));
                    break;
                case GlobeVariable.connectWifi://连接wifi
                    if (dialog != null && !dialog.isShowing()) {
                        dialog.setTitle("连接中，请稍后");
                        dialog.show();
                    }
                    WIFIHotSpot.closeWIFIHotspot(wifiManager);//关闭热点，打开wifi启动连接
                    admin.openWifi();
                    handler.sendEmptyMessageDelayed(2000, 1000);// 做延迟，因为立刻开启wifi就连会导致没有连接的动作
                    break;
                case 2000:
                    if (connectWifiNumber > WifiAdmin.IS_OPENED) {//判断连接次数
                        isHot = true;//连接次数大于3次，不连接了，启动热点
                        openWifiHotSpot();//为啥加这个，注意是这个盒子wifi有问题，有时候调起连接，并没有连接的动作
                        dialog.close();
                        Toast.makeText(GSessionActivity.this, "连接不上wifi,请重试",
                                Toast.LENGTH_SHORT).show();
                        handler.removeMessages(2000);
                        return;
                    }
                    if (admin.getWifitate() != WifiAdmin.IS_OPENED)
                        admin.openWifi();//开启连接wifi
                    connectWifiNumber = connectWifiNumber + 1;//次数加一
                    admin.addNetwork(admin.CreateWifiInfo(
                            yjhzAppication.getWifiName(),
                            yjhzAppication.getwifiPs(), yjhzAppication.getWifiTp()));
                    handler.sendEmptyMessageDelayed(2000, 30000);//30秒后判断是否连接成
                    break;
                case GlobeVariable.downLoadApk://检查更新下载apl
                    dialog.setTitle(msg.obj.toString());
                    if (!dialog.isShowing())
                        dialog.show();
                    break;
                case GlobeVariable.downLoadApkSuccess://下载apk成功后启动静默安装
                    dialog.setTitle("安装中,请稍后");
                    if (!dialog.isShowing())
                        dialog.show();
                    final String s = msg.obj.toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SilentInstall installHelper = new SilentInstall();
                            final boolean result = installHelper.install(s);
                            handler.sendEmptyMessage(GlobeVariable.closeDialog);
                            if (result) {//判断静默安装是否成
                                CommonUitl.stratApk(GSessionActivity.this, CommonUitl.DownLoadApkPackName);
                            } else {
                                Toast.makeText(getApplicationContext(), "启动失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).start();
                    break;
                case GlobeVariable.downLoadApkFial://下载失败
                    dialog.close();
                    Toast.makeText(GSessionActivity.this, "启动失败", Toast.LENGTH_SHORT).show();
                    break;
                case GlobeVariable.closeDialog:
                    dialog.close();
                    break;
                case 20:
                    Cancle();
                    teaching();
                    break;
            }
        }
    };

    /**
     * 打开热点
     */
    private void openWifiHotSpot() {
        String tmpWifiId = CommonUitl.getWifiSID(this);//获取热点名字
        if ((tmpWifiId != null) && (!"".equalsIgnoreCase(tmpWifiId.trim()))) {//热点名字不为空
            curWIFISID = tmpWifiId;
        } else {//热点名字为空则创建一个并保存
            curWIFISID = CommonUitl.getWifiHot(this);
            CommonUitl.saveWifiSID(curWIFISID);
        }
        Log.i("TAG", WIFI_PWD + "");
        isHot = true;
        if (curWIFISID != null && WIFI_PWD != null) {//开启热点
            wifiHotManager.setWifiApEnabled(true, wifiManager, curWIFISID,
                    WIFI_PWD);
        }
        initialScreen();//开启热点后更新二维码
    }

    /**
     * 设置界面数据
     */
    public void initialScreen() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                saveQRCodeUtils(imageview1, "");
                if (isHot) {//如果是热点设置红颜色
                    setTextStyle(R.drawable.ic_device_signal_wifi_red,
                            R.color._f41100, yjhzTag, curWIFISID);
                } else {//不是热点
                    yjhzTag.setText(curWIFISID);
                    IConstant.executorService.execute(new Runnable() {
                        public void run() {
                            if (CommonUitl.ping()) {//ping的使我们网站，能ping白色，不能黄色
                                setTextStyle(R.drawable.ic_device_signal_wifi_white,
                                        R.color._ffffff, yjhzTag, curWIFISID);
                            } else {
                                setTextStyle(R.drawable.ic_device_signal_wifi_yellow,
                                        R.color._ff9800, yjhzTag, curWIFISID);
                            }
                        }
                    });
                }
                findSDPath();//获取sd卡路径，网络变化后重新更新下
            }
        });
    }

    /**
     * 显示二维码图片
     *
     * @param imageview
     */
    private void saveQRCodeUtils(final ImageView imageview, String webCode) {
        if (WIFI_PWD != null && curWIFISID != null && wifiManager != null) {
            PushServer.start(accpectMessageHander);//开始tcp协议
            final String qrCode = "http://"
                    + CommonUitl.getLocalIpAddress(wifiManager,
                    GSessionActivity.this) + "/dl?ref=box&" + "&ssid="
                    + curWIFISID + "&pwd=" + WIFI_PWD + "&cid=" + MAC
                    + "&resoures=" + heiziSdInfo.getText() + "&ishot="
                    + isHot + "&web=" + webCode.replace("=", "");//二维码的图片信息
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        imageview.setImageBitmap(QRCodeUtils.createQRCode(qrCode, 150));//设置二维码
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    /**
     * 检查是否有SD卡,且资源是否正确 小班资源名 SmallClass 中班资源名 MiddleClass 大班资源名 LargeClass
     * 大大资源名 HighClass
     */
    private void findSDPath() {
        runOnUiThread(new Runnable() {
            @SuppressLint("ResourceAsColor")
            public void run() {
                if (sdpaths != null && sdpaths.size() > 0) {
                    hasSDCard = true;
                    for (int i = 0; i < sdpaths.size(); i++) {//从获取sd卡路径中来判断所有的
                        if (!CommonUitl.isHasResource(gSessionActivity,//路径中是否有资源来
                                sdpaths.get(i))) {//切断sd卡的路径
                            GlobeVariable.File_Path = "";
                        } else {
                            Log.e("tag", "::::::::::" + GlobeVariable.File_Path);
                            GlobeVariable.File_Path = sdpaths.get(i);
                            break;
                        }
                    }
                } else {
                    hasSDCard = false;
                }
                ChechSd();//检查sd卡
            }
        });
    }

    void ChechSd() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (!hasSDCard) {// 获取sd卡里面的资源
                    setTextStyle(R.drawable.ic_hardware_sim_card_no,
                            R.color._f41100, heiziSdInfo, "请插入SD卡");
                } else {//根据卡的路径来更新界面
                    String string = CommonUitl.checkSdResource(
                            GSessionActivity.this, sdpaths);
                    if (string.equals("没有资源文件")) {
                        setTextStyle(R.drawable.ic_hardware_sim_card_no,
                                R.color._f41100, heiziSdInfo, string);
                    } else {
                        setTextStyle(R.drawable.ic_hardware_sim_card_has,
                                R.color._ffffff, heiziSdInfo, string);
                    }
                }
            }
        });
        if (broadCastUdp != null)//启动广播
            broadCastUdp.setMessageData(MAC + ":::" + heiziSdInfo.getText());
    }

    //设置颜色值
    public static void setTextStyle(final int drawable, final int color,
                                    final TextView view, final String text) {
        gSessionActivity.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    if (!text.equals(""))
                        view.setText(text);
                    CommonUitl.setTextFontColor(view,
                            gSessionActivity.getResources(), color);
                    CommonUitl.setArroundDrawable(gSessionActivity, view, drawable,
                            -1, -1, -1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startListen();//sd监听
        try {
            setData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mWebsocketControlCenter == null)//websocket广播
            mWebsocketControlCenter = new WebsocketControlCenter(30001, accpectMessageHander);
    }

    /**
     * 播放
     *
     * @param url  播放地址
     * @param type 1视频 2ppt 3图片
     */
    public void Play(String url, int type) {//从发送命令那边调用，至于为啥写在这我也不知道
        SharedPreferencesUtil.save(this, GlobeVariable.GRADE,
                url.split(":::")[1]);//保存命令过来的年级，从而从文件分年级去找
        if (GlobeVariable.File_Path.equals("")
                || GlobeVariable.File_Path == null || !hasSDCard)//如果卡路径为空。重新获取下
            findSDPath();
        if (!hasSDCard) {//没有插入sd卡不允许进入
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(GSessionActivity.this, "请插入SD卡",
                            Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        if (GlobeVariable.File_Path.equals("")
                || GlobeVariable.File_Path == null)// 如果为空的话，重新获取
            GlobeVariable.File_Path = CommonUitl.getSdPath(this, sdpaths);
        if ((GlobeVariable.File_Path.equals("")//这里判断了这么多路径为空，当初为啥加，好像是有bug才加的
                || GlobeVariable.File_Path == null) && url.split(":::").length < 3) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GSessionActivity.this, "没有找到该资源",
                            Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        Intent intent = null;
        switch (type) {//根据文件类别，来启动
            case 1:
                intent = new Intent(GSessionActivity.this, VideoActivity.class);
                break;
            case 2:
                intent = new Intent(GSessionActivity.this, PptActivity.class);
                break;
            case 3:
                intent = new Intent(GSessionActivity.this, ImageViewActivity.class);
                break;
        }
        if (intent != null) {
            intent.putExtra(GlobeVariable.PLAY_NAME, url.split(":::")[0]);
            if (url.split(":::").length >= 3)
                intent.putExtra(GlobeVariable.PLAY_URL, url.split(":::")[2]);
            startActivity(intent);
        }
    }

    /**
     * 移除SD卡调用
     */
    public void outputSdCard() {
        hasSDCard = false;
        gSessionActivity.runOnUiThread(new Runnable() {
            @SuppressLint("ResourceAsColor")
            public void run() {
                setTextStyle(R.drawable.ic_hardware_sim_card_no,
                        R.color._f41100, heiziSdInfo, "请插入SD卡");
                if (broadCastUdp != null)
                    broadCastUdp.setMessageData(MAC + ":::"
                            + heiziSdInfo.getText());
            }
        });
    }

    /**
     * 插入Sd卡调用
     */
    public void inputSdCard() {
        try {
            findSDPath();
            ChechSd();
        } catch (Exception e) {
            writeErrorLog.writeErrorLogger(e);
        }
    }

    /**
     * 监听SD卡的拔插
     */
    public void startListen() {//监听sd卡广播只能放到activity
        IntentFilter intentFilter = new IntentFilter(
                Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.setPriority(1000);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addDataScheme("file");
        registerReceiver(broadcastRec, intentFilter);
    }

    //监听sd广播，不过没啥用，移除能监听，但是插入因为系统原因不能读取到卡的路径，会显示没有资源
    private final BroadcastReceiver broadcastRec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {//插入广播
                inputSdCard();
            } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {//移除广播
                outputSdCard();
            }
            saveQRCodeUtils(imageview1, "");
        }
    };

    /**
     * 连接到公用的wifi
     */
    public void ConnectWifi(String SSID, String PS, String TYPE) {//在接收数据时调用
        connectWifiNumber = 0;
        yjhzAppication.setwifiPs(PS);
        yjhzAppication.setwifiTp(Integer.parseInt(TYPE));
        yjhzAppication.setwifiName(SSID);
        isHot = false;
        handler.sendEmptyMessageDelayed(GlobeVariable.connectWifi, 1000);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        switch (i) {
            case R.id.all_session:
                loadData();
                break;
            case R.id.kinect_session:
                yjhzAppication.mHttpClient.newCall(HttpUtils.get(HttpRequestUrl.GET_KINECTSESSION_URL,
                        yjhzAppication.session)).enqueue(this);
                break;
        }
    }
}
