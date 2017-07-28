package cn.kiway.http;

import android.util.Log;

public class Logger {
	public static String TAG = "cn.kiway.yjptj";
	public static final boolean log = true;

	public static void log(Object msg) {
		if (msg == null)
			return;
		if (log) {
			Log.e(TAG, msg.toString());
		}
	}
}
