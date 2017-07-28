package cn.kiway.activity.common;

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
import android.os.Vibrator;
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
import android.widget.Toast;

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
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.activity.choosebaby.ChooseMyBabyActivity;
import cn.kiway.dialog.NewVersionDialog;
import cn.kiway.dialog.NewVersionDialog.NewVersionCallBack;
import cn.kiway.dialog.WebLoginDailog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.message.MinaClientHandler;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.CRequest;

public class MipcaCaptureActivity extends BaseNetWorkActicity implements
        Callback, NewVersionCallBack {
    NewVersionDialog dialog;
    Map<String, String> mapRequest;
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

    private ProgressDialog mProgress;// 解析过程的dialgo
    private String photo_path;// 图像地址
    private Bitmap scanBitmap;
    private static final int PARSE_BARCODE_SUC = 300;// 解析成功
    private static final int PARSE_BARCODE_FAIL = 303;// 解析失败
    private static final long VIBRATE_DURATION = 200L;
    WebLoginDailog webLoginDailog;// 网页登录dialog

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

    /**
     * Called when the activity is first created.
     */
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
        webLoginDailog = new WebLoginDailog(this);
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
                                if (result.getText().contains("/yjpt?ref=school&schoolId=")) {
                                    dialog.setTitle("该二维码不可用相册扫描");
                                    if (dialog != null)
                                        dialog.show();
                                    return;
                                }
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
                    Toast.makeText(MipcaCaptureActivity.this, (String) msg.obj,
                            Toast.LENGTH_LONG).show();
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

        // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
        // handler.sendEmptyMessage(R.id.restart_preview);
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
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    String schoolName = "";

    // 解析二维码
    void analyzeQR(String result) throws Exception {
        if (result.substring(0, 2).equals("==")) {// 网页登录
            webLoginDailog.setTokenId(result);
            Map<String, String> map = new HashMap<>();
            map.put("userId", app.getUid() + "");
            map.put("tokenId", result);
            IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.SCAN_URL, map,
                    activityHandler);
            return;
        }
        mapRequest = CRequest.URLRequest(result);
        String str = mapRequest.get("ref");
        if (str == null) {
            dialog.setTitle("该二维码不属于幼教平台的二维码,请更换二维码重新扫描");
            if (dialog != null)
                dialog.show();
            return;
        }
        if (str.equals("box")) {// 上课
            dialog.setTitle("该二维码是盒子二维码哦");
            if (dialog != null)
                dialog.show();
            return;
        }
        if (!str.equals("class") && mapRequest.get("schoolId") != null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", app.getUid() + "");
            map.put("schoolId", mapRequest.get("schoolId"));
            IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.SCAN_SCHOOL_URL, map,
                    activityHandler, true);
            schoolName = str;
            return;
        }
        startActivity();
    }

    void startActivity() {
        Bundle bundle = new Bundle();
        bundle.putString(IConstant.BUNDLE_PARAMS, mapRequest.get("classid"));
        bundle.putString(IConstant.BUNDLE_PARAMS1, mapRequest.get("classname"));
        bundle.putString(IConstant.BUNDLE_PARAMS2, mapRequest.get("teacher"));
        startActivity(ChooseMyBabyActivity.class, bundle);
        finish();
    }

    boolean isTakeTheChild = false;

    @Override
    public void newVersionCallBack() throws Exception {
        if (handler != null) // 实现连续扫描
            handler.sendEmptyMessage(R.id.restart_preview);
        if (isTakeTheChild) {

            finish();
        }
    }

    @Override
    public void httpSuccess(HttpResponseModel message) throws Exception {
        super.httpSuccess(message);
        if (message.getUrl().equals(IUrContant.SCAN_URL)) {
            JSONObject data = new JSONObject(new String(message.getResponse()));
            if (data.optInt("retcode") == 3 || data.optInt("retcode") == 0)
                webLoginDailog.setLoginText("重新扫描");
            else
                webLoginDailog.setLoginText("确认登录");
            if (webLoginDailog != null && !webLoginDailog.isShowing())
                webLoginDailog.show();
            webLoginDailog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    try {
                        newVersionCallBack();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (message.getUrl().equals(IUrContant.SCAN_SCHOOL_URL)) {
            dialog.setTitle(new String(message.getResponse()).replace("[\"", "").replace("\"]", " ").replace("\",\"",
                    "\n"));
            if (dialog != null)
                dialog.show();
            if (!new String(message.getResponse()).contains("不在"))
                 sendNotify();
        }
    }

    void sendNotify() {
        for (int i = 0; i < app.boyModels.size(); i++) {
            if (app.boyModels.get(i).getSchoolName().equals(schoolName))
                try {
                    sendMessage(app.boyModels.get(i).getClassName(), app.boyModels.get(i).getClassId(), app.boyModels
                            .get(i).getChildName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 发送文字消息
     */
    void sendMessage(String className, long classId, String childName) throws Exception {
        MessageModel mId = new MessageModel();
        mId.setContent("我已扫描门口二维码，确认来接送学生 " + childName);
        if (mCache.getAsJSONObject(IUrContant.GET_MY_INFO_URL + app.getUid()) != null
                && mCache.getAsJSONObject(
                IUrContant.GET_MY_INFO_URL + app.getUid())
                .optJSONObject("userInfo") != null)
            mId.setHeadUrl(mCache
                    .getAsJSONObject(IUrContant.GET_MY_INFO_URL + app.getUid())
                    .optJSONObject("userInfo").optString("photo"));
        mId.setMid(System.currentTimeMillis());
        mId.setName(className);
        mId.setUserName("我");
        mId.setTime(System.currentTimeMillis());
        mId.setToUid(classId);
        mId.setUid(app.getUid());
        mId.setMsgContentType(4);
        mId.setMsgType(3);
        MinaClientHandler.sendMessage(mId,
                "classId=" + classId + "\n",
                getContentResolver());
    }
}
