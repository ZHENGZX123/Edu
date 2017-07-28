package cn.kiway.yjhz.utils;

public class GlobeVariable {
    /**
     * 播放视频
     */
    public static final int PLAY_VIDEO = 1;
    /**
     * 增加声音
     */
    public static final int ADD_VOLUME = 2;
    /**
     * 减少声音
     */
    public static final int DECREASE_VOLUME = 3;
    /**
     * 停止播放
     */
    public static final int STOP_PLAY_VIDEO = 4;
    /**
     * 调节屏幕亮
     */
    public static final int BRIGHT = 5;
    /**
     * 调节屏幕暗
     */
    public static final int DRAK = 6;
    /**
     * 上一个
     */
    public static final int THE_LAST = 7;
    /**
     * 下一个
     */
    public static final int THE_NEXT = 8;
    /**
     * 快进
     */
    public static final int ADD_STEP = 9;
    /**
     * 后退
     */
    public static final int DECREASE_STEP = 10;
    /**
     * 班级消息
     */
    public static final int CLASSCONTENT = 11;
    /**
     * 用户信息
     */
    public static final int USERINFO = 12;
    /**
     * 初始化屏幕
     */
    public static final int INITIAL_SCREEN = 13;
    /**
     * 暂停
     */
    public static final int PAUSE_PLAY_VIDEO = 14;
    /**
     * 继续
     */
    public static final int RESTART_PLAY_VIDEO = 15;
    /**
     * 播放ppt
     */
    public static final int PLAY_PPT = 16;
    /**
     * ppt下一页
     */
    public static final int PPT_NEXT = 17;
    /**
     * ppt上一页
     */
    public static final int PPT_LASR = 18;
    /**
     * wifi信息
     */
    public static final int GET_WIFI_INFO = 19;
    /**
     * 播放图片
     */
    public static final int PLAY_IMG = 20;
    /**
     * 启动应用
     */
    public static final int STRAT_APK = 21;
    /**
     * 结束应用
     */
    public static final int KILL_APK = 22;
    /**
     * 模拟按键
     */
    public static final int ACTION_KEY = 23;
    /**
     * 模拟鼠标
     */
    public static final int MotionEvent = 24;
    /**
     * 课程目录
     *    public static final String KWHZ_COURSE_PATH = "turui_hezi/course";
     *
     */
    public static final String KWHZ_COURSE_PATH = "kiway_hezi/course";

    /**
     * 自定义课程目录
     *     public static final String KWHZ_USERSEESION_PATH = "turui_hezi/usersession";
     */

    public static final String KWHZ_USERSEESION_PATH = "kiway_hezi/usersession";
    /**
     * 下载ppt的目录
     */
    public static final String KWHZ_PPT_PATH = "kiway_hezi";
    /**
     * 下载ppt的目录
     */
    public static final String KWHZ_PPT_CPATH = "ppt";
    /**
     * 下载APK的目录
     */
    public static final String KWHZ_APK_CPATH = "apk";
    /**
     * apk下载地址
     */
    public static final String DOWNLOAD_APK = "http://www.yuertong.com/yjpts/static/app/Yjhz.apk";
    /**
     * 检查更新
     */
    public static final String CHECK_APK = "http://www.yuertong.com/yjpts/app/version";
    /**
     * 获取广告资源的路径
     */
    public static final String ADV_URL = "http://www.yuertong.com/yjpt/slidePic/getByType";
    /**
     * 统计路径
     */
    public static final String STATISTICSMODEL_URL = "http://www.yuertong.com/yjpt/slidePic/getByType";
    /**
     * wifi信息
     */
    public static final String WIFI_INFO = "wifi_info";
    /**
     * 携带的参数
     */
    public static final String PLAY_NAME = "play_name";
    /**
     * 携带的dizhi
     */
    public static final String PLAY_URL = "play_url";
    /**
     * 绑定的年级
     */
    public static final String GRADE = "grade";
    /**
     * 当前获取的的资源路径
     */
    public static String File_Path = "";
    /**
     * 版本更新
     */
    public final static int newVersion = 0;
    /**
     * 更新时间
     */
    public final static int update_time = 1;
    /**
     * 连接wifi
     */
    public final static int connectWifi = 2;
    /**
     * 获取资源路径
     */
    public final static int findFile = 3;
    /**
     * 轮播图
     */
    public final static int viewPager = 4;
    /**
     * 下载apk更新图
     */
    public final static int downLoadApk = 5;
    /**
     * apk下载乘车
     */
    public final static int downLoadApkSuccess = 6;
    /**
     * apk下载乘车
     */
    public final static int downLoadApkFial = 7;
    /**
     * dialog关闭
     */
    public final static int closeDialog = 8;
    /**
     * 信息采集的缓存
     */
    public final static String statisticsModel = "statisticsModel";
    /**
     * 启动推送服务
     */
    public static final int ACTION_START = 0x1000001;
    /**
     * 停止推送服务
     */
    public static final int ACTION_STOP = ACTION_START + 1;
    /**
     * 接收到《发送心跳包》推送服务命令
     */
    public static final int ACTION_KEEPALIVE = ACTION_STOP + 1;
    /**
     * 接收到《重启》推送服务命令
     */
    public static final int ACTION_RECONNECT = ACTION_KEEPALIVE + 1;

    // Action to start 启动
    public static final String ACTION_START_CONN = "MQTT_CONN_SERVICE_START";
    // Action to stop 停止
    public static final String ACTION_STOP_CONN = "MQTT_CONN_SERVICE_STOP";
    //
    public static final String ACTION_KEEPALIVE_CONN = "MQTT_CONN_SERVICE_KEEPALIVE";
    // Action to reconnect 重新连接
    public static final String ACTION_RECONNECT_CONN = "MQTT_CONN_SERVICE_RECONNECT";

    public static final int MQTT_SUBSCRIBE = 0x11000001;
    public static final int MQTT_CONNECTION_SUCC = MQTT_SUBSCRIBE + 1;
    public static final int MQTT_CONNECTION_FAIL = MQTT_CONNECTION_SUCC + 1;
    public static final int MQTT_CONNECTION_STOP = MQTT_CONNECTION_FAIL + 1;
    public static final int HPROSE_INIT_SUCC = MQTT_CONNECTION_STOP + 1;
    public static final int MQTT_SEND_MESSAGE = HPROSE_INIT_SUCC + 1;
    public static final int MQTT_SEND_MESSAGE_SUCC = MQTT_SEND_MESSAGE + 1;
    public static final int MQTT_SEND_MESSAGE_LOOP = MQTT_SEND_MESSAGE_SUCC + 1;
    public static final int HPROSE_REGISTER_TOKER = MQTT_SEND_MESSAGE_LOOP + 1;
    public static final int HPROSE_REGISTER_TOKER_ERROR = HPROSE_REGISTER_TOKER + 1;
    public static final int HPROSE_REGISTER_TOKER_SUCC = HPROSE_REGISTER_TOKER_ERROR + 1;
    public static final int HPROSE_REGISTER_TOKER_REGIST = HPROSE_REGISTER_TOKER_SUCC + 1;
    public static final int GET_LIST_INSTALLED_APKS = HPROSE_REGISTER_TOKER_REGIST + 1;
    public static final int GET_LIST_WHITE_APKS = GET_LIST_INSTALLED_APKS + 1;
    public static final int GET_LIST_BLACK_APKS = GET_LIST_WHITE_APKS + 1;
    public static final int GET_LIST_WHITE_WIFIS = GET_LIST_BLACK_APKS + 1;
    public static final int GET_LIST_BLACK_WIFIS = GET_LIST_WHITE_WIFIS + 1;
    public static final int UPDATE_LIST_INSTALLED_APKS = GET_LIST_BLACK_WIFIS + 1;
    public static final int UPDATE_LIST_WHITE_APKS = UPDATE_LIST_INSTALLED_APKS + 1;
    public static final int UPDATE_LIST_BLACK_APKS = UPDATE_LIST_WHITE_APKS + 1;
    public static final int UPDATE_LIST_WHITE_WIFIS = UPDATE_LIST_BLACK_APKS + 1;
    public static final int UPDATE_LIST_BLACK_WIFIS = UPDATE_LIST_WHITE_WIFIS + 1;
    public static final int DELETE_LIST_INSTALLED_APKS = UPDATE_LIST_BLACK_WIFIS + 1;
    public static final int DELETE_LIST_WHITE_APKS = DELETE_LIST_INSTALLED_APKS + 1;
    public static final int DELETE_LIST_BLACK_APKS = DELETE_LIST_WHITE_APKS + 1;
    public static final int DELETE_LIST_WHITE_WIFIS = DELETE_LIST_BLACK_APKS + 1;
    public static final int DELETE_LIST_BLACK_WIFIS = DELETE_LIST_WHITE_WIFIS + 1;
    public static final int REMOTE_INSTALL_APK = DELETE_LIST_BLACK_WIFIS + 1;
    public static final int REMOTE_UNINSTALL_APK = REMOTE_INSTALL_APK + 1;
    public static final int REMOTE_DOWNLOAD_FILES = REMOTE_UNINSTALL_APK + 1;
    public static final int REMOTE_DELETE_FILES = REMOTE_DOWNLOAD_FILES + 1;
    public static final int REMOTE_REMOVE_PASSWD = REMOTE_DELETE_FILES + 1;
    public static final int REMOTE_GET_LOCATION = REMOTE_REMOVE_PASSWD + 1;
    public static final int REMOTE_RESTART_FACTORY = REMOTE_GET_LOCATION + 1;
    public static final int SHOW_WEBSOCKET_MESSAGE = REMOTE_RESTART_FACTORY + 1;
}
