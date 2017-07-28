package cn.kiway.adapter;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.CircleImageView;
import uk.co.senab.photoview.SelectableRoundedImageView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.adapter.common.UserInfoGalleryPicAdapter;
import cn.kiway.utils.ViewUtil;

public class MainAdapter extends BaseExpandableListAdapter {
	BaseActivity activity;
	ClassGroupHolder groupHolder;
	ClassItemHodler itemHodler;
	int picHeight;
	List<String> stringsd = new ArrayList<String>();

	public MainAdapter(BaseActivity activity) {
		this.activity = activity;
		stringsd.add("http://pic.wenwen.soso.com/p/20120710/20120710183507-2101092448.jpg");
		stringsd.add("http://pic.wenwen.soso.com/p/20120710/20120710183507-2101092448.jpg");
		stringsd.add("http://pic.wenwen.soso.com/p/20120710/20120710183507-2101092448.jpg");
		stringsd.add("http://pic.wenwen.soso.com/p/20120710/20120710183507-2101092448.jpg");
		stringsd.add("http://pic.wenwen.soso.com/p/20120710/20120710183507-2101092448.jpg");
		picHeight = (int) ((activity.displayMetrics.widthPixels - activity.resources
				.getDimension(R.dimen._100dp)) / 3)
				+ (int) activity.resources.getDimension(R.dimen._5dp);
	}

	@Override
	public Object getGroup(int arg0) {
		return null;
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		return null;
	}

	@Override
	public int getChildrenCount(int arg0) {
		return 5;
	}

	@Override
	public int getGroupCount() {
		return 5;
	}

	@Override
	public long getChildId(int gruoppostion, int childpostion) {
		return childpostion;
	}

	@Override
	public long getGroupId(int grooppostion) {
		return grooppostion;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity,
					R.layout.my_comment_list_item_head);
			groupHolder = new ClassGroupHolder();
			groupHolder.userPhotos = ViewUtil.findViewById(view,
					R.id.user_photos);
			groupHolder.userImg = ViewUtil.findViewById(view, R.id.profile);
			groupHolder.userName = ViewUtil.findViewById(view, R.id.user_name);
			groupHolder.content = ViewUtil.findViewById(view, R.id.content);
			groupHolder.creatTime = ViewUtil.findViewById(view,
					R.id.create_time);
			groupHolder.delete = ViewUtil.findViewById(view, R.id.delete);
			groupHolder.zan = ViewUtil.findViewById(view, R.id.zan);
			groupHolder.comment = ViewUtil.findViewById(view, R.id.comment);
			groupHolder.zanPeople = ViewUtil
					.findViewById(view, R.id.zan_prople);
			view.setTag(groupHolder);
		} else {
			groupHolder = (ClassGroupHolder) view.getTag();
		}
		activity.imageLoader
				.displayImage(
						"http://pic.wenwen.soso.com/p/20120710/20120710183507-2101092448.jpg",
						groupHolder.userImg, activity.options);
		groupHolder.userPhotos.setAdapter(new UserInfoGalleryPicAdapter(
				activity, stringsd, picHeight));
		if (stringsd.size() > 6) {
			groupHolder.userPhotos
					.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT, 3 * picHeight + 50));

		} else if (stringsd.size() > 3) {
			groupHolder.userPhotos
					.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT, 2 * picHeight + 50));

		} else if (stringsd.size() > 0) {
			groupHolder.userPhotos
					.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT, picHeight + 50));

		} else {
			groupHolder.userPhotos
					.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT, 0));
		}
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity,
					R.layout.my_comment_list_item_item);
			itemHodler = new ClassItemHodler();
			itemHodler.commentImg = ViewUtil.findViewById(view, R.id.user_img);
			itemHodler.commentUserName = ViewUtil.findViewById(view,
					R.id.user_name);
			itemHodler.commentTime = ViewUtil.findViewById(view,
					R.id.reply_time);
			itemHodler.commentContent = ViewUtil.findViewById(view,
					R.id.reply_content);
			view.setTag(itemHodler);
		} else {
			itemHodler = (ClassItemHodler) view.getTag();
		}

		return view;
	}

	static class ClassGroupHolder {
		/**
		 * 说说的图像
		 * */
		GridView userPhotos;
		/**
		 * 发布人头像
		 * */
		CircleImageView userImg;
		/**
		 * 发布人名字
		 * */
		TextView userName;
		/**
		 * 发布内容
		 * */
		TextView content;
		/**
		 * 发布时间
		 * */
		TextView creatTime;
		/**
		 * 删除按钮
		 * */
		ImageView delete;
		/**
		 * 赞按钮
		 * */
		TextView zan;
		/**
		 * 评论按钮
		 * */
		TextView comment;
		/**
		 * 赞的人
		 * */
		TextView zanPeople;
	}

	static class ClassItemHodler {
		/**
		 * 评论人头像
		 * */
		SelectableRoundedImageView commentImg;
		/**
		 * 评论人名字
		 * */
		TextView commentUserName;
		/**
		 * 评论时间
		 * */
		TextView commentTime;
		/**
		 * 评论内容
		 * */
		TextView commentContent;
	}
}
