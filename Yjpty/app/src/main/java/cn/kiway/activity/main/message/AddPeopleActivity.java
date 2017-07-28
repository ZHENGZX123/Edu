package cn.kiway.activity.main.message;

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
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.adapter.main.message.SortGroupMemberAdapter;
import cn.kiway.http.HttpResponseModel;
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
	public static ArrayList<GroupMemberBean> SourceDateList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	/**
	 * 是否为获取讨论组列表 否为获取班级
	 * */
	boolean isGetClassPeople;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isGetClassPeople = bundle.getBoolean(IConstant.BUNDLE_PARAMS);
		SourceDateList = new ArrayList<GroupMemberBean>();
		setContentView(R.layout.activity_add_friends);
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		try {
			ViewUtil.setContent(this, R.id.title,
					bundle.getString(IConstant.BUNDLE_PARAMS2));
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		adapter = new SortGroupMemberAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);
		if (SourceDateList.size() != 0) {
			sortListView.setOnScrollListener(this);
		}
		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(this);
		
	}

	@Override
	public void loadData() throws Exception {
		super.loadData();
		Map<String, String> map = new HashMap<>();
		if (isGetClassPeople) {
			map.put("discussId", bundle.getString(IConstant.BUNDLE_PARAMS1));
			IConstant.HTTP_CONNECT_POOL.addRequest(
					IUrContant.GET_TAO_LUN_ZU_CY_URL, map, activityHandler,
					true);
		} else {
			map.put("classId", bundle.getString(IConstant.BUNDLE_PARAMS1));
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
	public void httpSuccess(HttpResponseModel message) throws Exception {
		super.httpSuccess(message);
		if (message.getUrl().equals(IUrContant.GET_CLASS_PEOPLE_URL)) {

			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null && data.optInt("retcode") == 1) {
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
				if (SourceDateList.size() == 0) {
					ViewUtil.showMessage(this, "该班级还没其他加入的老师和家长哦");
				}
			}
		} else if (message.getUrl().equals(IUrContant.GET_TAO_LUN_ZU_CY_URL)) {
			JSONObject data = new JSONObject(new String(message.getResponse()));
			if (data != null && data.optInt("retcode") == 1) {
				JSONArray list = data.optJSONArray("disList");
				List<GroupMemberBean> PeopleList = new ArrayList<GroupMemberBean>();
				for (int i = 0; i < list.length(); i++) {
					JSONObject object = list.optJSONObject(i);
					GroupMemberBean bean = new GroupMemberBean();
					bean.setId(object.optInt("userId"));
					if (object.optString("display_name").equals("null")
							|| object.optString("display_name").equals("")) {
						bean.setName(object.optString("realname"));
					} else {
						bean.setName(object.optString("display_name"));
					}
					PeopleList.add(bean);
				}
				SourceDateList = (ArrayList<GroupMemberBean>) filledData(PeopleList);
			}

		}
		initViews();
	}
}
