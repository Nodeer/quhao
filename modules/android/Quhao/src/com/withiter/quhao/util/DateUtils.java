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
	
	public static String yyyyMMddHHmmss2yyyyMMdd(String createdTime) {
		
		if(StringUtils.isNull(createdTime))
		{
			return "";
		}
		
		String created = "";
		try
		{
//			Date date = new Date(createdTime);
//			Calendar cal = Calendar.getInstance();
//			cal.setTimeInMillis(new Date(createdTime).getTime());
			DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = format1.parse(createdTime);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			created = format.format(date);
		}catch(Exception e)
		{
			e.printStackTrace();
			return created;
		}
		
		return created;
	}
}
