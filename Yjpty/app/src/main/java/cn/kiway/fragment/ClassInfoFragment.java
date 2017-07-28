package cn.kiway.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.List;

import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.main.creatclass.ClassInfoActivity;
import cn.kiway.model.ClassModel;
import cn.kiway.utils.ViewUtil;

public class ClassInfoFragment extends BaseFragment {
    List<ClassModel> list;
    int position;


    public static ClassInfoFragment newInstance(int position, List<ClassModel> list) {

        Bundle args = new Bundle();
        args.putSerializable("list", (Serializable) list);
        args.putSerializable("position", position);
        ClassInfoFragment fragment = new ClassInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.position = getArguments().getInt("position");
            this.list = (List<ClassModel>) getArguments().getSerializable("list");
        }
    }

    public ClassInfoFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = ViewUtil.inflate(activity, R.layout.fragment_class);
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
        if (list != null) {
            ViewUtil.setContent(view, R.id.class_name, list.get(position)
                    .getClassName());
            ViewUtil.setContent(view, R.id.class_child, list.get(position)
                    .getChildNum() + "");
            switch (list.get(position).getYear()) {
                case 1:
                    ViewUtil.setContent(view, R.id.class_year, R.string.ddb);
                    break;
                case 2:
                    ViewUtil.setContent(view, R.id.class_year, R.string.db);
                    break;
                case 3:
                    ViewUtil.setContent(view, R.id.class_year, R.string.zb);
                    break;
                case 4:
                    ViewUtil.setContent(view, R.id.class_year, R.string.xb);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.layouts:
                activity.app.setClassModel(list.get(position));
                Bundle bundle = new Bundle();
                bundle.putSerializable(IConstant.BUNDLE_PARAMS, list.get(position));
                activity.startActivity(ClassInfoActivity.class, bundle);
                break;
        }
    }

    @Override
    public void loadData() throws Exception {

    }

}
