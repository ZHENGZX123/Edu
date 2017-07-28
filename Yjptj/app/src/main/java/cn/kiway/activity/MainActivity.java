package cn.kiway.activity;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.dialog.NewVersionDialog;
import cn.kiway.dialog.NewVersionDialog.NewVersionCallBack;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.fragment.main.MainFragment;
import cn.kiway.fragment.me.MeFragment;
import cn.kiway.fragment.message.MessageFragment;
import cn.kiway.fragment.studio.ClassFragment;
import cn.kiway.http.DownloadService;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.message.MinaClientHandler;
import cn.kiway.message.model.MessageChatProvider;
import cn.kiway.message.model.MessageObserver;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.ViewUtil;

public class MainActivity extends BaseActivity implements
		OnCheckedChangeListener, NewVersionCallBack {
	RadioGroup rg;
	BaseFragment mianFragment, messageFragment, studioFragment, meFragment;
	int page = 0;
	MessageObserver messageObserver;
	ContentResolver contentResolver;
	public static TextView studioText;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contentResolver = getContentResolver();
		messageObserver = new MessageObserver(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					setMsgCount();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		contentResolver.registerContentObserver(
				Uri.parse(MessageChatProvider.MESSAGECHATS_URL), true,
				messageObserver);
		setContentView(R.layout.activity_main);
		try {
			initView();
			setMsgCount();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		app.setInit(true);
		IConstant.executorService.execute(new Runnable() {
			public void run() {
				try {
					if (app.getIoSession() == null
							|| app.getIoSession().isConnected())
						MinaClientHandler.openMessage(app);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	NewVersionDialog versionDialog;// 检查更新dialog

	@Override
	public void loadData() throws Exception {
		super.loadData();
		versionDialog = new NewVersionDialog(this, this);
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.CHECK_VERSION_URL,
				null, activityHandler);
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.CHECK_VERSION_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optDouble("yjptj") > Double.valueOf(AppUtil
					.getVersion(this))) {
				versionDialog.setTitle(resources
						.getString(R.string.new_versionont));
				versionDialog.setApkUrl(IUrContant.DOWNLOAD_APK_URL);
				if (versionDialog != null && !versionDialog.isShowing()) {
					versionDialog.show();
				}
			}
		}
	}

	void initView() throws Exception {
		rg = ViewUtil.findViewById(this, R.id.rg);
		studioText = ViewUtil.findViewById(this, R.id.main_study_number);
		rg.setOnCheckedChangeListener(this);
		// 进来默认展示第一个
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragment(transaction, mianFragment, mianFragment);
		if (mianFragment == null) {
			mianFragment = new MainFragment();
			transaction.add(R.id.fragment, mianFragment, "mianFragment");
		} else if (mianFragment.isAdded() && mianFragment.isHidden()) {
			transaction.show(mianFragment);
		}
		transaction.commit();
	}

	@Override
	public void onCheckedChanged(RadioGroup rg, int checkId) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		int pg = 0;
		switch (checkId) {
		case R.id.rb1:
			pg = 0;
			break;
		case R.id.rb2:
			pg = 1;
			break;
		case R.id.rb3:
			pg = 2;
			break;
		case R.id.rb4:
			pg = 3;
			break;
		}
		if (page > pg) {
			transaction.setCustomAnimations(R.anim.silde_in_left,
					R.anim.silde_out_right);
		} else {
			transaction.setCustomAnimations(R.anim.silde_in_right,
					R.anim.silde_out_left);
		}
		page = pg;
		hideFragment(transaction, mianFragment, messageFragment,
				studioFragment, meFragment);
		switch (checkId) {
		case R.id.rb1:// 主界面
			if (mianFragment == null) {
				mianFragment = new MainFragment();
				transaction.add(R.id.fragment, mianFragment, "mianFragment");
			} else if (mianFragment.isAdded() && mianFragment.isHidden()) {
				transaction.show(mianFragment);
			}
			break;
		case R.id.rb2:// 私信
			if (messageFragment == null) {
				messageFragment = new MessageFragment();
				transaction.add(R.id.fragment, messageFragment,
						"messageFragment");
			} else if (messageFragment.isAdded() && messageFragment.isHidden()) {
				transaction.show(messageFragment);
			}
			break;
		case R.id.rb3:// 课堂
			if (app.getBoyModels() != null && app.getBoyModels().size() > 0)
				SharedPreferencesUtil.save(this,
						MinaClientHandler.ATTEND_CLASS_BEAT
								+ "#"
								+ app.getBoyModels().get(app.getPosition())
										.getClassId(), false);
			studioText.setVisibility(View.GONE);
			if (studioFragment == null) {
				studioFragment = new ClassFragment();
				transaction
						.add(R.id.fragment, studioFragment, "studioFragment");
			} else if (studioFragment.isAdded() && studioFragment.isHidden()) {
				transaction.show(studioFragment);
			}
			break;
		case R.id.rb4:// 我的
			if (meFragment == null) {
				meFragment = new MeFragment();
				transaction.add(R.id.fragment, meFragment, "meFragment");
			} else if (meFragment.isAdded() && meFragment.isHidden()) {
				transaction.show(meFragment);
			}
			break;
		}
		transaction.commit();
	}

	long time = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			long t = System.currentTimeMillis();
			if (t - time >= 2000) {
				time = t;
				ViewUtil.showMessage(context, R.string.exit);
			} else
				finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 设置消息数量
	 * */
	void setMsgCount() throws Exception {
		Cursor cursor = contentResolver.query(
				Uri.parse(MessageChatProvider.MESSAGECHATS_URL),
				new String[] { "sum(_unread) as unreadcount" },
				"_msgtype=? or _msgtype=?", new String[] { "2", "3" }, null);
		while (cursor.moveToNext()) {
			int count = cursor.getInt(0);
			if (count > 0) {
				String str = String.valueOf(count);
				if (count > 99)
					str = "99+";
				ViewUtil.setContent(this, R.id.school_message_number, str);
				findViewById(R.id.school_message_number).setVisibility(
						View.VISIBLE);
			} else {
				findViewById(R.id.school_message_number).setVisibility(
						View.GONE);
			}
		}
		cursor.close();
		cursor = contentResolver.query(
				Uri.parse(MessageChatProvider.MESSAGECHATS_URL),
				new String[] { "sum(_unread) as unreadcount" }, " _msgtype=? ",
				new String[] { "1" }, null);
		while (cursor.moveToNext()) {
			int count = cursor.getInt(0);
			if (count > 0) {
				String str = String.valueOf(count);
				if (count > 99)
					str = "99+";
				ViewUtil.setContent(this, R.id.main_message_number, str);
				findViewById(R.id.main_message_number).setVisibility(
						View.VISIBLE);
			} else {
				findViewById(R.id.main_message_number).setVisibility(View.GONE);
			}
		}
		cursor.close();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if (messageObserver != null)
				contentResolver.unregisterContentObserver(messageObserver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void newVersionOkCallBack() throws Exception {
		Intent intent = new Intent(this, DownloadService.class);
		intent.putExtra(IConstant.BUNDLE_PARAMS, IUrContant.DOWNLOAD_APK_URL);
		startService(intent);
		finishAllAct();
	}
}
