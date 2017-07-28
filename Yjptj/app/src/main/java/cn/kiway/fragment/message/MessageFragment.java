package cn.kiway.fragment.message;

import handmark.pulltorefresh.library.PullToRefreshBase;
import handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import cn.kiway.IConstant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.choosebaby.EditTeacherPhoneActivity;
import cn.kiway.activity.common.MipcaCaptureActivity;
import cn.kiway.activity.message.AddPeopleActivity;
import cn.kiway.adapter.message.ChatListAdapter;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.message.model.MessageChatProvider;
import cn.kiway.message.model.MessageObserver;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.ViewUtil;

public class MessageFragment extends BaseFragment implements
		OnRefreshListener2<ListView> {
	PullToRefreshListView listView;
	ListView lv;
	ChatListAdapter adapter;
	/**
	 * 消息观察者
	 * */
	MessageObserver messageObserver;
	ContentResolver contentResolver;

	public MessageFragment() {
		super();
	}

	@SuppressLint("HandlerLeak")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = ViewUtil.inflate(activity, R.layout.fragment_message);
		contentResolver = activity.getContentResolver();
		messageObserver = new MessageObserver(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					loadData();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		contentResolver.registerContentObserver(
				Uri.parse(MessageChatProvider.MESSAGECHATS_URL), true,
				messageObserver);
		try {
			initView();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	void initView() throws Exception {
		if (activity.app.getBoyModels().size() <= 0) {
			view.findViewById(R.id.no_class).setVisibility(View.VISIBLE);
			view.findViewById(R.id.boy_list).setVisibility(View.GONE);
			view.findViewById(R.id.img).setVisibility(View.GONE);
			view.findViewById(R.id.add).setVisibility(View.GONE);
			view.findViewById(R.id.scan).setOnClickListener(this);
			view.findViewById(R.id.phone).setOnClickListener(this);
		}else{
		view.findViewById(R.id.no_class).setVisibility(View.GONE);
		view.findViewById(R.id.add).setVisibility(View.VISIBLE);
		view.findViewById(R.id.boy_list).setVisibility(View.VISIBLE);
		view.findViewById(R.id.img).setVisibility(View.VISIBLE);
		adapter = new ChatListAdapter(activity, new ArrayList<MessageModel>());
		listView = ViewUtil.findViewById(view, R.id.boy_list);
		listView.setOnRefreshListener(this);
		lv = listView.getRefreshableView();
		lv.setAdapter(adapter);
		view.findViewById(R.id.add).setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.scan:
			activity.startActivity(MipcaCaptureActivity.class);// 扫描二维码
			break;
		case R.id.add:
			Bundle bundle = new Bundle();
			bundle.putBoolean(IConstant.BUNDLE_PARAMS, true);
			bundle.putBoolean(IConstant.BUNDLE_PARAMS3, false);
			activity.startActivity(AddPeopleActivity.class, bundle);
			break;
		case R.id.phone:
			activity.startActivity(EditTeacherPhoneActivity.class);
			break;
		}
	}

	@Override
	public void loadData() throws Exception {
		listView.onRefreshComplete();
		List<MessageModel> models = new ArrayList<MessageModel>();
		Cursor cursor = contentResolver.query(
				Uri.parse(MessageChatProvider.MESSAGECHATS_URL), null,
				"_msgtype=?", new String[] { "1" }, " _time desc");
		while (cursor.moveToNext()) {
			MessageModel model = new MessageModel();
			model.setUid(cursor.getLong(1));
			model.setMid(cursor.getLong(2));
			model.setName(cursor.getString(3));
			model.setContent(cursor.getString(4));
			model.setTime(cursor.getLong(5));
			model.setHeadUrl(cursor.getString(6));
			model.setMessageNumber(cursor.getInt(7));
			model.setToUid(cursor.getLong(8));
			model.setMsgType(cursor.getInt(10));
			models.add(model);
		}
		cursor.close();
		adapter.messagelist.clear();
		adapter.messagelist.addAll(models);
		adapter.notifyDataSetChanged();
		listView.onRefreshComplete();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		try {
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		try {
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			try {
				loadData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onResume() {
		try {
			initView();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (messageObserver != null)
			contentResolver.unregisterContentObserver(messageObserver);
	}
}
