package cn.kiway.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import cn.kiway.fragment.main.BabyInfoFragment;
import cn.kiway.model.BoyModel;

public class BabyViewPagerAdapter extends FragmentPagerAdapter {
    List<BoyModel> list;
    BabyInfoFragment[] fragments;

    public BabyViewPagerAdapter(FragmentManager fm, List<BoyModel> list) {
        super(fm);
        this.list = list;
        fragments = new BabyInfoFragment[list.size()];
    }

    @Override
    public Fragment getItem(int postion) {
        if (fragments[postion] == null)
            fragments[postion] = BabyInfoFragment.newInstance(postion, list);
        return fragments[postion];
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
