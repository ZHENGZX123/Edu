package cn.kiway.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.kiway.App;
import cn.kiway.IConstant;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.Logger;

public class WifiReceiver extends BroadcastReceiver {
	App app;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (app == null)
			app = (App) context.getApplicationContext();
		if (app.isInit()) {
			if (AppUtil.isNetworkAvailable(context)) {
				if ((app.getIoSession() == null || app.getIoSession()
						.isClosing()) && !app.getIsConnect()) {
					IConstant.executorService.execute(new Runnable() {
						public void run() {
							try {
								Logger.log("失效了，再创建一个");
								MinaClientHandler.openMessage(app);
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					});
				} else {
					Logger.log("有效，不创建");
				}
			} else {
				if (app.getIoSession() != null) {
					if (app.getIoSession().isActive()) {
						app.getIoSession().closeNow();
						Logger.log("断网了，关闭");
					}
				}
			}
			app.setInit(true);
		}
	}
}
