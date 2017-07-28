package cn.kiway.adapter.main.teacher;

import java.util.Calendar;

public class SpecialCalendar {

	private int daysOfMonth = 0; // 某月的天数
	private int dayOfWeek = 0; // 具体某一天是星期几
	private int eachDayOfWeek = 0;

	// 判断是否为闰年
	public boolean isLeapYear(int year) {
		if (year % 100 == 0 && year % 400 == 0) {
			return true;
		} else if (year % 100 != 0 && year % 4 == 0) {
			return true;
		}
		return false;
	}

	// 得到某月有多少天数
	public int getDaysOfMonth(boolean isLeapyear, int month) {
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			daysOfMonth = 31;
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			daysOfMonth = 30;
			break;
		case 2:
			if (isLeapyear) {
				daysOfMonth = 29;
			} else {
				daysOfMonth = 28;
			}

		}
		return daysOfMonth;
	}

	// 指定某年中的某月的第一天是星期几
	public int getWeekdayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 1);
		dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
		return dayOfWeek;
	}

	public int getWeekDayOfLastMonth(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day);
		eachDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
		return eachDayOfWeek;
	}

	/**
	 * 获取上一天，下一天的日期
	 * */
	public String getData(int year, int month, int day, int type) {
		int newYear = 0;
		int newMonth = 0;
		int newDay = 0;
		if (type == 1) {// 减
			if (day == 1) {
				if (month == 1) {
					newDay = getDaysOfMonth(isLeapYear(year - 1), 12);
					newMonth = 12;
					newYear = year - 1;
				} else {
					newDay = getDaysOfMonth(isLeapYear(year), month - 1);
					newMonth = month - 1;
					newYear = year;
				}
			} else {
				newMonth = month;
				newYear = year;
				newDay = day - 1;
			}
		} else if (type == 2) {// 加
			int daynumber = getDaysOfMonth(isLeapYear(year), month);// 获取这一月的天数
			if (day == daynumber) {
				if (month == 12) {
					newDay = 1;
					newMonth = 1;
					newYear = year + 1;
				} else {
					newDay = 1;
					newMonth = month + 1;
					newYear = year;
				}
			} else {
				newDay = day + 1;
				newMonth = month;
				newYear = year;
			}
		}
		String months;
		String days;
		if (newMonth < 10) {
			months = "0" + newMonth;
		} else {
			months = "" + newMonth;
		}
		if (newDay < 10) {
			days = "0" + newDay;
		} else {
			days = "" + newDay;
		}
		return newYear + "-" + months + "-" + days;
	}
}
