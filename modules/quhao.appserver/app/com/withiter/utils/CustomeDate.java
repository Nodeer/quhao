package com.withiter.utils;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class CustomeDate {
	private static String SECONDS_AGO = "%d seconds ago near %s";
	private static String MINUTES_AGO = "%d minutes ago near %s";
	private static String HOURS_AGO = "%d hours ago near %s";
	private static String YESTERDAY = "Yesterday near %s";
	private static String DAYS = "%d days near %s";
	private static String MEDIUMDF = "%s near %s";

	public static String getCustomeDate(Date date, String location) {
		Calendar now = Calendar.getInstance();
		int yearNow = now.get(Calendar.YEAR);
		int monthNow = now.get(Calendar.MONTH) + 1;
		int dateNow = now.get(Calendar.DATE);
		int hourNow = now.get(Calendar.HOUR_OF_DAY);
		int minuteNow = now.get(Calendar.MINUTE);
		int secondNow = now.get(Calendar.SECOND);
		DateFormat mediumDf = DateFormat.getDateInstance(DateFormat.MEDIUM);
		Calendar clnd = Calendar.getInstance();
		clnd.setTime(date);
		int yearClnd = clnd.get(Calendar.YEAR);
		int monthClnd = clnd.get(Calendar.MONTH) + 1;
		int dateClnd = clnd.get(Calendar.DATE);
		int hourClnd = clnd.get(Calendar.HOUR_OF_DAY);
		int minuteClnd = clnd.get(Calendar.MINUTE);
		int secondClnd = clnd.get(Calendar.SECOND);
		String text;
		if (yearNow > yearClnd) {
			text = String.format(MEDIUMDF, mediumDf.format(date), location);
		} else if (yearNow < yearClnd) {
			text = String.format(MEDIUMDF, mediumDf.format(date), location);
		} else {
			if (monthNow > monthClnd) {
				text = String.format(MEDIUMDF, mediumDf.format(date), location);
			} else if (monthNow < monthClnd) {
				text = String.format(MEDIUMDF, mediumDf.format(date), location);
			} else {
				int diff = dateNow - dateClnd;
				if (diff == 0) {
					diff = hourNow - hourClnd;
					if (diff > 0) {
						text = String.format(HOURS_AGO, diff, location);
					} else if (diff == 0) {
						diff = minuteNow - minuteClnd;
						if (diff > 0) {
							text = String.format(MINUTES_AGO, diff, location);
						} else if (diff == 0) {
							diff = secondNow - secondClnd;
							if (diff >= 0) {
								text = String.format(SECONDS_AGO, diff,
										location);
							} else {
								text = String.format(MEDIUMDF,
										mediumDf.format(date), location);
							}
						} else {
							text = String.format(MEDIUMDF,
									mediumDf.format(date), location);
						}
					} else {
						text = String.format(MEDIUMDF, mediumDf.format(date),
								location);
					}
				} else if (diff == 1) {
					text = String.format(YESTERDAY, location);
				} else if (diff < 6) {
					text = String.format(DAYS, diff, location);
				} else {
					text = String.format(MEDIUMDF, mediumDf.format(date),
							location);
				}
			}
		}
		return text;
	}
}
