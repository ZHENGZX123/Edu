package cn.kiway.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.model.BoyModel;
import cn.kiway.model.InSchoolInfoModel;
import cn.kiway.utils.ViewUtil;

public class InSchoolInfoAdapter extends ArrayAdapter<BoyModel> {
	BaseActivity activity;
	InSchoolInfoHolder holder;
	public List<InSchoolInfoModel> list;

	@Override
	public int getCount() {
		return list.size();
	}

	public InSchoolInfoAdapter(Context context, List<InSchoolInfoModel> list) {
		super(context, -1);
		this.activity = (BaseActivity) context;
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil
					.inflate(activity, R.layout.in_shcool_info_list_item);
			holder = new InSchoolInfoHolder();
			holder.name = ViewUtil.findViewById(view, R.id.name);
			holder.time = ViewUtil.findViewById(view, R.id.time);
			holder.wsImg = ViewUtil.findViewById(view, R.id.ws);
			holder.lyImg = ViewUtil.findViewById(view, R.id.ly);
			holder.ycImg = ViewUtil.findViewById(view, R.id.yc);
			holder.jlImg = ViewUtil.findViewById(view, R.id.jl);
			holder.fyImg = ViewUtil.findViewById(view, R.id.fy);
			holder.kqImg = ViewUtil.findViewById(view, R.id.kq);
			holder.className = ViewUtil.findViewById(view, R.id.class_name);
			view.setTag(holder);
		} else {
			holder = (InSchoolInfoHolder) view.getTag();
		}
		InSchoolInfoModel infoModel = list.get(position);
		ViewUtil.setImageView(activity, holder.wsImg, infoModel.getWs());
		ViewUtil.setImageView(activity, holder.lyImg, infoModel.getLy());
		ViewUtil.setImageView(activity, holder.ycImg, infoModel.getYc());
		ViewUtil.setImageView(activity, holder.fyImg, infoModel.getFy());
		ViewUtil.setImageView(activity, holder.jlImg, infoModel.getJl());
		ViewUtil.setImageView(activity, holder.kqImg, infoModel.getKq());
		ViewUtil.setContent(holder.name, activity.app.getBoyModels().get(activity.app.getPosition()).getChildName());
		ViewUtil.setContent(holder.className, activity.app.getBoyModels().get(activity.app.getPosition()).getClassName());
		ViewUtil.setContent(holder.time, infoModel.getData());
		return view;
	}

	class InSchoolInfoHolder {
		/**
		 * 宝贝名字
		 * */
		TextView name;
		/**
		 * 时间
		 * */
		TextView time;
		/**
		 * 班级
		 * */
		TextView className;
		/**
		 * 表现图
		 * */
		ImageView wsImg, lyImg, ycImg, jlImg, fyImg, kqImg;
	}
}
