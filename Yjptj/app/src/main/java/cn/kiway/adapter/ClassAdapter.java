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
import cn.kiway.activity.PlayVideoActivity;
import cn.kiway.model.VideoModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class ClassAdapter extends ArrayAdapter<VideoModel> implements
		OnClickListener {
	ClassHolder holder;
	BaseActivity activity;
	public List<VideoModel> list;

	public ClassAdapter(Context context, List<VideoModel> list) {
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
			view = ViewUtil.inflate(activity, R.layout.class_video_list_item);
			holder = new ClassHolder();
			holder.time = ViewUtil.findViewById(view, R.id.time);
			holder.videoContent = ViewUtil.findViewById(view,
					R.id.video_content);
			holder.videoImg = ViewUtil.findViewById(view, R.id.img);
			holder.videoName = ViewUtil.findViewById(view, R.id.video_name);
			holder.videoPlay = ViewUtil.findViewById(view, R.id.play);
			holder.videoTime = ViewUtil.findViewById(view, R.id.video_time);
			view.setTag(holder);
		} else {
			holder = (ClassHolder) view.getTag();
		}
		VideoModel model = list.get(position);
		ViewUtil.setContent(holder.time, model.getFinishData());
		ViewUtil.setContent(holder.videoContent, model.getTeachingAim());
		ViewUtil.setContent(holder.videoName, model.getLessonName());
		ViewUtil.setContent(holder.videoTime, model.getRequireTime());
		activity.imageLoader.displayImage(
				StringUtil.imgUrl(activity, model.getPreview()),
				holder.videoImg, activity.options);
		holder.videoPlay.setTag(R.id.bundle_params, position);
		holder.videoPlay.setOnClickListener(this);
		return view;
	}

	class ClassHolder {
		/**
		 * 视频时间
		 * */
		TextView time;
		/**
		 * 视频时长
		 * */
		TextView videoTime;
		/**
		 * 视频名字
		 * */
		TextView videoName;
		/**
		 * 视频介绍
		 * */
		TextView videoContent;
		/**
		 * 视频图像
		 * */
		ImageView videoImg;
		/**
		 * 视频播放按钮
		 * */
		ImageView videoPlay;
	}

	@Override
	public void onClick(View v) {
		int position = StringUtil
				.toInt(v.getTag(R.id.bundle_params).toString());
		switch (v.getId()) {
		case R.id.play:
			Bundle bundle = new Bundle();
			bundle.putSerializable(IConstant.BUNDLE_PARAMS, list.get(position));
			activity.startActivity(PlayVideoActivity.class, bundle);
			break;
		}
	}
}
