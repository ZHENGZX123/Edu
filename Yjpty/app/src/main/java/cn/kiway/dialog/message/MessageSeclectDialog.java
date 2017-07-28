package cn.kiway.dialog.message;

import android.content.Context;
import android.view.View;
import cn.kiway.Yjpty.R;
import cn.kiway.dialog.BaseDialog;
import cn.kiway.utils.ViewUtil;

public class MessageSeclectDialog extends BaseDialog {
	MessageSelectCallBack back;

	public MessageSeclectDialog(Context context, int homeWorkNumber,
			int notifyNumber, MessageSelectCallBack back) {
		super(context);
		this.back = back;
		view = ViewUtil.inflate(context, R.layout.message_select_dialog);
		setContentView(view, layoutParams);
		fullWindowTop(context);
		view.findViewById(R.id.homework).setOnClickListener(this);
		view.findViewById(R.id.notify).setOnClickListener(this);
		if (homeWorkNumber <= 0) {
			view.findViewById(R.id.homework_number).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.homework_number).setVisibility(View.VISIBLE);
			if (homeWorkNumber >= 99) {
				ViewUtil.setContent(view, R.id.homework_number, "99");
			} else {
				ViewUtil.setContent(view, R.id.homework_number, homeWorkNumber
						+ "");
			}
		}
		if (notifyNumber <= 0) {
			view.findViewById(R.id.notify_number).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.notify_number).setVisibility(View.VISIBLE);
			if (notifyNumber >= 99) {
				ViewUtil.setContent(view, R.id.notify_number, "99");
			} else {
				ViewUtil.setContent(view, R.id.notify_number, homeWorkNumber
						+ "");
			}
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.homework:
			if (back != null) {
				try {
					back.selectHomeWork();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.notify:
			if (back != null) {
				try {
					back.selectNotify();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
		dismiss();
	}

	public interface MessageSelectCallBack {
		public void selectHomeWork() throws Exception;

		public void selectNotify() throws Exception;
	}
}
