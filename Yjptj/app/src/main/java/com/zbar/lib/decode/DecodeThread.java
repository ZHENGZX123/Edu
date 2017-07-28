package com.zbar.lib.decode;

import java.util.concurrent.CountDownLatch;

import android.os.Handler;
import android.os.Looper;
import cn.kiway.activity.common.MipcaCaptureActivity;

/**
 * 描述: 解码线程
 */
final class DecodeThread extends Thread {

	MipcaCaptureActivity activity;
	private Handler handler;
	private final CountDownLatch handlerInitLatch;

	DecodeThread(MipcaCaptureActivity activity) {
		this.activity = activity;
		handlerInitLatch = new CountDownLatch(1);
	}

	Handler getHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			// continue?
		}
		return handler;
	}

	@Override
	public void run() {
		Looper.prepare();
		handler = new DecodeHandler(activity);
		handlerInitLatch.countDown();
		Looper.loop();
	}

}
