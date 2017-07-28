package cn.kiway.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.utils.ViewUtil;

public class NewVersionDialog extends BaseDialog implements OnShowListener {
	String apkUrl, title;
	BaseActivity activity;
	NewVersionCallBack back;

	public NewVersionDialog(Context context, NewVersionCallBack back) {
		super(context);
		this.activity = (BaseActivity) context;
		this.back = back;
		view = ViewUtil.inflate(context, R.layout.dialog_new_version);
		fullWindowCenter(context);
		setContentView(view, layoutParams);
		findViewById(R.id.cancle).setOnClickListener(this);
		findViewById(R.id.ok).setOnClickListener(this);
		setOnShowListener(this);
	}

	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.ok:
			dismiss();
			if (back != null)
				try {
					back.newVersionOkCallBack();
				} catch (Exception e) {
					e.printStackTrace();
				}
		case R.id.cancle:
			dismiss();
			if (back != null)
				if (!title.equals(activity.resources
						.getString(R.string.new_versionont))) {
					try {
						back.newVersionCallBack();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					try {
						back.newVersionOkCallBack();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			break;
		}
	}

	public interface NewVersionCallBack {
		public void newVersionCallBack() throws Exception;

		public void newVersionOkCallBack() throws Exception;
	}

	@Override
	public void onShow(DialogInterface arg0) {
		ViewUtil.setContent(view, R.id.title, title);
		view.findViewById(R.id.ok).setVisibility(View.GONE);
		ViewUtil.setContent(view, R.id.cancle, "确定");
	}
}
