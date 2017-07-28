package cn.kiway.adapter.main.message;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.main.message.AddPeopleActivity;
import cn.kiway.activity.main.message.PrivateMessageActivity;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

import com.sortlistview.GroupMemberBean;

public class SortGroupMemberAdapter extends BaseAdapter implements
		SectionIndexer, OnClickListener {
	public List<GroupMemberBean> list;
	private AddPeopleActivity activity;
	ViewHolder viewHolder;
	boolean isCreateMessage;

	public SortGroupMemberAdapter(Context mContext, List<GroupMemberBean> list) {
		this.activity = (AddPeopleActivity) mContext;
		this.list = list;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<GroupMemberBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		if (view == null) {
			viewHolder = new ViewHolder();
			view = ViewUtil.inflate(activity,
					R.layout.activity_group_member_item);
			viewHolder.tvTitle = ViewUtil.findViewById(view, R.id.title);
			viewHolder.tvLetter = ViewUtil.findViewById(view, R.id.catalog);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		GroupMemberBean model = list.get(position);
		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(model.getSortLetters());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		viewHolder.tvTitle.setText(this.list.get(position).getName());
		view.setTag(R.id.bundle_params, position);
		view.setOnClickListener(this);
		return view;
	}

	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	@SuppressLint("DefaultLocale")
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public void onClick(View v) {
		if (list.size() == 0)
			return;
		int position = StringUtil
				.toInt(v.getTag(R.id.bundle_params).toString());
		if (list.get(position).getId() == activity.app.getUid())
			return;
		MessageModel messageModel = new MessageModel();
		messageModel.setToUid(list.get(position).getId());
		messageModel.setUid(activity.app.getUid());
		messageModel.setName(list.get(position).getName());
		Bundle bundle = new Bundle();
		bundle.putSerializable(IConstant.BUNDLE_PARAMS, messageModel);
		activity.startActivity(PrivateMessageActivity.class, bundle);
		activity.finish();
	}
}