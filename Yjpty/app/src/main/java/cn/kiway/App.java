package cn.kiway;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.cookie.Cookie;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;
import cn.kiway.activity.BaseActivity;
import cn.kiway.common.LocalImageHelper;
import cn.kiway.http.Global;
import cn.kiway.message.MinaClientHandler;
import cn.kiway.model.ClassModel;
import cn.kiway.utils.Logger;
import cn.kiway.utils.UnCeHandler;
import cn.kiway.utils.WifiAdmin;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * 应用程序
 */
public class App extends Application {
	/**
	 * 用户Uid
	 * */
	public long Uid;
	/**
	 * 班级孩子数
	 * */
	public int lessionId;
	/**
	 * 用户头像
	 * */
	public String avatar;
	/**
	 * 用户名字
	 * */
	public String name;
	/**
	 * 当前网络名字
	 * */
	public String nowWifi;
	/**
	 * 是否初始化
	 * */
	boolean isInit;
	/**
	 * session
	 * */
	public Cookie cookie;
	/**
	 * 班级
	 * */
	public ClassModel classModel;
	/**
	 * 正在跟谁聊天
	 * */
	public long intMsgId;
	/**
	 * 上课tcp的ip地址
	 * */
	public String SessionIp;
	private static App app = null;
	private Display display;
	/**
	 * 开启的界面视图集合
	 * */
	public ArrayList<BaseActivity> activities = new ArrayList<BaseActivity>();
	/**
	 * 发送消息的send
	 * */
	public IoSession ioSession;
	/**
	 * 是否在上课
	 * */
	public boolean isOnClass;
	/**
	 * 聊天连接初始
	 * */
	public NioSocketConnector connector;

	/**
	 * 聊天是否连接的上
	 * */
	public boolean isConnect;
	public WifiAdmin admin;
	@Override
	public void onCreate() {
		super.onCreate();
		Logger.TAG = getPackageName();
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
				this);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(
				config.build());
		getNioSocketConnector();
		app = this;
		if (display == null) {
			WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			display = windowManager.getDefaultDisplay();
		}
		// 本地图片辅助类初始化
		LocalImageHelper.init(this);
		Global.initApp();
		admin = new WifiAdmin(this.getApplicationContext());
		
	}

	public static App getInstance() {
		return app;
	}

	public String getCachePath() {
		File cacheDir;
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = getExternalCacheDir();
		else
			cacheDir = getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
		
		return cacheDir.getAbsolutePath();
	}

	/**
	 * @return
	 * @Description： 获取当前屏幕1/4宽度
	 */
	@SuppressWarnings("deprecation")
	public int getQuarterWidth() {
		return display.getWidth() / 4;
	}

	/**
	 * 结束所有界面
	 * */
	public void finishAllAct() {
		if (activities != null) {
			for (Activity activity : activities) {
				if (!activity.isFinishing())
					activity.finish();
			}
		}
	}

	public void setUid(long Uid) {
		this.Uid = Uid;
	}

	public long getUid() {
		return this.Uid;
	}

	public void setLessionId(int lessionId) {
		this.lessionId = lessionId;
	}

	public int getLessionId() {
		return this.lessionId;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getAvatar() {
		return avatar;
	}

	public boolean isInit() {
		return isInit;
	}

	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}

	public void setNowWifi(String nowWifi) {
		this.nowWifi = nowWifi;
	}

	public String getNowWifi() {
		return this.nowWifi;
	}

	public void setClassModel(ClassModel classModel) {
		this.classModel = classModel;
	}

	public ClassModel getClassModel() {
		return this.classModel;
	}

	public void setCookie(Cookie cookie) {
		this.cookie = cookie;
	}

	public Cookie getCookie() {
		return this.cookie;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setIntMsgId(long msgid) {
		this.intMsgId = msgid;
	}

	public long getIntMsgId() {
		return this.intMsgId;
	}

	public void setSessionIp(String SessionIp) {
		this.SessionIp = SessionIp;
	}

	public String getSessionIp() {
		return this.SessionIp;
	}

	public void setIoSession(IoSession IoSession) {
		this.ioSession = IoSession;
	}

	public IoSession getIoSession() {
		return this.ioSession;
	}

	public void setIsOnClass(boolean isOnClass) {
		this.isOnClass = isOnClass;
	}

	public boolean getIsOnClass() {
		return this.isOnClass;
	}

	public void setIsConnect(boolean isConnect) {
		this.isConnect = isConnect;
	}

	public boolean getIsConnect() {
		return this.isConnect;
	}

	public void getNioSocketConnector() {
		// Create TCP/IP connection
		connector = new NioSocketConnector();
		// 创建接受数据的过滤器
		DefaultIoFilterChainBuilder chain = connector.getFilterChain();
		// 设定这个过滤器将一行一行(/r/n)的读取数据
		chain.addLast("myChin", new ProtocolCodecFilter(
				new ObjectSerializationCodecFactory()));
		// 客户端的消息处理器：一个SamplMinaServerHander对象
		connector.setHandler(new MinaClientHandler());
		// set connect timeout
		connector.setConnectTimeoutMillis(30 * 1000L);
		connector.setConnectTimeoutCheckInterval(60 * 10 * 1000L);
	}
	public void init() {
		// 设置该CrashHandler为程序的默认处理器
		UnCeHandler catchExcep = new UnCeHandler(this);
		Thread.setDefaultUncaughtExceptionHandler(catchExcep);
	}
}
