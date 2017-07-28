package cn.kiway.dialog;

import android.content.Context;
import android.view.View;
import cn.kiway.Yjpty.R;
import cn.kiway.utils.ViewUtil;

public class IsNetWorkDialog extends BaseDialog {
	IsNetWorkCallBack back;
	View vx;

	public IsNetWorkDialog(Context context, IsNetWorkCallBack back, String title,String sure) {
		super(context);
		view = ViewUtil.inflate(context, R.layout.dialog_clear_data);
		setContentView(view, layoutParams);
		fullWindowBottom(context);
		this.back = back;
		findViewById(R.id.choose_existing).setOnClickListener(this);
		findViewById(R.id.cacel).setOnClickListener(this);
		ViewUtil.setContent(view, R.id.title, title);
		ViewUtil.setContent(view, R.id.choose_existing, sure);
		setCancelable(false);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		dismiss();
		switch (v.getId()) {
		case R.id.choose_existing:
			if (back != null) {
				try {
					back.isNetWorkCallBack();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		case R.id.cacel:
			if (back != null) {
				try {
					back.cancel();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}

	public interface IsNetWorkCallBack {
		public void isNetWorkCallBack() throws Exception;

		public void cancel() throws Exception;
	}
}
