package cn.kiway.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.List;

import cn.kiway.Yjptj.R;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.model.BoyModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class BabyInfoFragment extends BaseFragment {
    List<BoyModel> list;
    int position;


    public static BabyInfoFragment newInstance(int position, List<BoyModel> list) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putSerializable("list", (Serializable) list);
        BabyInfoFragment fragment = new BabyInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.position = getArguments().getInt("position");
            this.list = (List<BoyModel>) getArguments().getSerializable("list");
        }
    }

    public BabyInfoFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = ViewUtil.inflate(activity, R.layout.fragment_baby);
        try {
            initView();
            setData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    void initView() throws Exception {
        view.findViewById(R.id.layouts).setOnClickListener(this);
    }

    void setData() throws Exception {
        if (list == null)
            return;
        ViewUtil.setContent(view, R.id.baby_name, list.get(position)
                .getChildName());
        switch (list.get(position).getSex()) {
            case 1:
                ViewUtil.setContent(view, R.id.bay_sex, "(男)");
                break;
            case 2:
                ViewUtil.setContent(view, R.id.bay_sex, "(女)");
                break;
        }
        int year = StringUtil.toInt(StringUtil.getDateField(
                System.currentTimeMillis(), 0))
                - StringUtil
                .toInt(list.get(position).getBirthday().split("-")[0])
                - 1;
        int month = 12
                - StringUtil
                .toInt(list.get(position).getBirthday().split("-")[1])
                + StringUtil.toInt(StringUtil.getDateField(
                System.currentTimeMillis(), 1));
        if (StringUtil.toInt(StringUtil.getDateField(
                System.currentTimeMillis(), 0))
                - StringUtil
                .toInt(list.get(position).getBirthday().split("-")[0]) == 0) {
            year = 0;
            month = StringUtil.toInt(StringUtil.getDateField(
                    System.currentTimeMillis(), 1))
                    - StringUtil.toInt(list.get(position).getBirthday()
                    .split("-")[1]);
        }
        if (month > 12) {
            year = year + 1;
            month = month % 12;
        }
        ViewUtil.setContent(view, R.id.baby_year, year + "岁" + month + "月");
    }

    @Override
    public void loadData() throws Exception {
    }

}
