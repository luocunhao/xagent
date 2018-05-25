package xlink.core.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarUtils {

	/**
	 * 获得指定时间戳当天的凌晨00:00:00的时间戳
	 * 
	 * @param calendar
	 * @param mills_time
	 * @return
	 */
	public static final long getDayBreak(Calendar calendar, long mills_time) {
		calendar.setTimeInMillis(mills_time);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static final List<String> getBetweenDays(Date start, Date end) {
		List<String> days = new ArrayList<>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		long start_break = getDayBreak(format.getCalendar(), start.getTime());
		long end_break = getDayBreak(format.getCalendar(), end.getTime());
		for (;;) {
			if (start_break > end_break) {
				break;
			}
			days.add(format.format(new Date(start_break)));
			start_break += 86400000;
		}
		return days;
	}

	
	/**
	 * 
	 * @param date
	 * @return
	 */
	public static final String funcGetDate(Date date) {
		String format = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'";
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
}
