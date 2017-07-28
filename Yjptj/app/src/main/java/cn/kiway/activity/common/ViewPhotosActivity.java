package cn.kiway.activity.common;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.HackyViewPager;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import cn.kiway.IConstant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.adapter.common.ViewPhotoPagerAdapter;
import cn.kiway.utils.ViewUtil;

/**
 * 浏览多页图片
 * 
 * @author Zao 可传两种类型参数 字符串类型和自定义类型的集合 参数名称为BUNDLE_PARAMS(图像集合)
 *         BUNDLE_PARAMS1(第几个)
 * */
public class ViewPhotosActivity extends BaseActivity implements
		OnPageChangeListener {
	HackyViewPager pager;
	List<String> urls;
	int page = 0;
	ViewPhotoPagerAdapter adapter;
	View view;
	public ArrayList<String> selectPhotos = new ArrayList<String>();
	RadioGroup group;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		view = ViewUtil.inflate(this, R.layout.activity_view_photos);
		fullWindowWH();
		setContentView(view, layoutParams);
		pager = ViewUtil.findViewById(view, R.id.vPager);
		try {
			initView();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据不同的值初始化适配器
	 * */
	@SuppressWarnings("unchecked")
	void initView() throws Exception {
		group = ViewUtil.findViewById(this, R.id.group);
		if (bundle.getInt(IConstant.BUNDLE_PARAMS1) == 11) {
			page = 1;
			adapter = new ViewPhotoPagerAdapter(fragmentManager, urls, false,
					this);
		} else {
			urls = (List<String>) bundle
					.getSerializable(IConstant.BUNDLE_PARAMS);
			adapter = new ViewPhotoPagerAdapter(fragmentManager, urls, true,
					this);
			page = bundle.getInt(IConstant.BUNDLE_PARAMS1);
		}
	}

	@Override
	public void loadData() throws Exception {
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(this);
		if (page < adapter.getCount())
			pager.setCurrentItem(page);
		if (adapter.getCount() > 1) {
			findViewById(R.id.step).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.step).setVisibility(View.GONE);
		}
		ViewUtil.setContent(view, R.id.step,
				(page + 1) + " / " + adapter.getCount());
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	int position;

	@Override
	public void onPageSelected(int page) {
		position = page;
		ViewUtil.setContent(view, R.id.step,
				(page + 1) + " / " + adapter.getCount());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
