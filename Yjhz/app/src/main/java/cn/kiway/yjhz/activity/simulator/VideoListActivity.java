package cn.kiway.yjhz.activity.simulator;

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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.kiway.yjhz.R;
import cn.kiway.yjhz.activity.BaseActivity;
import cn.kiway.yjhz.adapter.session.OnSessionAdapter;
import cn.kiway.yjhz.dialog.LoginDialog;
import cn.kiway.yjhz.model.OnClassModel;
import cn.kiway.yjhz.model.VideoModel;
import cn.kiway.yjhz.utils.CommonUitl;
import cn.kiway.yjhz.utils.GlobeVariable;
import cn.kiway.yjhz.utils.IConstant;
import cn.kiway.yjhz.utils.Logger;
import cn.kiway.yjhz.utils.okhttp.HttpRequestUrl;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

@SuppressLint("NewApi")
public class VideoListActivity extends BaseActivity implements MediaPlayer.OnInfoListener,
        OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, AdapterView
                .OnItemClickListener {

    private VideoView videoview;
    private int stopPosition;// 暂停的位置
    public static String lastVideoName;
    public String videoIcon;
    StorageManager storageManager;// 外部数据管理
    TextView blackView;// 变黑
    boolean isPause;
    String videoPlayUrl = "";
    private ObjectAnimator discAnimation, needleAnimation;
    private ImageView disc, needle;
    LoginDialog loginDialog;
    long time;
    List<OnClassModel> Zhukelist = new ArrayList<>();//主课列表
    List<OnClassModel> Weikelist1 = new ArrayList<>();//微课列表
    List<OnClassModel> PlayList = new ArrayList<>();//当前播放视频的列表
    ListView listView;//视频列表
    OnSessionAdapter adapter;//视频列表适配器
    VideoModel videoModel;
    public int position = 0;
    /**
     * 屏幕显示信息
     */
    public DisplayMetrics displayMetrics = new DisplayMetrics();
    Button zhu;
    private MediaController mMediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        iniView();
        videoModel = (VideoModel) getIntent().getExtras()
                .getSerializable(IConstant.BUNDLE_PARAMS);
        initData();
        if (PlayList.size() > 0) {
            getObbVideoPath(PlayList.get(position).getUuid());
            loginDialog = new LoginDialog(this);
            loginDialog.setTitle("正在加载中");
        } else {
            Toast.makeText(this, "该课程没有视频", Toast.LENGTH_SHORT).show();
        }
        findViewById(R.id.layout).setVisibility(View.GONE);//关闭预览
    }

    void initData() {
        JSONObject data = mCache.getAsJSONObject(HttpRequestUrl.GET_ONE_COURSE + videoModel.getId());
        if (data != null) {
            JSONArray ZhukeJs = data.optJSONObject("data").optJSONArray("processList");
            JSONArray WeikeJs = data.optJSONObject("data").optJSONArray("courseTinyList");
            if (ZhukeJs != null) {
                for (int i = 0; i < ZhukeJs.length(); i++) {
                    JSONObject item = ZhukeJs.optJSONObject(i);
                    OnClassModel model = new OnClassModel();
                    model.setUuid(item.optJSONObject("resource").optString("uuid") + "." + item.optJSONObject
                            ("resource").optString("suffix"));
                    model.setContent(item.optString("content"));
                    model.setVideoUrl(item.optJSONObject("resource").optString("file_path"));
                    if (i == 0)
                        model.setBool(true);
                    else
                        model.setBool(false);
                    Zhukelist.add(model);
                }
            }
            if (WeikeJs != null) {
                for (int i = 0; i < WeikeJs.length(); i++) {
                    JSONObject item = WeikeJs.optJSONObject(i);
                    OnClassModel model = new OnClassModel();
                    model.setUuid(item.optJSONObject("resource").optString("uuid") + "." + item.optJSONObject
                            ("resource").optString("suffix"));
                    model.setContent(item.optJSONObject("resource").optString("name"));
                    model.setVideoUrl(item.optJSONObject("resource").optString("file_path"));
                    model.setBool(false);
                    Weikelist1.add(model);
                }
            }
        }
        if (Zhukelist.size() > 0) {
            PlayList = Zhukelist;
            adapter.isZhuke = true;
        } else {
            adapter.isZhuke = false;
            PlayList = Weikelist1;
        }
        if (Weikelist1.size() <= 0)
            findViewById(R.id.buttonPanel).setVisibility(View.GONE);
        adapter.PlayList = PlayList;
        adapter.notifyDataSetChanged();
    }


    //切换主课微课
    public void ZhuKe(View view) {
        if (adapter.isZhuke)
            return;
        adapter.isZhuke = true;
        adapter.PlayList = Zhukelist;
        adapter.notifyDataSetChanged();
        moveFrontBg(findViewById(R.id.view),
                findViewById(R.id.view).getWidth(), 0, 0, 0);
    }

    //切换主课微课
    public void WeiKe(View view) {
        if (!adapter.isZhuke)
            return;
        adapter.isZhuke = false;
        adapter.PlayList = Weikelist1;
        adapter.notifyDataSetChanged();
        moveFrontBg(findViewById(R.id.view), 0,
                findViewById(R.id.view).getWidth(), 0, 0);
    }

    private void iniView() {
        blackView = (TextView) findViewById(R.id.black);
        videoview = (VideoView) findViewById(R.id.surface_view);
        disc = (ImageView) findViewById(R.id.disc);
        needle = (ImageView) findViewById(R.id.needle);
        listView = (ListView) findViewById(R.id.listview);
        zhu = (Button) findViewById(R.id.zhuke);
        zhu.setFocusable(true);
        zhu.setFocusableInTouchMode(true);
        zhu.requestFocus();
        zhu.requestFocusFromTouch();
        adapter = new OnSessionAdapter(this, new ArrayList<OnClassModel>(), true);
        listView.setAdapter(adapter);
        blackView.setAlpha(0);
        listView.setOnItemClickListener(this);
        listView.setSelected(true);
        videoview.setShowErrorDialog(false);
        videoview.setOnCompletionListener(this);
        videoview.setOnErrorListener(this);
        videoview.setOnPreparedListener(this);
        videoview.setOnInfoListener(this);
        mMediaController = new MediaController(this);//实例化控制器
        mMediaController.show(5000);//控制器显示5s后自动隐藏
        videoview.setMediaController(mMediaController);//绑定控制器
        setAnimations();
    }

    public void ShowLayout(View view) {
        if (findViewById(R.id.layout).getVisibility() == View.VISIBLE) {
            findViewById(R.id.layout).setVisibility(View.GONE);
            videoview.setLayoutParams(new RelativeLayout.LayoutParams(displayMetrics.widthPixels,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
        } else {
            findViewById(R.id.layout).setVisibility(View.VISIBLE);
            videoview.setLayoutParams(new RelativeLayout.LayoutParams(displayMetrics.widthPixels - (int) getResources
                    ().getDimension(R.dimen._300dp),
                    RelativeLayout.LayoutParams.MATCH_PARENT));
        }
    }

    public void setDarkScreen() { // 暗屏
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                blackView.setAlpha(1);
            }
        });
    }

    public void setShineScreen() {// 亮屏
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listView.setSelection(position);
                            listView.setFocusable(true);
                            listView.setFocusableInTouchMode(true);
                            listView.requestFocus();
                            listView.requestFocusFromTouch();
                            if (findViewById(R.id.layout).getVisibility() == View.VISIBLE) {
                                videoview.setLayoutParams(new RelativeLayout.LayoutParams(displayMetrics.widthPixels
                                        - (int) getResources().getDimension(R.dimen._300dp),
                                        RelativeLayout.LayoutParams.MATCH_PARENT));
                            } else {
                                videoview.setLayoutParams(new RelativeLayout.LayoutParams(displayMetrics.widthPixels,
                                        RelativeLayout.LayoutParams.MATCH_PARENT));
                            }
                        }
                    }, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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
            playVideo(PlayList.get(position).getVideoUrl());
            return;
        }
        String aVideoObb = GlobeVariable.File_Path + "/"
                + GlobeVariable.KWHZ_USERSEESION_PATH + "/" + lastVideoName
                + "/" + obbFilePath;
        File file = new File(aVideoObb);
        if (!file.exists()) {
            if (CommonUitl.getLocalEthernetMacAddress() == null)//专用模拟器的，可以删除
                GlobeVariable.File_Path = "/mnt/sdcard";
            aVideoObb = GlobeVariable.File_Path + "/"
                    + GlobeVariable.KWHZ_COURSE_PATH + "/"
                    + CommonUitl.getResourceName(this) + "/" + lastVideoName
                    + "/" + lastVideoName + ".obb";
            if (!new File(aVideoObb).exists()) {
                playVideo(PlayList.get(position).getVideoUrl());
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

    //视频播放信息
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

    @Override
    public void onCompletion(MediaPlayer arg0) {
        loginDialog.close();
        onCompelte();
    }

    void onCompelte() {
        if (position < PlayList.size() - 1) {
            position = position + 1;
            getObbVideoPath(PlayList.get(position).getUuid());
            findViewById(R.id.videocom).setVisibility(View.GONE);
        } else {
            if (adapter.isZhuke && Weikelist1.size() > 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(VideoListActivity.this, "切换到微课了", Toast.LENGTH_SHORT).show();
                    }
                });
                adapter.PlayList = PlayList = Weikelist1;
                adapter.isZhuke = false;
                adapter.notifyDataSetChanged();
                position = 0;
                getObbVideoPath(PlayList.get(position).getUuid());
                findViewById(R.id.videocom).setVisibility(View.GONE);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(VideoListActivity.this, "已经是最后一个了", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.setVideoChroma(-1);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setPlaybackSpeed(1.0f);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        PlayList = adapter.PlayList;
        position = i;
        getObbVideoPath(PlayList.get(i).getUuid());
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
                if (PlayList.get(position).getVideoUrl() != null)
                    playVideo(PlayList.get(position).getVideoUrl());
            }
            if (state == OnObbStateChangeListener.ERROR_COULD_NOT_UNMOUNT) {
                Log.e("OBB", "这个OBB不能反挂载");
                if (PlayList.get(position).getVideoUrl() != null)
                    playVideo(PlayList.get(position).getVideoUrl());
            }
            if (state == OnObbStateChangeListener.ERROR_INTERNAL) {
                Log.e("obb", "一个内部的系统错误导致正在重试挂载obb");
                if (PlayList.get(position).getVideoUrl() != null)
                    playVideo(PlayList.get(position).getVideoUrl());
            }
            if (state == OnObbStateChangeListener.ERROR_NOT_MOUNTED) {
                Log.e("obb", "一个反挂载调用执行时这个obb还没有挂在过");
                if (PlayList.get(position).getVideoUrl() != null)
                    playVideo(PlayList.get(position).getVideoUrl());
            }
            if (state == OnObbStateChangeListener.ERROR_PERMISSION_DENIED) {
                Log.e("obb", "当前程序没有使用这个obb的权限");
                if (PlayList.get(position).getVideoUrl() != null)
                    playVideo(PlayList.get(position).getVideoUrl());
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (findViewById(R.id.layout).getVisibility() == View.VISIBLE) {
                findViewById(R.id.layout).setVisibility(View.GONE);
                videoview.setLayoutParams(new RelativeLayout.LayoutParams(displayMetrics.widthPixels,
                        RelativeLayout.LayoutParams.MATCH_PARENT));
                return true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (findViewById(R.id.layout).getVisibility() == View.VISIBLE) {
                findViewById(R.id.layout).setVisibility(View.GONE);
                videoview.setLayoutParams(new RelativeLayout.LayoutParams(displayMetrics.widthPixels,
                        RelativeLayout.LayoutParams.MATCH_PARENT));
            } else {
                zhu.setFocusable(true);
                zhu.setFocusableInTouchMode(true);
                zhu.requestFocus();
                zhu.requestFocusFromTouch();
                findViewById(R.id.layout).setVisibility(View.VISIBLE);
                videoview.setLayoutParams(new RelativeLayout.LayoutParams(displayMetrics.widthPixels - (int)
                        getResources().getDimension(R.dimen._300dp),
                        RelativeLayout.LayoutParams.MATCH_PARENT));
            }
            return true;
        }
        if (findViewById(R.id.layout).getVisibility() == View.GONE) {
            Logger.log("**************" + keyCode);
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                setShineScreen();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                setDarkScreen();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (position - 1 > 0) {
                    position--;
                } else {
                    if (adapter.isZhuke) {
                        position = 0;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(VideoListActivity.this, "已经是第一个了", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(VideoListActivity.this, "切换到主课了", Toast.LENGTH_SHORT).show();
                            }
                        });
                        adapter.isZhuke=true;
                        adapter.PlayList = PlayList = Zhukelist;
                        adapter.notifyDataSetChanged();
                        position = Zhukelist.size() - 1;
                        getObbVideoPath(PlayList.get(position).getUuid());
                        findViewById(R.id.videocom).setVisibility(View.GONE);
                    }
                }
                getObbVideoPath(PlayList.get(position).getUuid());
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                onCompelte();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (videoview.isPlaying())
                    pauseVideo();
                else
                    resumeVideo();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 控件的位移动画
     */
    void moveFrontBg(View v, int startX, int toX, int startY,
                     int toY) {
        TranslateAnimation anim = new TranslateAnimation(startX, toX, startY,
                toY);
        anim.setDuration(200);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }

}
