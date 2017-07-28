package cn.kiway.activity.main.message;

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
import android.view.View;
import android.widget.ListView;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.adapter.main.message.ChatListAdapter;
import cn.kiway.message.model.MessageChatProvider;
import cn.kiway.message.model.MessageObserver;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.ViewUtil;

public class ChatMessageListActivity extends BaseActivity implements
		OnRefreshListener2<ListView> {
	ChatListAdapter adapter;
	ContentResolver contentResolver;
	PullToRefreshListView listView;
	ListView lv;
	/**
	 * 消息观察者
	 * */
	MessageObserver messageObserver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_list);
		contentResolver = getContentResolver();
		try {
			initView();
			loadData();
			setData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void initView() throws Exception {
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
		ViewUtil.setContent(this, R.id.title,
				resources.getString(R.string.news));
		listView = ViewUtil.findViewById(this, R.id.boy_list);
		adapter = new ChatListAdapter(this, new ArrayList<MessageModel>());
		listView.setMode(Mode.DISABLED);
		listView.setOnRefreshListener(this);
		lv = listView.getRefreshableView();
		lv.setAdapter(adapter);
		findViewById(R.id.next_class).setVisibility(View.GONE);
		findViewById(R.id.add).setVisibility(View.VISIBLE);
		findViewById(R.id.add).setOnClickListener(this);
	}

	@SuppressLint("HandlerLeak")
	@Override
	public void loadData() throws Exception {
		super.loadData();
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
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.add:
			Bundle bundle = new Bundle();
			bundle.putBoolean(IConstant.BUNDLE_PARAMS, false);
			bundle.putString(IConstant.BUNDLE_PARAMS1, app.getClassModel()
					.getId() + "");
			bundle.putString(IConstant.BUNDLE_PARAMS2, app.getClassModel()
					.getClassName());
			bundle.putBoolean(IConstant.BUNDLE_PARAMS3, false);
			startActivity(AddPeopleActivity.class, bundle);
			break;

		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
	
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (messageObserver != null)
			contentResolver.unregisterContentObserver(messageObserver);
	}
}
