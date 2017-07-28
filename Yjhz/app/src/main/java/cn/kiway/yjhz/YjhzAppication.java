package cn.kiway.yjhz;

import android.app.Activity;
import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class YjhzAppication extends Application {

	private String wifiName;
	private String wifiPs;
	public  String session="";
	private int wifiTp;
	ArrayList<Activity> list = new ArrayList<Activity>();
	//public OkHttpClient mHttpClient;
	public OkHttpClient mHttpClient = new OkHttpClient();
//	.Builder()
//			 .cookieJar(new CookieJar() {
//				 private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
//
//				 @Override
//				 public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//					 cookieStore.put(url, cookies);
//				 }
//
//				 @Override
//				 public List<Cookie> loadForRequest(HttpUrl url) {
//					 List<Cookie> cookies = cookieStore.get(url);
//					 return cookies != null ? cookies : new ArrayList<Cookie>();
//				 }
//			 }).build();

	/*
	 * public static YjhzAppication getInstance(){ if(instance==null){ instance
	 * = new YjhzAppication(); } return instance; }
	 */

	@Override
	public void onCreate() {
		super.onCreate();
//		mHttpClient= new OkHttpClient.Builder()
//				.cookieJar(new CookieJarImpl(new PersistentCookieStore(this)))
//				.build();
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
				this);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(
				config.build());
		// instance = this;
		init();
	}


	public String getWifiName() {
		return this.wifiName;
	}

	public void setwifiName(String wifiName) {
		this.wifiName = wifiName;
	}

	public String getwifiPs() {
		return this.wifiPs;
	}

	public void setwifiPs(String wifiPs) {
		this.wifiPs = wifiPs;
	}

	public void setwifiTp(int tp) {
		this.wifiTp = tp;
	}

	public int getWifiTp() {
		return this.wifiTp;
	}

	public void init() {
//		// 设置该CrashHandler为程序的默认处理器
//		UnCeHandler catchExcep = new UnCeHandler(this);
//	Thread.setDefaultUncaughtExceptionHandler(catchExcep);
	}


	/**
	 * 向Activity列表中添加Activity对象
	 */
	public void addActivity(Activity a) {
		list.add(a);
	}

	/**
	 * 关闭Activity列表中的所有Activity
	 */
	public void finishActivity() {
		for (Activity activity : list) {
			if (null != activity) {
				activity.finish();
			}
		}
		// 杀死该应用进程
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
