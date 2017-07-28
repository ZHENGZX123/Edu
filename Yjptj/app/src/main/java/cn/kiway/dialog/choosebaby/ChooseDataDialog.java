package cn.kiway.dialog.choosebaby;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import cn.kiway.Yjptj.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.dialog.BaseDialog;
import cn.kiway.utils.ViewUtil;

import com.byl.datepicker.wheelview.OnWheelScrollListener;
import com.byl.datepicker.wheelview.WheelView;
import com.byl.datepicker.wheelview.adapter.NumericWheelAdapter;

public class ChooseDataDialog extends BaseDialog {

	private WheelView year;
	private WheelView month;
	private WheelView day;

	private int mYear = 2010;
	private int mMonth = 0;
	private int mDay = 1;
	boolean isMonthSetted = false, isDaySetted = false;
	BaseActivity activity;

	public ChooseDataDialog(Context context) {
		super(context);
		activity = (BaseActivity) context;
		view = ViewUtil.inflate(context, R.layout.dialog_choose_time);
		setContentView(view, layoutParams);
		fullWindowBottom(context);
		getDataPick();
		view.findViewById(R.id.sure).setOnClickListener(this);
		setCancelable(false);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.sure:
			String birthday = new StringBuilder()
					// 当前选择的日期
					.append((year.getCurrentItem() + 2006))
					.append("-")
					.append((month.getCurrentItem() + 1) < 10 ? "0"
							+ (month.getCurrentItem() + 1) : (month
							.getCurrentItem() + 1))
					.append("-")
					.append(((day.getCurrentItem() + 1) < 10) ? "0"
							+ (day.getCurrentItem() + 1) : (day
							.getCurrentItem() + 1)).toString();
			ViewUtil.setContent(activity, R.id.brithday_val, birthday);
			dismiss();
			break;
		}
	}

	@SuppressLint("InflateParams")
	private void getDataPick() {
		Calendar c = Calendar.getInstance();
		int norYear = c.get(Calendar.YEAR);

		int curYear = mYear;
		int curMonth = mMonth + 1;
		int curDate = mDay;

		year = (WheelView) findViewById(R.id.year);
		NumericWheelAdapter numericWheelAdapter1 = new NumericWheelAdapter(
				activity, norYear - 10, norYear);
		numericWheelAdapter1.setLabel("年");
		year.setViewAdapter(numericWheelAdapter1);
		year.setCyclic(true);// 是否可循环滑动
		year.addScrollingListener(scrollListener);

		month = (WheelView) findViewById(R.id.month);
		NumericWheelAdapter numericWheelAdapter2 = new NumericWheelAdapter(
				activity, 1, 12, "%02d");
		numericWheelAdapter2.setLabel("月");
		month.setViewAdapter(numericWheelAdapter2);
		month.setCyclic(true);
		month.addScrollingListener(scrollListener);

		day = (WheelView) findViewById(R.id.day);
		initDay(curYear, curMonth);
		day.setCyclic(true);

		year.setVisibleItems(9);// 设置显示行数
		month.setVisibleItems(9);
		day.setVisibleItems(9);

		year.setCurrentItem(curYear - 10);
		month.setCurrentItem(curMonth - 1);
		day.setCurrentItem(curDate - 1);

	}

	OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		@Override
		public void onScrollingStarted(WheelView wheel) {

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			int n_year = year.getCurrentItem() + 10;// 年
			int n_month = month.getCurrentItem() + 1;// 月

			initDay(n_year, n_month);

			String birthday = new StringBuilder()
					// 当前选择的日期
					.append((year.getCurrentItem() + 2006))
					.append("-")
					.append((month.getCurrentItem() + 1) < 10 ? "0"
							+ (month.getCurrentItem() + 1) : (month
							.getCurrentItem() + 1))
					.append("-")
					.append(((day.getCurrentItem() + 1) < 10) ? "0"
							+ (day.getCurrentItem() + 1) : (day
							.getCurrentItem() + 1)).toString();
			ViewUtil.setContent(activity, R.id.brithday_val, birthday);
		}
	};

	/**
	 */
	private void initDay(int arg1, int arg2) {
		NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(
				activity, 1, getDay(arg1, arg2), "%02d");
		numericWheelAdapter.setLabel("日");
		day.setViewAdapter(numericWheelAdapter);
	}

	/**
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private int getDay(int year, int month) {
		int day = 30;
		boolean flag = false;
		switch (year % 4) {
		case 0:
			flag = true;
			break;
		default:
			flag = false;
			break;
		}
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			day = 31;
			break;
		case 2:
			day = flag ? 29 : 28;
			break;
		default:
			day = 30;
			break;
		}
		return day;
	}
}
