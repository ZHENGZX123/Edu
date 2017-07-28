package cn.kiway.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;

public class BaseDialog extends Dialog implements OnClickListener {
	public View view;
	protected Context context;
	protected LayoutParams layoutParams = new LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

	public BaseDialog(Context context) {
		this(context, R.style.LoadingDialog);
	}

	public BaseDialog(Context context, int style) {
		super(context, style);
		this.context = context;
	}

	@Override
	public void onClick(View v) {

	}

	// 居中
	protected void fullWindowCenter(Context context) {
		layoutParams = getWindow().getAttributes();
		Rect rect = new Rect();
		View v = getWindow().getDecorView();
		v.getWindowVisibleDisplayFrame(rect);
	}

	// 居底
	protected void fullWindowBottom(Context context) {
		layoutParams = getWindow().getAttributes();
		BaseActivity ba = (BaseActivity) context;
		Rect rect = new Rect();
		View v = getWindow().getDecorView();
		v.getWindowVisibleDisplayFrame(rect);
		layoutParams.width = ba.displayMetrics.widthPixels;
		Window window = getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.dialogWindowAnim);
	}

	// 居上
	protected void fullWindowTop(Context context) {
		layoutParams = getWindow().getAttributes();
		BaseActivity ba = (BaseActivity) context;
		Rect rect = new Rect();
		View v = getWindow().getDecorView();
		v.getWindowVisibleDisplayFrame(rect);
		layoutParams.width = ba.displayMetrics.widthPixels;
		Window window = getWindow();
		window.setGravity(Gravity.TOP);
		window.setWindowAnimations(R.style.dialogWindowAnimTop);
	}

	// 铺满
	protected void fullWindowWH(Context context) {
		layoutParams = getWindow().getAttributes();
		BaseActivity ba = (BaseActivity) context;
		Rect rect = new Rect();
		View v = getWindow().getDecorView();
		v.getWindowVisibleDisplayFrame(rect);
		layoutParams.width = ba.displayMetrics.widthPixels;
		layoutParams.height = ba.displayMetrics.heightPixels;
		Window window = getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.dialogWindowAnim);
	}

	public interface SavePicCallBack {
		public void savePicSuccess() throws Exception;

		public void savePicFail() throws Exception;
	}
}
