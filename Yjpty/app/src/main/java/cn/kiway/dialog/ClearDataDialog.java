package cn.kiway.dialog;

import cn.kiway.Yjpty.R;
import cn.kiway.utils.ViewUtil;
import android.content.Context;
import android.view.View;

public class ClearDataDialog extends BaseDialog {
	ClearDataCallBack back;
	View vx;

	public ClearDataDialog(Context context, ClearDataCallBack back,
			String title, View vx) {
		super(context);
		view = ViewUtil.inflate(context, R.layout.dialog_clear_data);
		setContentView(view, layoutParams);
		this.back = back;
		this.vx = vx;
		fullWindowBottom(context);
		findViewById(R.id.choose_existing).setOnClickListener(this);
		findViewById(R.id.cacel).setOnClickListener(this);
		ViewUtil.setContent(view, R.id.title, title);
		if (vx.getId() == R.id.previos_class) {
			view.findViewById(R.id.title).setVisibility(View.GONE);
			ViewUtil.setContent(view, R.id.choose_existing, title);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.choose_existing:
			if (back != null) {
				try {
					back.clearDataCallBack(vx);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		case R.id.cacel:
			dismiss();
			break;
		}
	}

	public interface ClearDataCallBack {
		public void clearDataCallBack(View vx) throws Exception;
	}
}
