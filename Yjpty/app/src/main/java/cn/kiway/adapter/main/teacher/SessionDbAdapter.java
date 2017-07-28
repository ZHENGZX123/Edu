package cn.kiway.adapter.main.teacher;

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
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.main.teaching.SessionDbDetailActivity;
import cn.kiway.model.VideoCateMode;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class SessionDbAdapter extends ArrayAdapter<VideoCateMode> implements
		OnClickListener {
	BaseActivity activity;
	SessionDbHolder holder;
	public List<VideoCateMode> list;
	boolean isAttendClass;

	public SessionDbAdapter(Context context, List<VideoCateMode> list,
			boolean isAttendClass) {
		super(context, -1);
		this.activity = (BaseActivity) context;
		this.list = list;
		this.isAttendClass = isAttendClass;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity, R.layout.session_db_list_item);
			holder = new SessionDbHolder();
			holder.videoImg = ViewUtil.findViewById(view, R.id.pic);
			holder.videoName = ViewUtil.findViewById(view, R.id.name);
			view.setTag(holder);
		} else {
			holder = (SessionDbHolder) view.getTag();
		}
		VideoCateMode model = list.get(position);
		ViewUtil.setContent(holder.videoName, model.getName());
		activity.imageLoader.displayImage(
				StringUtil.imgUrl(activity, model.getPreview()),
				holder.videoImg, activity.options);
		if (model.getIsUser() == 1) {
			view.findViewById(R.id.zidingyi).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.zidingyi).setVisibility(View.VISIBLE);
		}
		view.setTag(R.id.bundle_params, position);
		view.setOnClickListener(this);
		return view;
	}

	class SessionDbHolder {
		/**
		 * 视频图像
		 * */
		ImageView videoImg;
		/**
		 * 视频名字
		 * */
		TextView videoName;
	}

	@Override
	public void onClick(View v) {
		int postion = StringUtil.toInt(v.getTag(R.id.bundle_params).toString());
		VideoCateMode model = list.get(postion);
		Bundle bundle = new Bundle();
		bundle.putSerializable(IConstant.BUNDLE_PARAMS, model);
		bundle.putBoolean(IConstant.BUNDLE_PARAMS1, isAttendClass);
		activity.startActivity(SessionDbDetailActivity.class, bundle);
	}
}
