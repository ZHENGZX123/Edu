package cn.kiway;

public class IUrContant {
	//public static String BASE_URL = "http://192.168.8.6:8180/yjpt";// 内网地址
	 public static String BASE_URL = "http://www.yuertong.com/yjpt";// 测试地址
	// public static String BASE_URL = "http://sp.sz-edu.net/yjpttest";// 测试地址
	/**
	 * 聊天地址
	 * */
	public static String CHAT_URL = "www.yuertong.com";
	/**
	 * 登录地址
	 * */
	public static final String LOGIN_URL = BASE_URL + "/login/teacher";
	/**
	 * 班级信息地址
	 * */
	public static final String CALSS_URL = BASE_URL
			+ "/yjpclass/classMemberInfo";
	/**
	 * 获取视频列表地址
	 * */
	public static final String VIDEO_URL = BASE_URL + "/course/get";
	/**
	 * 课程库分类列表地址
	 * */
	public static final String SESSION_DB_URL = BASE_URL
			+ "/lessonDir/searchDir";
	/**
	 * 忘记密码
	 * */
	public static final String FORGETPASSWORD_URL = BASE_URL + "/user/uppw";
	/**
	 * 修改密码
	 * */
	public static final String CHANGE_PASSWORD_URL = BASE_URL
			+ "/user/uppwLogin";
	/**
	 * 课程库分类列表具体数据地址
	 * */
	public static final String SESSION_DB_LIST_URL = BASE_URL
			+ "/lesson/searchLessonByDir";
	/**
	 * 课节列表数据
	 * */
	public static final String SESSION_LIST_URL = BASE_URL
			+ "/lesson/getSection";
	/**
	 * 上课列表地址
	 * */
	public static final String SESSION_PLAN_URL = BASE_URL + "/coursePlan.jsp";
	/**
	 * 获取短信验证码
	 * */
	public static final String VALIDATE_URL = BASE_URL + "/getValidateMessage";
	/**
	 * 获取图片验证码
	 * */
	public static final String MEASSGE_URL = BASE_URL + "/code";
	/**
	 * 注册
	 * */
	public static final String REGIGER_URL = BASE_URL + "/registApp";
	/**
	 * 在校情况
	 * */
	public static final String IN_SCHOOL_INFO_URL = BASE_URL
			+ "/performance/check";
	/**
	 * 修改在校情况其中的某一个
	 * */
	public static final String IN_SCHOOL_INFO_ITEM_URL = BASE_URL
			+ "/performance/checkUp";
	/**
	 * 获取班级同学
	 * */
	public static final String GET_CLASS_URL = BASE_URL
			+ "/performance/childList";
	/**
	 * 获取班级情况的历史情况
	 * */
	public static final String GET_SCHOOL_INFO_HISSTRONG_URL = BASE_URL
			+ "/performance/childSPHistory";
	/**
	 * 创建班级
	 * */
	public static final String CREAT_CLASSS_URL = BASE_URL
			+ "/yjpclass/createClass";
	/**
	 * 获取学校列表
	 * */
	public static final String GET_SCHOOL_LIST_URL = BASE_URL
			+ "/school/searchByAddr";
	/**
	 * 获取班级列表
	 * */
	public static final String GET_CLASS_LIST_URL = BASE_URL
			+ "/yjpclass/searchClass";
	/**
	 * 新增班级宝贝
	 * */
	public static final String ADD_CLASS_BABY_URL = BASE_URL
			+ "/classChildren/create";
	/**
	 * 加入班级
	 * */
	public static final String JOIN_CLASS_URL = BASE_URL
			+ "/yjpclass/QRCClass/teacher";
	/**
	 * 绑定盒子
	 * */
	public static final String BANG_DING_HE_ZI_URL = BASE_URL
			+ "/yjpclass/bindHCode";
	/**
	 * 修改用户信息
	 * */
	public static final String UPDATEUSERINFO_URL = BASE_URL
			+ "/userInfo/updateUserInfo";
	/**
	 * 获取我的用户信息
	 * */
	public static final String GET_MY_INFO_URL = BASE_URL + "/userInfo/myInfo";
	/**
	 * 更新宝贝信息
	 * */
	public static final String UPDATE_BABY_INFO_URL = BASE_URL
			+ "/classChildren/updateChild";
	/**
	 * 删除宝贝
	 * */
	public static final String DELETE_BABY_URL = BASE_URL
			+ "/classChildren/delete";
	/***
	 * 获取宝贝信息
	 * */
	public static final String GET_BABY_URL = BASE_URL
			+ "/classChildren/search";
	/**
	 * 发送邮件
	 * */
	public static final String SENG_EMAIL_URL = BASE_URL + "/mail/send";
	/**
	 * 邮件预览
	 * */
	public static final String SEE_EMAIL_URL = BASE_URL + "/letter.jsp?no=";
	/**
	 * 删除家长
	 * */
	public static final String DELTET_PARENT_URL = BASE_URL
			+ "/yjpclass/deleteClassParent";
	/**
	 * 退出班级
	 * */
	public static final String EXIT_CLASS_URL = BASE_URL
			+ "/yjpclass/deleteClassTeacher";
	/**
	 * 创建班级圈动态
	 * */
	public static final String CREATE_CLASS_RING_URL = BASE_URL
			+ "/classGroup/create";
	/**
	 * 查找朋友圈动态
	 * */
	public static final String SREACH_CLASS_RING_URL = BASE_URL
			+ "/classGroup/search";
	/**
	 * 赞朋友圈动态
	 * */
	public static final String PRASE_CLASS_RING_URL = BASE_URL
			+ "/classGroup/praise";
	/**
	 * 评论朋友圈动态
	 * */
	public static final String COMMENT_CLASS_RING_URL = BASE_URL
			+ "/classGroup/reply";
	/**
	 * 删除朋友圈动态
	 * */
	public static final String DELETE_CLASS_RING_URL = BASE_URL
			+ "/classGroup/delete";
	/**
	 * 获取成长足迹的列表地址
	 * */
	public static final String GET_GROWTH_LIST_URL = BASE_URL
			+ "/growth/getCGrowthList";
	/**
	 * 获取某个孩子的成长足迹
	 * */
	public static final String GET_CHILDE_GROWTH_URL = BASE_URL
			+ "/growth/getPGrowthList";
	/**
	 * 学校新闻地址
	 * */
	public static final String GET_SCHOOL_WEB_URL = BASE_URL
			+ "/tjsp/news/js_list.jsp";
	/**
	 * 聊天地址
	 * */
	public static final String MESSAGECHAT_URL = BASE_URL + "/websocket";
	/**
	 * 获取 班级联系人
	 * */
	public static final String GET_CLASS_PEOPLE_URL = BASE_URL
			+ "/userInfo/classInfo";
	/**
	 * 创建私信
	 * */
	public static final String CREATE_MESSAGE_URL = BASE_URL
			+ "/discuss/createPriMess";
	/**
	 * 创建作业
	 * **/
	public static final String CREATE_HOMEWORK_URL = BASE_URL
			+ "/homework/create";
	/**
	 * 创建通知
	 * */
	public static final String CREATE_NOTIFY_URL = BASE_URL + "/notice/create";
	/**
	 * 获取讨论组信息
	 * */
	public static final String GET_TAO_LUN_ZU_URL = BASE_URL
			+ "/discuss/getDisInfo";
	/**
	 * 获取讨论组成员
	 * */
	public static final String GET_TAO_LUN_ZU_CY_URL = BASE_URL
			+ "/discuss/getDisUserList";
	/**
	 * 获取班级群信息
	 * */
	public static final String GET_CLASS_NUMBER_URL = BASE_URL
			+ "/userInfo/getClassDisInfo";
	/**
	 * 修改群名片
	 * */
	public static final String EDIT_QMP_URL = BASE_URL
			+ "/discuss/updateDisName";
	/**
	 * 修改班级讨论组名字
	 * */
	public static final String EDIT_CLASS_QMP_URL = BASE_URL
			+ "/discuss/upClassDisName";
	/**
	 * 老师apk的下载地址
	 * */
	public static final String DOWNLOAD_APK_URL = BASE_URL
			+ "/download/Yjpty.apk";
	/**
	 * 检查更新
	 * */
	public static final String CHECK_VERSION_URL = BASE_URL
			+ "/version/getVersion";
	/**
	 * 服务协议地址
	 * */
	public static final String SERIVCE_AGREE_URL = BASE_URL
			+ "/serviceAgreement.html";
	/**
	 * 修改班级名字
	 * */
	public static final String UPDATE_CLASS_INFO = BASE_URL
			+ "/yjpclass/updateClass";
	/**
	 * 退出登录
	 * */
	public static final String LOGOT_OUT = BASE_URL + "/logout/app";
	/**
	 * 扫描二维码登录
	 * */
	public static final String CONFIRMLOGIN_URL = BASE_URL
			+ "/scan/confirmLogin";
	/**
	 * 扫描二维码
	 * */
	public static final String SCAN_URL = BASE_URL + "/scan/bindUser";
	/**
	 * 二维码取消登录
	 * */
	public static final String SCAN_CANCLE_URL = BASE_URL + "/scan/cancelLogin";
	/**
	 * 请求课程全部数据
	 * */
	public static final String GET_ALL_SESSION_URL = BASE_URL
			+ "/course/getAll";
	/**
	 * 获取某个学校全部班级
	 * */
	public static final String GET_ALL_CLASS_URL = BASE_URL
			+ "/yjpclass/class/all";
	/**
	 * 批量加入班级
	 * */
	public static final String JOINS_CLASS_URL = BASE_URL
			+ "/yjpclass/joinclass/teacher";
}
