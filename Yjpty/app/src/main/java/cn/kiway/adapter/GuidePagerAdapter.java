package cn.kiway.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.utils.ViewUtil;

/**
 * 引导页适配器
 * 
 * @author YI
 * */
public class GuidePagerAdapter extends PagerAdapter {
	private List<View> views;
	int[] ids = { R.drawable.one, R.drawable.two, R.drawable.three };
	BaseActivity activity;

	@SuppressLint("NewApi")
	public GuidePagerAdapter(Context context) {
		super();
		this.activity = (BaseActivity) context;
		this.views = getViews();
		for (int id : ids) {
			View view = ViewUtil.inflate(context, R.layout.guide_list_item);
			ImageView guide_img = ViewUtil.findViewById(view, R.id.guide_img);
			activity.imageLoader.displayImage("drawable://" + id, guide_img);
			views.add(view);
		}
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(views.get(position));
		return views.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(views.get(position));
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	public List<View> getViews() {
		if (views == null)
			views = new ArrayList<View>();
		return views;
	}
}