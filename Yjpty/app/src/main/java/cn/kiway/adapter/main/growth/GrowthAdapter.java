package cn.kiway.adapter.main.growth;

import java.util.List;

import uk.co.senab.photoview.CircleImageView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.adapter.UserInfoGalleryPicAdapter;
import cn.kiway.model.GrowthListModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class GrowthAdapter extends ArrayAdapter<GrowthListModel> {
	GrowthProfileHolder holder;
	BaseActivity activity;
	int picHeight;
	public List<GrowthListModel> list;

	public GrowthAdapter(Context context, List<GrowthListModel> list) {
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
			view = ViewUtil.inflate(activity, R.layout.growth_list_item);
			holder = new GrowthProfileHolder();
			holder.createTime = ViewUtil.findViewById(view, R.id.time);
			holder.content = ViewUtil.findViewById(view, R.id.content);
			holder.photoList = ViewUtil.findViewById(view, R.id.user_photos);
			holder.view = ViewUtil.findViewById(view, R.id.view);
			view.setTag(holder);
		} else {
			holder = (GrowthProfileHolder) view.getTag();
		}
		GrowthListModel growthModel = list.get(position);
		holder.photoList.setAdapter(new UserInfoGalleryPicAdapter(activity,
				growthModel.getPicList(), picHeight));
		if (growthModel.getPicList().size() > 6) {// 设置图片9宫格的高度
			holder.photoList.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 3 * picHeight + 50));
			holder.view.setLayoutParams(new LinearLayout.LayoutParams(
					(int) activity.resources.getDimension(R.dimen._1px),
					3 * picHeight + 50));
		} else if (growthModel.getPicList().size() > 3) {
			holder.photoList.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 2 * picHeight + 50));
			holder.view.setLayoutParams(new LinearLayout.LayoutParams(
					(int) activity.resources.getDimension(R.dimen._1px),
					2 * picHeight + 50));
		} else if (growthModel.getPicList().size() > 0) {
			holder.photoList.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, picHeight + 50));
			holder.view.setLayoutParams(new LinearLayout.LayoutParams(
					(int) activity.resources.getDimension(R.dimen._1px),
					picHeight + 50));
		} else {
			holder.photoList.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 0));
			holder.view.setLayoutParams(new LinearLayout.LayoutParams(
					(int) activity.resources.getDimension(R.dimen._1px), 50));
		}
		ViewUtil.setContent(
				holder.createTime,
				StringUtil.getDateField(StringUtil.stringTimeToLong(growthModel
						.getCreateTime()), 6)
						+ "\n\n"
						+ StringUtil.getDateField(StringUtil
								.stringTimeToLong(growthModel.getCreateTime()),
								5));
		ViewUtil.setContent(holder.content, growthModel.getContent());
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
		/**
		 * 线条
		 * */
		View view;
		/**
		 * 记录几天的时光
		 * */
		TextView day;
	}
}
