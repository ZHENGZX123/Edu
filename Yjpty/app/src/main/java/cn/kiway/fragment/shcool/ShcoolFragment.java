package cn.kiway.fragment.shcool;

import handmark.pulltorefresh.library.PullToRefreshBase;
import handmark.pulltorefresh.library.PullToRefreshBase.Mode;
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
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import cn.kiway.Yjpty.R;
import cn.kiway.adapter.main.message.ChatListAdapter;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.message.model.MessageChatProvider;
import cn.kiway.message.model.MessageObserver;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.ViewUtil;

public class ShcoolFragment extends BaseFragment implements
		OnRefreshListener2<ListView> {
	ChatListAdapter adapter;
	ContentResolver contentResolver;
	PullToRefreshListView listView;
	ListView lv;
	Handler handler;

	public ShcoolFragment() {
		super();
	}

	/**
	 * 消息观察者
	 * */
	MessageObserver messageObserver;

	@SuppressLint("HandlerLeak")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = ViewUtil.inflate(activity, R.layout.activity_growth_profile);
		contentResolver = activity.getContentResolver();
		messageObserver = new MessageObserver(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				handler.sendEmptyMessage(0);
			}
		});
		contentResolver.registerContentObserver(
				Uri.parse(MessageChatProvider.MESSAGECHATS_URL), true,
				messageObserver);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				listView.onRefreshComplete();
				if (adapter == null)
					return;
				List<MessageModel> models = new ArrayList<MessageModel>();
				Cursor cursor = contentResolver.query(
						Uri.parse(MessageChatProvider.MESSAGECHATS_URL), null,
						"_msgtype=? or _msgtype=?", new String[] { "3", "2" },
						" _time desc");
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
					model.setHomerWorkNumber(cursor.getInt(11));
					model.setNotifyNumber(cursor.getInt(12));
					models.add(model);
				}
				cursor.close();
				adapter.messagelist.clear();
				adapter.messagelist.addAll(models);
				adapter.notifyDataSetChanged();
			}
		};
		try {
			initView();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	@SuppressWarnings("deprecation")
	void initView() throws Exception {
		view.findViewById(R.id.previos).setVisibility(View.GONE);
		ViewUtil.setContent(view, R.id.title, R.string.shcool_home);
		listView = ViewUtil.findViewById(view, R.id.boy_list);
		adapter = new ChatListAdapter(activity, new ArrayList<MessageModel>());
		listView.setMode(Mode.PULL_DOWN_TO_REFRESH);
		listView.setOnRefreshListener(this);
		lv = listView.getRefreshableView();
		lv.setAdapter(adapter);
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void loadData() throws Exception {
		handler.sendEmptyMessage(0);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		handler.sendEmptyMessageDelayed(0, 1000);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (messageObserver != null)
			contentResolver.unregisterContentObserver(messageObserver);
	}
}
