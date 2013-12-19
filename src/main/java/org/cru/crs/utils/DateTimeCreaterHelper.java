package org.cru.crs.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateTimeCreaterHelper
{

	public static DateTime createDateTime(int year, int month, int day, int hour, int minute, int second)
	{
		return new DateTime(DateTimeZone.UTC)
						.withYear(year)
						.withMonthOfYear(month)
						.withDayOfMonth(day)
						.withHourOfDay(hour)
						.withMinuteOfHour(minute)
						.withSecondOfMinute(second)
						.withMillisOfSecond(0);
	}
}
