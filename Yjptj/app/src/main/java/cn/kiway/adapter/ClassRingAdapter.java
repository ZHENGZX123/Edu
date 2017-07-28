package cn.kiway.adapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import uk.co.senab.photoview.CircleImageView;
import uk.co.senab.photoview.SelectableRoundedImageView;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.mian.ClassRingActivity;
import cn.kiway.adapter.common.UserInfoGalleryPicAdapter;
import cn.kiway.http.BaseHttpHandler;
import cn.kiway.http.HttpHandler;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.model.ClassRingModel;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class ClassRingAdapter extends BaseExpandableListAdapter implements
		OnClickListener, HttpHandler {
	ClassRingActivity activity;
	ClassGroupHolder groupHolder;
	ClassItemHodler itemHodler;
	int picHeight;
	public List<ClassRingModel> classRingList;
	public List<ClassRingModel> replyList;
	LinearLayout layout;
	protected BaseHttpHandler adapetHandler = new BaseHttpHandler(this) {
	};
	String[] facesKey, faces;
	int h;

	public ClassRingAdapter(ClassRingActivity activity,
			List<ClassRingModel> classRingList, LinearLayout layout) {
		this.activity = activity;
		this.classRingList = classRingList;
		this.layout = layout;
		picHeight = (int) ((activity.displayMetrics.widthPixels - activity.resources
				.getDimension(R.dimen._100dp)) / 3)
				+ (int) activity.resources.getDimension(R.dimen._5dp);
		h = (int) activity.resources.getDimension(R.dimen._20dp);
		facesKey = activity.getResources().getStringArray(R.array.faces_key);
		try {
			faces = activity.getAssets().list("faces");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object getGroup(int arg0) {
		return null;
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		return null;
	}

	@Override
	public int getChildrenCount(int arg0) {
		return classRingList.get(arg0).getReplyList().size();
	}

	@Override
	public int getGroupCount() {
		return classRingList.size();
	}

	@Override
	public long getChildId(int gruoppostion, int childpostion) {
		return childpostion;
	}

	@Override
	public long getGroupId(int grooppostion) {
		return grooppostion;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity,
					R.layout.my_comment_list_item_head);
			groupHolder = new ClassGroupHolder();
			groupHolder.userPhotos = ViewUtil.findViewById(view,
					R.id.user_photos);
			groupHolder.userImg = ViewUtil.findViewById(view, R.id.profile);
			groupHolder.userName = ViewUtil.findViewById(view, R.id.user_name);
			groupHolder.content = ViewUtil.findViewById(view, R.id.content);
			groupHolder.creatTime = ViewUtil.findViewById(view,
					R.id.create_time);
			groupHolder.delete = ViewUtil.findViewById(view, R.id.delete);
			groupHolder.zan = ViewUtil.findViewById(view, R.id.zan);
			groupHolder.comment = ViewUtil.findViewById(view, R.id.comment);
			groupHolder.zanPeople = ViewUtil
					.findViewById(view, R.id.zan_prople);
			groupHolder.quanWen = ViewUtil.findViewById(view, R.id.quanwen);
			groupHolder.zheDie = ViewUtil.findViewById(view, R.id.zhedie);
			view.setTag(groupHolder);
		} else {
			groupHolder = (ClassGroupHolder) view.getTag();
		}
		ClassRingModel model = classRingList.get(groupPosition);
		activity.imageLoader.displayImage(
				StringUtil.imgUrl(activity, model.getUserImg()),
				groupHolder.userImg, activity.fadeOptions);
		ViewUtil.setContent(groupHolder.content, model.getContent());
		ViewUtil.setContent(groupHolder.creatTime,
				StringUtil.getStandardDate(model.getTime()));
		ViewUtil.setContent(groupHolder.userName, model.getUserName());
		ViewUtil.setContent(groupHolder.zanPeople, model.getPriase());
		if (classRingList.get(groupPosition).getRraise()) {
			ViewUtil.setArroundDrawable(groupHolder.zan,
					R.drawable.ic_action_favorite_true, -1, -1, -1);
			ViewUtil.setContent(groupHolder.zan, R.string.yizan);
		} else {
			ViewUtil.setArroundDrawable(groupHolder.zan,
					R.drawable.ic_action_favorite_green, -1, -1, -1);
			ViewUtil.setContent(groupHolder.zan, R.string.zan);
		}
		groupHolder.userPhotos.setAdapter(new UserInfoGalleryPicAdapter(
				activity, classRingList.get(groupPosition).getLisPhoto(),
				picHeight));
		if (classRingList.get(groupPosition).getLisPhoto().size() > 6) {
			groupHolder.userPhotos
					.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT, 3 * picHeight + 50));
		} else if (classRingList.get(groupPosition).getLisPhoto().size() > 3) {
			groupHolder.userPhotos
					.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT, 2 * picHeight + 50));
		} else if (classRingList.get(groupPosition).getLisPhoto().size() > 0) {
			groupHolder.userPhotos
					.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT, picHeight + 50));

		} else {
			groupHolder.userPhotos
					.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT, 0));
		}
		if (model.getUserId() == activity.app.getUid()) {
			groupHolder.delete.setVisibility(View.VISIBLE);
		} else {
			groupHolder.delete.setVisibility(View.GONE);
		}
		if (groupPosition == 0) {
			view.findViewById(R.id.view).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.view).setVisibility(View.VISIBLE);
		}
		if (groupHolder.content.getLineCount() > 6) {
			if (model.getIsQuan()) {
				groupHolder.quanWen.setVisibility(View.GONE);
				groupHolder.zheDie.setVisibility(View.VISIBLE);
				groupHolder.content.setMaxLines(1000);
			} else {
				groupHolder.quanWen.setVisibility(View.VISIBLE);
				groupHolder.zheDie.setVisibility(View.GONE);
				groupHolder.content.setMaxLines(6);
			}
		} else {
			groupHolder.quanWen.setVisibility(View.GONE);
			groupHolder.zheDie.setVisibility(View.GONE);
		}
		groupHolder.comment.setTag(R.id.bundle_params, groupPosition);
		groupHolder.delete.setTag(R.id.bundle_params, groupPosition);
		groupHolder.zan.setTag(R.id.bundle_params, groupPosition);
		groupHolder.quanWen.setTag(R.id.bundle_params, groupPosition);
		groupHolder.zheDie.setTag(R.id.bundle_params, groupPosition);
		groupHolder.comment.setOnClickListener(this);
		groupHolder.zan.setOnClickListener(this);
		groupHolder.delete.setOnClickListener(this);
		groupHolder.quanWen.setOnClickListener(this);
		groupHolder.zheDie.setOnClickListener(this);
		view.setTag(R.id.bundle_params, groupPosition);
		view.setOnClickListener(this);
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = ViewUtil.inflate(activity,
					R.layout.my_comment_list_item_item);
			itemHodler = new ClassItemHodler();
			itemHodler.commentImg = ViewUtil.findViewById(view, R.id.user_img);
			itemHodler.commentUserName = ViewUtil.findViewById(view,
					R.id.user_name);
			itemHodler.commentTime = ViewUtil.findViewById(view,
					R.id.reply_time);
			itemHodler.commentContent = ViewUtil.findViewById(view,
					R.id.reply_content);
			view.setTag(itemHodler);
		} else {
			itemHodler = (ClassItemHodler) view.getTag();
		}
		ClassRingModel ringModel = classRingList.get(groupPosition)
				.getReplyList().get(childPosition);
		ViewUtil.setContent(itemHodler.commentTime,
				StringUtil.getStandardDate(ringModel.getCommentTime()));
		ViewUtil.setContent(itemHodler.commentUserName,
				ringModel.getCommentName());
		Spanned spanned = Html.fromHtml(
				AppUtil.strToHtml(ringModel.getCommentContent(), facesKey),
				new ImageGetter() {
					@Override
					public Drawable getDrawable(String source) {
						try {
							Drawable drawable = AppUtil.loadFaceResourse(
									activity.getAssets(), activity, source,
									facesKey, faces);
							drawable.setBounds(0, 0, h, h);
							return drawable;
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}
				}, null);
		ViewUtil.setContent(itemHodler.commentContent, spanned);
		return view;
	}

	static class ClassGroupHolder {
		/**
		 * 说说的图像
		 * */
		GridView userPhotos;
		/**
		 * 发布人头像
		 * */
		CircleImageView userImg;
		/**
		 * 发布人名字
		 * */
		TextView userName;
		/**
		 * 发布内容
		 * */
		TextView content;
		/**
		 * 发布时间
		 * */
		TextView creatTime;
		/**
		 * 删除按钮
		 * */
		ImageView delete;
		/**
		 * 赞按钮
		 * */
		TextView zan;
		/**
		 * 评论按钮
		 * */
		TextView comment;
		/**
		 * 赞的人
		 * */
		TextView zanPeople;
		/**
		 * 全文按钮
		 * */
		TextView quanWen;
		/**
		 * 折叠按钮
		 * */
		TextView zheDie;
	}

	static class ClassItemHodler {
		/**
		 * 评论人头像
		 * */
		SelectableRoundedImageView commentImg;
		/**
		 * 评论人名字
		 * */
		TextView commentUserName;
		/**
		 * 评论时间
		 * */
		TextView commentTime;
		/**
		 * 评论内容
		 * */
		TextView commentContent;
	}

	@Override
	public void onClick(View v) {
		int position = StringUtil
				.toInt(v.getTag(R.id.bundle_params).toString());
		Map<String, String> map = new HashMap<>();
		Map<String, Object> params = new HashMap<>();// 携带参数

		switch (v.getId()) {
		case R.id.comment:
			if (layout != null)
				if (layout.getVisibility() == View.VISIBLE) {
					layout.setVisibility(View.GONE);
				} else {
					layout.setVisibility(View.VISIBLE);
					activity.editText.setFocusable(true);
					activity.editText.setFocusableInTouchMode(true);
					activity.editText.requestFocus();
					activity.editText.setHint("");
					InputMethodManager inputManager = (InputMethodManager) activity.editText
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					inputManager.showSoftInput(activity.editText, 0);
					int[] location = new int[2];
					v.getLocationOnScreen(location);
					activity.srcolPosition(v.getHeight(), location[1]
							- activity.layout.getHeight());
				}
			ClassRingActivity.postid = classRingList.get(position)
					.getClassRingId();
			ClassRingActivity.groundposition = position;
			break;
		case R.id.zan:
			if (classRingList.get(position).getRraise()) {
				ViewUtil.showMessage(activity, R.string.nyjzlgbjq);
				return;
			}
			params.put("position", position);
			map.put("postId", classRingList.get(position).getClassRingId() + "");
			map.put("userId", activity.app.getUid() + "");
			IConstant.HTTP_CONNECT_POOL
					.addRequest(IUrContant.PRASE_CLASS_RING_URL, map,
							adapetHandler, params);
			break;
		case R.id.zhedie:
			classRingList.get(position).setIsQuan(false);
			notifyDataSetChanged();
			break;
		case R.id.quanwen:
			classRingList.get(position).setIsQuan(true);
			notifyDataSetChanged();
			break;
		default:
			activity.findViewById(R.id.layouts).setVisibility(View.GONE);
			activity.editText.getEditableText().clear();
			break;
		}
	}

	@Override
	public void httpErr(HttpResponseModel message) throws Exception {

	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		if (message.getUrl().equals(IUrContant.PRASE_CLASS_RING_URL)) {// 赞成功
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				Map<String, Object> params = message.getMap();
				int position = (int) params.get("position");
				ClassRingModel classRingModel = classRingList.get(position);
				String string = classRingModel.getPriase();
				if (string != null) {
					string = string + "、";
				} else {
					string = "";
				}
				if (activity.mCache.getAsJSONObject(IUrContant.GET_MY_INFO_URL
						+ activity.app.getUid()) != null
						&& activity.mCache.getAsJSONObject(
								IUrContant.GET_MY_INFO_URL
										+ activity.app.getUid()).optJSONObject(
								"userInfo") != null)
					classRingList.get(position).setPriase(
							string
									+ ""
									+ activity.mCache
											.getAsJSONObject(
													IUrContant.GET_MY_INFO_URL
															+ activity.app
																	.getUid())
											.optJSONObject("userInfo")
											.optString("realname"));
				else
					classRingList.get(position).setPriase(string + "我");
				classRingList.get(position).setPraiseId(true);
				notifyDataSetChanged();
			}
		}
	}

	@Override
	public void HttpError(HttpResponseModel message) throws Exception {

	}
}
