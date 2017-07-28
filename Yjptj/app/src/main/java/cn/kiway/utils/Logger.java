package cn.kiway.utils;

import android.util.Log;

/**
 * 日志的打印
 * 
 * @author Zao
 * */
public class Logger {
	public static  String TAG = "cn.kiway.yjptj";
	public static final boolean log = true;

	public static void log(Object msg) {
		if (msg == null)
			return;
		if (log) {
			Log.v(TAG, msg.toString());
		}
	}
}
