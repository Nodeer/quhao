package com.withiter.utils;

public class StringUtils extends org.apache.commons.lang.StringUtils {
	public static String getMaxSubString(int max, String str) {
		if (str.length() < max)
			return str;
		else {
			return str.substring(0, max) + "...";
		}
	}

	public static String getLimitedWords(int max, String str) {
		if (StringUtils.isEmpty(str)) {
			return "";
		} else {
			String[] strArray = split(str, null);
			StringBuilder builder = new StringBuilder();
			int i = 0;
			for (String s : strArray) {
				builder.append(s);
				i++;
				if (i >= max) {
					builder.append("...");
					break;
				} else {
					builder.append(" ");
				}
			}
			return builder.toString().trim();
		}
	}

}
