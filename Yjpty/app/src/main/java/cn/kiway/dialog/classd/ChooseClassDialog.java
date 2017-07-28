package cn.kiway.dialog.classd;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.main.InShcoolnfoActivity;
import cn.kiway.activity.main.teaching.GoToClassActivity;
import cn.kiway.adapter.main.ChooseClassAdapter;
import cn.kiway.adapter.main.inschoolinfo.InSchoolnfoAdater;
import cn.kiway.dialog.BaseDialog;
import cn.kiway.fragment.InSchoolInfoFragment;
import cn.kiway.model.ClassModel;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.ViewUtil;

public class ChooseClassDialog extends BaseDialog implements
		OnItemClickListener {
	ChooseClassAdapter adapter;
	ListView listView;
	List<String> list;
	List<ClassModel> list_classs;
	BaseActivity activity;
	TextView text;
	boolean b = false;
	int positiont;
	InSchoolnfoAdater adater;
	InSchoolInfoFragment fragment;

	public ChooseClassDialog(Context context, List<ClassModel> list_classs,
			boolean b) {
		super(context);
		this.list_classs = list_classs;
		this.list = new ArrayList<String>();
		activity = (GoToClassActivity) context;
		for (int i = 0; i < list_classs.size(); i++) {
			this.list.add(list_classs.get(i).getClassName());
		}
		adapter = new ChooseClassAdapter(context, this.list, "请选择班级");
		this.b = b;
		setCancelable(false);
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ChooseClassDialog(Context context, List<String> list, TextView v,
			int position, InSchoolnfoAdater adater,
			InSchoolInfoFragment fragment) {
		super(context);
		this.list = list;
		this.positiont = position;
		this.fragment = fragment;
		adapter = new ChooseClassAdapter(context, list, "选择默认状态");
		this.text = v;
		this.activity = (BaseActivity) context;
		this.adater = adater;
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void initView() throws Exception {
		view = ViewUtil.inflate(context, R.layout.dialog_list_view);
		fullWindowBottom(context);
		setContentView(view, layoutParams);
		listView = ViewUtil.findViewById(view, R.id.list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (position == 0)
			return;
		if (b) {
			activity.app.setClassModel(list_classs.get(position - 1));
			SharedPreferencesUtil.save(activity, IConstant.CHANGE_CLASS,
					position - 1);
			ViewUtil.setContent(activity, R.id.title, activity.app
					.getClassModel().getClassName());
			if (list_classs.get(position - 1).getHeZiCode().equals("null")) {
				activity.findViewById(R.id.layout2).setVisibility(View.VISIBLE);// 没绑定盒子的界面
				activity.findViewById(R.id.layout).setVisibility(View.GONE);// 绑定了盒子的界面
			} else {
				activity.findViewById(R.id.layout2).setVisibility(View.GONE);// 没绑定盒子的界面
				activity.findViewById(R.id.layout).setVisibility(View.VISIBLE);// 绑定了盒子的界面
			}
		} else {
			String string = list.get(position - 1);
			SharedPreferencesUtil.save(activity, IConstant.CLASS_NAME, string);
			if (v != null) {
				ViewUtil.setContent(text, " 未评价的学生，默认评价为：" + string);
				try {
					ViewUtil.setTextFontColor(text, null,
							activity.resources.getColor(R.color._666666), 0, 13);
					SharedPreferencesUtil.save(activity, IConstant.PING_JIA
							+ positiont, position);
					switch (positiont) {
					case 1:
						InShcoolnfoActivity.list1.clear();
						break;
					case 2:
						InShcoolnfoActivity.list2.clear();
						break;
					case 3:
						InShcoolnfoActivity.list3.clear();
						break;
					case 4:
						InShcoolnfoActivity.list4.clear();
						break;
					case 5:
						InShcoolnfoActivity.list5.clear();
						break;
					case 6:
						InShcoolnfoActivity.list6.clear();
						break;
					}
					fragment.setListValue();
					adater.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		dismiss();
	}
}
