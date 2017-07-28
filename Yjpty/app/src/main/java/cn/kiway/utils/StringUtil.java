package cn.kiway.utils;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import cn.kiway.IUrContant;
import cn.kiway.activity.BaseActivity;

/**
 * 字符串操作
 * 
 * @author Zao
 * */
public class StringUtil {
	/**
	 * MD5加密
	 * */
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}

			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 字符串验证
	 * */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);

	}

	/**
	 * 时间戳转换为 **之前
	 * */
	@SuppressLint("SimpleDateFormat")
	public static String getStandardDate(long ctimelong) {
		String r = "";

		Calendar currentTime = Calendar.getInstance();
		long currentTimelong = System.currentTimeMillis();

		Calendar publicCal = Calendar.getInstance();
		publicCal.setTimeInMillis(ctimelong);

		long timeDelta = currentTimelong - ctimelong;

		if (timeDelta <= 0L) {

			r = "刚刚";

		} else if (timeDelta < 60 * 1000L) {

			r = timeDelta / 1000L + "秒前";

		} else if (timeDelta < 60 * 60 * 1000L) {

			r = timeDelta / (60 * 1000L) + "分钟前";

		} else if (timeDelta < 24 * 60 * 60 * 1000L) {

			// if (currentTime.get(Calendar.DAY_OF_YEAR) ==
			// publicCal.get(Calendar.DAY_OF_YEAR)) {
			r = timeDelta / (60 * 60 * 1000L) + "小时前";
			// } else {
			// r = "昨天 " + new SimpleDateFormat("HH:mm").format(ctimelong);
			// }

		} else if (timeDelta < 2 * 24 * 60 * 60 * 1000L) {

			if (currentTime.get(Calendar.DAY_OF_YEAR) == publicCal
					.get(Calendar.DAY_OF_YEAR) + 1) {
				r = "昨天" + new SimpleDateFormat("HH:mm").format(ctimelong);
			} else {
				r = "前天" + new SimpleDateFormat("HH:mm").format(ctimelong);
			}

		} else if (timeDelta < 3 * 24 * 60 * 60 * 1000L) {

			if (currentTime.get(Calendar.DAY_OF_YEAR) == publicCal
					.get(Calendar.DAY_OF_YEAR) + 2) {
				r = "前天" + new SimpleDateFormat("HH:mm").format(ctimelong);
			} else {
				r = new SimpleDateFormat("MM月dd日").format(ctimelong);
			}

		} else {
			r = new SimpleDateFormat("MM月dd日").format(ctimelong);
		}
		return r;
	}

	/**
	 * 数据的格式化
	 * */
	public static String format(String format, Object... strings) {
		return String.format(format, strings);

	}

	public static int toInt(String str) {
		try {
			if (isEmpty(str))
				return 0;
			return Integer.parseInt(str);
		} catch (Exception e) {
			return 0;
		}

	}

	/**
	 * 寻找索引值
	 * */
	public static int findStrIndex(List<String> list, String str) {
		if (str == null)
			return 0;
		int idx = list.indexOf(str);
		return idx == -1 ? 0 : idx;

	}

	public static int findStrIndexs(List<String> list, String str) {
		if (str == null)
			return -1;
		for (int i = 0, leg = list.size(); i < leg; i++) {
			if (list.get(i).equals(str))
				return i;

		}
		return -1;

	}

	/**
	 * 字符串索引
	 * */
	public static int findStrIndexs(String pat, String str) {
		try {
			return pat.indexOf(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static String getPicPath(Uri uri, ContentResolver contentResolver) {
		try {
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = contentResolver.query(uri, projection, null, null,
					null);
			int column_index = cursor.getColumnIndexOrThrow(projection[0]);
			cursor.moveToFirst();
			String str = cursor.getString(column_index);
			cursor.close();
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String[] split(String splitStr, String str) {
		return str.split(splitStr);
	}

	public static String getStrings(String[] strings, int idx) {
		if (strings == null)
			return null;
		if (strings.length - 1 < idx)
			return null;
		return strings[idx];

	}

	public static String toString(List<String> list, String split) {
		StringBuffer buffer = new StringBuffer();
		for (String str : list) {
			buffer.append(str).append(split);
		}
		buffer.delete(buffer.length() - split.length(), buffer.length());
		return buffer.toString();
	}

	public static String toString(String split, String... list) {
		StringBuffer buffer = new StringBuffer();
		for (String str : list) {
			buffer.append(str).append(split);
		}
		buffer.delete(buffer.length() - split.length(), buffer.length());
		return buffer.toString();
	}

	/**
	 * 根据年份以及月份获取天数
	 * 
	 * @param year
	 *            年份
	 * @param month
	 *            月份 1~12
	 * */
	public static ArrayList<String> getDaysByYM(String year, String month) {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, Integer.parseInt(year));
			calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
			int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			ArrayList<String> days = new ArrayList<String>();
			for (int i = 1; i < maxDay + 1; i++) {
				days.add("" + i);

			}
			return days;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String dateFormat(String str, int filed) {

		String s = null;
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.getDefault());
		try {
			if (isNotEmpty(str)) {
				Date date = format.parse(str);
				calendar.setTime(date);
			} else {
				calendar.setTime(new Date());
			}

			switch (filed) {
			case 0:
				s = "" + calendar.get(Calendar.YEAR);
				break;

			case 1:
				s = "" + (1 + calendar.get(Calendar.MONTH));
				break;
			case 2:
				s = "" + calendar.get(Calendar.DAY_OF_MONTH);
				break;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String getDateField(long time, int filed) {

		String s = null;
		Date date = new Date(time);
		SimpleDateFormat sdf;
		try {
			switch (filed) {
			case 0:
				sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 1:
				sdf = new SimpleDateFormat("MM", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 2:
				sdf = new SimpleDateFormat("dd", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 3:
				sdf = new SimpleDateFormat("MM.yyyy", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 4:
				sdf = new SimpleDateFormat("dd-MM HH:mm", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 5:
				sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 6:
				sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 7:
				sdf = new SimpleDateFormat(" HH:mm MM-dd", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 8:
				sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 9:
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
						Locale.getDefault());
				s = sdf.format(date);
				break;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String getAge(String year) {
		Calendar calendar = Calendar.getInstance();
		return ""
				+ Math.abs(calendar.get(Calendar.YEAR) - Integer.parseInt(year));
	}

	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	public static int bytesToInt(byte[] src, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (src[i + offset] & 0x000000FF) << shift;// 往高位游
		}
		return value;
	}

	/**
	 * 判断是否为手机号码
	 * */
	public static boolean isMobileNum(String mobiles) {
		/*
		 * Pattern p = Pattern
		 * .compile("^((13[0-9])|(17[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		 */
		Pattern p = Pattern
				.compile("^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();

	}

	/**
	 * 固定集合转化为活动集合
	 * 
	 * @param <T>
	 * */
	public static <T> List<T> changeList(List<T> list) {
		List<T> lst = new ArrayList<T>();
		for (int i = 0, leg = list.size(); i < leg; i++) {
			lst.add(list.get(i));
		}
		return lst;
	}

	/**
	 * 日期转时分秒
	 * */
	public static String dateToHms() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss",
				Locale.getDefault());
		return sdf.format(new Date());

	}

	/**
	 * 获取视频目录
	 * **/
	public static String getVideoPath(Uri uri, ContentResolver contentResolver) {
		try {
			String[] projection = { MediaStore.Video.Media.DATA };
			Cursor cursor = contentResolver.query(uri, projection, null, null,
					null);
			int column_index = cursor.getColumnIndexOrThrow(projection[0]);
			cursor.moveToFirst();
			String str = cursor.getString(column_index);
			cursor.close();
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断是否为邮箱
	 * */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 拼接图像的Url 由于获取的地址不完全得拼接基础地址
	 * */
	@SuppressWarnings("deprecation")
	public static String imgUrl(BaseActivity activity, String url) {
		String string;
		String s;
		if (url == null)
			return "";
		if (url.equals("null"))
			return "";
		s = IUrContant.BASE_URL + url.replace("\\", "/");
		if (activity.imageLoader.getDiscCache().get(s) == null) {
			string = s;
		} else {
			string = "file://" + activity.imageLoader.getDiscCache().get(s);
		}
		return string;
	}

	/**
	 * 时间转为时间戳
	 * */
	@SuppressLint("SimpleDateFormat")
	public static long stringTimeToLong(String time) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date date = null;
			try {
				date = simpleDateFormat.parse(time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return date.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return System.currentTimeMillis();
	}

	/**
	 * 去除特殊符号
	 * */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
}
