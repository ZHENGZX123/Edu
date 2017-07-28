package cn.kiway.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.common.PPTViewer;
import cn.kiway.dialog.ChooseVideoDialog;
import cn.kiway.dialog.ChooseVideoDialog.ChooseVideoCallBack;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.dialog.LoginDialog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.VideoModel;
import cn.kiway.model.VideoPlayModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class PlayVideoActivity extends BaseNetWorkActicity implements
		MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
		MediaPlayer.OnErrorListener, OnBufferingUpdateListener,
		OnClickListener, ChooseVideoCallBack, ImageLoadingListener {

	/**
	 * surfaceView
	 */
	private SurfaceView surfaceView;

	/**
	 * surfaceView播放控制
	 */
	private SurfaceHolder surfaceHolder;

	/**
	 * 播放控制条
	 */
	private SeekBar seekBar;

	/**
	 * 暂停播放按钮
	 */
	private ImageView playButton;

	/**
	 * 重新播放按钮
	 */
	private ImageView replayButton;


	/**
	 * 播放视频
	 */
	private MediaPlayer mediaPlayer;

	/**
	 * seekBar是否自动拖动
	 */
	private boolean seekBarAutoFlag = false;

	/**
	 * 视频时间显示
	 */
	private TextView vedioTiemTextView;

	/**
	 * 播放总时间
	 */
	private String videoTimeString;

	private long videoTimeLong;

	/**
	 * 视频模型
	 * */
	VideoModel model;
	/**
	 * 视频数据列表
	 * */
	List<VideoPlayModel> list = new ArrayList<VideoPlayModel>();
	/**
	 * 视频选择弹框
	 * */
	ChooseVideoDialog chooseVideoDialog;
	/**
	 * 当前播放的视频地址
	 * */
	String videoUrl;
	/**
	 * 当前选择视频列表的位置
	 * */
	int position = 0;
	/**
	 * 展示视频列表
	 * */
	ImageView showDialog;
	/**
	 * 是否显示进度条
	 * */
	boolean isShow = false;
	/**
	 * ppt
	 * */
	PPTViewer pptViewer;
	/**
	 * 图像
	 * */
	ImageView view;
	LoginDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!AppUtil.isNetworkAvailable(this)) {
			newWorkdialog = new IsNetWorkDialog(context, this,
					resources.getString(R.string.dqsjmylrhlwqljhlwl),
					resources.getString(R.string.ljhlw));
			if (newWorkdialog != null && !newWorkdialog.isShowing()) {
				newWorkdialog.show();
				return;
			}
		}
		model = (VideoModel) bundle.getSerializable(IConstant.BUNDLE_PARAMS);
		setContentView(R.layout.activity_surface_view);
		initViews();
		isShow = true;
	}

	@SuppressWarnings("deprecation")
	public void initViews() {
		// 初始化控件
		surfaceView = ViewUtil.findViewById(this, R.id.surfaceView);
		pptViewer = ViewUtil.findViewById(this, R.id.pptviewer);
	
		seekBar = ViewUtil.findViewById(this, R.id.seekbar);
		playButton = ViewUtil.findViewById(this, R.id.button_play);
		replayButton = ViewUtil.findViewById(this, R.id.button_replay);
		vedioTiemTextView = ViewUtil.findViewById(this, R.id.textView_showTime);
		showDialog = ViewUtil.findViewById(this, R.id.show_dialog);
		view = ViewUtil.findViewById(this, R.id.img);
		// 设置surfaceHolder
		surfaceHolder = surfaceView.getHolder();
		// 设置Holder类型,该类型表示surfaceView自己不管理缓存区,虽然提示过时，但最好还是要设置
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// 设置surface回调
		surfaceHolder.addCallback(new SurfaceCallback());
		showDialog.setOnClickListener(this);
		findViewById(R.id.ppt_last).setOnClickListener(this);
		findViewById(R.id.ppt_next).setOnClickListener(this);
		findViewById(R.id.video_zl).setOnClickListener(this);
		
		dialog = new LoginDialog(this);
		dialog.setTitle("玩命加载中");
		dialog.mLoadingDialog.setCancelable(false);
		AppUtil.playPosition = 0;
	}

	// SurfaceView的callBack
	private class SurfaceCallback implements SurfaceHolder.Callback {
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		public void surfaceCreated(SurfaceHolder holder) {
			// surfaceView被创建 请求数据
			try {
				loadData();
				// playVideo("\\yjhz_upload\\resource\\925db58d2382443fb720754707541c2a.mp4");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// surfaceView销毁,同时销毁mediaPlayer
			if (null != mediaPlayer) {
				mediaPlayer.release();
				mediaPlayer = null;
			}
		}
	}

	@Override
	public void loadData() throws Exception {
		super.loadData();
		Map<String, String> map = new HashMap<>();
		map.put("lessonId", model.getLessionId() + "");
		IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.GET_VIDEO_URL, map,
				activityHandler);
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.GET_VIDEO_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				JSONArray array = data.optJSONArray("sectinList");
				list.clear();
				for (int i = 0; i < array.length(); i++) {
					JSONObject item = array.optJSONObject(i);
					VideoPlayModel model = new VideoPlayModel();
					model.setContent(item.optJSONObject("resource").optString(
							"name"));// 视频的名字
					model.setUrl(item.optJSONObject("resource").optString(
							"file_path"));// 视频的地址
					model.setIcon(item.optJSONObject("resource").optString(
							"suffix"));
					list.add(model);
				}
				if (list.size() > 1) {
					play(list.get(0).getUrl());
					chooseVideoDialog = new ChooseVideoDialog(this, list, this);
					if (chooseVideoDialog != null
							&& !chooseVideoDialog.isShowing()) {
						chooseVideoDialog.show();
					}
				} else {
					position = 0;
					play(list.get(0).getUrl());
					findViewById(R.id.show_dialog).setVisibility(View.GONE);
					findViewById(R.id.button_next).setVisibility(View.GONE);
				}
			}
		}
	}

	@Override
	public void chooseVideolBack(int position) throws Exception {
		this.position = position;
		play(list.get(position).getUrl());
	}

	public void play(String url) {
		if (list.size() == 0)
			return;
		ViewUtil.setContent(this, R.id.title, list.get(position).getContent());
		if (list.get(position).getIcon().equals("mp4")) {
			playVideo(url);
		} else if (list.get(position).getIcon().equals("ppt")) {
			playPpt(url);
		} else if (list.get(position).getIcon().equals("png")
				|| list.get(position).getIcon().equals("jpeg")
				|| list.get(position).getIcon().equals("jpg")) {
			playImg(url);
		}
	}

	/**
	 * 播放ppt
	 * */
	public void playPpt(String url) {
		if (mediaPlayer != null) {
			mediaPlayer.pause();
			mediaPlayer.release();
			mediaPlayer = null;
			AppUtil.playPosition = 0;
		}
		dialog.show();
		findViewById(R.id.videoplay).setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		findViewById(R.id.pptview).setVisibility(View.VISIBLE);
		videoUrl = url;
		final String s = StringUtil.imgUrl(this, url);
		final String pptPath = AppUtil.downloadVideo(s);
		final File f = new File(pptPath);
		IConstant.executorService.execute(new Runnable() {
			@Override
			public void run() {
				if (!f.exists())
					ViewUtil.downloadFile(f, s, PlayVideoActivity.this, null);
				runOnUiThread(new Runnable() {
					public void run() {
						try {
							pptViewer.loadPPT(PlayVideoActivity.this, pptPath);
							dialog.mLoadingDialog.dismiss();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
	}

	/**
	 * 播放视频
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void playVideo(String url) {
		if (ViewUtil.getContent(this, R.id.video_zl).equals(
				resources.getString(R.string.gaoqing))) {
		}
		handler.sendEmptyMessageDelayed(0, 7000);
		dialog.show();
		findViewById(R.id.videoplay).setVisibility(View.VISIBLE);
		findViewById(R.id.pptview).setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		videoUrl = url;
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
		// 初始化MediaPlayer
		mediaPlayer = new MediaPlayer();
		// 重置mediaPaly,建议在初始滑mediaplay立即调用。
		mediaPlayer.reset();
		// 设置声音效果
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		// 设置播放完成监听
		mediaPlayer.setOnCompletionListener(this);
		// 设置媒体加载完成以后回调函数。
		mediaPlayer.setOnPreparedListener(this);
		// 错误监听回调函数
		mediaPlayer.setOnErrorListener(this);
		// 设置缓存变化监听
		mediaPlayer.setOnBufferingUpdateListener(this);
		try {
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("cookie", "JSESSIONID=" + app.getCookie().getValue());
			mediaPlayer.setDataSource(this,
					Uri.parse(StringUtil.imgUrl(this, url)), headers);
			mediaPlayer.prepareAsync();
			System.out.println("播放地址：：：：：：：：：：：：：："
					+ StringUtil.imgUrl(this, url));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 播放图片
	 * */
	public void playImg(String url) {
		if (mediaPlayer != null) {
			mediaPlayer.pause();
			mediaPlayer.release();
			mediaPlayer = null;
			AppUtil.playPosition = 0;
		}
		findViewById(R.id.videoplay).setVisibility(View.GONE);
		findViewById(R.id.pptview).setVisibility(View.GONE);
		dialog.mLoadingDialog.dismiss();
		view.setVisibility(View.VISIBLE);
		imageLoader.displayImage(StringUtil.imgUrl(this, url), view,
				fadeOptions);
	}

	/**
	 * 视频加载完毕监听
	 * 
	 * @param mp
	 */
	@Override
	public void onPrepared(MediaPlayer mp) {
		// 播放视频
		mediaPlayer.start();
		// 设置显示到屏幕
		mediaPlayer.setDisplay(surfaceHolder);
		// 开启线程 刷新进度条
		new Thread(runnable).start();
		// 设置surfaceView保持在屏幕上
		mediaPlayer.setScreenOnWhilePlaying(true);
		// 屏幕一直保持为开启状态
		surfaceHolder.setKeepScreenOn(true);
		// 当视频加载完毕以后，隐藏加载进度条
		dialog.mLoadingDialog.dismiss();
		// 判断是否有保存的播放位置,防止屏幕旋转时，界面被重新构建，播放位置丢失。
		if (AppUtil.playPosition >= 0) {
			mediaPlayer.seekTo(AppUtil.playPosition);
			AppUtil.playPosition = -1;
			// surfaceHolder.unlockCanvasAndPost(AppUtil.getCanvas());
		}
		seekBarAutoFlag = true;
		// 设置控制条,放在加载完成以后设置，防止获取getDuration()错误
		seekBar.setMax(mediaPlayer.getDuration());
		// 设置播放时间
		videoTimeLong = mediaPlayer.getDuration();
		videoTimeString = getShowTime(videoTimeLong);
		vedioTiemTextView.setText("00:00:00/" + videoTimeString);
		// 设置拖动监听事件
		seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
		// 设置按钮监听事件
		// 重新播放
		replayButton.setOnClickListener(this);
		// 暂停和播放
		playButton.setOnClickListener(this);
		surfaceView.setOnClickListener(this);
		// 上一首
		findViewById(R.id.button_last).setOnClickListener(this);
		// 下一首
		findViewById(R.id.button_next).setOnClickListener(this);
		findViewById(R.id.previos).setOnClickListener(this);
	}

	/**
	 * 滑动条变化线程
	 */
	private Runnable runnable = new Runnable() {

		public void run() {
			// 增加对异常的捕获，防止在判断mediaPlayer.isPlaying的时候，报IllegalStateException异常
			try {
				while (seekBarAutoFlag) {
					/*
					 * mediaPlayer不为空且处于正在播放状态时，使进度条滚动。
					 * 通过指定类名的方式判断mediaPlayer防止状态发生不一致
					 */
					if (null != PlayVideoActivity.this.mediaPlayer
							&& PlayVideoActivity.this.mediaPlayer.isPlaying()) {
						seekBar.setProgress(mediaPlayer.getCurrentPosition());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * seekBar拖动监听类
	 * 
	 * @author shenxiaolei
	 */
	private class SeekBarChangeListener implements OnSeekBarChangeListener {

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (progress >= 0) {
				// 如果是用户手动拖动控件，则设置视频跳转。
				if (fromUser) {
					mediaPlayer.seekTo(progress);
				}
				// 设置当前播放时间
				vedioTiemTextView.setText(getShowTime(progress) + "/"
						+ videoTimeString);
			}
		}

		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		public void onStopTrackingTouch(SeekBar seekBar) {

		}

	}

	/**
	 * 按钮点击事件监听
	 */
	@SuppressLint("NewApi")
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_replay:
			seekBarAutoFlag = true;
			new Thread(runnable).start();
			// mediaPlayer不空，则直接跳转
			if (null != mediaPlayer) {
				// MediaPlayer和进度条都跳转到开始位置
				mediaPlayer.seekTo(0);
				seekBar.setProgress(0);
				// 如果不处于播放状态，则开始播放
				if (!mediaPlayer.isPlaying()) {
					mediaPlayer.start();
				}
			} else {
				// 为空则重新设置mediaPlayer
				play(videoUrl);
			}
			findViewById(R.id.button_replay).setVisibility(View.GONE);
			findViewById(R.id.button_play).setVisibility(View.VISIBLE);
			break;
		// 播放、暂停按钮
		case R.id.button_play:
			if (null != mediaPlayer) {
				// 正在播放
				if (mediaPlayer.isPlaying()) {
					AppUtil.playPosition = mediaPlayer.getCurrentPosition();
					// seekBarAutoFlag = false;
					mediaPlayer.pause();
					playButton.setBackground(resources.getDrawable(
							R.drawable.ic_av_play_arrow, null));
				} else {
					if (AppUtil.playPosition >= 0) {
						// seekBarAutoFlag = true;
						mediaPlayer.seekTo(AppUtil.playPosition);
						mediaPlayer.start();
						playButton.setBackground(resources.getDrawable(
								R.drawable.ic_av_pause, null));
						AppUtil.playPosition = -1;
					}
				}
			}
			break;
		case R.id.surfaceView:
			if (findViewById(R.id.control).getVisibility() == View.VISIBLE) {
				findViewById(R.id.control).setVisibility(View.GONE);
				findViewById(R.id.show).setVisibility(View.GONE);
			} else {
				findViewById(R.id.control).setVisibility(View.VISIBLE);
				findViewById(R.id.show).setVisibility(View.VISIBLE);
				if (list.size() > 2)
					findViewById(R.id.show_dialog).setVisibility(View.VISIBLE);
				handler.removeMessages(0);
				handler.sendEmptyMessageDelayed(0, 7000);
			}
			break;
		case R.id.button_last:// 播放下一个
			if (position - 1 < 0) {
				ViewUtil.showMessage(this, "已经是第一个视频了");
				return;
			}
			position = position - 1;
			play(list.get(position).getUrl());
			break;
		case R.id.button_next:// 播放上一个
			if (position + 2 > list.size()) {
				ViewUtil.showMessage(this, "已经是最后一个视频");
				return;
			}
			position = position + 1;
			play(list.get(position).getUrl());
			break;
		case R.id.show_dialog:
			if (chooseVideoDialog != null && !chooseVideoDialog.isShowing()) {
				chooseVideoDialog.show();
				isShow = false;
			}
			break;
		case R.id.ppt_next:
			pptViewer.next();
			break;
		case R.id.ppt_last:
			pptViewer.prev();
			break;
		case R.id.previos:
			finish();
			break;
		case R.id.video_zl:
			if (ViewUtil.getContent(this, R.id.video_zl).equals(
					resources.getString(R.string.biaoqing))) {
				ViewUtil.setContent(this, R.id.video_zl, R.string.gaoqing);
			} else {
				ViewUtil.setContent(this, R.id.video_zl, R.string.biaoqing);
			}
			break;
		}
	}

	/**
	 * 播放完毕监听
	 * 
	 * @param mp
	 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		if (list.size() == 1 || list.size() == 0) {
			// 设置seeKbar跳转到最后位置
			seekBar.setProgress(Integer.parseInt(String.valueOf(videoTimeLong)));
			// 设置播放标记为false
			seekBarAutoFlag = false;
			findViewById(R.id.button_replay).setVisibility(View.VISIBLE);
			findViewById(R.id.button_play).setVisibility(View.GONE);
		} else {
			if (position + 2 > list.size()) {
				if (chooseVideoDialog != null && !chooseVideoDialog.isShowing())
					chooseVideoDialog.show();
			} else {
				position = position + 1;
				play(list.get(position).getUrl());
			}
		}
	}

	/**
	 * 视频缓存大小监听,当视频播放以后 在started状态会调用
	 */
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// percent 表示缓存加载进度，0为没开始，100表示加载完成，在加载完成以后也会一直调用该方法
		Log.e("text", "onBufferingUpdate-->" + percent);
		// 可以根据大小的变化来
	}

	/**
	 * 错误监听
	 * 
	 * @param mp
	 * @param what
	 * @param extra
	 * @return
	 */
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_ERROR_UNKNOWN:// 未知错误
			ViewUtil.showMessage(this, "视频错误");
			break;
		case MediaPlayer.MEDIA_ERROR_SERVER_DIED:// 视频流错误
			ViewUtil.showMessage(this, "未知错误");
			break;
		default:
			break;
		}

		switch (extra) {
		case MediaPlayer.MEDIA_ERROR_IO:
			ViewUtil.showMessage(this, "视频流错误");
			break;
		case MediaPlayer.MEDIA_ERROR_MALFORMED:
			ViewUtil.showMessage(this, "未知错误");
			break;
		case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
			ViewUtil.showMessage(this, "不支持连续播放");
			break;
		case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
			ViewUtil.showMessage(this, "请求超时");
			break;
		case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
			ViewUtil.showMessage(this, "不支持播放该视频");
			break;
		}
		return false;
	}

	/**
	 * 从暂停中恢复
	 */
	protected void onResume() {
		super.onResume();
		// 判断播放位置
		if (AppUtil.playPosition >= 0) {
			if (null != mediaPlayer) {
				seekBarAutoFlag = true;
				mediaPlayer.seekTo(AppUtil.playPosition);
				mediaPlayer.start();
			} else {
				play(videoUrl);
			}
		}
	}

	/**
	 * 页面处于暂停状态
	 */
	@Override
	protected void onPause() {
		super.onPause();
		try {
			if (null != mediaPlayer && mediaPlayer.isPlaying()) {
				AppUtil.playPosition = mediaPlayer.getCurrentPosition();
				mediaPlayer.pause();
				seekBarAutoFlag = false;
			}
			if (chooseVideoDialog.isShowing() && chooseVideoDialog != null)
				chooseVideoDialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发生屏幕旋转时调用
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (null != mediaPlayer) {
			// 保存播放位置
			AppUtil.playPosition = mediaPlayer.getCurrentPosition();
		}
	}

	/**
	 * 屏幕旋转完成时调用
	 */
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * 屏幕销毁时调用
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 由于MediaPlay非常占用资源，所以建议屏幕当前activity销毁时，则直接销毁
		try {
			if (null != PlayVideoActivity.this.mediaPlayer) {
				// 提前标志为false,防止在视频停止时，线程仍在运行。
				seekBarAutoFlag = false;
				// 如果正在播放，则停止。
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				AppUtil.playPosition = -1;
				// 释放mediaPlayer
				PlayVideoActivity.this.mediaPlayer.release();
				PlayVideoActivity.this.mediaPlayer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 转换播放时间
	 * 
	 * @param milliseconds
	 *            传入毫秒值
	 * @return 返回 hh:mm:ss或mm:ss格式的数据
	 */
	@SuppressLint("SimpleDateFormat")
	public String getShowTime(long milliseconds) {
		// 获取日历函数
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliseconds);
		SimpleDateFormat dateFormat = null;
		// 判断是否大于60分钟，如果大于就显示小时。设置日期格式
		if (milliseconds / 60000 > 60) {
			dateFormat = new SimpleDateFormat("hh:mm:ss");
		} else {
			dateFormat = new SimpleDateFormat("mm:ss");
		}
		return dateFormat.format(calendar.getTime());
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (msg.what == 0) {
				if (findViewById(R.id.control).getVisibility() == View.VISIBLE) {
					findViewById(R.id.control).setVisibility(View.GONE);
				}
				if (findViewById(R.id.show).getVisibility() == View.VISIBLE) {
					findViewById(R.id.show).setVisibility(View.GONE);
				}
				if (chooseVideoDialog != null) {
					if (chooseVideoDialog.isShowing() && isShow) {
						chooseVideoDialog.dismiss();
						isShow = false;
					}
				}

			}

		};
	};

	@Override
	public void onLoadingStarted(String imageUri, View view) {

	}

	@Override
	public void onLoadingFailed(String imageUri, View view,
			FailReason failReason) {

	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		dialog.mLoadingDialog.dismiss();
	}

	@Override
	public void onLoadingCancelled(String imageUri, View view) {

	}
}
