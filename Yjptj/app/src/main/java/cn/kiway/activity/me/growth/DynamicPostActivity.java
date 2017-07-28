package cn.kiway.activity.me.growth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import uk.co.senab.photoview.widget.AlbumViewPager;
import uk.co.senab.photoview.widget.FilterImageView;
import uk.co.senab.photoview.widget.LocalImageHelper;
import uk.co.senab.photoview.widget.MatrixImageView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.dialog.ChooseVideoDialog;
import cn.kiway.dialog.ChooseVideoDialog.ChooseVideoCallBack;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.dialog.LoginDialog;
import cn.kiway.http.UploadFile;
import cn.kiway.http.UploadFile.UploadCallBack;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * @Description:发布动态界面
 */
public class DynamicPostActivity extends BasePhotoActivity implements
		MatrixImageView.OnSingleTapListener, TextWatcher, UploadCallBack,
		ChooseVideoCallBack {

	private View mSend;// 发送
	public EditText mContent;// 动态内容编辑框
	private InputMethodManager imm;// 软键盘管理
	private TextView textRemain;// 字数提示
	private TextView picRemain;// 图片数量提示
	private ImageView add;// 添加图片按钮
	private LinearLayout picContainer;// 图片容器
	private List<LocalImageHelper.LocalFile> pictures = new ArrayList<>();// 图片路径数组
	HorizontalScrollView scrollView;// 滚动的图片容器
	View editContainer;// 动态编辑部分
	View pagerContainer;// 图片显示部分
	// 显示大图的viewpager 集成到了Actvity中 下面是和viewpager相关的控件
	AlbumViewPager viewpager;// 大图显示pager
	ImageView mBackView;// 返回/关闭大图
	TextView mCountView;// 大图数量提示
	View mHeaderBar;// 大图顶部栏
	ImageView delete;// 删除按钮

	int size;// 小图大小
	int padding;// 小图间距
	DisplayImageOptions options;
	public static DynamicPostActivity instance = null;
	LoginDialog dialog;
	List<String> list = new ArrayList<String>();
	ChooseVideoDialog dialog2;
	int ispublic = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_dynamic);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		instance = this;
		list.add("公开");
		list.add("不公开");
		dialog2 = new ChooseVideoDialog(this, this, list);
		try {
			initViews();
			initData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description： 初始化Views
	 */
	void initViews() throws Exception {
		mSend = findViewById(R.id.post_send);
		mContent = ViewUtil.findViewById(this, R.id.post_content);
		textRemain = ViewUtil.findViewById(this, R.id.post_text_remain);
		picRemain = ViewUtil.findViewById(this, R.id.post_pic_remain);
		add = ViewUtil.findViewById(this, R.id.post_add_pic);
		picContainer = ViewUtil.findViewById(this, R.id.post_pic_container);
		scrollView = ViewUtil.findViewById(this, R.id.post_scrollview);
		viewpager = ViewUtil.findViewById(this, R.id.albumviewpager);
		mBackView = ViewUtil.findViewById(this, R.id.header_bar_photo_back);
		mCountView = ViewUtil.findViewById(this, R.id.header_bar_photo_count);
		mHeaderBar = findViewById(R.id.album_item_header_bar);
		delete = ViewUtil.findViewById(this, R.id.header_bar_photo_delete);
		editContainer = ViewUtil.findViewById(this, R.id.post_edit_container);
		pagerContainer = findViewById(R.id.pagerview);
		delete.setVisibility(View.VISIBLE);
		viewpager.setOnPageChangeListener(pageChangeListener);
		viewpager.setOnSingleTapListener(this);
		mBackView.setOnClickListener(this);
		mCountView.setOnClickListener(this);
		mSend.setOnClickListener(this);
		add.setOnClickListener(this);
		delete.setOnClickListener(this);
		mContent.addTextChangedListener(this);
		findViewById(R.id.previos).setOnClickListener(this);
		findViewById(R.id.gk).setOnClickListener(this);
	}

	void initData() throws Exception {
		size = (int) getResources().getDimension(R.dimen._100dp);
		padding = (int) getResources().getDimension(R.dimen._10dp);
		LocalImageHelper.getInstance().setResultOk(false);
		// 获取选中的图片
		List<LocalImageHelper.LocalFile> files = LocalImageHelper.getInstance()
				.getCheckedItems();
		for (int i = 0; i < files.size(); i++) {
			LayoutParams params = new LayoutParams(size, size);
			params.rightMargin = padding;
			FilterImageView imageView = new FilterImageView(this);
			imageView.setLayoutParams(params);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			// /修改的地方
			imageLoader.displayImage(files.get(i).getThumbnailUri(),
					new ImageViewAware(imageView));
			imageView.setOnClickListener(this);
			pictures.add(files.get(i));
			if (pictures.size() == 9) {
				add.setVisibility(View.GONE);
			} else {
				add.setVisibility(View.VISIBLE);
			}
			picContainer.addView(imageView, picContainer.getChildCount() - 1);
			picRemain.setText(pictures.size() + "/9");
			LocalImageHelper.getInstance().setCurrentSize(pictures.size());
		}
		// 设置当前选中的图片数量
		LocalImageHelper.getInstance().setCurrentSize(0);
		// 延迟滑动至最右边
		new Handler().postDelayed(new Runnable() {
			public void run() {
				scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
			}
		}, 50L);
		dialog = new LoginDialog(this);
		dialog.setTitle("玩命上传中");
	}

	@Override
	public void onBackPressed() {
		if (pagerContainer.getVisibility() != View.VISIBLE) {
			// showSaveDialog();
		} else {
			hideViewPager();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		// showSaveDialog();
		case R.id.header_bar_photo_back:
		case R.id.header_bar_photo_count:
			hideViewPager();
			break;
		case R.id.header_bar_photo_delete:
			final int index = viewpager.getCurrentItem();
			pictures.remove(index);
			if (pictures.size() == 9) {
				add.setVisibility(View.GONE);
			} else {
				add.setVisibility(View.VISIBLE);
			}
			if (pictures.size() == 0) {
				hideViewPager();
			}
			List<LocalImageHelper.LocalFile> files = LocalImageHelper
					.getInstance().getCheckedItems();
			picContainer.removeView(picContainer.getChildAt(index));
			picRemain.setText(pictures.size() + "/9");
			mCountView.setText((viewpager.getCurrentItem() + 1) + "/"
					+ pictures.size());
			viewpager.getAdapter().notifyDataSetChanged();
			LocalImageHelper.getInstance().setCurrentSize(0);
			files.remove(index);
			break;
		case R.id.post_send:
			if (!AppUtil.isNetworkAvailable(this)) {
				newWorkdialog = new IsNetWorkDialog(context, this,
						resources.getString(R.string.dqsjmylrhlwqljhlwl),
						resources.getString(R.string.ljhlw));
				if (newWorkdialog != null && !newWorkdialog.isShowing()) {
					newWorkdialog.show();
					return;
				}
			}
			if (!AppUtil.isWifiActive(this)
					&& SharedPreferencesUtil.getBoolean(this, IConstant.WIFI)) {
				ViewUtil.showMessage(this, R.string.dqwlhjbs);
				return;
			}
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			String content = mContent.getText().toString();
			if (StringUtil.isEmpty(content) /* && pictures.isEmpty() */) {
				Toast.makeText(
						this,
						resources
								.getString(R.string.pelease_add_content_or_img),
						Toast.LENGTH_SHORT).show();
				return;
			} else {
				try {
					// 设置为不可点击，防止重复提交
					if (dialog != null && !dialog.isShowing())
						dialog.show();
					new Thread(networkTask).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.post_add_pic:
			Intent intent = new Intent(DynamicPostActivity.this,
					LocalAlbumListActivity.class);
			startActivity(intent);
			break;
		case R.id.previos:
			finish();
			break;
		case R.id.gk:
			if (dialog2 != null && !dialog2.isShowing()) {
				dialog2.show();
			}
			break;
		default:
			if (view instanceof FilterImageView) {
				for (int i = 0; i < picContainer.getChildCount(); i++) {
					if (view == picContainer.getChildAt(i)) {
						showViewPager(i);
					}
				}
			}
			break;
		}
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			if (viewpager.getAdapter() != null) {
				String text = (position + 1) + "/"
						+ viewpager.getAdapter().getCount();
				mCountView.setText(text);
			} else {
				mCountView.setText("0/0");
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	// 显示大图pager
	private void showViewPager(int index) {
		pagerContainer.setVisibility(View.VISIBLE);
		editContainer.setVisibility(View.GONE);
		viewpager.setAdapter(viewpager.new LocalViewPagerAdapter(pictures));
		viewpager.setCurrentItem(index);
		mCountView.setText((index + 1) + "/" + pictures.size());
		AnimationSet set = new AnimationSet(true);
		ScaleAnimation scaleAnimation = new ScaleAnimation((float) 0.9, 1,
				(float) 0.9, 1, pagerContainer.getWidth() / 2,
				pagerContainer.getHeight() / 2);
		scaleAnimation.setDuration(200);
		set.addAnimation(scaleAnimation);
		AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.1, 1);
		alphaAnimation.setDuration(200);
		set.addAnimation(alphaAnimation);
		pagerContainer.startAnimation(set);
	}

	// 关闭大图显示
	private void hideViewPager() {
		pagerContainer.setVisibility(View.GONE);
		editContainer.setVisibility(View.VISIBLE);
		AnimationSet set = new AnimationSet(true);
		ScaleAnimation scaleAnimation = new ScaleAnimation(1, (float) 0.9, 1,
				(float) 0.9, pagerContainer.getWidth() / 2,
				pagerContainer.getHeight() / 2);
		scaleAnimation.setDuration(200);
		set.addAnimation(scaleAnimation);
		AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
		alphaAnimation.setDuration(200);
		set.addAnimation(alphaAnimation);
		pagerContainer.startAnimation(set);
	}

	@Override
	public void onSingleTap() {
		hideViewPager();
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		textRemain.setText(arg0.toString().length() + "/140");
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}

	Map<String, File> upfiles = new HashMap<String, File>();
	/**
	 * 上传头像
	 * */
	Runnable networkTask = new Runnable() {
		@Override
		public void run() {
			for (int i = 0; i < pictures.size(); i++) {// 把图像地址转换为具体的文件路径
				if (pictures.get(i).getIsTakePhoto()) {
					File f = new File(pictures.get(i).getOriginalUri()
							.toString().replace("file://", ""));
					if (f.exists())
						upfiles.put(f.getName(), f);
				} else {
					String[] pojo = { MediaStore.Images.Media.DATA };
					Uri uri = pictures.get(i).getOriginalUri();
					@SuppressWarnings("deprecation")
					Cursor cursor = managedQuery(uri, pojo, null, null, null);
					if (cursor != null) {
						int colunm_index = cursor
								.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						String path = cursor.getString(colunm_index);
						File f = new File(path);
						if (f.exists())
							upfiles.put(f.getName(), f);
					}
				}
			}
			try {
				Map<String, String> params = new HashMap<String, String>();
				params.put("userId", app.getUid() + "");
				params.put("content", mContent.getText().toString());
				params.put("childId", app.getBoyModels().get(app.getPosition())
						.getChildId()
						+ "");
				params.put("ispublic", ispublic + "");
				UploadFile.post(DynamicPostActivity.this,
						IUrContant.SEND_CHILDE_INFO_URL, params, upfiles,
						DynamicPostActivity.this, app);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void uploadCallBack(String data, String actionUrl) throws Exception {
		if (actionUrl.equals(IUrContant.LOGIN_URL)) {
			new Thread(networkTask).start();
			return;
		}
		if (!data.subSequence(0, 1).equals("{")) {
			Map<String, String> map = new HashMap<>();
			map.put("userName",
					SharedPreferencesUtil.getString(this, IConstant.USER_NAME));
			map.put("password",
					SharedPreferencesUtil.getString(this, IConstant.PASSWORD));
			map.put("type", "2");
			map.put("code", null);
			IConstant.HTTP_CONNECT_POOL.addRequest(IUrContant.LOGIN_URL, map,
					activityHandler, false);
		} else {
			JSONObject da = new JSONObject(data);
			if (da.optInt("retcode") == 1) {
				if (dialog != null) {
					GrowthActivity.isLoad = true;
					dialog.mLoadingDialog.dismiss();
					finish();
				}
			} else {
				if (dialog != null && dialog.isShowing())
					dialog.mLoadingDialog.dismiss();
				runOnUiThread(new Runnable() {
					public void run() {
						ViewUtil.showMessage(DynamicPostActivity.this, "上传失败");
					}
				});
			}
		}
	}

	@Override
	public void chooseVideolBack(int position) throws Exception {
		if (position == 0) {
			ispublic = 1;
			ViewUtil.setContent(this, R.id.gk, R.string.gk);
		} else {
			ispublic = 2;
			ViewUtil.setContent(this, R.id.gk, R.string.bgk);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
