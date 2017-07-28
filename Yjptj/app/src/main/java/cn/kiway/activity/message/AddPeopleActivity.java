package cn.kiway.activity.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.adapter.message.SortGroupMemberAdapter;
import cn.kiway.http.HttpResponseModel;
import cn.kiway.message.util.WriteMsgUitl;
import cn.kiway.model.MessageModel;
import cn.kiway.utils.Logger;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

import com.sortlistview.CharacterParser;
import com.sortlistview.ClearEditText;
import com.sortlistview.GroupMemberBean;
import com.sortlistview.PinyinComparator;
import com.sortlistview.SideBar;
import com.sortlistview.SideBar.OnTouchingLetterChangedListener;

public class AddPeopleActivity extends BaseActivity implements SectionIndexer,
		OnTouchingLetterChangedListener, TextWatcher, OnScrollListener {
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortGroupMemberAdapter adapter;
	private ClearEditText mClearEditText;

	private LinearLayout titleLayout;
	private TextView title;
	private TextView tvNofriends;
	/**
	 * 上次第一个可见元素，用于滚动时记录标识。
	 */
	private int lastFirstVisibleItem = -1;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	public ArrayList<GroupMemberBean> SourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	/**
	 * 是否为创建私信
	 * */
	boolean isCreateMessage;

	/**
	 * 讨论组的名字
	 * */
	static String taoName = "";
	MessageModel model;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friends);
		SourceDateList = new ArrayList<>();
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		isCreateMessage = bundle.getBoolean(IConstant.BUNDLE_PARAMS);
		try {
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ViewUtil.setContent(this, R.id.title,app.getBoyModels().get(app.getPosition()).getClassName());
	}

	private void initViews() {
		titleLayout = ViewUtil.findViewById(this, R.id.title_layout);
		title = ViewUtil.findViewById(this, R.id.title_layout_catalog);
		tvNofriends = ViewUtil.findViewById(this, R.id.title_layout_no_friends);
		sideBar = ViewUtil.findViewById(this, R.id.sidrbar);
		dialog = ViewUtil.findViewById(this, R.id.dialog);
		sortListView = ViewUtil.findViewById(this, R.id.country_lvcountry);
		mClearEditText = ViewUtil.findViewById(this, R.id.filter_edit);
		sideBar.setTextView(dialog);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(this);
		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortGroupMemberAdapter(this, SourceDateList,
				isCreateMessage);
		sortListView.setAdapter(adapter);
		if (SourceDateList.size() != 0) {
			sortListView.setOnScrollListener(this);
		}
		// 根据输入框输入值的改变来过滤搜索
		if (isCreateMessage) {
			findViewById(R.id.finish).setVisibility(View.GONE);
		}
		mClearEditText.addTextChangedListener(this);
		findViewById(R.id.finish).setOnClickListener(this);
	}

	@Override
	public void loadData() throws Exception {
		super.loadData();
		Map<String, String> map = new HashMap<>();
		if (bundle.getSerializable(IConstant.BUNDLE_PARAMS2) != null) {// 不为空则表明是从群或讨论组过来的
			model = (MessageModel) bundle
					.getSerializable(IConstant.BUNDLE_PARAMS2);
			if (bundle.getInt(IConstant.BUNDLE_PARAMS1) == 1) {// 获取讨论组为添加和已添加的人
				map.put("classId", app.getBoyModels().get(app.getPosition()).getClassId() + "");
				map.put("discussId", model.getToUid() + "");
				IConstant.HTTP_CONNECT_POOL.addRequest(
						IUrContant.GET_TAO_LUN_ZU_CY, map, activityHandler,
						true);
			} else {
				if (model.getMsgType() == 3) {// 获取班级的群员
					map.put("classId", model.getToUid() + "");
					IConstant.HTTP_CONNECT_POOL.addRequest(
							IUrContant.GET_CLASS_PEOPLE_URL, map,
							activityHandler, true);
				} else if (model.getMsgType() == 2) {// 获取讨论组的群员
					map.put("discussId", model.getToUid() + "");
					IConstant.HTTP_CONNECT_POOL.addRequest(
							IUrContant.GET_TAO_LUN_ZU_CY_URL, map,
							activityHandler, true);
				}
			}
		} else {// 创建讨论组或者添加私信的时候
			map.put("classId", app.getBoyModels().get(app.getPosition()).getClassId() + "");
			IConstant.HTTP_CONNECT_POOL
					.addRequest(IUrContant.GET_CLASS_PEOPLE_URL, map,
							activityHandler, true);
		}
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private List<GroupMemberBean> filledData(List<GroupMemberBean> list) {
		List<GroupMemberBean> mSortList = new ArrayList<GroupMemberBean>();
		for (int i = 0; i < list.size(); i++) {
			if (bundle.getBoolean(IConstant.BUNDLE_PARAMS3)) {
				GroupMemberBean sortModel = new GroupMemberBean();
				sortModel.setName(list.get(i).getName());
				sortModel.setId(list.get(i).getId());
				sortModel.setIsAdd(list.get(i).getIsAdd());
				sortModel.setIsSelector(list.get(i).getIsSelector());
				// 汉字转换成拼音
				String pinyin = characterParser.getSelling(list.get(i)
						.getName());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}
				mSortList.add(sortModel);
			} else {
				if (list.get(i).getId() != app.getUid()) {
					GroupMemberBean sortModel = new GroupMemberBean();
					sortModel.setName(list.get(i).getName());
					sortModel.setId(list.get(i).getId());
					sortModel.setIsAdd(list.get(i).getIsAdd());
					sortModel.setIsSelector(list.get(i).getIsSelector());
					// 汉字转换成拼音
					String pinyin = characterParser.getSelling(list.get(i)
							.getName());
					String sortString = pinyin.substring(0, 1).toUpperCase();
					// 正则表达式，判断首字母是否是英文字母
					if (sortString.matches("[A-Z]")) {
						sortModel.setSortLetters(sortString.toUpperCase());
					} else {
						sortModel.setSortLetters("#");
					}
					mSortList.add(sortModel);
				}
			}
		}
		return mSortList;
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<GroupMemberBean> filterDateList = new ArrayList<GroupMemberBean>();
		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
			titleLayout.setVisibility(View.VISIBLE);
			tvNofriends.setVisibility(View.GONE);
		} else {
			filterDateList.clear();
			for (GroupMemberBean sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
		if (filterDateList.size() == 0) {
			tvNofriends.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		if (SourceDateList.size() == 0)
			return 0;
		return SourceDateList.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	@SuppressLint("DefaultLocale")
	public int getPositionForSection(int section) {
		for (int i = 0; i < SourceDateList.size(); i++) {
			String sortStr = SourceDateList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void onTouchingLetterChanged(String s) {
		// 该字母首次出现的位置
		int position = adapter.getPositionForSection(s.charAt(0));
		if (position != -1) {
			sortListView.setSelection(position);
		}
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
		// 这个时候不需要挤压效果 就把他隐藏掉
		titleLayout.setVisibility(View.GONE);
		// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
		filterData(s.toString());
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int section = getSectionForPosition(firstVisibleItem);
		int nextSection = getSectionForPosition(firstVisibleItem);
		int nextSecPosition = getPositionForSection(+nextSection);
		if (firstVisibleItem != lastFirstVisibleItem) {
			MarginLayoutParams params = (MarginLayoutParams) titleLayout
					.getLayoutParams();
			params.topMargin = 0;
			titleLayout.setLayoutParams(params);
			title.setText(SourceDateList.get(getPositionForSection(section))
					.getSortLetters());
		}
		if (nextSecPosition == firstVisibleItem) {
			View childView = view.getChildAt(0);
			if (childView != null) {
				int titleHeight = titleLayout.getHeight();
				int bottom = childView.getBottom();
				MarginLayoutParams params = (MarginLayoutParams) titleLayout
						.getLayoutParams();
				if (bottom < titleHeight) {
					float pushedDistance = bottom - titleHeight;
					params.topMargin = (int) pushedDistance;
					titleLayout.setLayoutParams(params);
				} else {
					if (params.topMargin != 0) {
						params.topMargin = 0;
						titleLayout.setLayoutParams(params);
					}
				}
			}
		}
		lastFirstVisibleItem = firstVisibleItem;
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.finish:
			if (getUserIdList((ArrayList<GroupMemberBean>) adapter.list).size() <= 0) {
				finish();
				return;
			}
			if (bundle.getSerializable(IConstant.BUNDLE_PARAMS2) != null) {// 加入讨论组成员
				if (getUserIdList((ArrayList<GroupMemberBean>) adapter.list)
						.size() <= 0) {
					finish();
					return;
				}
				Map<String, String> map = new HashMap<>();
				map.put("discussId", model.getToUid() + "");
				map.put("userIdList",
						getUserIdList((ArrayList<GroupMemberBean>) adapter.list)
								+ "");
				IConstant.HTTP_CONNECT_POOL.addRequest(
						IUrContant.ADD_TAO_LUN_ZU_URL, map, activityHandler,
						true);
			} else {
				if (getUserIdList((ArrayList<GroupMemberBean>) adapter.list)
						.size() == 1) {// 如果为一个人则创建私信
					MessageModel model = new MessageModel();
					model.setUid(app.getUid());
					model.setToUid(StringUtil.toInt(getUserIdList(
							(ArrayList<GroupMemberBean>) adapter.list).get(0)
							.get("userId")));
					model.setName(taoName);
					Bundle bundle = new Bundle();
					bundle.putSerializable(IConstant.BUNDLE_PARAMS, model);
					startActivity(PrivateMessageActivity.class, bundle);
					finish();
				} else {// 创建讨论组
					Map<String, String> map = new HashMap<>();
					map.put("owner", "" + app.getUid());
					map.put("userIdList",
							getUserIdList((ArrayList<GroupMemberBean>) adapter.list)
									+ "");
					map.put("content",
							taoName.substring(0, taoName.length() - 1));
					IConstant.HTTP_CONNECT_POOL.addRequest(
							IUrContant.CREATE_TAO_LU_ZU_UREL, map,
							activityHandler, true);
				}
			}
			break;
		}
	}

	@Override
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.GET_CLASS_PEOPLE_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null && data.optInt("retcode") == 1) {
				if (data != null) {
					JSONArray list = data.optJSONArray("cUserList");
					List<GroupMemberBean> PeopleList = new ArrayList<GroupMemberBean>();
					for (int i = 0; i < list.length(); i++) {
						JSONObject object = list.optJSONObject(i);
						GroupMemberBean bean = new GroupMemberBean();
						bean.setId(object.optInt("user_id"));
						bean.setName(object.optString("realname"));
						PeopleList.add(bean);
					}
					SourceDateList = (ArrayList<GroupMemberBean>) filledData(PeopleList);
				}
				if (SourceDateList.size() == 0) {
					ViewUtil.showMessage(this, "该班级还没其他加入的老师和家长哦");
				}
			}
		} else if (message.getUrl().equals(IUrContant.CREATE_TAO_LU_ZU_UREL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			WriteMsgUitl.WriteClassData(this, app, taoName,
					data.optInt("discussId"), 2);
			finish();
		} else if (message.getUrl().equals(IUrContant.GET_TAO_LUN_ZU_CY_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null && data.optInt("retcode") == 1) {
				JSONArray list = data.optJSONArray("disList");
				List<GroupMemberBean> PeopleList = new ArrayList<GroupMemberBean>();
				for (int i = 0; i < list.length(); i++) {
					JSONObject object = list.optJSONObject(i);
					GroupMemberBean bean = new GroupMemberBean();
					bean.setId(object.optInt("userId"));
					if (object.optString("display_name").equals("")) {
						bean.setName(object.optString("realname"));
					} else {
						bean.setName(object.optString("display_name"));
					}
					PeopleList.add(bean);
				}
				SourceDateList = (ArrayList<GroupMemberBean>) filledData(PeopleList);
			}
		} else if (message.getUrl().equals(IUrContant.GET_TAO_LUN_ZU_CY)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null && data.optInt("retcode") == 1) {
				if (data != null && data.optInt("retcode") == 1) {
					JSONArray list = data.optJSONArray("disList");
					List<GroupMemberBean> PeopleList = new ArrayList<GroupMemberBean>();
					for (int i = 0; i < list.length(); i++) {
						JSONObject object = list.optJSONObject(i);
						GroupMemberBean bean = new GroupMemberBean();
						bean.setId(object.optInt("userId"));
						if (object.optString("display_name").equals("null")) {
							bean.setName(object.optString("realname"));
						} else {
							bean.setName(object.optString("display_name"));
						}
						if (object.optInt("type") == 1) {
							bean.setIsAdd(true);
							bean.setIsSelector(true);
						} else {
							bean.setIsSelector(false);
							bean.setIsAdd(false);
						}
						PeopleList.add(bean);
					}
					SourceDateList = (ArrayList<GroupMemberBean>) filledData(PeopleList);
				}
			}
		} else if (message.getUrl().equals(IUrContant.ADD_TAO_LUN_ZU_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data.optInt("retcode") == 1) {
				ViewUtil.showMessage(this, "添加成功");
				finish();
			} else {
				ViewUtil.showMessage(this, "添加失败");
			}
		}
		initViews();
	}

	List<Map<String, String>> getUserIdList(
			ArrayList<GroupMemberBean> SourceDateList) {
		taoName = "";
		List<Map<String, String>> liston = new ArrayList<Map<String, String>>();// 创建列表
		Logger.log(SourceDateList.size());
		for (int i = 0; i < SourceDateList.size(); i++) {
			Logger.log(SourceDateList.get(i).getIsSelector());
			if (SourceDateList.get(i).getIsSelector()
					&& !SourceDateList.get(i).getIsAdd()) {
				Logger.log(SourceDateList.get(i).getId() + "");
				Map<String, String> param = new HashMap<>();
				taoName = taoName + SourceDateList.get(i).getName() + "、";
				param.put("userId", SourceDateList.get(i).getId() + "");
				liston.add(param);
			}
		}
		Logger.log(taoName);
		return liston;
	}
}
