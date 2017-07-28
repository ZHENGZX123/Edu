package cn.kiway.activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ProgressDialog;
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
import cn.kiway.App;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.data.ACache;
import cn.kiway.dialog.LoginDialog;
import cn.kiway.dialog.NewVersionDialog;
import cn.kiway.dialog.NewVersionDialog.NewVersionCallBack;
import cn.kiway.http.BaseHttpConnectPool;
import cn.kiway.http.BaseHttpHandler;
import cn.kiway.http.HttpHandler;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.BoyModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.SharedPreferencesUtil;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class BaseActivity extends FragmentActivity implements HttpHandler,
		OnClickListener, NewVersionCallBack {
	/**
	 * 应有程序
	 * */
	public App app;
	/**
	 * 加载图像
	 * */
	public ImageLoader imageLoader;
	/**
	 * 图像加载配置参数
	 */
	public DisplayImageOptions options, fadeOptions;
	/**
	 * 上下文
	 * */
	protected Context context;
	/**
	 * activity界面传输数据
	 * */
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
	 * */
	public Resources resources;
	/**
	 * 帧布局管理
	 * */
	public FragmentManager fragmentManager;
	protected LayoutParams layoutParams = new LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	/**
	 * 渐入
	 * */
	public Animation fadeIn;
	/**
	 * 淡出
	 * */
	public Animation fadeOut;
	/**
	 * 缓存
	 * */
	public ACache mCache;
	/**
	 * 刷新
	 * */
	protected boolean isRefresh;
	public static BaseActivity baseActivityInsantnce;
	/**
	 * 异常登录dialog
	 * */
	NewVersionDialog dialog;
	/**
	 * 应用是否在活动
	 * */
	public static boolean isActive;
	/**
	 * 应用是否退出
	 * */
	public static boolean isExit;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		resources = getResources();
		app = AppUtil.getApplication(this);
		app.init();
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
		app.activities.add(this);
		mCache = ACache.get(this);
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
	 * 数据加载
	 */
	public void loadData() throws Exception {
	}

	/**
	 * 程序被回收保存数据
	 */
	public void saveInstanceState(Bundle outState) {
		outState.putLong("uid", app.getUid());
		outState.putSerializable("boymodel", (Serializable) app.getBoyModels());
	}

	/**
	 * 程序重新加载数据
	 */
	@SuppressWarnings("unchecked")
	public void restoreInstanceState(Bundle inState) {
		if (inState.containsKey("uid"))
			app.setUid(inState.getLong("uid"));
		if (inState.containsKey("boymodel"))
			app.setBoyList((List<BoyModel>) inState.getSerializable("boymodel"));
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
				map.put("type", "2");
				map.put("code", null);
				IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.LOGIN_URL,
						map, activityHandler, false);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public void startForResult(Intent intent, int requestCode) {
		startActivityForResult(intent, requestCode);
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		String string = new String(message.getResponse());
		if (message.getUrl().equals(IUrContant.LOGIN_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null) {
				if (data.optInt("retcode") == 1) {
					app.setUid(data.optInt("userId"));
					mCache.put(IUrContant.LOGIN_URL, data);
				}
			}
		} else {
			if (string.length()>0&&!string.subSequence(0, 1).equals("{")
					&& !SharedPreferencesUtil.getString(this,
							IConstant.USER_NAME).equals("")
					&& !SharedPreferencesUtil.getString(this,
							IConstant.PASSWORD).equals("")
					&& !message.getUrl().equals(IUrContant.LOGIN_URL)
					&& !message.getUrl().equals(IUrContant.MEASSGE_URL)) {
				Map<String, String> map = new HashMap<>();
				map.put("userName", SharedPreferencesUtil.getString(this,
						IConstant.USER_NAME));
				map.put("password", SharedPreferencesUtil.getString(this,
						IConstant.PASSWORD));
				map.put("type", "2");
				map.put("code", null);
				IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.LOGIN_URL,
						map, activityHandler);
			}
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
	 * */
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
	 * 结束所有界面
	 * */
	public void finishAllAct() {
		if (app != null && app.activities != null) {
			for (Activity activity : app.activities) {
				if (!activity.isFinishing())
					activity.finish();
			}
		} else
			finish();
	}

	@Override
	public void httpErr(HttpResponseModel message) throws Exception {
	}

	@Override
	public void HttpError(HttpResponseModel message) throws Exception {
		// if (AppUtil.isNetworkAvailable(this))
		// ViewUtil.showMessage(this, R.string.no_network);
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

	@Override
	protected void onStop() {
		super.onStop();
		if (!isAppOnForeground())
			isActive = false;// 记录当前已经进入后台
	}

	/**
	 * 账号异常登录
	 * */
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
