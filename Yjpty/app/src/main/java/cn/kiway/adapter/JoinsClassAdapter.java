package cn.kiway.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.model.JoinsClassModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class JoinsClassAdapter extends ArrayAdapter<JoinsClassModel> implements
		OnClickListener {
	JoinClassHolder holder;
	public List<JoinsClassModel> list;
	BaseActivity activity;

	public JoinsClassAdapter(Context context, List<JoinsClassModel> list) {
		super(context, -1);
		this.list = list;
		this.activity = (BaseActivity) context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity, R.layout.choose_class_item);
			holder = new JoinClassHolder();
			holder.className = ViewUtil.findViewById(view, R.id.class_name);
			holder.isSelect = ViewUtil.findViewById(view, R.id.select);
			holder.classGrade = ViewUtil.findViewById(view, R.id.class_grade);
			view.setTag(holder);
		} else {
			holder = (JoinClassHolder) view.getTag();
		}
		JoinsClassModel model = list.get(position);
		ViewUtil.setContent(holder.className, model.getClassName());
		if (model.isSelect()) {
			holder.isSelect.setChecked(true);
		} else {
			holder.isSelect.setChecked(false);
		}
		switch (model.getClassGraid()) {
		case 1:
			ViewUtil.setContent(holder.classGrade, "(大大班)");
			break;
		case 2:
			ViewUtil.setContent(holder.classGrade, "(大班)");
			break;
		case 3:
			ViewUtil.setContent(holder.classGrade, "(中班)");
			break;
		case 4:
			ViewUtil.setContent(holder.classGrade, "(小班)");
			break;
		}
		holder.isSelect.setOnClickListener(this);
		holder.isSelect.setTag(R.id.bundle_params, position);
		return view;
	}

	class JoinClassHolder {
		/**
		 * 班级名字
		 * */
		TextView className;
		/**
		 * 班级年级
		 * */
		TextView classGrade;
		/**
		 * 是否选择
		 * */
		CheckBox isSelect;
	}

	@Override
	public void onClick(View v) {
		int position = StringUtil.toInt(v.getTag(R.id.bundle_params)
				.toString());
		if (list.get(position).isSelect()) {
			list.get(position).setSelect(false);
		} else {
			list.get(position).setSelect(true);
		}
		notifyDataSetChanged();

	}
}
