package cn.kiway.yjhz.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kiway.yjhz.R;
import cn.kiway.yjhz.utils.views.LVCircularRing;

public class LoginDialog extends Dialog implements OnDismissListener {
	LVCircularRing mLoadingView;
	public Dialog mLoadingDialog;
	TextView loadingText;

	public LoginDialog(Context context) {
		super(context);
		// 首先得到整个View
		View view = LayoutInflater.from(context).inflate(R.layout.login_dialog,
				null);
		// 获取整个布局
		LinearLayout layout = (LinearLayout) view
				.findViewById(R.id.dialog_view);
		// 页面中的LoadingView
		mLoadingView = (LVCircularRing) view.findViewById(R.id.lv_circularring);
		// 页面中显示文本
		loadingText = (TextView) view.findViewById(R.id.loading_text);
		// 创建自定义样式的Dialog
		mLoadingDialog = new Dialog(context, R.style.loading_dialog);
		mLoadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		setOnDismissListener(this);
	}

	public void setTitle(String string) {
		loadingText.setText(string);
	}

	public void show() {
		mLoadingDialog.show();
		mLoadingView.startAnim();
	}

	public void close() {
		if (mLoadingDialog != null) {
			mLoadingView.stopAnim();
			mLoadingDialog.dismiss();
			// mLoadingDialog = null;
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (mLoadingDialog != null) {
			mLoadingView.stopAnim();
			mLoadingDialog.dismiss();
			// mLoadingDialog = null;
		}
	}
}
