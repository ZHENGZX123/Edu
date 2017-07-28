package cn.kiway.yjhz.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import cn.kiway.yjhz.R;
import cn.kiway.yjhz.fragment.AdvFragment;

public class AdvViewPagerAdapter extends FragmentPagerAdapter {
    List<String> list;
    public AdvFragment[] fragments;

    public AdvViewPagerAdapter(FragmentManager fm, List<String> list) {
        super(fm);
        this.list = list;
        fragments = new AdvFragment[list.size()];
        if (list.size() == 0)
            list.add("drawable://" + R.drawable.kwadv);
    }

    @Override
    public Fragment getItem(int postion) {
        if (fragments[postion] == null)
            fragments[postion] = AdvFragment.newInstance(postion, list);
        return fragments[postion];
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
