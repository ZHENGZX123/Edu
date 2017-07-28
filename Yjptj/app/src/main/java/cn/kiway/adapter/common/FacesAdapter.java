package cn.kiway.adapter.common;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.Selection;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.utils.ViewUtil;

/**
 * 表情适配器
 * 
 * @author YI
 * */
public class FacesAdapter extends ArrayAdapter<String> implements
		OnClickListener {
	BaseActivity activity;
	String[] facesKey;
	AssetManager assetManager;
	FacesHolder holder;
	Editable editable;
	int max;

	public FacesAdapter(Context context, Editable editable) throws Exception {
		super(context, -1, context.getAssets().list("faces"));
		this.activity = (BaseActivity) context;
		assetManager = activity.getAssets();
		facesKey = activity.resources.getStringArray(R.array.faces_key);
		this.editable = editable;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity, R.layout.face_list_item);
			holder = new FacesHolder();
			holder.face = ViewUtil.findViewById(view, R.id.face);
			view.setTag(holder);
		} else {
			holder = (FacesHolder) view.getTag();
		}
		String str = "faces/" + getItem(position);
		try {
			Bitmap bitmap = BitmapFactory.decodeStream(assetManager.openFd(str)
					.createInputStream());
			holder.face.setTag(facesKey[position]);
			holder.face.setImageBitmap(bitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		holder.face.setOnClickListener(this);
		return view;
	}

	static class FacesHolder {
		ImageView face;
	}

	@Override
	public void onClick(View v) {
		Object tag = v.getTag();
		if (tag instanceof String) {
			editable.insert(Selection.getSelectionStart(editable),
					tag.toString());
		}
	}
}
