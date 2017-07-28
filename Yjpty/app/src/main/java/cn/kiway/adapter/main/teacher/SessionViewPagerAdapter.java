package cn.kiway.adapter.main.teacher;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cn.kiway.fragment.teacher.SessionDbZFragment;

public class SessionViewPagerAdapter extends FragmentPagerAdapter {
    boolean isAttendClass;
    SessionDbZFragment[] fragments;

    public SessionViewPagerAdapter(FragmentManager fm, boolean isAttendClass) {
        super(fm);
        this.isAttendClass = isAttendClass;
        fragments = new SessionDbZFragment[3];
    }

    @Override
    public Fragment getItem(int postion) {
        if (fragments[postion]==null)
            fragments[postion]=SessionDbZFragment.newInstance(postion + 1, isAttendClass);
        return fragments[postion] ;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
