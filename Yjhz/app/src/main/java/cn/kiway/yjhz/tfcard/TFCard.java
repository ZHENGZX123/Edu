package cn.kiway.yjhz.tfcard;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TFCard {
	/*
	 * avoid initializations of tool classes
	 */
	private TFCard() {
	}

	/**
	 * @Title: getExtSDCardPaths
	 * @Description: to obtain storage paths, the first path is theoretically
	 *               the returned value of
	 *               Environment.getExternalStorageDirectory(), namely the
	 *               primary external storage. It can be the storage of internal
	 *               device, or that of external sdcard. If paths.size() >1,
	 *               basically, the current device contains two type of storage:
	 *               one is the storage of the device itself, one is that of
	 *               external sdcard. Additionally, the paths is directory.
	 * @return List<String>
	 * @throws IOException
	 */
	public static List<String> getExtSDCardPaths() {
		List<String> paths = new ArrayList<String>();
		String extFileStatus = Environment.getExternalStorageState();
		File extFile = Environment.getExternalStorageDirectory();
		Log.i("获得外部存储器TF卡", Environment.getExternalStorageState()
				+ "--华丽分割线---" + Environment.MEDIA_UNMOUNTED);
		if (extFileStatus.endsWith(Environment.MEDIA_UNMOUNTED)
				&& extFile.exists() && extFile.isDirectory()
				&& extFile.canWrite()) {
			paths.add(extFile.getAbsolutePath());
		}
		try {
			// obtain executed result of command line code of 'mount', to judge
			// whether tfCard exists by the result

			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec("mount");
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			int mountPathIndex = 1;
			while ((line = br.readLine()) != null) {

				// format of sdcard file system: vfat/fuse
				@SuppressWarnings("unused")
				boolean hasFlag = false;

				if ((!line.contains("fat") && !line.contains("fuse") && !line
						.contains("storage"))
						|| line.contains("secure")
						|| line.contains("asec")
						|| line.contains("firmware")
						|| line.contains("shell")
						|| line.contains("obb")
						|| line.contains("legacy") || line.contains("data")) {

					continue;
				}
				String[] parts = line.split(" ");

				int length = parts.length;

				if (mountPathIndex >= length) {
					continue;
				}
				String mountPath = parts[mountPathIndex];
				Log.i("Stringparts", mountPath + "：++：");
				if (!mountPath.contains("/") || mountPath.contains("data")
						|| mountPath.contains("Data")) {
					continue;
				}
				Log.i("Stringparts+++", mountPath + "：++：");
				/*
				 * File mountRoot = new File(mountPath); Log.i("TAG",
				 * !mountRoot.exists() + "" + !mountRoot.isDirectory() + "" +
				 * !mountRoot.canWrite()); if (!mountRoot.exists() ||
				 * !mountRoot.isDirectory() || !mountRoot.canWrite()) {
				 * continue; } boolean equalsToPrimarySD =
				 * mountPath.equals(extFile .getAbsolutePath());
				 * 
				 * Log.i("tag", equalsToPrimarySD + ""); if (equalsToPrimarySD)
				 * { continue; }
				 */
				paths.add(mountPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.i("sssss", paths.toString());
		return paths;
	}

	/**
	 * 获取sd卡的大小
	 * */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@SuppressLint("NewApi")
	public static String readSDCard(String file) {
		if (file == null || file.equals("") || !new File(file).exists())
			return "0G";
		StatFs sf = new StatFs(file);
		long blockSize = sf.getBlockSizeLong();
		long blockCount = sf.getBlockCountLong();
		long availCount = sf.getAvailableBlocksLong();
		DecimalFormat df = new DecimalFormat();
		df.setGroupingSize(3);// 每3位分为一组
		// 总容量
		String totalSize = (blockSize * blockCount) / 1024 >= 1024 ? df
				.format((((blockSize * blockCount) / 1024) / 1024) / 1024)
				+ "G" : df.format(((blockSize * blockCount) / 1024) / 1024)
				+ "MB";
		// 未使用量
		String avalilable = (blockSize * availCount) / 1024 >= 1024 ? df
				.format((((blockSize * availCount) / 1024) / 1024) / 1024)
				+ "G" : df.format(((blockSize * availCount) / 1024) / 1024)
				+ "MB";
		return totalSize + "      " + avalilable;
	}
	/**
	 * 获取sd卡的总大小
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@SuppressLint("NewApi")
	public static String readTotalSDCard(String file) {
		if (file == null || file.equals("") || !new File(file).exists())
			return "0G";
		StatFs sf = new StatFs(file);
		long blockSize = sf.getBlockSizeLong();
		long blockCount = sf.getBlockCountLong();
		DecimalFormat df = new DecimalFormat();
		df.setGroupingSize(3);// 每3位分为一组
		// 总容量
		String totalSize = (blockSize * blockCount) / 1024 >= 1024 ? df
				.format((((blockSize * blockCount) / 1024) / 1024) / 1024)
				+ "G" : df.format(((blockSize * blockCount) / 1024) / 1024)
				+ "MB";
		return totalSize;
	}

	/**
	 * 获取sd卡未使用容量
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@SuppressLint("NewApi")
	public static String readAvailableSDCard(String file) {

		if (file == null || file.equals("") || !new File(file).exists())
			return "0G";
		StatFs sf = new StatFs(file);
		long blockSize = sf.getBlockSizeLong();
		long availCount = sf.getAvailableBlocksLong();
		DecimalFormat df = new DecimalFormat();
		df.setGroupingSize(3);// 每3位分为一组
		// 未使用量
		String avalilable = (blockSize * availCount) / 1024 >= 1024 ? df
				.format((((blockSize * availCount) / 1024) / 1024) / 1024)
				+ "G" : df.format(((blockSize * availCount) / 1024) / 1024)
				+ "MB";

		return avalilable;
	}

	//data
	public static String getDataDirectory() {
		File file = Environment.getDataDirectory();
		return file.getPath();
	}

	//root
	public static String getRootDirectory() {
		File file = Environment.getRootDirectory();
		return file.getPath();
	}

	//extrnal
	public static String getExternalStorageDirectory() {
		String path = Environment.getExternalStorageState();
		File file = Environment.getExternalStorageDirectory();

		return path.endsWith(Environment.MEDIA_MOUNTED) ? file.getPath() : null;
	}

	//ccache
	public static String getDownloadCacheDirectory() {
		File file = Environment.getDownloadCacheDirectory();
		return file.getPath();
	}


	public static ArrayList<String> getAllSdPaths(Context context) {
		Method mMethodGetPaths = null;
		String[] paths = null;
		//通过调用类的实例mStorageManager的getClass()获取StorageManager类对应的Class对象
		//getMethod("getVolumePaths")返回StorageManager类对应的Class对象的getVolumePaths方法，这里不带参数
		StorageManager mStorageManager = (StorageManager) context
				.getSystemService(context.STORAGE_SERVICE);//storage
		try {
			mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths");
			paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<String> arrayList = new ArrayList<>();
		for (String s : paths) {
			File file = new File(s);
			if (s != null && !s.equals("") && file.exists() && file.canWrite() && file.canRead()) {
				arrayList.add(s);
			}
		}
		return arrayList;
	}

	public static String getSecondaryStorageDirectory(Context context) {
		ArrayList<String> arrayList = getAllSdPaths(context);
		if (arrayList.size() > 0 ) {
			for (String s : arrayList) {
				String exPath = getExternalStorageDirectory();
				if (exPath != null && (!s.equals(exPath)) && s.contains("external")) {
					return s;
				}
			}
		}
		return null;
	}
}
