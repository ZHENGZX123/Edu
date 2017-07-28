package cn.kiway.dialog.picture;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.dialog.BaseDialog;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.utils.ViewUtil;

/**
 * 取得图像来源对话框
 * */
public class SelectPictureDialog extends BaseDialog {
	String picPath;
	BaseFragment fragment;
	
	public SelectPictureDialog(Context context, BaseFragment fragment) {
		super(context);
		view = ViewUtil.inflate(context, R.layout.dialog_log_user_pic);
		fullWindowBottom(context);
		setContentView(view, layoutParams);
		view.findViewById(R.id.take_photo).setOnClickListener(this);
		view.findViewById(R.id.choose_existing).setOnClickListener(this);
		view.findViewById(R.id.cacel).setOnClickListener(this);
		this.fragment = fragment;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		dismiss();
		switch (v.getId()) {
		case R.id.take_photo:
			try {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(new File(picPath)));
				fragment.startActivityForResult(intent,
						IConstant.FOR_CAMERA);
			} catch (Exception e) {
			}
			break;
		case R.id.choose_existing:
			try {
				Intent intent = new Intent(Intent.ACTION_PICK,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				fragment.startActivityForResult(intent,
						IConstant.FOR_PHOTO);
			} catch (Exception e) {
			}
			break;
		case R.id.cacel:
			dismiss();
			break;
		}
	}

}
