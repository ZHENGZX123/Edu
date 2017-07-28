package cn.kiway.adapter.message;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.utils.ViewUtil;

public class MessagePeopleAdapter extends BaseExpandableListAdapter {
	GroupHolder groupHolder;
	ItemHoder itemHoder;
	BaseActivity activity;

	public MessagePeopleAdapter(Context context) {
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
		return 10;
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
			view.setTag(groupHolder);
		} else {
			groupHolder = (GroupHolder) view.getTag();
		}
		groupHolder.groupName.setText(getGroup(groupPosition));
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

}
