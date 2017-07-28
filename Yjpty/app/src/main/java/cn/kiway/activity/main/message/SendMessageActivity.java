package cn.kiway.activity.main.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.adapter.common.FacesAdapter;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.message.model.MesageStatus;
import cn.kiway.message.model.MessageProvider;
import cn.kiway.message.util.WriteMsgUitl;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.ViewUtil;

import com.sortlistview.GroupMemberBean;

public class SendMessageActivity extends BaseActivity {
	EditText editPeople, editMessage;
	List<GroupMemberBean> strings = new ArrayList<GroupMemberBean>();
	GridView faceList;

	// /storage/emulated/0/Pictures/Screenshots/S60517-180150.jpg

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_message);
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initView() throws Exception {
		faceList = ViewUtil.findViewById(this, R.id.faces_list);
		editPeople = ViewUtil.findViewById(this, R.id.notify_edit);
		editMessage = ViewUtil.findViewById(this, R.id.edit);
		findViewById(R.id.cancle).setOnClickListener(this);
		findViewById(R.id.add).setOnClickListener(this);
		findViewById(R.id.emoticon).setOnClickListener(this);
		findViewById(R.id.send).setOnClickListener(this);
		editPeople.setEnabled(false);
		try {
			faceList.setAdapter(new FacesAdapter(this, editMessage
					.getEditableText()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.cancle:
			finish();
			break;
		case R.id.add:
			Bundle b = new Bundle();
			b.putBoolean(IConstant.BUNDLE_PARAMS1, true);
			Intent intent = new Intent(SendMessageActivity.this,
					AddPeopleActivity.class);
			intent.putExtras(b);
			startForResult(intent, 1);
			break;
		case R.id.emoticon:
			if (faceList.getVisibility() == View.GONE) {
				faceList.setVisibility(View.VISIBLE);
				ViewUtil.hideKeyboard(this);
			} else {
				faceList.setVisibility(View.GONE);
			}
			break;
		case R.id.send:
			try {
				sendMessage();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requstCode, int resultCode, Intent data) {
		super.onActivityResult(requstCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		strings = (List<GroupMemberBean>) data
				.getSerializableExtra(IConstant.BUNDLE_PARAMS);
		String string = "";
		for (int i = 0; i < strings.size(); i++) {
			/*
			 * if (strings.get(i).getIsSelector()) { string = string +
			 * strings.get(i).getName() + ";"; }
			 */
		}
		ViewUtil.setContent(this, R.id.notify_edit, string);
	}

	void sendMessage() throws Exception {
		if (strings.size() <= 0 || strings == null) {
			ViewUtil.showMessage(this, "没有选定发送的人哦");
			return;
		}
		/*
		 * for (int i = 0; i < strings.size(); i++) { if
		 * (strings.get(i).getIsSelector() && strings.get(i).getId() > 0) {
		 * MessageModel mId = new MessageModel();
		 * mId.setContent(ViewUtil.getContent(this, R.id.edit));
		 * mId.setMid(System.currentTimeMillis() + i);
		 * mId.setName(strings.get(i).getName());
		 * mId.setTime(System.currentTimeMillis());
		 * mId.setToUid(strings.get(i).getId()); mId.setUid(app.getUid());
		 * mId.setMsgType(1); mId.setMsgContentType(4); Map<String, String> map
		 * = new HashMap<>(); Map<String, Object> param = new HashMap<>(); long
		 * id = WriteMsgUitl.writeMsg(getContentResolver(), mId, app,
		 * app.getUid(), MesageStatus.SEND_SUCC); param.put("id", id);
		 * param.put("model", mId); map.put("owner", app.getUid() + "");
		 * map.put("toUser", strings.get(i).getId() + ""); map.put("content",
		 * ViewUtil.getContent(this, R.id.edit));
		 * IConstant.HTTP_CONNECT_POOL.addRequest(
		 * IUrContant.CREATE_MESSAGE_URL, map, activityHandler, param); } }
		 * finish();
		 */
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
	}

	@Override
	public void HttpError(HttpResponseModel message) throws Exception {
		super.HttpError(message);
		Map<String, Object> param = message.getMap();
		if (param != null) {
			long id = (long) param.get("id");
			MessageModel msg = (MessageModel) param.get("model");
			getContentResolver().delete(
					Uri.parse(MessageProvider.MESSAGES_URL), " _id=? ",
					new String[] { "" + id });
			WriteMsgUitl.writeMsg(getContentResolver(), msg, app, app.getUid(),
					null, MesageStatus.SEND_ERR);
		}
	}
}
