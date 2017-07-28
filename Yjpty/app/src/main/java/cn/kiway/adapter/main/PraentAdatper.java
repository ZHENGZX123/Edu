package cn.kiway.adapter.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.main.creatclass.TeacherDetailActivity;
import cn.kiway.http.BaseHttpHandler;
import cn.kiway.http.HttpHandler;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.BoyModel;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class PraentAdatper extends ArrayAdapter<BoyModel> implements
		OnLongClickListener, OnClickListener, HttpHandler {
	PraentHolder holder;
	BaseActivity activity;
	public List<BoyModel> list;
	protected BaseHttpHandler adapterHandler = new BaseHttpHandler(this) {
	};
	int own;

	public PraentAdatper(Context context, List<BoyModel> list, int own) {
		super(context, -1);
		this.list = list;
		this.activity = (BaseActivity) context;
		this.own = own;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity, R.layout.item_list_item);
			holder = new PraentHolder();
			holder.imageView = ViewUtil.findViewById(view, R.id.user_img);
			holder.name = ViewUtil.findViewById(view, R.id.user_name);
			view.setTag(holder);
		} else {
			holder = (PraentHolder) view.getTag();
		}
		BoyModel boyModel = list.get(position);
		activity.imageLoader.displayImage(
				StringUtil.imgUrl(activity, boyModel.getImg()),
				holder.imageView, activity.fadeOptions);
		switch (boyModel.getType()) {
		case 1:
			ViewUtil.setContent(holder.name, R.string.dad);
			break;
		case 2:
			ViewUtil.setContent(holder.name, R.string.mun);
			break;
		case 3:
			ViewUtil.setContent(holder.name, R.string.qin);
			break;
		}
		view.setTag(R.id.bundle_params, position);
		if (own == activity.app.getUid()) {
			view.setOnLongClickListener(this);
		}
		view.setOnClickListener(this);
		return view;
	}

	class PraentHolder {
		/***
		 * 家长名字
		 */
		TextView name;
		/**
		 * 家长头像
		 * */
		ImageView imageView;
	}

	@Override
	public boolean onLongClick(View v) {
		int position = StringUtil
				.toInt(v.getTag(R.id.bundle_params).toString());
		Map<String, String> map = new HashMap<>();
		map.put("classId", list.get(position).getClassId() + "");
		map.put("childId", list.get(position).getChildId() + "");
		map.put("parentId", list.get(position).getUid() + "");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.DELTET_PARENT_URL,
				map, adapterHandler);
		return false;
	}

	@Override
	public void onClick(View v) {
		if (list.size() <= 0)
			return;
		int position = StringUtil
				.toInt(v.getTag(R.id.bundle_params).toString());
		Bundle bundle = new Bundle();
		bundle.putSerializable(IConstant.BUNDLE_PARAMS, list.get(position));
		activity.startActivity(TeacherDetailActivity.class, bundle);
	}

	@Override
	public void httpErr(HttpResponseModel message) throws Exception {

	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		if (message.getUrl().equals(IUrContant.DELTET_PARENT_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				ViewUtil.showMessage(activity, "删除成功");
				activity.loadData();
			}
		}

	}

	@Override
	public void HttpError(HttpResponseModel message) throws Exception {

	}
}
