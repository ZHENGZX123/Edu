package cn.kiway.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import cn.kiway.App;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.login.LoadingActivity;
import cn.kiway.message.model.MessageChatProvider;
import cn.kiway.message.model.MessageProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import eu.janmuller.android.simplecropimage.CropImage;

/**
 * 应用工具集
 * 
 * @author Zao
 */
public class AppUtil {
	public static App getApplication(Context context) {
		return (App) context.getApplicationContext();

	}

	/**
	 * 验证是否在wifi环境下
	 * */
	public static boolean isWifiActive(Context icontext) {
		Context context = icontext.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info;
		if (connectivity != null) {
			info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getTypeName().equals("WIFI")
							&& info[i].isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * 获取网咯的 加密方式
	 * */
	public static int getCipherType(Context context, String ssid) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> list = wifiManager.getScanResults();
		for (ScanResult scResult : list) {
			if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
				String capabilities = scResult.capabilities;
				Log.i("hefeng", "capabilities=" + capabilities);
				if (!TextUtils.isEmpty(capabilities)) {
					if (capabilities.contains("WPA")
							|| capabilities.contains("wpa")) {
						Log.i("hefeng", "wpa");
						return 3;
					} else if (capabilities.contains("WEP")
							|| capabilities.contains("wep")) {
						Log.i("hefeng", "wep");
						return 2;
					} else {
						Log.i("hefeng", "no");
						return 1;
					}
				}
			}
		}
		return 0;
	}
	public static DisplayImageOptions getOptions(boolean reset) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.resetViewBeforeLoading(reset).cacheInMemory(true)
				.considerExifParams(true).build();
		return options;
	}

	public static String getWifiServerIp(Context context) {
		try {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int i = wifiInfo.getIpAddress();
			StringBuilder sb = new StringBuilder();
			sb.append(i & 0xFF).append(".");
			sb.append((i >> 8) & 0xFF).append(".");
			sb.append((i >> 16) & 0xFF).append(".");
			sb.append((i >> 24) & 0xFF);
			return sb.toString();
		} catch (Exception ex) {
			return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
		}
	}

	/**
	 * 获取imei
	 * */
	public static String getImei(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	/**
	 * 获取当前网络名字
	 * */
	public static String getConnectWifiSsid(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (wifiInfo.getSSID() == null)
			return "";
		return wifiInfo.getSSID().replace("\"", "");// 手机适配//在魅族手机获取的名字带有双引号，这边去除
	}

	/**
	 * 获取当前网络id
	 * */
	public static int getConnectWifiNetid(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getNetworkId();
	}

	/**
	 * 获取当前网络ip地址
	 * 
	 * @param context
	 * @return
	 */
	public String getLocalIpAddress(Context context) {
		try {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int i = wifiInfo.getIpAddress();
			StringBuilder sb = new StringBuilder();
			sb.append(i & 0xFF).append(".");
			sb.append((i >> 8) & 0xFF).append(".");
			sb.append((i >> 16) & 0xFF).append(".");
			sb.append((i >> 24) & 0xFF);
			return sb.toString();
		} catch (Exception ex) {
			return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
		}
	}

	/**
	 * 网络是否可用
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	/**
	 * 调用图像截图
	 */
	public static void cropImage(Uri picUri, Uri outUri, int requestCode,
			Activity activity) {
		try {
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			cropIntent.setDataAndType(picUri, "image/*");
			cropIntent.putExtra("crop", "true");
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			cropIntent.putExtra("outputX", 500);
			cropIntent.putExtra("outputY", 500);
			cropIntent.putExtra("return-data", false);
			cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
			activity.startActivityForResult(cropIntent, requestCode);
		} catch (ActivityNotFoundException anfe) {
			anfe.printStackTrace();
		}
	}

	/**
	 * 文件转化为byte[]
	 */
	public static byte[] fileToBytes(File file) {
		if (file == null || file.isDirectory() || !file.exists())
			return null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] b = new byte[fis.available()];
			fis.read(b);
			return b;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
		}
		return null;
	}

	/**
	 * 创建软件的私有文件夹
	 */
	public static void createFloder() {

		File file = new File(Environment.getExternalStorageDirectory(),
				IConstant.ZWHD_ROOT);
		if (!file.exists())
			file.mkdirs();
		File f = new File(file, IConstant.CAMERA_PHOTO_FLODER);
		if (!f.exists())
			f.mkdirs();
		f = new File(file, IConstant.DOWNLOAD_PHOTO_FLODER);
		if (!f.exists())
			f.mkdirs();
		f = new File(file, IConstant.RECORDER_AUDIO_FLODER);
		if (!f.exists())
			f.mkdirs();
		f = new File(file, IConstant.DOWNLOAD_FILES);
		if (!f.exists())
			f.mkdirs();
		f = new File(file, IConstant.RECORDER_VIDEO_FLODER);
		if (!f.exists())
			f.mkdirs();
		f = new File(file, IConstant.DOWNLOAD_VIDEO_FLODER);
		if (!f.exists())
			f.mkdirs();
	}

	/**
	 * 获取缓存大小
	 * */
	public static String getTotalCacheSize(Context context) throws Exception {
		long cacheSize = getFolderSize(context.getCacheDir());
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + IConstant.ZWHD_ROOT);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			cacheSize += getFolderSize(file);
		}
		return getFormatSize(cacheSize);
	}

	// 获取文件
	// Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/
	// 目录，一般放一些长时间保存的数据
	// Context.getExternalCacheDir() -->
	// SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
	public static long getFolderSize(File file) throws Exception {
		long size = 0;
		try {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				// 如果下面还有文件
				if (fileList[i].isDirectory()) {
					size = size + getFolderSize(fileList[i]);
				} else {
					size = size + fileList[i].length();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	/**
	 * 格式化单位
	 * 
	 * @param size
	 * @return
	 */
	public static String getFormatSize(double size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
			return "0KB";
		}
		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "KB";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "MB";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
				+ "TB";
	}

	/**
	 * 清空我们的文件
	 */
	public static void deleteFiles(String filePath, boolean isDeleteFloder)
			throws Exception {
		File file = new File(filePath);
		if (!file.exists())
			return;
		if (file.isFile()) {
			file.delete();
		} else {
			File[] files = file.listFiles();
			for (File f : files) {
				deleteFiles(f.getAbsolutePath(), isDeleteFloder);
			}
			if (isDeleteFloder)
				file.delete();
		}
	}

	/**
	 * 删除更新下载目录文件
	 */

	public static String deleteDownload() {
		try {
			createFloder();
			File file = new File(Environment.getExternalStorageDirectory(),
					IConstant.ZWHD_ROOT);
			file = new File(file, IConstant.DOWNLOAD_FILES);
			File[] files = file.listFiles();
			for (File f : files) {
				if (!f.isDirectory())
					f.delete();
			}
			return file.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 创建新的相片
	 */
	public static String createNewPhoto() {
		try {
			createFloder();
			File file = new File(Environment.getExternalStorageDirectory(),
					IConstant.ZWHD_ROOT);
			file = new File(file, IConstant.CAMERA_PHOTO_FLODER);
			file = new File(file, System.currentTimeMillis() + ".png");
			return file.getAbsolutePath();
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 下载相片
	 */
	public static String downloadPhoto(Object object) {
		try {
			createFloder();
			File file = new File(Environment.getExternalStorageDirectory(),
					IConstant.ZWHD_ROOT);
			file = new File(file, IConstant.DOWNLOAD_PHOTO_FLODER);
			file = new File(file, object.toString());
			return file.getAbsolutePath();
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 获取截取的图像数据
	 */
	public static Bitmap getBitmapForCrop(Intent data, String picturePath) {
		Bitmap bitmap = null;
		try {
			if (data != null) {
				Bundle extras = data.getExtras();
				if (extras != null)
					bitmap = extras.getParcelable("data");
			}
			if (bitmap == null) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(picturePath);
					bitmap = BitmapFactory.decodeStream(fis);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 文件能转化为二进制数组
	 */
	public static byte[] fileToBytes(String path) {
		return fileToBytes(new File(path));
	}

	/**
	 * 检测文件是否有内容
	 */
	public final static boolean fileHasValue(String path) {
		if (StringUtil.isEmpty(path))
			return false;
		File file = new File(path);
		if (!file.exists())
			return false;
		if (file.length() <= 0)
			return false;
		return true;
	}

	/**
	 * 文件下载
	 */
	public static void downloadFile(File f, String path) {
		if (!f.exists()) {
			HttpURLConnection con = null;
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			BufferedInputStream bis = null;
			try {
				URL url = new URL(path);
				con = (HttpURLConnection) url.openConnection();
				con.setConnectTimeout(60 * 1000);
				con.setReadTimeout(60 * 1000);
				if (con != null
						&& con.getResponseCode() == HttpURLConnection.HTTP_OK) {
					bis = new BufferedInputStream(con.getInputStream());
					fos = new FileOutputStream(f);
					bos = new BufferedOutputStream(fos);
					byte[] b = new byte[1024];
					int length;
					while ((length = bis.read(b)) != -1) {
						bos.write(b, 0, length);
						bos.flush();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (bos != null)
						bos.close();
					if (fos != null)
						fos.close();
					if (bis != null)
						bis.close();
					if (con != null)
						con.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 截图数据
	 * */
	public static void performCrop(String picUri, String outUri,
			int requestCode, BaseActivity activity, BaseFragment fragment) {
		try {
			Intent intent = new Intent(activity, CropImage.class);
			intent.putExtra(CropImage.IMAGE_PATH, picUri);
			intent.putExtra(CropImage.SCALE, true);
			intent.putExtra(CropImage.ASPECT_X, 1);
			intent.putExtra(CropImage.ASPECT_Y, 1);
			intent.putExtra(CropImage.OUT_IMAGE_PATH, outUri);
			fragment.startActivityForResult(intent, requestCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public static String getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return context.getString(R.string.can_not_find_version_name);
		}
	}

	/**
	 * 文本转表情符
	 * */
	public static String strToHtml(String str, String[] facesKey) {
		if (StringUtil.isEmpty(str))
			return str;
		List<String> list = new ArrayList<String>();
		Pattern pattern = Pattern.compile("\\[[\u4E00-\u9FA5]*\\]"); // 中文版本
		// Pattern pattern = Pattern.compile("\\[[a-zA-Z]*\\]"); //英文版本
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			String s = matcher.group();
			if ((!list.contains(s)) && (Arrays.asList(facesKey).contains(s))) {
				list.add(s);
				str = str.replace(s, "<img src='" + s + "'>");
			}
		}
		return str;
	}

	static Map<String, BitmapDrawable> faceIcons = new HashMap<String, BitmapDrawable>();

	/**
	 * 获取表情资源
	 * */
	@SuppressWarnings("deprecation")
	public static Drawable loadFaceResourse(AssetManager assetManager,
			Context context, String str, String[] facesKey, String[] faces)
			throws Exception {
		int position = -1;
		for (int i = 0, leg = facesKey.length; i < leg; i++) {
			if (facesKey[i].equals(str))
				position = i;
		}
		if (position == -1)
			return null;
		if (faceIcons.containsKey(str)) {
			return faceIcons.get(str);
		} else {
			String ss = "faces/" + faces[position];
			Bitmap bitmap = BitmapFactory.decodeStream(assetManager.openFd(ss)
					.createInputStream());
			BitmapDrawable drawable = new BitmapDrawable(bitmap);
			faceIcons.put(str, drawable);
			return drawable;
		}
	}

	/**
	 * 生成二维码 要转换的地址或字符串,可以是中文
	 * 
	 * @param url
	 *            地址
	 * @param width
	 *            生成的宽度
	 * @param height
	 *            生成的高度
	 * @return
	 */
	public static Bitmap createQRImage(String url, final int width,
			final int height) {
		try {
			// 判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1) {
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, width, height, hints);
			int[] pixels = new int[width * height];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					} else {
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取图片文件名
	 * 
	 * @param pathandname
	 *            文件路径
	 * */
	public static String getFileName(String pathandname) {
		int start = pathandname.lastIndexOf("/");
		if (start != -1) {
			return pathandname.substring(start + 1);
		} else {
			return null;
		}
	}

	/**
	 * 手机震动
	 * 
	 * @param context
	 *            上下文
	 * @param milliseconds
	 *            震动时长
	 * */
	public static void Vibrate(Context context, long milliseconds) {
		Vibrator vib = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}

	/**
	 * 退出登录
	 * */
	public static void ExitLoading(BaseActivity activity) {
		activity.mCache.clear();
		activity.getContentResolver().delete(
				Uri.parse(MessageProvider.MESSAGES_URL),
				"_msgtype=? or _msgtype=? or _msgtype=?",
				new String[] { "1", "2", "3" });
		activity.getContentResolver().delete(
				Uri.parse(MessageChatProvider.MESSAGECHATS_URL),
				"_msgtype=? or _msgtype=? or _msgtype=?",
				new String[] { "1", "2", "3" });
		SharedPreferencesUtil.save(activity, IConstant.USER_NAME, "");
		SharedPreferencesUtil.save(activity, IConstant.PASSWORD, "");
		activity.finishAllAct();
		BaseActivity.isExit = true;
		activity.startActivity(LoadingActivity.class);// 退出
	}
}
