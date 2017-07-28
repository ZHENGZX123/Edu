package cn.kiway.fragment.studio;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.common.MipcaCaptureActivity;
import cn.kiway.adapter.StudioViewPageAdapter;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.utils.ViewUtil;

public class StudioFragment extends BaseFragment implements
		OnCheckedChangeListener, OnPageChangeListener {
	RadioGroup radioGroup;
	RadioButton rb1, rb2, rb3;
	ViewPager viewPager;
	StudioViewPageAdapter adapter;

	public StudioFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = ViewUtil.inflate(activity, R.layout.fragment_studio);
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	void initView() throws Exception {
		if (activity.app.getBoyModels().size() <= 0) {
			view.findViewById(R.id.no_class).setVisibility(View.VISIBLE);
			view.findViewById(R.id.baby_view).setVisibility(View.GONE);
			view.findViewById(R.id.radio_group2).setVisibility(View.GONE);
			view.findViewById(R.id.img).setVisibility(View.GONE);
			view.findViewById(R.id.scan).setOnClickListener(this);
			return;
		}
		viewPager = ViewUtil.findViewById(view, R.id.baby_view);
		radioGroup = ViewUtil.findViewById(view, R.id.radio_group2);
		rb1 = ViewUtil.findViewById(view, R.id.rb1);
		rb2 = ViewUtil.findViewById(view, R.id.rb2);
		rb3 = ViewUtil.findViewById(view, R.id.rb3);
		radioGroup.setOnCheckedChangeListener(this);
		adapter = new StudioViewPageAdapter(activity.fragmentManager);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.scan:
			activity.startActivity(MipcaCaptureActivity.class);// 扫描二维码
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
		if (page == 0) {
			rb1.setChecked(true);
			rb2.setChecked(false);
			rb3.setChecked(false);
		} else if (page == 1) {
			rb1.setChecked(false);
			rb2.setChecked(true);
			rb3.setChecked(false);

		} else if (page == 2) {
			rb1.setChecked(false);
			rb2.setChecked(false);
			rb3.setChecked(true);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int id) {
		switch (id) {
		case R.id.rb1:
			viewPager.setCurrentItem(0);
			break;
		case R.id.rb2:
			viewPager.setCurrentItem(1);
			break;
		case R.id.rb3:
			viewPager.setCurrentItem(2);
			break;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			if (activity.app.getBoyModels().size() <= 0) {
				view.findViewById(R.id.no_class).setVisibility(View.VISIBLE);
				view.findViewById(R.id.baby_view).setVisibility(View.GONE);
				view.findViewById(R.id.radio_group2).setVisibility(View.GONE);
				view.findViewById(R.id.img).setVisibility(View.GONE);
				view.findViewById(R.id.scan).setOnClickListener(this);
				return;
			}
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
