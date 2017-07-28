package cn.kiway.fragment.teacher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import cn.kiway.Yjpty.R;
import cn.kiway.adapter.main.teacher.SessionViewPagerAdapter;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.utils.ViewUtil;

public class SessionDbFragment extends BaseFragment implements
        OnCheckedChangeListener, OnPageChangeListener {
    private SessionViewPagerAdapter adapter;// 列表适配器
    RadioGroup radioGroup;
    RadioButton rb1, rb2, rb3;
    ViewPager viewPager;
    boolean isAttendClass;// 1上课 2看详情

    public SessionDbFragment() {
        super();
    }


    public static SessionDbFragment newInstance(boolean isAttendClass) {

        Bundle args = new Bundle();
        args.putBoolean("isAttendClass", isAttendClass);
        SessionDbFragment fragment = new SessionDbFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            this.isAttendClass = getArguments().getBoolean("isAttendClass");
    }

    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = ViewUtil.inflate(activity, R.layout.fragment_session_db);
        try {
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    void initView() throws Exception {
        viewPager = ViewUtil.findViewById(view, R.id.gift_view);
        radioGroup = ViewUtil.findViewById(view, R.id.radio_group2);
        rb1 = ViewUtil.findViewById(view, R.id.rb1);
        rb2 = ViewUtil.findViewById(view, R.id.rb2);
        rb3 = ViewUtil.findViewById(view, R.id.rb3);
        radioGroup.setOnCheckedChangeListener(this);
        adapter = new SessionViewPagerAdapter(activity.fragmentManager,
                isAttendClass);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);
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
        viewPager.getAdapter().notifyDataSetChanged();
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
        viewPager.getAdapter().notifyDataSetChanged();
    }
}
