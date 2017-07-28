package cn.kiway.yjhz.utils.okhttp;


/**
 * Created by Administrator on 2017/6/28.
 */

public class HttpRequestUrl {


    //public static String BASE_URL = "http://192.168.8.6:8180/yjpts/";// 内网地址
   public static String BASE_URL = "http://www.yuertong.com/yjpts/";// 内网地址
    /**
     * 登录地址  post  userName  password type
     */
    public static final String LOGIN_URL = BASE_URL + "app/login";
    /**
     * 获取我的班级列表 get
     */
    public static final String GET_MY_CLASS_LIST = BASE_URL + "app/class";

    /**
     * 获取全部课程 get
     */
    public static final String GET_ALL_SESSION = BASE_URL + "app/course?classId=";
    /**
     * 获取我的课程
     */
    public static final String GET_MY_SESSION = BASE_URL + "app/course/my_section?classId=";
    /**
     * 获取某个课程的里面课时数据 get
     */
    public static final String GET_ONE_SESSION = BASE_URL + "app/course/{courseId}/section?classId=";
    /**
     * 获取某个课时的数据 get
     */
    public static final String GET_ONE_COURSE = BASE_URL + "app/course/section/{sectionId}?classId=";
    /**
     * 获取体感课程列表
     */
    public static final String GET_KINECTSESSION_URL = BASE_URL + "app/feelingCourse";
    /**
     * 获取基础数据 get
     */
    public static final String GET_CUSE_BASE = BASE_URL + "app/base";
    /**
     * 退出登录 get
     */
    public static final String LOGOUT_URL = BASE_URL + "app/logout";

    //请求数据返回的状态嘛
    public class StatusCode {
        public static final int _200 = 200;//请求成功
        public static final int _300 = 300;
        public static final int _400 = 400;
        public static final int _500 = 500;
    }

    //url地址中请求需要替换的字符串，有新加入的地址也要加到这里
    public class ReplaceParam {
        public static final String albumn = "{albumn}";//请求成功
    }

    //返回的Json数据常用key
    public class JsonData {
        public final static String StatusCode = "StatusCode";
        public final static String Jsondata = "data";
    }
}
