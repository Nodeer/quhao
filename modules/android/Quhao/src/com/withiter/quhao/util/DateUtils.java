package com.withiter.quhao.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

	public static String formatDate(String createdTime, String formatStr) {
		if(StringUtils.isNull(createdTime))
		{
			return "";
		}
		if(StringUtils.isNull(formatStr))
		{
			formatStr = "yyyy-MM-dd HH:mm:ss";
		}
		String created = "";
		try
		{
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(Date.parse(createdTime));
			DateFormat format = new SimpleDateFormat(formatStr, Locale.CHINA);
			created = format.format(cal.getTime());
		}catch(Exception e)
		{
			return created;
		}
		
		return created;
	}
}
