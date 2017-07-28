package cn.kiway.adapter.common;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.utils.ViewUtil;

/**
 * 选择学校列表适配器
 * */
@SuppressLint("InflateParams")
public class SpinnerAdapter extends BaseAdapter {
	public List<String> list = new ArrayList<>();
	BaseActivity activity;

	public SpinnerAdapter(Context context) {
		list.add("大大班");
		list.add("大班");
		list.add("中班");
		list.add("小班");
		this.activity = (BaseActivity) context;
	}

	public SpinnerAdapter(Context context, int type) {
		list.add("开放/无加密");
		list.add("WEP");
		list.add("WPA/WPA2 PSK");
		this.activity = (BaseActivity) context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertview, ViewGroup group) {
		LayoutInflater _LayoutInflater = LayoutInflater.from(activity);
		convertview = _LayoutInflater.inflate(R.layout.spinner_list_item, null);
		if (convertview != null) {
			TextView textView = (TextView) convertview.findViewById(R.id.text1);
			textView.setText(list.get(position));
			if (list.size() == 1) {
				ViewUtil.setArroundDrawable(textView, -1, -1, -1, -1);
			} else {
				ViewUtil.setArroundDrawable(textView, -1, -1,
						R.drawable.icon_go, -1);
			}
		}
		return convertview;
	}
}
