package cn.kiway.utils;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
	private static final String SP_NAME = "cn.kiway.yuptte";

	public static void save(Context context, String key, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(key, value).apply();
		sharedPreferences.edit().commit();
	}

	public static void save(Context context, String key, boolean value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean(key, value).apply();
		sharedPreferences.edit().commit();
	}

	public static void save(Context context, String key, float value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().putFloat(key, value).apply();
		sharedPreferences.edit().commit();
	}

	public static void save(Context context, String key, int value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().putInt(key, value).apply();
		sharedPreferences.edit().commit();
	}

	public static void save(Context context, String key, long value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().putLong(key, value).apply();
		sharedPreferences.edit().commit();
	}

	public static void save(Context context, String key, Set<String> value) {
		/*SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().putStringSet(key, value).apply();*/
	}

	public static String getString(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		if (sharedPreferences.contains(key))
			return sharedPreferences.getString(key, "");
		else
			return "";
	}

	public static boolean getBoolean(Context context, String key) {
	
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		if (sharedPreferences.contains(key))
			return sharedPreferences.getBoolean(key, false);
		else
			return false;
	}

	public static float getFloat(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		if (sharedPreferences.contains(key))
			return sharedPreferences.getFloat(key, 0f);
		else
			return 0f;
	}

	public static int getInteger(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		if (sharedPreferences.contains(key))
			return sharedPreferences.getInt(key, -1);
		else
			return 0;
	}

	public static long getLong(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		if (sharedPreferences.contains(key))
			return sharedPreferences.getLong(key, 0);
		else
			return 0;
	}

	
}
