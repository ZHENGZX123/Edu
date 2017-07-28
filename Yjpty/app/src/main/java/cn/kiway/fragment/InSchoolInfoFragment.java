package cn.kiway.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.main.InShcoolnfoActivity;
import cn.kiway.adapter.main.inschoolinfo.InSchoolnfoAdater;
import cn.kiway.dialog.classd.ChooseClassDialog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.BoyModel;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.ViewUtil;

public class InSchoolInfoFragment extends BaseFragment {
    int position;// 选择的位置
    ListView listView;
    public static InSchoolnfoAdater adapter;
    List<String> list; // 弹框列表
    ChooseClassDialog chooseClassDialog;// 选择默认弹框
    TextView text;// 默认评价文字
    List<BoyModel> listboy;// 小孩列表
    List<Map<String, String>> liston = new ArrayList<Map<String, String>>();// 提交列表
    boolean isSend;


    public static InSchoolInfoFragment newInstance(int position, List<BoyModel> listboy,
                                                   boolean isSend) {

        Bundle args = new Bundle();
        args.putSerializable("listboy", (Serializable) listboy);
        args.putBoolean("isSend", isSend);
        args.putInt("position", position);
        InSchoolInfoFragment fragment = new InSchoolInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.listboy = (List<BoyModel>) getArguments().getSerializable("listboy");
            this.isSend = getArguments().getBoolean("isSend");
            this.position = getArguments().getInt("position");
        }
    }

    public InSchoolInfoFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = ViewUtil.inflate(activity, R.layout.fragment_inschool_info);
        list = new ArrayList<String>();// 初始化选择的列表的数据
        list.add(activity.resources.getString(R.string.bxyx));
        list.add(activity.resources.getString(R.string.bxbc));
        list.add(activity.resources.getString(R.string.bxnl));
        try {
            setListValue();
            initView();
            setData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    void initView() throws Exception {
        view.findViewById(R.id.pingjia).setOnClickListener(this);
        // view.findViewById(R.id.fglayout).setOnClickListener(this);
        listView = ViewUtil.findViewById(view, R.id.list);
        adapter = new InSchoolnfoAdater(activity, listboy, liston, position);
        listView.setAdapter(adapter);
        text = ViewUtil.findViewById(view, R.id.pingjia);
        ViewUtil.setTextFontColor(text, null,
                activity.resources.getColor(R.color._666666), 0, 13);
        chooseClassDialog = new ChooseClassDialog(activity, list, text,
                position, adapter, this);
    }

    void setData() throws Exception {
        if (InShcoolnfoActivity.isLast()) {// 如果是以前,则隐藏
            view.findViewById(R.id.fglayout).setVisibility(View.GONE);
            view.findViewById(R.id.pingjia).setVisibility(View.GONE);
        }
        if (adapter.getCount() <= 0) {// 孩子数为0，不能提交
            view.findViewById(R.id.fglayout).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.pingjia:// 选择默认评价的按钮
                if (chooseClassDialog != null && !chooseClassDialog.isShowing())
                    chooseClassDialog.show();
                break;
            case R.id.fglayout:// 提交的按钮 单个提交，功能隐藏未开放
                if (listboy.size() <= 0)
                    return;
                Map<String, String> map = new HashMap<>();
                map.put("type", position + "");// 提交的那一项
                map.put("checkList", liston + "");// 提交数据的列表
                IConstant.HTTP_CONNECT_POOL.addRequest(
                        IUrContant.IN_SCHOOL_INFO_ITEM_URL, map, fragmentHandler,
                        true);
                break;
        }
    }

    @Override
    public void loadData() throws Exception {

    }

    @Override
    public void httpSuccess(HttpResponseModel message) throws Exception {
        super.httpSuccess(message);
        if (message.getUrl().equals(IUrContant.IN_SCHOOL_INFO_ITEM_URL)) {
            JSONObject data = new JSONObject(new String(message.getResponse()));
            if (data.optInt("retcode") == 1) {// 提交成功，提示成功提示，更新数据
                ViewUtil.showMessage(activity, R.string.tjcg);
            }
        }
    }

    /**
     * 初始化提交的列表
     */
    public void setListValue() throws Exception {
        int string = SharedPreferencesUtil.getInteger(activity,
                IConstant.PING_JIA + position);
        String str = 1 + "";
        if (string == 1 || string == 0) {
            ViewUtil.setContent(view, R.id.pingjia, R.string.yx);
            str = 1 + "";
        } else if (string == 2) {
            ViewUtil.setContent(view, R.id.pingjia, R.string.bc);
            str = 2 + "";
        } else if (string == 3) {
            ViewUtil.setContent(view, R.id.pingjia, R.string.xn);
            str = 3 + "";
        }
        for (int j = 0; j < listboy.size(); j++) {
            switch (position) {
                case 1:
                    if (listboy.size() != InShcoolnfoActivity.list1.size())
                        InShcoolnfoActivity.list1.add(str);
                    break;
                case 2:
                    if (listboy.size() != InShcoolnfoActivity.list2.size())
                        InShcoolnfoActivity.list2.add(str);
                    break;
                case 3:
                    if (listboy.size() != InShcoolnfoActivity.list3.size())
                        InShcoolnfoActivity.list3.add(str);
                    break;
                case 4:
                    if (listboy.size() != InShcoolnfoActivity.list4.size())
                        InShcoolnfoActivity.list4.add(str);
                    break;
                case 5:
                    if (listboy.size() != InShcoolnfoActivity.list5.size())
                        InShcoolnfoActivity.list5.add(str);
                    break;
                case 6:
                    if (listboy.size() != InShcoolnfoActivity.list6.size())
                        InShcoolnfoActivity.list6.add(str);
                    break;
            }
        }
        liston.clear();
        for (int i = 0; i < listboy.size(); i++) {
            Map<String, String> map = new HashMap<>();
            map.put("childId", listboy.get(i).getUid() + "");
            map.put("level", str);
            liston.add(i, map);
        }
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
}
