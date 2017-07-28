package cn.kiway.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.kiway.App;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.data.ACache;
import cn.kiway.dialog.LoginDialog;
import cn.kiway.dialog.NewVersionDialog;
import cn.kiway.dialog.NewVersionDialog.NewVersionCallBack;
import cn.kiway.http.BaseHttpConnectPool;
import cn.kiway.http.BaseHttpHandler;
import cn.kiway.http.HttpHandler;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.ClassModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.SharedPreferencesUtil;

public class BaseActivity extends FragmentActivity implements HttpHandler,
        OnClickListener, NewVersionCallBack {
    /**
     * 应有程序
     */
    public App app;
    /**
     * 加载图像
     */
    public ImageLoader imageLoader;
    /**
     * 图像加载配置参数
     */
    public DisplayImageOptions options, fadeOptions;
    /**
     * 上下文
     */
    protected Context context;
    /**
     * activity界面传输数据
     */
    protected Bundle bundle;
    /**
     * 是否被回收
     */
    protected boolean isRecycle = false;
    /**
     * 屏幕显示信息
     */
    public DisplayMetrics displayMetrics = new DisplayMetrics();
    /**
     * 请求回调
     */
    protected BaseHttpHandler activityHandler = new BaseHttpHandler(this) {
    };
    /**
     * 资源加载
     */
    public Resources resources;
    /**
     * 帧布局管理
     */
    public FragmentManager fragmentManager;
    protected LayoutParams layoutParams = new LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    /**
     * 渐入
     */
    public Animation fadeIn;
    /**
     * 淡出
     */
    public Animation fadeOut;
    /**
     * 缓存
     */
    public ACache mCache;
    public static BaseActivity baseActivityInsantnce;
    /**
     * 是否刷新
     */
    protected boolean isRefresh;
    /**
     * 异常登录dialog
     */
    NewVersionDialog dialog;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        resources = getResources();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .resetViewBeforeLoading(true).cacheInMemory(true)
                .considerExifParams(true).cacheOnDisc(true).build();
        fadeOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .showImageForEmptyUri(R.drawable.ic_action_supervisor_account)
                .showImageOnFail(R.drawable.ic_action_supervisor_account)
                .cacheInMemory(true).considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(500)).cacheInMemory(true)
                .cacheOnDisc(true).build();
        bundle = getIntent().getExtras();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        fragmentManager = getSupportFragmentManager();
        fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        fadeOut = AnimationUtils
                .loadAnimation(context, android.R.anim.fade_out);
        app = AppUtil.getApplication(this);
        app.init();
        mCache = ACache.get(this);
        app.activities.add(this);
        baseActivityInsantnce = this;
        BaseHttpConnectPool.loodingDialog = new LoginDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        View previous = findViewById(R.id.previos);
        if (previous != null)
            previous.setOnClickListener(this);
    }

    /**
     * 初始化视图
     */
    public void initView() throws Exception {
    }

    /**
     * 数据加载
     */
    public void loadData() throws Exception {
    }

    /**
     * 设置数据
     */
    public void setData() throws Exception {
    }

    /**
     * 程序被回收保存数据
     */
    public void saveInstanceState(Bundle outState) {
        outState.putLong("uid", app.getUid());
        // outState.putInt("lessionId", app.getLessionId());
        outState.putString("avatar", app.getAvatar());
        outState.putString("name", app.getName());
        outState.putString("nowWifi", app.getNowWifi());
        outState.putString("sessionId", app.getCookie() + "");
        outState.putSerializable("classModel", app.getClassModel());
    }

    /**
     * 程序重新加载数据
     */
    public void restoreInstanceState(Bundle inState) {
        if (inState.containsKey("uid"))
            app.setUid(inState.getLong("uid"));
        if (inState.containsKey("lessionId"))
            app.setLessionId(inState.getInt("lessionId"));
        if (inState.containsKey("avatar"))
            app.setAvatar(inState.getString("avatar"));
        if (inState.containsKey("name"))
            app.setName(inState.getString("name"));
        if (inState.containsKey("nowWifi"))
            app.setNowWifi(inState.getString("nowWifi"));
        /*
		 * if (inState.containsKey("sessionId")) app.setCookie((Cookie)
		 * inState.getSerializable("sessionId"));
		 */
        if (inState.containsKey("classModel"))
            app.setClassModel((ClassModel) inState
                    .getSerializable("classModel"));
        isRecycle = true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            restoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    /**
     * 启动新的活动界面
     */
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 启动新的活动界面
     */
    public void startActivity(Class<?> cls, Bundle bundle) {
        try {
            Intent intent = new Intent(context, cls);
            if (bundle != null)
                intent.putExtras(bundle);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        app.activities.remove(this);
    }

    public void startForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void httpErr(HttpResponseModel message) throws Exception {
    }

    @Override
    public void httpSuccess(HttpResponseModel message) throws Exception {
        String string = new String(message.getResponse());
        if (message.getUrl().equals(IUrContant.LOGIN_URL)) {
            JSONObject data = new JSONObject(new String(message.getResponse()));
            if (data != null) {
                if (data.optInt("retcode") == 1) {
                    app.setUid(data.optInt("userId"));
                    app.setName(data.optString("userName"));
                    mCache.put(IUrContant.LOGIN_URL, data);
                }
            }
        } else {
            if (!string.subSequence(0, 1).equals("{")
                    && !SharedPreferencesUtil.getString(this,
                    IConstant.USER_NAME).equals("")
                    && !message.getUrl().equals(IUrContant.MEASSGE_URL)) {
                Map<String, String> map = new HashMap<>();
                map.put("userName", SharedPreferencesUtil.getString(this,
                        IConstant.USER_NAME));
                map.put("password", SharedPreferencesUtil.getString(this,
                        IConstant.PASSWORD));
                map.put("type", "1");
                map.put("code", null);
                IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.LOGIN_URL,
                        map, activityHandler, false);
            }
        }
    }

    @Override
    public void HttpError(HttpResponseModel message) throws Exception {
        if (AppUtil.isNetworkAvailable(this)) {
            // ViewUtil.showMessage(this, R.string.no_network);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previos:
                finish();
                break;
        }
    }

    /**
     * 结束所有界面
     */
    public void finishAllAct() {
        if (app != null && app.activities != null) {
            for (Activity activity : app.activities) {
                if (!activity.isFinishing())
                    activity.finish();
            }
        } else {
            finish();
        }
    }

    protected void fullWindowWH() {
        layoutParams = getWindow().getAttributes();
        Rect rect = new Rect();
        View v = getWindow().getDecorView();
        v.getWindowVisibleDisplayFrame(rect);
        layoutParams.width = displayMetrics.widthPixels;
        layoutParams.height = displayMetrics.heightPixels;
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
    }

    /**
     * 隐藏Fragment
     *
     * @param transaction
     * @param fragments
     */
    public void hideFragment(FragmentTransaction transaction,
                             Fragment... fragments) {
        if (transaction == null)
            return;
        if (fragments == null || fragments.length == 0)
            return;
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                transaction.hide(fragment);
        }
    }

    @Override
    protected void onActivityResult(int requstCode, int resultCode, Intent data) {
        super.onActivityResult(requstCode, resultCode, data);
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否进入后台运行
     */
    public static boolean isActive;
    /**
     * 是否为退出账号动作，由于在退出的时候也会跑去登录，所以这里做判断
     */
    public static boolean isExit;

    @Override
    protected void onStop() {
        super.onStop();
        if (!isAppOnForeground()) {
            isActive = false;// 记录当前已经进入后台
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRecycle) {
            try {
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isRecycle = false;
        }
        if (!isActive && !isExit) {
            // app 从后台唤醒，进入前台
            isActive = true;
            if (!SharedPreferencesUtil.getString(this, IConstant.USER_NAME)
                    .equals("")
                    && !SharedPreferencesUtil.getString(this,
                    IConstant.PASSWORD).equals("")) {
                Map<String, String> map = new HashMap<>();
                map.put("userName", SharedPreferencesUtil.getString(this,
                        IConstant.USER_NAME));
                map.put("password", SharedPreferencesUtil.getString(this,
                        IConstant.PASSWORD));
                map.put("type", "1");
                map.put("code", null);
                IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.LOGIN_URL,
                        map, activityHandler, false);
            }
        }
    }

    /**
     * 账号异常登录
     */
    public void showDialog() {
        dialog = new NewVersionDialog(this, this);
        dialog.setTitle("你的账号在其他设备上登录");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void newVersionCallBack() throws Exception {
        AppUtil.ExitLoading(this);
        app.getNioSocketConnector();
    }

    @Override
    public void newVersionOkCallBack() throws Exception {

    }


}
