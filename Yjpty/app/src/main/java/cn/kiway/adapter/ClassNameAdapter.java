package cn.kiway.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.main.creatclass.ClassNameListActivity;
import cn.kiway.model.ClassModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class ClassNameAdapter extends ArrayAdapter<ClassModel> implements
		OnClickListener {
	public List<ClassModel> list;
	ClassNameListActivity activity;
	ClassNameHodler hodler;

	public ClassNameAdapter(Context context, List<ClassModel> list) {
		super(context, -1);
		this.list = list;
		this.activity = (ClassNameListActivity) context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			hodler = new ClassNameHodler();
			view = ViewUtil.inflate(activity, R.layout.spinner_list_item);
			hodler.schoolName = ViewUtil.findViewById(view, R.id.text1);
			view.setTag(hodler);
		} else {
			hodler = (ClassNameHodler) view.getTag();
		}
		ViewUtil.setContent(hodler.schoolName, list.get(position)
				.getSchoolName());
		view.setTag(R.id.bundle_params, position);
		view.setOnClickListener(this);
		return view;
	}

	class ClassNameHodler {
		/**
		 * 学校名字
		 * */
		TextView schoolName;
	}

	@SuppressWarnings("static-access")
	@Override
	public void onClick(View v) {
		Intent da = activity.getIntent();
		da.putExtra(IConstant.BUNDLE_PARAMS, list.get(StringUtil.toInt(v
				.getTag(R.id.bundle_params).toString())));
		activity.setResult(activity.RESULT_OK, da);
		activity.finish();
	}
}
