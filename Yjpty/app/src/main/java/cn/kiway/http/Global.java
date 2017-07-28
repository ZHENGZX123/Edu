package cn.kiway.http;

import java.util.HashMap;
import java.util.Map;

public class Global {

	public static final int LINES = 1;
	// 应用信息
	public static Map<String, Map<String, Object>> apps = new HashMap<String, Map<String, Object>>();
	public static String u;

	public static void initApp() { // 初始化应用数据
		try {
			Map<String, Object> yjpt = new HashMap<String, Object>();
			Global.apps.put("yjpty", yjpt); // 幼教平台
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
