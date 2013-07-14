package com.withiter.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.time.DateFormatUtils;

public class DateUtils {

	private final static long minute = 60 * 1000;// 1 minute
	private final static long hour = 60 * minute;// 1 hour
	private final static long day = 24 * hour;// 1 day
	private final static long week = 7 * day;// 1 week
	private final static long month = 31 * day;// 1 month
	private final static long year = 12 * month;// 1 year

	/**
	 * return the description of time period
	 * 
	 * @author Cross
	 * @param date
	 * @return
	 */
	public static String getTimeFormatText(Date date) {
		if (date == null) {
			return null;
		}
		long diff = new Date().getTime() - date.getTime();
		long r = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
		/*
		if (diff > year) {
			r = (diff / year);
			if(r == 1){
				return r + "year ago";
			}
			return r + " years ago";
		}
		if (diff > month) {
			r = (diff / month);
			if(r == 1){
				return r + "month ago";
			}
			return r + " months ago";
		}
		*/
		if(diff > year || diff > month){
			return sdf.format(date);
		}
		
		if(diff > week){
			r = (diff / week);
			if(r == 1){
				return r + " week ago";
			}
			return sdf.format(date);
		}
		
		if (diff > day) {
			r = (diff / day);
			if (r == 1) {
				return r + " day ago";
			}
			return r + " days ago";
		}
		if (diff > hour) {
			r = (diff / hour);
			if (r == 1) {
				return r + " hour ago";
			}
			return r + " hours ago";
		}
		if (diff > minute) {
			r = (diff / minute);
			if(r == 1){
				return r + "minute ago";
			}
			return r + " minutes ago";
		}
		return " just now";
	}
}
