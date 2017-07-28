package cn.kiway.adapter;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.WebViewActivity;
import cn.kiway.model.SchoolNewsModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class SchoolNewsAdapter extends ArrayAdapter<SchoolNewsModel> implements
		OnClickListener {
	SchoolHolder holder;
	BaseActivity activity;
	public List<SchoolNewsModel> list;

	public SchoolNewsAdapter(Context context, List<SchoolNewsModel> list) {
		super(context, -1);
		activity = (BaseActivity) context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity, R.layout.school_list_item);
			holder = new SchoolHolder();
			holder.schoolImg = ViewUtil.findViewById(view, R.id.school_img);
			holder.schoolTitle = ViewUtil.findViewById(view, R.id.school_news);
			view.setTag(holder);
		} else {
			holder = (SchoolHolder) view.getTag();
		}
		SchoolNewsModel model = list.get(position);
		ViewUtil.setContent(holder.schoolTitle, model.getSchoolTitle());
		activity.imageLoader.displayImage(
				StringUtil.imgUrl(activity, model.getSchoolImg()),
				holder.schoolImg, activity.options);
		view.setTag(R.id.bundle_params, position);
		view.setOnClickListener(this);
		return view;
	}

	class SchoolHolder {
		/**
		 * 学校标题
		 * */
		TextView schoolTitle;
		/**
		 * 学校图片
		 * */
		ImageView schoolImg;
	}

	@Override
	public void onClick(View v) {
		int position = StringUtil
				.toInt(v.getTag(R.id.bundle_params).toString());
		Bundle bundle = new Bundle();
		bundle.putString(IConstant.BUNDLE_PARAMS,
				IUrContant.GET_SCHOOL_DATIAL_URL
						+ list.get(position).getSchoolId());
		bundle.putString(IConstant.BUNDLE_PARAMS1,
				activity.resources.getString(R.string.school_detial));
		activity.startActivity(WebViewActivity.class, bundle);// 学校新闻
	}
}
