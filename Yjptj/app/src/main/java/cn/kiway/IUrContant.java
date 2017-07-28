package cn.kiway;

public class IUrContant {
    // public static String BASE_URL = "http://192.168.8.19:8080/yjpts";// 内网地址
    public static String BASE_URL = "http://www.yuertong.com/yjpt";// 测试地址
    // public static String BASE_URL = "http://sp.sz-edu.net/yjpttest";// 测试地址
    /**
     * 聊天地址
     */
    public static String CHAT_URL = "www.yuertong.com";

    /**
     * 登录地址
     */
    public static final String LOGIN_URL = BASE_URL + "/login/parent";
    /**
     * 获取短信验证码
     */
    public static final String VALIDATE_URL = BASE_URL + "/getValidateMessage";
    /**
     * 获取图片验证码
     */
    public static final String MEASSGE_URL = BASE_URL + "/code";
    /**
     * 注册
     */
    public static final String REGIGER_URL = BASE_URL + "/registApp";
    /**
     * 忘记密码
     */
    public static final String FORGETPASSWORD_URL = BASE_URL + "/user/uppw";
    /**
     * 修改密码
     */
    public static final String CHANGE_PASSWORD_URL = BASE_URL
            + "/user/uppwLogin";
    /**
     * 获取班级孩子列表
     */
    public static final String GET_CLASS_CHLID_URL = BASE_URL
            + "/classChildren/searchAll";
    /**
     * 加入班级
     */
    public static final String JOIN_CLASS_URL = BASE_URL
            + "/yjpclass/QRCClass/parent";
    /***
     * 获取我的孩子的信息
     */
    public static final String GET_MY_BABY_URL = BASE_URL
            + "/classChildren/search";
    /**
     * 取消关注家长
     */
    public static final String DELTET_PARENT_URL = BASE_URL
            + "/yjpclass/deleteClassParent";
    /**
     * 更新宝贝信息
     */
    public static final String UPDATE_BABY_INFO_URL = BASE_URL
            + "/classChildren/updateChild";
    /**
     * 获取上课列表
     */
    public static final String GET_SESSION_URL = BASE_URL
            + "/lesson/getAttendedLesson";
    /**
     * 获取上课视频列表
     */
    public static final String GET_VIDEO_URL = BASE_URL + "/lesson/getSection";
    /**
     * 修改用户信息
     */
    public static final String UPDATEUSERINFO_URL = BASE_URL
            + "/userInfo/updateUserInfo";
    /**
     * 获取我的用户信息
     */
    public static final String GET_MY_INFO_URL = BASE_URL + "/userInfo/myInfo";
    /**
     * 查找朋友圈动态
     */
    public static final String SREACH_CLASS_RING_URL = BASE_URL
            + "/classGroup/search";
    /**
     * 赞朋友圈动态
     */
    public static final String PRASE_CLASS_RING_URL = BASE_URL
            + "/classGroup/praise";
    /**
     * 评论朋友圈动态
     */
    public static final String COMMENT_CLASS_RING_URL = BASE_URL
            + "/classGroup/reply";
    /**
     * 获取在学情况
     */
    public static final String GET_SHCOOLS_INFO_URL = BASE_URL
            + "/performance/childSP";
    /**
     * 发布成长档案地址
     */
    public static final String SEND_CHILDE_INFO_URL = BASE_URL
            + "/growth/create";
    /**
     * 获取成长足迹的列表地址
     */
    public static final String GET_GROWTH_LIST_URL = BASE_URL
            + "/growth/getGrowthList";
    /**
     * 学校新闻地址
     */
    public static final String GET_SCHOOL_WEB_URL = BASE_URL
            + "/tjsp/news/js_list.jsp";
    /**
     * 获取新闻列表
     */
    public static final String GET_SCHOOL_NEWS = BASE_URL + "/news/jznews";
    /**
     * 获取新闻详情地址
     */
    public static final String GET_SCHOOL_DATIAL_URL = BASE_URL
            + "/news/jsdetails?id=";
    /**
     * 获取 班级联系人
     */
    public static final String GET_CLASS_PEOPLE_URL = BASE_URL
            + "/userInfo/classInfo";
    /**
     * 聊天地址
     */
    public static final String MESSAGECHAT_URL = BASE_URL + "/websocket";
    /**
     * 提交作业
     */
    public static final String REPLY_HOMEWORK_URL = BASE_URL
            + "/homework/reply";
    /**
     * 创建讨论组
     */
    public static final String CREATE_TAO_LU_ZU_UREL = BASE_URL
            + "/discuss/createDis";
    /**
     * 获取讨论组成员
     */
    public static final String GET_TAO_LUN_ZU_CY_URL = BASE_URL
            + "/discuss/getDisUserList";
    /**
     * 获取班级讨论组的人数
     */
    public static final String GET_CLASS_NUMBER_URL = BASE_URL
            + "/userInfo/getClassDisInfo";
    /**
     * 修改群名片
     */
    public static final String EDIT_QMP_URL = BASE_URL
            + "/discuss/updateDisName";
    /**
     * 获取讨论组人数
     */
    public static final String GET_TAO_LUN_ZU_URL = BASE_URL
            + "/discuss/getDisInfo";
    /**
     * 修改班级讨论组名字
     */
    public static final String EDIT_CLASS_QMP_URL = BASE_URL
            + "/discuss/upClassDisName";
    /**
     * 添加讨论组成员
     */
    public static final String ADD_TAO_LUN_ZU_URL = BASE_URL
            + "/discuss/addDisUser";
    /**
     * 获取讨论组未加入的成员
     */
    public static final String GET_TAO_LUN_ZU_CY = BASE_URL
            + "/discuss/getNotAddList";
    /**
     * 家长apk的下载地址
     */
    public static final String DOWNLOAD_APK_URL = BASE_URL
            + "/download/Yjptj.apk";
    /**
     * 检查更新
     */
    public static final String CHECK_VERSION_URL = BASE_URL
            + "/version/getVersion";
    /**
     * 服务协议地址
     */
    public static final String SERIVCE_AGREE_URL = BASE_URL
            + "/serviceAgreement.html";
    /**
     * 退出登录
     */
    public static final String LOGOT_OUT = BASE_URL + "/logout/app";
    /**
     * 获取孩子信息列表
     */
    public static final String GET_CHILD_INFO_URL = BASE_URL
            + "/classChildren/myChilds";
    /**
     * 获取班级列表
     */
    public static final String GET_CLASS_LIST_URL = BASE_URL
            + "/yjpclass/searchClass";
    /**
     * 扫描二维码登录
     */
    public static final String CONFIRMLOGIN_URL = BASE_URL
            + "/scan/confirmLogin";
    /**
     * 扫描二维码
     */
    public static final String SCAN_URL = BASE_URL + "/scan/bindUser";
    /**
     * 二维码取消登录
     */
    public static final String SCAN_CANCLE_URL = BASE_URL + "/scan/cancelLogin";
    /**
     * 接送小孩
     */
    public static final String SCAN_SCHOOL_URL = "http://www.yuertong.com/yjpts/scan/oneSchoolScan";
}
