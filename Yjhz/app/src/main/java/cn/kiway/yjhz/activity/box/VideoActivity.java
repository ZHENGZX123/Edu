package cn.kiway.yjhz.activity.box;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.storage.OnObbStateChangeListener;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.kiway.yjhz.R;
import cn.kiway.yjhz.activity.BaseActivity;
import cn.kiway.yjhz.dialog.LoginDialog;
import cn.kiway.yjhz.utils.CommonUitl;
import cn.kiway.yjhz.utils.GlobeVariable;
import cn.kiway.yjhz.wifimanager.MessageHander.AccpectMessageHander;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.widget.VideoView;

@SuppressLint("NewApi")
public class VideoActivity extends BaseActivity {

    private VideoView videoview;
    private int stopPosition;// 暂停的位置
    public static String lastVideoName;
    public String videoIcon;
    StorageManager storageManager;// 外部数据管理
    View blackView;// 变黑
    boolean isPause;
    String videoPlayUrl = "";
    private ObjectAnimator discAnimation, needleAnimation;
    private ImageView disc, needle;
    LoginDialog loginDialog;
    String videoUrl;
    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        // 初始化
        AccpectMessageHander.setAudioActivity(VideoActivity.this);
        iniView();
        videoUrl = intent.getStringExtra(GlobeVariable.PLAY_URL);
        getObbVideoPath(intent.getStringExtra(GlobeVariable.PLAY_NAME));
        loginDialog = new LoginDialog(this);
        loginDialog.setTitle("正在加载中");
    }


    private void iniView() {
        blackView = (View) findViewById(R.id.black);
        videoview = (VideoView) findViewById(R.id.surface_view);
        disc = (ImageView) findViewById(R.id.disc);
        needle = (ImageView) findViewById(R.id.needle);
        blackView.setAlpha(0);
        videoview.setShowErrorDialog(false);
        videoview.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(io.vov.vitamio.MediaPlayer arg0) {
                findViewById(R.id.videocom).setVisibility(View.VISIBLE);
                loginDialog.close();
            }
        });
        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mediaPlayer.setVideoChroma(-1);

                return false;
            }
        });
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });
        videoview.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, final int extra) {
                //在有警告或错误信息时调用。例如：开始缓冲、缓冲结束、下载速度变化
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START://开始缓存，暂停播放，显示正在加载
                        if (videoview.isPlaying()) {
                            videoview.pause();
                            handler.sendEmptyMessage(1);
                        }
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END://缓存完成，继续播放
                        videoview.start();
                        loginDialog.close();
                        break;
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED://显示 下载速度（KB/s）
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loginDialog.setTitle("加载中...\n\n" + extra + "kb/s");
                            }
                        });
                        break;
                }
                return true;
            }
        });
        setAnimations();
    }

    public void setDarkScreen() { // 暗屏

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                /*
                 * if (blackView.getAlpha() + 0.2 > 1) {
				 *
				 * } else { blackView.setAlpha((float) (blackView.getAlpha() +
				 * 0.2)); }
				 */
                blackView.setAlpha(1);
            }
        });

    }

    public void setShineScreen() {// 亮屏
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                /*
                 * if (blackView.getAlpha() - 0.2 < 0) {
				 * 
				 * } else { blackView.setAlpha((float) (blackView.getAlpha() -
				 * 0.2)); }
				 */
                blackView.setAlpha(0);
            }
        });
    }

    @SuppressLint({"NewApi", "HandlerLeak"})
    private void playVideo(final String videoPlayUrl) {// final String name) {
        if (videoview == null)
            return;
        if (videoview.isPlaying()) {
            videoview.pause();
        }
        if (videoIcon.contains("mp3") || videoIcon.contains("wav")
                || videoIcon.contains("ogg") || videoIcon.contains("ape")
                || videoIcon.contains("acc") || videoIcon.contains("wma")) {
            runOnUiThread(new Runnable() {
                public void run() {
                    findViewById(R.id.music).setVisibility(View.VISIBLE);
                    needleAnimation.start();
                    discAnimation.start();
                }
            });
        } else {
            findViewById(R.id.music).setVisibility(View.GONE);
        }
        this.videoPlayUrl = videoPlayUrl;
        handler.sendEmptyMessage(0);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (loginDialog != null && !loginDialog.isShowing())
                    loginDialog.show();
            } else {
                try {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("kw-app-id", "2747ffbb3cfca89d0084d3d95fe42c3f");
                    headers.put("kw-app-key", "a3de031261388f2bd52c57cd4e0f2d964dfbd992");
                    headers.put("kw-client-type", "web");
                    videoview.setVideoURI(Uri.parse(videoPlayUrl), headers);
                    videoview.requestFocus();
                    findViewById(R.id.videocom).setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        ;
    };

    /**
     * 停止
     */
    public void stop() {
        try {
            if (videoview.isPlaying()) {
                videoview.pause();
            } else {
                videoview.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoview.isPlaying()) {
            pauseVideo();
            isPause = true;
        }
        if (loginDialog != null && loginDialog.isShowing())
            loginDialog.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoview != null && isPause) {
            resumeVideo();
            isPause = false;
        }
    }

    public void pauseVideo() {
        try {
            stopPosition = (int) videoview.getCurrentPosition();
            videoview.pause();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 重新播放视频
    public void resumeVideo() {
        try {
            videoview.seekTo(stopPosition);
            videoview.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 快进
     */
    public void goHead() {
        try {
            int goHeadTime = (int) (videoview.getDuration() / 20);
            int currentPosition = (int) videoview.getCurrentPosition();
            if ((goHeadTime + currentPosition) <= videoview.getDuration()) {
                videoview.seekTo(currentPosition + goHeadTime);
                videoview.start();
            } else {
                videoview.seekTo(videoview.getDuration());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 后退
     */
    public void retreat() {
        try {
            int goHeadTime = (int) (videoview.getDuration() / 20);
            int currentPosition = (int) videoview.getCurrentPosition();
            if ((currentPosition - goHeadTime) >= 0) {
                videoview.seekTo(currentPosition - goHeadTime);
                videoview.start();
            } else {
                videoview.seekTo(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void accecpData(final String videoName, final String videoUrl) {
        this.videoUrl = videoUrl;
        getObbVideoPath(videoName);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public void getObbVideoPath(String obbFilePath) {// 先找没加密的，再找有加密的
        if (System.currentTimeMillis() - time < 500) {
            return;
        }
        time = System.currentTimeMillis();
        handler.sendEmptyMessage(1);
        lastVideoName = obbFilePath.split("\\.")[0];
        videoIcon = "." + obbFilePath.split("\\.")[1];
        if (GlobeVariable.File_Path.equals("") && videoPlayUrl != "") {
            playVideo(videoUrl);
            return;
        }
        String aVideoObb = GlobeVariable.File_Path + "/"
                + GlobeVariable.KWHZ_USERSEESION_PATH + "/" + lastVideoName
                + "/" + obbFilePath;
        File file = new File(aVideoObb);
        if (!file.exists()) {
//            if (videoUrl.equals("1"))//专用模拟器的，可以删除
//                GlobeVariable.File_Path = "/mnt/sdcard";
            aVideoObb = GlobeVariable.File_Path + "/"
                    + GlobeVariable.KWHZ_COURSE_PATH + "/"
                    + CommonUitl.getResourceName(this) + "/" + lastVideoName
                    + "/" + lastVideoName + ".obb";
            if (!new File(aVideoObb).exists()) {
                playVideo(videoUrl);
            } else {
                if (!storageManager.isObbMounted(aVideoObb)) {// 判断是否已加载
                    storageManager.mountObb(aVideoObb, null,// obb 文件名
                            new obbListener());
                } else {
                    Log.e("之前已挂载，可以使用", "");
                    playVideo(storageManager.getMountedObbPath(aVideoObb) + "/"
                            + obbFilePath);
                }
            }
        } else {
            playVideo(aVideoObb);
        }
    }

    @SuppressLint("NewApi")
    private class obbListener extends OnObbStateChangeListener {
        @Override
        public void onObbStateChange(String path, int state) {
            super.onObbStateChange(path, state);
            if (state == OnObbStateChangeListener.ERROR_ALREADY_MOUNTED) {
                Log.e("已经挂载", "");
                playVideo(storageManager.getMountedObbPath(path) + "/"
                        + lastVideoName + videoIcon);
            }
            if (state == OnObbStateChangeListener.ERROR_COULD_NOT_MOUNT) {
                Log.e("OBB", "这个OBB不能挂在到系统");
                if (videoUrl != null)
                    playVideo(videoUrl);
            }
            if (state == OnObbStateChangeListener.ERROR_COULD_NOT_UNMOUNT) {
                Log.e("OBB", "这个OBB不能反挂载");
                if (videoUrl != null)
                    playVideo(videoUrl);
            }
            if (state == OnObbStateChangeListener.ERROR_INTERNAL) {
                Log.e("obb", "一个内部的系统错误导致正在重试挂载obb");
                if (videoUrl != null)
                    playVideo(videoUrl);
            }
            if (state == OnObbStateChangeListener.ERROR_NOT_MOUNTED) {
                Log.e("obb", "一个反挂载调用执行时这个obb还没有挂在过");
                if (videoUrl != null)
                    playVideo(videoUrl);
            }
            if (state == OnObbStateChangeListener.ERROR_PERMISSION_DENIED) {
                Log.e("obb", "当前程序没有使用这个obb的权限");
                if (videoUrl != null)
                    playVideo(videoUrl);
            }
            if (state == OnObbStateChangeListener.MOUNTED) {
                Log.e("obb", "The obb容器已经挂载好了，可以使用了");
                playVideo(storageManager.getMountedObbPath(path) + "/"
                        + lastVideoName + videoIcon);
            }
            if (state == OnObbStateChangeListener.UNMOUNTED) {
                Log.e("The OBB容易现在反挂载完成，将无法再使用", "");
            }
            Log.e("obb", "挂载obb文件的路径:::" + path);
            Log.e("obb", "挂载的状态：：：" + state);
        }
    }

    // 动画设置
    private void setAnimations() {
        discAnimation = ObjectAnimator.ofFloat(disc, "rotation", 0, 360);
        discAnimation.setDuration(20000);
        discAnimation.setInterpolator(new LinearInterpolator());
        discAnimation.setRepeatCount(ValueAnimator.INFINITE);
        needleAnimation = ObjectAnimator.ofFloat(needle, "rotation", 0, 25);
        needle.setPivotX(0);
        needle.setPivotY(0);
        needleAnimation.setDuration(800);
        needleAnimation.setInterpolator(new LinearInterpolator());
    }

}
