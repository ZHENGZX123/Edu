package cn.kiway.adapter;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.choosebaby.ChooseMyBabyActivity;
import cn.kiway.model.BoyModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class ChooseClassAdapter extends ArrayAdapter<BoyModel> implements
		OnClickListener {
	public List<BoyModel> list;
	BaseActivity activity;
	ChooseClassHolder holder;

	public ChooseClassAdapter(Context context, List<BoyModel> list) {
		super(context, -1);
		this.activity = (BaseActivity) context;
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity, R.layout.item_list_item);
			holder = new ChooseClassHolder();
			holder.className = ViewUtil.findViewById(view, R.id.user_name);
			holder.classGrade = ViewUtil.findViewById(view, R.id.grade);
			view.findViewById(R.id.user_img).setVisibility(View.GONE);
			view.setTag(holder);
		}
		ViewUtil.setContent(holder.className, list.get(position).getClassName());
		switch (list.get(position).getGrade()) {
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
		view.setTag(position);
		view.setOnClickListener(this);
		return view;
	}

	class ChooseClassHolder {
		/**
		 * 班级名字
		 * */
		TextView className;
		/**
		 * 班级年级
		 * */
		TextView classGrade;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public void onClick(View v) {
		int position = StringUtil.toInt(v.getTag().toString());
		Bundle bundle = new Bundle();
		bundle.putString(IConstant.BUNDLE_PARAMS1, list.get(position)
				.getClassName());
		bundle.putString(IConstant.BUNDLE_PARAMS2, list.get(position)
				.getTeacherName());
		bundle.putString(IConstant.BUNDLE_PARAMS, list.get(position)
				.getClassId() + "");
		activity.startActivity(ChooseMyBabyActivity.class, bundle);
	}
}
