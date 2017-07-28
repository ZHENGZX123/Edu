package cn.kiway.activity.mian;

import handmark.pulltorefresh.library.PullToRefreshBase;
import handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import handmark.pulltorefresh.library.PullToRefreshExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.adapter.ClassRingAdapter;
import cn.kiway.adapter.common.FacesAdapter;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.ClassRingModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class ClassRingActivity extends BaseNetWorkActicity implements
		OnRefreshListener2<ExpandableListView>, OnChildClickListener,
		TextWatcher, OnEditorActionListener, OnTouchListener {
	PullToRefreshExpandableListView listView;
	public ExpandableListView lv;
	ClassRingAdapter adapter;
	ImageView avatar;
	LinearLayout layouts;// 输入框
	public EditText editText;
	GridView faceList;// 表情
	public static long postid = 0;// 回复人的id
	public static int groundposition = 0;
	int heightDifference;
	public RelativeLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!AppUtil.isNetworkAvailable(this)
				&& mCache.getAsJSONObject(IUrContant.SREACH_CLASS_RING_URL
						+ app.getBoyModels().get(app.getPosition())
								.getClassId()) == null) {
			newWorkdialog = new IsNetWorkDialog(context, this,
					resources.getString(R.string.dqsjmylrhlwqljhlwl),
					resources.getString(R.string.ljhlw));
			if (newWorkdialog != null && !newWorkdialog.isShowing()) {
				newWorkdialog.show();
				return;
			}
		}
		setContentView(R.layout.activity_class_rings);
		try {
			initView();
			setData();
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void initView() throws Exception {
		listView = ViewUtil.findViewById(this, R.id.listview);
		layouts = ViewUtil.findViewById(this, R.id.layouts);
		editText = ViewUtil.findViewById(this, R.id.edit);
		faceList = ViewUtil.findViewById(this, R.id.faces_list);
		layout = ViewUtil.findViewById(this, R.id.re);
		lv = listView.getRefreshableView();
		adapter = new ClassRingAdapter(this, new ArrayList<ClassRingModel>(),
				layouts);
		lv.setAdapter(adapter);
		lv.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
			}
		});
		lv.setDivider(null);
		listView.setOnRefreshListener(this);
		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader,
				true, true));// 滑动不加载图片
		listView.setScrollingWhileRefreshingEnabled(true);
		try {
			faceList.setAdapter(new FacesAdapter(this, editText
					.getEditableText()));// 初始化表情适配器
		} catch (Exception e) {
			e.printStackTrace();
		}
		lv.setOnChildClickListener(this);
		editText.addTextChangedListener(this);
		editText.setOnEditorActionListener(this);
		lv.setOnTouchListener(this);
		findViewById(R.id.emoticon).setOnClickListener(this);
		findViewById(R.id.send).setOnClickListener(this);
		final View decorView = getWindow().getDecorView();
		decorView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						Rect rect = new Rect();
						decorView.getWindowVisibleDisplayFrame(rect);
						// 计算出可见屏幕的高度
						int displayHight = rect.bottom - rect.top;
						// 获得屏幕整体的高度
						int hight = decorView.getHeight();
						// 获得键盘高度
						heightDifference = hight - displayHight;
					}
				});
	}

	@Override
	public void loadData() throws Exception {
		super.loadData();
		isRefresh = true;
		Map<String, String> map = new HashMap<>();
		map.put("classId", app.getBoyModels().get(app.getPosition())
				.getClassId()
				+ "");
		map.put("minDate", "null");
		IConstant.HTTP_CONNECT_POOL.addRequest(
				IUrContant.SREACH_CLASS_RING_URL, map, activityHandler);
	}

	void setData() throws Exception {
		JSONObject data = mCache
				.getAsJSONObject(IUrContant.SREACH_CLASS_RING_URL
						+ app.getBoyModels().get(app.getPosition())
								.getClassId());
		if (data != null) {
			JSONArray resultList = data.optJSONArray("resultList");
			if (resultList != null) {
				if (isRefresh)
					adapter.classRingList.clear();
				for (int i = 0; i < resultList.length(); i++) {
					ClassRingModel classRingModel = new ClassRingModel();
					JSONObject item = resultList.optJSONObject(i);
					JSONObject content = item.optJSONObject("classPost");// 获取班级圈内容列表
					classRingModel.setUserId(content.optInt("owner"));// 用户id
					classRingModel.setClassRingId(content.optInt("id"));// 班级圈id
					classRingModel.setTime(StringUtil.stringTimeToLong(content
							.optString("create_time")));// 创建时间
					classRingModel.setContent(content.optString("content"));// 内容
					classRingModel.setUserName(content.optString("realname"));// 用户名字
					classRingModel.setUserImg(content.optString("photo"));// 用户头像
					JSONArray classPhotoList = item
							.optJSONArray("classPostingList");// 获取图片地址
					classRingModel.setIsQuan(false);
					if (classPhotoList != null) {
						List<String> listPhoto = new ArrayList<String>();
						for (int j = 0; j < classPhotoList.length(); j++) {
							JSONObject photolist = classPhotoList
									.optJSONObject(j);
							String string = photolist.optString("img_url");
							listPhoto.add(string);
						}
						classRingModel.setListPhoto(listPhoto);// 设置图像地址
					}
					JSONArray classPraiseList = item
							.optJSONArray("classPraiseList");// 获取赞的人列表
					if (classPraiseList != null) {
						String praiseName = "";
						for (int j = 0; j < classPraiseList.length(); j++) {// 赞的人名字
							if (classPraiseList.optJSONObject(j)
									.optString("realname").equals("null")) {
								praiseName = praiseName + "  、";
							} else {
								praiseName = praiseName
										+ classPraiseList.optJSONObject(j)
												.optString("realname") + "、";
							}
							classRingModel.setPriase(praiseName);
							if (classPraiseList.optJSONObject(j).optInt(
									"user_id") == app.getUid()) {// 设置是否赞过
								classRingModel.setPraiseId(true);
							}
						}
					}
					// 获取评论列表
					JSONArray classReplyList = item
							.optJSONArray("classReplyList");
					List<ClassRingModel> models = new ArrayList<ClassRingModel>();
					if (classReplyList != null) {
						for (int j = 0; j < classReplyList.length(); j++) {
							JSONObject replyItem = classReplyList
									.optJSONObject(j);
							ClassRingModel replyModel = new ClassRingModel();
							replyModel.setCommentContent(replyItem
									.optString("content"));// 评论内容
							replyModel.setCommentUserId(replyItem
									.optInt("user_id"));// 评论人id
							String aRealName = replyItem.optString("realname");
							if ((aRealName == null)
									|| ("null".equalsIgnoreCase(aRealName))) {
								aRealName = "";
							}
							replyModel.setCommentName(aRealName);// 评论人名字
							replyModel.setCommentTime(StringUtil
									.stringTimeToLong(replyItem
											.optString("create_time")));// 评论时间
							replyModel.setCommentId(replyItem.optInt("id"));// 评论的id
							models.add(replyModel);
						}
						classRingModel.setReplyList(models);
					}
					adapter.classRingList.add(classRingModel);
				}
				adapter.notifyDataSetChanged();
			}
		}

		for (int i = 0; i < adapter.classRingList.size(); i++) {// 全部展开子控件
			lv.expandGroup(i);
		}
		if (adapter.getGroupCount() <= 0) {
			findViewById(R.id.no_data).setVisibility(View.VISIBLE);
			ViewUtil.setContent(this, R.id.no_data, "老师还没有发班级圈哦");
			listView.setVisibility(View.GONE);
		} else {
			findViewById(R.id.no_data).setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.emoticon:
			if (faceList.getVisibility() == View.GONE) {
				faceList.setVisibility(View.VISIBLE);// 隐藏键盘
				ViewUtil.hideKeyboard(this);
			} else {
				faceList.setVisibility(View.GONE);
				editText.setFocusable(true);
				editText.setFocusableInTouchMode(true);
				editText.requestFocus();
				InputMethodManager inputManager = (InputMethodManager) editText
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(editText, 0);
			}
			break;
		case R.id.send:
			try {
				sendComment();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

	@Override
	public void onPullDownToRefresh(
			PullToRefreshBase<ExpandableListView> refreshView) {
		isRefresh = true;
		try {
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPullUpToRefresh(
			PullToRefreshBase<ExpandableListView> refreshView) {
		isRecycle = false;
		Map<String, String> map = new HashMap<>();
		map.put("classId", app.getBoyModels().get(app.getPosition())
				.getClassId()
				+ "");
		map.put("minDate", StringUtil.getDateField(
				adapter.classRingList.get(adapter.getGroupCount() - 1)
						.getTime(), 9));
		IConstant.HTTP_CONNECT_POOL.addRequest(
				IUrContant.SREACH_CLASS_RING_URL, map, activityHandler);
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.SREACH_CLASS_RING_URL)) {// 获取班级圈的数据
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null) {
				mCache.put(IUrContant.SREACH_CLASS_RING_URL
						+ app.getBoyModels().get(app.getPosition())
								.getClassId(), data);
				setData();
			}
		} else if (message.getUrl().equals(IUrContant.COMMENT_CLASS_RING_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null) {
				if (data.optInt("retcode") == 1) {// 评论成功 添加评论数据
					loadData();
					editText.getEditableText().clear();
					layouts.setVisibility(View.GONE);
					replyStr = "";
					editText.setHint("");
					if (faceList.getVisibility() == View.VISIBLE) {
						faceList.setVisibility(View.GONE);
					}
					ViewUtil.hideKeyboard(this);
				}
			}
		}
	}

	String replyStr = "";

	@Override
	public boolean onChildClick(ExpandableListView parent, final View v,
			int groupPosition, int childPosition, long id) {
		if (adapter.classRingList.get(groupPosition).getReplyList()
				.get(childPosition).getCommentUserId() == app.getUid()) {
			ViewUtil.showMessage(this, R.string.bnhfzi);// 提示不能回复自己
			return false;
		}
		if (layouts != null && layouts.getVisibility() == View.GONE)
			layouts.setVisibility(View.VISIBLE);
		postid = adapter.classRingList.get(groupPosition).getClassRingId();// 回复的id
		groundposition = groupPosition;// 当前点击的位置
		ClassRingModel model = adapter.classRingList.get(groupPosition)
				.getReplyList().get(childPosition);
		try {
			editText.setFocusable(true);
			editText.setFocusableInTouchMode(true);
			editText.requestFocus();
			InputMethodManager inputManager = (InputMethodManager) editText
					.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.showSoftInput(editText, 0);
			editText.setHint(resources.getString(R.string.reply) + "  "
					+ model.getCommentName());
			replyStr = resources.getString(R.string.reply)// 添加回复字段 谁回复谁
					+ " <font color='#00F5FF'>"
					+ model.getCommentName()
					+ " </font>: ";
			srcolPosition(v.getHeight(), v.getY());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 评论的发送
	 * */
	void sendComment() throws Exception {
		if (postid == 0)
			return;
		replyStr = replyStr + editText.getText().toString();
		Map<String, String> map = new HashMap<>();
		map.put("postId", postid + "");
		map.put("userId", app.getUid() + "");
		map.put("content", replyStr);
		IConstant.HTTP_CONNECT_POOL.addRequest(
				IUrContant.COMMENT_CLASS_RING_URL, map, activityHandler);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEND
				|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
			try {
				sendComment();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	@Override
	public void afterTextChanged(Editable arg0) {
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
		if (TextUtils.isEmpty(s)) {
			findViewById(R.id.send).setVisibility(View.GONE);
		} else {
			findViewById(R.id.send).setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		layouts.setVisibility(View.GONE);
		editText.getEditableText().clear();
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			WindowManager.LayoutParams params = getWindow().getAttributes();
			if (layouts.getVisibility() == View.VISIBLE
					|| params.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
				editText.getEditableText().clear();
				ViewUtil.hideKeyboard(this);
				layouts.setVisibility(View.GONE);
			} else
				finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public void srcolPosition(final int viewHeight, final float f) {
		int time = 0;
		if (heightDifference > 0)
			time = 400;
		new Handler().postDelayed(new Runnable() {
			@SuppressLint("NewApi")
			public void run() {
				WindowManager wm = getWindowManager();
				@SuppressWarnings("deprecation")
				int height = wm.getDefaultDisplay().getHeight();
				lv.scrollListBy(-(int) (height - (viewHeight + f - 20
						+ heightDifference + layouts.getHeight()
						+ layout.getHeight() + getStatusBarHeight())));
			}
		}, time);
	}
}
