package cn.kiway.dialog;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.PlayVideoActivity;
import cn.kiway.adapter.ChooseVideoAdapter;
import cn.kiway.model.VideoPlayModel;
import cn.kiway.utils.ViewUtil;

public class ChooseVideoDialog extends BaseDialog implements
		OnItemClickListener {
	ChooseVideoAdapter adapter;
	ListView listView;
	List<String> list;
	List<VideoPlayModel> list_classs;
	BaseActivity activity;
	TextView text;
	ChooseVideoCallBack back;

	public ChooseVideoDialog(Context context, List<VideoPlayModel> listVideo,
			ChooseVideoCallBack back) {
		super(context);
		this.list_classs = listVideo;
		this.list = new ArrayList<String>();
		this.back = back;
		activity = (PlayVideoActivity) context;
		for (int i = 0; i < list_classs.size(); i++) {
			this.list.add(list_classs.get(i).getContent());
		}
		adapter = new ChooseVideoAdapter(context, this.list, "请选择课节");
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
		fullWindowTop(context);
	}

	public ChooseVideoDialog(Context context, ChooseVideoCallBack back,
			List<String> list) {
		super(context);
		this.list = list;
		this.back = back;
		activity = (BaseActivity) context;
		adapter = new ChooseVideoAdapter(context, this.list, "选择是否公开");
		try {
			initView();
			view.findViewById(R.id.view).setVisibility(View.GONE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		fullWindowBottom(context);
	}

	void initView() throws Exception {
		view = ViewUtil.inflate(context, R.layout.dialog_list_view);
		setContentView(view, layoutParams);
		listView = ViewUtil.findViewById(view, R.id.list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		view.findViewById(R.id.view).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.view) {
			dismiss();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (position == 0)
			return;
		if (back != null) {
			try {
				back.chooseVideolBack(position - 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		dismiss();
	}

	public interface ChooseVideoCallBack {
		public void chooseVideolBack(int position) throws Exception;
	}
}
