package cn.kiway.yjhz.download;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;
import cn.kiway.yjhz.utils.CommonUitl;
import cn.kiway.yjhz.utils.GlobeVariable;
import cn.kiway.yjhz.utils.Logger;
import cn.kiway.yjhz.utils.SilentInstall;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class DownloadService extends Service {
	private DownloadManager dm;
	@SuppressWarnings("unused")
	private long enqueue;
	private BroadcastReceiver receiver;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						SilentInstall installHelper = new SilentInstall();
						final boolean result = installHelper
								.install(Environment
										.getExternalStorageDirectory()
										+ "/download/Yjhz.apk");
						if (result) {
							Toast.makeText(getApplicationContext(), "安装成功",
									Toast.LENGTH_SHORT).show();
						} else {
							Logger.log("安装失败");
						}
					};
				}).start();
				stopSelf();
			}
		};
		registerReceiver(receiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		startDownload();
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@SuppressLint("NewApi")
	private void startDownload() {
		try {
			CommonUitl.deleteFiles(Environment.getExternalStorageDirectory()
					+ "/download/Yjhz.apk", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(GlobeVariable.DOWNLOAD_APK));
		request.setMimeType("application/vnd.android.package-archive");
		request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, "Yjhz.apk");
		enqueue = dm.enqueue(request);
	}
}