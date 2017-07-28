package cn.kiway.adapter.main.inschoolinfo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import cn.kiway.fragment.InSchoolInfoFragment;
import cn.kiway.model.BoyModel;

public class InSchoolInfoViewPagerAdapter extends FragmentPagerAdapter {
    List<BoyModel> list;// 孩子列表
    boolean isSend;
    InSchoolInfoFragment[] fragments;

    public InSchoolInfoViewPagerAdapter(FragmentManager fm,
                                        List<BoyModel> list, boolean isSend) {
        super(fm);
        this.list = list;
        this.isSend = isSend;
        fragments = new InSchoolInfoFragment[6];
    }

    @Override
    public Fragment getItem(int postion) {
        if (fragments[postion] == null)
            fragments[postion] = InSchoolInfoFragment.newInstance(postion + 1, list, isSend);
        return fragments[postion];
    }

    @Override
    public int getCount() {
        return 6;
    }

}
