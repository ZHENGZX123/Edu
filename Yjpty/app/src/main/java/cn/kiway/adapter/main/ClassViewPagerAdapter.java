package cn.kiway.adapter.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import cn.kiway.fragment.ClassInfoFragment;
import cn.kiway.model.ClassModel;

public class ClassViewPagerAdapter extends FragmentPagerAdapter {
	List<ClassModel> list;
	ClassInfoFragment[] fragments;
	public ClassViewPagerAdapter(FragmentManager fm, List<ClassModel> list) {
		super(fm);
		this.list = list;
		fragments=new ClassInfoFragment[6];
	}

	@Override
	public Fragment getItem(int postion) {
		if (fragments[postion]==null)
			fragments[postion]=ClassInfoFragment.newInstance(postion, list);
		return fragments[postion] ;
	}

	@Override
	public int getCount() {
		return list.size();
	}
}
