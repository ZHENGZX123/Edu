package cn.kiway.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import cn.kiway.Yjpty.R;
import cn.kiway.adapter.GuidePagerAdapter;
import cn.kiway.login.LoadingActivity;
import cn.kiway.utils.ViewUtil;

/**
 * 引导页
 * 
 * @author YI
 * */
public class GuideActivity extends BaseActivity implements OnPageChangeListener {
	ViewPager pager;
	int[] ids = { R.id.rb1, R.id.rb2, R.id.rb3 };
	long time;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_guide);
		findViewById(R.id.join_us).setOnClickListener(this);
		pager = ViewUtil.findViewById(this, R.id.view_pager);
		pager.setAdapter(new GuidePagerAdapter(this));
		pager.setOnPageChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.join_us:
			finishAllAct();
			startActivity(LoadingActivity.class);
			break;
		}
	}

	@Override
	public void loadData() throws Exception {
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int page) {
		ViewUtil.setCheckStatusRadioButton(this, ids[page], true);
		if (page == 2) {
			findViewById(R.id.join_us).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.join_us).setVisibility(View.GONE);
		}
	}

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
}
