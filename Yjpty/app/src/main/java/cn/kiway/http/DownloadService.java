package cn.kiway.http;

import java.io.File;

import cn.kiway.IConstant;
import cn.kiway.activity.BaseActivity;
import cn.kiway.utils.AppUtil;

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

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class DownloadService extends Service {
	private DownloadManager dm;
	@SuppressWarnings("unused")
	private long enqueue;
	private BroadcastReceiver receiver;
	String apkUrl;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		apkUrl = intent.getStringExtra(IConstant.BUNDLE_PARAMS);
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(
						Uri.fromFile(new File(Environment
								.getExternalStorageDirectory()
								+ "/download/Yjpty.apk")),
						"application/vnd.android.package-archive");
				startActivity(intent);
				BaseActivity.baseActivityInsantnce.finishAllAct();
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
			AppUtil.deleteFiles(Environment.getExternalStorageDirectory()
					+ "/download/Yjptj.apk", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(apkUrl));
		request.setMimeType("application/vnd.android.package-archive");
		request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, "Yjpty.apk");
		enqueue = dm.enqueue(request);
	}
}