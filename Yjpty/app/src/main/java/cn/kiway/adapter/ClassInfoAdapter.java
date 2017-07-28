package cn.kiway.adapter;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.main.creatclass.BabyDetailActivity;
import cn.kiway.activity.main.creatclass.TeacherDetailActivity;
import cn.kiway.model.BoyModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class ClassInfoAdapter extends BaseExpandableListAdapter implements
		OnClickListener {
	GroupHolder groupHolder;
	ItemHoder itemHoder;
	BaseActivity activity;
	public List<BoyModel> teacherlist;
	public List<BoyModel> childerlist;
	int type;

	public ClassInfoAdapter(Context context, List<BoyModel> teacherlist,
			List<BoyModel> childerlist, int type) {
		super();
		this.activity = (BaseActivity) context;
		this.childerlist = childerlist;
		this.teacherlist = teacherlist;
		this.type = type;
	}

	public ClassInfoAdapter(Context context) {
		super();
		this.activity = (BaseActivity) context;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int gruoppostion, int childpostion) {
		return childpostion;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupPosition == 0) {
			return teacherlist.size();
		} else if (groupPosition == 1) {
			return childerlist.size();
		}
		return 0;
	}

	@Override
	public String getGroup(int groupPosition) {
		switch (groupPosition) {
		case 0:
			return activity.resources.getString(R.string.remove_teacher);
		case 1:
			return activity.resources.getString(R.string.baby);
		}
		return null;
	}

	@Override
	public int getGroupCount() {
		return 2;
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
			view = ViewUtil.inflate(activity, R.layout.head_list_item);
			groupHolder = new GroupHolder();
			groupHolder.groupName = ViewUtil.findViewById(view, R.id.item);
			groupHolder.view = ViewUtil.findViewById(view, R.id.view);
			view.setTag(groupHolder);
		} else {
			groupHolder = (GroupHolder) view.getTag();
		}
		groupHolder.groupName.setText(getGroup(groupPosition));
		if (groupPosition == 0) {
			groupHolder.view.setVisibility(View.VISIBLE);
		} else {
			groupHolder.view.setVisibility(View.GONE);
		}
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity, R.layout.item_list_item);
			itemHoder = new ItemHoder();
			itemHoder.userImg = ViewUtil.findViewById(view, R.id.user_img);
			itemHoder.userName = ViewUtil.findViewById(view, R.id.user_name);
			view.setTag(itemHoder);
		} else {
			itemHoder = (ItemHoder) view.getTag();
		}
		if (groupPosition == 0) {
			BoyModel boyModel = teacherlist.get(childPosition);
			ViewUtil.setContent(itemHoder.userName, boyModel.getName());
			activity.imageLoader.displayImage(
					StringUtil.imgUrl(activity, boyModel.getImg()),
					itemHoder.userImg, activity.fadeOptions);
		} else {
			BoyModel boyModel = childerlist.get(childPosition);
			ViewUtil.setContent(itemHoder.userName, boyModel.getName());
			activity.imageLoader.displayImage(
					StringUtil.imgUrl(activity, boyModel.getImg()),
					itemHoder.userImg, activity.fadeOptions);
		}
		view.setTag(R.id.bundle_params, groupPosition);
		view.setTag(R.id.bundle_params1, childPosition);
		view.setOnClickListener(this);
		return view;
	}

	static class GroupHolder {
		/**
		 * 组的名字
		 * */
		TextView groupName;
		/**
		 * 展开隐藏
		 * */
		View check;
		/**
		 * 组的名字
		 * */
		TextView view;
	}

	static class ItemHoder {
		/**
		 * 用户头像
		 * */
		ImageView userImg;
		/**
		 * 用户名字
		 * */
		TextView userName;
	}

	@Override
	public void onClick(View v) {
		int gropPosition = StringUtil.toInt(v.getTag(R.id.bundle_params)
				.toString());
		int childPosition = StringUtil.toInt(v.getTag(R.id.bundle_params1)
				.toString());
		if (gropPosition == 0) {
			BoyModel boyModel = teacherlist.get(childPosition);
			Bundle bundle = new Bundle();
			bundle.putSerializable(IConstant.BUNDLE_PARAMS,
					(Serializable) boyModel);
			activity.startActivity(TeacherDetailActivity.class, bundle);
		} else if (gropPosition == 1) {
			BoyModel boyModel = childerlist.get(childPosition);
			Bundle bundle = new Bundle();
			bundle.putSerializable(IConstant.BUNDLE_PARAMS,
					(Serializable) boyModel);
			bundle.putInt(IConstant.BUNDLE_PARAMS1, type);
			activity.startActivity(BabyDetailActivity.class, bundle);
		}
	}
}
