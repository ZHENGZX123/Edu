package cn.kiway.adapter.main;

import java.util.List;

import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.utils.ViewUtil;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChooseClassAdapter extends ArrayAdapter<String> {
	List<String> list;
	BaseActivity activity;
	ChooseClassHodler hodler;
	String string;

	public ChooseClassAdapter(Context context, List<String> list, String string) {
		super(context, -1);
		this.list = list;
		this.activity = (BaseActivity) context;
		this.string = string;
	}

	@Override
	public int getCount() {
		return list.size() + 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity, R.layout.dialog_list_item);
			hodler = new ChooseClassHodler();
			hodler.item = ViewUtil.findViewById(view, R.id.item);
			view.setTag(hodler);
		} else {
			hodler = (ChooseClassHodler) view.getTag();
		}
		if (position == 0) {
			ViewUtil.setContent(hodler.item, string);
		} else {
			String string = list.get(position - 1);
			ViewUtil.setContent(hodler.item, string);
		}
		return view;
	}

	class ChooseClassHodler {
		TextView item;
	}
}
