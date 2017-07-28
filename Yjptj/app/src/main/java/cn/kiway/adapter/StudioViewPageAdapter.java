package cn.kiway.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import cn.kiway.fragment.studio.ClassFragment;
import cn.kiway.fragment.studio.WebFragment;

public class StudioViewPageAdapter extends FragmentPagerAdapter {

	public StudioViewPageAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		if (position == 0) {
			return new ClassFragment();
		} else if (position == 1) {
			return  WebFragment.newInstance(position);
		} else if (position == 2) {
			return  WebFragment.newInstance(position);
		}
		return null;
	}

	@Override
	public int getCount() {
		return 1;
	}

}
