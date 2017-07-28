package cn.kiway.message.model;

import android.database.ContentObserver;
import android.os.Handler;

public class MessageObserver extends ContentObserver {
	private Handler handler;

	public MessageObserver(Handler handler) {
		super(handler);
		this.handler = handler;
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		if (handler != null) {
			handler.sendEmptyMessage(0);
		}
	}
}
