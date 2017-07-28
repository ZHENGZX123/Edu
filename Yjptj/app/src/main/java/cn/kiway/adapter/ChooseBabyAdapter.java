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
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.choosebaby.ProfileBabyActivity;
import cn.kiway.activity.me.MyBabyInfoActivity;
import cn.kiway.model.BoyModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class ChooseBabyAdapter extends ArrayAdapter<BoyModel> implements
		OnClickListener {
	ChooseBabyHolder holder;
	BaseActivity activity;
	public List<BoyModel> list;

	public ChooseBabyAdapter(Context context, List<BoyModel> list) {
		super(context, -1);
		this.activity = (BaseActivity) context;
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
			view = ViewUtil.inflate(activity, R.layout.item_list_item);
			holder = new ChooseBabyHolder();
			holder.img = ViewUtil.findViewById(view, R.id.user_img);
			holder.name = ViewUtil.findViewById(view, R.id.user_name);
			holder.isBan = ViewUtil.findViewById(view, R.id.grade);
			view.setTag(holder);
		} else {
			holder = (ChooseBabyHolder) view.getTag();
		}
		BoyModel model = list.get(position);
		ViewUtil.setContent(holder.name, model.getName());
		activity.imageLoader.displayImage(
				StringUtil.imgUrl(activity, model.getUrl()), holder.img,
				activity.fadeOptions);
		if (model.getIsBan()) {
			ViewUtil.setContent(holder.isBan, "(已绑定)");
		} else {
			ViewUtil.setContent(holder.isBan, "");
		}
		view.setTag(R.id.bundle_params, position);
		view.setOnClickListener(this);
		return view;
	}

	class ChooseBabyHolder {
		/**
		 * 宝贝头像
		 * */
		ImageView img;
		/**
		 * 宝贝名字
		 * */
		TextView name;
		/**
		 * 是否绑定
		 * */
		TextView isBan;
	}

	@Override
	public void onClick(View v) {
		int position = StringUtil
				.toInt(v.getTag(R.id.bundle_params).toString());
		BoyModel model = list.get(position);
		if (model.getIsBan())
			return;
		Bundle bundle = new Bundle();
		if (!model.getParentList().toString().equals("[]")) {// 有绑定家长
			bundle.putInt(IConstant.BUNDLE_PARAMS, model.getUid());
			bundle.putBoolean(IConstant.BUNDLE_PARAMS1, true);
			bundle.putString(IConstant.BUNDLE_PARAMS2,
					ViewUtil.getContent(activity, R.id.class_name));
			bundle.putBoolean(IConstant.BUNDLE_PARAMS3, false);
			activity.startActivity(MyBabyInfoActivity.class, bundle);
		} else {// 没有绑定家长
			bundle.putSerializable(IConstant.BUNDLE_PARAMS, model);
			bundle.putBoolean(IConstant.BUNDLE_PARAMS1, false);
			bundle.putBoolean(IConstant.BUNDLE_PARAMS3, false);
			activity.startActivity(ProfileBabyActivity.class, bundle);
		}
	}
}
