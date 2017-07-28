package cn.kiway.adapter.main.growth;

import java.util.List;

import uk.co.senab.photoview.CircleImageView;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.main.growth.GrowthActivity;
import cn.kiway.adapter.UserInfoGalleryPicAdapter;
import cn.kiway.model.GrowthListModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class GrowthProfileAdapter extends ArrayAdapter<GrowthListModel>
		implements OnClickListener {
	GrowthProfileHolder holder;
	BaseActivity activity;
	int picHeight;
	public List<GrowthListModel> list;

	public GrowthProfileAdapter(Context context, List<GrowthListModel> list) {
		super(context, -1);
		this.activity = (BaseActivity) context;
		this.list = list;
		picHeight = (int) ((activity.displayMetrics.widthPixels - activity.resources
				.getDimension(R.dimen._100dp)) / 3)
				+ (int) activity.resources.getDimension(R.dimen._5dp);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity, R.layout.develop_list_item);
			holder = new GrowthProfileHolder();
			holder.userImg = ViewUtil.findViewById(view, R.id.profile);
			holder.userName = ViewUtil.findViewById(view, R.id.user_name);
			holder.createTime = ViewUtil.findViewById(view, R.id.create_time);
			holder.content = ViewUtil.findViewById(view, R.id.content);
			holder.photoList = ViewUtil.findViewById(view, R.id.user_photos);
			view.setTag(holder);
		} else {
			holder = (GrowthProfileHolder) view.getTag();
		}
		GrowthListModel model = list.get(position);
		holder.photoList.setAdapter(new UserInfoGalleryPicAdapter(activity,
				model.getPicList(), picHeight));
		if (model.getPicList().size() > 6) {
			holder.photoList.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 3 * picHeight + 50));
		} else if (model.getPicList().size() > 3) {
			holder.photoList.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 2 * picHeight + 50));
		} else if (model.getPicList().size() > 0) {
			holder.photoList.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, picHeight + 50));
		} else {
			holder.photoList.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 0));
		}
		activity.imageLoader.displayImage(
				StringUtil.imgUrl(activity, model.getUserImg()),
				holder.userImg, activity.fadeOptions);
		ViewUtil.setContent(holder.userName, model.getUserName());
		ViewUtil.setContent(holder.createTime, StringUtil
				.getStandardDate(StringUtil.stringTimeToLong(model
						.getCreateTime())));
		ViewUtil.setContent(holder.content, model.getContent());
		holder.userImg.setTag(R.id.bundle_params, position);
		holder.userImg.setOnClickListener(this);
		return view;
	}

	static class GrowthProfileHolder {
		/**
		 * 用户头像
		 * */
		CircleImageView userImg;
		/**
		 * 用户名字
		 * */
		TextView userName;
		/**
		 * 创建时间
		 * */
		TextView createTime;
		/**
		 * 内容
		 * */
		TextView content;
		/**
		 * 图像列表
		 * */
		GridView photoList;
	}

	@Override
	public void onClick(View v) {
		int position = StringUtil
				.toInt(v.getTag(R.id.bundle_params).toString());
		Bundle bundle = new Bundle();
		bundle.putSerializable(IConstant.BUNDLE_PARAMS, list.get(position));
		activity.startActivity(GrowthActivity.class, bundle);
	}
}
