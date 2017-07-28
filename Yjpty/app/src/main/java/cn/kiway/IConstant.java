package cn.kiway;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.kiway.http.BaseHttpConnectPool;

/**
 * 常用数据定义
 */
public interface IConstant {
	/**
	 * 请求连接池
	 */
	public BaseHttpConnectPool HTTP_CONNECT_POOL = BaseHttpConnectPool
			.getInstance();
	/**
	 * 线程管理
	 */
	public ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
	/**
	 * 参数传递
	 */
	public String BUNDLE_PARAMS = "bundle_params";
	public String BUNDLE_PARAMS1 = "bundle_params1";
	public String BUNDLE_PARAMS2 = "bundle_params2";
	public String BUNDLE_PARAMS3 = "bundle_params3";
	/**
	 * 图像的上传尺寸
	 */
	public int UPLOAD_PHOTO_SIZE = 500;
	/**
	 * 调用拍照返回码
	 */
	public int FOR_CAMERA = 1001;
	/**
	 * 调用相册返回码
	 */
	public int FOR_PHOTO = 1002;
	/**
	 * 调用截图返回码
	 */
	public int FOR_CROP = 1003;
	/**
	 * 一般的数据返回
	 */
	public int FOR_BUNDLE = 1004;
	/**
	 * 图像尺寸最大大小
	 * */
	public String MAX_SIZE = "max_size";

	/**
	 * 缓存线程
	 * */
	public final ExecutorService executorService = Executors
			.newCachedThreadPool();
	/**
	 * 加载条数
	 * */
	public int MAX_COUNT = 20;
	/*---------------------------------------文件相关------------------------------------------------*/
	/**
	 * 缓存文件
	 */
	public String ZWHD_ROOT = "Yjpty";
	/**
	 * 录制的音频文件目录
	 */
	public final String RECORDER_AUDIO_FLODER = "recorder_audios";
	/**
	 * 下载
	 */
	public final String DOWNLOAD_FILES = "download_files";
	/**
	 * 拍摄的照片目录
	 */
	public final String CAMERA_PHOTO_FLODER = "photos";
	/**
	 * 下载的照片目录
	 */
	public final String DOWNLOAD_PHOTO_FLODER = "download_photos";
	/**
	 * 录制的视频目录
	 */
	public final String RECORDER_VIDEO_FLODER = "recorder_videos";
	/**
	 * 下载的视频目录
	 */
	public final String DOWNLOAD_VIDEO_FLODER = "download_videos";
	/*---------------------------------------文件相关------------------------------------------------*/
	/**
	 * 是否开启午休
	 * **/
	public final String AFTERE_ON = "after_on";
	public static final String LOCAL_FOLDER_NAME = "local_folder_name";// 跳转到相册页的文件夹名称
	/** 请求相机 */
	public static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 1001;
	/** 请求裁剪 */
	public static final int REQUEST_CODE_GETIMAGE_BYCROP = 2;
	/**
	 * 是否为新版本
	 * */
	public static final String NEW_VERSION = "new_version";
	/**
	 * 省选择
	 * */
	public static final String PROVINCE_NUMBER = "province_number";
	/**
	 * 城市选择
	 * */
	public static final String CITY_NUMBER = "city_number";
	/**
	 * 区选择
	 * */
	public static final String AREA_NUMBER = "area_number";
	/**
	 * 是否在wifi 环境下
	 * */
	public static final String WIFI = "wifi";
	/**
	 * 用户名
	 * */
	public static final String USER_NAME = "user_name";
	/**
	 * 验证码
	 * */
	public static final String PASSWORD = "password";
	/**
	 * 选择的班级
	 * */
	public static final String CLASS_NAME = "class_name";
	/**
	 * wifi名字
	 * */
	public static final String WIFI_NEME = "wifi_name";
	/**
	 * wifi密码
	 * */
	public static final String WIFI_PASSWORD = "wifi_password";
	/**
	 * 默认评价
	 * */
	public static final String PING_JIA = "pingjia";
	/**
	 * 默认选择的班级
	 * */
	public static final String CHANGE_CLASS = "change_class";
	/**
	 * 消息是否提醒
	 * */
	public static final String MESSAGE_NOTIFY = "message_notify";
	/**
	 * 是否在上课
	 * */
	public static final String IS_ON_CLASS = "is_onclass";
	
}
