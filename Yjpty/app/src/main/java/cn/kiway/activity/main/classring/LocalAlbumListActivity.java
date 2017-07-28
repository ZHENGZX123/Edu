package cn.kiway.activity.main.classring;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.common.LocalImageHelper;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

/**
 * 本地相册
 */
public class LocalAlbumListActivity extends BasePhotoActivity implements
		AdapterView.OnItemClickListener {
	ListView listView;
	ImageView progress;
	LocalImageHelper helper;
	View camera;
	List<String> folderNames;
	public static LocalAlbumListActivity instance = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_album);
		instance = this;
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void initView() throws Exception {
		listView = ViewUtil.findViewById(this, R.id.local_album_list);
		camera = ViewUtil.findViewById(this,R.id.loacal_album_camera);
		camera.setOnClickListener(this);
		camera.setVisibility(View.GONE);
		progress = ViewUtil.findViewById(this, R.id.progress_bar);
		helper = LocalImageHelper.getInstance();
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.rotate_loading);
		progress.startAnimation(animation);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 开启线程初始化本地图片列表，该方法是synchronized的，因此当AppContent在初始化时，此处阻塞
				LocalImageHelper.getInstance().initImage();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// 初始化完毕后，显示文件夹列表
						if (!isDestroy) {
							initAdapter();
							progress.clearAnimation();
							((View) progress.getParent())
									.setVisibility(View.GONE);
							listView.setVisibility(View.VISIBLE);
							camera.setVisibility(View.VISIBLE);
						}
					}
				});
			}
		}).start();
		listView.setOnItemClickListener(this);
		findViewById(R.id.previos).setOnClickListener(this);
	}

	public void initAdapter() {
		listView.setAdapter(new FolderAdapter(this, helper.getFolderMap()));
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loacal_album_camera:
			if (LocalImageHelper.getInstance().getCurrentSize()
					+ LocalImageHelper.getInstance().getCheckedItems().size() >= 9) {
				Toast.makeText(this, resources.getString(R.string.max_photos),
						Toast.LENGTH_SHORT).show();
				return;
			}
			PackageManager packageManager = context.getPackageManager();
			if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
				try {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// 拍照后保存图片的绝对路径
					String cameraPath = LocalImageHelper.getInstance()
							.setCameraImgPath();
					File file = new File(cameraPath);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
					intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
					startActivityForResult(intent,
							IConstant.REQUEST_CODE_GETIMAGE_BYCAMERA);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				ViewUtil.showMessage(this,
						resources.getString(R.string.no_user_camcre));
			}
			break;
		case R.id.previos:
			finish();
			break;
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case IConstant.REQUEST_CODE_GETIMAGE_BYCAMERA:
				String cameraPath = LocalImageHelper.getInstance()
						.getCameraImgPath();
				if (StringUtil.isEmpty(cameraPath)) {
					Toast.makeText(this,
							resources.getString(R.string.get_fail_photos),
							Toast.LENGTH_SHORT).show();
					return;
				}
				File file = new File(cameraPath);
				if (file.exists()) {
					Uri uri = Uri.fromFile(file);
					LocalImageHelper.LocalFile localFile = new LocalImageHelper.LocalFile();
					localFile.setThumbnailUri(uri.toString());
					localFile.setOriginalUri(uri);
					localFile.setOrientation(getBitmapDegree(cameraPath));
					localFile.setIsTakePhoto(true);
					LocalImageHelper.getInstance().getCheckedItems()
							.add(localFile);
					LocalImageHelper.getInstance().setResultOk(true);
					// 这里本来有个弹出progressDialog的，在拍照结束后关闭，但是要延迟1秒，原因是由于三星手机的相机会强制切换到横屏，
					// 此处必须等它切回竖屏了才能结束，否则会有异常
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							try {
								startActivity(DynamicPostActivity.class);
								finish();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, 1000);
				} else {
					Toast.makeText(this, resources.getString(R.string.get_fail_photos), Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 读取图片的旋转的角度，还是三星的问题，需要根据图片的旋转角度正确显示
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return 图片的旋转角度
	 */
	private int getBitmapDegree(String path) {
		int degree = 0;
		try {
			// 从指定路径下读取图片，并获取其EXIF信息
			ExifInterface exifInterface = new ExifInterface(path);
			// 获取图片的旋转信息
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@SuppressLint("InflateParams")
	public class FolderAdapter extends BaseAdapter {
		Map<String, List<LocalImageHelper.LocalFile>> folders;
		Context context;

		@SuppressWarnings("rawtypes")
		FolderAdapter(Context context,
				Map<String, List<LocalImageHelper.LocalFile>> folders) {
			this.folders = folders;
			this.context = context;
			folderNames = new ArrayList<>();
			Iterator iter = folders.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				folderNames.add(key);
			}
			// 根据文件夹内的图片数量降序显示
			Collections.sort(folderNames, new Comparator<String>() {
				public int compare(String arg0, String arg1) {
					Integer num1 = helper.getFolder(arg0).size();
					Integer num2 = helper.getFolder(arg1).size();
					return num2.compareTo(num1);
				}
			});
		}

		@Override
		public int getCount() {
			return folders.size();
		}

		@Override
		public Object getItem(int i) {
			return null;
		}

		@Override
		public long getItemId(int i) {
			return 0;
		}

		@Override
		public View getView(int i, View convertView, ViewGroup viewGroup) {
			ViewHolder viewHolder;
			if (convertView == null || convertView.getTag() == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.item_albumfoler, null);
				viewHolder.imageView = (ImageView) convertView
						.findViewById(R.id.imageView);
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.textview);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			String name = folderNames.get(i);
			List<LocalImageHelper.LocalFile> files = folders.get(name);
			viewHolder.textView.setText(name + "(" + files.size() + ")");
			if (files.size() > 0) {
				//修改的地方
				imageLoader.displayImage(files.get(0).getThumbnailUri(),
						viewHolder.imageView, options);
			}
			return convertView;
		}

		private class ViewHolder {
			ImageView imageView;
			TextView textView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		Intent intent = new Intent(LocalAlbumListActivity.this,
				LocalAlbumDetailActivity.class);
		intent.putExtra(IConstant.LOCAL_FOLDER_NAME, folderNames.get(i));
		intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
		startActivity(intent);
	}
}
