package cn.kiway.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;
import com.zbar.lib.decode.RGBLuminanceSource;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.main.creatclass.ClassInfoActivity;
import cn.kiway.activity.main.teaching.SendWifiNameActivity;
import cn.kiway.activity.main.teaching.TeachingPlansActivity;
import cn.kiway.activity.main.teaching.netty.NettyClientBootstrap;
import cn.kiway.activity.main.teaching.netty.PushClient;
import cn.kiway.dialog.NewVersionDialog;
import cn.kiway.dialog.NewVersionDialog.NewVersionCallBack;
import cn.kiway.dialog.WebLoginDailog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.message.util.WriteMsgUitl;
import cn.kiway.model.ClassModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.CRequest;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

/**
 * 描述: 扫描界面
 */
public class MipcaCaptureActivity extends BaseNetWorkActicity implements
		Callback, NewVersionCallBack {
	private CaptureActivityHandler handler;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private boolean vibrate;
	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private RelativeLayout mContainer = null;
	private RelativeLayout mCropLayout = null;
	private boolean isNeedCapture = false;
	NewVersionDialog dialog;

	private ProgressDialog mProgress;// 解析过程的dialgo
	private String photo_path;// 图像地址
	private Bitmap scanBitmap;
	private static final int PARSE_BARCODE_SUC = 300;// 解析成功
	private static final int PARSE_BARCODE_FAIL = 303;// 解析失败
	WebLoginDailog dailog;// 网页登录dialog

	public boolean isNeedCapture() {
		return isNeedCapture;
	}

	public void setNeedCapture(boolean isNeedCapture) {
		this.isNeedCapture = isNeedCapture;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getCropWidth() {
		return cropWidth;
	}

	public void setCropWidth(int cropWidth) {
		this.cropWidth = cropWidth;
	}

	public int getCropHeight() {
		return cropHeight;
	}

	public void setCropHeight(int cropHeight) {
		this.cropHeight = cropHeight;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_qr_scan);
		// 初始化 CameraManager
		CameraManager.init(getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
		mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);
		ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
		TranslateAnimation mAnimation = new TranslateAnimation(
				TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE,
				0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0.85f);
		mAnimation.setDuration(1500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mAnimation.setInterpolator(new LinearInterpolator());
		mQrLineView.setAnimation(mAnimation);
		findViewById(R.id.for_photos).setOnClickListener(this);
		dialog = new NewVersionDialog(this, this);
		if (bundle.getInt(IConstant.BUNDLE_PARAMS) == 1) // 1为上课动作
			findViewById(R.id.for_photos).setVisibility(View.GONE);
		dailog = new WebLoginDailog(this);
	}

	boolean flag = true;

	protected void light() {
		if (flag == true) {
			flag = false;
			// 开闪光灯
			CameraManager.get().openLight();
		} else {
			flag = true;
			// 关闪光灯
			CameraManager.get().offLight();
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.for_photos:
			Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
			innerIntent.setType("image/*");
			Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
			this.startActivityForResult(wrapperIntent, IConstant.FOR_PHOTO);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case IConstant.FOR_PHOTO:
				// 获取选中图片的路径
				Cursor cursor = getContentResolver().query(data.getData(),
						null, null, null, null);
				if (cursor.moveToFirst()) {
					photo_path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
				}
				cursor.close();
				mProgress = new ProgressDialog(MipcaCaptureActivity.this);
				mProgress.setMessage("正在扫描...");
				mProgress.setCancelable(false);
				mProgress.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						Result result = scanningImage(photo_path);
						if (result != null) {
							Message m = mHandler.obtainMessage();
							m.what = PARSE_BARCODE_SUC;
							m.obj = result.getText();
							mHandler.sendMessage(m);
						} else {
							Message m = mHandler.obtainMessage();
							m.what = PARSE_BARCODE_FAIL;
							m.obj = "二维码错误";
							mHandler.sendMessage(m);
						}
					}
				}).start();
				break;
			}
		}
	}

	/**
	 * 扫描二维码图片的方法
	 * 
	 * @param path
	 * @return
	 */
	public Result scanningImage(String path) {
		if (TextUtils.isEmpty(path))
			return null;
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); // 设置二维码内容的编码
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 获取新的大小
		int sampleSize = (int) (options.outHeight / (float) 200);
		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;
		scanBitmap = BitmapFactory.decodeFile(path, options);
		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		try {
			return reader.decode(bitmap1, hints);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {// 图片扫描处理
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mProgress.dismiss();
			switch (msg.what) {
			case PARSE_BARCODE_SUC:
				try {
					analyzeQR((String) msg.obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case PARSE_BARCODE_FAIL:
				dialog.setTitle("无效二维码");
				if (dialog != null)
					dialog.show();
				break;
			}
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	public void handleDecode(String result) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		try {
			analyzeQR(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;

			int x = mCropLayout.getLeft() * width / mContainer.getWidth();
			int y = mCropLayout.getTop() * height / mContainer.getHeight();

			int cropWidth = mCropLayout.getWidth() * width
					/ mContainer.getWidth();
			int cropHeight = mCropLayout.getHeight() * height
					/ mContainer.getHeight();

			setX(x);
			setY(y);
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);
			// 设置是否需要截图
			setNeedCapture(true);

		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(MipcaCaptureActivity.this);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public Handler getHandler() {
		return handler;
	}

	private void playBeepSoundAndVibrate() {
		if (vibrate) {
			AppUtil.Vibrate(this, 100);
		}
	}

	String classId, schoolName, className, hCode, isHot, ip;
	boolean isChangHeziCode = false;

	// 解析二维码
	void analyzeQR(String result) throws Exception {
		if (result.substring(0, 2).equals("==")) {// 网页登录
			dailog.setTokenId(result);
			Map<String, String> map = new HashMap<>();
			map.put("userId", app.getUid() + "");
			map.put("tokenId", result);
			IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.SCAN_URL, map,
					activityHandler);
			return;
		}
		ip = result.split("\\?")[0].substring(7,
				result.split("\\?")[0].length() - 3);
		Map<String, String> map = new HashMap<>();
		Map<String, String> mapRequest = CRequest.URLRequest(result);
		String str = mapRequest.get("ref");
		if (str == null) {
			dialog.setTitle("该二维码不属于幼教平台的二维码,请更换二维码重新扫描");
			if (dialog != null)
				dialog.show();
			return;
		}
		if (str.equals("box")) {// 上课
			hCode = mapRequest.get("cid");
			isHot = mapRequest.get("ishot");
			SharedPreferencesUtil.save(this, IConstant.WIFI_NEME
					+ app.getClassModel().getId(), mapRequest.get("ssid")
					+ ":::" + isHot);
			SharedPreferencesUtil.save(this, IConstant.WIFI_PASSWORD,
					mapRequest.get("pwd"));
			if (mapRequest.get("resoures").indexOf(getResourse()) < 0) {
				dialog.setTitle("盒子资源不对应,无法绑定上课");
				if (dialog != null)
					dialog.show();
				return;
			}
			if (app.getClassModel().getHeZiCode() != null
					&& !"null".equals(app.getClassModel().getHeZiCode())
					&& !hCode.equals(app.getClassModel().getHeZiCode())) {
				dialog.setTitle("该班级已经绑定过盒子了,是否更换绑定的盒子?");
				if (dialog != null)
					dialog.show();
				isChangHeziCode = true;
				return;
			}
			if ("null".equals(app.getClassModel().getHeZiCode())
					|| !hCode.equals(app.getClassModel().getHeZiCode())) {
				map.put("classId", app.getClassModel().getId() + "");
				map.put("hCode", hCode);
				IConstant.HTTP_CONNECT_POOL.addRequest(
						IUrContant.BANG_DING_HE_ZI_URL, map, activityHandler,
						true);
			} else {
				scan();
			}
		} else if (str.equals("class")) {// 加入班级
			classId = mapRequest.get("classid");
			schoolName = mapRequest.get("schoolname");
			className = mapRequest.get("classname");
			if (null != app.getClassModel()
					&& !schoolName.equals(app.getClassModel().getSchoolName())) {// 判断是否属于自己学校的班级
				dialog.setTitle("该班级不属于您现在所在的学校，无法绑定");
				if (dialog != null)
					dialog.show();
			} else {
				map.put("classId", classId);
				map.put("userId", app.getUid() + "");
				IConstant.HTTP_CONNECT_POOL.addRequest(
						IUrContant.JOIN_CLASS_URL, map, activityHandler, true);
			}
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.JOIN_CLASS_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			ViewUtil.showMessage(this, new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				ClassModel classModel = new ClassModel();
				classModel.setClassName(className);
				classModel.setSchoolName(schoolName);
				classModel.setId(StringUtil.toInt(classId));
				classModel.setHeZiCode("null");
				Bundle bundle = new Bundle();
				bundle.putSerializable(IConstant.BUNDLE_PARAMS, classModel);
				startActivity(ClassInfoActivity.class, bundle);
				finish();
				WriteMsgUitl.WriteClassData(this, app, className,
						StringUtil.toInt(classId));
			} else if (data.optInt("retcode") == 11) {
				dialog.setTitle("你已经加入该班级啦");
				if (dialog != null)
					dialog.show();
			} else if (data.optInt("retcode") == 0) {
				dialog.setTitle("加入失败");
				if (dialog != null)
					dialog.show();
			}
		} else if (message.getUrl().equals(IUrContant.BANG_DING_HE_ZI_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 22) {
				dialog.setTitle("该盒子已经绑定了班级,请更换盒子");
				if (dialog != null)
					dialog.show();
				return;
			}
			if (bundle.getInt(IConstant.BUNDLE_PARAMS) != 2) {
				app.getClassModel().setHeZiCode(hCode);
				scan();
			} else {
				if (data.optInt("retcode") == 11) {
					dialog.setTitle("该班级已经绑定该盒子啦");
					if (dialog != null)
						dialog.show();
					return;
				}
			}
			finish();
		} else if (message.getUrl().equals(IUrContant.SCAN_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 3 || data.optInt("retcode") == 0)
				dailog.setLoginText("重新扫描");
			else
				dailog.setLoginText("确认登录");
			if (dailog != null && !dailog.isShowing())
				dailog.show();
			dailog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					try {
						newVersionCallBack();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	public void HttpError(HttpResponseModel message) throws Exception {
		super.HttpError(message);
		if (message.getUrl().equals(IUrContant.JOIN_CLASS_URL))
			dialog.setTitle("加入失败");
		else if (message.getUrl().equals(IUrContant.BANG_DING_HE_ZI_URL))
			dialog.setTitle("绑定失败");
		if (dialog != null)
			dialog.show();
	}

	@Override
	public void newVersionCallBack() throws Exception {
		if (handler != null) // 实现连续扫描
			handler.sendEmptyMessage(R.id.restart_preview);
		if (isChangHeziCode) {
			HashMap<String, String> map = new HashMap<>();
			map.put("classId", app.getClassModel().getId() + "");
			map.put("hCode", hCode);
			IConstant.HTTP_CONNECT_POOL.addRequest(
					IUrContant.BANG_DING_HE_ZI_URL, map, activityHandler, true);
		}
		isChangHeziCode = false;
	}

	// 扫码后的处理
	void scan() {
		SharedPreferencesUtil.save(this, SendWifiNameActivity.IS_NOTIFY
				+ app.getClassModel().getHeZiCode(), false);
		if (Boolean.parseBoolean(isHot)) {
			startActivity(SendWifiNameActivity.class);
		} else {
			NettyClientBootstrap.host = ip;
			PushClient.create();
			if (PushClient.isOpen())
				PushClient.close();
			PushClient.start();
			Bundle bundle = new Bundle();
			bundle.putBoolean(IConstant.BUNDLE_PARAMS, true);// 1上课 2 看备课
			startActivity(TeachingPlansActivity.class, bundle);
		}
		finish();
	}

	public String getResourse() {
		String string = "";
		if (app.getClassModel().getYear() == 0) {
			string = "";
		} else if (app.getClassModel().getYear() == 1) {
			string = "大大班";
		}
		if (app.getClassModel().getYear() == 2) {
			string = "大班";
		}
		if (app.getClassModel().getYear() == 3) {
			string = "中班";
		}
		if (app.getClassModel().getYear() == 4) {
			string = "小班";
		}
		return string;
	}
}