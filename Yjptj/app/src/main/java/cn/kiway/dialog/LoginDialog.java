package cn.kiway.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kiway.Yjptj.R;
import cn.kiway.utils.views.LVCircularRing;

public class LoginDialog extends BaseDialog implements OnDismissListener {
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
		// 显示文本
		loadingText.setText("玩命加载中");
		// 创建自定义样式的Dialog
		mLoadingDialog = new Dialog(context, R.style.loading_dialog);
		mLoadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		setOnDismissListener(this);
	}

	public void setTitle(String title) {
		loadingText.setText(title);
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
