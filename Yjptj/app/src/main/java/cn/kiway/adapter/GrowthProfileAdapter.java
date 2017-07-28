package cn.kiway.adapter;

import java.util.List;

import uk.co.senab.photoview.CircleImageView;
import uk.co.senab.photoview.widget.LocalImageHelper;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.me.growth.BasePhotoActivity;
import cn.kiway.activity.me.growth.GrowthActivity;
import cn.kiway.activity.me.growth.LocalAlbumListActivity;
import cn.kiway.adapter.common.UserInfoGalleryPicAdapter;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.model.GrowthListModel;
import cn.kiway.model.SelectPictureModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class GrowthProfileAdapter extends ArrayAdapter<SelectPictureModel>
		implements OnClickListener {
	GrowthProfileHolder holder;
	BasePhotoActivity activity;
	int picHeight;
	public List<GrowthListModel> list;

	public GrowthProfileAdapter(Context context, List<GrowthListModel> list) {
		super(context, -1);
		this.activity = (BasePhotoActivity) context;
		this.list = list;
		picHeight = (int) ((activity.displayMetrics.widthPixels - activity.resources
				.getDimension(R.dimen._100dp)) / 3)
				+ (int) activity.resources.getDimension(R.dimen._5dp);
	}

	@Override
	public int getCount() {
		if (list.size() <= 0)
			return 1;
		else
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
			holder.layout = ViewUtil.findViewById(view, R.id.layout);
			holder.view = ViewUtil.findViewById(view, R.id.view);
			holder.cmaera = ViewUtil.findViewById(view, R.id.carmea);
			holder.day = ViewUtil.findViewById(view, R.id.number);
			view.setTag(holder);
		} else {
			holder = (GrowthProfileHolder) view.getTag();
		}
		if (position == 0) {
			holder.layout.setVisibility(View.VISIBLE);
		} else {
			holder.layout.setVisibility(View.GONE);
		}
		if (list.size() <= 0) {
			view.findViewById(R.id.pic).setVisibility(View.GONE);
			ViewUtil.setContent(holder.day, " 0 ");
		} else {
			ViewUtil.setContent(holder.day, GrowthActivity.dayCount + "");
			view.findViewById(R.id.pic).setVisibility(View.VISIBLE);
			GrowthListModel growthModel = list.get(position);
			holder.photoList.setAdapter(new UserInfoGalleryPicAdapter(activity,
					growthModel.getPicList(), picHeight));
			if (growthModel.getPicList().size() > 6) {
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
				holder.view
						.setLayoutParams(new LinearLayout.LayoutParams(
								(int) activity.resources
										.getDimension(R.dimen._1px), 50));
			}
			ViewUtil.setContent(
					holder.createTime,
					StringUtil.getDateField(StringUtil
							.stringTimeToLong(growthModel.getCreateTime()), 6)
							+ "\n\n"
							+ StringUtil.getDateField(StringUtil
									.stringTimeToLong(growthModel
											.getCreateTime()), 5));
			ViewUtil.setContent(holder.content, growthModel.getContent());
		}
		holder.cmaera.setOnClickListener(this);
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
		 * 是否显示
		 * */
		LinearLayout layout;
		/**
		 * 线条
		 * */
		View view;
		/**
		 * 拍照上传
		 * */
		ImageView cmaera;
		/**
		 * 记录几天的时光
		 * */
		TextView day;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.carmea:
			if (!AppUtil.isNetworkAvailable(activity)) {
				activity.newWorkdialog = new IsNetWorkDialog(activity,
						activity,
						activity.resources
								.getString(R.string.dqsjmylrhlwqljhlwl),
						activity.resources.getString(R.string.ljhlw));
				if (activity.newWorkdialog != null
						&& !activity.newWorkdialog.isShowing()) {
					activity.newWorkdialog.show();
					return;
				}
			}
			LocalImageHelper.getInstance().clear();
			activity.startActivity(LocalAlbumListActivity.class);
			break;

		}
	}
}
