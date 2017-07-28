package cn.kiway.dialog.choosebaby;

import android.content.Context;
import android.view.View;
import cn.kiway.Yjptj.R;
import cn.kiway.dialog.BaseDialog;
import cn.kiway.utils.ViewUtil;

public class EditBabyDialog extends BaseDialog {
	EditBabyNameCallBack callBack;

	public EditBabyDialog(Context context, int string, int s,
			EditBabyNameCallBack callBack) {
		super(context);
		view = ViewUtil.inflate(context, R.layout.dialog_baby_detail);
		fullWindowTop(context);
		setContentView(view, layoutParams);
		this.callBack = callBack;
		view.findViewById(R.id.edit_name).setOnClickListener(this);
		view.findViewById(R.id.detele_baby).setOnClickListener(this);
		ViewUtil.setContent(view, R.id.edit_name, string);
		ViewUtil.setContent(view, R.id.detele_baby, s);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.edit_name:
			if (callBack != null) {
				try {
					callBack.editBabyNameCallBack();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.detele_baby:
			if (callBack != null) {
				try {
					callBack.deleteBabyCallBack();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
		dismiss();
	}

	public interface EditBabyNameCallBack {
		public void editBabyNameCallBack() throws Exception;

		public void deleteBabyCallBack() throws Exception;
	}
}
