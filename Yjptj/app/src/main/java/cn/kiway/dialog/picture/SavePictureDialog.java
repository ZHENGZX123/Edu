package cn.kiway.dialog.picture;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import cn.kiway.IConstant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.dialog.BaseDialog;
import cn.kiway.dialog.BaseDialog.SavePicCallBack;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class SavePictureDialog extends BaseDialog implements SavePicCallBack {
	BaseActivity activity;
	String picUrl;

	public SavePictureDialog(Context context) {
		super(context);
		activity = (BaseActivity) context;
		view = ViewUtil.inflate(context, R.layout.dialog_save_pic);
		fullWindowBottom(context);
		setContentView(view, layoutParams);
		view.findViewById(R.id.save_photo).setOnClickListener(this);
		view.findViewById(R.id.cacel).setOnClickListener(this);
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	String videoPath;

	@Override
	public void onClick(View v) {
		super.onClick(v);
		dismiss();
		switch (v.getId()) {
		case R.id.save_photo:
			if (picUrl == null) {
				return;
			}
			videoPath = AppUtil.downloadPhoto(StringUtil.MD5(picUrl));
			final File f = new File(videoPath + ".jpg");
			IConstant.executorService.execute(new Runnable() {
				@Override
				public void run() {
					if (!f.exists())
						ViewUtil.downloadFile(f, picUrl, activity,
								SavePictureDialog.this);
				}
			});
			break;
		case R.id.cacel:
			dismiss();
			break;
		}
	}

	@Override
	public void savePicSuccess() throws Exception {
		handler.sendEmptyMessage(1);
	}

	@Override
	public void savePicFail() throws Exception {
		handler.sendEmptyMessage(2);
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				ViewUtil.showMessage(activity, R.string.bczp);
				activity.sendBroadcast(new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
								.parse("file://" + videoPath + ".jpg")));
				break;
			case 2:
				ViewUtil.showMessage(activity, R.string.bcsb);
				break;
			}
		};
	};
}
