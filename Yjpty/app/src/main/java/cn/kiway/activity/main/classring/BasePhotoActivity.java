package cn.kiway.activity.main.classring;

import android.os.Bundle;
import cn.kiway.activity.BaseNetWorkActicity;

public class BasePhotoActivity extends BaseNetWorkActicity {
	// 应用是否销毁标志
	protected boolean isDestroy;
	// 防止重复点击设置的标志，涉及到点击打开其他Activity时，将该标志设置为false，在onResume事件中设置为true
	private boolean clickable = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isDestroy = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isDestroy = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 每次返回界面时，将点击标志设置为可点击
		clickable = true;
	}

	/**
	 * 当前是否可以点击
	 * 
	 * @return
	 */
	protected boolean isClickable() {
		return clickable;
	}

	/**
	 * 锁定点击
	 */
	protected void lockClick() {
		clickable = false;
	}
}
