package cn.kiway.adapter;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.widget.FilterImageView;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.common.ViewPhotosActivity;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

/**
 * 相册九宫格图像适配器
 * 
 * @author YI
 * */
public class UserInfoGalleryPicAdapter extends ArrayAdapter<String> implements
		OnClickListener {
	UserInfoGalleryPicHolder holder;
	BaseActivity activity;
	LinearLayout.LayoutParams layoutParams;
	Bundle bundle;

	public UserInfoGalleryPicAdapter(Context context, List<String> objects,
			int height) {
		super(context, -1, objects);
		this.activity = (BaseActivity) context;
		layoutParams = new LinearLayout.LayoutParams(height, height);
		bundle = new Bundle();
		bundle.putStringArrayList(IConstant.BUNDLE_PARAMS,
				(ArrayList<String>) objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity, R.layout.gallery_pic_list_item);
			holder = new UserInfoGalleryPicHolder();
			holder.galleryPic = ViewUtil.findViewById(view, R.id.gallery_pic);
			view.setTag(holder);
		} else {
			holder = (UserInfoGalleryPicHolder) view.getTag();
		}
		final String name = getItem(position);
		holder.galleryPic.setTag(R.id.bundle_params, position);
		holder.galleryPic.setLayoutParams(layoutParams);
		activity.imageLoader.displayImage(StringUtil.imgUrl(activity, name),
				holder.galleryPic, activity.options);
		holder.galleryPic.setOnClickListener(this);
		return view;
	}

	static class UserInfoGalleryPicHolder {
		/**
		 * 相册的图像
		 * */
		FilterImageView galleryPic;
	}

	@Override
	public void onClick(View v) {
		Object object = v.getTag(R.id.bundle_params);
		if (object instanceof Integer) {
			bundle.putInt(IConstant.BUNDLE_PARAMS1,
					StringUtil.toInt(object.toString()));
			activity.startActivity(ViewPhotosActivity.class, bundle);
		}
	}
}
