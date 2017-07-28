package cn.kiway.dialog.message;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.widget.EditText;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.dialog.BaseDialog;
import cn.kiway.http.BaseHttpHandler;
import cn.kiway.http.HttpHandler;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.ViewUtil;

public class SendHomeNotifyDialog extends BaseDialog implements OnShowListener,
		HttpHandler {
	BaseActivity activity;
	String title;
	MessageModel messageModel;
	int type;// 1发布作业 2 发布通知
	EditText content;
	/**
	 * 请求回调
	 */
	protected BaseHttpHandler dialogHandler = new BaseHttpHandler(this) {
	};
	ProgressDialog dialog;

	public void setTitle(String title, int type) {
		this.title = title;
		this.type = type;
	}

	public SendHomeNotifyDialog(Context context, MessageModel messageModel) {
		super(context);
		activity = (BaseActivity) context;
		this.messageModel = messageModel;
		view = ViewUtil.inflate(context, R.layout.dialog_send_home_or_notify);
		content = ViewUtil.findViewById(view, R.id.home_notify_content);
		fullWindowCenter(context);
		setContentView(view, layoutParams);
		view.findViewById(R.id.send).setOnClickListener(this);
		setOnShowListener(this);
		dialog = new ProgressDialog(activity);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.send:
			if (dialog != null && !dialog.isShowing())
				dialog.show();
			Map<String, String> map = new HashMap<>();
			map.put("classId", messageModel.getToUid() + "");
			map.put("userId", activity.app.getUid() + "");
			map.put("content", content.getText().toString());
			if (type == 1)
				IConstant.HTTP_CONNECT_POOL.addRequest(
						IUrContant.CREATE_HOMEWORK_URL, map, dialogHandler);
			else
				IConstant.HTTP_CONNECT_POOL.addRequest(
						IUrContant.CREATE_NOTIFY_URL, map, dialogHandler);
			break;
		}
	}

	@Override
	public void onShow(DialogInterface arg0) {
		ViewUtil.setContent(view, R.id.title, title);
	}

	@Override
	public void httpErr(HttpResponseModel message) throws Exception {

	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
		if (message.getUrl().equals(IUrContant.CREATE_HOMEWORK_URL)
				|| message.getUrl().equals(IUrContant.CREATE_NOTIFY_URL)) {
			content.getText().clear();
			dismiss();
		}
	}

	@Override
	public void HttpError(HttpResponseModel message) throws Exception {

	}
}
